package telenor.com.ep_v1_sdk.ui.activities

import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_payment_webview.*
import kotlinx.android.synthetic.main.ll_top_view.*
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.config.PAYMENTURL
import telenor.com.ep_v1_sdk.ui.FeedBackCall
import telenor.com.ep_v1_sdk.ui.StopHandlerCallback
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.webkit.WebView
import android.net.http.SslError
import android.webkit.SslErrorHandler
import telenor.com.ep_v1_sdk.config.LOADINGMSG

class EasypayPaymentWebView : AppCompatActivity() {



    lateinit var progressBar: ProgressBar
    lateinit var feedbackCall: FeedBackCall
    var webviewShown : Boolean = false
    var loadingMsg: String = ""

    companion object{
        lateinit var stopHandlerCallback: StopHandlerCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_webview)
        //setting toolbar
        setHeadingTitle(resources.getString(R.string.easypaisa_wallet))

        stopHandlerCallback = object : StopHandlerCallback{
            override fun closeWebview(status: Boolean) {
                Log.d("Webview stopHandlerInte", "Called")

            }
        }

        var urlHtml = intent.getSerializableExtra(PAYMENTURL) as String
        if (intent.getStringExtra(LOADINGMSG) != null)
            loadingMsg = intent.getStringExtra(LOADINGMSG)

        webviewShown = true


        webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {

            }
        }

        webview.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                Log.d("Failure Url :", failingUrl)
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                Log.d("Ssl Error:", handler.toString() + "error:" + error.toString())
                //handler.proceed()
                // possible solution
                val builder = AlertDialog.Builder(view.context);
                //builder.setMessage("Your request is being processed. Continue ?")
                builder.setMessage("There are problems with the security certificate for this site. Do you want to continue?")
                builder.setPositiveButton("Continue"){dialog, which ->
                    handler.proceed()
                }
                builder.setNegativeButton("Cancel"){dialog, which ->
                    handler.cancel()
                }
                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
                //
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            /*override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.d("Page Started:", "loading shown")
                ActivityUtil().loadingStarted(loading_spinner, rl_progress, tv_progress, loadingMsg, this@EasypayPaymentWebView)
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("Page Finished:", "loading finished")
                ActivityUtil().loadingStop(loading_spinner, rl_progress, tv_progress, this@EasypayPaymentWebView)
                super.onPageFinished(view, url)
            }*/
        }
        webview.settings.javaScriptEnabled = true
        webview.settings.loadWithOverviewMode = true
        webview.settings.useWideViewPort = true
        webview.settings.domStorageEnabled = true
        webview.loadData(urlHtml, "text/html","UTF-8")
//        webview.loadDataWithBaseURL("", urlHtml, "text/html", "UTF-8", "");

      //  feedbackCall.checkFeedback()
        EasypayPaymentForm.obj = object : FeedBackCall{
            override fun checkFeedback(check: Boolean) {
                Log.d("WebView Interface", "Calling finish")
                if (check){
                    finish()
                }
            }
        }
    }


    private fun setHeadingTitle(title: String) {

        val toolbar = findViewById<Toolbar>(R.id.mainToolbar_payment_webview)
        val mTitle = toolbar.findViewById(R.id.toolbar_title_payment_webview) as TextView
        mTitle.text = title
        mTitle.setTypeface(null, Typeface.BOLD)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.back)
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                Snackbar.make(webviewLayout , R.string.operation_not_allowed, Snackbar.LENGTH_SHORT).show()
//                onBackPressed()
//                stopHandlerCallback.closeWebview(true)
            }
        })

    }

    override fun onBackPressed() {

        if (!webviewShown){
            super.onBackPressed()
        }
        else{
            Snackbar.make(webviewLayout , R.string.operation_not_allowed, Snackbar.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

//        stopHandlerCallback.closeWebview(true)

    }

}