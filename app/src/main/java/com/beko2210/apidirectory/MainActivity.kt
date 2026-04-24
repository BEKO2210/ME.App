package com.beko2210.apidirectory

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.beko2210.apidirectory.ui.theme.ApiDirectoryTheme
import com.beko2210.apidirectory.web.ApiDirectoryWebView
import com.beko2210.apidirectory.web.WebViewState
import com.beko2210.apidirectory.web.rememberWebViewState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var keepSplash by mutableStateOf(true)
        splash.setKeepOnScreenCondition { keepSplash }

        setContent {
            ApiDirectoryTheme {
                val state = rememberWebViewState(initialUrl = HOME_URL)
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                LaunchedEffect(state.isInitialLoadDone) {
                    if (state.isInitialLoadDone) keepSplash = false
                }

                LaunchedEffect(state.lastError) {
                    state.lastError?.let { msg ->
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { padding ->
                    AppContent(state = state, padding = padding)
                }
            }
        }
    }

    @Composable
    private fun AppContent(state: WebViewState, padding: PaddingValues) {
        BackHandler(enabled = state.canGoBack) { state.goBack() }
        ApiDirectoryWebView(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }

    companion object {
        const val HOME_URL = "https://beko2210.github.io/API_directory/"
    }
}
