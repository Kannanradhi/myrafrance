package com.isteer.b2c.utility;

import android.webkit.WebView;

public class SingletoneWebview {
	private static SingletoneWebview Instance;
	public WebView webview;
	public static void InitInstance()
	{
		if(Instance==null)
		{
			Instance = new SingletoneWebview();
		}
	}
	private SingletoneWebview()
	{
		
	}
	public static SingletoneWebview getInstance()
	{
		return Instance;
	}
	public void setCordovaWebview(WebView webview)
	{
		this.webview = webview;
	}
	public WebView retrieveCordovaWebview()
	{
		return this.webview;
	}

}
