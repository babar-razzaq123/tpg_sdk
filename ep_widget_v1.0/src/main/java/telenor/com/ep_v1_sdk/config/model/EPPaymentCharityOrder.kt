package telenor.com.ep_v1_sdk.config.model

import java.io.Serializable

data class EPPaymentCharityOrder(

    var charityName: String,
    var charityStoreId: String,
    var orderAmount: String,
    var secretKey: String,
    var storeName: String
): Serializable