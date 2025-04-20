package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Comment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.comment
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState

@Composable
fun CommentField(
    commentField: FormField<String, StringResource>,
    modifier: Modifier = Modifier
) {
    val (commentValue, commentValueSetter) = commentField.data.collectAsMutableState()
    val commentError by commentField.error.collectAsState()


    TextField(
        value = commentValue,
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Comment,
                contentDescription = stringResource(Res.string.comment)
            )
        },
        onValueChange = { commentValueSetter(it) },
        placeholder = { Text(stringResource(Res.string.comment)) },
        isError = commentError != null,
        supportingText = SupportingText(commentError),
        modifier = modifier,
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrectEnabled = true,
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = androidx.compose.ui.text.input.ImeAction.Done,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Text
        )
    )
}