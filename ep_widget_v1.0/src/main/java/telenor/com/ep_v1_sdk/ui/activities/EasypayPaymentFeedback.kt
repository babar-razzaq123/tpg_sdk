package telenor.com.ep_v1_sdk.ui.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.activity_payment_method.*
import kotlinx.android.synthetic.main.ll_form_cc.*
import kotlinx.android.synthetic.main.ll_top_view.*
import org.json.JSONObject
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.config.*
import telenor.com.ep_v1_sdk.config.model.*
import telenor.com.ep_v1_sdk.rest.HttpTask
import telenor.com.ep_v1_sdk.util.ActivityUtil
import telenor.com.ep_v1_sdk.util.InternetHelper
import telenor.com.ep_v1_sdk.util.Validation
import java.io.Serializable
import java.util.*


class EasypayPaymentFeedback : AppCompatActivity() {


    lateinit var progressBar: ProgressBar
    lateinit var storeConfig :EPConfiguration
    lateinit var paymentOrder:EPPaymentOrder
    lateinit var paymnentCharity:EPPaymentCharityOrder
    var feedbackType :String = ""
    var feedbackSubmited:Boolean=false
    var isLoading:Boolean=false
    lateinit var orderId: String
    lateinit var amount: String
    lateinit var storeName: String
    var transactionStatus: String = ""
    var ccResponseSuccess: String = ""
    var mpgsFlag: String = ""
    var mpgsOrMigs: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        //setting toolbar
        setHeadingTitle(resources.getString(R.string.easypaisa_wallet))

        progressBar =findViewById<ProgressBar>(R.id.loading_spinner)
        storeConfig = intent.getSerializableExtra(CONFIGURATION) as EPConfiguration
        paymentOrder = intent.getSerializableExtra(PAYMENTORDER) as EPPaymentOrder
        if (intent.getSerializableExtra(CHARITYORDER) != null) {
            paymnentCharity = intent.getSerializableExtra(CHARITYORDER) as EPPaymentCharityOrder
        }
        else {
            paymnentCharity = EPPaymentCharityOrder("", "", "", "", "")
        }
        if (intent.getStringExtra(ORDERID) != null)
            orderId = intent.getStringExtra(ORDERID)
        if (intent.getStringExtra(AMOUNT) != null)
            amount = intent.getStringExtra(AMOUNT)
        if (intent.getStringExtra(STORENAME) != null)
            storeName = intent.getStringExtra(STORENAME)
        if (intent.getStringExtra(TRANSACTIONSTATUS) != null)
            transactionStatus = intent.getStringExtra(TRANSACTIONSTATUS)
        if (intent.getStringExtra(MPGSFLAG) != null)
            mpgsFlag = intent.getStringExtra(MPGSFLAG)
        if (intent.getStringExtra(CCRESPONSESUCCESS) != null)
            ccResponseSuccess = intent.getStringExtra(CCRESPONSESUCCESS)
        if (intent.getStringExtra(MPGSORMIGS) != null)
            mpgsOrMigs = intent.getStringExtra(MPGSORMIGS)
        setFeedback(storeConfig)
        setStoreDetails(storeConfig,paymentOrder)
        setResponse()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        //ed_feedback.setRawInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

    }

    private fun setFeedback( storeConfig: EPConfiguration ) {


        var allowedPaymentMethods = intent.getSerializableExtra(PAYMENTTYPE) as AllowedPaymentMethods


        if(!allowedPaymentMethods.code.contentEquals("DD")) {
            ll_feedback.visibility =View.VISIBLE

            if (allowedPaymentMethods.code.contentEquals("CC") && ccResponseSuccess.contentEquals("true")){
                 var response = intent.getSerializableExtra(PAYMENTRESPONSE) as CCTransactionStatusResponse

                if (response.content != null) {
                    if (response.content.transactionId.isNullOrEmpty()) {
                        ll_feedback.visibility = View.GONE
                    }
                }
                else{
                    ll_feedback.visibility = View.GONE
                }

                buttonSubmit.setOnClickListener {

                    if(!feedbackSubmited){
                        sendFeedback(storeConfig,response.content.transactionId.toString(),ed_feedback.text.toString())

                    }
                    if(feedbackType.isNullOrEmpty()){
                        buttonSubmit.alpha =0.5f
                        buttonSubmit.isEnabled = false
                    }
                    else if(!feedbackType.isNullOrEmpty()){
                        buttonSubmit.alpha=1f
                        buttonSubmit.isEnabled = true
                    }
                }
            }
            else {
                 var response = intent.getSerializableExtra(PAYMENTRESPONSE) as PaymentBaseResponse

                if (response.content != null) {
                    if (response.content.transactionId.isNullOrEmpty()) {
                        ll_feedback.visibility = View.GONE
                    }
                }
                else{
                    ll_feedback.visibility = View.GONE
                }

                buttonSubmit.setOnClickListener {

                    if(!feedbackSubmited){
                        sendFeedback(storeConfig,response.content.transactionId.toString(),ed_feedback.text.toString())

                    }
                    if(feedbackType.isNullOrEmpty()){
                        buttonSubmit.alpha =0.5f
                        buttonSubmit.isEnabled = false
                    }
                    else if(!feedbackType.isNullOrEmpty()){
                        buttonSubmit.alpha=1f
                        buttonSubmit.isEnabled = true
                    }
                }
            }
//        if (response.content != null) {
//            if (response.content.transactionId.isNullOrEmpty()) {
//                ll_feedback.visibility = View.GONE
//            }
//        }
//        else{
//            ll_feedback.visibility = View.GONE
//        }
//
//        buttonSubmit.setOnClickListener {
//
//            if(!feedbackSubmited){
//            sendFeedback(storeConfig,response.content.transactionId.toString(),ed_feedback.text.toString())
//
//            }
//            if(feedbackType.isNullOrEmpty()){
//                buttonSubmit.alpha =0.5f
//                buttonSubmit.isEnabled = false
//            }
//            else if(!feedbackType.isNullOrEmpty()){
//                buttonSubmit.alpha=1f
//                buttonSubmit.isEnabled = true
//            }
//        }

        imgHappy.setOnClickListener {
            imgHappy.alpha = 1f
            imgAverage.alpha = 0.5f
            imgBad.alpha = 0.5f
            if(!feedbackSubmited){
                feedbackType= "good"}
            titleHappy.setTextColor(resources.getColor(R.color.black))
            titleAverage.setTextColor(resources.getColor(R.color.grey))
            titleBad.setTextColor(resources.getColor(R.color.grey))

            if(feedbackType.isNullOrEmpty()){
                buttonSubmit.alpha =0.5f
                buttonSubmit.isEnabled = false
            }
            else if(!feedbackType.isNullOrEmpty()){
                buttonSubmit.alpha=1f
                buttonSubmit.isEnabled = true
            }
        }
        imgAverage.setOnClickListener {


            imgHappy.alpha = 0.5f
            imgAverage.alpha = 1f
            imgBad.alpha = 0.5f
            if(!feedbackSubmited){
                feedbackType= "average"}

            titleHappy.setTextColor(resources.getColor(R.color.grey))
            titleAverage.setTextColor(resources.getColor(R.color.black))
            titleBad.setTextColor(resources.getColor(R.color.grey))

            if(feedbackType.isNullOrEmpty()){
                buttonSubmit.alpha =0.5f
                buttonSubmit.isEnabled = false
            }
            else if(!feedbackType.isNullOrEmpty()){
                buttonSubmit.alpha=1f
                buttonSubmit.isEnabled = true
            }
        }
        imgBad.setOnClickListener {

            imgHappy.alpha = 0.5f
            imgAverage.alpha = 0.5f
            imgBad.alpha = 1f
            if(!feedbackSubmited){
                feedbackType= "bad"}

            titleHappy.setTextColor(resources.getColor(R.color.grey))
            titleAverage.setTextColor(resources.getColor(R.color.grey))
            titleBad.setTextColor(resources.getColor(R.color.black))

            if(feedbackType.isNullOrEmpty()){
                buttonSubmit.alpha =0.5f
                buttonSubmit.isEnabled = false
            }
            else if(!feedbackType.isNullOrEmpty()){
                buttonSubmit.alpha=1f
                buttonSubmit.isEnabled = true
            }
        }


        if(feedbackType.isNullOrEmpty()){
            buttonSubmit.alpha =0.5f
            buttonSubmit.isEnabled = false
        }
        else if(!feedbackType.isNullOrEmpty()){
            buttonSubmit.alpha=1f
            buttonSubmit.isEnabled = true
        }
       }
       else{
            ll_feedback.visibility =View.VISIBLE

            var responseCode = intent.getSerializableExtra(RESPONSECODE) as String
            var responseMethod = intent.getSerializableExtra(RESPONSEMETHOD) as String

            if(responseMethod.contentEquals("OTP")){
                var response = intent.getSerializableExtra(PAYMENTRESPONSE) as PaymentDDResponse

                if (response.content != null) {
                    if (response.content.transactionId.isNullOrEmpty()) {
                        ll_feedback.visibility = View.GONE
                    }
                }
                else{
                    ll_feedback.visibility = View.GONE
                }

                buttonSubmit.setOnClickListener {

                    if (!feedbackSubmited) {
                        sendFeedback(
                            storeConfig,
                            response.content.transactionId.toString(),
                            ed_feedback.text.toString()
                        )

                    }
                    if (feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 0.5f
                        buttonSubmit.isEnabled = false
                    } else if (!feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 1f
                        buttonSubmit.isEnabled = true
                    }
                }

                imgHappy.setOnClickListener {
                    imgHappy.alpha = 1f
                    imgAverage.alpha = 0.5f
                    imgBad.alpha = 0.5f
                    if (!feedbackSubmited) {
                        feedbackType = "good"
                    }
                    titleHappy.setTextColor(resources.getColor(R.color.black))
                    titleAverage.setTextColor(resources.getColor(R.color.grey))
                    titleBad.setTextColor(resources.getColor(R.color.grey))

                    if (feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 0.5f
                        buttonSubmit.isEnabled = false
                    } else if (!feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 1f
                        buttonSubmit.isEnabled = true
                    }
                }
                imgAverage.setOnClickListener {


                    imgHappy.alpha = 0.5f
                    imgAverage.alpha = 1f
                    imgBad.alpha = 0.5f
                    if (!feedbackSubmited) {
                        feedbackType = "average"
                    }

                    titleHappy.setTextColor(resources.getColor(R.color.grey))
                    titleAverage.setTextColor(resources.getColor(R.color.black))
                    titleBad.setTextColor(resources.getColor(R.color.grey))

                    if (feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 0.5f
                        buttonSubmit.isEnabled = false
                    } else if (!feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 1f
                        buttonSubmit.isEnabled = true
                    }
                }
                imgBad.setOnClickListener {

                    imgHappy.alpha = 0.5f
                    imgAverage.alpha = 0.5f
                    imgBad.alpha = 1f
                    if (!feedbackSubmited) {
                        feedbackType = "bad"
                    }

                    titleHappy.setTextColor(resources.getColor(R.color.grey))
                    titleAverage.setTextColor(resources.getColor(R.color.grey))
                    titleBad.setTextColor(resources.getColor(R.color.black))

                    if (feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 0.5f
                        buttonSubmit.isEnabled = false
                    } else if (!feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 1f
                        buttonSubmit.isEnabled = true
                    }
                }


                if (feedbackType.isNullOrEmpty()) {
                    buttonSubmit.alpha = 0.5f
                    buttonSubmit.isEnabled = false
                } else if (!feedbackType.isNullOrEmpty()) {
                    buttonSubmit.alpha = 1f
                    buttonSubmit.isEnabled = true
                }

            }
            else{


                var response = intent.getSerializableExtra(PAYMENTRESPONSE) as PaymentBaseDDResponse

                if (response.content != null) {
                    if (response.content.paymentDto != null) {
                        if (response.content.paymentDto.transactionId.isNullOrEmpty()) {
                            ll_feedback.visibility = View.GONE
                        }
                    }
                    else{
                        ll_feedback.visibility = View.GONE
                    }
                }else{
                    ll_feedback.visibility = View.GONE
                }

                buttonSubmit.setOnClickListener {

                    if (!feedbackSubmited) {
                        sendFeedback(
                            storeConfig,
                            response.content.paymentDto.transactionId.toString(),
                            ed_feedback.text.toString()
                        )

                    }
                    if (feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 0.5f
                        buttonSubmit.isEnabled = false
                    } else if (!feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 1f
                        buttonSubmit.isEnabled = true
                    }
                }

                imgHappy.setOnClickListener {
                    imgHappy.alpha = 1f
                    imgAverage.alpha = 0.5f
                    imgBad.alpha = 0.5f
                    if (!feedbackSubmited) {
                        feedbackType = "good"
                    }
                    titleHappy.setTextColor(resources.getColor(R.color.black))
                    titleAverage.setTextColor(resources.getColor(R.color.grey))
                    titleBad.setTextColor(resources.getColor(R.color.grey))

                    if (feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 0.5f
                        buttonSubmit.isEnabled = false
                    } else if (!feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 1f
                        buttonSubmit.isEnabled = true
                    }
                }
                imgAverage.setOnClickListener {


                    imgHappy.alpha = 0.5f
                    imgAverage.alpha = 1f
                    imgBad.alpha = 0.5f
                    if (!feedbackSubmited) {
                        feedbackType = "average"
                    }

                    titleHappy.setTextColor(resources.getColor(R.color.grey))
                    titleAverage.setTextColor(resources.getColor(R.color.black))
                    titleBad.setTextColor(resources.getColor(R.color.grey))

                    if (feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 0.5f
                        buttonSubmit.isEnabled = false
                    } else if (!feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 1f
                        buttonSubmit.isEnabled = true
                    }
                }
                imgBad.setOnClickListener {

                    imgHappy.alpha = 0.5f
                    imgAverage.alpha = 0.5f
                    imgBad.alpha = 1f
                    if (!feedbackSubmited) {
                        feedbackType = "bad"
                    }

                    titleHappy.setTextColor(resources.getColor(R.color.grey))
                    titleAverage.setTextColor(resources.getColor(R.color.grey))
                    titleBad.setTextColor(resources.getColor(R.color.black))

                    if (feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 0.5f
                        buttonSubmit.isEnabled = false
                    } else if (!feedbackType.isNullOrEmpty()) {
                        buttonSubmit.alpha = 1f
                        buttonSubmit.isEnabled = true
                    }
                }


                if (feedbackType.isNullOrEmpty()) {
                    buttonSubmit.alpha = 0.5f
                    buttonSubmit.isEnabled = false
                } else if (!feedbackType.isNullOrEmpty()) {
                    buttonSubmit.alpha = 1f
                    buttonSubmit.isEnabled = true
                }

            }
        }

    }

    private fun setDonation(allowedPaymentMethods: AllowedPaymentMethods) {

        if(allowedPaymentMethods.code.contentEquals("MA")){
            if (!intent.getSerializableExtra(CHARITYRESPONSE).toString().isNullOrEmpty()){

                if (!intent.getStringExtra(CHARITYRESULT).toString().isNullOrEmpty()){
                    if (intent.getStringExtra(CHARITYRESULT).toString().contentEquals("true")) {
                        setDonationDetails(storeConfig, paymentOrder)
                    }
                }

                var donationBaseResponse = intent.getSerializableExtra(CHARITYRESPONSE) as DonationBaseResponse

                if(!donationBaseResponse.content.isNullOrEmpty()){
                ll_donation.visibility=View.VISIBLE
                var index =0
                if(donationBaseResponse.content.isNotEmpty()){
                    tv_charity.text =donationBaseResponse.content.get(index).charityName

                    val pkr = resources.getText(R.string.pkr).toString()
                    tv_donation1.text = pkr+"\n"+ donationBaseResponse.content.get(index).donationLevel1
                    tv_donation2.text =pkr+"\n"+ donationBaseResponse.content.get(index).donationLevel2
                    tv_donation3.text =pkr+"\n"+donationBaseResponse.content.get(index).donationLevel3

                    tv_donation1.setBackgroundResource(R.drawable.donation_white_bg)
                    tv_donation1.setTextColor(Color.parseColor("#788995"))
//                    tv_donation1.setTextAppearance(this, R.style.greyColorText)
//                    tv_donation1.textSize =resources.getDimension(R.dimen._9ssp)

                    tv_donation2.setBackgroundResource(R.drawable.donation_white_bg)
                    tv_donation2.setTextColor(Color.parseColor("#788995"))
//                    tv_donation2.setTextAppearance(this, R.style.greyColorText)
//                    tv_donation2.textSize =resources.getDimension(R.dimen._9ssp)

                    tv_donation3.setBackgroundResource(R.drawable.donation_white_bg)
                    tv_donation3.setTextColor(Color.parseColor("#788995"))
//                    tv_donation3.setTextAppearance(this, R.style.greyColorText)
//                    tv_donation3.textSize =resources.getDimension(R.dimen._9ssp)

                    paymnentCharity.charityStoreId = donationBaseResponse.content.get(index).store.id.toString()
                    if(!donationBaseResponse.content.get(index).store.hashKey.isNullOrEmpty())
                    paymnentCharity.secretKey =donationBaseResponse.content.get(index).store.hashKey
                    paymnentCharity.orderAmount=""
                    paymnentCharity.storeName = donationBaseResponse.content.get(index).store.name
                    if(paymnentCharity.orderAmount.isNullOrEmpty()){
                        buttonDonate.alpha =0.5f
                        buttonDonate.isEnabled = false
                    }
                    else if(!paymnentCharity.orderAmount.isNullOrEmpty()){
                        buttonDonate.alpha=1f
                        buttonDonate.isEnabled = true
                    }
                    ed_donation_amount_cc.text!!.clear()

                }

                imgArrowLeft.setOnClickListener {

                    if (index> 0){
                        index--
                        if(!donationBaseResponse.content.get(index).charityName.isNullOrBlank()){
                            tv_charity.text =donationBaseResponse.content.get(index).charityName
                            val pkr = resources.getText(R.string.pkr).toString()
                            tv_donation1.text = pkr+"\n"+ donationBaseResponse.content.get(index).donationLevel1
                            tv_donation2.text =pkr+"\n"+ donationBaseResponse.content.get(index).donationLevel2
                            tv_donation3.text =pkr+"\n"+donationBaseResponse.content.get(index).donationLevel3

                            tv_donation1.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation1.setTextColor(Color.parseColor("#788995"))
//                            tv_donation1.setTextAppearance(this, R.style.greyColorText)
//                            tv_donation1.textSize =resources.getDimension(R.dimen._9ssp)

                            tv_donation2.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation2.setTextColor(Color.parseColor("#788995"))
//                            tv_donation2.setTextAppearance(this, R.style.greyColorText)
//                            tv_donation2.textSize =resources.getDimension(R.dimen._9ssp)

                            tv_donation3.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation3.setTextColor(Color.parseColor("#788995"))
//                            tv_donation3.setTextAppearance(this, R.style.greyColorText)
//                            tv_donation3.textSize =resources.getDimension(R.dimen._9ssp)

                            paymnentCharity.charityStoreId = donationBaseResponse.content.get(index).store.id.toString()
                            if(!donationBaseResponse.content.get(index).store.hashKey.isNullOrEmpty())
                            paymnentCharity.secretKey =donationBaseResponse.content.get(index).store.hashKey
                            paymnentCharity.storeName = donationBaseResponse.content.get(index).store.name
                            paymnentCharity.orderAmount=""
                            if(paymnentCharity.orderAmount.isNullOrEmpty()){
                                buttonDonate.alpha =0.5f
                                buttonDonate.isEnabled = false
                            }
                            else if(!paymnentCharity.orderAmount.isNullOrEmpty()){
                                buttonDonate.alpha=1f
                                buttonDonate.isEnabled = true
                            }
                            ed_donation_amount_cc.text!!.clear()
                        }
                    }
                    else{
                        index=donationBaseResponse.content.size-1
                        if(!donationBaseResponse.content.get(index).charityName.isNullOrBlank()){
                            tv_charity.text =donationBaseResponse.content.get(index).charityName
                            val pkr = resources.getText(R.string.pkr).toString()
                            tv_donation1.text = pkr+"\n"+ donationBaseResponse.content.get(index).donationLevel1
                            tv_donation2.text =pkr+"\n"+ donationBaseResponse.content.get(index).donationLevel2
                            tv_donation3.text =pkr+"\n"+donationBaseResponse.content.get(index).donationLevel3

                            tv_donation1.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation1.setTextColor(Color.parseColor("#788995"))
//                            tv_donation1.setTextAppearance(this, R.style.greyColorText)
//                            tv_donation1.textSize =resources.getDimension(R.dimen._9ssp)

                            tv_donation2.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation2.setTextColor(Color.parseColor("#788995"))
//                            tv_donation2.setTextAppearance(this, R.style.greyColorText)
//                            tv_donation2.textSize =resources.getDimension(R.dimen._9ssp)

                            tv_donation3.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation3.setTextColor(Color.parseColor("#788995"))
//                            tv_donation3.setTextAppearance(this, R.style.greyColorText)
//                            tv_donation3.textSize =resources.getDimension(R.dimen._9ssp)

                            paymnentCharity.charityStoreId = donationBaseResponse.content.get(index).store.id.toString()
                            if(!donationBaseResponse.content.get(index).store.hashKey.isNullOrEmpty())
                            paymnentCharity.secretKey =donationBaseResponse.content.get(index).store.hashKey
                            paymnentCharity.storeName = donationBaseResponse.content.get(index).store.name
                            paymnentCharity.orderAmount=""
                            if(paymnentCharity.orderAmount.isNullOrEmpty()){
                                buttonDonate.alpha =0.5f
                                buttonDonate.isEnabled = false
                            }
                            else if(!paymnentCharity.orderAmount.isNullOrEmpty()){
                                buttonDonate.alpha=1f
                                buttonDonate.isEnabled = true
                            }
                            ed_donation_amount_cc.text!!.clear()
                        }

                    }
                }
                imgArrowRight.setOnClickListener {

                    if (index< donationBaseResponse.content.size-1){
                        index++
                        if(!donationBaseResponse.content.get(index).charityName.isNullOrBlank()){
                            tv_charity.text =donationBaseResponse.content.get(index).charityName

                            val pkr = resources.getText(R.string.pkr).toString()
                            tv_donation1.text = pkr+"\n"+ donationBaseResponse.content.get(index).donationLevel1
                            tv_donation2.text =pkr+"\n"+ donationBaseResponse.content.get(index).donationLevel2
                            tv_donation3.text =pkr+"\n"+donationBaseResponse.content.get(index).donationLevel3


                            tv_donation1.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation1.setTextColor(Color.parseColor("#788995"))
//                            tv_donation1.setTextAppearance(this, R.style.greyColorText)
//                            tv_donation1.textSize =resources.getDimension(R.dimen._9ssp)

                            tv_donation2.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation2.setTextColor(Color.parseColor("#788995"))
//                            tv_donation2.setTextAppearance(this, R.style.greyColorText)
//                            tv_donation2.textSize =resources.getDimension(R.dimen._9ssp)

                            tv_donation3.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation3.setTextColor(Color.parseColor("#788995"))
//                            tv_donation3.setTextAppearance(this, R.style.greyColorText)
//                            tv_donation3.textSize =resources.getDimension(R.dimen._9ssp)

                            paymnentCharity.charityStoreId = donationBaseResponse.content.get(index).store.id.toString()
                            if(!donationBaseResponse.content.get(index).store.hashKey.isNullOrEmpty())
                            paymnentCharity.secretKey =donationBaseResponse.content.get(index).store.hashKey
                            paymnentCharity.storeName = donationBaseResponse.content.get(index).store.name
                            paymnentCharity.orderAmount=""
                            if(paymnentCharity.orderAmount.isNullOrEmpty()){
                                buttonDonate.alpha =0.5f
                                buttonDonate.isEnabled = false
                            }
                            else if(!paymnentCharity.orderAmount.isNullOrEmpty()){
                                buttonDonate.alpha=1f
                                buttonDonate.isEnabled = true
                            }
                            ed_donation_amount_cc.text!!.clear()
                        }
                    }
                    else{
                        index=0

                        if(!donationBaseResponse.content.get(index).charityName.isNullOrBlank()) {
                            tv_charity.text = donationBaseResponse.content.get(index).charityName

                            val pkr = resources.getText(R.string.pkr).toString()
                            tv_donation1.text = pkr + "\n" + donationBaseResponse.content.get(index).donationLevel1
                            tv_donation2.text = pkr + "\n" + donationBaseResponse.content.get(index).donationLevel2
                            tv_donation3.text = pkr + "\n" + donationBaseResponse.content.get(index).donationLevel3


                            tv_donation1.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation1.setTextColor(Color.parseColor("#788995"))
//                            tv_donation1.setTextAppearance(this, R.style.greyColorText)
//                            tv_donation1.textSize =resources.getDimension(R.dimen._9ssp)

                            tv_donation2.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation2.setTextColor(Color.parseColor("#788995"))
//                            tv_donation2.setTextAppearance(this, R.style.greyColorText)
//                            tv_donation2.textSize =resources.getDimension(R.dimen._9ssp)

                            tv_donation3.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation3.setTextColor(Color.parseColor("#788995"))
//                            tv_donation3.setTextAppearance(this, R.style.greyColorText)
//                            tv_donation3.textSize =resources.getDimension(R.dimen._9ssp)

                            paymnentCharity.charityStoreId = donationBaseResponse.content.get(index).store.id.toString()
                            if(!donationBaseResponse.content.get(index).store.hashKey.isNullOrEmpty())
                            paymnentCharity.secretKey = donationBaseResponse.content.get(index).store.hashKey
                            paymnentCharity.storeName = donationBaseResponse.content.get(index).store.name
                            paymnentCharity.orderAmount = ""
                            if (paymnentCharity.orderAmount.isNullOrEmpty()) {
                                buttonDonate.alpha = 0.5f
                                buttonDonate.isEnabled = false
                            } else if (!paymnentCharity.orderAmount.isNullOrEmpty()) {
                                buttonDonate.alpha = 1f
                                buttonDonate.isEnabled = true
                            }
                            ed_donation_amount_cc.text!!.clear()
                        }

                    }


                }

                tv_donation1.setOnClickListener {

                    ed_donation_amount_cc.text!!.clear()

                    paymnentCharity.orderAmount = tv_donation1.text.toString().replace("PKR","").replace("\n","")

                    if(!paymnentCharity.orderAmount.contentEquals("0")) {

                        tv_donation1.isFocusable = true
                        tv_donation1.setBackgroundResource(R.drawable.donation_grey_bg)
                        tv_donation1.setTextColor(Color.parseColor("#FFFFFF"))
//                        tv_donation1.setTextAppearance(this, R.style.whiteColorText)
//                    tv_donation1.textSize =resources.getDimension(R.dimen._9ssp)

                        tv_donation2.setBackgroundResource(R.drawable.donation_white_bg)
                        tv_donation2.setTextColor(Color.parseColor("#788995"))
                        //tv_donation2.setTextAppearance(this, R.style.greyColorText)
//                    tv_donation2.textSize =resources.getDimension(R.dimen._9ssp)


                        tv_donation3.setBackgroundResource(R.drawable.donation_white_bg)
                        tv_donation3.setTextColor(Color.parseColor("#788995"))
//                        tv_donation3.setTextAppearance(this, R.style.greyColorText)
//                    tv_donation3.textSize =resources.getDimension(R.dimen._9ssp)

                        paymnentCharity.charityName = tv_charity.text.toString()

                        if (!donationBaseResponse.content.get(index).store.hashKey.isNullOrEmpty())
                            paymnentCharity.secretKey = donationBaseResponse.content.get(index).store.hashKey
                        paymnentCharity.storeName = donationBaseResponse.content.get(index).store.name

                        if (paymnentCharity.orderAmount.isNullOrEmpty()) {
                            buttonDonate.alpha = 0.5f
                            buttonDonate.isEnabled = false
                        } else if (!paymnentCharity.orderAmount.isNullOrEmpty()) {
                            buttonDonate.alpha = 1f
                            buttonDonate.isEnabled = true
                        }

                    }
                }
                tv_donation2.setOnClickListener {

                    ed_donation_amount_cc.text!!.clear()

                    paymnentCharity.orderAmount = tv_donation2.text.toString().replace("PKR","").replace("\n","")

                    if (!paymnentCharity.orderAmount.contentEquals("0")) {
                        tv_donation2.isFocusable = true
                        tv_donation1.setBackgroundResource(R.drawable.donation_white_bg)
                        tv_donation1.setTextColor(Color.parseColor("#788995"))
//                        tv_donation1.setTextAppearance(this, R.style.greyColorText)
//                    tv_donation1.textSize =resources.getDimension(R.dimen._9ssp)

                        tv_donation2.setBackgroundResource(R.drawable.donation_grey_bg)
                        tv_donation2.setTextColor(Color.parseColor("#FFFFFF"))
//                        tv_donation2.setTextAppearance(this, R.style.whiteColorText)
//                    tv_donation2.textSize =resources.getDimension(R.dimen._9ssp)


                        tv_donation3.setBackgroundResource(R.drawable.donation_white_bg)
                        tv_donation3.setTextColor(Color.parseColor("#788995"))
//                        tv_donation3.setTextAppearance(this, R.style.greyColorText)
//                    tv_donation3.textSize =resources.getDimension(R.dimen._9ssp)

                        paymnentCharity.charityName = tv_charity.text.toString()

                        if (!donationBaseResponse.content.get(index).store.hashKey.isNullOrEmpty())
                            paymnentCharity.secretKey = donationBaseResponse.content.get(index).store.hashKey
                        paymnentCharity.storeName = donationBaseResponse.content.get(index).store.name

                        if (paymnentCharity.orderAmount.isNullOrEmpty()) {
                            buttonDonate.alpha = 0.5f
                            buttonDonate.isEnabled = false
                        } else if (!paymnentCharity.orderAmount.isNullOrEmpty()) {
                            buttonDonate.alpha = 1f
                            buttonDonate.isEnabled = true
                        }
                    }
                }
                tv_donation3.setOnClickListener {

                    ed_donation_amount_cc.text!!.clear()

                    paymnentCharity.orderAmount = tv_donation3.text.toString().replace("PKR","").replace("\n","")

                    if (!paymnentCharity.orderAmount.contentEquals("0")) {

                        tv_donation3.isFocusable = true
                        tv_donation1.setBackgroundResource(R.drawable.donation_white_bg)
                        tv_donation1.setTextColor(Color.parseColor("#788995"))
//                        tv_donation1.setTextAppearance(this, R.style.greyColorText)
//                    tv_donation1.textSize =resources.getDimension(R.dimen._9ssp)

                        tv_donation2.setBackgroundResource(R.drawable.donation_white_bg)
                        tv_donation2.setTextColor(Color.parseColor("#788995"))
//                        tv_donation2.setTextAppearance(this, R.style.greyColorText)
//                    tv_donation2.textSize =resources.getDimension(R.dimen._9ssp)


                        tv_donation3.setBackgroundResource(R.drawable.donation_grey_bg)
                        tv_donation3.setTextColor(Color.parseColor("#FFFFFF"))
//                        tv_donation3.setTextAppearance(this, R.style.whiteColorText)
//                    tv_donation3.textSize =resources.getDimension(R.dimen._9ssp)
                        paymnentCharity.charityName = tv_charity.text.toString()

                        if (!donationBaseResponse.content.get(index).store.hashKey.isNullOrEmpty())
                            paymnentCharity.secretKey = donationBaseResponse.content.get(index).store.hashKey
                        if (paymnentCharity.orderAmount.isNullOrEmpty()) {
                            buttonDonate.alpha = 0.5f
                            buttonDonate.isEnabled = false
                        } else if (!paymnentCharity.orderAmount.isNullOrEmpty()) {
                            buttonDonate.alpha = 1f
                            buttonDonate.isEnabled = true
                        }
                    }
                }


                ed_donation_amount_cc.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                        paymnentCharity.orderAmount= Validation().getAmount(ed_donation_amount_cc.text.toString())

                        if (!paymnentCharity.orderAmount.isNullOrEmpty()) {
                            if (paymnentCharity.orderAmount.toDouble() > 0) {

                                tv_donation1.setBackgroundResource(R.drawable.donation_white_bg)
                                tv_donation1.setTextColor(Color.parseColor("#788995"))
//                                tv_donation1.setTextAppearance(this@EasypayPaymentFeedback, R.style.greyColorText)
//                        tv_donation1.textSize =resources.getDimension(R.dimen._9ssp)

                                tv_donation2.setBackgroundResource(R.drawable.donation_white_bg)
                                tv_donation2.setTextColor(Color.parseColor("#788995"))
//                                tv_donation2.setTextAppearance(this@EasypayPaymentFeedback, R.style.greyColorText)
//                        tv_donation2.textSize =resources.getDimension(R.dimen._9ssp)


                                tv_donation3.setBackgroundResource(R.drawable.donation_white_bg)
                                tv_donation3.setTextColor(Color.parseColor("#788995"))
//                                tv_donation3.setTextAppearance(this@EasypayPaymentFeedback, R.style.greyColorText)
//                        tv_donation3.textSize =resources.getDimension(R.dimen._9ssp)


                                paymnentCharity.charityName = tv_charity.text.toString()

                                if (!donationBaseResponse.content.get(index).store.hashKey.isNullOrEmpty())
                                    paymnentCharity.secretKey = donationBaseResponse.content.get(index).store.hashKey

                                paymnentCharity.storeName = donationBaseResponse.content.get(index).store.name

                                if (paymnentCharity.orderAmount.isNullOrEmpty()) {
                                    buttonDonate.alpha = 0.5f
                                    buttonDonate.isEnabled = false
                                } else if (!paymnentCharity.orderAmount.isNullOrEmpty()) {
                                    buttonDonate.alpha = 1f
                                    buttonDonate.isEnabled = true
                                }
                            }
                            else{
                                buttonDonate.alpha = 0.5f
                                buttonDonate.isEnabled = false

                                tv_donation1.setBackgroundResource(R.drawable.donation_white_bg)
                                tv_donation1.setTextColor(Color.parseColor("#788995"))
//                                tv_donation1.setTextAppearance(this@EasypayPaymentFeedback, R.style.greyColorText)
//                        tv_donation1.textSize =resources.getDimension(R.dimen._9ssp)

                                tv_donation2.setBackgroundResource(R.drawable.donation_white_bg)
                                tv_donation2.setTextColor(Color.parseColor("#788995"))
//                                tv_donation2.setTextAppearance(this@EasypayPaymentFeedback, R.style.greyColorText)
//                        tv_donation2.textSize =resources.getDimension(R.dimen._9ssp)


                                tv_donation3.setBackgroundResource(R.drawable.donation_white_bg)
                                tv_donation3.setTextColor(Color.parseColor("#788995"))
//                                tv_donation3.setTextAppearance(this@EasypayPaymentFeedback, R.style.greyColorText)
//                        tv_donation3.textSize =resources.getDimension(R.dimen._9ssp)

                            }
                        }
                        else{
                            buttonDonate.alpha = 0.5f
                            buttonDonate.isEnabled = false

                            tv_donation1.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation1.setTextColor(Color.parseColor("#788995"))
//                            tv_donation1.setTextAppearance(this@EasypayPaymentFeedback, R.style.greyColorText)
//                        tv_donation1.textSize =resources.getDimension(R.dimen._9ssp)

                            tv_donation2.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation2.setTextColor(Color.parseColor("#788995"))
//                            tv_donation2.setTextAppearance(this@EasypayPaymentFeedback, R.style.greyColorText)
//                        tv_donation2.textSize =resources.getDimension(R.dimen._9ssp)


                            tv_donation3.setBackgroundResource(R.drawable.donation_white_bg)
                            tv_donation3.setTextColor(Color.parseColor("#788995"))
//                            tv_donation3.setTextAppearance(this@EasypayPaymentFeedback, R.style.greyColorText)
//                        tv_donation3.textSize =resources.getDimension(R.dimen._9ssp)

                        }

                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
            buttonDonate.setOnClickListener {

                if(!paymnentCharity.orderAmount.isNullOrEmpty()){

                    if (Validation().validAmountValue(
                            paymnentCharity.orderAmount.toString(),
                            "^\\d{0,6}?\$"
                        )
                    ) {

                        val intent = Intent(
                            this,
                            Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentConfirmation")
                        )
                        intent.putExtra(CONFIGURATION, storeConfig)
                        intent.putExtra(PAYMENTORDER, paymentOrder)
                        intent.putExtra(PAYMENTTYPE, allowedPaymentMethods)
                        intent.putExtra(CHARITYORDER, paymnentCharity)
                        intent.putExtra(ORDERID, generateOrderId())
                        intent.putExtra(CHARITYRESPONSE, donationBaseResponse)
                        startActivity(intent)
                        //this.finish()
                    }
                    else{
                        Toast.makeText(this, "Invalid donation amount entered. Please enter any 6 digit value", Toast.LENGTH_LONG).show()
                    }

                    }


            }

            }

        }else{
            ll_donation.visibility= View.GONE
            setDonationDetails(storeConfig, paymentOrder)

        }
        }else{
            ll_donation.visibility= View.GONE
        }


    }

    private fun setResponse() {

        var allowedPaymentMethods = intent.getSerializableExtra(PAYMENTTYPE) as AllowedPaymentMethods


        if(!allowedPaymentMethods.code.contentEquals("DD")) {

            if (allowedPaymentMethods.code.contentEquals("CC") && ccResponseSuccess.contentEquals("true")){

                var response = intent.getSerializableExtra(PAYMENTRESPONSE) as CCTransactionStatusResponse

                if ((response.content.pendingCaptureInd.contentEquals("1")
                            && response.content.status.contentEquals("AUTHORIZED"))
                    || (response.content.pendingCaptureInd.contentEquals("0")
                            && response.content.status.contentEquals("PAID"))) {
                    titleStatus.text = getText(R.string.awesome)
                    titleStatus.setTextColor(resources.getColor(R.color.green))
                    imgIcon.setImageResource(R.drawable.ic_success)
                    paymentStatus.text = getText(R.string.successPayment)
                    //transactionId.text = response.content.transactionId.toString()
                    transactionIdTV.text = "Transaction Number"
                    transactionId.text = response.content.transactionRefNumber
                    totalAmount.text = response.content.transactionAmount
                    paymentDate.text = response.content.transactionDateTime
                    ll_transactionId.visibility = View.VISIBLE
                    ll_amount.visibility = View.VISIBLE
                    tv_failure.visibility = View.GONE
                    ll_token.visibility = View.GONE

                    //setDonation(allowedPaymentMethods)
                } else if (((response.content.pendingCaptureInd.contentEquals("1")
                            || response.content.pendingCaptureInd.contentEquals("0"))
                            && (response.content.status.contentEquals("FAILED")
                            ))){
                    titleStatus.text = getText(R.string.onno)
                    titleStatus.setTextColor(resources.getColor(R.color.red))
                    imgIcon.setImageResource(R.drawable.ic_failure)
                    paymentStatus.text = getText(R.string.failurePayment)
                    if (mpgsFlag.contentEquals("true")){
                        var responseCCCard = intent.getSerializableExtra(PAYMENTCCCARDRESPONSE) as PaymentCCCardResponse
                        if (mpgsOrMigs.contentEquals("MPGS")) {
                            tv_failure.text = responseCCCard.content.reattemptFailureMsg
                        }
                        else if (mpgsOrMigs.contentEquals("MIGS")){
                            tv_failure.text = responseCCCard.content.reattemptFailureMsgForMigs
                        }
                    }
                    else {
                        tv_failure.text = response.content.transactionStatusResponse
                    }
                    ll_transactionId.visibility = View.GONE
                    ll_amount.visibility = View.GONE
                    paymentDate.visibility = View.GONE
                    tv_failure.visibility = View.VISIBLE
                    ll_token.visibility = View.GONE


                }
                else{
                    // For Interim Case

//                    var responseCCCard = intent.getSerializableExtra(PAYMENTCCCARDRESPONSE) as PaymentCCCardResponse

                    titleStatus.text = getText(R.string.onno)
                    titleStatus.setTextColor(resources.getColor(R.color.red))
                    imgIcon.setImageResource(R.drawable.ic_failure)
                    paymentStatus.text = getText(R.string.failurePayment)
                    if(response.content != null) {
                        tv_failure.text = response.content.transactionStatusResponse
                    }
                    else{
                        tv_failure.text = "Request Failed"
                    }
                    ll_transactionId.visibility = View.GONE
                    ll_amount.visibility = View.GONE
                    paymentDate.visibility = View.GONE
                    tv_failure.visibility = View.VISIBLE
                    ll_token.visibility = View.GONE
                }
            }
            else if (allowedPaymentMethods.code.contentEquals("CC") && ccResponseSuccess.contentEquals("false")){

                var response = intent.getSerializableExtra(PAYMENTRESPONSE) as PaymentBaseResponse

                if (!response.ackMessage.responseCode.toString().contentEquals("0000")) {
                    titleStatus.text = getText(R.string.onno)
                    titleStatus.setTextColor(resources.getColor(R.color.red))
                    imgIcon.setImageResource(R.drawable.ic_failure)
                    paymentStatus.text = getText(R.string.failurePayment)
                    tv_failure.text = response.ackMessage.responseDescription
                    ll_transactionId.visibility = View.GONE
                    ll_amount.visibility = View.GONE
                    paymentDate.visibility = View.GONE
                    tv_failure.visibility = View.VISIBLE
                    ll_token.visibility = View.GONE

                }

            }
            else {
                var response = intent.getSerializableExtra(PAYMENTRESPONSE) as PaymentBaseResponse

                if (!allowedPaymentMethods.code.contentEquals("OTC")) {

                    if (response.ackMessage.responseCode.toString().contentEquals("0000")) {
                        titleStatus.text = getText(R.string.awesome)
                        titleStatus.setTextColor(resources.getColor(R.color.green))
                        imgIcon.setImageResource(R.drawable.ic_success)
                        paymentStatus.text = getText(R.string.successPayment)
                        transactionId.text = response.content.transactionId.toString()
                        totalAmount.text = response.content.transactionAmount
                        paymentDate.text = response.content.transactionDateTime
                        ll_transactionId.visibility = View.VISIBLE
                        ll_amount.visibility = View.VISIBLE
                        tv_failure.visibility = View.GONE
                        ll_token.visibility = View.GONE

                        setDonation(allowedPaymentMethods)
                    } else {
                        titleStatus.text = getText(R.string.onno)
                        titleStatus.setTextColor(resources.getColor(R.color.red))
                        imgIcon.setImageResource(R.drawable.ic_failure)
                        paymentStatus.text = getText(R.string.failurePayment)
                        tv_failure.text = response.ackMessage.responseDescription
                        ll_transactionId.visibility = View.GONE
                        ll_amount.visibility = View.GONE
                        paymentDate.visibility = View.GONE
                        tv_failure.visibility = View.VISIBLE
                        ll_token.visibility = View.GONE

                        if (allowedPaymentMethods.code.contentEquals("MA")) {

                            if (transactionStatus.isNullOrEmpty()) {
                                if (paymnentCharity.orderAmount.isNullOrEmpty()) {

                                    paymnentCharity.storeName = storeName
                                    paymnentCharity.orderAmount = amount
                                    setDonationDetails(storeConfig, paymentOrder)
                                }
                            }

                        }
                    }
                } else {

                    if (response.ackMessage.responseCode.toString().contentEquals("0000")) {
                        titleStatus.text = getText(R.string.awesome)
                        titleStatus.setTextColor(resources.getColor(R.color.green))
                        titleStatus.visibility = View.GONE
                        imgIcon.setImageResource(R.drawable.ic_success)
                        ll_payment.visibility = View.GONE
                        ll_token.visibility = View.VISIBLE
                        titleToken.visibility = View.VISIBLE
                        titleToken.text = getText(R.string.pleaseNoteToken)
                        tokenID.visibility = View.VISIBLE
                        tokenID.text = response.content.paymentToken
                        token_text.visibility = View.VISIBLE
                        token_text.text =
                            getString(R.string.tokenwillexpire) + response.content.paymentTokenExpiryDateTime + getString(
                                R.string.pleasevisit
                            ) + response.content.transactionAmount + getString(
                                R.string.againstorder
                            ) + response.content.orderId + getString(R.string.beforeexpiry)
                    } else {
                        titleStatus.text = getText(R.string.onno)
                        titleStatus.setTextColor(resources.getColor(R.color.red))
                        paymentStatus.text = getText(R.string.failurePayment)
                        imgIcon.setImageResource(R.drawable.ic_failure)
                        ll_payment.visibility = View.GONE
                        ll_token.visibility = View.VISIBLE
                        titleToken.visibility = View.GONE
                        tokenID.visibility = View.GONE
                        token_text.visibility = View.VISIBLE
                        token_text.text = response.ackMessage.responseDescription
                    }
                }
            }
        }
        else{



            var responseCode = intent.getSerializableExtra(RESPONSECODE) as String
            var responseMethod = intent.getSerializableExtra(RESPONSEMETHOD) as String

            if(responseMethod.contentEquals("OTP")){

            var response = intent.getSerializableExtra(PAYMENTRESPONSE) as PaymentDDResponse

            if (response.ackMessage.responseCode.toString().contentEquals("0")) {
                titleStatus.text = getText(R.string.awesome)
                titleStatus.setTextColor(resources.getColor(R.color.green))
                imgIcon.setImageResource(R.drawable.ic_success)
                paymentStatus.text = getText(R.string.successPayment)
                transactionId.text = response.content.transactionId.toString()
                totalAmount.text = paymentOrder.orderAmount.toString()

                val cal = Calendar.getInstance(Locale.ENGLISH)
                //cal.setTimeInMillis(response.content.paymentDto.orderDateTime.toLong())
                val date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString()
                paymentDate.text = date
                ll_transactionId.visibility = View.VISIBLE
                ll_amount.visibility = View.VISIBLE
                tv_failure.visibility = View.GONE
                ll_token.visibility = View.GONE

            } else {
                titleStatus.text = getText(R.string.onno)
                titleStatus.setTextColor(resources.getColor(R.color.red))
                imgIcon.setImageResource(R.drawable.ic_failure)
                paymentStatus.text = getText(R.string.failurePayment)
                tv_failure.text = response.ackMessage.responseDescription
                ll_transactionId.visibility = View.GONE
                ll_amount.visibility = View.GONE
                paymentDate.visibility = View.GONE
                tv_failure.visibility = View.VISIBLE
                ll_token.visibility = View.GONE
            }
        }

            else{
                var response = intent.getSerializableExtra(PAYMENTRESPONSE) as PaymentBaseDDResponse

                if (response.ackMessage.responseCode.toString().contentEquals("0")) {
                    titleStatus.text = getText(R.string.awesome)
                    titleStatus.setTextColor(resources.getColor(R.color.green))
                    imgIcon.setImageResource(R.drawable.ic_success)
                    paymentStatus.text = getText(R.string.successPayment)
                    transactionId.text = response.content.paymentDto.transactionId.toString()
                    totalAmount.text = paymentOrder.orderAmount.toString()

                    val cal = Calendar.getInstance(Locale.ENGLISH)
                    cal.setTimeInMillis(response.content.paymentDto.orderDateTime.toLong())
                    val date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString()
                    paymentDate.text = date
                    ll_transactionId.visibility = View.VISIBLE
                    ll_amount.visibility = View.VISIBLE
                    tv_failure.visibility = View.GONE
                    ll_token.visibility = View.GONE

                } else {
                    titleStatus.text = getText(R.string.onno)
                    titleStatus.setTextColor(resources.getColor(R.color.red))
                    imgIcon.setImageResource(R.drawable.ic_failure)
                    paymentStatus.text = getText(R.string.failurePayment)
                    tv_failure.text = response.ackMessage.responseDescription
                    ll_transactionId.visibility = View.GONE
                    ll_amount.visibility = View.GONE
                    paymentDate.visibility = View.GONE
                    tv_failure.visibility = View.VISIBLE
                    ll_token.visibility = View.GONE

                }
            }


        }


    }

    private fun sendFeedback(storeConfig: EPConfiguration, transactionId:String, feedbackmessage:String ) {

        if(!ActivityUtil().isLoading){
        val json = JSONObject()
        json.put(STOREID, storeConfig.storeId)
        if(transactionId != null){
        json.put(TRANSACTIONID, transactionId)
        }
        else{
            json.put(TRANSACTIONID, "")
        }
        json.put(FEEDBACKTYPE, feedbackType)
        json.put(FEEDBACKMESSAGE, feedbackmessage)
        json.put(TRANSACTIONTYPE, "FB")

        var postData = ""

        if (!feedbackmessage.isNullOrEmpty()) {
            postData += FEEDBACKMESSAGE + ENCRYPT_KEY_EQUAL + feedbackmessage

            if (!feedbackType.isNullOrEmpty()){
                postData += ENCRYPT_KEY_AMPERSAND + FEEDBACKTYPE + ENCRYPT_KEY_EQUAL + feedbackType
            }

            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL  + "FB"

            if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
            }

            if(!transactionId.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + TRANSACTIONID + ENCRYPT_KEY_EQUAL + transactionId
            }
        }
        else{
            if (!feedbackType.isNullOrEmpty()){
                postData += FEEDBACKTYPE + ENCRYPT_KEY_EQUAL + feedbackType
            }

            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL  + "FB"

            if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
            }

            if(!transactionId.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + TRANSACTIONID + ENCRYPT_KEY_EQUAL + transactionId
            }
        }



        val encryptedRequest = Validation().encrypt(postData.toString(),storeConfig.secretKey)
        json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))

            if(InternetHelper().isInternetConnected(this)) {

            ActivityUtil().loadingStarted(progressBar, this)
            isLoading = true
            HttpTask(this)  {

                ActivityUtil().loadingStop(progressBar, this)
                isLoading = false
                if (it == null) {
                    Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show()
                    return@HttpTask
                } else {
                    val json = it.toString() // json value
                    val response = Gson().fromJson(json, FeedbackBaseResponse::class.java)

                    when (response.ackMessage.responseCode.toString().contentEquals("0")) {
                        true -> {
                            Toast.makeText(
                                this,
                                "" + response.ackMessage.responseDescription.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                            feedbackType=""
                            feedbackSubmited =true

                            if (feedbackSubmited) {

                                imgHappy.alpha = 0.5f
                                imgAverage.alpha = 0.5f
                                imgBad.alpha = 0.5f
                                titleHappy.setTextColor(resources.getColor(R.color.grey))
                                titleAverage.setTextColor(resources.getColor(R.color.grey))
                                titleBad.setTextColor(resources.getColor(R.color.grey))
                                imgHappy.setOnClickListener(null)
                                imgAverage.setOnClickListener(null)
                                imgBad.setOnClickListener(null)
                                ed_feedback.setText("")
                                ed_feedback.keyListener = null
                                ed_feedback.isEnabled = false



                                if (feedbackType.isNullOrEmpty()) {
                                    buttonSubmit.alpha = 0.5f
                                    buttonSubmit.isEnabled = false
                                } else if (!feedbackType.isNullOrEmpty()) {
                                    buttonSubmit.alpha = 1f
                                    buttonSubmit.isEnabled = true
                                }


                            }
                        }
                        false -> {
                            Toast.makeText(
                                this,
                                "" + response.ackMessage.responseDescription.toString(),
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }
                }

            }.execute(
                "POST",
                BASE_URL+"/checkout/feedback/initiate-transaction",
                json.toString()
            )

        }
        else{
            Toast.makeText(this, "Internet Not Available !", Toast.LENGTH_LONG).show()
        }}
    }

    private fun setStoreDetails(storeConfig: EPConfiguration, paymentOrder: EPPaymentOrder) {
        val orderID = findViewById<TextView>(R.id.tv_orderId)
        orderID.text = paymentOrder.orderId

        val storeName = findViewById<TextView>(R.id.tv_storeName)
        storeName.text = storeConfig.storeName

        val orderAmount = findViewById<TextView>(R.id.tv_totalAmount)
        orderAmount.text = paymentOrder.orderAmount.toString()

    }

    private fun setDonationDetails(storeConfig: EPConfiguration, paymentOrder: EPPaymentOrder) {
        val orderID = findViewById<TextView>(R.id.tv_orderId)
        orderID.text = orderId

        val storeName = findViewById<TextView>(R.id.tv_storeName)
        storeName.text = paymnentCharity.storeName

        val orderAmount = findViewById<TextView>(R.id.tv_totalAmount)
        orderAmount.text = paymnentCharity.orderAmount

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
        if (!isLoading) {
            super.onBackPressed()
        }
    }

    private fun generateOrderId(): String{

        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH) + 1  // Note: zero based!
        val monthUpdated =(if (month < 10) "0$month" else month)
        val day = now.get(Calendar.DAY_OF_MONTH)
        val dayUpdated =(if (day < 10) "0$day" else day)
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val hourUpdated =(if (hour < 10) "0$hour" else hour)
        val minute = now.get(Calendar.MINUTE)
        val minuteUpdated =(if (minute < 10) "0$minute" else minute)
        val second = now.get(Calendar.SECOND)
        val secondUpdated =(if (second < 10) "0$second" else second)


        val sumDate = year.toString()+ month.toString()+day.toString()+hour.toString()+minute.toString()+second.toString()

        val orderId = "ED"+sumDate
        return orderId
    }
}