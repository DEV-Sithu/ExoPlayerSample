package mm.exoplayersample.com

import androidx.media3.common.MimeTypes
import androidx.core.net.toUri
import okhttp3.OkHttpClient
import okhttp3.Request

class ExoMediaTypeChecker {

    enum class MediaType {
        HLS,
        DASH,
        SMOOTH_STREAMING,
        PROGRESSIVE,
        RTSP,
        UNKNOWN
    }

    fun detectMediaType(urlString: String): MediaType {
        val AUDIO_M4A = "audio/mp4"
        val uri = urlString.toUri()
        val scheme = uri.scheme?.lowercase()
        val path = uri.path?.lowercase() ?: ""
        val query = uri.query ?: ""

        // 1. Scheme-based detection
        when (scheme) {
            "rtsp" -> return MediaType.RTSP
            "rtmp" -> return MediaType.RTSP // ExoPlayer doesn't support RTMP by default
        }

        // 2. Extension detection
        val extension = uri.lastPathSegment?.substringAfterLast('.', "")?.lowercase()
        when (extension) {
            "m3u8" -> return MediaType.HLS
            "mpd" -> return MediaType.DASH
            "ism" -> return MediaType.SMOOTH_STREAMING
            "mp3", "mp4", "mkv", "webm", "mov", "avi",
            "flv", "wmv", "3gp", "m4v", "ts", "aac", "wav", "ogg", "m4a" ,"ogv"-> return MediaType.PROGRESSIVE
        }

        // 3. Path pattern detection
        when {
            path.contains("m3u8") -> return MediaType.HLS
            path.contains("mpd") -> return MediaType.DASH
            path.contains("manifest") || path.contains("ism") -> return MediaType.SMOOTH_STREAMING
            path.endsWith(".mp3") -> return MediaType.PROGRESSIVE
        }

        // 4. Query parameter-based detection
        uri.getQueryParameter("format")?.lowercase()?.let { format ->
            return when (format) {
                "hls" -> MediaType.HLS
                "dash" -> MediaType.DASH
                "smooth" -> MediaType.SMOOTH_STREAMING
                "mp3", "mp4" -> MediaType.PROGRESSIVE
                else -> MediaType.UNKNOWN
            }
        }

        // 5. Try MIME type detection (optional but more accurate)
        detectMimeTypeFromHead(urlString)?.let { mime ->
            return when (mime) {
                MimeTypes.APPLICATION_M3U8 -> MediaType.HLS
                MimeTypes.APPLICATION_MPD -> MediaType.DASH
                MimeTypes.APPLICATION_SS -> MediaType.SMOOTH_STREAMING
                MimeTypes.AUDIO_MPEG,
                MimeTypes.AUDIO_AAC,
                MimeTypes.VIDEO_MP4,
                MimeTypes.VIDEO_WEBM,
                MimeTypes.AUDIO_OGG,
                MimeTypes.AUDIO_OPUS,
                MimeTypes.VIDEO_OGG,
                MimeTypes.AUDIO_WAV,
                AUDIO_M4A -> MediaType.PROGRESSIVE
                else -> MediaType.UNKNOWN
            }
        }

        return MediaType.UNKNOWN
    }

    fun detectMimeTypeFromHead(url: String): String? {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).head().build()
            val response = client.newCall(request).execute()
            response.header("Content-Type")?.substringBefore(";")?.lowercase()
        } catch (e: Exception) {
            null
        }
    }

}