package telenor.com.ep_v1_sdk.config.model

import java.io.Serializable

data class PaymentMethodBaseResponse(

    val content : Content,
    val ackMessage : AckMessage
)

data class Content (

    val timeStamp : String,
    val hashKey : String,
    val orderId : String,
    val email : String,
    val cellNumber : String,
    val directDebitBank : ArrayList<DirectDebitBank>,
    val allowedPaymentMethods : List<AllowedPaymentMethods>,
    val storeId : Int,
    val transactionAmount : String,
    val transactionType : String,
    val tokenExpiry : String,
    val bankIdentificationNumber : String,
    val countryTelCodes :  ArrayList<CountryTelCodes>,
    val teleCode : String,
    val expiryMonths :  ArrayList<ExpiryMonths>,
    val expiryYears :  ArrayList<ExpiryYears>,
    val merchantAccountId : String

)
data class AckMessage (

    val responseDescription : String,
    val responseCode : Int
):Serializable
data class DirectDebitBank (

    val bankId : Int,
    val bankName : String,
    val instrument : List<Instrument>
): Serializable
data class Instrument (

    val instrumentId : Int,
    val instrumentName : String
): Serializable


data class AllowedPaymentMethods (

    var icon : Int,
    val code : String,
    var description : String
): Serializable

data class CountryTelCodes (

    val key : String,
    val value : String,
    val currentUser : String
): Serializable

data class ExpiryMonths (

    val key : String,
    val value : String,
    val currentUser : String
): Serializable

data class ExpiryYears (

    val key : String,
    val value : String,
    val currentUser : String
): Serializable