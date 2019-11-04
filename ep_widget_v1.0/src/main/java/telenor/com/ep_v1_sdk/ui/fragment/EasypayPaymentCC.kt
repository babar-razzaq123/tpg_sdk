package telenor.com.ep_v1_sdk.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.ll_form_cc.*
import kotlinx.android.synthetic.main.ll_form_dd.*
import kotlinx.android.synthetic.main.ll_form_ma.*
import kotlinx.android.synthetic.main.ll_form_otc.*
import org.json.JSONObject
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.adapter.BankDropDownAdapter
import telenor.com.ep_v1_sdk.adapter.MonthDropDownAdapter
import telenor.com.ep_v1_sdk.adapter.YearDropDownAdapter
import telenor.com.ep_v1_sdk.config.*
import telenor.com.ep_v1_sdk.config.model.*
import telenor.com.ep_v1_sdk.rest.HttpTask
import telenor.com.ep_v1_sdk.ui.EasypayCall
import telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentForm
import telenor.com.ep_v1_sdk.util.ActivityUtil
import telenor.com.ep_v1_sdk.util.InternetHelper
import telenor.com.ep_v1_sdk.util.Validation
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.Handler
import android.text.InputType
import android.util.Base64
import android.widget.Toast
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_form_main.*
import kotlinx.android.synthetic.main.activity_otp.loading_spinner
import androidx.core.os.HandlerCompat.postDelayed
import com.google.android.material.snackbar.Snackbar
import telenor.com.ep_v1_sdk.ui.CardResponseCallback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EasypayPaymentCC(easypay: EasypayCall, cardResponseCallback: CardResponseCallback) : Fragment() {


    lateinit var storeConfig: EPConfiguration
    lateinit var paymentOrder :EPPaymentOrder
    lateinit var allowedPaymentMethods: AllowedPaymentMethods
    lateinit var merchantAccountId: String
    lateinit var cardResponse: PaymentCCCardResponse

    var easypayCall: EasypayCall = easypay
    var cardResponseCallback: CardResponseCallback = cardResponseCallback

    lateinit var expiryMonths:  ArrayList<ExpiryMonths>
    lateinit var expiryYears:  ArrayList<ExpiryYears>
    lateinit var expiryMonthsList: ArrayList<ExpiryMonths>
    lateinit var expiryYearsList: ArrayList<ExpiryYears>

    lateinit var month: String
    lateinit var year: String

    var isPhoneValid: Boolean = false
    var isEmailValid: Boolean = false
    var isCreditCardValid: Boolean = false
    var isMonthValid: Boolean = false
    var isYearValid: Boolean = false
    var isCVVValid: Boolean = false
//    var cardFocus: Boolean = false
    var isLoading : Boolean = false

    companion object {

        fun newInstance( easypayCall: EasypayCall, cardResponseCallback: CardResponseCallback): EasypayPaymentCC {
            return EasypayPaymentCC(easypayCall , cardResponseCallback)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storeConfig =  arguments?.get(CONFIGURATION) as EPConfiguration
        paymentOrder =  arguments?.get(PAYMENTORDER) as EPPaymentOrder
        allowedPaymentMethods =arguments?.get(PAYMENTTYPE) as AllowedPaymentMethods
        merchantAccountId =  arguments?.get(MERCHANTACCOUNTID) as String

        if (!storeConfig.bankIdentifier.toString().isNullOrEmpty()) {
            getCCLogo()
        }
        initCC(allowedPaymentMethods)
        ed_email_cc.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        ed_card_cc.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        //ed_cvv_cc.setInputType( InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

    }

    // inflate your view here
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.ll_form_cc, container, false)

    private fun initCC(allowedPaymentMethods: AllowedPaymentMethods) {

        expiryMonths = arguments?.get(EXPIRYMONTH) as ArrayList<ExpiryMonths>
         expiryYears = arguments?.get(EXPIRYYEAR) as ArrayList<ExpiryYears>


        var keyDel = false

        val params = cvvLL.layoutParams
        params.height = 125
        cvvLL.layoutParams = params

        ll_child_cc.visibility =View.VISIBLE
        listTitle_cc.text = getString(R.string.visa_master_card_txt)
        listTitle_cc.setTextColor(resources.getColor(R.color.green))

        tv_error_ed_mobile_cc.text = getString(R.string.invalidNumber)
        tv_error_ed_mobile_cc.visibility =View.INVISIBLE

        tv_error_ed_email_cc.text = getString(R.string.invalidEmail)
        tv_error_ed_email_cc.visibility =View.INVISIBLE

        tv_error_card_cc.text = getString(R.string.invalidCard)
        tv_error_card_cc.visibility =View.INVISIBLE

        tv_error_month_cc.text = getString(R.string.invalidExpDate)
        tv_error_month_cc.visibility =View.INVISIBLE

//        tv_error_year_cc.text = getString(R.string.invalidYear)
//        tv_error_year_cc.visibility =View.INVISIBLE

        tv_error_cvv_cc.text = getString(R.string.invalidCVV)
        tv_error_cvv_cc.visibility =View.INVISIBLE


        if(!paymentOrder.phone.isNullOrEmpty()){

            var appendedPhone:String =""
            val char ='-'

            if(Validation().isPhoneValid(paymentOrder.phone,11 ,"^(0)(3)[0-9]{9}\$")){
                if( paymentOrder.phone.length ==11){
                    var str = paymentOrder.phone
                    val first = str.subSequence(0,4)
                    val second = str.subSequence(4,7)
                    val third = str.subSequence(7,11)
                    appendedPhone = first.toString()+char+second+char+third


                    when(Validation().isPhoneValid(appendedPhone.toString(),13,"^(0)(3)[0-9]{2}-([0-9]{3})-([0-9]{4})\$")){
                        true->{
                            tv_error_ed_mobile_cc.visibility =View.INVISIBLE
                            isPhoneValid=true
                        }
                        false->{
                            tv_error_ed_mobile_cc.visibility =View.VISIBLE
                            isPhoneValid=false
                        }
                    }



                }
            }
            else if(Validation().isPhoneValid(paymentOrder.phone,13 ,"^[+](9)(2)(3)[0-9]{9}\$")){

                var phone = paymentOrder.phone.replace("+92","0")
                if( phone.length ==11){
                    var str = phone
                    val first = str.subSequence(0,4)
                    val second = str.subSequence(4,7)
                    val third = str.subSequence(7,11)
                    appendedPhone = first.toString()+char+second+char+third


                    when(Validation().isPhoneValid(appendedPhone.toString(),13,"^(0)(3)[0-9]{2}-([0-9]{3})-([0-9]{4})\$")){
                        true->{
                            tv_error_ed_mobile_cc.visibility =View.INVISIBLE
                            isPhoneValid=true
                        }
                        false->{
                            tv_error_ed_mobile_cc.visibility =View.VISIBLE
                            isPhoneValid=false
                        }
                    }



                }

            }
            // Below else is for New condition told by qasim
            else{
                var phone = paymentOrder.phone.replace("+92","0")
                if( phone.length ==11){
                    var str = phone
                    val first = str.subSequence(0,4)
                    val second = str.subSequence(4,7)
                    val third = str.subSequence(7,11)
                    appendedPhone = first.toString()+char+second+char+third


                    when(Validation().isPhoneValid(appendedPhone.toString(),13,"^(0)(3)[0-9]{2}-([0-9]{3})-([0-9]{4})\$")){
                        true->{
                            tv_error_ed_mobile_cc.visibility =View.INVISIBLE
                            isPhoneValid=true
                        }
                        false->{
                            tv_error_ed_mobile_cc.visibility =View.VISIBLE
                            isPhoneValid=false
                        }
                    }



                }
                else{

                    when(Validation().isPhoneValid(phone.toString(),13,"^(0)(3)[0-9]{2}-([0-9]{3})-([0-9]{4})\$")){
                        true->{
                            tv_error_ed_mobile_cc.visibility =View.INVISIBLE
                            isPhoneValid=true
                        }
                        false->{
                            tv_error_ed_mobile_cc.visibility =View.VISIBLE
                            isPhoneValid=false
                        }
                    }
                }
            }

            // Trying New below start

//            var appendedPhone:String =""
////            val char ='-'
//
//            if(Validation().isPhoneValid(paymentOrder.phone,11 ,"^(0)(3)[0-9]{9}\$")){
//                if( paymentOrder.phone.length ==11){
//                    var str = paymentOrder.phone
//                    val first = str.subSequence(0,4)
//                    val second = str.subSequence(4,7)
//                    val third = str.subSequence(7,11)
//                    appendedPhone = first.toString()/*+char*/+second/*+char*/+third
//
//
//                    when(Validation().isPhoneValid(appendedPhone.toString(),/*13*/11,"^(0)(3)[0-9]{2}([0-9]{3})([0-9]{4})\$")){
//                        true->{
//                            tv_error_ed_mobile_cc.visibility =View.INVISIBLE
//                            isPhoneValid=true
//                        }
//                        false->{
//                            tv_error_ed_mobile_cc.visibility =View.VISIBLE
//                            isPhoneValid=false
//                        }
//                    }
//
//
//
//                }
//            }
//            else if(Validation().isPhoneValid(paymentOrder.phone,13 ,"^[+](9)(2)(3)[0-9]{9}\$")){
//
//                var phone = paymentOrder.phone.replace("+92","0")
//                if( phone.length ==11){
//                    var str = phone
//                    val first = str.subSequence(0,4)
//                    val second = str.subSequence(4,7)
//                    val third = str.subSequence(7,11)
//                    appendedPhone = first.toString()/*+char*/+second/*+char*/+third
//
//
//                    when(Validation().isPhoneValid(appendedPhone.toString(),/*13*/11,"^(0)(3)[0-9]{2}([0-9]{3})([0-9]{4})\$")){
//                        true->{
//                            tv_error_ed_mobile_cc.visibility =View.INVISIBLE
//                            isPhoneValid=true
//                        }
//                        false->{
//                            tv_error_ed_mobile_cc.visibility =View.VISIBLE
//                            isPhoneValid=false
//                        }
//                    }
//
//
//
//                }
//
//            }
//            // Below else is for New condition told by qasim
//            else{
//                var phone = paymentOrder.phone.replace("+92","0")
//                if( phone.length ==11){
//                    var str = phone
//                    val first = str.subSequence(0,4)
//                    val second = str.subSequence(4,7)
//                    val third = str.subSequence(7,11)
//                    appendedPhone = first.toString()/*+char*/+second/*+char*/+third
//
//
//                    when(Validation().isPhoneValid(appendedPhone.toString(),/*13*/11,"^(0)(3)[0-9]{2}([0-9]{3})([0-9]{4})\$")){
//                        true->{
//                            tv_error_ed_mobile_cc.visibility =View.INVISIBLE
//                            isPhoneValid=true
//                        }
//                        false->{
//                            tv_error_ed_mobile_cc.visibility =View.VISIBLE
//                            isPhoneValid=false
//                        }
//                    }
//
//
//
//                }
//                else{
//
//                    when(Validation().isPhoneValid(phone.toString(),/*13*/11,"^(0)(3)[0-9]{2}([0-9]{3})([0-9]{4})\$")){
//                        true->{
//                            tv_error_ed_mobile_cc.visibility =View.INVISIBLE
//                            isPhoneValid=true
//                        }
//                        false->{
//                            tv_error_ed_mobile_cc.visibility =View.VISIBLE
//                            isPhoneValid=false
//                        }
//                    }
//                }
//            }

            // Trying New below end


            ed_mobile_cc.setText(appendedPhone)

        }
        ed_mobile_cc.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {


            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                // Previous When block
//                Validation().isPhoneValid(s.toString(),13,"^(0)(3)[0-9]{2}-([0-9]{3})-([0-9]{4})\$")
                when(Validation().isPhoneValid(s.toString(),13,"^(0)(3)[0-9]{2}-([0-9]{3})-([0-9]{4})\$")){
                    true->{
                        tv_error_ed_mobile_cc.visibility =View.INVISIBLE
                        isPhoneValid=true
                    }
                    false->{
                        tv_error_ed_mobile_cc.visibility =View.VISIBLE
                        isPhoneValid=false
                    }
                }
                if(isEmailValid && isPhoneValid){
                    buttonPay_cc.alpha =1f
                }
                else if(!isEmailValid || !isPhoneValid){
                    buttonPay_cc.alpha=0.5f
                }
            }
        })






        if(!paymentOrder.email.isEmpty()){
            ed_email_cc.setText(paymentOrder.email)
            when(Validation().isEmailValid(paymentOrder.email.toString())){
                true->{
                    tv_error_ed_email_cc.visibility =View.INVISIBLE
                    isEmailValid=true
                }
                false->{
                    tv_error_ed_email_cc.visibility =View.VISIBLE
                    isEmailValid=false
                }
            }
        }

        ed_email_cc.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                when(Validation().isEmailValid(s.toString())){
                    true->{
                        tv_error_ed_email_cc.visibility =View.INVISIBLE
                        isEmailValid=true
                    }
                    false->{
                        tv_error_ed_email_cc.visibility =View.VISIBLE
                        isEmailValid=false
                    }
                }
                if(isEmailValid && isPhoneValid && isCreditCardValid && isMonthValid && isYearValid && isCVVValid){
                    buttonPay_cc.alpha =1f
                }
                else if(!isEmailValid || !isPhoneValid || !isCreditCardValid || !isMonthValid || !isYearValid || !isCVVValid){
                    buttonPay_cc.alpha=0.5f
                }
            }
        })

        ed_card_cc.addTextChangedListener(object : TextWatcher {

            private val TOTAL_SYMBOLS = 19 // size of pattern 0000-0000-0000-0000
            private val TOTAL_DIGITS = 16 // max numbers of digits in pattern: 0000 x 4
            private val DIVIDER_MODULO = 5 // means divider position is every 5th symbol beginning with 1
            private val DIVIDER_POSITION =
                DIVIDER_MODULO - 1 // means divider position is every 4th symbol beginning with 0
            private val DIVIDER = '-'

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // noop
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // noop
            }

            override fun afterTextChanged(s: Editable) {

//                cardFocus = true
                if (s.startsWith("4", false)){
                    imgCard_cc.visibility = View.VISIBLE
                    imgCard_cc.setImageResource(R.drawable.ic_visa_cc)
                }
                else if (s.startsWith("5", false)){
                    imgCard_cc.visibility = View.VISIBLE
                    imgCard_cc.setImageResource(R.drawable.ic_mastercard_cc)
                }
                else{
                    imgCard_cc.visibility = View.GONE
                }

                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0,s.length,buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER) )

                }
                if(Validation().validCreditCard(s.toString(),"^\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}\$",19)){
                    tv_error_card_cc.visibility=View.INVISIBLE
//                    isCreditCardValid =true
                    verifyCardNumber()
                }
                else{
                    tv_error_card_cc.visibility=View.VISIBLE
                    isCreditCardValid =false
                }

                if(isEmailValid && isPhoneValid && isCreditCardValid && isMonthValid && isYearValid && isCVVValid){
                    buttonPay_cc.alpha =1f
                }
                else if(!isEmailValid || !isPhoneValid || !isCreditCardValid || !isMonthValid || !isYearValid || !isCVVValid){
                    buttonPay_cc.alpha=0.5f
                }
            }

            private fun isInputCorrect(s: Editable, totalSymbols: Int, dividerModulo: Int, divider: Char): Boolean {
                var isCorrect = s.length <= totalSymbols // check size of entered string
                for (i in 0 until s.length) { // check that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect = isCorrect and (divider == s[i])
                    } else {
                        isCorrect = isCorrect and Character.isDigit(s[i])
                    }
                }
                return isCorrect
            }

            private fun buildCorrectString(digits: CharArray, dividerPosition: Int, divider: Char): String {
                val formatted = StringBuilder()

                for (i in digits.indices) {
                    if (digits[i].toInt() != 0) {
                        formatted.append(digits[i])
                        if (i > 0 && i < digits.size - 1 && (i + 1) % dividerPosition == 0) {
                            formatted.append(divider)
                        }
                    }
                }

                return formatted.toString()
            }

            private fun getDigitArray(s: Editable, size: Int): CharArray {
                val digits = CharArray(size)
                var index = 0
                var i = 0
                while (i < s.length && index < size) {
                    val current = s[i]
                    if (Character.isDigit(current)) {
                        digits[index] = current
                        index++
                    }
                    i++
                }
                return digits
            }
        })

        /*ed_card_cc.setOnFocusChangeListener { v, hasFocus ->

            if (!hasFocus){
                var cardNumber = ed_card_cc.text!!.toString()//.replace("-", "")

                if(Validation().validCreditCard(cardNumber,"^\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}\$",19)){
//                    tv_error_card_cc.visibility=View.INVISIBLE
                    //isCreditCardValid =true
                    verifyCardNumber()
                }
            }
        }*/


        expiryMonthsList = ArrayList<ExpiryMonths>()
        expiryYearsList = ArrayList<ExpiryYears>()
        val defaultMonthExpiry = ExpiryMonths("-1","Month" , "")

        //expiryMonthsList.add(defaultMonthExpiry)
        for ( expiryMonth in expiryMonths){
            if(!expiryMonth.value.isNullOrEmpty())
            expiryMonthsList.add(expiryMonth)
        }
        val defaultYearExpiry = ExpiryYears("-1","Year" , "")

       // expiryYearsList.add(defaultYearExpiry)
        for ( expiryYear in expiryYears){
            if(!expiryYear.value.isNullOrEmpty())
            expiryYearsList.add(expiryYear)
        }

        var spinnerMonthAdapter: MonthDropDownAdapter = MonthDropDownAdapter(context as EasypayPaymentForm, expiryMonthsList)
        // Set Adapter to Spinner
        spinnerMonth_cc!!.adapter = spinnerMonthAdapter


        var spinnerYearAdapter: YearDropDownAdapter = YearDropDownAdapter(context as EasypayPaymentForm, expiryYearsList)
        // Set Adapter to Spinner
        spinnerYear_cc!!.adapter = spinnerYearAdapter

//        spinnerYear_cc.setOnTouchListener { v, event ->
//
//            if (cardFocus){
//                ed_card_cc.clearFocus()
//            }
//            false
//        }
//        spinnerMonth_cc.setOnTouchListener(View.OnTouchListener { v, event ->
//            if (cardFocus){
//                ed_card_cc.clearFocus()
//            }
//            false
//        })

        spinnerMonth_cc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

//                if (cardFocus){
//                    ed_card_cc.isFocusable = false
//                    ed_card_cc.isFocusable = true
//                }
                isMonthValid = position!=0
                if(isMonthValid){

                    month= expiryMonthsList.get(position).key
                    if(isYearValid){
                        if(Validation().isValidDate(month, year))
                        {

                            month= expiryMonthsList.get(position).key
                            tv_error_month_cc.visibility =View.INVISIBLE
                            isMonthValid=true

//                            tv_error_year_cc.visibility =View.INVISIBLE
                            isYearValid=true
                        }
                        else
                        {
                            tv_error_month_cc.visibility =View.VISIBLE
                            isMonthValid=false
                        }
                    }
                    else{
//                        tv_error_year_cc.visibility =View.VISIBLE
                        tv_error_month_cc.visibility =View.VISIBLE
                        isYearValid=false
                    }

                }
                else{

                    buttonPay_cc.alpha=0.5f
                    isMonthValid=false
                }

                if(isEmailValid && isPhoneValid && isCreditCardValid && isMonthValid && isYearValid && isCVVValid){
                    buttonPay_cc.alpha =1f
                }
                else if(!isEmailValid || !isPhoneValid || !isCreditCardValid || !isMonthValid || !isYearValid || !isCVVValid){
                    buttonPay_cc.alpha=0.5f
                }


            }



            override fun onNothingSelected(parent: AdapterView<*>) {

                buttonPay_cc.alpha=0.5f
            }
        }


        spinnerYear_cc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

//                if (cardFocus){
//                    ed_card_cc.isFocusable = false
//                    ed_card_cc.isFocusable = true
//                }
                isYearValid = position!=0
                if(isYearValid){
                    year= expiryYearsList.get(position).value
                    if ((spinnerMonth_cc.selectedItemPosition) != 0) {
                        month = expiryMonthsList.get((spinnerMonth_cc.selectedItemPosition)).value

                        if(Validation().isValidDate(month, year))
                        {

//                        month= expiryMonthsList.get(position).key
                            tv_error_month_cc.visibility =View.INVISIBLE
                            isMonthValid=true

//                            tv_error_year_cc.visibility =View.INVISIBLE
                        }
                        else
                        {
                            tv_error_month_cc.visibility =View.VISIBLE
                            isMonthValid=false
                        }
                        if(isMonthValid){
                            if(Validation().isValidDate(month, year))
                            {

                                year= expiryYearsList.get(position).value
//                            tv_error_year_cc.visibility =View.INVISIBLE
                                tv_error_month_cc.visibility =View.INVISIBLE
                                isYearValid=true


                            }
                            else
                            {
//                            tv_error_year_cc.visibility =View.VISIBLE
                                tv_error_month_cc.visibility =View.VISIBLE
                                isYearValid=true
                            }
                        }
                        else{
                            tv_error_month_cc.visibility =View.VISIBLE
                            isMonthValid=false
                        }
                    }
                    else{
                        tv_error_month_cc.visibility =View.VISIBLE
                        isMonthValid=false
                    }



                }
                else{

                    buttonPay_cc.alpha=0.5f
                    isYearValid=false
                }

                if(isEmailValid && isPhoneValid && isCreditCardValid && isMonthValid && isYearValid && isCVVValid){
                    buttonPay_cc.alpha =1f
                }
                else if(!isEmailValid || !isPhoneValid || !isCreditCardValid || !isMonthValid || !isYearValid || !isCVVValid){
                    buttonPay_cc.alpha=0.5f
                }


            }



            override fun onNothingSelected(parent: AdapterView<*>) {

                buttonPay_cc.alpha=0.5f
            }
        }
        /*edExpiryMonth_cc.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                if(Validation().isValidMonth(s.toString(),"^(01|02|03|04|05|06|07|08|09|10|11|12)\$",2)){
                    if(isYearValid){
                        if(Validation().isValidDate(edExpiryMonth_cc.text.toString(), edExpiryYear_cc.text.toString()))
                        {
                        tv_error_month_cc.visibility =View.INVISIBLE
                        isMonthValid=true
                        }
                        else
                        {
                            tv_error_month_cc.visibility =View.VISIBLE
                            isMonthValid=false
                        }
                        }
                        else{
                        tv_error_month_cc.visibility =View.VISIBLE
                        isMonthValid=false
                    }

                }
                else{
                    tv_error_month_cc.visibility =View.VISIBLE
                    isMonthValid=false
                }

                if(isEmailValid && isPhoneValid && isCreditCardValid && isMonthValid && isYearValid && isCVVValid){
                    buttonPay_cc.alpha =1f
                }
                else if(!isEmailValid || !isPhoneValid || !isCreditCardValid || !isMonthValid || !isYearValid || !isCVVValid){
                    buttonPay_cc.alpha=0.5f
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        edExpiryYear_cc.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                if(Validation().isValidYear(s.toString(),"^([1-2][0-9][0-9][0-9])\$",4)){

                    if(Validation().isYearValid(edExpiryYear_cc.text.toString())){
                        tv_error_year_cc.visibility =View.INVISIBLE
                        isYearValid=true

                        if(!edExpiryMonth_cc.text.isNullOrEmpty()){

                            if(Validation().isValidMonth(edExpiryMonth_cc.text.toString(),"^(01|02|03|04|05|06|07|08|09|10|11|12)\$",2)){
                                if(isYearValid){
                                    if(Validation().isValidDate(edExpiryMonth_cc.text.toString(), edExpiryYear_cc.text.toString()))
                                    {
                                        tv_error_month_cc.visibility =View.INVISIBLE
                                        isMonthValid=true
                                    }
                                    else
                                    {
                                        tv_error_month_cc.visibility =View.VISIBLE
                                        isMonthValid=false
                                    }
                                }
                                else{
                                    tv_error_month_cc.visibility =View.VISIBLE
                                    isMonthValid=false
                                }

                            }
                            else{
                                tv_error_month_cc.visibility =View.VISIBLE
                                isMonthValid=false
                            }
                        }
                    }
                    else{
                        tv_error_year_cc.visibility =View.VISIBLE
                        isYearValid =false
                    }

                }
                else{
                    tv_error_year_cc.visibility =View.VISIBLE
                    isYearValid =false
                }

                if(isEmailValid && isPhoneValid && isCreditCardValid && isMonthValid && isYearValid && isCVVValid){
                    buttonPay_cc.alpha =1f
                }
                else if(!isEmailValid || !isPhoneValid || !isCreditCardValid || !isMonthValid || !isYearValid || !isCVVValid){
                    buttonPay_cc.alpha=0.5f
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })*/


        ed_cvv_cc.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                if(Validation().isValidCVV(s.toString())){
                    tv_error_cvv_cc.visibility =View.INVISIBLE
                    isCVVValid=true
                }
                else{
                    tv_error_cvv_cc.visibility =View.VISIBLE
                    isCVVValid=false
                }

                if(isEmailValid && isPhoneValid && isCreditCardValid && isMonthValid && isYearValid && isCVVValid){
                    buttonPay_cc.alpha =1f
                }
                else if(!isEmailValid || !isPhoneValid || !isCreditCardValid || !isMonthValid || !isYearValid || !isCVVValid){
                    buttonPay_cc.alpha=0.5f
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        if(isEmailValid && isPhoneValid && isCreditCardValid && isMonthValid && isYearValid && isCVVValid){
            buttonPay_cc.alpha =1f
        }
        else if(!isEmailValid || !isPhoneValid || !isCreditCardValid || !isMonthValid || !isYearValid || !isCVVValid){
            buttonPay_cc.alpha=0.5f
        }
        buttonPay_cc.setOnClickListener {
            if(isPhoneValid){

                buttonPay_cc.alpha=1f
                tv_error_ed_mobile_cc.visibility=View.INVISIBLE
                if( isEmailValid){
                    tv_error_ed_email_cc.visibility=View.INVISIBLE
                    buttonPay_cc.alpha=1f

                    if(isCreditCardValid){
                        buttonPay_cc.alpha=1f
//                        tv_error_card_cc.visibility=View.INVISIBLE


                        if(isMonthValid){
                            buttonPay_cc.alpha=1f
                            tv_error_month_cc.visibility=View.INVISIBLE

                            if(isYearValid) {
                                buttonPay_cc.alpha=1f
//                                tv_error_year_cc.visibility=View.INVISIBLE

                                if(isCVVValid){

                                    buttonPay_cc.alpha=1f
                                    tv_error_cvv_cc.visibility=View.INVISIBLE

                                    paymentOrder.email = ed_email_cc.text!!.toString()
                                    paymentOrder.phone = ed_mobile_cc.text!!.toString().replace("-", "")

                                    paymentOrder.creditCard = ed_card_cc.text!!.toString().replace("-", "")
                                    paymentOrder.creditCardMonth =  month!!.toString()
                                    paymentOrder.creditCardYear =  year!!.toString().substring(2,4)

                                    paymentOrder.creditCardCVV = ed_cvv_cc.text!!.toString()

                                    cardResponseCallback.cardResponse(cardResponse)
                                    easypayCall.paymentMethod(storeConfig, paymentOrder, allowedPaymentMethods)
//                                    paymentCC(storeConfig, paymentOrder, allowedPaymentMethods)
                                }
                                else{

                                    buttonPay_cc.alpha=0.5f
                                    tv_error_cvv_cc.visibility=View.VISIBLE
                                }

                            }
                            else{

                                buttonPay_cc.alpha=0.5f
//                                tv_error_year_cc.visibility=View.VISIBLE
                            }
                        }
                        else{
                            buttonPay_cc.alpha=0.5f
                            tv_error_month_cc.visibility=View.VISIBLE
                        }
                    }
                    else{
                        buttonPay_cc.alpha=0.5f
//                        tv_error_card_cc.visibility=View.VISIBLE
                    }
                }
                else{

                    buttonPay_cc.alpha=0.5f
                    tv_error_ed_email_cc.visibility=View.VISIBLE
                }
            }
            else{
                buttonPay_cc.alpha=0.5f
                tv_error_ed_mobile_cc.visibility=View.VISIBLE
            }
        }

        ivMobileTitle_cc.setOnClickListener {

            //ActivityUtil().hideKeyboard(activity as EasypayPaymentForm)
            Snackbar.make(ll_child_cc , getString(R.string.cc_mobile_hint), Snackbar.LENGTH_SHORT).show()
        }

        ivEmailTitle_cc.setOnClickListener {

            //ActivityUtil().hideKeyboard(activity as EasypayPaymentForm)
            Snackbar.make(ll_child_cc , getString(R.string.cc_email_hint), Snackbar.LENGTH_SHORT).show()
        }

        ivBankTitle_cc.setOnClickListener {

            //ActivityUtil().hideKeyboard(activity as EasypayPaymentForm)
            Snackbar.make(ll_child_cc , getString(R.string.cc_card_number_hint), Snackbar.LENGTH_SHORT).show()
        }

        ivCVVTitle_cc.setOnClickListener {

            //ActivityUtil().hideKeyboard(activity as EasypayPaymentForm)
            Snackbar.make(ll_child_cc , getString(R.string.cc_cvv_hint), Snackbar.LENGTH_SHORT).show()
        }

    }

    public fun disablePayNowBtn(){

        buttonPay_cc.alpha = 0.5f
        buttonPay_cc.isClickable = false
    }

    public fun enablePayNowBtn(){

        buttonPay_cc.alpha = 1f
        buttonPay_cc.isClickable = true
    }



    private fun verifyCardNumber(){

        if (!ActivityUtil().isLoading) {

            var cardNumber = ed_card_cc.text!!.toString().replace("-", "")

            val cardNumberr = Validation().encrypt(cardNumber, storeConfig.secretKey)
            val  encrytedCardNumber =  cardNumberr.replace("\n","")


            val json = JSONObject()
            json.put(MERCHANTACCOUNTID, merchantAccountId)
            json.put(CARDNUMBER, encrytedCardNumber)
            json.put(STOREID, storeConfig.storeId)
            json.put(TRANSACTIONTYPE, "CC_PREVALIDATION")

            var postData = ""

            if (!encrytedCardNumber.isNullOrEmpty()) {
                postData += ENCRYPT_CC_NUM + ENCRYPT_KEY_EQUAL + encrytedCardNumber
            }

            if (!merchantAccountId.isNullOrEmpty()){
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_MERCHANT_ACCOUNT_ID + ENCRYPT_KEY_EQUAL + merchantAccountId
            }

            postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_PAYMENTMETHOD + ENCRYPT_KEY_EQUAL  + "CC_PREVALIDATION"

            if(!storeConfig.storeId.toString().isNullOrEmpty()) {
                postData += ENCRYPT_KEY_AMPERSAND + ENCRYPT_STOREID + ENCRYPT_KEY_EQUAL + storeConfig.storeId.toLong()
            }


            val encryptedRequest = Validation().encrypt(postData.toString(),storeConfig.secretKey)
            json.put(ENCRYPTEDREQUEST,  encryptedRequest.replace("\n",""))


            if (InternetHelper().isInternetConnected(activity as EasypayPaymentForm)) {
//                ActivityUtil().loadingStarted(loading_spinner, activity as EasypayPaymentForm)
                HttpTask(activity as EasypayPaymentForm) {

//                    ActivityUtil().loadingStop(loading_spinner, activity as EasypayPaymentForm)
                    if (it == null) {
                        Toast.makeText(activity as EasypayPaymentForm, "Something went Wrong", Toast.LENGTH_LONG).show()
                        //(activity as EasypayPaymentForm).finish()
                        return@HttpTask
                    } else {
                        var json = it.toString() // json value
                        if (!json.contains("\"responseCode\":\"0\"")){
                            json = json.replace("\"content\":\"\",", "").replace("\"responseCode\":\"\"", "\"responseCode\":\"-1\"")
                        }
                        val response = Gson().fromJson(json, PaymentCCCardResponse::class.java)
                        cardResponse = response

                            if (response.content != null) {


                                if (response.ackMessage.responseCode.toString().contentEquals("0000")){

                                    if (response.content.binCheckReasonCode.contentEquals("SB") || response.content.binCheckReasonCode.toString().isEmpty()){

                                        if (!response.content.binCheckReasonCode.isEmpty()) {
                                            showDialog(response)
                                        }
                                        isCreditCardValid = true

                                    }
                                    else if (response.content.binCheckReasonCode.contentEquals("BLBEP") ||
                                        response.content.binCheckReasonCode.contentEquals("BBI") ||
                                        response.content.binCheckReasonCode.contentEquals("SC") ||
                                        response.content.binCheckReasonCode.contentEquals("BLEPML")){

                                        if (!response.content.binCheckReasonCode.isEmpty()) {
                                            showDialog(response)
                                        }
                                        isCreditCardValid = false
                                    }
                                }
                            }
                            else{
                                if (response.ackMessage != null) {
                                    showDialog(response)
                                    isCreditCardValid = false
                                }
                                    isCreditCardValid = false
                            }


                        if(isEmailValid && isPhoneValid && isCreditCardValid && isMonthValid && isYearValid && isCVVValid){
                            buttonPay_cc.alpha =1f
                        }
                        else if(!isEmailValid || !isPhoneValid || !isCreditCardValid || !isMonthValid || !isYearValid || !isCVVValid){
                            buttonPay_cc.alpha=0.5f
                        }
                    }

                }.execute(
                    POST,
                    BASE_URL + CC_PRE_VALIDATION_ENDPOINT,
                    json.toString()
                )

            } else {
                Toast.makeText(activity as EasypayPaymentForm, "Internet Not Available !", Toast.LENGTH_LONG).show()
                //finish()
            }
        }
    }


    private fun getCCLogo() {
        if(InternetHelper().isInternetConnected(activity as EasypayPaymentForm)) {

            val json = JSONObject()
            if (!storeConfig.bankIdentifier.toString().isNullOrEmpty()) {
                json.put(BANKIDNUMBER, storeConfig.bankIdentifier)
            }

            HttpTask(activity as EasypayPaymentForm) {
                if (it == null) {
                    Toast.makeText(activity as EasypayPaymentForm, "Something went Wrong", Toast.LENGTH_LONG).show()
                    return@HttpTask
                } else {
                    val json = it.toString() // json value
                    val response = Gson().fromJson(json, CCLogoResponse::class.java)

                    when (response.ackMessage.responseCode.toString().contentEquals("0000")) {
                        true -> {
                            imgBank_cc.visibility = View.VISIBLE
                            var commaIndex = response.content.bankContent.indexOf(",")
                            var base64 = response.content.bankContent.substring(commaIndex+1, response.content.bankContent.length)
                            val decodedString = Base64.decode(base64, Base64.DEFAULT)
                            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                            imgBank_cc.setImageBitmap(decodedByte)

                        }
                        false -> {
                            imgBank_cc.visibility = View.GONE
                        }
                    }
                }

            }.execute(
                "POST",
                BASE_URL+"/checkout/cc/initiate-cc",
                json.toString()
            )

        }
        else{
            Toast.makeText(activity as EasypayPaymentForm, "Internet Not Available !", Toast.LENGTH_LONG).show()
        }
    }


    private fun showDialog(response: PaymentCCCardResponse){

        // Initialize a new instance of
        val builder = AlertDialog.Builder(activity as EasypayPaymentForm)

        // Set the alert dialog title
        if (response.content != null) {
            builder.setTitle(response.content.binCheckReason)

            // Display a message on alert dialog
            var notificationMsg = response.content.binCheckNotification.replace("\r\n", " ").replace("\n", " ")
            builder.setMessage(notificationMsg)
        }
        else{
//            builder.setTitle("Black listed by EP")
            builder.setMessage(response.ackMessage.responseDescription)
        }
        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Close"){dialog, which ->

            dialog.dismiss()

        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()

    }

    fun setError (errorMsg : String){

//        var sc =  view?.findViewById(R.id.scrollViewCC) as ScrollView

        Handler().postDelayed({
            scrollViewCC.scrollTo(0,0)
            tv_failure_cc.visibility = View.VISIBLE
            tv_failure_cc.text = errorMsg
        }, 150)

    }




}