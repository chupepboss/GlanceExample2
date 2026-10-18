
package com.example.glanceexample

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

data class StockQuote(
    val symbol: String,
    val price: Double,
    val changePercent: Double,
    val tradingDate: String
)

object PriceDataRepo {

    private const val SYMBOL = "AAPL"

    suspend fun fetchQuote(): StockQuote? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.ALPHA_VANTAGE_API_KEY

        if (apiKey.isBlank()) return@withContext null

        var connection: HttpURLConnection? = null

        try {
            val url = URL(
                "https://www.alphavantage.co/query" +
                        "?function=TIME_SERIES_DAILY" +
                        "&symbol=$SYMBOL" +
                        "&outputsize=compact" +
                        "&apikey=$apiKey"
            )

            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext null
            }

            val response = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            val json = JSONObject(response)

            if (
                json.has("Error Message") ||
                json.has("Note") ||
                json.has("Information")
            ) {
                return@withContext null
            }

            val series = json.optJSONObject("Time Series (Daily)")
                ?: return@withContext null

            val dates = series.keys().asSequence().toList().sortedDescending()

            if (dates.size < 2) return@withContext null

            val latestDate = dates[0]
            val previousDate = dates[1]

            val latest = series.getJSONObject(latestDate)
            val previous = series.getJSONObject(previousDate)

            val price = latest.getString("4. close").toDouble()
            val previousClose = previous.getString("4. close").toDouble()

            val change = if (previousClose != 0.0) {
                (price - previousClose) / previousClose * 100.0
            } else {
                0.0
            }

            StockQuote(
                symbol = SYMBOL,
                price = price,
                changePercent = change,
                tradingDate = latestDate
            )
        } catch (e: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }

    fun formatPrice(price: Double): String =
        String.format(Locale.US, "$%.2f", price)

    fun formatChange(change: Double): String =
        String.format(Locale.US, "%+.2f%%", change)
}
