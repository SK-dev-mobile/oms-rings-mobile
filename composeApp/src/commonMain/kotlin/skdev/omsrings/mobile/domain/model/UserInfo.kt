package skdev.omsrings.mobile.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class UserInfo(
    val fullName: String = "",
    val phoneNumber: String = "",
    val isEmployer: Boolean
) {
    companion object {
        val DEFAULT = UserInfo(
            fullName = "",
            phoneNumber = "",
            isEmployer = false
        )
    }
}
