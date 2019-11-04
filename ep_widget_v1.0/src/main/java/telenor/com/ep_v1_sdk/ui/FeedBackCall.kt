package telenor.com.ep_v1_sdk.ui

import telenor.com.ep_v1_sdk.config.model.AllowedPaymentMethods
import telenor.com.ep_v1_sdk.config.model.EPConfiguration
import telenor.com.ep_v1_sdk.config.model.EPPaymentOrder

interface FeedBackCall {

    abstract fun checkFeedback(check : Boolean)

}