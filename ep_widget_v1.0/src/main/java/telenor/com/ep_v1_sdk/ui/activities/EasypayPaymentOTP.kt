package telenor.com.ep_v1_sdk.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.ll_top_view.*
import org.json.JSONObject
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.adapter.EPPaymentMethodAdapter
import telenor.com.ep_v1_sdk.config.*
import telenor.com.ep_v1_sdk.config.model.*
import telenor.com.ep_v1_sdk.rest.HttpTask
import telenor.com.ep_v1_sdk.util.ActivityUtil
import telenor.com.ep_v1_sdk.util.InternetHelper
import telenor.com.ep_v1_sdk.util.Validation
import java.util.*
import java.util.concurrent.TimeUnit


class EasypayPaymentOTP : AppCompatActivity(),TextWatcher  {


    lateinit var storeConfig: EPConfiguration
    lateinit var paymentOrder :EPPaymentOrder
    lateinit var paymnentCharity:EPPaymentCharityOrder
    lateinit var allowedPaymentMethods :AllowedPaymentMethods
    lateinit var response :PaymentBaseDDResponse

    var valid: Boolean = false

    // 60 seconds (1 minute)
    val minute:Long = 1000 * 31 // 1000 milliseconds = 1 second

    // 1 day 2 hours 35 minutes 50 seconds
    val millisInFuture:Long = minute

    // Count down interval 1 second
    val countDownInterval:Long = 1000

    var activityName: String = "OTPActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        //setting toolbar
        setHeadingTitle(resources.getString(R.string.easypaisa_wallet))

         storeConfig = intent.getSerializableExtra(CONFIGURATION) as EPConfiguration
         paymentOrder = intent.getSerializableExtra(PAYMENTORDER) as EPPaymentOrder
        allowedPaymentMethods = intent.getSerializableExtra(PAYMENTTYPE) as AllowedPaymentMethods
        response = intent.getSerializableExtra(PAYMENTRESPONSE) as PaymentBaseDDResponse

        tv_resendOTP.alpha = 0.5f
        tv_resendOTP.isEnabled = false

        timer(millisInFuture, countDownInterval).start()


        editTextone.addTextChangedListener(this)
        editTexttwo.addTextChangedListener(this)
        editTextthree.addTextChangedListener(this)
        editTextfour.addTextChangedListener(this)


        setStoreDetails(storeConfig,paymentOrder)

        if (valid){
            buttonPay_dd.alpha = 1f
        }
        else{
            buttonPay_dd.alpha = 0.5f
        }

        buttonPay_dd.setOnClickListener {

            if (valid) {
                var otp =
                    editTextone.text.toString() + editTexttwo.text.toString() + editTextthree.text.toString() + editTextfour.text.toString()

                OTPVerify(otp)
            }
            else {
                Toast.makeText(this, "Please enter code", Toast.LENGTH_LONG)
                    .show()
            }

        }

        tv_resendOTP.setOnClickListener {


            OTPResend()

        }

    }

    private fun OTPResend(){

        if (!ActivityUtil().isLoading) {

            val paymentDTO = response.content.paymentDto

            var encrytedCCNumber : String = ""
            var encrytedBankAccountNumber : String = ""
            var encrytedWalletAccountNumber : String= ""

            if (!paymentDTO.ccNumber.isNullOrEmpty()) {
                val ccNumber = Validation().encrypt(paymentDTO.ccNumber, storeConfig.secretKey)
                encrytedCCNumber = ccNumber.replace("\n", "")
            }

            if (!paymentDTO.bankAccountNumber.isNullOrEmpty()) {
                val bankAccountNumber = Validation().encrypt(paymentDTO.bankAccountNumber, storeConfig.secretKey)
                encrytedBankAccountNumber = bankAccountNumber.replace("\n", "")
            }

            if (!paymentDTO.walletAccountNumber.isNullOrEmpty()) {
                val walletAccountNumber = Validation().encrypt(paymentDTO.walletAccountNumber, storeConfig.secretKey)
                encrytedWalletAccountNumber = walletAccountNumber.replace("\n", "")
            }

            val cnic = Validation().encrypt(paymentDTO.cnic, storeConfig.secretKey)
            val encrytedCnic =  cnic.replace("\n","")

            val json = JSONObject()
            json.put(TRANSACTIONID, paymentDTO.transactionId)
            json.put(ACCESSTOKEN, paymentDTO.accessToken)
            json.put(PAYMENTINSTRUMENT, paymentDTO.paymentMode)
            if (!encrytedCCNumber.isNullOrEmpty()) {
                json.put(CARDNUMBER, encrytedCCNumber)
            }
            if (!encrytedBankAccountNumber.isNullOrEmpty()) {
                json.put(BANKACCOUNTNUMBER, encrytedBankAccountNumber)
            }
            if (!encrytedWalletAccountNumber.isNullOrEmpty()) {
                json.put(WALLETACCOUNTNUMBER, encrytedWalletAccountNumber)
            }
            json.put(AMOUNT, paymentDTO.amount)
            //json.put(OTP, "")
            json.put(STOREID, storeConfig.storeId)
            json.put(EMAIL, paymentOrder.email)
            json.put(ORDERID, paymentOrder.orderId)
            json.put(CELLPHONE, paymentOrder.phone)
            json.put(TRANSACTIONTYPE, paymentDTO.transactionType)
            json.put(BANKCODE, paymentDTO.bankCode)
            json.put(CNIC, encrytedCnic)
            json.put(TRANSACTIONDATETIME, paymentDTO.transactionDateTimeStr)

            //
            var postData = ""

            postData += ACCESSTOKEN + ENCRYPT_KEY_EQUAL + paymentDTO.accessToken

            if (!paymentDTO.amount.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL + paymentDTO.amount
            }

            // Bank Account was required here only for now
            if (!encrytedCCNumber.isNullOrEmpty()){
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_CC_NUM + ENCRYPT_KEY_EQUAL + encrytedCCNumber
            }

            if (!encrytedBankAccountNumber.isNullOrEmpty()){
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_DD_BANK_ACCOUNT + ENCRYPT_KEY_EQUAL + encrytedBankAccountNumber
            }

            if (!encrytedWalletAccountNumber.isNullOrEmpty()){
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_DD_MOBILE_WALLET + ENCRYPT_KEY_EQUAL + encrytedWalletAccountNumber
            }
            // Bank Account was required here only for now

            postData += ENCRYPT_KEY_AMPERSAND + CNIC + ENCRYPT_KEY_EQUAL + encrytedCnic

            if (!paymentOrder.email.toString().isNullOrEmpty()){
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_EMAILADDRESS + ENCRYPT_KEY_EQUAL + paymentOrder.email
            }

            if (!paymentOrder.phone.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_MOBILENUM + ENCRYPT_KEY_EQUAL + paymentOrder.phone
            }

            if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL +paymentOrder.orderId
            }

            if (!allowedPaymentMethods.code.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL  + allowedPaymentMethods.code
            }

            if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
            }

            val encryptedRequest = Validation().encrypt(postData.toString(),storeConfig.secretKey)
            json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))

            //

            if (InternetHelper().isInternetConnected(this)) {
                ActivityUtil().loadingStarted(loading_spinner, this)
                tv_resendOTP.alpha = 0.5f
                tv_resendOTP.isEnabled = false
                timer(millisInFuture, countDownInterval).start()
                HttpTask(this) {

                    ActivityUtil().loadingStop(loading_spinner, this)
                    if (it == null) {
                        Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show()
                        finish()
                        return@HttpTask
                    } else {
                        val json = it.toString() // json value
                        val response = Gson().fromJson(json, PaymentOTPResponse::class.java)

                        when (response.ackMessage.responseCode.toString().contentEquals("0")) {
                            true -> {

                                Toast.makeText(this, response.ackMessage.responseDescription, Toast.LENGTH_LONG).show()

                            }
                            false -> {
                                Toast.makeText(this, response.ackMessage.responseDescription, Toast.LENGTH_LONG).show()

                            }
                        }
                    }

                }.execute(
                    POST,
                    storeConfig.baseUrl/*BASE_URL*/ + RESEND_OTP_ENDPOINT,
                    json.toString()
                )

            } else {
                Toast.makeText(this, "Internet Not Available !", Toast.LENGTH_LONG).show()
                //finish()
            }
        }
    }

    private fun OTPVerify(otp:String) {

        if(!ActivityUtil().isLoading) {
            val paymentDTO = response.content.paymentDto

            var encrytedCCNumber : String = ""
            var encrytedBankAccountNumber : String = ""
            var encrytedWalletAccountNumber : String= ""

            if (!paymentDTO.ccNumber.isNullOrEmpty()) {
                val ccNumber = Validation().encrypt(paymentDTO.ccNumber, storeConfig.secretKey)
                encrytedCCNumber = ccNumber.replace("\n", "")
            }

            if (!paymentDTO.bankAccountNumber.isNullOrEmpty()) {
                val bankAccountNumber = Validation().encrypt(paymentDTO.bankAccountNumber, storeConfig.secretKey)
                encrytedBankAccountNumber = bankAccountNumber.replace("\n", "")
            }

            if (!paymentDTO.walletAccountNumber.isNullOrEmpty()) {
                val walletAccountNumber = Validation().encrypt(paymentDTO.walletAccountNumber, storeConfig.secretKey)
                encrytedWalletAccountNumber = walletAccountNumber.replace("\n", "")
            }

            val otpp = Validation().encrypt(otp, storeConfig.secretKey)
            val  encrytedOtp =  otpp.replace("\n","")

            val cnic = Validation().encrypt(paymentDTO.cnic, storeConfig.secretKey)
            val encrytedCnic =  cnic.replace("\n","")

            val json = JSONObject()
            json.put(TRANSACTIONID, paymentDTO.transactionId)
            json.put(ACCESSTOKEN, paymentDTO.accessToken)

            json.put(PAYMENTINSTRUMENT, paymentDTO.paymentMode)
            if (!encrytedCCNumber.isNullOrEmpty()) {
                json.put(CARDNUMBER, encrytedCCNumber)
            }
            if (!encrytedBankAccountNumber.isNullOrEmpty()) {
                json.put(BANKACCOUNTNUMBER, encrytedBankAccountNumber)
            }
            if (!encrytedWalletAccountNumber.isNullOrEmpty()) {
                json.put(WALLETACCOUNTNUMBER, encrytedWalletAccountNumber)
            }
            json.put(AMOUNT, paymentDTO.amount)
            json.put(OTP, encrytedOtp)
            json.put(STOREID, storeConfig.storeId)
            json.put(EMAIL, paymentOrder.email)
            json.put(ORDERID, paymentOrder.orderId)
            json.put(CELLPHONE, paymentOrder.phone)
            json.put(TRANSACTIONTYPE, paymentDTO.transactionType)
            json.put(BANKCODE, paymentDTO.bankCode)
            json.put(CNIC, encrytedCnic)
            json.put(ORDERREFNUMBER, paymentOrder.orderId)
            json.put(TRANSACTIONREFNUMBER, "")
            json.put(TRANSACTIONDATETIME, paymentDTO.transactionDateTimeStr)

            //
            var postData = ""

            postData += ACCESSTOKEN + ENCRYPT_KEY_EQUAL + paymentDTO.accessToken

            if (!paymentDTO.amount.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL + paymentDTO.amount
            }

            // Bank Account was required here only for now
            if (!encrytedCCNumber.isNullOrEmpty()){
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_CC_NUM + ENCRYPT_KEY_EQUAL + encrytedCCNumber
            }

            if (!encrytedBankAccountNumber.isNullOrEmpty()){
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_DD_BANK_ACCOUNT + ENCRYPT_KEY_EQUAL + encrytedBankAccountNumber
            }

            if (!encrytedWalletAccountNumber.isNullOrEmpty()){
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_DD_MOBILE_WALLET + ENCRYPT_KEY_EQUAL + encrytedWalletAccountNumber
            }
            // Bank Account was required here only for now

            postData += ENCRYPT_KEY_AMPERSAND + CNIC + ENCRYPT_KEY_EQUAL + encrytedCnic

            if (!paymentOrder.email.toString().isNullOrEmpty()){
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_EMAILADDRESS + ENCRYPT_KEY_EQUAL + paymentOrder.email
            }

            if (!paymentOrder.phone.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_MOBILENUM + ENCRYPT_KEY_EQUAL + paymentOrder.phone
            }

            if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL +paymentOrder.orderId
            }

            postData += ENCRYPT_KEY_AMPERSAND + OTP + ENCRYPT_KEY_EQUAL + encrytedOtp


            if (!allowedPaymentMethods.code.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL  + allowedPaymentMethods.code
            }
            if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
            }


            val encryptedRequest = Validation().encrypt(postData.toString(),storeConfig.secretKey)
            json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))

            //

            if (InternetHelper().isInternetConnected(this)) {
                ActivityUtil().loadingStarted(loading_spinner, this)
                HttpTask(this) {

                    ActivityUtil().loadingStop(loading_spinner, this)
                    if (it == null) {
                        Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show()
                        finish()
                        return@HttpTask
                    } else {
                        val json = it.toString() // json value
                        val response = Gson().fromJson(json, PaymentDDResponse::class.java)

                        when (response.ackMessage.responseCode.toString().contentEquals("0")) {
                            true -> {
                                val intent = Intent(
                                    this,
                                    Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback")
                                )
                                intent.putExtra(CONFIGURATION, storeConfig)
                                intent.putExtra(PAYMENTORDER, paymentOrder)
                                intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                                intent.putExtra(PAYMENTRESPONSE, response)
                                intent.putExtra(RESPONSECODE, response.ackMessage.responseCode.toString())
                                intent.putExtra(RESPONSEMETHOD, "OTP")
                                intent.putExtra(TRANSACTIONSTATUS, "true")
                                setResult(200, intent)
                                startActivity(intent)
                                finish()
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
                                intent.putExtra(RESPONSEMETHOD, "OTP")
                                intent.putExtra(TRANSACTIONSTATUS, "false")
                                setResult(200, intent)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }

                }.execute(
                    POST,
                    storeConfig.baseUrl/*BASE_URL*/ + SUBMIT_OTP_ENDPOINT,
                    json.toString()
                )

            } else {
                Toast.makeText(this, "Internet Not Available !", Toast.LENGTH_LONG).show()
                //finish()
            }
        }
    }


    override fun afterTextChanged(editable: Editable?) {

        if (editable!!.length == 1) {
            if (editTextone.length() == 1) {
                editTexttwo.requestFocus();
                valid = false
            }

            if (editTexttwo.length() == 1) {
                editTextthree.requestFocus();
                valid = false
            }
            if (editTextthree.length() == 1) {
                editTextfour.requestFocus();
                valid = false
            }
            if (editTextfour.length() == 1){
                if ((editTextone.length() == 1) && (editTexttwo.length() == 1) && (editTextthree.length() == 1))
                    valid = true;
                else
                    valid = false
            }
            else{
                valid = false;
            }

        } else if (editable.isEmpty()) {
            if (editTextfour.length() == 0) {
                editTextthree.requestFocus()
                valid = false
            }
            if (editTextthree.length() == 0) {
                editTexttwo.requestFocus()
                valid = false
            }
            if (editTexttwo.length() == 0) {
                editTextone.requestFocus()
                valid = false
            }
            if (editTextone.length() == 0){
                valid = false
            }
        }

        if (valid){
            buttonPay_dd.alpha = 1f
        }
        else{
            buttonPay_dd.alpha = 0.5f
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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

        val toolbar = findViewById<Toolbar>(R.id.mainToolbar_payment_otp)
        val mTitle = toolbar.findViewById(R.id.toolbar_title_payment_otp) as TextView
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
                val timeRemaining = timeString(millisUntilFinished)
                tv_timer.text = timeRemaining.toString()
            }

            override fun onFinish() {
                tv_timer.text = timeString(0)
                tv_resendOTP.isEnabled = true
                tv_resendOTP.alpha = 1f


            }
        }
    }

    // Method to get days hours minutes seconds from milliseconds
    private fun timeString(millisUntilFinished:Long):String{
        var millisUntilFinished:Long = millisUntilFinished

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

        // Format the string
        return String.format(
            Locale.getDefault(),
            "%2d sec",
            seconds
        )
    }
}