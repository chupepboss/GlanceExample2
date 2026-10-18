
package com.example.glanceexample.glance

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StockAppWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = StockAppWidget()

    companion object {
        private const val UPDATE_INTERVAL_MS = 20_000L

        private var updateJob: Job? = null
        private var updateScope: CoroutineScope? = null

        @Synchronized
        private fun startUpdates(context: Context) {
            if (updateJob?.isActive == true) return

            val appContext = context.applicationContext
            val scope = CoroutineScope(
                SupervisorJob() + Dispatchers.IO
            )

            updateScope = scope

            updateJob = scope.launch {
                while (isActive) {
                    try {
                        StockAppWidget().updateAll(appContext)
                    } catch (_: Exception) {
                        // Ошибка обновления не должна останавливать цикл.
                    }

                    delay(UPDATE_INTERVAL_MS)
                }
            }
        }

        @Synchronized
        private fun stopUpdates() {
            updateJob?.cancel()
            updateJob = null

            updateScope?.cancel()
            updateScope = null
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        startUpdates(context)
    }

    override fun onDisabled(context: Context) {
        stopUpdates()
        super.onDisabled(context)
    }
}
