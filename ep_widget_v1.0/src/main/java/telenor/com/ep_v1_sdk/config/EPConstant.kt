package telenor.com.ep_v1_sdk.config

// Staging URL
const val BASE_URL = "https://easypaystg.easypaisa.com.pk/easypay-service/rest"
// Production URL
//const val BASE_URL = "https://easypay.easypaisa.com.pk/easypay-service/rest"
// Local QA URL
//const val BASE_URL = "http://sl-tpg-57674.systemsltd.local:9081/easypay-service/rest"
const val CONFIGURATION = "config"
const val ENVIRONMENT = "environment"
const val PAYMENTORDER = "paymentOrder"
const val STOREID ="storeId"
const val ORDERID="orderId"
const val TRANSACTIONAMOUNT ="transactionAmount"
const val MOBILEACCOUTNUMBER="mobileAccountNo"
const val MSISDN="msisdn"
const val EMAILADDRESS ="emailAddress"
const val BANKIDNUMBER ="bankIdentificationNumber"
const val CREDITCARDNUMBER ="ccNumber"
const val EXPIRYMONTH ="expMonth"
const val EXPIRYYEAR ="expYear"
const val MERCHANTACCOUNTID ="merchantAccountId"
const val CVV ="cvv"
const val ENCRYPTEDREQUEST ="encryptedHashRequest"
const val TOKENEXPIRY ="tokenExpiry"
const val AMOUNT ="amount"
const val STORENAME ="storeName"
const val CELLPHONE="cellPhone"
const val EMAIL ="email"
const val BANKCODE ="bankCode"
const val CNIC ="cnic"
const val PAYMENTINSTRUMENT ="paymentInstrument"
const val BANKACCOUNTNUMBER ="bankAccountNumber"
const val WALLETACCOUNTNUMBER ="walletAccountNumber"
const val CARDNUMBER ="cardNumber"
const val CHARITYORDER = "charityOrder"
const val DONATIONRESPONSE ="donationResponse"
const val OTP ="otp"
const val ORDERREFNUMBER ="orderRefNumber"
const val TRANSACTIONREFNUMBER ="transactionRefNumber"
const val TRANSACTIONDATETIME ="transactionDateTime"

const val MPGSFLAG ="mpgsFlag"
const val CCRESPONSESUCCESS ="ccResponseSuccess"
const val PAYMENTCCCARDRESPONSE ="paymentCCCardResponse"
const val PAYMENTTYPE ="paymenttype"
const val LOADINGMSG ="loadingMsg"
const val PAYMENTURL ="paymenturl"
const val TRANSACTIONTYPE ="transactionType"
const val DIRECTDEBITBANK ="directdebitbank"
const val PAYMENTRESPONSE ="paymentresponse"
const val CHARITYRESULT = "charityresult"
const val CHARITYRESPONSE = "charityresponse"
const val TRANSACTIONID = "transactionId"
const val FEEDBACKMESSAGE= "feedbackDes"
const val FEEDBACKTYPE ="feedbackName"
const val MPGSREATTEMPTCOMPLETEDFLAG ="mpgsReattemptCompletedFlag"
const val MIGSREATTEMPTCOMPLETEDFLAG ="migsReattemptCompletedFlag"
const val REATTEMPTFAILUREMSG = "reattemptFailureMsg"
const val QUERYDRREATTEMPTCOUNT = "queryDRReAttemptCount"
const val VERSION = "version"
const val CCTRANSACTIONTYPE ="ccTransactionType"
const val TRANSACTIONQUERYDRCOMMAND = "transactionQueryDRCommand"
const val MPGSORMIGS = "mpgsOrMigs"

const val ACCESSTOKEN ="accessToken"
const val RESPONSECODE ="responseCode"
const val RESPONSEMETHOD ="responseMethod"

const val TRANSACTIONSTATUS ="transactionStatus"

const val GET ="GET"
const val POST ="POST"
const val ENCRYPT_KEY_EQUAL = "="
const val ENCRYPT_KEY_AMPERSAND = "&"

// initiate-payment-methods request KEYs start
const val INITIATEPAYMENTMETHODS_ENDPOINT = "/checkout/initiate-payment-methods"
const val ENCRYPT_AMOUNT = "amount"
const val ENCRYPT_BANKIDENTIFICATIONNUM = "bankIdentificationNum"
const val ENCRYPT_EMAILADDRESS = "emailAddress"
const val ENCRYPT_EXPIRYDATE = "expiryDate"
const val ENCRYPT_MOBILENUM = "mobileNum"
const val ENCRYPT_ORDERREFNUM = "orderRefNum"
const val ENCRYPT_PAYMENTMETHOD = "paymentMethod"
const val ENCRYPT_STOREID = "storeId"
const val VALUE_INITIAL_REQUEST = "InitialRequest"


// initiate-payment-methods request KEYs end

// initiate-transaction MA request KEYs start
const val MA_INITIATETRANSACTION_ENDPOINT = "/checkout/ma/initiate-transaction"

// initiate-transaction MA request KEYs end


// initiate-transaction OTC request KEYs start
const val OTC_INITIATETRANSACTION_ENDPOINT = "/checkout/otc/initiate-transaction"

// initiate-transaction OTC request KEYs end


// initiate-transaction CC request KEYs start
const val CC_INITIATETRANSACTION_ENDPOINT = "/checkout/cc/initiate-transaction"
const val ENCRYPT_CC_EXPIRY_MONTH = "ccExpiryMonth"
const val ENCRYPT_CC_EXPIRY_YEAR = "ccExpiryYear"
const val ENCRYPT_CC_NUM = "ccNum"
const val ENCRYPT_CC_CVV = "cvv"
const val ENCRYPT_MERCHANT_ACCOUNT_ID = "merchantAccountId"
const val ENCRYPT_TRANSACTION_DATETIME = "transactionDateTime"
const val ENCRYPT_TRANSACTION_ID = "transactionId"

// initiate-transaction CC request KEYs end


// initiate-transaction DD request KEYs start
const val DD_INITIATETRANSACTION_ENDPOINT = "/checkout/dd/initiate-transaction"
const val ENCRYPT_DD_BANK_ACCOUNT = "bankAccount"
const val ENCRYPT_DD_CNIC = "cnic"
const val ENCRYPT_DD_EMAILADDRESS = "emailAddress"
const val ENCRYPT_DD_MOBILE_WALLET = "mobileWallet"

// initiate-transaction DD request KEYs end

const val MA_DONATIONS_ENDPOINT = "/checkout/ma/donations"
const val SUBMIT_OTP_ENDPOINT = "/checkout/dd/submit-otp"
const val RESEND_OTP_ENDPOINT = "/checkout/dd/resend-otp"
const val CC_TRANSACTION_STATUS_ENDPOINT = "/checkout/cc/transaction-status"
const val CC_PRE_VALIDATION_ENDPOINT = "/checkout/cc/pre-validations"
