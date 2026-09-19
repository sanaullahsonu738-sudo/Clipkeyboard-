package com.example.clipkeyboard

import android.content.ClipboardManager
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.HorizontalScrollView
import android.widget.TextView

class ClipKeyboardService : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private lateinit var keyboardView: KeyboardView
    private lateinit var qwertyKeyboard: Keyboard
    private lateinit var suggestionText: TextView
    private lateinit var suggestionScroll: HorizontalScrollView

    private var capsOn = false

    private val clipboardManager: ClipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private val clipListener = ClipboardManager.OnPrimaryClipChangedListener {
        updateSuggestion()
    }

    override fun onCreate() {
        super.onCreate()
        clipboardManager.addPrimaryClipChangedListener(clipListener)
    }

    override fun onDestroy() {
        clipboardManager.removePrimaryClipChangedListener(clipListener)
        super.onDestroy()
    }

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)

        keyboardView = view.findViewById(R.id.keyboard_view)
        suggestionText = view.findViewById(R.id.suggestion_text)
        suggestionScroll = view.findViewById(R.id.suggestion_scroll)

        qwertyKeyboard = Keyboard(this, R.xml.qwerty)
        keyboardView.keyboard = qwertyKeyboard
        keyboardView.setOnKeyboardActionListener(this)

        return view
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        updateSuggestion()
    }

    private fun updateSuggestion() {
        if (!::suggestionText.isInitialized) return

        val clip = clipboardManager.primaryClip
        val rawText = if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).coerceToText(this)?.toString()
        } else null

        if (rawText != null && EmojiCodeTranslator.looksTranslatable(rawText)) {
            val translated = EmojiCodeTranslator.translate(rawText)
            suggestionText.text = translated
            suggestionText.visibility = View.VISIBLE
            suggestionText.setOnClickListener {
                currentInputConnection?.commitText(translated, 1)
                suggestionText.visibility = View.GONE
            }
        } else {
            suggestionText.visibility = View.GONE
            suggestionText.setOnClickListener(null)
        }
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic = currentInputConnection ?: return
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> ic.deleteSurroundingText(1, 0)
            Keyboard.KEYCODE_SHIFT -> {
                capsOn = !capsOn
                qwertyKeyboard.isShifted = capsOn
                keyboardView.invalidateAllKeys()
            }
            -4 -> {
                val sentAction = sendDefaultEditorAction(true)
                if (!sentAction) ic.commitText("\n", 1)
            }
            else -> {
                var code = primaryCode.toChar()
                if (capsOn && code.isLetter()) {
                    code = code.uppercaseChar()
                    capsOn = false
                    qwertyKeyboard.isShifted = false
                    keyboardView.invalidateAllKeys()
                }
                ic.commitText(code.toString(), 1)
            }
        }
    }

    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}
    override fun onText(text: CharSequence?) {}
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}
}
