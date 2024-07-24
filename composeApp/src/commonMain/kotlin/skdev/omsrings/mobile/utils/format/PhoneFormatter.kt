package skdev.omsrings.mobile.utils.format

import skdev.omsrings.mobile.utils.Constants

fun formatPhoneNumber(input: String, mask: String = Constants.PhoneMask, maskNumber: Char = Constants.PhoneMaskChar): String {
    val digitsOnly = input.filter { it.isDigit() }
    val maxLength = mask.count { it == maskNumber }
    val trimmedDigits = if (digitsOnly.length > maxLength) digitsOnly.take(maxLength) else digitsOnly

    val formattedNumber = StringBuilder()
    var digitIndex = 0

    for (maskChar in mask) {
        if (digitIndex < trimmedDigits.length && maskChar == maskNumber) {
            formattedNumber.append(trimmedDigits[digitIndex])
            digitIndex++
        } else if (maskChar != maskNumber) {
            formattedNumber.append(maskChar)
        } else {
            break
        }
    }

    return formattedNumber.toString()
}
