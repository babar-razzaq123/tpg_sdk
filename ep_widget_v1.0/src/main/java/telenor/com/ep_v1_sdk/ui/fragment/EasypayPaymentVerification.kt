package telenor.com.ep_v1_sdk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_verification.*
import telenor.com.ep_v1_sdk.R
import telenor.com.ep_v1_sdk.config.CHARITYORDER
import telenor.com.ep_v1_sdk.config.CONFIGURATION
import telenor.com.ep_v1_sdk.config.PAYMENTORDER
import telenor.com.ep_v1_sdk.config.model.AllowedPaymentMethods
import telenor.com.ep_v1_sdk.config.model.EPConfiguration
import telenor.com.ep_v1_sdk.config.model.EPPaymentCharityOrder
import telenor.com.ep_v1_sdk.config.model.EPPaymentOrder
import telenor.com.ep_v1_sdk.ui.EasypayDonationCall

class EasypayPaymentVerification(easypay: EasypayDonationCall) : Fragment() {


    lateinit var storeConfig: EPConfiguration
    lateinit var paymentOrder : EPPaymentOrder
    lateinit var charityOrder: EPPaymentCharityOrder
    lateinit var allowedPaymentMethods: AllowedPaymentMethods
    var easypayDonation: EasypayDonationCall = easypay
    companion object {

        fun newInstance( easypayDonation: EasypayDonationCall): EasypayPaymentVerification {
            return EasypayPaymentVerification(easypayDonation)
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
        charityOrder =  arguments?.get(CHARITYORDER) as EPPaymentCharityOrder

        paymentStatus.text = getText(R.string.pkr).toString().capitalize() + " "+charityOrder.orderAmount +" are about to be donated to " + charityOrder.charityName +" from your Easypaisa Mobile Account " +paymentOrder.phone +".  Click Ok to confirm."

        buttonPay_ok.setOnClickListener {
            easypayDonation.paymentMethod(storeConfig,paymentOrder,"OK")
        }
        buttonPay_cancel.setOnClickListener {
            easypayDonation.paymentMethod(storeConfig,paymentOrder,"CANCEL")
        }
    }

    // inflate your view here
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_verification, container, false)

}
