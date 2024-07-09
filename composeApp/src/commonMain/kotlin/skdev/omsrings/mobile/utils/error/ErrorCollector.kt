package skdev.omsrings.mobile.utils.error

interface ErrorCollector {
    val observers: List<ErrorObserver>

    fun notifyError(error: ErrorMessage) {
        observers.forEach { it.onError(error) }
    }
}