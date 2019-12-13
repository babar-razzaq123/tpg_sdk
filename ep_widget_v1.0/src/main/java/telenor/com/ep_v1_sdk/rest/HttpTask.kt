package telenor.com.ep_v1_sdk.rest

import android.content.Context
import android.os.AsyncTask
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import telenor.com.ep_v1_sdk.R
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*


class HttpTask(  context: Context,callback: (String?) -> Unit) : AsyncTask<String, Unit, String>()  {

    private val TIMEOUT: Int = 300000
    var callback = callback
    var context = context

    override fun doInBackground(vararg params: String): String? {

        trustEveryone()

/*
        //  SSL Certification work start

        val cf = CertificateFactory.getInstance("X.509", "BC")
        val caInput = BufferedInputStream(context.resources.openRawResource(R.raw.telclient))
        val ca: Certificate
        try {
            ca = cf.generateCertificate(caInput)
            println("ca=" + (ca as X509Certificate).subjectDN)
        } finally {
            caInput.close()
        }
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)
        val contextP = SSLContext.getInstance("TLS")
        contextP.init(null, tmf.trustManagers, null)
        // Below 2 line are unused here
//        httpClient.sslSocketFactory(contextP.socketFactory, tmf.trustManagers[0] as X509TrustManager)
//            .hostnameVerifier(HostnameVerifier { s, sslSession -> true })

        // SSL Certification work end

        */
        val url = URL(params[1])
        val httpClient = url.openConnection() as HttpURLConnection
        // Below line for SSL also add above HttpsURLConnection
//        httpClient.sslSocketFactory = contextP.socketFactory
        httpClient.readTimeout = TIMEOUT
        httpClient.connectTimeout = TIMEOUT
        httpClient.requestMethod = params[0]

        if (params[0] == "POST") {
            httpClient.instanceFollowRedirects = false
            httpClient.doOutput = true
            httpClient.doInput = true
            httpClient.useCaches = false
            httpClient.setRequestProperty("Content-Type", "application/json")
            httpClient.setRequestProperty("Accept", "application/json")
        }
        try {
            if (params[0] == "POST") {
                httpClient.connect()
                val os = httpClient.outputStream
                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                writer.write(params[2])
                writer.flush()
                writer.close()
                os.close()
            }
            if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
                val stream = BufferedInputStream(httpClient.inputStream)
                val data: String = readStream(inputStream = stream)
                return data
            } else {
                println("ERROR ${httpClient.responseCode}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            httpClient.disconnect()
        }

        return null
    }

    fun readStream(inputStream: BufferedInputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        return stringBuilder.toString()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        callback(result)
    }

    fun trustEveryone() = try {
        HttpsURLConnection.setDefaultHostnameVerifier(object : HostnameVerifier {
            override fun verify(hostname: String, session: SSLSession): Boolean {
                return true
            }
        })
        val context = SSLContext.getInstance("TLSv1.2")
        context.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate?> {
//                    val acceptedIssuers: Array<X509Certificate?>
//                    return arrayOfNulls<X509Certificate>(0)
                return arrayOfNulls<X509Certificate>(size = 0)
            }



            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }
        }), SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(
            context.getSocketFactory()
        )
    } catch (e: Exception) { // should never happen
        e.printStackTrace()
    }


}
