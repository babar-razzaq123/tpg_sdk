package telenor.com.ep_v1_sdk.ui

import telenor.com.ep_v1_sdk.config.model.AllowedPaymentMethods
import telenor.com.ep_v1_sdk.config.model.EPConfiguration
import telenor.com.ep_v1_sdk.config.model.EPPaymentOrder

interface EasypayCall {

    abstract fun paymentMethod(storeConfig: EPConfiguration, paymentOrder: EPPaymentOrder, allowedPaymentMethods: AllowedPaymentMethods)

}