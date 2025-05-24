package skdev.omsrings.mobile.utils.notification

import OMSRingsMobile.composeApp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable

@Serializable
data class PushBody(
    val title: String,
    val content: String
)

object PushManager {
    private val URL: String = BuildConfig.FIREBASE_MESSAGING_SERVER_URL
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun sendPush(title: String, content: String) {
        URL.ifBlank { return } // Ensure URL is not empty
        if (title.isBlank() || content.isBlank()) return // Ensure title and content are not empty
        client.post("$URL/push") {
            contentType(ContentType.Application.Json)
            setBody(PushBody(title, content))
        }
    }
}
