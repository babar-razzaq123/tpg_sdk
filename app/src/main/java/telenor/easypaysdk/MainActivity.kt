package telenor.easypaysdk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_store_credential.*
import telenor.com.ep_v1_sdk.config.Easypay
import android.text.InputType

class MainActivity : AppCompatActivity()  {

    var isEditable : Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_credential)

        editTextStoreName.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        editTextOrderRef.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        editTextEmail.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        editTextBankIdentifier.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        editTextSecretKey.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        editTextTokenExpiry.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        editTextURL.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)

        versionCodeTV.text = BuildConfig.VERSION_NAME

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
                        isEditable


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
                        isEditable

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


}
