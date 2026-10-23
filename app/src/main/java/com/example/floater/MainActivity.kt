package com.example.floater

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.startButton)
        
        var isServiceRunning = false

        btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                if (!isServiceRunning) {
                    startService(Intent(this, FloatingService::class.java))
                    btn.text = "STOP"
                    isServiceRunning = true
                } else {
                    stopService(Intent(this, FloatingService::class.java))
                    btn.text = "START"
                    isServiceRunning = false
                }
            }
        }
    }
}
