package com.example.actesimple

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.actesimple.ui.screens.MainAppScreen
import com.example.actesimple.ui.theme.ActeSimpleTheme
import com.example.actesimple.ui.viewmodel.ActeSimpleViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ActeSimpleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ActeSimpleTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}
