package com.runemaster.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF080706)
private val Gold = Color(0xFFD6A94C)
private val LightGold = Color(0xFFF6DA8A)
private val Surface = Color(0xFF17130D)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Gold,
                    secondary = LightGold,
                    background = Background,
                    surface = Surface
                )
            ) {
                RuneMasterApp()
            }
        }
    }
}

@Composable
fun RuneMasterApp() {
    var query by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(22.dp))

            Text(
                text = "ᚠ",
                color = LightGold,
                fontSize = 76.sp
            )

            Text(
                text = "РУНОЛОГ",
                color = LightGold,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Старший футарк • 24 руны",
                color = Gold,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(30.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Опишите ситуацию") },
                placeholder = {
                    Text("Например: проблемы в семье и с деньгами...")
                },
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold,
                    unfocusedBorderColor = Gold.copy(alpha = 0.5f),
                    focusedLabelColor = Gold,
                    cursorColor = Gold
                )
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    "ПОДОБРАТЬ ПРАКТИКУ",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(28.dp))

            MenuCard("ᚠ", "24 РУНЫ", "Полный интерактивный справочник")
            MenuCard("ᛃ", "ДИАГНОСТИКА", "Расклады и интерпретация")
            MenuCard("ᛉ", "СТАВЫ", "Каталог и автоматический подбор")
            MenuCard("ᚷ", "КОНСТРУКТОР", "Создание собственной формулы")
            MenuCard("ᚲ", "СВЕЧИ", "Свечные практики")
            MenuCard("ᚨ", "ЖУРНАЛ", "История работы")
            MenuCard("ᛟ", "ИСТОЧНИКИ", "Традиции и литература")

            Spacer(Modifier.height(40.dp))

            Text(
                text = "RuneMaster • локальный справочник",
                color = Gold.copy(alpha = 0.55f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun MenuCard(
    rune: String,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rune,
                color = LightGold,
                fontSize = 35.sp,
                modifier = Modifier.width(58.dp)
            )

            Column {
                Text(
                    text = title,
                    color = LightGold,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    color = Gold.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }
    }
}
