package telenor.com.ep_v1_sdk.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import org.json.JSONObject
import telenor.com.ep_v1_sdk.config.*
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_payment_method.*
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.ll_top_view.*
import telenor.com.ep_v1_sdk.adapter.EPPaymentMethodAdapter
import telenor.com.ep_v1_sdk.config.model.*
import telenor.com.ep_v1_sdk.rest.HttpTask
import telenor.com.ep_v1_sdk.util.InternetHelper
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.util.ActivityUtil
import telenor.com.ep_v1_sdk.util.Validation
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.Flags


class EasypayPaymentMethod : AppCompatActivity(){
    private lateinit var storeConfig: EPConfiguration
    private lateinit var paymentOrder :EPPaymentOrder

    private lateinit var epPaymentMethodAdapter: EPPaymentMethodAdapter
    private lateinit var sharedPrefDataSource: EPSharedPrefDataSource

    private var allowedPayments : ArrayList<AllowedPaymentMethods> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)
        //setting toolbar
        setHeadingTitle(resources.getString(R.string.easypaisa_wallet))

        storeConfig = intent.getSerializableExtra(CONFIGURATION) as EPConfiguration
        paymentOrder = intent.getSerializableExtra(PAYMENTORDER) as EPPaymentOrder


        getPaymentMethods(storeConfig,paymentOrder)

        setStoreDetails(storeConfig,paymentOrder)

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



            var postData = ""
            if (!paymentOrder.orderAmount.toString().isNullOrEmpty()) {
                postData += ENCRYPT_AMOUNT + ENCRYPT_KEY_EQUAL + paymentOrder.orderAmount
            }
            if (!storeConfig.bankIdentifier.toString().isNullOrEmpty()){
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_BANKIDENTIFICATIONNUM + ENCRYPT_KEY_EQUAL + storeConfig.bankIdentifier
            }
            if (!paymentOrder.email.toString().isNullOrEmpty()){
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_EMAILADDRESS + ENCRYPT_KEY_EQUAL + paymentOrder.email
            }
            if (!storeConfig.expiryToken.isNullOrEmpty()) {
                var expiryToken = storeConfig.expiryToken
                var d1 =expiryToken.replace("-","")
                var d2 = d1.replace(":","")
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_EXPIRYDATE + ENCRYPT_KEY_EQUAL + d2
            }
            if (!paymentOrder.phone.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_MOBILENUM + ENCRYPT_KEY_EQUAL + paymentOrder.phone.toString()
            }
            if (!paymentOrder.orderId.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_ORDERREFNUM + ENCRYPT_KEY_EQUAL +paymentOrder.orderId.toString()
            }
            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL + VALUE_INITIAL_REQUEST
            if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
            }


            val encryptedRequest = Validation().encrypt(postData.toString(),storeConfig.secretKey)
            json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))


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
                            if(!response!!.content!!.hashKey.isNullOrEmpty())
                                storeConfig.secretKey =response.content.hashKey
                            sharedPrefDataSource.setEasyPayConfig(storeConfig)

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
                                startActivityForResult(intent,200)
                            }

                            epPaymentMethodAdapter.addItems(allowedPayments)

                            paymentMethods.adapter =epPaymentMethodAdapter



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
                BASE_URL+ INITIATEPAYMENTMETHODS_ENDPOINT,
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
            if (data!!.getStringExtra(TRANSACTIONSTATUS).contentEquals("true")){
                finish()
            }


        }
    }




}