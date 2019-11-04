package telenor.com.ep_v1_sdk.config

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import telenor.com.ep_v1_sdk.config.model.EPConfiguration
import telenor.com.ep_v1_sdk.config.model.PaymentMethodBaseResponse


/**
 * @author Mirza Adil
 * @date 03-10-2019
 */
class EPSharedPrefDataSource {

    private val NAME = "Easypay"
    private val MODE = Context.MODE_PRIVATE
    private var context: Context

    constructor(appContext: Context){
        context = appContext
    }


    fun getEasyPayConfig(): EPConfiguration {
        var cacheValue = getCacheValue(context, CONFIGURATION)
        if (cacheValue == null) {
            cacheValue = "{\"secretKey\":\"\",\"secretName\":\"\",\"storeId\":0}"
        }

        return Gson().fromJson(cacheValue, EPConfiguration::class.java)
    }

    fun setEasyPayConfig(data: EPConfiguration) {
        if (data != null) {
            println(data)
            saveCacheValue(context, CONFIGURATION, Gson().toJson(data))
        }
    }


    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }


    /**
     * @param mContext      Application Base Context.
     * @param CacheKeyTitle Title of key.
     * @param cacheValue
     * In this function save shared preferences Value.
     */

    fun saveCacheValue(
        mContext: Context, CacheKeyTitle: String,
        cacheValue: String
    ) {

        try {
            val settings = mContext.getSharedPreferences(
                NAME, MODE
            )
            val editor = settings.edit()

            editor.putString(CacheKeyTitle, cacheValue)
            editor.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /**
     * @param mContext      Application Base Context.
     * @param CacheKeyTitle
     * In this function get shared preferences Value.
     */

    fun getCacheValue(mContext: Context, CacheKeyTitle: String): String? {
        var value: String? = null
        val settings = mContext.getSharedPreferences(
            NAME,
            MODE
        )
        try {
            value = settings.getString(CacheKeyTitle, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return value
    }


    /**
     * @param mContext      Application Base Context
     * @param CacheKeyTitle
     * In this function remove shared preferences Value.
     */

    fun removeCacheValue(mContext: Context, CacheKeyTitle: String) {
        try {
            val settings = mContext.getSharedPreferences(
                NAME, MODE
            )
            val editor = settings.edit()
            editor.remove(CacheKeyTitle)
            editor.commit()
        } catch (e: Exception) {
            //e.printStackTrace();
        }

    }


}