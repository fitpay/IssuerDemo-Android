package com.fitpay.issuerdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.fragment.app.Fragment

class PushProvisioningWebFragment : Fragment() {
    private var webViewContainer: WebView? = null
    private var progressDialog: ProgressDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_push_provisioning_web, container, false)
        (activity as PushProvisioningActivity?)!!.setupActionBarTitle("Push Provisioning MC")
        initViews(view)
        configureWebView()
        loadUrl()
        return view
    }

    private fun initViews(view: View) {
        webViewContainer = view.findViewById(R.id.containedWebView)
    }

    @SuppressLint("JavascriptInterface")
    private fun configureWebView() {
        webViewContainer!!.setBackgroundColor(Color.WHITE)
        webViewContainer!!.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
        webViewContainer!!.clearCache(true)
        webViewContainer!!.isScrollbarFadingEnabled = true
        webViewContainer!!.isVerticalScrollBarEnabled = true
        webViewContainer!!.settings.javaScriptEnabled = true
        webViewContainer!!.settings.setAppCacheEnabled(false)
        webViewContainer!!.settings.domStorageEnabled = true
    }

    private fun loadUrl() {
        progressDialog = ProgressDialog.show(activity, "Loading", "Please wait...", true)
        progressDialog?.setCancelable(false)
        webViewContainer!!.webChromeClient = WebChromeClient()
        webViewContainer!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading")
                val url = request.url.toString()
                if (url.contains(DEEP_LINK_BASE_URL)) {
                    sendIntent(url)
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView, url: String) {
                Log.d(TAG, "onPageFinished: $url")
                webViewContainer!!.loadUrl("javascript:(function() { document.getElementById('tridInput').value = '" + TRID + "'; ;})()")
                webViewContainer!!.loadUrl("javascript:(function() { document.getElementById('accessCodeInput').value = '" + ACCESS_CODE + "'; ;})()")
                progressDialog?.dismiss()
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                Log.d(TAG, "onReceivedError: $error")
            }
        }
        webViewContainer!!.loadUrl(BASE_URL)
    }

    private fun sendIntent(deepLink: String) {
        Log.d(TAG, "Deep Link sent: $deepLink")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(deepLink)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    companion object {
        private val TAG = PushProvisioningWebFragment::class.java.canonicalName
        private const val BASE_URL = "https://tokenconnect.mcsrcteststore.com/dashboard"
        private const val DEEP_LINK_BASE_URL = "https://connect.garmin.com/payment/push/android/mc"
        private const val TRID = "50183038325"
        private const val ACCESS_CODE = "2610099d"
    }
}