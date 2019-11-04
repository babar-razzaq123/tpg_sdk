package telenor.com.ep_v1_sdk.config.model

import java.io.Serializable


data class PaymentBaseResponse(

    val content : ContentData,
    val ackMessage : AckMessageData
):Serializable

data class ContentData (

    val orderId : String,
    val storeId : String,
    val paymentToken :String,
    val transactionId : String,
    val transactionDateTime : String,
    val paymentTokenExpiryDateTime :String,
    val htmlBody : String,
    val completeHtmlBody: String,
    val paymentGatewayType: String,
    val version: String,
    val transactionAmount : String
):Serializable
data class AckMessageData (

    val responseDescription : String,
    val responseCode : String
):Serializable


data class PaymentBaseDDResponse (

    val content : ContentBase,
    val ackMessage : AckMessage__DD
): Serializable


data class ContentBase (

    val targetView : String,
    val paymentDto : PaymentDto
): Serializable

data class PaymentDto (

    val sendSmsInd : Int,
    val createdDate : String,
    val amount : Int,
    val ip : String,
    val accountId : Int,
    val storeId : Int,
    val email : String,
    val cellPhone : String,
    val tokenExpiryDays : Int,
    val tokenExpiryDate : String,
    val transactionId : String,
    val merchantName : String,
    val formattedExpiryDateTime : String,
    val orderDateTime : String,
    val sendEmailInd : Int,
    val storeName : String,
    val paymentMethodMA : Boolean,
    val paymentMethodOTC : Boolean,
    val paymentMethodCC : Boolean,
    val paymentMethodQR : Boolean,
    val paymentMethodOMW : Boolean,
    val orderRefNumber : String,
    val extendedByAdmin : Boolean,
    val transactionStatus : String,
    val authTokenId : Int,
    val autoRedirectInd : Int,
    val transactionType : String,
    val store : Store_,
    val mdrCharges : Int,
    val transactionDateTime : String,
    val merchantEmail : String,
    val escrowAcctId : Int,
    val hasDelivered : Boolean,
    val merchantSMS : String,
    val expiryDurationUnitId : Int,
    val cnic : String,
    val maTopupServiceFee : Int,
    val walletAccountId : Int,
    val fee : Int,
    val internetBankingPaymentMode : Int,
    val stub : Boolean,
    val retryEnabledInd : Int,
    val retryExpiryTime : Int,
    val standaloneTxn : Boolean,
    val accSettlementDetailsDto : AccSettlementDetailsDto,
    val ewpAccountNumber : String,
    val epTax : Int,
    val paymentMethodsEnabledInds : PaymentMethodsEnabledInds,
    val cvvRequired : Int,
    val isInternationalCard : Int,
    val allowAutoCapture : Int,
    val campaignSmsSent : Boolean,
    val noBrandingInd : Boolean,
    val hostedCheckout : Boolean,
    val revScheduledInd : Boolean,
    val paymentMode : Int,
    val bankAccountNumber : String,
    val walletAccountNumber : String,
    val orderId : String,
    val ccNumber : String,
    val bankCode : Int,
    val paymentModeValue : String,
    val accessToken : String,
    val opsPrvKeyForMerchant : String,
    val errorrCode : String,
    val errorrMsg : String,
    val transactionDateTimeStr : String,
    val currentUser : Int

): Serializable


data class AccSettlementDetailsDto (

    val settlementId : Int,
    val convertToCorporateChecked : Boolean,
    val mdr : Int,
    val easypaisaDiscount : Int,
    val merchantDiscount : Int,
    val nadraVerificationInd : Boolean,
    val pepVerificationInd : Boolean,
    val sdnVerificationInd : Boolean,
    val rejectionReason : Int,
    val revisionReason : Int,
    val outletCheque : Boolean,
    val merchantNameCheque : Boolean,
    val coreBankValue : Int,
    val settlementMode : String,
    val ewpPoolAccountNum : Int,
    val settlementInitTime : Int,
    val customOTCFeeOnline : Int,
    val customOTCFeeRetail : Int,
    val customOTCTaxOnline : Int,
    val customOTCTaxRetail : Int,
    val customMAFeeOnline : Int,
    val customMAFeeRetail : Int,
    val customMATaxOnline : Int,
    val customMATaxRetail : Int,
    val customQRFeeOnline : Int,
    val customQRFeeRetail : Int,
    val customQRTaxOnline : Int,
    val customQRTaxRetail : Int,
    val customTillFeeOnline : Int,
    val customTillTaxOnline : Int,
    val customTillTaxRetail : Int,
    val customTillFeeRetail : Int,
    val accountBankId : Int,
    val otherBankId : Int,
    val accountId : Int,
    val manualIBFTChecked : Boolean,
    val coreBankingEnabled : Boolean,
    val otherAccountChecked : Boolean,
    val paymentHold : Boolean,
    val settlementInitiatedInd : Int,
    val ibftCount : Int,
    val retailAccount : Boolean,
    val customSettmntTimeChecked : Boolean,
    val settlementLevelCodeId : Int,
    val settlementFrequency : Int,
    val settlementDate : Int,
    val settlementDay : Int,
    val customCCFeeOnline : Int,
    val customCCTaxOnline : Int,
    val customCCFeeRetail : Int,
    val customCCTaxRetail : Int,
    val customIbFeeOnline : Int,
    val customIbTaxOnline : Int,
    val customIbFeeRetail : Int,
    val customIbTaxRetail : Int,
    val customOmwFeeOnline : Int,
    val customOmwTaxOnline : Int,
    val customOmwFeeRetail : Int,
    val customOmwTaxRetail : Int,
    val customRevMAFee : Int,
    val customRevMATax : Int,
    val customRevOTCFee : Int,
    val customRevOTCTax : Int,
    val customRevCCFee : Int,
    val customRevCCTax : Int,
    val customRevQRFee : Int,
    val customRevQRTax : Int,
    val customRevTillFee : Int,
    val customRevTillTax : Int,
    val customRevIBFee : Int,
    val customRevIBTax : Int,
    val customRevOMWFee : Int,
    val customRevOMWTax : Int,
    val customDDFeeOnline : Int,
    val customDDTaxOnline : Int,
    val customDDFeeRetail : Int,
    val customDDTaxRetail : Int,
    val customRevDDFee : Int,
    val customRevDDTax : Int,
    val currentUser : Int
): Serializable

data class Store_ (

    val merchantAccountId : Int,
    val sno : Int,
    val storeId : Int,
    val storeName : String,
    val alreadyLinked : Boolean,
    val refStoreId : Int,
    val paymentEnabled : Boolean,
    val sNo : Int,
    val subMerchantId : Int,
    val subMerchantName : String,
    val subMerchantStreetAddress : String,
    val subMerchantCity : String,
    val subMerchantState : String,
    val subMerchantCountryCode : String,
    val subMerchantPosCountryCode : String,
    val subMerchantPosPostalCode : String,
    val subMerchantCategoryCode : Int,
    val numberOfTransactionPoints : Int,
    val statusId : Int,
    val region : Int,
    val city : Int,
    val logoUploaded : Boolean,
    val sendNotificationInd : Boolean,
    val storeQueueId : Int,
    val cashInAllowed : Boolean,
    val cashOutAllowed : Boolean,
    val isTpFound : Boolean,
    val currentUser : Int
): Serializable


data class PaymentMethodsEnabledInds (

    val dD : Boolean,
    val cC : Boolean,
    val qR : Boolean,
    val mA : Boolean,
    val oMW : Boolean,
    val iB : Boolean,
    val oTC : Boolean
): Serializable
data class AckMessage__ (

    val responseDescription : String,
    val responseCode : String
): Serializable

data class AckMessage__DD (

    val responseDescription : String,
    val responseCode : Int
): Serializable


data class PaymentCCCardResponse (

    val content : ContentCC,
    val ackMessage : AckMessage__
): Serializable

data class ContentCC (

    val binCheckReason : String,
    val binCheckNotification : String,
    val binCheckReasonCode : String,
    val firstWaitInterval : Int,
    val successiveWaitInterval : Int,
    val reattemptMsg : String,
    val reattemptFailureMsg : String,
    val queryDRAttempts : Int,
    val mpgsReattempts : Int,
    val firstWaitIntervalForMigs : String,
    val successiveWaitIntervalForMigs : String,
    val reattemptFailureMsgForMigs : String,
    val reattemptMsgForMigs : String,
    val transactionQueryDRCommand : String

): Serializable

data class CCLogoResponse (

    val content : CCLogoContent,
    val ackMessage : AckMessage__
): Serializable

data class CCLogoContent (

    val bankIdentificationNumber : String,
    val bankContent : String,
    val bankMimeType : String,
    val bankLogoFlag : Boolean
): Serializable

data class CCTransactionStatusResponse (

    val content : ContentCCTransactionStatus,
    val ackMessage : AckMessage__
): Serializable

data class ContentCCTransactionStatus (

    val orderId : String,
    val transactionDateTime : String,
    val status : String,
    val transactionAmount : String,
    val transactionId : String,
    val transactionRefNumber : String,
    val storeId : String,
    val transactionStatusResponse : String,
    val transactionStatusResponseCode : String,
    val pendingCaptureInd : String
): Serializable
