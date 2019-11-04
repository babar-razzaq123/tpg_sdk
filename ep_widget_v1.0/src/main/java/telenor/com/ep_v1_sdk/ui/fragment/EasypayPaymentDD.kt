package telenor.com.ep_v1_sdk.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.ll_form_cc.*
import kotlinx.android.synthetic.main.ll_form_dd.*
import kotlinx.android.synthetic.main.ll_form_dd.*
import kotlinx.android.synthetic.main.ll_form_ma.*
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.adapter.BankDropDownAdapter
import telenor.com.ep_v1_sdk.adapter.PaymentModeDropDownAdapter
import telenor.com.ep_v1_sdk.config.*
import telenor.com.ep_v1_sdk.config.model.*
import telenor.com.ep_v1_sdk.ui.EasypayCall
import telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentForm
import telenor.com.ep_v1_sdk.util.ActivityUtil
import telenor.com.ep_v1_sdk.util.Validation

class EasypayPaymentDD(easypay: EasypayCall) : Fragment() {


    lateinit var storeConfig: EPConfiguration
    lateinit var paymentOrder :EPPaymentOrder
    lateinit var allowedPaymentMethods: AllowedPaymentMethods
    lateinit var directDebitBank: ArrayList<DirectDebitBank>
    lateinit var bankList: ArrayList<DirectDebitBank>
    lateinit var instList: ArrayList<Instrument>
    var bankId: Int =0
    var paymentId: Int =0
    var easypayCall: EasypayCall = easypay
    var typeSelected : String =""
    companion object {

        fun newInstance( easypayCall: EasypayCall): EasypayPaymentDD {
            return EasypayPaymentDD(easypayCall)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    // inflate your view here
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.ll_form_dd, container, false)


    private fun initDD(allowedPaymentMethods: AllowedPaymentMethods, savedInstanceState: Bundle?) {

        var isPhoneValid: Boolean = false
        var isEmailValid: Boolean = false
        var isBankSelected: Boolean = false
        var isPaymentSelected: Boolean = false
        var isCNICValid: Boolean = false
        var isAccountNumberSelected: Boolean = false
        var isAccountValid: Boolean = false
        var isMobileNumberSelected: Boolean = false
        var isMobileValid: Boolean = false
        var isCardNumberSelected: Boolean = false
        var isCardValid: Boolean = false

        directDebitBank = arguments?.get(DIRECTDEBITBANK) as ArrayList<DirectDebitBank>

        ll_child_dd.visibility =View.VISIBLE

        listTitle_dd.text = allowedPaymentMethods.description
        listTitle_dd.setTextColor(ContextCompat.getColor(activity as EasypayPaymentForm, R.color.green))

        tv_error_ed_mobile_dd.text = getString(R.string.invalidNumber)
        tv_error_ed_mobile_dd.visibility =View.INVISIBLE


        tv_error_ed_mobileaccount_dd.text = getString(R.string.invalidNumber)
        tv_error_ed_mobileaccount_dd.visibility =View.INVISIBLE


        tv_error_ed_email_dd.text = getString(R.string.invalidEmail)
        tv_error_ed_email_dd.visibility =View.INVISIBLE

        tv_error_bank_dd.text = getString(R.string.selectBank)
        tv_error_bank_dd.visibility =View.INVISIBLE

        tv_error_payment_mode_dd.text = getString(R.string.selectPayment)
        tv_error_payment_mode_dd.visibility =View.INVISIBLE

        tv_error_ed_account_dd.text = getString(R.string.invalidAccount)
        tv_error_ed_account_dd.visibility =View.INVISIBLE


        tv_error_ed_cnic_dd.text = getString(R.string.invalidCNIC)
        tv_error_ed_cnic_dd.visibility =View.INVISIBLE


        if(isEmailValid && isPhoneValid && isBankSelected && isPaymentSelected && isCNICValid){
            if(isAccountNumberSelected){
                if(isAccountValid){

                    buttonPay_dd.alpha =1f
                }else{
                    buttonPay_dd.alpha=0.5f

                }
            }
            if(isMobileNumberSelected){
                if(isMobileValid){

                    buttonPay_dd.alpha =1f

                }else{
                    buttonPay_dd.alpha=0.5f

                }
            }

            if(isCardNumberSelected){
                if(isCardValid){

                    buttonPay_dd.alpha =1f

                }else{
                    buttonPay_dd.alpha=0.5f

                }
            }
        }
        else if(!isEmailValid || !isPhoneValid || !isBankSelected || !isPaymentSelected || !isCNICValid){
            buttonPay_dd.alpha=0.5f

        }

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
                            tv_error_ed_mobile_dd.visibility =View.INVISIBLE
                            isPhoneValid=true
                        }
                        false->{
                            tv_error_ed_mobile_dd.visibility =View.VISIBLE
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
                            tv_error_ed_mobile_dd.visibility =View.INVISIBLE
                            isPhoneValid=true
                        }
                        false->{
                            tv_error_ed_mobile_dd.visibility =View.VISIBLE
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
                            tv_error_ed_mobile_dd.visibility =View.INVISIBLE
                            isPhoneValid=true
                        }
                        false->{
                            tv_error_ed_mobile_dd.visibility =View.VISIBLE
                            isPhoneValid=false
                        }
                    }



                }
                else{

                    when(Validation().isPhoneValid(phone.toString(),13,"^(0)(3)[0-9]{2}-([0-9]{3})-([0-9]{4})\$")){
                        true->{
                            tv_error_ed_mobile_dd.visibility =View.INVISIBLE
                            isPhoneValid=true
                        }
                        false->{
                            tv_error_ed_mobile_dd.visibility =View.VISIBLE
                            isPhoneValid=false
                        }
                    }
                }
            }
            ed_mobile_dd.setText(appendedPhone)

        }
        ed_mobile_dd.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {


            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {


                when(Validation().isPhoneValid(s.toString(),13,"^(0)(3)[0-9]{2}-([0-9]{3})-([0-9]{4})\$")){
                    true->{
                        tv_error_ed_mobile_dd.visibility =View.INVISIBLE
                        isPhoneValid=true
                    }
                    false->{
                        tv_error_ed_mobile_dd.visibility =View.VISIBLE
                        isPhoneValid=false
                    }
                }


                if(isEmailValid && isPhoneValid && isBankSelected && isPaymentSelected && isCNICValid){
                    if(isAccountNumberSelected){
                        if(isAccountValid){

                            buttonPay_dd.alpha =1f
                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }
                    if(isMobileNumberSelected){
                        if(isMobileValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }

                    if(isCardNumberSelected){
                        if(isCardValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }
                }
                else if(!isEmailValid || !isPhoneValid || !isBankSelected || !isPaymentSelected || !isCNICValid){
                    buttonPay_dd.alpha=0.5f

                }
            }
        })


        if(!paymentOrder.email.isEmpty()){
            ed_email_dd.setText(paymentOrder.email)
            when(Validation().isEmailValid(paymentOrder.email.toString())){
                true->{
                    tv_error_ed_email_dd.visibility =View.INVISIBLE
                    isEmailValid=true
                }
                false->{
                    tv_error_ed_email_dd.visibility =View.VISIBLE
                    isEmailValid=false
                }
            }
        }

        ed_email_dd.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                when(Validation().isEmailValid(s.toString())){
                    true->{
                        tv_error_ed_email_dd.visibility =View.INVISIBLE
                        isEmailValid=true
                    }
                    false->{
                        tv_error_ed_email_dd.visibility =View.VISIBLE
                        isEmailValid=false
                    }
                }

                if(isEmailValid && isPhoneValid && isBankSelected && isPaymentSelected && isCNICValid){
                    if(isAccountNumberSelected){
                        if(isAccountValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }
                    if(isMobileNumberSelected){
                        if(isMobileValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }

                    if(isCardNumberSelected){
                        if(isCardValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }
                }
                else if(!isEmailValid || !isPhoneValid || !isBankSelected || !isPaymentSelected || !isCNICValid){
                    buttonPay_dd.alpha=0.5f

                }

            }
        })
        bankList = ArrayList<DirectDebitBank>()
        instList = ArrayList<Instrument>()
        val defaultBank = DirectDebitBank(-1,"Select Bank" , instList)

        bankList.add(defaultBank)
        for ( directDebitBank in directDebitBank){
            if (directDebitBank.bankId != 0)
                bankList.add(directDebitBank)
        }



        var spinnerBankAdapter: BankDropDownAdapter = BankDropDownAdapter(context as EasypayPaymentForm, bankList)
        // Set Adapter to Spinner
        spinnerBank_dd!!.adapter = spinnerBankAdapter

        spinnerBank_dd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {


                 isAccountNumberSelected = false
                 isMobileNumberSelected = false
                 isCardNumberSelected= false

                if(position!=0){
                    ed_account_dd.text!!.clear()
                    ed_mobile_account_dd.text!!.clear()
                    ed_card_dd.text!!.clear()

                    if (savedInstanceState != null){
                        ed_account_dd.setText(savedInstanceState.getString("enteredAccountNumber", ""))
                    }
                    if (savedInstanceState != null){
                        ed_mobile_account_dd.setText(savedInstanceState.getString("enteredWalletNumber", ""))
                    }
                    if (savedInstanceState != null){
                        ed_card_dd.setText(savedInstanceState.getString("enteredCardNumber", ""))
                    }

                    buttonPay_dd.alpha=1f
                    tv_error_bank_dd.visibility=View.INVISIBLE
                    bankId= bankList.get(position).bankId
                    instList = ArrayList<Instrument>()
                    val defaultIns = Instrument(-1,"Select Payment Method" )
                    instList.add(defaultIns)
                    if (!bankList.get(position).instrument.isNullOrEmpty()) {
                        for (inst in bankList.get(position).instrument) {
                            instList.add(inst)
                        }
                    }
                    var spinnerPaymentAdapter: PaymentModeDropDownAdapter = PaymentModeDropDownAdapter( context as EasypayPaymentForm, instList)
                    // Set Adapter to Spinner
                    spinnerPaymentMode_dd!!.adapter = spinnerPaymentAdapter
                    spinnerPaymentMode_dd.isEnabled=true

                    spinnerPaymentMode_dd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {


                            isBankSelected =true
                            tv_error_bank_dd.visibility=View.INVISIBLE
                            isPaymentSelected = position!=0
                            if(isPaymentSelected){
                                tv_error_payment_mode_dd.visibility=View.INVISIBLE
                                paymentId= instList.get(position).instrumentId
                                ed_account_dd.text!!.clear()
                                ed_mobile_account_dd.text!!.clear()
                                ed_card_dd.text!!.clear()

                                if (savedInstanceState != null){
                                    ed_account_dd.setText(savedInstanceState.getString("enteredAccountNumber", ""))
                                }
                                if (savedInstanceState != null){
                                    ed_mobile_account_dd.setText(savedInstanceState.getString("enteredWalletNumber", ""))
                                }
                                if (savedInstanceState != null){
                                    ed_card_dd.setText(savedInstanceState.getString("enteredCardNumber", ""))
                                }

                                isAccountValid=false
                                isMobileValid=false
                                isCardValid=false
                            when(instList.get(position).instrumentId){

                                1390001->{
                                    ll_account.visibility= View.VISIBLE
                                    ll_mobile.visibility =View.GONE
                                    ll_card.visibility =View.GONE
                                    isAccountNumberSelected =true
                                    isMobileNumberSelected =false
                                    isCardNumberSelected =false

                                }
                                1390002->{
                                    ll_account.visibility= View.GONE
                                    ll_mobile.visibility =View.VISIBLE
                                    ll_card.visibility =View.GONE
                                    isAccountNumberSelected =false
                                    isMobileNumberSelected =true
                                    isCardNumberSelected =false

                                }
                                1390003->{
                                    ll_account.visibility= View.GONE
                                    ll_mobile.visibility =View.GONE
                                    ll_card.visibility =View.VISIBLE

                                    isAccountNumberSelected =false
                                    isMobileNumberSelected =false
                                    isCardNumberSelected =true


                                }
                            }

                            }
                            else{

                                buttonPay_dd.alpha=0.5f
                                //tv_error_payment_mode_dd.visibility=View.VISIBLE
                                ll_account.visibility= View.GONE
                                ll_mobile.visibility =View.GONE
                                ll_card.visibility =View.GONE
                            }


                        }



                    override fun onNothingSelected(parent: AdapterView<*>) {
                        isPaymentSelected =false
                        buttonPay_dd.alpha=0.5f
                        tv_error_payment_mode_dd.visibility=View.VISIBLE
                        ll_account.visibility= View.GONE
                        ll_mobile.visibility =View.GONE
                        ll_card.visibility =View.GONE
                        isBankSelected =true
                        }
                    }

                    if (savedInstanceState != null){
                        spinnerPaymentMode_dd.setSelection(savedInstanceState.getInt("selectedPaymentMode", 0))
                    }
                }
                else{
                    instList = ArrayList<Instrument>()
                    val defaultIns = Instrument(-1,"Select Payment Method" )
                    instList.add(defaultIns)

                    var spinnerPaymentAdapter: PaymentModeDropDownAdapter = PaymentModeDropDownAdapter( context as EasypayPaymentForm, instList)
                    // Set Adapter to Spinner
                    spinnerPaymentMode_dd!!.adapter = spinnerPaymentAdapter
                    spinnerPaymentMode_dd.isEnabled=false
                    isBankSelected =false
                    isPaymentSelected=false


                    isAccountNumberSelected =false
                    isMobileNumberSelected =false
                    isCardNumberSelected =false
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                isBankSelected =false
                isPaymentSelected=false


            }
        }

        if (savedInstanceState != null){
            spinnerBank_dd.setSelection(savedInstanceState.getInt("selectedBank", 0))
        }

        ed_mobile_account_dd.addTextChangedListener(object : TextWatcher {


                override fun afterTextChanged(s: Editable) {


                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {



                    when (Validation().isPhoneValid(s.toString(), 13, "^(0)(3)[0-9]{2}-([0-9]{3})-([0-9]{4})\$")) {
                        true -> {
                            tv_error_ed_mobileaccount_dd.visibility = View.INVISIBLE
                            isMobileValid = true
                        }
                        false -> {
                            tv_error_ed_mobileaccount_dd.visibility = View.VISIBLE
                            isMobileValid = false
                        }
                    }

                    if(isEmailValid && isPhoneValid && isBankSelected && isPaymentSelected && isCNICValid){
                        if(isAccountNumberSelected){
                            if(isAccountValid){

                                buttonPay_dd.alpha =1f

                            }else{
                                buttonPay_dd.alpha=0.5f

                            }
                        }
                        if(isMobileNumberSelected){
                            if(isMobileValid){

                                buttonPay_dd.alpha =1f

                            }else{
                                buttonPay_dd.alpha=0.5f

                            }
                        }

                        if(isCardNumberSelected){
                            if(isCardValid){

                                buttonPay_dd.alpha =1f

                            }else{
                                buttonPay_dd.alpha=0.5f

                            }
                        }
                    }
                    else if(!isEmailValid || !isPhoneValid || !isBankSelected || !isPaymentSelected || !isCNICValid){
                        buttonPay_dd.alpha=0.5f

                    }
                }


        })
        ed_account_dd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                if(Validation().isAccountValid(s.toString(),8,45)){
                    tv_error_ed_account_dd.visibility=View.INVISIBLE
                    isAccountValid =true
                }
                else{
                    tv_error_ed_account_dd.visibility=View.VISIBLE
                    isAccountValid =false
                }
                if(isEmailValid && isPhoneValid && isBankSelected && isPaymentSelected && isCNICValid){
                    if(isAccountNumberSelected){
                        if(isAccountValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }
                    if(isMobileNumberSelected){
                        if(isMobileValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }

                    if(isCardNumberSelected){
                        if(isCardValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }
                }
                else if(!isEmailValid || !isPhoneValid || !isBankSelected || !isPaymentSelected || !isCNICValid){
                    buttonPay_dd.alpha=0.5f

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        ed_card_dd.addTextChangedListener(object : TextWatcher {

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
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0,s.length,buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER) )

                }
                if(Validation().isCardValid(s.toString(),"^\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}[\\s\\-]?\\d{4}\$",19)){
                    tv_error_ed_card_dd.visibility=View.INVISIBLE
                    isCardValid =true
                }
                else{
                    tv_error_ed_card_dd.visibility=View.VISIBLE
                    isCardValid =false
                }

                if(isEmailValid && isPhoneValid && isBankSelected && isPaymentSelected && isCNICValid){
                    if(isAccountNumberSelected){
                        if(isAccountValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }
                    if(isMobileNumberSelected){
                        if(isMobileValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }

                    if(isCardNumberSelected){
                        if(isCardValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }
                }
                else if(!isEmailValid || !isPhoneValid || !isBankSelected || !isPaymentSelected || !isCNICValid){
                    buttonPay_dd.alpha=0.5f

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

        ed_cnic_dd.addTextChangedListener(object :TextWatcher {


            override fun afterTextChanged(s: Editable) {


            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                when(Validation().isCNICValid(s.toString(),15,"^[0-9+]{5}-[0-9+]{7}-[0-9]{1}\$")){
                    true->{
                        tv_error_ed_cnic_dd.visibility =View.INVISIBLE
                        isCNICValid=true
                    }
                    false->{
                        tv_error_ed_cnic_dd.visibility =View.VISIBLE
                        isCNICValid=false
                    }
                }

                if(isEmailValid && isPhoneValid && isBankSelected && isPaymentSelected && isCNICValid){
                    if(isAccountNumberSelected){
                        if(isAccountValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }
                    if(isMobileNumberSelected){
                        if(isMobileValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }

                    if(isCardNumberSelected){
                        if(isCardValid){

                            buttonPay_dd.alpha =1f

                        }else{
                            buttonPay_dd.alpha=0.5f

                        }
                    }
                }
                else if(!isEmailValid || !isPhoneValid || !isBankSelected || !isPaymentSelected || !isCNICValid){
                    buttonPay_dd.alpha=0.5f

                }
            }
        })

        buttonPay_dd.setOnClickListener {

            if(isAccountNumberSelected){
                typeSelected = "ACCOUNT"
            }
            if(isMobileNumberSelected){
                typeSelected="MOBILE"
            }
            if(isCardNumberSelected){
                typeSelected="CARD"
            }

            if(isPhoneValid){

                buttonPay_dd.alpha=1f
                tv_error_ed_mobile_dd.visibility=View.INVISIBLE
                if( isEmailValid){
                    tv_error_ed_email_dd.visibility=View.INVISIBLE
                    buttonPay_dd.alpha=1f

                    if(isBankSelected){
                        buttonPay_dd.alpha=1f
                        tv_error_bank_dd.visibility=View.INVISIBLE


                        if(isPaymentSelected){
                            buttonPay_dd.alpha=1f
                            tv_error_payment_mode_dd.visibility=View.INVISIBLE

                            when(typeSelected){

                                "ACCOUNT"->{
                                    if(isAccountValid) {
                                        buttonPay_dd.alpha=1f
                                        tv_error_ed_account_dd.visibility=View.INVISIBLE

                                        if(isCNICValid){

                                            buttonPay_dd.alpha=1f
                                            tv_error_ed_cnic_dd.visibility=View.INVISIBLE

                                            paymentOrder.email = ed_email_dd.text!!.toString()
                                            paymentOrder.phone = ed_mobile_dd.text!!.toString().replace("-", "")
                                            paymentOrder.bank = bankId.toString()
                                            paymentOrder.paymentMode =  paymentId.toString()
                                            paymentOrder.accountNumber =  ed_account_dd.text!!.toString().replace("-", "")
                                            paymentOrder.walletAccountNumber =  ed_mobile_account_dd.text!!.toString().replace("-", "")
                                            paymentOrder.creditCard =  ed_card_dd.text!!.toString().replace("-", "")
                                            paymentOrder.cnic =  ed_cnic_dd.text!!.toString().replace("-", "")


                                            easypayCall.paymentMethod(storeConfig, paymentOrder, allowedPaymentMethods)
                                        }
                                        else{

                                            buttonPay_dd.alpha=0.5f
                                            tv_error_ed_cnic_dd.visibility=View.VISIBLE
                                        }

                                    }
                                    else{

                                        buttonPay_dd.alpha=0.5f
                                        tv_error_ed_account_dd.visibility=View.VISIBLE
                                    }
                                }
                                "MOBILE"-> {
                                    if(isMobileValid) {
                                        buttonPay_dd.alpha=1f
                                        tv_error_ed_mobileaccount_dd.visibility=View.INVISIBLE

                                        if(isCNICValid){

                                            buttonPay_dd.alpha=1f
                                            tv_error_ed_cnic_dd.visibility=View.INVISIBLE

                                            paymentOrder.email = ed_email_dd.text!!.toString()
                                            paymentOrder.phone = ed_mobile_dd.text!!.toString().replace("-", "")
                                            paymentOrder.bank = bankId.toString()
                                            paymentOrder.paymentMode =  paymentId.toString()
                                            paymentOrder.accountNumber =  ed_account_dd.text!!.toString().replace("-", "")
                                            paymentOrder.walletAccountNumber =  ed_mobile_account_dd.text!!.toString().replace("-", "")
                                            paymentOrder.creditCard =  ed_card_dd.text!!.toString().replace("-", "")
                                            paymentOrder.cnic =  ed_cnic_dd.text!!.toString().replace("-", "")


                                            easypayCall.paymentMethod(storeConfig, paymentOrder, allowedPaymentMethods)
                                        }
                                        else{

                                            buttonPay_dd.alpha=0.5f
                                            tv_error_ed_cnic_dd.visibility=View.VISIBLE
                                        }

                                    }
                                    else{

                                        buttonPay_dd.alpha=0.5f
                                        tv_error_ed_mobileaccount_dd.visibility=View.VISIBLE
                                    }
                                }
                                "CARD"->{
                                    if(isCardValid) {
                                        buttonPay_dd.alpha=1f
                                        tv_error_ed_card_dd.visibility=View.INVISIBLE

                                        if(isCNICValid){

                                            buttonPay_dd.alpha=1f
                                            tv_error_ed_cnic_dd.visibility=View.INVISIBLE

                                            paymentOrder.email = ed_email_dd.text!!.toString()
                                            paymentOrder.phone = ed_mobile_dd.text!!.toString().replace("-", "")
                                            paymentOrder.bank = bankId.toString()
                                            paymentOrder.paymentMode =  paymentId.toString()
                                            paymentOrder.accountNumber =  ed_account_dd.text!!.toString().replace("-", "")
                                            paymentOrder.walletAccountNumber =  ed_mobile_account_dd.text!!.toString().replace("-", "")
                                            paymentOrder.creditCard =  ed_card_dd.text!!.toString().replace("-", "")
                                            paymentOrder.cnic =  ed_cnic_dd.text!!.toString().replace("-", "")


                                            easypayCall.paymentMethod(storeConfig, paymentOrder, allowedPaymentMethods)
                                        }
                                        else{

                                            buttonPay_dd.alpha=0.5f
                                            tv_error_ed_cnic_dd.visibility=View.VISIBLE
                                        }

                                    }
                                    else{

                                        buttonPay_dd.alpha=0.5f
                                        tv_error_ed_card_dd.visibility=View.VISIBLE
                                    }
                                }
                            }





                        }
                        else{
                            buttonPay_dd.alpha=0.5f
                            tv_error_payment_mode_dd.visibility=View.VISIBLE
                        }
                    }
                    else{
                        buttonPay_dd.alpha=0.5f
                        tv_error_bank_dd.visibility=View.VISIBLE
                    }
                }
                else{

                    buttonPay_dd.alpha=0.5f
                    tv_error_ed_email_dd.visibility=View.VISIBLE
                }
            }
            else{
                buttonPay_dd.alpha=0.5f
                tv_error_ed_mobile_dd.visibility=View.VISIBLE
            }
        }

        ivMobileTitle_dd.setOnClickListener {

            //ActivityUtil().hideKeyboard(activity as EasypayPaymentForm)
            Snackbar.make(ll_child_dd , R.string.dd_mobile_hint, Snackbar.LENGTH_SHORT).show()
        }

        ivEmailTitle_dd.setOnClickListener {

            //ActivityUtil().hideKeyboard(activity as EasypayPaymentForm)
            Snackbar.make(ll_child_dd , getString(R.string.dd_email_hint), Snackbar.LENGTH_SHORT).show()

        }



    }

    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)

        outState.putInt("selectedBank", spinnerBank_dd.selectedItemPosition)
        outState.putInt("selectedPaymentMode", spinnerPaymentMode_dd.selectedItemPosition)
        outState.putString("enteredAccountNumber", ed_account_dd.text.toString())
        outState.putString("enteredWalletNumber", ed_mobile_account_dd.text.toString())
        outState.putString("enteredCardNumber", ed_card_dd.text.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        storeConfig =  arguments?.get(CONFIGURATION) as EPConfiguration
        paymentOrder =  arguments?.get(PAYMENTORDER) as EPPaymentOrder
        allowedPaymentMethods =arguments?.get(PAYMENTTYPE) as AllowedPaymentMethods
        initDD(allowedPaymentMethods, savedInstanceState)
        ed_email_dd.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        ed_account_dd.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        ed_card_dd.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

    }




}