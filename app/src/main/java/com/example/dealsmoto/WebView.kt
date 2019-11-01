package com.example.dealsmoto

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import kotlinx.android.synthetic.main.activity_web_view.*

class WebView : AppCompatActivity() {

    private val URL = "http://10.0.2.2:8000"
    private var isAlreadyCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        startLoaderAnimate()

        webview.settings.javaScriptEnabled = true
        webview.settings.setSupportZoom(false)

        webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                endLoaderAnimate()
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                endLoaderAnimate()
                showErrorDialog("Error",
                    "No internet connection. Please check your connection.",
                    this@WebView)
            }
        }
        webview.loadUrl(URL)
    }

    override fun onResume() {
        super.onResume()

        if (isAlreadyCreated && !isNetworkAvailable()) {
            isAlreadyCreated = false
            showErrorDialog("Error",
                "No internet connection. Please check your connection.",
                this@WebView)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectionManager =
            this@WebView.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo
     return networkInfo != null && networkInfo.isConnectedOrConnecting
        //return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()){
            webview.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showErrorDialog(title: String, message: String, context: Context){
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setNegativeButton("Cancel", { _, _ ->
            this@WebView.finish()})
        dialog.setNeutralButton("Settings", { _, _ ->
            startActivity(Intent(Settings.ACTION_SETTINGS))})
        dialog.setPositiveButton("Retry", { _, _ ->
            this@WebView.recreate()})
        dialog.create().show()

    }
    private fun endLoaderAnimate() {
        loaderImage.clearAnimation()
        loaderImage.visibility = View.GONE
    }

    private fun startLoaderAnimate() {
       val objectAnimator = object : Animation(){
           override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
               val startHeight = 170
               val newHeight = (startHeight * (startHeight + 40) * interpolatedTime).toInt()
               loaderImage.layoutParams.height = newHeight
               loaderImage.requestLayout()
           }

           override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
               super.initialize(width, height, parentWidth, parentHeight)
           }

           override fun willChangeBounds(): Boolean {
               return true
           }
       }

        objectAnimator.repeatCount = -1
        objectAnimator.repeatMode = ValueAnimator.REVERSE
        objectAnimator.duration = 1000
        loaderImage.startAnimation(objectAnimator)
    }

}
