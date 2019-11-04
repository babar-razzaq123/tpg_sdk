package telenor.com.ep_v1_sdk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_verification.*
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.config.CONFIGURATION
import telenor.com.ep_v1_sdk.config.DONATIONRESPONSE
import telenor.com.ep_v1_sdk.config.PAYMENTORDER
import telenor.com.ep_v1_sdk.config.model.AllowedPaymentMethods
import telenor.com.ep_v1_sdk.config.model.EPConfiguration
import telenor.com.ep_v1_sdk.config.model.EPPaymentOrder
import telenor.com.ep_v1_sdk.config.model.PaymentBaseResponse
import telenor.com.ep_v1_sdk.ui.EasypayCall
import telenor.com.ep_v1_sdk.ui.EasypayDonationCall

class EasypayPaymentVerificationStatus(easypayDonation: EasypayDonationCall) : Fragment() {


    lateinit var storeConfig: EPConfiguration
    lateinit var paymentOrder : EPPaymentOrder
    lateinit var allowedPaymentMethods: AllowedPaymentMethods
    lateinit var paymentBaseResponse: PaymentBaseResponse
    var easypayCall: EasypayDonationCall = easypayDonation
    companion object {

        fun newInstance( easypayCall: EasypayDonationCall): EasypayPaymentVerificationStatus {
            return EasypayPaymentVerificationStatus(easypayCall)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }
    private fun  initView(){
        storeConfig =  arguments?.get(CONFIGURATION) as EPConfiguration
        paymentOrder =  arguments?.get(PAYMENTORDER) as EPPaymentOrder
        paymentBaseResponse = arguments?.get(DONATIONRESPONSE) as PaymentBaseResponse


        when (paymentBaseResponse.ackMessage.responseCode.toString().contentEquals("0000")) {
            true -> {

                paymentStatus.text = getText(R.string.thankyou).toString()


            }
            false -> {

                paymentStatus.text = paymentBaseResponse.ackMessage.responseDescription


            }
        }


        buttonPay_cancel.setOnClickListener {
            easypayCall.paymentMethod(storeConfig,paymentOrder,"SUMMARY")
        }
    }

    // inflate your view here
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_verification_status, container, false)

}
