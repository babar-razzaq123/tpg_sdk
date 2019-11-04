package telenor.com.ep_v1_sdk.util

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import telenor.com.ep_v1_sdk.R

/**
 * Created by fatihsantalu on 20.11.2018
 */

class MaskEditText : TextInputEditText {

  var maskTextWatcher: MaskTextWatcher? = null

  var mask: String? = null
    set(value) {
      field = value
      if (value.isNullOrEmpty()) {
        removeTextChangedListener(maskTextWatcher)
      } else {
        maskTextWatcher = MaskTextWatcher(this, mask!!)
        addTextChangedListener(maskTextWatcher)
      }
    }

  val rawText: String?
    get() {
      val formatted = text
      return maskTextWatcher?.unformat(formatted) ?: formatted.toString()
    }

  constructor(context: Context) : super(context){

    blockContextMenu()
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    init(context, attrs)

    blockContextMenu()
  }

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
      super(context, attrs, defStyleAttr) {
    init(context, attrs)

    blockContextMenu()
  }

  private fun init(context: Context, attrs: AttributeSet?) {
    attrs?.let {
      val a = context.obtainStyledAttributes(it, R.styleable.MaskEditText)
      with(a) {
        mask = getString(R.styleable.MaskEditText_met_mask)
        recycle()
      }
    }
  }

  private fun blockContextMenu() {
      this.customSelectionActionModeCallback = BlockedActionModeCallback()
      this.isLongClickable = false
      this.setOnTouchListener { v, event ->
        this@MaskEditText.clearFocus()
        false
      }
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
}