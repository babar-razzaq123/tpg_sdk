package telenor.com.ep_v1_sdk.ui.activities

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_payment_method.*
import org.json.JSONObject
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.adapter.EPPaymentMethodAdapter
import telenor.com.ep_v1_sdk.config.*
import telenor.com.ep_v1_sdk.config.model.AllowedPaymentMethods
import telenor.com.ep_v1_sdk.config.model.EPConfiguration
import telenor.com.ep_v1_sdk.config.model.EPPaymentOrder
import telenor.com.ep_v1_sdk.config.model.PaymentMethodBaseResponse
import telenor.com.ep_v1_sdk.rest.HttpTask
import telenor.com.ep_v1_sdk.util.ActivityUtil
import telenor.com.ep_v1_sdk.util.InternetHelper
import telenor.com.ep_v1_sdk.util.Validation
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EasypayPaymentMethod : AppCompatActivity(){
    private lateinit var storeConfig: EPConfiguration
    private lateinit var paymentOrder :EPPaymentOrder

    private lateinit var epPaymentMethodAdapter: EPPaymentMethodAdapter
    private lateinit var sharedPrefDataSource: EPSharedPrefDataSource

    private var allowedPayments : ArrayList<AllowedPaymentMethods> = ArrayList()

    lateinit var timeRemaining : String
    var timeRemainingInMillis : Long = 0
    // 60 seconds (1 minute)
    val minute:Long = 1000 * 601 // 1000 * 601 = 10 minutes ||| 1000 * 301 = 5 minutes

    // 1 day 2 hours 35 minutes 50 seconds
    var millisInFuture:Long = 0 //minute

    // Count down interval 1 second
    val countDownInterval:Long = 1000 // 1000 milliseconds = 1 second

    lateinit var toolbarTimer : TextView

    val totalTime:Long = 1000 * 601 // = 10 minutes
    var timeStampService: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)
        //setting toolbar
        setHeadingTitle(resources.getString(R.string.easypaisa_wallet))

        storeConfig = intent.getSerializableExtra(CONFIGURATION) as EPConfiguration
        paymentOrder = intent.getSerializableExtra(PAYMENTORDER) as EPPaymentOrder


        getPaymentMethods(storeConfig,paymentOrder)

        setStoreDetails(storeConfig,paymentOrder)

        registerReceiver(broadcastReceiver, IntentFilter("finish_activity"));
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

        val toolbar = findViewById<Toolbar>(R.id.mainToolbar_payment_method)
        val mTitle = toolbar.findViewById(R.id.toolbar_title__payment_method) as TextView
        toolbarTimer = toolbar.findViewById(R.id.toolbar_timer__payment_method) as TextView
        mTitle.text = title
        mTitle.setTypeface(null, Typeface.BOLD)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.back)
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                onBackPressed()
            }
        })

    }


    private fun timer(millisInFuture:Long,countDownInterval:Long): CountDownTimer {
        return object: CountDownTimer(millisInFuture,countDownInterval){
            override fun onTick(millisUntilFinished: Long){
                timeRemainingInMillis = millisUntilFinished
                timeRemaining = timeString(millisUntilFinished)
                toolbarTimer.text = timeRemaining.toString()
            }

            override fun onFinish() {
                toolbarTimer.text = timeString(0)
                showTimeExpiredDialog()
                //Toast.makeText(applicationContext, "Time Expired", Toast.LENGTH_LONG).show()

            }
        }
    }

    // Method to get days hours minutes seconds from milliseconds
    private fun timeString(millisUntilFinished:Long):String{
        var millisUntilFinished:Long = millisUntilFinished

        val second = millisUntilFinished / 1000 % 60
        val minutes = millisUntilFinished / (1000 * 60) % 60

        return String.format("%02d", minutes) + ":" + String.format("%02d", second)

    }

    private fun showTimeExpiredDialog(){

        // Initialize a new instance of
        val builder = AlertDialog.Builder(this)

        // Set the alert dialog title
        builder.setTitle("Session time expired.")
        builder.setCancelable(false)

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Ok"){dialog, which ->

            //dialog.dismiss()
            this.finish()

        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        if (!isFinishing) {
            dialog.show()
        }

    }


    fun getCurrentTimeStamp(): String {
        val timeStamp: String = SimpleDateFormat("dd-MM-yyyy'T'hh:mm:ss").format(Date())
        return timeStamp
    }

    private fun getPaymentMethods( storeConfig: EPConfiguration,  paymentOrder: EPPaymentOrder ) {

        if(!ActivityUtil().isLoading){
        if(InternetHelper().isInternetConnected(this)) {
            ActivityUtil().loadingStarted(loading_spinner,this)

        val json = JSONObject()
            if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                json.put(STOREID, storeConfig.storeId)
            }
            if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
                json.put(ORDERID, paymentOrder.orderId)
            }
            if (!paymentOrder.orderAmount.toString().isNullOrEmpty()) {
                json.put(TRANSACTIONAMOUNT, paymentOrder.orderAmount)
            }
            if (!paymentOrder.phone.toString().isNullOrEmpty()) {
                json.put(MOBILEACCOUTNUMBER, paymentOrder.phone)
            }
            if (!paymentOrder.email.toString().isNullOrEmpty()) {
                json.put(EMAILADDRESS, paymentOrder.email)
            }
            if (!storeConfig.bankIdentifier.toString().isNullOrEmpty()) {
                json.put(BANKIDNUMBER, storeConfig.bankIdentifier)
            }
            if (!storeConfig.expiryToken.isNullOrEmpty()) {
                var expiryToken = storeConfig.expiryToken
                var d1 =expiryToken.replace("-","")
                var d2 = d1.replace(":","")
                json.put(TOKENEXPIRY, d2)
            }

            json.put(TRANSACTIONTYPE, VALUE_INITIAL_REQUEST)
//            json.put("timeStamp", getCurrentTimeStamp())


//            var postData = ""
//            if (!paymentOrder.orderAmount.toString().isNullOrEmpty()) {
//                postData += ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL + paymentOrder.orderAmount
//            }
//            if (!storeConfig.bankIdentifier.toString().isNullOrEmpty()){
//                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_BANKIDENTIFICATIONNUM + ENCRYPT_KEY_EQUAL + storeConfig.bankIdentifier
//            }
//            if (!paymentOrder.email.toString().isNullOrEmpty()){
//                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_EMAILADDRESS + ENCRYPT_KEY_EQUAL + paymentOrder.email
//            }
//            if (!storeConfig.expiryToken.isNullOrEmpty()) {
//                var expiryToken = storeConfig.expiryToken
//                var d1 =expiryToken.replace("-","")
//                var d2 = d1.replace(":","")
//                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_EXPIRYDATE + ENCRYPT_KEY_EQUAL + d2
//            }
//            if (!paymentOrder.phone.toString().isNullOrEmpty()) {
//                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_MOBILENUM + ENCRYPT_KEY_EQUAL + paymentOrder.phone.toString()
//            }
//            if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
//                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL +paymentOrder.orderId.toString()
//            }
//            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL + VALUE_INITIAL_REQUEST
//            if(!storeConfig.storeId.toString().isNullOrEmpty()) {
//                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
//            }
//            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPTEDREQUEST + ENCRYPT_KEY_EQUAL + storeConfig.secretKey

//            val encryptedRequest = Validation().encrypt(postData.toString(),storeConfig.secretKey)
//            json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))
            json.put(ENCRYPTEDREQUEST,  storeConfig.secretKey)


            HttpTask(this)  {

                ActivityUtil().loadingStop(loading_spinner,this)
                if (it == null) {
                    Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show()
                    finish()
                    return@HttpTask
                } else {
                    val json = it.toString() // json value
                    val response = Gson().fromJson(json, PaymentMethodBaseResponse::class.java)

                    when (response.ackMessage.responseCode.toString().contentEquals("0")) {
                        true -> {
                            sharedPrefDataSource=EPSharedPrefDataSource(this)
                            val storeConfig = sharedPrefDataSource.getEasyPayConfig()

                            if (storeConfig.specificPaymentMethod == null) {
                                if (!response!!.content!!.hashKey.isNullOrEmpty()) {
                                    val decryptHash: String = Validation().decryptHashKey(
                                        response.content.hashKey,
                                        paymentOrder.orderId,
                                        storeConfig.storeId
                                    )
                                    //storeConfig.secretKey = response.content.hashKey
                                    storeConfig.secretKey = decryptHash
                                    sharedPrefDataSource.setEasyPayConfig(storeConfig)
                                }
                                // trying timer
                                if (!response.content!!.timeStamp.isNullOrEmpty()){
                                    val timeStampDate = getTimeStampDate(response.content.timeStamp)
                                    val currentDate = Calendar.getInstance().time
                                    if (timeStampDate == null){
                                        Toast.makeText(this, "Invalid timestamp", Toast.LENGTH_SHORT).show()
                                        finish()
                                        return@HttpTask
                                    }
                                    val diffDate = printDifference(currentDate, timeStampDate)
                                    if (diffDate > 0) {
                                        timeStampService =
                                            getTimeInMilliFromDate(response.content.timeStamp)
                                        millisInFuture = totalTime -
                                            (currentDate.time - timeStampService)
                                        timer(millisInFuture, countDownInterval).start()
                                    }
                                    else {
                                        showTimeExpiredDialog()
                                    }
                                }
                                else {
                                    showTimeExpiredDialog()
                                }
                                // trying timer
                                //timer(millisInFuture, countDownInterval).start()

                            for (allowedPaymentMethods in response.content.allowedPaymentMethods) {

                                when (allowedPaymentMethods.code) {

                                    "MA" -> {

                                        allowedPaymentMethods.icon = R.drawable.ic_ma_inactive
                                        allowedPayments.add(allowedPaymentMethods)
                                    }
                                    "OTC" -> {

                                        allowedPaymentMethods.icon = R.drawable.ic_otc_inactive
                                        allowedPayments.add(allowedPaymentMethods)
                                    }
                                    "CC" -> {
                                        allowedPaymentMethods.icon = R.drawable.ic_cc_inactive
//                                        allowedPaymentMethods.description = getString(R.string.visa_master_card_txt)
                                        allowedPayments.add(allowedPaymentMethods)
                                    }
                                    "DD" -> {
                                        allowedPaymentMethods.icon = R.drawable.ic_dd_inactive
                                        allowedPayments.add(allowedPaymentMethods)
                                    }

                                }

                            }

                            epPaymentMethodAdapter= EPPaymentMethodAdapter {

                                val intent = Intent(this, Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentForm"))
                                intent.putExtra(CONFIGURATION, storeConfig)
                                intent.putExtra(PAYMENTORDER, paymentOrder)
                                intent.putExtra(PAYMENTTYPE, it )
                                intent.putExtra(DIRECTDEBITBANK, response.content.directDebitBank )
                                intent.putExtra(EXPIRYMONTH, response.content.expiryMonths )
                                intent.putExtra(EXPIRYYEAR, response.content.expiryYears )
                                intent.putExtra(MERCHANTACCOUNTID,response.content.merchantAccountId)
                                intent.putExtra("timer_time", timeRemainingInMillis)
                                intent.putExtra("hash_key", response.content.hashKey)
                                startActivityForResult(intent,200)
                            }

                            epPaymentMethodAdapter.addItems(allowedPayments)

                            paymentMethods.adapter =epPaymentMethodAdapter

                            }
                            else {

                                //
                                if (!response!!.content!!.hashKey.isNullOrEmpty()) {
                                    val decryptHash: String = Validation().decryptHashKey(
                                        response.content.hashKey,
                                        paymentOrder.orderId,
                                        storeConfig.storeId
                                    )
                                    //storeConfig.secretKey = response.content.hashKey
                                    storeConfig.secretKey = decryptHash
                                    sharedPrefDataSource.setEasyPayConfig(storeConfig)
                                }
                                // trying timer
                                if (!response.content!!.timeStamp.isNullOrEmpty()){
                                    val timeStampDate = getTimeStampDate(response.content.timeStamp)
                                    val currentDate = Calendar.getInstance().time
                                    if (timeStampDate == null){
                                        Toast.makeText(this, "Invalid timestamp", Toast.LENGTH_SHORT).show()
                                        finish()
                                        return@HttpTask
                                    }
                                    val diffDate = printDifference(currentDate, timeStampDate)
                                    if (diffDate > 0) {
                                        timeStampService =
                                            getTimeInMilliFromDate(response.content.timeStamp)
                                        millisInFuture = totalTime -
                                                (currentDate.time - timeStampService)
                                        //timer(millisInFuture, countDownInterval).start()
                                    }
                                    else {
                                        showTimeExpiredDialog()
                                    }
                                }
                                else {
                                    showTimeExpiredDialog()
                                }
                                // trying timer
                                //

                                // list filter approach start new way
                                var list : List<AllowedPaymentMethods> = response.content.allowedPaymentMethods.filter { obj ->
                                    obj.code == storeConfig.specificPaymentMethod
                                }

                                if (list.isEmpty()){
                                    Toast.makeText(
                                            this,
                                            "Selected payment method is not allowed",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        finish()
                                }
                                else {

                                    val intent = Intent(this, Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentForm"))
                                    intent.putExtra(CONFIGURATION, storeConfig)
                                    intent.putExtra(PAYMENTORDER, paymentOrder)
                                    intent.putExtra(PAYMENTTYPE, list.get(0) )
                                    intent.putExtra(DIRECTDEBITBANK, response.content.directDebitBank )
                                    intent.putExtra(EXPIRYMONTH, response.content.expiryMonths )
                                    intent.putExtra(EXPIRYYEAR, response.content.expiryYears )
                                    intent.putExtra(MERCHANTACCOUNTID,response.content.merchantAccountId)
                                    intent.putExtra("timer_time", millisInFuture)
                                    intent.putExtra("hash_key", response.content.hashKey)
                                    //startActivityForResult(intent,200)
                                    startActivity(intent)
                                    finish()
                                    //break
                                }
                                // list filter approach end new way
                            }

                        }
                        false -> {
                            Toast.makeText(
                                this,
                                " " + response.ackMessage.responseDescription.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }
                }

            }.execute(
                POST,
                storeConfig.baseUrl/*BASE_URL*/+ INITIATEPAYMENTMETHODS_ENDPOINT,
                json.toString()
            )

        }
        else{
            Toast.makeText(this, "Internet Not Available !",Toast.LENGTH_LONG).show()
            finish()
        }}
    }




    // Call Back method  to get the Message form other Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 200) {
            if (data != null) {
                if (data!!.getStringExtra(TRANSACTIONSTATUS)!!.contentEquals("true")) {
                    finish()
                }
            }
        }
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(arg0: Context?, intent: Intent) {
            val action = intent.action
            if (action == "finish_activity") {
                finish()
                // DO WHATEVER YOU WANT.
            }
        }
    }

    fun getTimeInMilliFromDate(dateTime: String?): Long {
        return try {
            val sdf =
                SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
            sdf.parse(dateTime).time
        } catch (ex: Exception) { //ex.printStackTrace();
            0
        }
    }

    fun getTimeStampDate(dateTime: String?): Date? {
        val sdf =
            SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
        try {
            val date = sdf.parse(dateTime)
            return date
        }
        catch (ex: Exception){
            return null
        }

    }


    fun printDifference(startDate: Date, endDate: Date) : Long{ //milliseconds
        val different = startDate.time - endDate.time
        println("different : $different")
        return different
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(broadcastReceiver)
    }

}