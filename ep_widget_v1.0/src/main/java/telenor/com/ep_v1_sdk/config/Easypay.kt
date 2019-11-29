package telenor.com.ep_v1_sdk.config

import android.content.Context
import android.content.Intent
import android.widget.Toast
import telenor.com.ep_v1_sdk.config.model.EPConfiguration
import telenor.com.ep_v1_sdk.config.model.EPPaymentOrder
import telenor.com.ep_v1_sdk.util.Validation

class Easypay{

    private lateinit var initEasyPay: Easypay

    private lateinit var easyPayConfig : EPConfiguration
    private lateinit var sharedPrefDataSource: EPSharedPrefDataSource

    /**
     * Static method to create instance.
     */
    fun configure(appContext: Context,
                  storeId: String,
                  storeName: String,
                  expiryToken:String,
                  bankIdentifier: String,
                  hashKey: String,
                  baseUrl: String
    ): Easypay {
         val config = EPConfiguration(storeId,storeName,hashKey, expiryToken,bankIdentifier, baseUrl)
        initEasyPay = EasyPay(appContext,config)

        return initEasyPay
    }


    fun EasyPay(appContext: Context,
                easyPayCon: EPConfiguration
    ): Easypay {
        easyPayConfig =easyPayCon
        sharedPrefDataSource=EPSharedPrefDataSource(appContext)
        sharedPrefDataSource.setEasyPayConfig(easyPayCon)
        return this
    }


    fun checkout(appContext: Context, email : String , phone : String, orderID :String ,totalAmount : String) {

        sharedPrefDataSource=EPSharedPrefDataSource(appContext)

        val storeConfig = sharedPrefDataSource.getEasyPayConfig()
        when(!storeConfig.baseUrl/*BASE_URL*/.isNullOrEmpty()){



            true->{

                if (Validation().isUrlValid(storeConfig.baseUrl/*BASE_URL*/)) {

                    if (Validation().validSecretKey(storeConfig.secretKey)) {


                        when (phone.isNullOrEmpty()) {
                            true -> {

                                when (email.isNullOrEmpty()) {
                                    true -> {
                                        val paymentOrder = EPPaymentOrder(
                                            email,
                                            phone,
                                            orderID,
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            totalAmount
                                        )

                                        when (storeConfig.expiryToken.isNullOrEmpty()) {
                                            true -> {
                                                if (Validation().validStoreID(storeConfig.storeId.toString()) && Validation().validStoreName(
                                                        storeConfig.storeName.toString()
                                                    )
                                                ) {

                                                    if (Validation().validOrderRef(paymentOrder.orderId.toString()) && Validation().validAmount(
                                                            paymentOrder.orderAmount.toString()
                                                        )
                                                    ) {
                                                        if (Validation().validOrderRefValue(paymentOrder.orderId.toString())) {

                                                            if (Validation().validAmountValue(
                                                                    paymentOrder.orderAmount.toString(),
                                                                    "^\\d{0,6}(\\.\\d{0,2})?\$"
                                                                )
                                                            ) {
                                                                val intent = Intent(
                                                                    appContext,
                                                                    Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentMethod")
                                                                )
                                                                var paymentOr = paymentOrder
                                                                paymentOr.orderAmount =
                                                                    Validation().getAmount(paymentOrder.orderAmount)
                                                                intent.putExtra(CONFIGURATION, storeConfig)
                                                                intent.putExtra(PAYMENTORDER, paymentOr)
                                                                appContext.startActivity(intent)
                                                            } else {

                                                                Toast.makeText(
                                                                    appContext,
                                                                    "Please enter correct order amount !",
                                                                    Toast.LENGTH_LONG
                                                                )
                                                                    .show()
                                                            }
                                                        } else {

                                                            Toast.makeText(
                                                                appContext,
                                                                "Please enter correct order detail !",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }


                                                    } else {

                                                        Toast.makeText(
                                                            appContext,
                                                            "Please enter order detail !",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        appContext,
                                                        "Please Setup Store !",
                                                        Toast.LENGTH_LONG
                                                    )
                                                        .show()
                                                }
                                            }
                                            false -> {

                                                if (Validation().validStoreID(storeConfig.storeId.toString()) && Validation().validStoreName(
                                                        storeConfig.storeName.toString()
                                                    ) && Validation().validExpiryToken(storeConfig.expiryToken.toString())
                                                ) {

                                                    if (Validation().validExpiryTokenFormat(storeConfig.expiryToken.toString())) {


                                                        if (Validation().validOrderRef(paymentOrder.orderId.toString()) && Validation().validAmount(
                                                                paymentOrder.orderAmount.toString()
                                                            )
                                                        ) {


                                                            if (Validation().validOrderRefValue(paymentOrder.orderId.toString())) {

                                                                if (Validation().validAmountValue(
                                                                        paymentOrder.orderAmount.toString(),
                                                                        "^\\d{0,6}(\\.\\d{0,2})?\$"
                                                                    )
                                                                ) {
                                                                    val intent = Intent(
                                                                        appContext,
                                                                        Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentMethod")
                                                                    )
                                                                    var paymentOr = paymentOrder
                                                                    paymentOr.orderAmount =
                                                                        Validation().getAmount(paymentOrder.orderAmount)
                                                                    intent.putExtra(CONFIGURATION, storeConfig)
                                                                    intent.putExtra(PAYMENTORDER, paymentOr)
                                                                    appContext.startActivity(intent)
                                                                } else {

                                                                    Toast.makeText(
                                                                        appContext,
                                                                        "Please enter correct order amount !",
                                                                        Toast.LENGTH_LONG
                                                                    )
                                                                        .show()
                                                                }
                                                            } else {

                                                                Toast.makeText(
                                                                    appContext,
                                                                    "Please enter correct order detail !",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                        } else {

                                                            Toast.makeText(
                                                                appContext,
                                                                "Please enter order detail !",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    } else {

                                                        Toast.makeText(
                                                            appContext,
                                                            "Incorrect Token Expiry Date format !",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }

                                                } else {
                                                    Toast.makeText(
                                                        appContext,
                                                        "Please Setup Store !",
                                                        Toast.LENGTH_LONG
                                                    )
                                                        .show()
                                                }
                                            }
                                        }
                                    }
                                    false -> {
                                        if (Validation().isEmailValid(email)) {
                                            val paymentOrder = EPPaymentOrder(
                                                email,
                                                phone,
                                                orderID,
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                totalAmount
                                            )

                                            when (storeConfig.expiryToken.isNullOrEmpty()) {
                                                true -> {
                                                    if (Validation().validStoreID(storeConfig.storeId.toString()) && Validation().validStoreName(
                                                            storeConfig.storeName.toString()
                                                        )
                                                    ) {

                                                        if (Validation().validOrderRef(paymentOrder.orderId.toString()) && Validation().validAmount(
                                                                paymentOrder.orderAmount.toString()
                                                            )
                                                        ) {
                                                            if (Validation().validOrderRefValue(paymentOrder.orderId.toString())) {

                                                                if (Validation().validAmountValue(
                                                                        paymentOrder.orderAmount.toString(),
                                                                        "^\\d{0,6}(\\.\\d{0,2})?\$"
                                                                    )
                                                                ) {
                                                                    val intent = Intent(
                                                                        appContext,
                                                                        Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentMethod")
                                                                    )
                                                                    var paymentOr = paymentOrder
                                                                    paymentOr.orderAmount =
                                                                        Validation().getAmount(paymentOrder.orderAmount)
                                                                    intent.putExtra(CONFIGURATION, storeConfig)
                                                                    intent.putExtra(PAYMENTORDER, paymentOr)
                                                                    appContext.startActivity(intent)
                                                                } else {

                                                                    Toast.makeText(
                                                                        appContext,
                                                                        "Please enter correct order amount !",
                                                                        Toast.LENGTH_LONG
                                                                    )
                                                                        .show()
                                                                }
                                                            } else {

                                                                Toast.makeText(
                                                                    appContext,
                                                                    "Please enter correct order detail !",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }


                                                        } else {

                                                            Toast.makeText(
                                                                appContext,
                                                                "Please enter order detail !",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    } else {
                                                        Toast.makeText(
                                                            appContext,
                                                            "Please Setup Store !",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                                false -> {

                                                    if (Validation().validStoreID(storeConfig.storeId.toString()) && Validation().validStoreName(
                                                            storeConfig.storeName.toString()
                                                        ) && Validation().validExpiryToken(storeConfig.expiryToken.toString())
                                                    ) {

                                                        if (Validation().validExpiryTokenFormat(storeConfig.expiryToken.toString())) {


                                                            if (Validation().validOrderRef(paymentOrder.orderId.toString()) && Validation().validAmount(
                                                                    paymentOrder.orderAmount.toString()
                                                                )
                                                            ) {


                                                                if (Validation().validOrderRefValue(paymentOrder.orderId.toString())) {

                                                                    if (Validation().validAmountValue(
                                                                            paymentOrder.orderAmount.toString(),
                                                                            "^\\d{0,6}(\\.\\d{0,2})?\$"
                                                                        )
                                                                    ) {
                                                                        val intent = Intent(
                                                                            appContext,
                                                                            Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentMethod")
                                                                        )
                                                                        var paymentOr = paymentOrder
                                                                        paymentOr.orderAmount =
                                                                            Validation().getAmount(paymentOrder.orderAmount)
                                                                        intent.putExtra(CONFIGURATION, storeConfig)
                                                                        intent.putExtra(PAYMENTORDER, paymentOr)
                                                                        appContext.startActivity(intent)
                                                                    } else {

                                                                        Toast.makeText(
                                                                            appContext,
                                                                            "Please enter correct order amount !",
                                                                            Toast.LENGTH_LONG
                                                                        )
                                                                            .show()
                                                                    }
                                                                } else {

                                                                    Toast.makeText(
                                                                        appContext,
                                                                        "Please enter correct order detail !",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                }
                                                            } else {

                                                                Toast.makeText(
                                                                    appContext,
                                                                    "Please enter order detail !",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                        } else {

                                                            Toast.makeText(
                                                                appContext,
                                                                "Incorrect Token Expiry Date format !",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }

                                                    } else {
                                                        Toast.makeText(
                                                            appContext,
                                                            "Please Setup Store !",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                            }
                                        } else {
                                            Toast.makeText(appContext, "Please enter valid email !", Toast.LENGTH_LONG)
                                                .show()
                                        }

                                    }
                                }
                            }
                            false -> {
                                /*if (Validation().isPhoneValid(
                                        phone,
                                        11,
                                        "^(0)(3)[0-9]{9}\$"
                                    ) || Validation().isPhoneValid(
                                        phone,
                                        13,
                                        "^[+](9)(2)(3)[0-9]{9}\$"
                                    )
                                ) {*/


                                    when (email.isNullOrEmpty()) {
                                        true -> {
                                            val paymentOrder = EPPaymentOrder(
                                                email,
                                                phone,
                                                orderID,
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                totalAmount
                                            )

                                            when (storeConfig.expiryToken.isNullOrEmpty()) {
                                                true -> {
                                                    if (Validation().validStoreID(storeConfig.storeId.toString()) && Validation().validStoreName(
                                                            storeConfig.storeName.toString()
                                                        )
                                                    ) {

                                                        if (Validation().validOrderRef(paymentOrder.orderId.toString()) && Validation().validAmount(
                                                                paymentOrder.orderAmount.toString()
                                                            )
                                                        ) {
                                                            if (Validation().validOrderRefValue(paymentOrder.orderId.toString())) {

                                                                if (Validation().validAmountValue(
                                                                        paymentOrder.orderAmount.toString(),
                                                                        "^\\d{0,6}(\\.\\d{0,2})?\$"
                                                                    )
                                                                ) {
                                                                    val intent = Intent(
                                                                        appContext,
                                                                        Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentMethod")
                                                                    )
                                                                    var paymentOr = paymentOrder
                                                                    paymentOr.orderAmount =
                                                                        Validation().getAmount(paymentOrder.orderAmount)
                                                                    intent.putExtra(CONFIGURATION, storeConfig)
                                                                    intent.putExtra(PAYMENTORDER, paymentOr)
                                                                    appContext.startActivity(intent)
                                                                } else {

                                                                    Toast.makeText(
                                                                        appContext,
                                                                        "Please enter correct order amount !",
                                                                        Toast.LENGTH_LONG
                                                                    )
                                                                        .show()
                                                                }
                                                            } else {

                                                                Toast.makeText(
                                                                    appContext,
                                                                    "Please enter correct order detail !",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }


                                                        } else {

                                                            Toast.makeText(
                                                                appContext,
                                                                "Please enter order detail !",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    } else {
                                                        Toast.makeText(
                                                            appContext,
                                                            "Please Setup Store !",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                                false -> {

                                                    if (Validation().validStoreID(storeConfig.storeId.toString()) && Validation().validStoreName(
                                                            storeConfig.storeName.toString()
                                                        ) && Validation().validExpiryToken(storeConfig.expiryToken.toString())
                                                    ) {

                                                        if (Validation().validExpiryTokenFormat(storeConfig.expiryToken.toString())) {


                                                            if (Validation().validOrderRef(paymentOrder.orderId.toString()) && Validation().validAmount(
                                                                    paymentOrder.orderAmount.toString()
                                                                )
                                                            ) {


                                                                if (Validation().validOrderRefValue(paymentOrder.orderId.toString())) {

                                                                    if (Validation().validAmountValue(
                                                                            paymentOrder.orderAmount.toString(),
                                                                            "^\\d{0,6}(\\.\\d{0,2})?\$"
                                                                        )
                                                                    ) {
                                                                        val intent = Intent(
                                                                            appContext,
                                                                            Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentMethod")
                                                                        )
                                                                        var paymentOr = paymentOrder
                                                                        paymentOr.orderAmount =
                                                                            Validation().getAmount(paymentOrder.orderAmount)
                                                                        intent.putExtra(CONFIGURATION, storeConfig)
                                                                        intent.putExtra(PAYMENTORDER, paymentOr)
                                                                        appContext.startActivity(intent)
                                                                    } else {

                                                                        Toast.makeText(
                                                                            appContext,
                                                                            "Please enter correct order amount !",
                                                                            Toast.LENGTH_LONG
                                                                        )
                                                                            .show()
                                                                    }
                                                                } else {

                                                                    Toast.makeText(
                                                                        appContext,
                                                                        "Please enter correct order detail !",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                }
                                                            } else {

                                                                Toast.makeText(
                                                                    appContext,
                                                                    "Please enter order detail !",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                        } else {

                                                            Toast.makeText(
                                                                appContext,
                                                                "Incorrect Token Expiry Date format !",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }

                                                    } else {
                                                        Toast.makeText(
                                                            appContext,
                                                            "Please Setup Store !",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                            }
                                        }
                                        false -> {
                                            if (Validation().isEmailValid(email)) {
                                                val paymentOrder = EPPaymentOrder(
                                                    email,
                                                    phone,
                                                    orderID,
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    totalAmount
                                                )

                                                when (storeConfig.expiryToken.isNullOrEmpty()) {
                                                    true -> {
                                                        if (Validation().validStoreID(storeConfig.storeId.toString()) && Validation().validStoreName(
                                                                storeConfig.storeName.toString()
                                                            )
                                                        ) {

                                                            if (Validation().validOrderRef(paymentOrder.orderId.toString()) && Validation().validAmount(
                                                                    paymentOrder.orderAmount.toString()
                                                                )
                                                            ) {
                                                                if (Validation().validOrderRefValue(paymentOrder.orderId.toString())) {

                                                                    if (Validation().validAmountValue(
                                                                            paymentOrder.orderAmount.toString(),
                                                                            "^\\d{0,6}(\\.\\d{0,2})?\$"
                                                                        )
                                                                    ) {
                                                                        val intent = Intent(
                                                                            appContext,
                                                                            Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentMethod")
                                                                        )
                                                                        var paymentOr = paymentOrder
                                                                        paymentOr.orderAmount =
                                                                            Validation().getAmount(paymentOrder.orderAmount)
                                                                        intent.putExtra(CONFIGURATION, storeConfig)
                                                                        intent.putExtra(PAYMENTORDER, paymentOr)
                                                                        appContext.startActivity(intent)
                                                                    } else {

                                                                        Toast.makeText(
                                                                            appContext,
                                                                            "Please enter correct order amount !",
                                                                            Toast.LENGTH_LONG
                                                                        )
                                                                            .show()
                                                                    }
                                                                } else {

                                                                    Toast.makeText(
                                                                        appContext,
                                                                        "Please enter correct order detail !",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                }


                                                            } else {

                                                                Toast.makeText(
                                                                    appContext,
                                                                    "Please enter order detail !",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                        } else {
                                                            Toast.makeText(
                                                                appContext,
                                                                "Please Setup Store !",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                                    false -> {

                                                        if (Validation().validStoreID(storeConfig.storeId.toString()) && Validation().validStoreName(
                                                                storeConfig.storeName.toString()
                                                            ) && Validation().validExpiryToken(storeConfig.expiryToken.toString())
                                                        ) {

                                                            if (Validation().validExpiryTokenFormat(storeConfig.expiryToken.toString())) {


                                                                if (Validation().validOrderRef(paymentOrder.orderId.toString()) && Validation().validAmount(
                                                                        paymentOrder.orderAmount.toString()
                                                                    )
                                                                ) {


                                                                    if (Validation().validOrderRefValue(paymentOrder.orderId.toString())) {

                                                                        if (Validation().validAmountValue(
                                                                                paymentOrder.orderAmount.toString(),
                                                                                "^\\d{0,6}(\\.\\d{0,2})?\$"
                                                                            )
                                                                        ) {
                                                                            val intent = Intent(
                                                                                appContext,
                                                                                Class.forName("telenor.com.ep_v1_sdk.ui.activities.EasypayPaymentMethod")
                                                                            )
                                                                            var paymentOr = paymentOrder
                                                                            paymentOr.orderAmount =
                                                                                Validation().getAmount(paymentOrder.orderAmount)
                                                                            intent.putExtra(CONFIGURATION, storeConfig)
                                                                            intent.putExtra(PAYMENTORDER, paymentOr)
                                                                            appContext.startActivity(intent)
                                                                        } else {

                                                                            Toast.makeText(
                                                                                appContext,
                                                                                "Please enter correct order amount !",
                                                                                Toast.LENGTH_LONG
                                                                            )
                                                                                .show()
                                                                        }
                                                                    } else {

                                                                        Toast.makeText(
                                                                            appContext,
                                                                            "Please enter correct order detail !",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()
                                                                    }
                                                                } else {

                                                                    Toast.makeText(
                                                                        appContext,
                                                                        "Please enter order detail !",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                }
                                                            } else {

                                                                Toast.makeText(
                                                                    appContext,
                                                                    "Incorrect Token Expiry Date format !",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }

                                                        } else {
                                                            Toast.makeText(
                                                                appContext,
                                                                "Please Setup Store !",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(
                                                    appContext,
                                                    "Please enter valid email !",
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            }

                                        }
                                    }
                                /*} else {
                                    Toast.makeText(appContext, "Please enter valid phone !", Toast.LENGTH_LONG).show()
                                }*/

                            }
                        }

                    }
                    else{
                        Toast.makeText(appContext, "Please enter Secret Key!", Toast.LENGTH_LONG).show()
                    }

                }
                else{
                    Toast.makeText(appContext, "Please enter valid BaseUrl !", Toast.LENGTH_LONG).show()
                }
            }
            false->{
                Toast.makeText(appContext, "Please enter BaseUrl !", Toast.LENGTH_LONG).show()
            }
        }

    }



}