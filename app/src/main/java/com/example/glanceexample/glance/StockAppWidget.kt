
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
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.glanceexample.PriceDataRepo
import com.example.glanceexample.StockQuote

class StockAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val quote = PriceDataRepo.fetchQuote()

        provideContent {
            WidgetContent(quote)
        }
    }

    @Composable
    private fun WidgetContent(quote: StockQuote?) {
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

            Spacer(GlanceModifier.height(8.dp))

            Text(
                text = "Apple Inc. (AAPL)",
                style = TextStyle(color = ColorProvider(Color.White))
            )

            Spacer(GlanceModifier.height(8.dp))

            if (quote != null) {
                Text(
                    text = PriceDataRepo.formatPrice(quote.price),
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = PriceDataRepo.formatChange(quote.changePercent),
                    style = TextStyle(
                        color = ColorProvider(
                            if (quote.changePercent >= 0)
                                Color(0xFF7CFFB2)
                            else
                                Color(0xFFFF8585)
                        )
                    )
                )

                Spacer(GlanceModifier.height(6.dp))

                Text(
                    text = "Дата котировки: ${quote.tradingDate}",
                    style = TextStyle(color = ColorProvider(Color.White))
                )
            } else {
                Text(
                    text = "Нет данных. Проверь API и интернет.",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFFFD180))
                    )
                )
            }
        }
    }
}
