package com.example.floater

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView

class FloatingService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var bubbleView: View
    private var menuView: View? = null
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var menuParams: WindowManager.LayoutParams

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1, buildNotification())
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createBubble()
    }

    // Уведомление с кнопкой STOP
    private fun buildNotification(): Notification {
        val channelId = "floater_channel"
        val stopIntent = Intent(this, FloatingService::class.java).apply { action = "STOP_SERVICE" }
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(NotificationChannel(channelId, "Floater", NotificationManager.IMPORTANCE_LOW))
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("Floater активен")
                .setContentText("Нажмите STOP, чтобы скрыть меню")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .addAction(android.R.drawable.ic_delete, "STOP", stopPendingIntent)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("Floater активен")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .addAction(android.R.drawable.ic_delete, "STOP", stopPendingIntent)
                .build()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_SERVICE") {
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun createBubble() {
        // Новый вертикальный прямоугольник
        val width = (30 * resources.displayMetrics.density).toInt() // Уменьшен
        val height = (60 * resources.displayMetrics.density).toInt() // Вертикальный

        bubbleView = TextView(this).apply {
            text = "|~×~|"
            textSize = 12f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            
            // Делаем закругленные углы и темно-синий фон
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadius = 20f
            shape.setColor(Color.parseColor("#0D1B2A"))
            background = shape
            
            setOnTouchListener(BubbleTouchListener())
            setOnClickListener { toggleMenu() }
        }

        params = WindowManager.LayoutParams(
            width, height,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 200
            y = 400
        }

        windowManager.addView(bubbleView, params)
    }

    private fun toggleMenu() {
        if (menuView != null) {
            windowManager.removeView(menuView)
            menuView = null
            return
        }

        // Подключаем нашу новую разметку
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val menu = inflater.inflate(R.layout.floating_menu, null)

        // Обработка крестика
        val closeBtn = menu.findViewById<TextView>(R.id.closeBtn)
        closeBtn.setOnClickListener { toggleMenu() }

        menuParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = params.x - (50 * resources.displayMetrics.density).toInt() // Сдвигаем меню, чтобы было ровно
            y = params.y
        }

        menuView = menu
        windowManager.addView(menu, menuParams)
    }

    private inner class BubbleTouchListener : View.OnTouchListener {
        private var initialX = 0
        private var initialY = 0
        private var touchX = 0f
        private var touchY = 0f

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    touchX = event.rawX
                    touchY = event.rawY
                    return false
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - touchX).toInt()
                    params.y = initialY + (event.rawY - touchY).toInt()
                    windowManager.updateViewLayout(bubbleView, params)
                    return true
                }
            }
            return false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::bubbleView.isInitialized) windowManager.removeView(bubbleView)
        menuView?.let { windowManager.removeView(it) }
    }
}
