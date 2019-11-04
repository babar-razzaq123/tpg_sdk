package telenor.com.ep_v1_sdk.config.model

import java.io.Serializable

class PaymentOTPResponse(

    val content : ContentOTPResponse,
    val ackMessage : AckMessage
): Serializable


data class ContentOTPResponse (

    val code : Int,
    val message : String,
    val transactionId : Int,
    val storeId : Int
) : Serializable


