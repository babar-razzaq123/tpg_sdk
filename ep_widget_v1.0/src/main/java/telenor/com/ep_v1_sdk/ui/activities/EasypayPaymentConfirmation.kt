package telenor.com.ep_v1_sdk.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_payment_confirmation.*
import kotlinx.android.synthetic.main.ll_top_view.*
import org.json.JSONObject
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.config.*
import telenor.com.ep_v1_sdk.config.model.*
import telenor.com.ep_v1_sdk.rest.HttpTask
import telenor.com.ep_v1_sdk.ui.EasypayCall
import telenor.com.ep_v1_sdk.ui.EasypayDonationCall
import telenor.com.ep_v1_sdk.ui.fragment.EasypayPaymentVerification
import telenor.com.ep_v1_sdk.ui.fragment.EasypayPaymentVerificationStatus
import telenor.com.ep_v1_sdk.util.ActivityUtil
import telenor.com.ep_v1_sdk.util.InternetHelper
import telenor.com.ep_v1_sdk.util.Validation
import java.util.*




class EasypayPaymentConfirmation : AppCompatActivity(), EasypayDonationCall {
    lateinit var storeConfig: EPConfiguration
    lateinit var paymentOrder :EPPaymentOrder
    lateinit var paymnentCharity:EPPaymentCharityOrder
    lateinit var allowedPaymentMethods :AllowedPaymentMethods
    lateinit var response :PaymentBaseResponse
    lateinit var donationCharityResponse:DonationBaseResponse

    var isLoading:Boolean=false
    var donationStatus:Boolean=false

    lateinit var orderId: String

    override fun paymentMethod(storeConfig: EPConfiguration, paymentOrder: EPPaymentOrder, method: String) {
        when(method){
            "OK"->{
                paymentMA(storeConfig,paymentOrder,allowedPaymentMethods)
            }
            "SUMMARY"->{
                val intent = Intent(this, Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback"))
                intent.putExtra(CONFIGURATION, storeConfig)
                intent.putExtra(PAYMENTORDER, paymentOrder)
                intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                intent.putExtra(PAYMENTRESPONSE,response)
                intent.putExtra(CHARITYRESPONSE,donationCharityResponse)
                intent.putExtra(CHARITYORDER, paymnentCharity)
                intent.putExtra(ORDERID, orderId)
                intent.putExtra(CHARITYRESULT, "true")
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                this.finish()
            }
            "CANCEL"->{
                finish()
            }
        }
    }


    var activityName: String = "DonationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_confirmation)
        //setting toolbar
        setHeadingTitle(resources.getString(R.string.easypaisa_wallet))

         storeConfig = intent.getSerializableExtra(CONFIGURATION) as EPConfiguration
         paymentOrder = intent.getSerializableExtra(PAYMENTORDER) as EPPaymentOrder
        paymnentCharity  = intent.getSerializableExtra(CHARITYORDER) as EPPaymentCharityOrder
        allowedPaymentMethods = intent.getSerializableExtra(PAYMENTTYPE) as AllowedPaymentMethods
        orderId = intent.getStringExtra(ORDERID) as String
        donationCharityResponse = intent.getSerializableExtra(CHARITYRESPONSE) as DonationBaseResponse
        initConfirmation()
        setStoreDetails(storeConfig,paymentOrder)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

    }

    private fun initConfirmation() {

        var fragment: Fragment? = supportFragmentManager.findFragmentByTag(activityName)
        fragment = EasypayPaymentVerification.newInstance(this)
        fragment.arguments = intent.extras
        ActivityUtil().replaceFragment(this,fragment, false, R.id.frame_content_confirmation, activityName)
    }

    private fun initStatus(response: PaymentBaseResponse) {

        var fragment: Fragment? = supportFragmentManager.findFragmentByTag(activityName)
        fragment = EasypayPaymentVerificationStatus.newInstance(this)
        intent.putExtra(DONATIONRESPONSE,response)
        fragment.arguments = intent.extras
        ActivityUtil().replaceFragment(this,fragment, false, R.id.frame_content_confirmation, activityName)
    }

    private fun paymentMA(storeConfig: EPConfiguration, paymentOrder: EPPaymentOrder, allowedPaymentMethods: AllowedPaymentMethods) {

        if(!ActivityUtil().isLoading){

//        val now = Calendar.getInstance()
//        val year = now.get(Calendar.YEAR)
//        val month = now.get(Calendar.MONTH) + 1  // Note: zero based!
//        val monthUpdated =(if (month < 10) "0$month" else month)
//        val day = now.get(Calendar.DAY_OF_MONTH)
//        val dayUpdated =(if (day < 10) "0$day" else day)
//        val hour = now.get(Calendar.HOUR_OF_DAY)
//        val hourUpdated =(if (hour < 10) "0$hour" else hour)
//        val minute = now.get(Calendar.MINUTE)
//        val minuteUpdated =(if (minute < 10) "0$minute" else minute)
//        val second = now.get(Calendar.SECOND)
//        val secondUpdated =(if (second < 10) "0$second" else second)
//
//
//        val sumDate = year.toString()+ month.toString()+day.toString()+hour.toString()+minute.toString()+second.toString()
//
//        val orderId = "ED"+sumDate
        val json = JSONObject()
        json.put(STOREID, paymnentCharity.charityStoreId.toString())
        json.put(ORDERID, orderId)
        json.put(TRANSACTIONAMOUNT, Validation().getAmount(paymnentCharity.orderAmount))
        json.put(TRANSACTIONTYPE, allowedPaymentMethods.code)
        json.put(MOBILEACCOUTNUMBER, paymentOrder.phone)
        json.put(EMAILADDRESS, paymentOrder.email)

        var postData = ""
        if (!paymnentCharity.orderAmount.toString().isNullOrEmpty()) {
            postData += "amount=" + Validation().getAmount(paymnentCharity.orderAmount)
        }
        if (!paymentOrder.email.toString().isNullOrEmpty()){
            postData += "&emailAddress=" + paymentOrder.email
        }

        if (!paymentOrder.phone.toString().isNullOrEmpty()) {
            postData += "&mobileNum=" + paymentOrder.phone.toString()
        }

        if (!orderId.toString().isNullOrEmpty()) {
            postData += "&orderRefNum=" +orderId.toString()
        }

        if (!allowedPaymentMethods.code.toString().isNullOrEmpty()) {
            postData += "&paymentMethod=" + allowedPaymentMethods.code.toString()
        }
        if(!paymnentCharity.charityStoreId.toString().isNullOrEmpty()) {
            postData += "&storeId=" + paymnentCharity.charityStoreId.toString()
        }


        val encryptedRequest = Validation().encrypt(postData.toString(),paymnentCharity.secretKey)
        json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))

        if(InternetHelper().isInternetConnected(this)) {
            ActivityUtil().loadingStarted(loading_spinner,rl_progress,tv_progress,"",this)
            isLoading = true
            Handler().postDelayed({
                HttpTask(this)  {
                    ActivityUtil().loadingStop(loading_spinner,rl_progress,tv_progress,this)
                    isLoading = false
                    if (it == null) {
                        Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show()
                        return@HttpTask
                    } else {

                        val json = it.toString() // json value
                         response = Gson().fromJson(json, PaymentBaseResponse::class.java)

                        when(response.ackMessage.responseCode.toString().contentEquals("0000")) {
                            true->{

                                initStatus(response)
                                donationStatus = true
                            }
                            false->{
//                                val intent = Intent(this, Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback"))
//                                intent.putExtra(CONFIGURATION, storeConfig)
//                                intent.putExtra(PAYMENTORDER, paymentOrder)
//                                intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
//                                intent.putExtra(PAYMENTRESPONSE,response)
//                                intent.putExtra(CHARITYRESPONSE,"")
//                                intent.putExtra(ORDERID, orderId)
//                                intent.putExtra(AMOUNT, paymnentCharity.orderAmount)
//                                intent.putExtra(STORENAME, paymnentCharity.storeName)
////                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
////                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                startActivity(intent)
//                                donationStatus = false
//                                this.finish()
                                showDialog(response)
                            }

                        }

                    }

                }.execute(
                    "POST",
                    storeConfig.baseUrl/*BASE_URL*/+"/checkout/ma/initiate-transaction",
                    json.toString()
                )

            }, 0)


        }
        else{
            Toast.makeText(this, "Internet Not Available !", Toast.LENGTH_LONG).show()
        }
        }
    }

    private fun setStoreDetails(storeConfig: EPConfiguration, paymentOrder: EPPaymentOrder) {
        val orderID = findViewById<TextView>(R.id.tv_orderId)
        orderID.text = orderId

        val storeName = findViewById<TextView>(R.id.tv_storeName)
        storeName.text = paymnentCharity.storeName

        val orderAmount = findViewById<TextView>(R.id.tv_totalAmount)
        orderAmount.text = paymnentCharity.orderAmount

    }
    private fun setHeadingTitle(title: String) {

        val toolbar = findViewById<Toolbar>(R.id.mainToolbar_payment_confirmation)
        val mTitle = toolbar.findViewById(R.id.toolbar_title_payment_confirmation) as TextView
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

    override fun onBackPressed() {

        if (!isLoading) {
            if (donationStatus) {
                //super.onBackPressed()
                val intent = Intent(this, Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentFeedback"))
                intent.putExtra(CONFIGURATION, storeConfig)
                intent.putExtra(PAYMENTORDER, paymentOrder)
                intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                intent.putExtra(PAYMENTRESPONSE,response)
                intent.putExtra(CHARITYRESPONSE,donationCharityResponse)
                intent.putExtra(CHARITYRESULT, "true")
                intent.putExtra(CHARITYORDER, paymnentCharity)
                intent.putExtra(ORDERID, orderId)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                this.finish()
            }
            else{
                super.onBackPressed()
            }

        }



    }

    private fun showDialog(response: PaymentBaseResponse){

        // Initialize a new instance of
        val builder = AlertDialog.Builder(this@EasypayPaymentConfirmation)

        // Set the alert dialog title
        builder.setTitle("Error")

        // Display a message on alert dialog
        builder.setMessage(response.ackMessage.responseDescription)

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Ok"){dialog, which ->

            this.finish()

        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()

    }
}