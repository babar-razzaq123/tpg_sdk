package telenor.easypaysdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_store_credential.*
import telenor.com.ep_v1_sdk.config.Easypay
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import telenor.com.ep_v1_sdk.adapter.PaymentMethodsDropDownAdapter
import telenor.com.ep_v1_sdk.config.model.PaymentMethodsList

class MainActivity : AppCompatActivity()  {

    var isEditable : Boolean = false;
    var selectedPaymentMethod : String? = null
    val paymentMethods = mutableListOf<PaymentMethodsList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_credential)

        editTextStoreName.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        editTextOrderRef.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        editTextEmail.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        editTextBankIdentifier.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        //editTextSecretKey.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        editTextTokenExpiry.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        editTextURL.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

        versionCodeTV.text = BuildConfig.VERSION_NAME

        addSpecificPaymentMethods()

        var spinnerPaymentMethodAdapter: PaymentMethodsDropDownAdapter = PaymentMethodsDropDownAdapter(this, paymentMethods)
        spinnerPaymentMethod!!.adapter = spinnerPaymentMethodAdapter

        spinnerPaymentMethod.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent:AdapterView<*>, view: View, position: Int, id: Long){
                if (position != 0) {
                    selectedPaymentMethod = paymentMethods[position].key
                }
                else {
                    selectedPaymentMethod = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>){
            }
        }

        editableSwitchBtn.setOnCheckedChangeListener { buttonView, isChecked ->

            when(isChecked){
                true -> {
                    isEditable = true
                }
                false -> {
                    isEditable = false
                }
            }

        }

        proceedButton.setOnClickListener {

            when(editStoreId.text?.toString().isNullOrBlank()){

                true -> {
                    Easypay().configure(
                        this,

                        "",
                        editTextStoreName!!.text!!.toString(),
                        editTextTokenExpiry!!.text!!.toString(),
                        editTextBankIdentifier!!.text!!.toString(),
                        editTextSecretKey!!.text!!.toString(),
                        editTextURL!!.text!!.toString(),
                        isEditable,
                        selectedPaymentMethod


                    )
                }
                false->{
                    Easypay().configure(
                        this,

                        editStoreId.text?.toString()!! ,
                        editTextStoreName!!.text!!.toString(),
                        editTextTokenExpiry!!.text!!.toString(),
                        editTextBankIdentifier!!.text!!.toString(),
                        editTextSecretKey!!.text!!.toString(),
                        editTextURL!!.text!!.toString(),
                        isEditable,
                        selectedPaymentMethod

                    )
                }
            }

                when(editTextAmount!!.text!!.toString().isNullOrBlank()){
                    true->{
                        Easypay().checkout(
                            this,
                            editTextEmail!!.text!!.toString(),
                            editTextMobile!!.text!!.toString(),
                            editTextOrderRef!!.text!!.toString(),
                           "0"
                        )
                    }
                    false->{
                        Easypay().checkout(
                            this,
                            editTextEmail!!.text!!.toString(),
                            editTextMobile!!.text!!.toString(),
                            editTextOrderRef!!.text!!.toString(),
                            editTextAmount!!.text!!.toString()
                        )
                    }

                }



        }

    }

    fun addSpecificPaymentMethods() {
        val pm : PaymentMethodsList = PaymentMethodsList(null , "ALL")
        val pm1 : PaymentMethodsList = PaymentMethodsList("MA" , "MA_PAYMENT_METHOD")
        val pm2 : PaymentMethodsList = PaymentMethodsList("OTC" , "OTC_PAYMENT_METHOD")
        val pm3 : PaymentMethodsList = PaymentMethodsList("CC" , "CC_PAYMENT_METHOD")
        val pm4 : PaymentMethodsList = PaymentMethodsList("DD" , "DD_PAYMENT_METHOD")
        paymentMethods.add(pm)
        paymentMethods.add(pm1)
        paymentMethods.add(pm2)
        paymentMethods.add(pm3)
        paymentMethods.add(pm4)
    }

}
