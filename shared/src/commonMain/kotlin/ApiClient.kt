import dto.AlbumApiDto
import dto.PhotoApiDto
import dto.UserApiDto
import dto.request.bodies.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

data class Credentials(val username: String, val password: String) {
    @OptIn(ExperimentalEncodingApi::class)
    fun getB64(): String {
        return Base64.encode("$username:$password".toByteArray(Charsets.UTF_8))
    }
}

class ApiClient<T : HttpClientEngineConfig>(httpClientEngineFactory: HttpClientEngineFactory<T>) {
    private var credentials: Credentials? = null

    fun setCredentials(creds: Credentials) {
        if (credentials == null) {
            credentials = creds
        } else {
            throw RuntimeException("credentials already initialized")
        }
    }

    private val httpClient =
        HttpClient(httpClientEngineFactory) {
            engine {
                request {
                    timeout {
                        requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    useAlternativeNames = false
                })
            }
            install(Auth) {
                basic {
                    credentials {
                        BasicAuthCredentials(
                            username = credentials?.username ?: "",
                            password = credentials?.password ?: ""
                        )
                    }
                }
            }
            install(HttpCookies)
            install(HttpCache)
        }

    private val API_URL = "http://158.160.103.192:8080"

    suspend fun getRootAlbum(): AlbumApiDto {
        return httpClient.get("$API_URL/album/root").body()
    }

    suspend fun getPhotoIdsByAlbum(albumId: Long): List<Long> {
        return httpClient.get("$API_URL/album/$albumId/photos").body()
    }

    suspend fun getRootAlbumPhotoIds(): List<Long> {
        val rootAlbum = getRootAlbum()
        return getPhotoIdsByAlbum(rootAlbum.id)
    }

    suspend fun getPhotoById(id: Long, compressed: Boolean): PhotoApiDto {
        return httpClient.get("$API_URL/photo/$id") {
            url {
                parameters["compressed"] = compressed.toString()
            }
        }.body<PhotoApiDto>()
    }

    suspend fun postPhotoToRootAlbum(photoBlob: ByteArray, photoName: String): Boolean {
        return postPhotoToAlbum(photoBlob, photoName, getRootAlbum().id)
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun postPhotoToAlbum(photoBlob: ByteArray, photoName: String, albumId: Long): Boolean {
        val b64 = Base64.encode(photoBlob)
        val status = httpClient.post("$API_URL/photo") {
            contentType(ContentType.Application.Json)
            setBody(PhotoPostRequest(b64, photoName, albumId))
        }.status
        return status.isSuccess()
    }

    suspend fun login(username: String, password: String): Boolean {
        val creds = Credentials(username, password)
        val response = httpClient.post("$API_URL/auth/login") {
            headers {
                header("Authorization", "Basic ${creds.getB64()}")
            }
        }
        if (response.status.isSuccess()) {

            this.credentials = creds
        } else {
            this.credentials = null
        }
        return response.status.isSuccess()
    }

    suspend fun logout(): Boolean {
        val status = httpClient.post("$API_URL/auth/logout").status
        if (status.isSuccess()) {
            this.credentials = null
        }
        return status.isSuccess()
    }

    suspend fun register(username: String, password: String): Boolean {
        val status = httpClient.post("$API_URL/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(username, password))
        }.status

        if (status.isSuccess()) {
            this.credentials = null
        }
        return status.isSuccess()
    }


    suspend fun getMe(): UserApiDto {
        return httpClient.get("$API_URL/user/me").body()
    }

    suspend fun getUsers(): List<UserApiDto> {
        return httpClient.get("$API_URL/user").body()
    }

    /**
     * Получает все альбомы, в которых пользователь - владелец
     */
    suspend fun getMyAlbums(): List<AlbumApiDto> {
        return httpClient.get("$API_URL/album/my").body()
    }

    suspend fun getAccessorToAlbum(albumId: Long): AlbumAccessibleByUsers {
        return httpClient.get("$API_URL/ownership/accessorsToMyAlbum") {
            url {
                parameters["albumId"] = albumId.toString()
            }
        }.body()
    }

    suspend fun shareAlbum(albumId: Long, userId: Long): Boolean {
        val responseStatus = httpClient.post("$API_URL/album/share") {
            contentType(ContentType.Application.Json)
            setBody(ShareRequest(userId, albumId))
        }.status

        return responseStatus.isSuccess()
    }

    suspend fun getAccessibleAlbums(): List<AlbumApiDto> {
        return httpClient.get("$API_URL/album").body()
    }

    suspend fun getAlbumById(albumId: Long): AlbumApiDto {
        return httpClient.get("$API_URL/album/get/$albumId").body()
    }

    suspend fun createAlbum(albumName: String): AlbumApiDto {
        return httpClient.post("$API_URL/album/new") {
            contentType(ContentType.Application.Json)
            setBody(NewAlbumRequest(albumName))
        }.body()
    }

    suspend fun removeAlbum(albumId: Long): Boolean {
        val responseStatus = httpClient.post("$API_URL/album/remove") {
            contentType(ContentType.Application.Json)
            setBody(RemoveAlbumRequest(albumId))
        }.status

        return responseStatus.isSuccess()
    }


    /**
     * Удаляет фото из альбома.
     */
    suspend fun removePhotoFromAlbum(photoId: Long, albumId: Long): Boolean {
        val responseStatus = httpClient.post("$API_URL/photo/removeFromAlbum") {
            contentType(ContentType.Application.Json)
            setBody(RemovePhotoRequest(photoId, albumId))
        }.status

        return responseStatus.isSuccess()
    }

    /**
     * Перемещает фото в указанный альбом. Аргументом указывается айди фото и целевой альбом. Удаляет фото из изначального альбома.
     */
    suspend fun movePhotoToAlbum(photoId: Long, albumId: Long): Boolean {
        val responseStatus = httpClient.post("$API_URL/photo/moveToAlbum") {
            contentType(ContentType.Application.Json)
            setBody(MovePhotoRequest(photoId, albumId))
        }.status

        return responseStatus.isSuccess()
    }

    /**
     * Копирование фото в альбом. Аргумнетом указывается айди фото и целевой альбом. Не удаляет фото из изначального альбома.
     */
    suspend fun copyPhotoToAlbum(photoId: Long, albumId: Long): Boolean {
        val responseStatus = httpClient.post("$API_URL/photo/copyToAlbum") {
            contentType(ContentType.Application.Json)
            setBody(CopyPhotoRequest(photoId, albumId))
        }.status

        return responseStatus.isSuccess()
    }
}
