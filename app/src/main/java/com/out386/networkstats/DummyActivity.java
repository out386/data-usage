package com.out386.networkstats;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Arrays;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

public class DummyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    5000);
        }
        manageWebView();
    }


    private void manageWebView() {
        WebView webView = findViewById(R.id.web_view);
        CookieManager.getInstance().setAcceptCookie(true);
        //CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebClient());
        webView.loadUrl("https://www.jio.com/Jio/portal/myAccount");//https://www.jio.com/JioWebApp/index.html?root=login");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 5000) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                askUsagePermission();
                finish();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        5000);
            }
        }
    }

    private void askUsagePermission() {
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();

        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo info;
        try {
            info = pm.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();    // Waaaaaaooow
            return;
        }

        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, info.uid, context.getPackageName());
        if (mode != MODE_ALLOWED) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .putBoolean("NOT_FIRST_START", true)
                .apply();
    }

    private class WebClient extends WebViewClient {
        private static final int BACKOFF = 250;

        private int iter = 0;
        private Handler handler = new Handler();

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideTopBar(view);

            if (url.startsWith("https://www.jio.com/Jio/portal/myAccount")) {
                iter++;
                if (iter >= 2) {    // It redirects to the same page the first time
                    handler.postDelayed(new DataRunnable(view), BACKOFF);
                }
            }
        }

        private void hideTopBar(WebView view) {
            handler.postDelayed(() ->
                    view.evaluateJavascript(
                            "document.querySelector(\".mobileHeaderContainer\").style.display='none'",
                            null), 2000);
        }

        private class DataRunnable implements Runnable {
            private WebView view;

            DataRunnable(WebView webView) {
                view = webView;
            }

            @Override
            public void run() {
                view.evaluateJavascript(Utils.getWebViewJs(),
                        string -> {
                            if (string == null) {
                                handler.postDelayed(this, BACKOFF);
                                Log.i("blah", "queue1");
                                return;
                            }
                            // Removing quotes that comes from IDK where
                            string = string.substring(1, string.length() - 1);
                            String [] data = string.split(":");

                            if (data.length != 3) {
                                handler.postDelayed(this, BACKOFF);
                                Log.i("blah", "queue2");
                                return;
                            }
                            Log.i("blah:", "onPageFinished: " + Arrays.toString(string.split(":")));
                            CookieManager.getInstance().flush();
                        });
            }
        }
    }
}


