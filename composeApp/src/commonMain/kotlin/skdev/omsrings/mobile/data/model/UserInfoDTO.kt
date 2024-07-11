package skdev.omsrings.mobile.data.model

data class UserInfoDTO(
    val email: String,
    val password: String,
    val name: String,
    val phone: String,
    val role: UserRole
)

enum class UserRole {
    CONTRAGENT,
    EMPLOYER
}
