package skdev.omsrings.mobile.utils.notification

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
    private const val URL = "http://southstartrade.com/push"
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun sendPush(title: String, content: String) {
        client.post(URL) {
            contentType(ContentType.Application.Json)
            setBody(PushBody(title, content))
        }
    }

}