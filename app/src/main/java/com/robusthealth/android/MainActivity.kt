package com.robusthealth.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.robusthealth.android.navigation.RobustHealthApp
import com.robusthealth.android.ui.theme.RobustHealthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RobustHealthTheme {
                RobustHealthApp()
            }
        }
    }
}
