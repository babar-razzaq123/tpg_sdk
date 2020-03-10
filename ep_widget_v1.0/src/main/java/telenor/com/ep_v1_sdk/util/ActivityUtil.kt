package telenor.com.ep_v1_sdk.util

import android.content.res.ColorStateList
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import telenor.com.ep_v1_sdk.R
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.widget.ContentLoadingProgressBar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_form_main.*


class ActivityUtil{
      var isLoading: Boolean =false

    fun addFragmentToActivity(manager: FragmentManager, fragment: Fragment, frameId: Int) {

        val transaction = manager.beginTransaction()
        transaction.add(frameId, fragment)
        transaction.commit()

    }

    fun replaceFragment(appCompatActivity: AppCompatActivity,fragment: Fragment, addToBackStack: Boolean, frameId: Int, tag: String) {

        if (appCompatActivity.supportFragmentManager != null) {
            val fragmentTransaction = appCompatActivity.supportFragmentManager.beginTransaction()
            if (fragment != null)
                fragmentTransaction.replace(frameId, fragment, tag)
            if (addToBackStack)
                fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
            if (!appCompatActivity.isFinishing) {
                fragmentTransaction.commit()
            }

        }
    }

    fun loadingStarted(progressBar: ProgressBar , appCompatActivity: AppCompatActivity) {
        hideKeyboard(appCompatActivity)
        progressBar.visibility = View.VISIBLE
        appCompatActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        isLoading=true
    }

    fun loadingStop(progressBar: ProgressBar , appCompatActivity: AppCompatActivity) {
        progressBar.visibility = View.GONE
        appCompatActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        isLoading=false
    }

    fun loadingStarted(progressBar: ProgressBar ,relativeLayout: RelativeLayout,textView: TextView,string: String ,appCompatActivity: AppCompatActivity) {

        hideKeyboard(appCompatActivity)
        progressBar.visibility = View.VISIBLE
        relativeLayout.visibility =View.VISIBLE
        textView.text = string
        isLoading=true
        if(!string.isNullOrEmpty()){
            relativeLayout.background = ContextCompat.getDrawable(appCompatActivity, R.drawable.button_grey)
            relativeLayout.alpha =0.8f
        }
        else{
            relativeLayout.background =ContextCompat.getDrawable(appCompatActivity, R.drawable.button_grey)
            relativeLayout.alpha  =0.5f
        }
        appCompatActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun loadingStop(progressBar: ProgressBar ,relativeLayout: RelativeLayout,textView: TextView, appCompatActivity: AppCompatActivity) {
        progressBar.visibility = View.GONE
        relativeLayout.visibility =View.GONE
        appCompatActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        isLoading=false

    }

    fun loadingGifStarted(imageView: ImageView, appCompatActivity: AppCompatActivity) {
        hideKeyboard(appCompatActivity)
        imageView.visibility = View.VISIBLE
        Glide.with(appCompatActivity)
            .asGif()
            .load(R.drawable.ma_loader_gif_updated)
            .into(imageView)
        appCompatActivity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        isLoading=true
    }

    fun loadingGifStop(imageView: ImageView , appCompatActivity: AppCompatActivity) {
        imageView.visibility = View.GONE
        appCompatActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        isLoading=false
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}