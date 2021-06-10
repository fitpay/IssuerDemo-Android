package com.fitpay.issuerdemo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PushProvisioningWebFragment extends Fragment {

    private static final String TAG = PushProvisioningWebFragment.class.getCanonicalName();
    private static final String BASE_URL = "https://tokenconnect.mcsrcteststore.com/dashboard";
    private static final String DEEP_LINK_BASE_URL = "https://connect.garmin.com/payment/push/android/mc";
    private static final String TRID = "50183038325";
    private static final String ACCESS_CODE = "2610099d";
    private WebView webViewContainer;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_push_provisioning_web, container, false);
        ((MainActivity)getActivity()).setupActionBarTitle("Push Provisioning MC");
        initViews(view);
        configureWebView();
        loadUrl();
        return view;
    }

    private void initViews(View view) {
        webViewContainer = view.findViewById(R.id.containedWebView);
    }

    @SuppressLint("JavascriptInterface")
    private void configureWebView() {
        webViewContainer.setBackgroundColor(Color.WHITE);
        webViewContainer.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        webViewContainer.clearCache(true);
        webViewContainer.setScrollbarFadingEnabled(true);
        webViewContainer.setVerticalScrollBarEnabled(true);

        webViewContainer.getSettings().setJavaScriptEnabled(true);
        webViewContainer.getSettings().setAppCacheEnabled(false);
        webViewContainer.getSettings().setDomStorageEnabled(true);
    }

    private void loadUrl() {
        progressDialog = ProgressDialog.show(getActivity(), "Loading","Please wait...", true);
        progressDialog.setCancelable(false);

        webViewContainer.setWebChromeClient(new WebChromeClient());
        webViewContainer.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, final String url) {
                Log.d(TAG, "onPageFinished: " + url);

                webViewContainer.loadUrl("javascript:(function() { document.getElementById('tridInput').value = '" + TRID + "'; ;})()");
                webViewContainer.loadUrl("javascript:(function() { document.getElementById('accessCodeInput').value = '" + ACCESS_CODE + "'; ;})()");

                progressDialog.dismiss();
                if (url.contains(DEEP_LINK_BASE_URL)) {
                    sendIntent(url);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.d(TAG, "onReceivedError: " + error.toString());
            }
        });

        webViewContainer.loadUrl(BASE_URL);
    }

    private void sendIntent(String deepLink) {
        Log.d(TAG, "Deep Link sent: " + deepLink);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(deepLink));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}