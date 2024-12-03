package com.example.reminderapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        setContent {
            ReminderApp()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel",
                "Reminder Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for reminder notifications"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun ReminderApp() {
    val context = LocalContext.current // Извлекаем текущий Context
    var message by remember { mutableStateOf("") }
    var timeMinutes by remember { mutableStateOf(1f) }
    var isTimerActive by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Место для ввода текста
        TextField(
            value = message,
            onValueChange = { if (!isTimerActive) message = it },
            label = { Text("Введите сообщение") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isTimerActive // Когда таймер активен, блокируется
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Ползунок для выбора времени
        Text(text = "Выберите время: ${timeMinutes.toInt()} мин.")
        Slider(
            value = timeMinutes,
            onValueChange = { if (!isTimerActive) timeMinutes = it },
            valueRange = 1f..60f,
            steps = 59,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isTimerActive // Когда таймер активен, блокируем
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка для установки напоминания
        Button(
            onClick = {
                isTimerActive = true
            },
            enabled = !isTimerActive
        ) {
            Text(text = "Установить напоминание")
        }

        // Логика таймера
        if (isTimerActive) {
            LaunchedEffect(key1 = isTimerActive) {
                delay((timeMinutes * 60 * 1000).toLong()) // Задержка в миллисекундах
                showNotification(context = context, message = message)
                isTimerActive = false
            }
        }
    }
}

// Функция для отображения уведомления
@SuppressLint("MissingPermission")
fun showNotification(context: Context, message: String) {
    val notification = NotificationCompat.Builder(context, "reminder_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Напоминание")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    NotificationManagerCompat.from(context).notify(1, notification)
}
