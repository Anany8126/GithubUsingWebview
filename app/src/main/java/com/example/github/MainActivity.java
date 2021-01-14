package com.example.github;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar progressBarWeb;
    ProgressDialog progressDialog;
    RelativeLayout relativeLayout;
    Button bttnRetry;
    SwipeRefreshLayout refreshLayout;
    ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListner;

    private String weburl = "https://github.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // niche vali 2 line se window full screen pr khulegi, status bar nahi dikhega
        // Window class h
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.myWebView);
        progressBarWeb = (ProgressBar) findViewById(R.id.progress_bar);
        bttnRetry = (Button) findViewById(R.id.Btn_No_connection);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        // niche vala method swipe refresh k liye use hua h
        refreshLayout.setColorSchemeColors(Color.RED,Color.BLUE,Color.GREEN);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
        // niche vala code is liye h agar web page lamba h or niche krte hi swipe na ho 
        refreshLayout.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListner = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if(webView.getScrollY() == 0)
                    refreshLayout.setEnabled(true);
                else
                    refreshLayout.setEnabled(false);
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Please Wait");

        //nichey vali line rikhne se ads, vedio bhi play ho jayengi agar hum url mai niche vali line rikh de
        //private String weburl = "https://google.com/";
        webView.getSettings().setJavaScriptEnabled(true);


        checkConnection();
        //niche vala code website app mai hi open hoyegi bajaye dusri website ko open krre

        webView.loadUrl(weburl);
        webView.setWebViewClient(new WebViewClient(){
            // onPageFinished method use for swipe refresh k liye
            @Override
            public void onPageFinished(WebView view, String url) {
                refreshLayout.setRefreshing(false);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBarWeb.setVisibility(View.VISIBLE);
                progressBarWeb.setProgress(newProgress);
                // action bar p rikha aayega jab bhi app open karengey
                setTitle("Loading...");
                progressDialog.show();
                if(newProgress == 100){
                    progressBarWeb.setVisibility(View.GONE);
                    setTitle(view.getTitle());
                    progressDialog.dismiss();
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        bttnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
            }
        });


    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_exit_to_app_black_24dp);
            builder.setTitle("Exit ?");
            builder.setMessage("Are you sure you want to exit GITHUB ?").setNegativeButton("No",null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            }).show();
        }

    }

    public void checkConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(wifi.isConnected()){
            webView.loadUrl(weburl);
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);

        }
        else if (mobileNetwork.isConnected()){
            webView.loadUrl(weburl);
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);

        }
        else {
            webView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }
 //toolbar p icon ki saari working niche h dono method m
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_previous:
                onBackPressed();
                break;
            case R.id.nav_next:
                if(webView.canGoForward()){
                    webView.goForward();
                }
                break;
            case R.id.nav_reload:
                checkConnection();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
