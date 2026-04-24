package com.beko2210.apidirectory.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

class WebViewState internal constructor(initialUrl: String) {
    var initialUrl: String = initialUrl
        private set

    var isLoading: Boolean by mutableStateOf(false)
        internal set

    var isRefreshing: Boolean by mutableStateOf(false)
        internal set

    var progress: Int by mutableStateOf(0)
        internal set

    var canGoBack: Boolean by mutableStateOf(false)
        internal set

    var currentUrl: String by mutableStateOf(initialUrl)
        internal set

    var isInitialLoadDone: Boolean by mutableStateOf(false)
        internal set

    var lastError: String? by mutableStateOf(null)
        internal set

    internal val pendingActions: SnapshotStateList<WebAction> = mutableListOf<WebAction>().toMutableStateList()

    fun goBack() {
        pendingActions.add(WebAction.Back)
    }

    fun reload() {
        isRefreshing = true
        pendingActions.add(WebAction.Reload)
    }

    fun loadUrl(url: String) {
        pendingActions.add(WebAction.Load(url))
    }
}

internal sealed interface WebAction {
    data object Back : WebAction
    data object Reload : WebAction
    data class Load(val url: String) : WebAction
}

@Composable
fun rememberWebViewState(initialUrl: String): WebViewState =
    remember(initialUrl) { WebViewState(initialUrl) }
