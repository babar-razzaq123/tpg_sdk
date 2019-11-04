package telenor.com.ep_v1_sdk.config.model

import java.io.Serializable


data class FeedbackBaseResponse(

    val ackMessage : AckMessag
):Serializable

data class AckMessag (

    val responseDescription : String,
    val responseCode : Int
):Serializable
