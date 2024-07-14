package skdev.omsrings.mobile.presentation.feature_main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import skdev.omsrings.mobile.presentation.base.BaseScreen

object MainScreen : BaseScreen("main_screen") {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun MainContent() {
        koinScreenModel<MainScreenModel>()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "ЖБИ Кольца")
                    },
                    actions = {
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Logout"
                            )
                        }
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Logout"
                            )
                        }
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Logout"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                )
            }
        ) { paddingValues ->
            MainScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
    }

    @Composable
    private fun MainScreenContent(
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
        ) {
            Button(
                onClick = {}
            ) {
                Text("sadds")
            }
        }
    }
}
