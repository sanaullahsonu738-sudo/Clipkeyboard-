package com.example.clipkeyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 48)
        }

        val info = TextView(this).apply {
            text = "Step 1: Enable ClipKeyboard in system settings.\n\n" +
                    "Step 2: Switch to it from the keyboard picker.\n\n" +
                    "Once it's your active keyboard, copy a string of " +
                    "\":six: :nine: :regional_indicator_a: :five: :five:\" " +
                    "style shortcodes anywhere, then tap into any text field " +
                    "here -- the translated text (e.g. \"69a55\") will appear " +
                    "as a tappable suggestion above the keyboard."
            textSize = 15f
            setPadding(0, 0, 0, 48)
        }
        root.addView(info)

        val enableButton = Button(this).apply {
            text = "1. Open Keyboard Settings"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            }
        }
        root.addView(enableButton)

        val switchButton = Button(this).apply {
            text = "2. Switch Keyboard"
            setOnClickListener {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
        }
        root.addView(switchButton)

        setContentView(root)
    }
}
