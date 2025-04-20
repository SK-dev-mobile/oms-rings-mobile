package skdev.omsrings.mobile.presentation.feature_faq

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.faq_cancelled_order
import omsringsmobile.composeapp.generated.resources.faq_cancelled_order_description
import omsringsmobile.composeapp.generated.resources.faq_collapse
import omsringsmobile.composeapp.generated.resources.faq_completed_order
import omsringsmobile.composeapp.generated.resources.faq_completed_order_description
import omsringsmobile.composeapp.generated.resources.faq_expand
import omsringsmobile.composeapp.generated.resources.faq_header
import omsringsmobile.composeapp.generated.resources.faq_new_order
import omsringsmobile.composeapp.generated.resources.faq_new_order_description
import omsringsmobile.composeapp.generated.resources.faq_rescheduled_order
import omsringsmobile.composeapp.generated.resources.faq_rescheduled_order_description
import org.jetbrains.compose.resources.stringResource
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.presentation.feature_order_form.OrderFormContract
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.CustomTheme
import skdev.omsrings.mobile.ui.theme.values.Dimens
import skdev.omsrings.mobile.utils.flow.observeAsEffects

data class FAQItem(
    val title: String,
    val description: String,
    val color: Color,
    val icon: ImageVector
)

object FAQScreen : BaseScreen("faq_screen") {
    @Composable
    override fun MainContent() {
        FAQScreenContent()
    }
}

@Composable
private fun FAQScreenContent() {
    val navigator = LocalNavigator.currentOrThrow

    val faqItems = listOf(
        FAQItem(
            title = stringResource(Res.string.faq_new_order),
            description = stringResource(Res.string.faq_new_order_description),
            color = CustomTheme.colors.success,
            icon = Icons.Rounded.LocalShipping
        ),
        FAQItem(
            title = stringResource(Res.string.faq_completed_order),
            description = stringResource(Res.string.faq_completed_order_description),
            color = CustomTheme.colors.warning,
            icon = Icons.Rounded.LocalShipping
        ),
        FAQItem(
            title = stringResource(Res.string.faq_cancelled_order),
            description = stringResource(Res.string.faq_cancelled_order_description),
            color = CustomTheme.colors.error,
            icon = Icons.Rounded.LocalShipping
        ),
        FAQItem(
            title = stringResource(Res.string.faq_rescheduled_order),
            description = stringResource(Res.string.faq_rescheduled_order_description),
            color = MaterialTheme.colorScheme.surface,
            icon = Icons.Rounded.Info
        )
    )

    Scaffold(
        topBar = {
            RingsTopAppBar(
                title = stringResource(Res.string.faq_header),
                onNavigationClicked = {
                    navigator.pop()
                }
            )
        }
    ) { innerPadding ->
        FAQList(
            faqItems = faqItems,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun FAQList(
    faqItems: List<FAQItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.spaceMedium)
    ) {
        items(items = faqItems) { item ->
            ExpandableFAQCard(item)
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
        }
    }
}

@Composable
private fun ExpandableFAQCard(faqItem: FAQItem) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = MaterialTheme.shapes.large,
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spaceMedium)
        ) {
            FAQCardHeader(faqItem, expanded)
            if (expanded) {
                Spacer(Dimens.spaceSmall)
                Text(
                    text = faqItem.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun FAQCardHeader(faqItem: FAQItem, expanded: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = faqItem.icon,
            contentDescription = null,
            tint = faqItem.color,
        )
        Spacer(Dimens.spaceSmall)
        Text(
            text = faqItem.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = faqItem.color,
            modifier = Modifier.weight(1f)
        )
        Spacer(Dimens.spaceSmall)
        Icon(
            imageVector = if (expanded) Icons.Rounded.ArrowDropUp else Icons.Filled.ArrowDropDown,
            contentDescription = if (expanded) stringResource(Res.string.faq_expand) else stringResource(
                Res.string.faq_collapse
            )
        )
    }
}


