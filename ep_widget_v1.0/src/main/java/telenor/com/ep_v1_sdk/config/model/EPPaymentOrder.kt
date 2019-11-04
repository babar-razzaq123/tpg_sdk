package telenor.com.ep_v1_sdk.config.model

import telenor.com.ep_v1_sdk.util.Validation
import java.io.Serializable

data class EPPaymentOrder(

    var email: String,
    var phone: String,
    var orderId: String,
    var creditCard: String,
    var creditCardMonth: String,
    var creditCardYear: String,
    var creditCardCVV: String,
    var bank: String,
    var paymentMode: String,
    var accountNumber: String,
    var walletAccountNumber: String,
    var cnic: String,


    var orderAmount: String
): Serializable