package com.example.wamg1.teacher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FileViewActivity extends AppCompatActivity {
    String fileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);
        Intent intent=getIntent();
        fileUrl=intent.getStringExtra("file_url");

        WebView mWebView=findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("https://view.officeapps.live.com/op/view.aspx?src="+fileUrl);
       // mWebView.loadUrl("http://docs.google.com/gview?embedded=true&url="+fileUrl);

    }
}
