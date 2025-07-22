package com.velundra.utils

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.velundra.R

abstract class BaseActivity : AppCompatActivity() {

    open fun statusBarColorRes(): Int = R.color.white
    open fun lightStatusBarIcons(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = lightStatusBarIcons()
    }

    /**
     * Panggil ini dari subclass SETELAH binding siap.
     */
    protected fun applyEdgeToEdgeInsets(rootView: View, applyBottomPadding: Boolean = false) {
        rootView.setBackgroundColor(ContextCompat.getColor(this, statusBarColorRes()))

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                if (applyBottomPadding) systemBars.bottom else 0
            )
            insets
        }
    }
}