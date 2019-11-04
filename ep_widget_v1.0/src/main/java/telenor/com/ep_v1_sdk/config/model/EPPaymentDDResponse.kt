package telenor.com.ep_v1_sdk.config.model

import java.io.Serializable


class PaymentDDResponse(

    val content : ContentOTP,
    val ackMessage : AckMessage
):Serializable

data class ContentOTP (

    val successDto : SuccessDto,
    val screenMsg : String,
    val transactionId : String,
    val storeId : Int
):Serializable


data class SuccessDto (

    val amount : Double,
    val transactionNumber : String,
    val transactionResponse : String,
    val showLoader : Boolean,
    val merchantStoreURL : String,
    val orderRefNumber : String,
    val success : Boolean,
    val transactionId : String,
    val paymentGatewayId : Int,
    val postBackURL : String,
    val migsReattemptsCount : Int,
    val mpgsReattemptsCount : Int,
    val noBrandingInd : Boolean,
    val accountId : Int,
    val currentUser : Int
):Serializable