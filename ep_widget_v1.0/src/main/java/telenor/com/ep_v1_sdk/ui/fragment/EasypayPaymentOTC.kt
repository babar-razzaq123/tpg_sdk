package telenor.com.ep_v1_sdk.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.ll_form_ma.*
import kotlinx.android.synthetic.main.ll_form_otc.*
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.config.*
import telenor.com.ep_v1_sdk.config.model.AllowedPaymentMethods
import telenor.com.ep_v1_sdk.config.model.EPConfiguration
import telenor.com.ep_v1_sdk.config.model.EPPaymentOrder
import telenor.com.ep_v1_sdk.ui.EasypayCall
import telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentForm
import telenor.com.ep_v1_sdk.util.ActivityUtil
import telenor.com.ep_v1_sdk.util.Validation

class EasypayPaymentOTC(easypay: EasypayCall) : Fragment() {


    lateinit var storeConfig: EPConfiguration
    lateinit var paymentOrder :EPPaymentOrder
    lateinit var allowedPaymentMethods: AllowedPaymentMethods
    var easypayCall: EasypayCall = easypay
    companion object {

        fun newInstance( easypayCall: EasypayCall): EasypayPaymentOTC {
            return EasypayPaymentOTC(easypayCall)
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
        initOTC(allowedPaymentMethods)
        ed_email_otc.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

    }

    // inflate your view here
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.ll_form_otc, container, false)


    private fun initOTC(allowedPaymentMethods: AllowedPaymentMethods) {


        var isPhoneValid: Boolean = false
        var isEmailValid: Boolean = false
        var keyDel = false

        ll_child_otc.visibility =View.VISIBLE
        listTitle_otc.text = allowedPaymentMethods.description
        listTitle_otc.setTextColor(resources.getColor(R.color.green))

        tv_error_ed_mobile_otc.text = getString(R.string.invalidNumber)
        tv_error_ed_mobile_otc.visibility =View.INVISIBLE

        tv_error_ed_email_otc.text = getString(R.string.invalidEmail)
        tv_error_ed_email_otc.visibility =View.INVISIBLE

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
                            tv_error_ed_mobile_otc.visibility =View.INVISIBLE
                            isPhoneValid=true
                        }
                        false->{
                            tv_error_ed_mobile_otc.visibility =View.VISIBLE
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
                            tv_error_ed_mobile_otc.visibility =View.INVISIBLE
                            isPhoneValid=true
                        }
                        false->{
                            tv_error_ed_mobile_otc.visibility =View.VISIBLE
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
                            tv_error_ed_mobile_otc.visibility =View.INVISIBLE
                            isPhoneValid=true
                        }
                        false->{
                            tv_error_ed_mobile_otc.visibility =View.VISIBLE
                            isPhoneValid=false
                        }
                    }



                }
                else{

                    when(Validation().isPhoneValid(phone.toString(),13,"^(0)(3)[0-9]{2}-([0-9]{3})-([0-9]{4})\$")){
                        true->{
                            tv_error_ed_mobile_otc.visibility =View.INVISIBLE
                            isPhoneValid=true
                        }
                        false->{
                            tv_error_ed_mobile_otc.visibility =View.VISIBLE
                            isPhoneValid=false
                        }
                    }
                }
            }

            ed_mobile_otc.setText(appendedPhone)
        }


        ed_mobile_otc.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {


            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {


                when(Validation().isPhoneValid(s.toString(),13,"^(0)(3)[0-9]{2}-([0-9]{3})-([0-9]{4})\$")){
                    true->{
                        tv_error_ed_mobile_otc.visibility =View.INVISIBLE
                        isPhoneValid=true
                    }
                    false->{
                        tv_error_ed_mobile_otc.visibility =View.VISIBLE
                        isPhoneValid=false
                    }
                }
                if(isEmailValid && isPhoneValid){
                    buttonPay_otc.alpha =1f
                }
                else if(!isEmailValid || !isPhoneValid){
                    buttonPay_otc.alpha=0.5f
                }
            }
        })






        if(!paymentOrder.email.isNullOrEmpty()){
            ed_email_otc.setText(paymentOrder!!.email)
            when(Validation().isEmailValid(paymentOrder!!.email.toString())){
                true->{
                    tv_error_ed_email_otc.visibility =View.INVISIBLE
                    isEmailValid=true
                }
                false->{
                    tv_error_ed_email_otc.visibility =View.VISIBLE
                    isEmailValid=false
                }
            }
        }

        ed_email_otc.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

                when(Validation().isEmailValid(s.toString())){
                    true->{
                        tv_error_ed_email_otc.visibility =View.INVISIBLE
                        isEmailValid=true
                    }
                    false->{
                        tv_error_ed_email_otc.visibility =View.VISIBLE
                        isEmailValid=false
                    }
                }
                if(isEmailValid && isPhoneValid){
                    buttonPay_otc.alpha =1f
                }
                else if(!isEmailValid || !isPhoneValid){
                    buttonPay_otc.alpha=0.5f
                }
            }
        })

        if(isEmailValid && isPhoneValid){
            buttonPay_otc.alpha =1f
        }
        else if(!isEmailValid || !isPhoneValid){
            buttonPay_otc.alpha=0.5f
        }
        buttonPay_otc.setOnClickListener {
            if(isPhoneValid){
                tv_error_ed_mobile_otc.visibility=View.INVISIBLE
                if( isEmailValid){
                    tv_error_ed_email_otc.visibility=View.INVISIBLE
                    buttonPay_otc.alpha=1f
                    paymentOrder.email= ed_email_otc.text!!.toString()
                    paymentOrder.phone =ed_mobile_otc.text!!.toString().replace("-", "")
                    easypayCall.paymentMethod(storeConfig,paymentOrder,allowedPaymentMethods)
                }
                else{
                    tv_error_ed_email_otc.visibility=View.VISIBLE
                }
            }
            else{
                buttonPay_otc.alpha=0.5f
                tv_error_ed_mobile_otc.visibility=View.VISIBLE
            }
        }

        ivMobileTitle_otc.setOnClickListener {

            //ActivityUtil().hideKeyboard(activity as EasypayPaymentForm)
            Snackbar.make(ll_child_otc , R.string.otc_mobile_hint, Snackbar.LENGTH_SHORT).show()
        }

        ivEmailTitle_otc.setOnClickListener {

            //ActivityUtil().hideKeyboard(activity as EasypayPaymentForm)
            Snackbar.make(ll_child_otc , getString(R.string.otc_email_hint), Snackbar.LENGTH_SHORT).show()

        }

    }




}