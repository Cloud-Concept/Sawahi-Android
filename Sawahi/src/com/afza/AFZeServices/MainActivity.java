package com.afza.AFZeServices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

	private WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		webView = (WebView) findViewById(R.id.webView);

		CustomWebViewClient webViewClient = new CustomWebViewClient();
		webView.setWebViewClient(webViewClient);

		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE))
			{ WebView.setWebContentsDebuggingEnabled(true); }
		}


		//Sandbox
//		webView.loadUrl("https://mobileapp-afzamobileapp.cs8.force.com/mobileapp/apex/MobileAppHomePage");
		
		//Production
		webView.loadUrl("https://afz.force.com/mobileapp/apex/MobileAppHomePage");
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack() == true
				&& !webView.getUrl().contains("MobileAppHomePage")
				&& !webView.getUrl().contains("MobileAppThankYou")
				&& !webView.getUrl().contains("MobileAppCustomLogin")
				&& !webView.getUrl().contains("MobileAppContactUsThankYou")
				&& !webView.getUrl().contains("MobileAppSuggestionThankYou")
				&& !webView.getUrl().contains("MobileAppForgotPasswordThankYou")
				&& !webView.getUrl().contains("MobileAppCompanyNotValidError")) {
			webView.goBack();
		}
	}

	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	public void showNoInternetAlert() {
		AlertDialog.Builder noInternetDialog = new AlertDialog.Builder(this);

		noInternetDialog.setTitle("Error");
		noInternetDialog
				.setMessage("No Internet connection. Please connect and try again.");

		noInternetDialog.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (isConnectingToInternet())
				{
					webView.clearView();
					webView.reload();
					
					webView.setVisibility(View.VISIBLE);
				}
				else
				{
					dialog.dismiss();
					showNoInternetAlert();
				}
				
			}
		});

		noInternetDialog.create().show();
	}

	private class CustomWebViewClient extends WebViewClient {

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO the error page.
			webView.setVisibility(View.INVISIBLE);
			showNoInternetAlert();
		}
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
			handler.proceed();
		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith("tel:")) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
				startActivity(intent);
			} else if (url.startsWith("http:") || url.startsWith("https:")) {
				if (isConnectingToInternet())
					view.loadUrl(url);
				else {
					showNoInternetAlert();
				}
			}
			return true;
		}
	}
}

/*
 * CookieSyncManager.createInstance(this); CookieManager cookieManager =
 * CookieManager.getInstance(); cookieManager.removeAllCookie();
 * cookieManager.setAcceptCookie(false)
 * 
 * WebView webview = new WebView(this); WebSettings ws = webview.getSettings();
 * ws.setSaveFormData(false); ws.setSavePassword(false); // Not needed for API
 * level 18 or greater (deprecated)
 */