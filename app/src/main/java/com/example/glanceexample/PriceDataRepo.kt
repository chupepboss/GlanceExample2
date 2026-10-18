
package com.example.glanceexample

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class StockQuote(
    val symbol: String,
    val price: Double,
    val changePercent: Double,
    val tradingDate: String,
    val fetchedAtMillis: Long,
    val isCached: Boolean = false
)

object PriceDataRepo {

    private const val SYMBOL = "AAPL"
    private const val PREFS_NAME = "stock_quote_cache"
    private const val KEY_PRICE = "price"
    private const val KEY_CHANGE = "change"
    private const val KEY_DATE = "trading_date"
    private const val KEY_FETCHED_AT = "fetched_at"

    private fun preferences(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun fetchQuote(context: Context): StockQuote? =
        withContext(Dispatchers.IO) {
            val cachedQuote = readCachedQuote(context)
            val apiKey = BuildConfig.ALPHA_VANTAGE_API_KEY

            if (apiKey.isBlank()) {
                return@withContext cachedQuote
            }

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
                    return@withContext cachedQuote
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
                    return@withContext cachedQuote
                }

                val series = json.optJSONObject("Time Series (Daily)")
                    ?: return@withContext cachedQuote

                val dates = series.keys()
                    .asSequence()
                    .toList()
                    .sortedDescending()

                if (dates.size < 2) {
                    return@withContext cachedQuote
                }

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

                val quote = StockQuote(
                    symbol = SYMBOL,
                    price = price,
                    changePercent = change,
                    tradingDate = latestDate,
                    fetchedAtMillis = System.currentTimeMillis()
                )

                saveCachedQuote(context, quote)
                quote
            } catch (e: Exception) {
                cachedQuote
            } finally {
                connection?.disconnect()
            }
        }

    private fun readCachedQuote(context: Context): StockQuote? {
        val prefs = preferences(context)

        if (!prefs.contains(KEY_PRICE)) return null

        val price = prefs.getFloat(KEY_PRICE, Float.NaN).toDouble()
        val change = prefs.getFloat(KEY_CHANGE, Float.NaN).toDouble()
        val date = prefs.getString(KEY_DATE, null) ?: return null
        val fetchedAt = prefs.getLong(KEY_FETCHED_AT, 0L)

        if (!price.isFinite() || !change.isFinite() || fetchedAt <= 0L) {
            return null
        }

        return StockQuote(
            symbol = SYMBOL,
            price = price,
            changePercent = change,
            tradingDate = date,
            fetchedAtMillis = fetchedAt,
            isCached = true
        )
    }

    private fun saveCachedQuote(context: Context, quote: StockQuote) {
        preferences(context).edit()
            .putFloat(KEY_PRICE, quote.price.toFloat())
            .putFloat(KEY_CHANGE, quote.changePercent.toFloat())
            .putString(KEY_DATE, quote.tradingDate)
            .putLong(KEY_FETCHED_AT, quote.fetchedAtMillis)
            .apply()
    }

    fun formatPrice(price: Double): String =
        String.format(Locale.US, "$%.2f", price)

    fun formatChange(change: Double): String =
        String.format(Locale.US, "%+.2f%%", change)

    fun formatLastUpdated(timeMillis: Long): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return formatter.format(Date(timeMillis))
    }
}
