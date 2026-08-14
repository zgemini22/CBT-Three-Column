package com.threecolumn.cbt

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.threecolumn.cbt.ui.CbtNavHost
import com.threecolumn.cbt.ui.theme.ThreeColumnCbtTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val application = application as CbtApplication
        setContent {
            ThreeColumnCbtTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CbtNavHost(application = application)
                }
            }
        }
    }
}
