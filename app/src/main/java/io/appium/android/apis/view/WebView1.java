/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appium.android.apis.view;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.appium.android.apis.R;


/**
 * Sample creating 1 webviews.
 */
public class WebView1 extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {;
        setContentView(R.layout.webview_1);

        WebView wv = (WebView) findViewById(R.id.wv1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setJavaScriptEnabled(false);

        wv.setWebViewClient(new WebViewClient());

        wv.loadUrl("file:///android_asset/html/index.html");

        super.onCreate(savedInstanceState);
    }
}
