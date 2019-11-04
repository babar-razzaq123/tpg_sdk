package telenor.com.ep_v1_sdk.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class InternetHelper {


    fun isInternetConnected(appContext: Context):Boolean{
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        return activeNetwork?.isConnectedOrConnecting == true
    }

}