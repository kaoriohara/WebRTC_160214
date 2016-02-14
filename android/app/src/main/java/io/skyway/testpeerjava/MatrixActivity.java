package io.skyway.testpeerjava;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MatrixActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);


        // Webビューの作成
        WebView webView = (WebView) findViewById(R.id.webview);
//        webView.setVerticalScrollbarOverlay(true);
//        webView.setWebViewClient(new WebViewClient());
//        WebSettings settings = webView.getSettings();
//        settings.setSupportMultipleWindows(true);
//        settings.setLoadsImagesAutomatically(true);
//        settings.setBuiltInZoomControls(true);
//        settings.setSupportZoom(true);
//        settings.setLightTouchEnabled(true);
//        webView.loadUrl("https://20151121ubuntu.cloudapp.net:8448/");
        webView.loadUrl("http://matrix.org/beta/");
    }
}
