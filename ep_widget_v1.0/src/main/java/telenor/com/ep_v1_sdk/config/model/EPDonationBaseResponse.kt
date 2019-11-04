package telenor.com.ep_v1_sdk.config.model

import java.io.Serializable


data class DonationBaseResponse(

    val content : List<CharityData>,
    val ackMessage : AckMessages
):Serializable

data class CharityData (

    val id : Int,
    val charityName : String,
    val donationLevel1 : String,
    val donationLevel2 : String,
    val donationLevel3 : String,
    val store : Store
):Serializable

data class Store (

    val id : Int,
    val name : String,
    val hashKey : String

):Serializable
data class AckMessages (

    val responseDescription : String,
    val responseCode : Int
):Serializable
