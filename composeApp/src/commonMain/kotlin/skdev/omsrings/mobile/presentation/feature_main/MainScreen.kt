package skdev.omsrings.mobile.presentation.feature_main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import skdev.omsrings.mobile.presentation.base.BaseScreen

object MainScreen : BaseScreen("main_screen") {

    @Composable
    override fun MainContent() {
        val screenModel = koinScreenModel<MainScreenModel>()

        Scaffold { paddingValues ->
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { screenModel.laucnhToast() }
                ) {
                    Text(text = "Показать сообщение")
                }
            }
        }
    }
}
