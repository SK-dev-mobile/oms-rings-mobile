package skdev.omsrings.mobile.utils.logging

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier


fun debugBuild() {
    Napier.base(DebugAntilog())
}
