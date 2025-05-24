package skdev.omsrings.mobile.utils.format

object OrderIdFormatter {
    fun getFirstPart(id: String): String {
        return id.substringBefore("-")
    }
}
