package telenor.com.ep_v1_sdk.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_form_main.*
import kotlinx.android.synthetic.main.ll_form_cc.*
import kotlinx.android.synthetic.main.ll_top_view.*
import org.json.JSONObject
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.config.*
import telenor.com.ep_v1_sdk.config.model.*
import telenor.com.ep_v1_sdk.rest.HttpTask
import telenor.com.ep_v1_sdk.rest.HttpTask2
import telenor.com.ep_v1_sdk.ui.*
import telenor.com.ep_v1_sdk.ui.fragment.*
import telenor.com.ep_v1_sdk.util.ActivityUtil
import telenor.com.ep_v1_sdk.util.InternetHelper
import telenor.com.ep_v1_sdk.util.Validation
import java.text.SimpleDateFormat
import java.util.*


class EasypayPaymentForm  : AppCompatActivity(), EasypayCall, CardResponseCallback {


    lateinit var storeConfig: EPConfiguration
    lateinit var paymentOrder :EPPaymentOrder
    lateinit var donationBaseResponse: DonationBaseResponse
    lateinit var merchantAccountId: String
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    lateinit var feedBackCall: FeedBackCall
    var iterationTime : Long = 0
    var intervalsCount : Int = 0
    var iterationTimeMigs : Long = 0
    var intervalsCountMigs : Int = 0
    var delay : Long = 0
    var counter : Int = 0
    var transactionQueryDRCommand = ""
    var activityName: String = "FormActivity"
    var isLoading : Boolean = false
    var isLoadingCC : Boolean = false
    var fragmentCC : EasypayPaymentCC? = null
//    var transactionVerified  = false
    var mpgsReattempt : Boolean = false
    lateinit var paymentCCCardResponse: PaymentCCCardResponse

    companion object{
        lateinit var obj : FeedBackCall
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_main)

        val intent = Intent()
        intent.putExtra(TRANSACTIONSTATUS, "false")
        setResult(200, intent)
        initView()
//        feedBackCall = this

        obj = object : FeedBackCall{
            override fun checkFeedback(check: Boolean) {
                Log.d("Form Interface", "Called")
            }
        }

        EasypayPaymentWebView.stopHandlerCallback = object : StopHandlerCallback{
            override fun closeWebview(status: Boolean) {
                Log.d("FormAct stopHandlerInte", "Called")
                if (status){
                    handler.removeCallbacks(runnable)
                }
            }

        }

    }

    override fun onResume() {
        super.onResume()

//        EasypayPaymentWebView.stopHandlerCallback = object : StopHandlerCallback{
//            override fun closeWebview(status: Boolean) {
//                Log.d("FormAct stopHandlerInte", "Called")
//                if (status){
//                    handler.removeCallbacks(runnable)
//                }
//            }
//
//        }
    }
    private fun initView(){
        //setting toolbar
        setHeadingTitle(resources.getString(R.string.easypaisa_wallet))

        storeConfig = intent.getSerializableExtra(CONFIGURATION) as EPConfiguration
        paymentOrder = intent.getSerializableExtra(PAYMENTORDER) as EPPaymentOrder
        merchantAccountId = intent.getStringExtra(MERCHANTACCOUNTID) as String

        setStoreDetails(storeConfig,paymentOrder)
        handler = Handler()
        val allowedPaymentMethods = intent.getSerializableExtra(PAYMENTTYPE) as AllowedPaymentMethods
        when(allowedPaymentMethods.code){
            "MA" -> {
                initMA()
            }
            "OTC" -> {
                initOTC()
            }
            "CC" -> {
                initCC()
            }
            "DD" -> {
                initDD()
            }
        }
        getCharityPackages()
    }
    private fun initMA() {

        var fragment: Fragment? = supportFragmentManager.findFragmentByTag(activityName)
        if (fragment == null) {
            fragment = EasypayPaymentMA.newInstance(this)
        }
        fragment.arguments = intent.extras
        ActivityUtil().replaceFragment(this,fragment, false, R.id.frame_content, activityName)
    }
    private fun initOTC() {
        var fragment: Fragment? = supportFragmentManager.findFragmentByTag(activityName)
        if (fragment == null) {
            fragment = EasypayPaymentOTC(this)
        }
        fragment.arguments = intent.extras
        ActivityUtil().replaceFragment(this,fragment, false, R.id.frame_content, activityName)
    }
    private fun initCC() {
        var fragment: Fragment? = supportFragmentManager.findFragmentByTag(activityName)
        if (fragment == null) {
            fragment = EasypayPaymentCC(this, this)
            fragmentCC = fragment
            //fragment.setError("hello")
        }
        fragment.arguments = intent.extras
        ActivityUtil().replaceFragment(this,fragment, false, R.id.frame_content, activityName)


    }
    private fun initDD() {
        var fragment: Fragment? = supportFragmentManager.findFragmentByTag(activityName)
        if (fragment == null) {
            fragment = EasypayPaymentDD(this)
        }
        fragment.arguments = intent.extras
        ActivityUtil().replaceFragment(this,fragment, false, R.id.frame_content, activityName)
    }
    override fun paymentMethod(
        storeConfig: EPConfiguration,
        paymentOrder: EPPaymentOrder,
        allowedPaymentMethods: AllowedPaymentMethods
    ) {
        when(allowedPaymentMethods.code){
            "MA"->{paymentMA(storeConfig,paymentOrder,allowedPaymentMethods)}
            "OTC"->{paymentOTC(storeConfig,paymentOrder,allowedPaymentMethods)}
            "CC"->{paymentCC(storeConfig,paymentOrder,allowedPaymentMethods)}
            "DD"->{paymentDD(storeConfig,paymentOrder,allowedPaymentMethods)}
        }

    }

    override fun cardResponse(response: PaymentCCCardResponse) {

        if (response.content != null){

            paymentCCCardResponse = response
            Log.d("IterationTime", "Delay ->" + response.content.successiveWaitInterval+"")
            Log.d("TotTime/IterTime", "Counter ->" + (response.content.firstWaitInterval/response.content.successiveWaitInterval)+"")
            try {
                iterationTime = response.content.successiveWaitInterval.toLong()
                intervalsCount = (response.content.firstWaitInterval / response.content.successiveWaitInterval)
                iterationTimeMigs = response.content.successiveWaitIntervalForMigs.toLong()
                intervalsCountMigs = (response.content.firstWaitIntervalForMigs.toInt() / response.content.successiveWaitIntervalForMigs.toInt())
                transactionQueryDRCommand = response.content.transactionQueryDRCommand

//                iterationTime = 20000
//                intervalsCount = 10
                Log.d("iterationTime", "Delay ->" + iterationTime+"")
                Log.d("intervalsCount", "Count ->" + intervalsCount+"")
                Log.d("iterationTimeMigs", "Delay Migs->" + iterationTimeMigs+"")
                Log.d("intervalsCountMigs", "Count Migs->" + intervalsCountMigs+"")
                Log.d("transQueryDRCmd ->", transactionQueryDRCommand)
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun paymentMA(storeConfig: EPConfiguration, paymentOrder: EPPaymentOrder, allowedPaymentMethods: AllowedPaymentMethods) {

        if(!ActivityUtil().isLoading){
        val json = JSONObject()
        json.put(STOREID, storeConfig.storeId.toString())
        json.put(ORDERID, paymentOrder.orderId.toString())
        json.put(TRANSACTIONAMOUNT, paymentOrder.orderAmount)
        json.put(TRANSACTIONTYPE, allowedPaymentMethods.code)
        json.put(MOBILEACCOUTNUMBER, paymentOrder.phone)
        json.put(EMAILADDRESS, paymentOrder.email)

        var postData = ""
        if (!paymentOrder.orderAmount.toString().isNullOrEmpty()) {
            postData += ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL + paymentOrder.orderAmount
        }
        if (!paymentOrder.email.toString().isNullOrEmpty()){
            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_EMAILADDRESS + ENCRYPT_KEY_EQUAL + paymentOrder.email
        }

        if (!paymentOrder.phone.toString().isNullOrEmpty()) {
            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_MOBILENUM + ENCRYPT_KEY_EQUAL + paymentOrder.phone.toString()
        }

        if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL +paymentOrder.orderId.toString()
        }

        if (!allowedPaymentMethods.code.toString().isNullOrEmpty()) {
            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL  + allowedPaymentMethods.code.toString()
        }
        if(!storeConfig.storeId.toString().isNullOrEmpty()) {
            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
        }


        val encryptedRequest = Validation().encrypt(postData.toString(),storeConfig.secretKey)
            json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))

        if(InternetHelper().isInternetConnected(this)) {
//                var msg= storeConfig.storeName + " has requested to pay Rs." + paymentOrder.orderAmount.toString() + ". To confirm enter 5-digit PIN sent "
//                ActivityUtil().loadingStarted(loading_spinner,rl_progress,tv_progress ,msg,this)
                //loadingGifStart()
                ActivityUtil().loadingGifStarted(ivLoadingGif_ma, this)
                isLoading = true
            Handler().postDelayed({
                HttpTask(this)  {
//                    loadingGifStop()
//                    ActivityUtil().loadingStop(loading_spinner,rl_progress,tv_progress ,this)
                    ActivityUtil().loadingGifStop(ivLoadingGif_ma, this)
                    isLoading = false
                    if (it == null) {
                        Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show()
                        return@HttpTask
                    } else {

                        var json = it.toString() // json value

                        if (json.contains("\"responseCode\":\"\"")){
                            json = json.replace("\"content\":\"\",", "").replace("\"responseCode\":\"\"", "\"responseCode\":\"-1\"")
                        }
                        val response = Gson().fromJson(json, PaymentBaseResponse::class.java)

                        when (response.ackMessage.responseCode.toString().contentEquals("0000")) {
                            true -> {

                                val intent = Intent(this, Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback"))
                                intent.putExtra(CONFIGURATION, storeConfig)
                                intent.putExtra(PAYMENTORDER, paymentOrder)
                                intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                                intent.putExtra(PAYMENTRESPONSE,response)
                                intent.putExtra(CHARITYRESPONSE,donationBaseResponse)
                                intent.putExtra(TRANSACTIONSTATUS, "true")
                                intent.putExtra(CHARITYRESULT, "false")
                                setResult(200, intent)
                                startActivity(intent)
                                this.finish()


                            }
                            false -> {
                                val intent = Intent(this, Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback"))
                                intent.putExtra(CONFIGURATION, storeConfig)
                                intent.putExtra(PAYMENTORDER, paymentOrder)
                                intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                                intent.putExtra(PAYMENTRESPONSE,response)
                                intent.putExtra(CHARITYRESPONSE,donationBaseResponse)
                                intent.putExtra(TRANSACTIONSTATUS, "true")
                                intent.putExtra(CHARITYRESULT, "false")
                                setResult(200, intent)
                                startActivity(intent)
                                this.finish()


                            }
                        }
                    }

                }.execute(
                    POST,
                    storeConfig.baseUrl/*BASE_URL*/+ MA_INITIATETRANSACTION_ENDPOINT,
                    json.toString()
                )

            }, 6000)


        }
        else{
            Toast.makeText(this, "Internet Not Available !", Toast.LENGTH_LONG).show()
        }
        }
    }

    private fun paymentOTC( storeConfig: EPConfiguration, paymentOrder: EPPaymentOrder, allowedPaymentMethods: AllowedPaymentMethods ) {


        if(!ActivityUtil().isLoading){
        val json = JSONObject()
        json.put(STOREID, storeConfig.storeId)
        json.put(ORDERID, paymentOrder.orderId)
        json.put(TRANSACTIONAMOUNT, paymentOrder.orderAmount)
        json.put(TRANSACTIONTYPE, allowedPaymentMethods.code)
        var expiryToken = storeConfig.expiryToken
        var d1 =expiryToken.replace("-","")
        var d2 = d1.replace(":","")
        json.put(TOKENEXPIRY, d2)
        json.put(MSISDN, paymentOrder.phone)
        json.put(EMAILADDRESS, paymentOrder.email)

        var postData = ""
        if (!paymentOrder.orderAmount.toString().isNullOrEmpty()) {
            postData += ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL  + paymentOrder.orderAmount
        }
        if (!paymentOrder.email.isNullOrEmpty()){
            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_EMAILADDRESS + ENCRYPT_KEY_EQUAL + paymentOrder.email
        }

        if (!storeConfig.expiryToken.isNullOrEmpty()) {
            var expiryToken = storeConfig.expiryToken
            var d1 =expiryToken.replace("-","")
            var d2 = d1.replace(":","")
            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_EXPIRYDATE + ENCRYPT_KEY_EQUAL + d2
        }
        if (!paymentOrder.phone.isNullOrEmpty()) {
            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_MOBILENUM + ENCRYPT_KEY_EQUAL + paymentOrder.phone
        }

        if (!paymentOrder.orderId.isNullOrEmpty()) {
            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL +paymentOrder.orderId
        }

        if (!allowedPaymentMethods.code.isNullOrEmpty()) {
            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL + allowedPaymentMethods.code
        }
        if(!storeConfig.storeId.toString().isNullOrEmpty()) {
            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId
        }


        val encryptedRequest = Validation().encrypt(postData.toString(),storeConfig.secretKey)
        json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))

        if(InternetHelper().isInternetConnected(this)) {
            ActivityUtil().loadingStarted(loading_spinner,rl_progress,tv_progress ,"",this)
            isLoading = true
            HttpTask(this) {
                ActivityUtil().loadingStop(loading_spinner,rl_progress,tv_progress ,this)
                isLoading = false
                if (it == null) {
                    Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show()
                    return@HttpTask
                } else {
                    var json = it.toString() // json value

                    if (json.contains("\"responseCode\":\"\"")){
                        json = json.replace("\"content\":\"\",", "").replace("\"responseCode\":\"\"", "\"responseCode\":\"-1\"")
                    }
                    val response = Gson().fromJson(json, PaymentBaseResponse::class.java)

                    when (response.ackMessage.responseCode.toString().contentEquals("0000")) {
                        true -> {

                            val intent = Intent(this, Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback"))
                            intent.putExtra(CONFIGURATION, storeConfig)
                            intent.putExtra(PAYMENTORDER, paymentOrder)
                            intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                            intent.putExtra(PAYMENTRESPONSE,response)
                            intent.putExtra(TRANSACTIONSTATUS, "true")
                            setResult(200, intent)
                            startActivity(intent)
                            this.finish()



                        }
                        false -> {
                            val intent = Intent(this, Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback"))
                            intent.putExtra(CONFIGURATION, storeConfig)
                            intent.putExtra(PAYMENTORDER, paymentOrder)
                            intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                            intent.putExtra(PAYMENTRESPONSE,response)
                            intent.putExtra(CHARITYRESPONSE,donationBaseResponse)
                            intent.putExtra(TRANSACTIONSTATUS, "true")
                            setResult(200, intent)
                            startActivity(intent)
                            this.finish()


                        }
                    }
                }

            }.execute(
                POST,
                storeConfig.baseUrl/*BASE_URL*/+ OTC_INITIATETRANSACTION_ENDPOINT,
                json.toString()
            )

        }
        else{
            Toast.makeText(this, "Internet Not Available !", Toast.LENGTH_LONG).show()
        }}
    }
    private fun paymentCC( storeConfig: EPConfiguration, paymentOrder: EPPaymentOrder, allowedPaymentMethods: AllowedPaymentMethods ) {

        if(!ActivityUtil().isLoading){

        val encrytedCreditCard = Validation().encrypt(paymentOrder.creditCard, storeConfig.secretKey)
        val  encrytedCreditCardNumber =  encrytedCreditCard.replace("\n","")

        var phoneNumber = paymentOrder.phone.replaceFirst("0","+92")
        val json = JSONObject()
        json.put(STOREID, storeConfig.storeId)
        json.put(ORDERID, paymentOrder.orderId)
        json.put(TRANSACTIONAMOUNT, paymentOrder.orderAmount)
        json.put(TRANSACTIONTYPE, allowedPaymentMethods.code)

        json.put(MOBILEACCOUTNUMBER, phoneNumber)
        json.put(EMAILADDRESS, paymentOrder.email)
        //json.put(CREDITCARDNUMBER, paymentOrder.creditCard)
        json.put(CREDITCARDNUMBER, encrytedCreditCardNumber)

        if (!storeConfig.bankIdentifier.toString().isNullOrEmpty()) {
            json.put(BANKIDNUMBER, storeConfig.bankIdentifier)
        }
        json.put(EXPIRYMONTH, paymentOrder.creditCardMonth)
        json.put(EXPIRYYEAR, paymentOrder.creditCardYear)
        json.put(CVV, paymentOrder.creditCardCVV)


            var postData = ""

            if (storeConfig.bankIdentifier.isNullOrEmpty()){

                if (!paymentOrder.orderAmount.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL + paymentOrder.orderAmount
                }
                if (!paymentOrder.creditCardMonth.toString().isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_CC_EXPIRY_MONTH + ENCRYPT_KEY_EQUAL + paymentOrder.creditCardMonth
                }
                if (!paymentOrder.creditCardYear.toString().isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_CC_EXPIRY_YEAR + ENCRYPT_KEY_EQUAL + paymentOrder.creditCardYear
                }
                if (!encrytedCreditCardNumber.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_CC_NUM + ENCRYPT_KEY_EQUAL + encrytedCreditCardNumber
                }
                if (!paymentOrder.creditCardCVV.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_CC_CVV + ENCRYPT_KEY_EQUAL + paymentOrder.creditCardCVV
                }
                if (!paymentOrder.email.toString().isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_EMAILADDRESS + ENCRYPT_KEY_EQUAL + paymentOrder.email
                }
                if (!paymentOrder.phone.toString().isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_MOBILENUM + ENCRYPT_KEY_EQUAL + phoneNumber
                }
                if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL +paymentOrder.orderId.toString()
                }
                if (!allowedPaymentMethods.code.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL + allowedPaymentMethods.code.toString()
                }
                if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
                }

            }
            else{
                if (!paymentOrder.orderAmount.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL + paymentOrder.orderAmount
                }
                if (!storeConfig.bankIdentifier.isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_BANKIDENTIFICATIONNUM + ENCRYPT_KEY_EQUAL + storeConfig.bankIdentifier
                }
                if (!paymentOrder.creditCardMonth.toString().isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_CC_EXPIRY_MONTH + ENCRYPT_KEY_EQUAL + paymentOrder.creditCardMonth
                }
                if (!paymentOrder.creditCardYear.toString().isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_CC_EXPIRY_YEAR + ENCRYPT_KEY_EQUAL + paymentOrder.creditCardYear
                }
                if (!encrytedCreditCardNumber.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_CC_NUM + ENCRYPT_KEY_EQUAL + encrytedCreditCardNumber
                }
                if (!paymentOrder.creditCardCVV.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_CC_CVV + ENCRYPT_KEY_EQUAL + paymentOrder.creditCardCVV
                }
                if (!paymentOrder.email.toString().isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_EMAILADDRESS + ENCRYPT_KEY_EQUAL + paymentOrder.email
                }
                if (!paymentOrder.phone.toString().isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_MOBILENUM + ENCRYPT_KEY_EQUAL + phoneNumber
                }
                if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL +paymentOrder.orderId.toString()
                }
                if (!allowedPaymentMethods.code.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL + allowedPaymentMethods.code.toString()
                }
                if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
                }

            }

            val encryptedRequest = Validation().encrypt(postData.toString(),storeConfig.secretKey)
            json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))


            if(InternetHelper().isInternetConnected(this)) {

            ActivityUtil().loadingStarted(loading_spinner,rl_progress,tv_progress ,"",this)
            isLoading = true
            isLoadingCC = true
                fragmentCC?.disablePayNowBtn()
            HttpTask(this) {
//                ActivityUtil().loadingStop(loading_spinner,rl_progress,tv_progress  ,this)
                isLoading = false
                if (it == null) {
                    Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show()
                    return@HttpTask
                } else {
                    var json = it.toString() // json value

                    if (json.contains("\"responseCode\":\"\"")){
                        json = json.replace("\"content\":\"\",", "").replace("\"responseCode\":\"\"", "\"responseCode\":\"-1\"")
                    }
                    else if (json.contains("\"responseCode\":\"Time Out\"")){
                        json = json.replace("\"content\":\"\",", "").replace("\"responseCode\":\"\"", "\"responseCode\":\"-1\"")
                    }
                    val response = Gson().fromJson(json, PaymentBaseResponse::class.java)

                    Log.d("CC Response", response.toString())
                    when (response.ackMessage.responseCode.toString().contentEquals("0000")) {
                        true -> {

                            if(response.content.completeHtmlBody.isNullOrEmpty()) {

                                startTimerHandler(response, false, allowedPaymentMethods )

                            }
                            else{

                                if (isLoadingCC) {
                                    ActivityUtil().loadingStop(loading_spinner, rl_progress, tv_progress, this)
                                    isLoadingCC = false
//                                    fragmentCC?.enablePayNowBtn()

                                }
//                                val firstHtmlTag = "<html>"
//                                val lastHtmlTag = "</html>"
//                                var html = firstHtmlTag + response.content.htmlBody + lastHtmlTag
                                val intent = Intent(
                                    this,
                                    Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentWebView")
                                )
                                intent.putExtra(CONFIGURATION, storeConfig)
                                intent.putExtra(PAYMENTORDER, paymentOrder)
                                intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                                intent.putExtra(PAYMENTURL, response.content.completeHtmlBody)
                                intent.putExtra(PAYMENTRESPONSE, response)
                                intent.putExtra(TRANSACTIONSTATUS, "true")
                                intent.putExtra(LOADINGMSG, paymentCCCardResponse.content.reattemptMsg)
                                setResult(200, intent)
                                startActivity(intent)
                                //this.finish()

                                startTimerHandler(response, true, allowedPaymentMethods )

                            }
                        }
                        false -> {

                            if (isLoadingCC) {
                                ActivityUtil().loadingStop(loading_spinner, rl_progress, tv_progress, this)
                                isLoadingCC = false
                                //fragmentCC?.enablePayNowBtn()
                            }
                            val intent = Intent(
                                this,
                                Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback")
                            )
                            intent.putExtra(CONFIGURATION, storeConfig)
                            intent.putExtra(PAYMENTORDER, paymentOrder)
                            intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                            intent.putExtra(PAYMENTRESPONSE, response)
                            intent.putExtra(TRANSACTIONSTATUS, "true")
                            intent.putExtra(CCRESPONSESUCCESS, "false")
                            setResult(200, intent)
                            startActivity(intent)
                            this.finish()


                            //  fragmentCC?.setError(response.ackMessage.responseDescription)


                        }
                    }
                }

            }.execute(
                POST,
                storeConfig.baseUrl/*BASE_URL*/+ CC_INITIATETRANSACTION_ENDPOINT,
                json.toString()
            )

        }
        else{
            Toast.makeText(this, "Internet Not Available !", Toast.LENGTH_LONG).show()
        }
        }
    }

    private fun startTimerHandler(response: PaymentBaseResponse, withHtmlFlag: Boolean, allowedPaymentMethods: AllowedPaymentMethods){


        if(response.content.paymentGatewayType.contentEquals("MPGS")) {

            delay = iterationTime
            counter = intervalsCount
        }
        else if (response.content.paymentGatewayType.contentEquals("MIGS")){

            delay = iterationTimeMigs
            counter = intervalsCountMigs
        }

        handler.apply {
            var count = 0
            runnable = object : Runnable {
                override fun run() {
                    if (++count <= counter /*&& !transactionVerified*/) {

                        getCCTransactionStatus(response, true, allowedPaymentMethods, count)

                        postDelayed(this, delay)
                    }
                }
            }
            postDelayed(runnable, delay)
        }



    }

    private fun getCCTransactionStatus(responseCC: PaymentBaseResponse, withHtmlFlag : Boolean, allowedPaymentMethods: AllowedPaymentMethods, count: Int){

        if (!ActivityUtil().isLoading) {


            val json = JSONObject()

            if (responseCC.content.paymentGatewayType.contentEquals("MPGS")){

                json.put(ORDERID, paymentOrder.orderId.toString())
                json.put(STOREID, storeConfig.storeId.toString())
                json.put(TRANSACTIONAMOUNT, paymentOrder.orderAmount)
                json.put(TRANSACTIONTYPE, allowedPaymentMethods.code)
                json.put(TRANSACTIONDATETIME, responseCC.content.transactionDateTime)
                json.put(TRANSACTIONID, responseCC.content.transactionId)
                json.put(MPGSREATTEMPTCOMPLETEDFLAG, mpgsReattempt)
                json.put(CCTRANSACTIONTYPE, "CC_TRANSACTION_STATUS_MPGS")
                //json.put(REATTEMPTFAILUREMSG, paymentCCCardResponse.content.reattemptFailureMsg)

                var postData = ""

                if (!paymentOrder.orderAmount.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL + paymentOrder.orderAmount
                }

                postData += ENCRYPT_KEY_AMPERSAND + CCTRANSACTIONTYPE + ENCRYPT_KEY_EQUAL  + "CC_TRANSACTION_STATUS_MPGS"

                postData += ENCRYPT_KEY_AMPERSAND + MPGSREATTEMPTCOMPLETEDFLAG + ENCRYPT_KEY_EQUAL  + mpgsReattempt

                if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL + paymentOrder.orderId
                }

                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL  + allowedPaymentMethods.code

                if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
                }

                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_TRANSACTION_DATETIME + ENCRYPT_KEY_EQUAL + responseCC.content.transactionDateTime

                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_TRANSACTION_ID + ENCRYPT_KEY_EQUAL + responseCC.content.transactionId


                val encryptedRequest = Validation().encrypt(postData.toString(),storeConfig.secretKey)
                json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))

            }
            else if (responseCC.content.paymentGatewayType.contentEquals("MIGS")){

                // New from Atif starts
                json.put(ORDERID, paymentOrder.orderId.toString())
                json.put(STOREID, storeConfig.storeId.toString())
                json.put(TRANSACTIONAMOUNT, paymentOrder.orderAmount)
                json.put(TRANSACTIONTYPE, allowedPaymentMethods.code)
                json.put(TRANSACTIONDATETIME, responseCC.content.transactionDateTime)
                //json.put(TRANSACTIONID, responseCC.content.transactionId)
                // New from Atif ends
                json.put(TRANSACTIONID, responseCC.content.transactionId)
                json.put(MIGSREATTEMPTCOMPLETEDFLAG, mpgsReattempt)
                json.put(CCTRANSACTIONTYPE, "CC_TRANSACTION_STATUS_MIGS")
                json.put(TRANSACTIONQUERYDRCOMMAND, paymentCCCardResponse.content.transactionQueryDRCommand)
                //json.put(QUERYDRREATTEMPTCOUNT, paymentCCCardResponse.content.queryDRAttempts)
                //json.put(REATTEMPTFAILUREMSG, paymentCCCardResponse.content.reattemptFailureMsgForMigs)
                json.put(VERSION, responseCC.content.version)

                var postData = ""

                if (!paymentOrder.orderAmount.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL + paymentOrder.orderAmount
                }

                postData += ENCRYPT_KEY_AMPERSAND + CCTRANSACTIONTYPE + ENCRYPT_KEY_EQUAL  + "CC_TRANSACTION_STATUS_MIGS"

                postData += ENCRYPT_KEY_AMPERSAND + MPGSREATTEMPTCOMPLETEDFLAG + ENCRYPT_KEY_EQUAL  + mpgsReattempt

                if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL + paymentOrder.orderId
                }

                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL  + allowedPaymentMethods.code

                if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
                }

                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_TRANSACTION_DATETIME + ENCRYPT_KEY_EQUAL + responseCC.content.transactionDateTime

                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_TRANSACTION_ID + ENCRYPT_KEY_EQUAL + responseCC.content.transactionId

                postData += ENCRYPT_KEY_AMPERSAND + TRANSACTIONQUERYDRCOMMAND + ENCRYPT_KEY_EQUAL + paymentCCCardResponse.content.transactionQueryDRCommand


                val encryptedRequest = Validation().encrypt(postData.toString(),storeConfig.secretKey)
                json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))


            }


            Log.d("count->", count.toString())
            Log.d("cc/trx-status request->", json.toString())
            Log.d("-------", "------------------------------------------------------------")

            if (InternetHelper().isInternetConnected(this)) {
                isLoading = true
                //ActivityUtil().loadingStarted(loading_spinner, this)
                HttpTask(this) {
                    //ActivityUtil().loadingStop(loading_spinner, this)
                    isLoading = false
                    if (it == null) {
                        Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show()
                        return@HttpTask
                    } else {
                        var json = it.toString() // json value
                        if (!json.contains("\"responseCode\":\"0000\"")){
                            json = json.replace("\"content\":\"\",", "").replace("\"responseCode\":\"\"", "\"responseCode\":\"-1\"")
                        }
                        var response = Gson().fromJson(json, CCTransactionStatusResponse::class.java)

                        Log.d("count->", count.toString())
                        Log.d("cc/trx-status resp->", response.toString())
                        Log.d("-------", "------------------------------------------------------------")


                            if ((response.content.pendingCaptureInd.contentEquals("1")
                                        && response.content.status.contentEquals("AUTHORIZED"))
                                || (response.content.pendingCaptureInd.contentEquals("0")
                                        && response.content.status.contentEquals("PAID"))
                                    ){

                                // SUCCESS

                                if (isLoadingCC) {
                                    ActivityUtil().loadingStop(loading_spinner, rl_progress, tv_progress, this)
                                    isLoadingCC = false
                                    fragmentCC?.enablePayNowBtn()

                                }
                                handler.removeCallbacks(runnable)
//                                transactionVerified = true
                                val intent = Intent(
                                    this,
                                    Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback")
                                )
                                //val feedBackCall : FeedBackCall
                                //feedBackCall.checkFeedback(true)
                                obj.checkFeedback(true)
                                intent.putExtra(CONFIGURATION, storeConfig)
                                intent.putExtra(PAYMENTORDER, paymentOrder)
                                intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                                intent.putExtra(PAYMENTRESPONSE, response)
                                intent.putExtra(TRANSACTIONSTATUS, "true")
                                intent.putExtra(CCRESPONSESUCCESS, "true")
                                setResult(200, intent)
                                startActivity(intent)
                                this.finish()

                            }
                            else if( ((response.content.pendingCaptureInd.contentEquals("1")
                                        || response.content.pendingCaptureInd.contentEquals("0"))
                                && (response.content.status.contentEquals("FAILED")
                                        /*|| response.content.status.contentEquals("PENDING")*/)) /*|| count == 5*/){
                                // FAILURE

//                                if (count == 5) {
                                if (isLoadingCC) {
                                    ActivityUtil().loadingStop(loading_spinner, rl_progress, tv_progress, this)
                                    isLoadingCC = false
                                    fragmentCC?.enablePayNowBtn()

                                }
                                handler.removeCallbacks(runnable)
                                val intent = Intent(
                                    this,
                                    Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback")
                                )
                                //feedBackCall.checkFeedback(true)
                                obj.checkFeedback(true)
                                intent.putExtra(CONFIGURATION, storeConfig)
                                intent.putExtra(PAYMENTORDER, paymentOrder)
                                intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                                intent.putExtra(PAYMENTRESPONSE, response)
                                if (mpgsReattempt) {
                                    intent.putExtra(MPGSFLAG,"true")
                                    intent.putExtra(MPGSORMIGS, responseCC.content.paymentGatewayType)
                                    intent.putExtra(PAYMENTCCCARDRESPONSE, paymentCCCardResponse)
                                }
                                intent.putExtra(TRANSACTIONSTATUS, "true")
                                intent.putExtra(CCRESPONSESUCCESS, "true")
                                setResult(200, intent)
                                startActivity(intent)
                                this.finish()
//                                }

                            }
                            else if (((response.content.status.contentEquals("INTERIM")
                                        || response.content.status.contentEquals("PENDING")
                                        || response.content.status.contentEquals("INPROGRESS")) /*&& count == intervalsCount-1*/)){
                                // For INTERM / PENDING case

                                if (count == counter - 1) {
                                    mpgsReattempt = true
                                }
                                else if (count == counter){
                                    // FAILURE
                                    if (isLoadingCC) {
                                        ActivityUtil().loadingStop(loading_spinner, rl_progress, tv_progress, this)
                                        isLoadingCC = false
                                        fragmentCC?.enablePayNowBtn()

                                    }
                                    handler.removeCallbacks(runnable)
                                    val intent = Intent(
                                        this,
                                        Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback")
                                    )
                                    //feedBackCall.checkFeedback(true)
                                    obj.checkFeedback(true)
                                    intent.putExtra(CONFIGURATION, storeConfig)
                                    intent.putExtra(PAYMENTORDER, paymentOrder)
                                    intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                                    intent.putExtra(PAYMENTRESPONSE, response)
                                    intent.putExtra(TRANSACTIONSTATUS, "true")
                                    intent.putExtra(CCRESPONSESUCCESS, "true")
                                    setResult(200, intent)
                                    startActivity(intent)
                                    this.finish()
                                }
                            }
                            else {
                                if (count == counter){

                                    if (isLoadingCC) {
                                        ActivityUtil().loadingStop(loading_spinner, rl_progress, tv_progress, this)
                                        isLoadingCC = false
                                        fragmentCC?.enablePayNowBtn()

                                    }
                                    handler.removeCallbacks(runnable)
                                    val intent = Intent(
                                        this,
                                        Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback")
                                    )
                                    //feedBackCall.checkFeedback(true)
                                    obj.checkFeedback(true)
                                    intent.putExtra(CONFIGURATION, storeConfig)
                                    intent.putExtra(PAYMENTORDER, paymentOrder)
                                    intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                                    intent.putExtra(PAYMENTRESPONSE, response)
                                    intent.putExtra(TRANSACTIONSTATUS, "true")
                                    intent.putExtra(CCRESPONSESUCCESS, "true")
                                    setResult(200, intent)
                                    startActivity(intent)
                                    this.finish()
                                }
                            }


                    }

                }.execute(
                    POST,
                    storeConfig.baseUrl/*BASE_URL*/ + CC_TRANSACTION_STATUS_ENDPOINT,
                    json.toString()
                )

            } else {
                Toast.makeText(this, "Internet Not Available !", Toast.LENGTH_LONG).show()
                //finish()
            }
        }
    }

    private fun paymentDD( storeConfig: EPConfiguration, paymentOrder: EPPaymentOrder, allowedPaymentMethods: AllowedPaymentMethods ) {

        if(!ActivityUtil().isLoading) {

            var encrytedAccountNumber : String = ""
            var encrytedWalletAccountNumber : String = ""
            var encrytedCreditCard : String= ""
            var encrytedCnic : String = ""

            if (!paymentOrder.accountNumber.isNullOrEmpty()) {
                val accountNumber = Validation().encrypt(paymentOrder.accountNumber, storeConfig.secretKey)
                encrytedAccountNumber = accountNumber.replace("\n", "")
            }

            if (!paymentOrder.walletAccountNumber.isNullOrEmpty()) {
                val walletAccountNumber = Validation().encrypt(paymentOrder.walletAccountNumber, storeConfig.secretKey)
                encrytedWalletAccountNumber = walletAccountNumber.replace("\n", "")
            }

            if (!paymentOrder.creditCard.isNullOrEmpty()) {
                val creditCard = Validation().encrypt(paymentOrder.creditCard, storeConfig.secretKey)
                encrytedCreditCard = creditCard.replace("\n", "")
            }

            val cnic = Validation().encrypt(paymentOrder.cnic, storeConfig.secretKey)
            encrytedCnic =  cnic.replace("\n","")

            val json = JSONObject()
            json.put(STOREID, storeConfig.storeId)
            json.put(ORDERID, paymentOrder.orderId)
            json.put(AMOUNT, paymentOrder.orderAmount)
            json.put(TRANSACTIONTYPE, allowedPaymentMethods.code)

            json.put(CELLPHONE, paymentOrder.phone)
            json.put(EMAIL, paymentOrder.email)
            json.put(BANKCODE, paymentOrder.bank)
            json.put(CNIC, encrytedCnic)
            json.put(PAYMENTINSTRUMENT, paymentOrder.paymentMode)
            if (!encrytedAccountNumber.isNullOrEmpty()) {
                json.put(BANKACCOUNTNUMBER, encrytedAccountNumber)
            }
            if (!encrytedWalletAccountNumber.isNullOrEmpty()) {
                json.put(WALLETACCOUNTNUMBER, encrytedWalletAccountNumber)
            }
            if (!encrytedCreditCard.isNullOrEmpty()) {
                json.put(CARDNUMBER, encrytedCreditCard)
            } /*else {
                json.put(CARDNUMBER, 0)
            }*/


            var postData = ""

            if (paymentOrder.paymentMode.contentEquals("1390001")){
                if (!paymentOrder.orderAmount.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL + paymentOrder.orderAmount
                }
                if (!encrytedAccountNumber.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_DD_BANK_ACCOUNT + ENCRYPT_KEY_EQUAL + encrytedAccountNumber
                }
                if (!paymentOrder.cnic.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_DD_CNIC + ENCRYPT_KEY_EQUAL + paymentOrder.cnic
                }
                if (!paymentOrder.email.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_DD_EMAILADDRESS + ENCRYPT_KEY_EQUAL + paymentOrder.email
                }
                if (!paymentOrder.phone.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_MOBILENUM + ENCRYPT_KEY_EQUAL + paymentOrder.phone
                }
                if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL +paymentOrder.orderId.toString()
                }
                if (!allowedPaymentMethods.code.isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL +allowedPaymentMethods.code
                }
                if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
                }
            }
            else if (paymentOrder.paymentMode.contentEquals("1390002")){
                if (!paymentOrder.orderAmount.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL + paymentOrder.orderAmount
                }
                if (!paymentOrder.cnic.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_DD_CNIC + ENCRYPT_KEY_EQUAL + paymentOrder.cnic
                }
                if (!paymentOrder.email.toString().isNullOrEmpty()) {
                    postData +=  ENCRYPT_KEY_AMPERSAND + ENCRYPT_DD_EMAILADDRESS + ENCRYPT_KEY_EQUAL + paymentOrder.email
                }
                if (!paymentOrder.phone.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_MOBILENUM + ENCRYPT_KEY_EQUAL + paymentOrder.phone
                }
                if (!encrytedWalletAccountNumber.isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_DD_MOBILE_WALLET + ENCRYPT_KEY_EQUAL + encrytedWalletAccountNumber
                }
                if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL +paymentOrder.orderId.toString()
                }
                if (!allowedPaymentMethods.code.isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL +allowedPaymentMethods.code
                }
                if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
                }
            }
            else {
                if (!paymentOrder.orderAmount.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL + paymentOrder.orderAmount
                }
                if (!encrytedCreditCard.isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_CC_NUM + ENCRYPT_KEY_EQUAL + encrytedCreditCard
                }
                if (!paymentOrder.cnic.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_DD_CNIC + ENCRYPT_KEY_EQUAL + paymentOrder.cnic
                }
                if (!paymentOrder.email.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_DD_EMAILADDRESS + ENCRYPT_KEY_EQUAL + paymentOrder.email
                }
                if (!paymentOrder.phone.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_MOBILENUM + ENCRYPT_KEY_EQUAL + paymentOrder.phone
                }
                if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL +paymentOrder.orderId.toString()
                }
                if (!allowedPaymentMethods.code.isNullOrEmpty()){
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL +allowedPaymentMethods.code
                }
                if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                    postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
                }
            }

            val encryptedRequest = Validation().encrypt(postData.toString(),storeConfig.secretKey)
            json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))





            if (InternetHelper().isInternetConnected(this)) {
                ActivityUtil().loadingStarted(loading_spinner, rl_progress, tv_progress, "", this)
                isLoading = true
                HttpTask(this) {
                    ActivityUtil().loadingStop(loading_spinner, rl_progress, tv_progress, this)
                    isLoading = false
                    if (it == null) {
                        Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show()
                        return@HttpTask
                    } else {
                        var json = it.toString() // json value

                        if (json.contains("\"responseCode\":\"\"")){
                            json = json.replace("\"content\":\"\",", "").replace("\"responseCode\":\"\"", "\"responseCode\":\"-1\"")
                        }
                        val response = Gson().fromJson(json, PaymentBaseDDResponse::class.java)

                        when (response.ackMessage.responseCode.toString().contentEquals("0")) {
                            true -> {

                                val intent =
                                    Intent(this, Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentOTP"))
                                intent.putExtra(CONFIGURATION, storeConfig)
                                intent.putExtra(PAYMENTORDER, paymentOrder)
                                intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                                intent.putExtra(PAYMENTRESPONSE, response)
                                intent.putExtra(TRANSACTIONSTATUS, "true")
                                setResult(200, intent)
                                startActivity(intent)
                                this.finish()


                            }
                            false -> {
                                val intent = Intent(
                                    this,
                                    Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback")
                                )
                                intent.putExtra(CONFIGURATION, storeConfig)
                                intent.putExtra(PAYMENTORDER, paymentOrder)
                                intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                                intent.putExtra(PAYMENTRESPONSE, response)
                                intent.putExtra(RESPONSECODE, response.ackMessage.responseCode.toString())
                                intent.putExtra(RESPONSEMETHOD, "DD")
                                intent.putExtra(TRANSACTIONSTATUS, "true")
                                setResult(200, intent)
                                startActivity(intent)
                                this.finish()

                            }
                        }
                    }

                }.execute(
                    POST,
                    storeConfig.baseUrl/*BASE_URL*/ + DD_INITIATETRANSACTION_ENDPOINT,
                    json.toString()
                )

            } else {
                Toast.makeText(this, "Internet Not Available !", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun getCharityPackages() {
        val json = JSONObject()
        if(InternetHelper().isInternetConnected(this)) {

            HttpTask2(storeConfig.secretKey) {
                if (it == null) {
                    Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show()
                    return@HttpTask2
                } else {
                    val json = it.toString() // json value
                    val response = Gson().fromJson(json, DonationBaseResponse::class.java)

                    when (response.ackMessage.responseCode.toString().contentEquals("0")) {
                        true -> {
                            donationBaseResponse= response
                        }
                        false -> {
                            Toast.makeText(
                                this,
                                "Response " + response.ackMessage.responseDescription ,
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }
                }

            }.execute(
                GET,
                storeConfig.baseUrl/*BASE_URL*/+ MA_DONATIONS_ENDPOINT,
                json.toString()
            )

        }
        else{
            Toast.makeText(this, "Internet Not Available !", Toast.LENGTH_LONG).show()
        }
    }



    private fun setStoreDetails(storeConfig: EPConfiguration, paymentOrder: EPPaymentOrder) {
        val orderID = findViewById<TextView>(R.id.tv_orderId)
        orderID.text = paymentOrder.orderId

        val storeName = findViewById<TextView>(R.id.tv_storeName)
        storeName.text = storeConfig.storeName

        val orderAmount = findViewById<TextView>(R.id.tv_totalAmount)
        orderAmount.text = paymentOrder.orderAmount.toString()

    }
    private fun setHeadingTitle(title: String) {

        val toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        val mTitle = toolbar.findViewById(R.id.toolbar_title) as TextView
        mTitle.text = title
        mTitle.setTypeface(null, Typeface.BOLD)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainToolbar.setNavigationIcon(R.drawable.back)
        mainToolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                onBackPressed()
            }
        })

    }

    override fun onBackPressed() {


        if(!isLoading){
            super.onBackPressed()

        }
    }

    fun getCurrentDate(): String {

        var date = Date();
        val formatter = SimpleDateFormat("MM/dd/yyyy")
        val answer: String = formatter.format(date)
        return answer
    }

    override fun onDestroy() {
        super.onDestroy()

//        if (handler != null) {
//            handler.removeCallbacks(runnable)
//        }
    }

}