import dto.AlbumDto
import dto.PhotoDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

data class Credentials(val username: String, val password: String) {
    @OptIn(ExperimentalEncodingApi::class)
    fun getB64(): String {
        return Base64.encode("$username:$password".toByteArray(Charsets.UTF_8));
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
                        BasicAuthCredentials(username = credentials?.username ?: "", password = credentials?.password ?: "")
                    }
                }
            }
            install(HttpCookies)
        }

    private val API_URL = "http://localhost:8080"

    suspend fun getRootAlbum(): AlbumDto {
        return httpClient.get("$API_URL/album/root").body()
    }

    suspend fun getPhotoIdsByAlbum(albumId: Long): List<Long> {
        return httpClient.get("$API_URL/album/$albumId/photos").body()
    }

    suspend fun getRootAlbumPhotoIds(): List<Long> {
        val rootAlbum = getRootAlbum()
        return getPhotoIdsByAlbum(rootAlbum.id)
    }

    suspend fun getPhotoById(id: Long): PhotoDto {
        return httpClient.get("$API_URL/photo/$id").body<PhotoDto>()
    }

    @Serializable
    data class PhotoPostRequest(val b64: String, val photoName: String)

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun postPhotoToRootAlbum(photoBlob: ByteArray, photoName: String) : Boolean {
        val b64 = Base64.encode(photoBlob)
        val status = httpClient.post("$API_URL/photo") {
            contentType(ContentType.Application.Json)
            setBody(PhotoPostRequest(b64, photoName))
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

    suspend fun logout() : Boolean  {
        val status = httpClient.post("$API_URL/auth/logout").status
        if (status.isSuccess()) {
            this.credentials = null
        }
        return status.isSuccess()
    }

    suspend fun register(username: String, password: String) : Boolean  {
        val status = httpClient.post("$API_URL/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to username, "password" to password))
        }.status

        if (status.isSuccess()) {
            this.credentials = null
        }
        return status.isSuccess()
    }
}
