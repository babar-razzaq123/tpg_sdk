package telenor.com.ep_v1_sdk.config.model

import java.io.Serializable

data class EPConfiguration(

    var storeId: String,
    var storeName: String,
    var secretKey: String,
    var expiryToken: String,
    var bankIdentifier: String,
    var baseUrl: String,
    var isEditable: Boolean,
    var specificPaymentMethod: String?
): Serializable