
package com.example.glanceexample

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

object PriceDataRepo {
    const val ticker = "AAPL"

    private val _currentPrice = MutableStateFlow(227.50f)
    val currentPrice: StateFlow<Float> = _currentPrice.asStateFlow()

    var change: Int = 2
        private set

    fun update() {
        val oldPrice = _currentPrice.value
        val newPrice = oldPrice * (1f + Random.nextFloat() * 0.04f - 0.02f)
        change = if (newPrice >= oldPrice) 1 else -1
        _currentPrice.value = newPrice
    }
}
