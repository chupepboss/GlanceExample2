
package com.example.glanceexample.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.glanceexample.PriceDataRepo
import com.example.glanceexample.R
import com.example.glanceexample.StockQuote

private val SmallWidgetSize = DpSize(100.dp, 80.dp)
private val MediumWidgetSize = DpSize(120.dp, 120.dp)

class StockAppWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(SmallWidgetSize, MediumWidgetSize)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val quote = PriceDataRepo.fetchQuote(context)

        provideContent {
            val size = LocalSize.current

            if (
                size.width <= SmallWidgetSize.width ||
                size.height <= SmallWidgetSize.height
            ) {
                SmallWidget(quote)
            } else {
                MediumWidget(quote)
            }
        }
    }

    @Composable
    private fun SmallWidget(quote: StockQuote?) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFF17352B)))
                .clickable(actionRunCallback<RefreshStockAction>())
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "AAPL",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold
                )
            )

            if (quote != null) {
                Text(
                    text = PriceDataRepo.formatPrice(quote.price),
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontWeight = FontWeight.Bold
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        provider = ImageProvider(
                            if (quote.changePercent >= 0) {
                                R.drawable.up_arrow
                            } else {
                                R.drawable.down_arrow
                            }
                        ),
                        contentDescription = "Направление изменения цены",
                        modifier = GlanceModifier.size(16.dp)
                    )

                    Text(
                        text = PriceDataRepo.formatChange(quote.changePercent),
                        style = TextStyle(
                            color = ColorProvider(
                                if (quote.changePercent >= 0) {
                                    Color(0xFF7CFFB2)
                                } else {
                                    Color(0xFFFF8585)
                                }
                            )
                        )
                    )
                }
            } else {
                Text(
                    text = "Нет данных. Нажми для повтора.",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFFFD180))
                    )
                )
            }
        }
    }

    @Composable
    private fun MediumWidget(quote: StockQuote?) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(Color(0xFF17352B)))
                .clickable(actionRunCallback<RefreshStockAction>())
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "APPLE INC. (AAPL)",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(GlanceModifier.height(6.dp))

            if (quote != null) {
                Text(
                    text = PriceDataRepo.formatPrice(quote.price),
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(GlanceModifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        provider = ImageProvider(
                            if (quote.changePercent >= 0) {
                                R.drawable.up_arrow
                            } else {
                                R.drawable.down_arrow
                            }
                        ),
                        contentDescription = "Направление изменения цены",
                        modifier = GlanceModifier.size(20.dp)
                    )

                    Text(
                        text = PriceDataRepo.formatChange(quote.changePercent),
                        style = TextStyle(
                            color = ColorProvider(
                                if (quote.changePercent >= 0) {
                                    Color(0xFF7CFFB2)
                                } else {
                                    Color(0xFFFF8585)
                                }
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(GlanceModifier.height(4.dp))

                Text(
                    text = "Дата торгов: ${quote.tradingDate}",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFB8D8C8))
                    )
                )

                Text(
                    text = if (quote.isCached) {
                        "Кэшированные данные"
                    } else {
                        "Обновлено: ${
                            PriceDataRepo.formatLastUpdated(
                                quote.fetchedAtMillis
                            )
                        }"
                    },
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFB8D8C8))
                    )
                )
            } else {
                Text(
                    text = "Нет данных. Проверь интернет.",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFFFD180))
                    )
                )
            }
        }
    }
}

class RefreshStockAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        PriceDataRepo.fetchQuote(context)
        StockAppWidget().update(context, glanceId)
    }
}
