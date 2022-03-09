package com.example.fastcampus_8

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private val webView : WebView by lazy {
        findViewById(R.id.webView)
    }

    private val addressBar : EditText by lazy {
        findViewById(R.id.addressBar)
    }

    private val moveHomeButton : ImageButton by lazy {
        findViewById(R.id.moveHomeButton)
    }

    private val moveForwardButton : ImageButton by lazy {
        findViewById(R.id.moveForwardButton)
    }

    private val moveBackButton : ImageButton by lazy {
        findViewById(R.id.moveBackButton)
    }

    private val refreshLayout : SwipeRefreshLayout by lazy {
        findViewById(R.id.refreshLayout)
    }

    private val progressBar : ContentLoadingProgressBar by lazy {
        findViewById(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        bindViews()
    }

    override fun onBackPressed() { //뒤로가기 버튼을 눌렀을 때의 동작
        if(webView.canGoBack())
            webView.goBack()
        else
            super.onBackPressed() //앱이 종료

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        webView.apply{
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(HOME_URL)
        }
    }

    private fun bindViews() {
        addressBar.setOnEditorActionListener { textView, actionId, keyEvent ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                val loadingUrl= textView.text.toString()
                if(URLUtil.isNetworkUrl(loadingUrl)) {
                    webView.loadUrl(loadingUrl)
                } else {
                    webView.loadUrl("http://${loadingUrl}")
                }
            }

            return@setOnEditorActionListener false
        }

        moveBackButton.setOnClickListener {
            webView.goBack()
        }

        moveForwardButton.setOnClickListener {
            webView.goForward()
        }

        moveHomeButton.setOnClickListener {
            webView.loadUrl(HOME_URL)
        }

        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }

    //inner를 붙여줌으로써 상위의 클래스에 접근이 가능해진다
    inner class WebViewClient : android.webkit.WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            progressBar.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            refreshLayout.isRefreshing = false
            progressBar.hide()
            moveBackButton.isEnabled = webView.canGoBack()
            moveForwardButton.isEnabled = webView.canGoForward()
            addressBar.setText(url)
        }
    }

    inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress
        }
    }

    companion object{
        private const val HOME_URL = "http://www.google.com"
    }
}