package skdev.omsrings.mobile.presentation.feature_auth.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Engineering
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.feature_auth.enitity.UserRole
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens


@Composable
fun RoleSelector(
    modifier: Modifier = Modifier,
    value: UserRole,
    onChangeValue: (UserRole) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        InputChip(
            selected = value == UserRole.CONTRAGENT,
            onClick = { onChangeValue(UserRole.CONTRAGENT) },
            label = { Text(stringResource(UserRole.CONTRAGENT.resource)) },
            shape = MaterialTheme.shapes.medium,
            avatar = {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = stringResource(UserRole.CONTRAGENT.resource),
                    modifier = Modifier.size(InputChipDefaults.AvatarSize)
                )
            }
        )
        Spacer(Dimens.spaceMedium)
        InputChip(
            selected = value == UserRole.EMPLOYER,
            onClick = { onChangeValue(UserRole.EMPLOYER) },
            label = { Text(stringResource(UserRole.EMPLOYER.resource)) },
            shape = MaterialTheme.shapes.medium,
            avatar = {
                Icon(
                    imageVector = Icons.Rounded.Engineering,
                    contentDescription = stringResource(UserRole.EMPLOYER.resource),
                    modifier = Modifier.size(InputChipDefaults.AvatarSize)
                )
            }
        )
    }
}