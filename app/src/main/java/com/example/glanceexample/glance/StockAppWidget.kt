
package com.example.glanceexample.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class StockAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFF17352B)))
                .padding(16.dp)
        ) {
            Text(
                text = "STOCK TRACKER",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "Apple Inc. (AAPL)",
                style = TextStyle(color = ColorProvider(Color.White))
            )

            Text(
                text = "$227.50",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "+2.4%  |  Сегодня",
                style = TextStyle(color = ColorProvider(Color(0xFF7CFFB2)))
            )

            Text(
                text = "Демонстрационные данные",
                style = TextStyle(color = ColorProvider(Color.White))
            )
        }
    }
}
