
package com.example.glanceexample.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text

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
                .padding(16.dp)
        ) {
            Text(text = "STOCK TRACKER")

            Spacer(modifier = GlanceModifier.padding(4.dp))

            Text(text = "Apple Inc. (AAPL)")

            Spacer(modifier = GlanceModifier.padding(4.dp))

            Text(text = "$227.50")

            Row {
                Text(text = "+2.4%")

                Spacer(modifier = GlanceModifier.width(8.dp))

                Text(text = "Сегодня")
            }

            Spacer(modifier = GlanceModifier.padding(4.dp))

            Text(text = "Демонстрационные данные")
        }
    }
}
