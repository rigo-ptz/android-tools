package com.oxygen.analytics.domain.events

interface Event

interface AnalyticsEvent : Event {
    val screenName: String
    val eventName: String?
    val data: Map<String, Any>?
}

interface UserPropertyEvent : Event {
    val propertyName: String
    val propertyValue: Any
}

data class ActionEvent(
    override val screenName: String,
    override val eventName: String,
    override val data: Map<String, Any>?
) : AnalyticsEvent

data class PurchaseEvent(
    override val screenName: String,
    override val eventName: String? = null,
    override val data: HashMap<String, Any>? = null,
    val itemName: String,
    val currencyName: String,
    val amount: Double,
    val isVirtualCurrency: Boolean
) : AnalyticsEvent

data class ScreenEvent(
    override val screenName: String,
    val classSimpleName: String,
    override val eventName: String? = null,
    override val data: Map<String, Any>? = null,
) : AnalyticsEvent

data class SetUserPropertyEvent(
    override val propertyName: String,
    override val propertyValue: Any
) : UserPropertyEvent

data class LocationEvent(
    val latitude: Double,
    val longitude: Double
) : Event
