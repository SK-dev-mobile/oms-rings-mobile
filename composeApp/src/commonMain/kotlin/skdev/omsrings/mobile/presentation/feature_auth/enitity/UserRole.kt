package skdev.omsrings.mobile.presentation.feature_auth.enitity

import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.contragent
import omsringsmobile.composeapp.generated.resources.employer
import org.jetbrains.compose.resources.StringResource

enum class UserRole(val resource: StringResource) {
    CONTRAGENT(Res.string.contragent),
    EMPLOYER(Res.string.employer)
}
