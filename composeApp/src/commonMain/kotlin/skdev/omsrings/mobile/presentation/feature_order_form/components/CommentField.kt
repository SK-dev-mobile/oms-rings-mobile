package skdev.omsrings.mobile.presentation.feature_order_form.components

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.StringResource
import skdev.omsrings.mobile.ui.components.fields.SupportingText
import skdev.omsrings.mobile.ui.components.fields.TextField
import skdev.omsrings.mobile.utils.fields.FormField
import skdev.omsrings.mobile.utils.fields.collectAsMutableState

@Composable
fun CommentField(
    commentField: FormField<String, StringResource>,
    onCommentChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (commentValue, commentValueSetter) = commentField.data.collectAsMutableState()
    val commentError by commentField.error.collectAsState()


    TextField(
        value = commentValue,
        onValueChange = {
            commentValueSetter(it)
            onCommentChanged(it)
        },
        label = { Text("Комментарий к заказу") },
        isError = commentError != null,
        supportingText = SupportingText(commentError),
        modifier = modifier
//        enabled = !state.isLoading
    )
}