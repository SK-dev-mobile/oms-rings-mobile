package skdev.omsrings.mobile.utils.uuid

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()