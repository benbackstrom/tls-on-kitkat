package com.recursivecase.ben.tlsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.button);
        mWebView = (WebView) findViewById(R.id.webview);

        try {
            HttpsURLConnection.setDefaultSSLSocketFactory(new BetterSocketFactory(this));
            mWebView.setWebViewClient(new HttpCallBackClient());
        } catch(Throwable t) {
            t.printStackTrace();
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("https://www.google.com/");
            }
        });
    }

    private class HttpCallBackClient extends WebViewClient {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String urlString) {
            WebResourceResponse response = null;
            URL url;
            InputStream stream = null;
            HttpsURLConnection connection = null;
            try {
                url = new URL(urlString);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                String mimeType = connection.getContentType();
                response = new WebResourceResponse(mimeType, "utf-8", connection.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }
    }
}
