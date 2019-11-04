package telenor.com.ep_v1_sdk.ui

import telenor.com.ep_v1_sdk.config.model.PaymentCCCardResponse


interface CardResponseCallback {

    abstract fun cardResponse(response: PaymentCCCardResponse)

}