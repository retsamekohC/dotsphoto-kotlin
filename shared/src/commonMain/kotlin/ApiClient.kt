import io.ktor.util.*

public class ApiClient {
    public companion object {
        val rootPhotoMap = mutableMapOf(
            1L to "",
            2L to "",
            3L to "",
            4L to "",
        )
        public fun getRootAlbumPhotoIds() : List<Long> {
            return listOf(1,2,3,4)
        }

        suspend fun getPhotoById(id: Long) : String {
            return rootPhotoMap[id]?:""
        }

        suspend fun postPhotoToRootAlbum(photoBlob: ByteArray) {
            val maxId = rootPhotoMap.keys.max();
            rootPhotoMap.put(maxId+1, photoBlob.encodeBase64())
        }
    }
}
