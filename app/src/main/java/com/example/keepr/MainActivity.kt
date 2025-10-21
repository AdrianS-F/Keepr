package com.example.keepr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.keepr.data.KeeprDatabase
import com.example.keepr.ui.theme.KeeprTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val db = KeeprDatabase.get(this)
        com.example.keepr.util.DatabaseUtils.verifyDatabase(this)
// example: val collectionsDao = db.collectionsDao()


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KeeprTheme {
                com.example.keepr.ui.KeeprApp()
            }
        }

    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KeeprTheme {
        Greeting("Android")
    }
}