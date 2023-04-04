package com.example.kotlinexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import org.w3c.dom.Text
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val authClient = Authentication(applicationContext);
        val token_display = findViewById<TextView>(R.id.token_display)
        fun showIDToken() {
            if (authClient.idToken.isNotEmpty()) {
                token_display.text = authClient.idToken
            } else {
               token_display.text = "No token found!"
            }
        }
        fun showAccessToken() {
            if(authClient.accessToken.isNotEmpty()) {
                token_display.text = authClient.accessToken;
            } else {
                token_display.text = "No token found"
            }
        }
        val statusTextView = findViewById<TextView>(R.id.signInStatus)
        val signInButton = findViewById<Button>(R.id.signIn_button);
        signInButton.setOnClickListener {
            thread(
                start=true,
                isDaemon = false,
                block = fun() { return authClient.signIn(this, statusTextView) }
            )
        };

        findViewById<Button>(R.id.show_accessToken).setOnClickListener { showAccessToken() };
        findViewById<Button>(R.id.show_idToken).setOnClickListener { showIDToken() };
    }


}