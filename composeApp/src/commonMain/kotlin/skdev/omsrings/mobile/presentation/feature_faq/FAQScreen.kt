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
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import omsringsmobile.composeapp.generated.resources.Res
import omsringsmobile.composeapp.generated.resources.theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import skdev.omsrings.mobile.presentation.base.BaseScreen
import skdev.omsrings.mobile.ui.components.helpers.RingsTopAppBar
import skdev.omsrings.mobile.ui.components.helpers.Spacer
import skdev.omsrings.mobile.ui.theme.values.Dimens


object FAQScreen : BaseScreen("faq_screen") {
    @Composable
    override fun MainContent() {
        FAQScreenContent()
    }

    @Composable
    private fun FAQScreenContent() {

        Scaffold(
            topBar = {
                RingsTopAppBar(
                    title = stringResource(Res.string.theme),
                    onNavigationClicked = { /* TODO: impl back navigation */ }
                )
            }
        ) { innerPadding ->
            FAQList(
                faqItems = faqItemsData,
                modifier = Modifier.padding(innerPadding)
            )
        }
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
            .padding(16.dp)
    ) {
        items(faqItems) { item ->
            ExpandableFAQCard(item)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        shape = MaterialTheme.shapes.large,
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
            imageVector = Icons.Rounded.LocalShipping,
            contentDescription = null,
            tint = faqItem.color,
        )
        Spacer(Dimens.spaceSmall)
        Text(
            text = faqItem.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = faqItem.color
        )
        Spacer(Dimens.spaceSmall)
        Icon(
            imageVector = if (expanded) Icons.Rounded.ArrowDropUp else Icons.Filled.ArrowDropDown,
            contentDescription = if (expanded) "Collapse" else "Expand"
        )
    }
}

data class FAQItem(
    val title: String,
    val description: String,
    val color: Color
)

private val faqItemsData = listOf(
    FAQItem(
        title = "НОВЫЙ ЗАКАЗ",
        description = "• Можно редактировать\n• Учитывается в расчете итогов дня\n• Статус по умолчанию для всех новых заказов",
        color = Color(0xFF2196F3) // Blue
    ),
    FAQItem(
        title = "ВЫПОЛНЕННЫЙ ЗАКАЗ",
        description = "• Можно редактировать\n• Не учитывается в расчете итогов дня\n• Используйте для отметки завершенных заказов",
        color = Color(0xFF4CAF50) // Green
    ),
    FAQItem(
        title = "СПИСАННЫЙ ЗАКАЗ",
        description = "• Нельзя редактировать\n• Не учитывается в расчете итогов дня\n• Можно скрыть через настройку \"Показывать списанные заказы\"\n• Используйте для отмененных или ошибочных заказов",
        color = Color(0xFFF44336) // Red
    ),
    FAQItem(
        title = "ПЕРЕНОС НА ДРУГУЮ ДАТУ",
        description = "• Выберите заказ и удерживайте для вызова меню\n• Доступно для статусов \"НОВЫЙ\" и \"ВЫПОЛНЕННЫЙ\"\n• При переносе исходный заказ получает статус \"СПИСАННЫЙ\"\n• В описание добавляется пометка о переносе и новая дата",
        color = Color(0xFF9C27B0) // Purple
    )
)
