package com.indexingbrowser.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;


public class BrowserActivity extends Activity {

    WebView web;
    Button BackButton, ForwardButton, GoButton, StopButton;
    EditText UrlText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        web = (WebView)findViewById(R.id.Web);
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl("http://xamarin.com/");
        web.setWebViewClient(new MonkeyWebViewClient());

        // allow zooming/panning
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setSupportZoom(true);


        // loading with the page zoomed-out, so you can see the whole thing (like the default behaviour of the real browser)
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);

        // scrollbar stuff
        web.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY); // so there's no 'white line'
        web.setScrollbarFadingEnabled(false);


        BackButton = (Button)findViewById(R.id.BackButton);
        ForwardButton = (Button)findViewById(R.id.ForwardButton);
        GoButton = (Button)findViewById(R.id.GoButton);
        StopButton = (Button)findViewById(R.id.StopButton);
        UrlText = (EditText)findViewById(R.id.UrlText);


        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                web.goBack();
            }
        });

        ForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                web.goForward();
            }
        });

        GoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                web.loadUrl(UrlText.getText().toString());
            }
        });

        StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                web.stopLoading();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class MonkeyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }

}
