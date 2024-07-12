package skdev.omsrings.mobile.utils

object Constants {
    const val PhoneMask = "+* (***) ***-**-**"
    const val PhoneMaskChar = '*'
    const val PasswordLenght = 6

    val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")
    val PHONE_REGEX = Regex("^7\\d{10}\$")
}
