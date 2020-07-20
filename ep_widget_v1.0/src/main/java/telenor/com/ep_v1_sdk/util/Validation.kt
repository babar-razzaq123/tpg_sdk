package telenor.com.ep_v1_sdk.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec



class Validation{


    fun isPhoneValid(phoneNumber : String , limit :   Int , regex : String):Boolean{


        var isValid:Boolean

        val pattern: Pattern
        val matcher: Matcher

        val MOBILE_REGEX = regex

        pattern = Pattern.compile(MOBILE_REGEX)
        matcher = pattern.matcher(phoneNumber)

        isValid =phoneNumber.length == limit
        if(isValid)
            isValid=matcher.matches()

        return isValid

    }

    fun isCCPhoneValid(phoneNumber : String , limit :   Int ):Boolean{


        var isValid:Boolean

        isValid =phoneNumber.length <= limit


        return isValid

    }

    fun isCCPhoneValid(number: String, minLength: Int, maxLength: Int): Boolean {
        var bool :Boolean = false
        if(number.length in (minLength )..(maxLength )){
            if(isCCPhoneNumberValid(number))
                bool=true
        }
        return bool

    }

    fun isCCPhoneNumberValid(phoneNumer: String): Boolean {


        var isValid:Boolean =false
        val pattern: Pattern
        val matcher: Matcher

        val phoneNoRegex = "^.{0,30}\$"
        pattern = Pattern.compile(phoneNoRegex)
        matcher = pattern.matcher(phoneNumer)

        isValid=matcher.matches()


        return isValid
    }

    fun  encrypt(value: String, secretKey: String): String{



        var encryptedValue: String? = ""

        var crypted: ByteArray? = null
        try {


            val skey = SecretKeySpec(secretKey.toByteArray(), "AES")

            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, skey)
            crypted = cipher.doFinal(value.toByteArray())
            encryptedValue = android.util.Base64.encodeToString(crypted, android.util.Base64.DEFAULT)

        } catch (e: Exception) {
            println(e.toString())
        }


        return encryptedValue!!
    }
    //  METHOD TO DECRYPT THE ENCRYPTED REQUEST
    fun decrypt(encrypted: String, secretKey: String): String {
        var decrypted = ""
        var output: ByteArray? = null
        try {


            val skey = SecretKeySpec(secretKey.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, skey)
            output = cipher.doFinal(android.util.Base64.decode(encrypted, android.util.Base64.DEFAULT))
            decrypted = String(output)
        } catch (e: Exception) {
            println(e.toString())
        }

        return decrypted
    }

    fun decryptHashKey(hashKey: String , orderId: String, storeId: String): String {

        var key = orderId + storeId
        if (key.length === 16) {
//            opsDecrypted = decrypt(key,hashKey);
//            return opsDecrypted;
            //
            var decrypted = ""
            var output: ByteArray? = null
            try {


                val skey = SecretKeySpec(key.toByteArray(), "AES")
                val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
                cipher.init(Cipher.DECRYPT_MODE, skey)
                output =
                    cipher.doFinal(android.util.Base64.decode(hashKey, android.util.Base64.DEFAULT))
                decrypted = String(output)
            } catch (e: Exception) {
                println(e.toString())
            }

            return decrypted
            //
        } else {
            while (key.length < 16) {
                key = key + "xEuGlK";
                if (key.length > 16) {
                    key = key.substring(0, key.length - (key.length - 16));
                    break;
                }
            }
            if (key.length > 16) {
                key = key.substring(0, key.length - (key.length - 16));
            }
            if (key.length === 16) {
                //opsDecrypted = Cipher.decrypt(key,encryptedhashfromservice);
                //opsDecryptedFInal = opsDecrypted.substring(0,16);
                //
                var decrypted = ""
                var output: ByteArray? = null
                try {


                    val skey = SecretKeySpec(key.toByteArray(), "AES")
                    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
                    cipher.init(Cipher.DECRYPT_MODE, skey)
                    output = cipher.doFinal(
                        android.util.Base64.decode(
                            hashKey,
                            android.util.Base64.DEFAULT
                        )
                    )
                    decrypted = String(output)
                } catch (e: Exception) {
                    println(e.toString())
                }

                return decrypted
                //
            }
        }

        //
        return ""
    }
        //


    fun isEmailValid(email: String): Boolean {


        var isValid:Boolean =false
        val pattern: Pattern
        val matcher: Matcher

        val EmailREGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z.-]{3,}\\.[A-Za-z]{2,}"

        pattern = Pattern.compile(EmailREGEX)
        matcher = pattern.matcher(email)

        isValid=matcher.matches() && email.length<50


        return isValid
    }

    fun isAccountNumberValid(accountNumber: String): Boolean {


        var isValid:Boolean =false
        val pattern: Pattern
        val matcher: Matcher

        val accountNoRegex = "^.{8,45}\$"
        pattern = Pattern.compile(accountNoRegex)
        matcher = pattern.matcher(accountNumber)

        isValid=matcher.matches()


        return isValid
    }

    fun isUrlValid(url: String): Boolean {


        var isValid:Boolean =false
        val pattern: Pattern
        val matcher: Matcher

        val urlREGEX = "^(http|https)://.*\$"

        pattern = Pattern.compile(urlREGEX)
        matcher = pattern.matcher(url)

        isValid=matcher.matches()


        return isValid
    }

    fun validToken(any: Any): Boolean {

        return true
    }
    fun validExpiryToken(token: String): Boolean {
        var bool :Boolean = false
        bool = !token.isNullOrEmpty()
        return bool
    }
    fun validStoreID(storeId: String): Boolean {
        var bool :Boolean = false

        if(!storeId.isNullOrEmpty() ){
            if(storeId.toDouble()>0)
            bool = true
        }
        return bool
    }

    fun validStoreName(storeName: String): Boolean {
        var bool :Boolean = false
        bool = !storeName.isNullOrEmpty()
        return bool
    }
    fun validBankIdentifier(bankID: String): Boolean {
        var bool :Boolean = false
        bool = !bankID.isNullOrEmpty()
        return bool
    }
    fun validEmail(email: String): Boolean {
        var bool :Boolean = false
        bool = !email.isNullOrEmpty()
        return bool
    }
    fun validPhone(phone: String): Boolean {
        var bool :Boolean = false
        bool = !phone.isNullOrEmpty()
        return bool
    }

    fun validOrderRef(order: String): Boolean {
        var bool :Boolean = false
        bool = !order.isNullOrEmpty()
        return bool
    }
    fun validSecretKey(secretKey: String): Boolean {
        var bool :Boolean = false
        bool = !secretKey.isNullOrEmpty()
        return bool
    }
    fun validOrderRefValue(order: String): Boolean {
        var bool :Boolean = false
        bool = order.length<=20 && !order.contentEquals("0")
        return bool
    }
    fun validAmount(amount: String): Boolean {
        var bool :Boolean = false
        bool = !amount.isNullOrEmpty()

        return bool
    }
    fun validAmountValue(amount: String, regex: String): Boolean {


        var isValid:Boolean =false

        if(amount.toDouble() >0){

        val pattern: Pattern
        val matcher: Matcher

        val AmountValueREGEX = regex

        pattern = Pattern.compile(AmountValueREGEX)
        matcher = pattern.matcher(amount)

            isValid=matcher.matches()
        }
        return isValid
    }
    fun getAmount(str: String): String {
        var returnStr = str
        if (str.contains(".")) {
            val temp = str.substring(str.indexOf('.') + 1, str.length)
            val pattern = Pattern.compile("[^1-9]+")
            val matcher = pattern.matcher(temp)

            if (matcher.matches()) {
                returnStr = str.substring(0, str.indexOf('.'))
            } else {
                returnStr = str
            }
        }

        return returnStr
    }
    fun validURL(url: String): Boolean {
        var bool :Boolean = false
        bool = !url.isNullOrEmpty()
        return bool
    }
    fun validExpiryTokenFormat(date:String):Boolean{
        var d1 =date.replace("-","")
        var d2 = d1.replace(":","")
        var sdf = SimpleDateFormat("yyyyMMdd HHmmss")
        try
        {
            sdf.parse(d2)
        }
        catch (ex :java.lang.Exception){
            return false

        }

        return true
    }

    fun validCreditCard(cardNumber:String , regex :String , limit :Int):Boolean{
        var isValid:Boolean

        val pattern: Pattern
        val matcher: Matcher

        val CARD_REGEX = regex

        pattern = Pattern.compile(CARD_REGEX)
        matcher = pattern.matcher(cardNumber)

        isValid =cardNumber.length == limit
        if(isValid)
            isValid=matcher.matches()

        return isValid

    }

    fun isValidMonth(month : String , regex: String ,limit: Int):Boolean{
        var isValid:Boolean

        val pattern: Pattern
        val matcher: Matcher

        val MONTH_REGEX = regex

        pattern = Pattern.compile(MONTH_REGEX)
        matcher = pattern.matcher(month)

        isValid =month.length == limit
        if(isValid)
            isValid=matcher.matches()

        return isValid

    }
    fun isValidDate(month : String , year: String ):Boolean{


        var isValid:Boolean =false
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("dd")
        val formattedDate = df.format(c)
        val day = formattedDate
        val strThatDay = "$year/$month/$day"
        val formatter = SimpleDateFormat("yyyy/MM/dd")
        var date1: Date? = null
        try {
            date1 = formatter.parse(strThatDay)//catch exception
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }


        val selectedDate = Calendar.getInstance()
        selectedDate.time = date1

        var current = Calendar.getInstance()

        val diff =  selectedDate.getTimeInMillis()  - current.getTimeInMillis()//result in millis

        var days = diff / (24 * 60 * 60 * 1000)

        if(days.toInt()>=0){

            isValid = true
        }
        return isValid

    }
    fun isValidYear(year : String , regex: String ,limit: Int):Boolean{
        var isValid:Boolean

        val pattern: Pattern
        val matcher: Matcher

        val YEAR_REGEX = regex

        pattern = Pattern.compile(YEAR_REGEX)
        matcher = pattern.matcher(year)

        isValid =year.length == limit
        if(isValid)
            isValid=matcher.matches()

        return isValid

    }

    fun isYearValid(year : String ):Boolean{
        var isValid:Boolean =false
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("YYYY")
        val formattedDate = df.format(c)
        val currentYear = formattedDate

        if(year.toInt()>=currentYear.toInt()){

            isValid = true
        }
        return isValid

    }
    fun isValidCVV(CVV : String ):Boolean{
        var bool :Boolean = false
        bool = !CVV.isNullOrEmpty() && CVV.length == 3
        return bool

    }

    fun isCNICValid(cnic: String, limit: Int, regex: String): Boolean {
        var isValid:Boolean

        val pattern: Pattern
        val matcher: Matcher

        val YEAR_REGEX = regex

        pattern = Pattern.compile(YEAR_REGEX)
        matcher = pattern.matcher(cnic)

        isValid =cnic.length == limit
        if(isValid)
            isValid=matcher.matches()

        return isValid
    }

    fun isCardValid(account: String, regex: String, limit: Int): Boolean {
            var isValid:Boolean

        val pattern: Pattern
        val matcher: Matcher

        val CARD_REGEX = regex

        pattern = Pattern.compile(CARD_REGEX)
        matcher = pattern.matcher(account)

        isValid =account.length == limit
        if(isValid)
            isValid=matcher.matches()

        return isValid
    }

    fun isAccountValid(acount: String, minLength: Int, maxLength: Int): Boolean {
        var bool :Boolean = false
        if(acount.length in (minLength )..(maxLength )){
            if(isAccountNumberValid(acount))
            bool=true
        }
        return bool

    }
}