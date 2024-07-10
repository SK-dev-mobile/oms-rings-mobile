package skdev.omsrings.mobile.ui.components.helpers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.back
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.ui.theme.values.Dimens


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit,
    backTitle: String? = null,
    backIcon: ImageVector,
    navigationButton: @Composable () -> Unit = {
        TextButton(
            onClick = onBackClicked,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = backIcon,
                    contentDescription = "back button"
                )

                Text(
                    text = backTitle ?: stringResource(Res.string.back),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    },

    actions: @Composable RowScope.() -> Unit = {},
    title: String? = null,
    content: @Composable () -> Unit = {}
) {

    Column(
        modifier = modifier
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = {
                navigationButton.invoke()
            },
            title = {
                title?.let {
                    Text(text = title)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            actions = actions
        )

        content.invoke()
    }
}