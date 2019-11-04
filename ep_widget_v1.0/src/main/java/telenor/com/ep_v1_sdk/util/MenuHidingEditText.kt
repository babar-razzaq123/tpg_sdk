package telenor.com.ep_v1_sdk.util

import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.util.AttributeSet
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import android.text.InputFilter
import android.util.Log


class MenuHidingEditText : AppCompatEditText {

    private val mContext: Context

    constructor(context: Context) : super(context) {
        this.mContext = context

        blockContextMenu()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.mContext = context

        blockContextMenu()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        this.mContext = context

        blockContextMenu()
    }

    private fun blockContextMenu() {
        this.customSelectionActionModeCallback = BlockedActionModeCallback()
        this.isLongClickable = false
        this.setOnTouchListener { v, event ->
            this@MenuHidingEditText.clearFocus()
            false
        }

//        val clipBoard = mContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
//        clipBoard.addPrimaryClipChangedListener {  }
//        val flag = clipBoard.hasPrimaryClip()
//        Log.d("clip exist", "Flag value "+flag)
//        if (flag) {
//            this.setFilters(arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
//                if (source.length > 1) {
//                    ""
//                } else null
//            }))
//        }
//        val startSelection = this.getSelectionStart()
//        val endSelection= this.getSelectionEnd()
//
//        if (startSelection != endSelection) {
//            //DO SOMETHING ...
//            Log.d("Selected clip ", " value -> "+this.text)
//
//            this.setFilters(arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
//                if (source.length > 1) {
//                    ""
//                } else null
//            }))
//        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // setInsertionDisabled when user touches the view
            this.setInsertionDisabled()
        }
        return super.onTouchEvent(event)
    }

    private fun setInsertionDisabled() {
        try {
            val editorField = TextView::class.java.getDeclaredField("mEditor")
            editorField.isAccessible = true
            val editorObject = editorField.get(this)

            val editorClass = Class.forName("android.widget.Editor")
            val mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled")
            mInsertionControllerEnabledField.isAccessible = true
            mInsertionControllerEnabledField.set(editorObject, false)
        } catch (ignored: Exception) {
            // ignore exception here
        }

    }

    override fun isSuggestionsEnabled(): Boolean {
        return false
    }

    private inner class BlockedActionModeCallback : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            menu.clear()
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {}
    }

    override fun getSelectionStart(): Int {
        for (element in Thread.currentThread().stackTrace) {
            if (element.methodName == "canPaste") {
                return -1
            }
        }
        return super.getSelectionStart()
    }

}