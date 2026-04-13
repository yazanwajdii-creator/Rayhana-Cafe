package com.rayhana.cafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.JavascriptInterface;
import android.content.Context;
import android.widget.Toast;
import java.io.File;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full screen - no title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);          // localStorage
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setTextZoom(100);
        settings.setSupportZoom(false);
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Set database path for localStorage to work
        String dbPath = getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabasePath(dbPath);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Keep all navigation inside the app
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, 
                                     android.webkit.JsResult result) {
                // Pass JS alerts through normally
                return super.onJsAlert(view, url, message, result);
            }
        });

        // Add native bridge for toasts
        webView.addJavascriptInterface(new NativeBridge(), "NativeApp");

        // Load the app
        webView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    class NativeBridge {
        @JavascriptInterface
        public void showToast(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public String getVersion() {
            return "1.1.0";
        }
    }
}
