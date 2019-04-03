package com.luoheng.miu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DaMaiVerifyActivity extends AppCompatActivity {
    @BindView(R.id.webView)
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(DaMaiSpider.URL_SEARCH_AJAX);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        CookieManager manager=CookieManager.getInstance();
        String cookies=manager.getCookie(DaMaiSpider.URL_SEARCH_AJAX);
        intent.putExtra("cookies",cookies);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }
}
