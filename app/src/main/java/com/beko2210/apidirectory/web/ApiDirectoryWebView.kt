package com.beko2210.apidirectory.web

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ApiDirectoryWebView(
    state: WebViewState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
                mediaPlaybackRequiresUserGesture = false
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
                userAgentString = userAgentString + " ApiDirectoryAndroid/1.0"
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    state.progress = newProgress
                }
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    val url = request.url
                    val host = url.host ?: return false
                    val internalHost = Uri.parse(state.initialUrl).host
                    return if (internalHost != null && host.endsWith(internalHost)) {
                        false
                    } else {
                        runCatching {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, url).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                        true
                    }
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    state.isLoading = true
                    state.currentUrl = url
                    state.lastError = null
                }

                override fun onPageFinished(view: WebView, url: String) {
                    state.isLoading = false
                    state.isRefreshing = false
                    state.canGoBack = view.canGoBack()
                    state.currentUrl = url
                    state.isInitialLoadDone = true
                }

                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {
                    if (request.isForMainFrame) {
                        state.lastError = "Loading failed: ${error.description}"
                        state.isInitialLoadDone = true
                        state.isRefreshing = false
                    }
                }
            }
            loadUrl(state.initialUrl)
        }
    }

    val swipe = remember {
        SwipeRefreshLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(webView)
            setOnRefreshListener {
                state.isRefreshing = true
                webView.reload()
            }
        }
    }

    LaunchedEffect(state.isRefreshing) {
        swipe.isRefreshing = state.isRefreshing
    }

    LaunchedEffect(state.pendingActions.size) {
        if (state.pendingActions.isEmpty()) return@LaunchedEffect
        val actions = state.pendingActions.toList()
        state.pendingActions.clear()
        actions.forEach { action ->
            when (action) {
                is WebAction.Back -> if (webView.canGoBack()) webView.goBack()
                is WebAction.Reload -> webView.reload()
                is WebAction.Load -> webView.loadUrl(action.url)
            }
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { swipe },
            modifier = Modifier
        )
        if (state.isLoading && state.progress in 1..99) {
            LinearProgressIndicator(
                progress = { state.progress / 100f },
                modifier = Modifier
            )
        }
    }
}

@Suppress("unused")
private fun View.removeFromParent() {
    (parent as? ViewGroup)?.removeView(this)
}
