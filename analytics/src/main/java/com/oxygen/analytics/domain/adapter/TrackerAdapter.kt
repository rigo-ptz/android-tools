package com.oxygen.analytics.domain.adapter

import com.oxygen.analytics.domain.events.ActionEvent
import com.oxygen.analytics.domain.events.LocationEvent
import com.oxygen.analytics.domain.events.PurchaseEvent
import com.oxygen.analytics.domain.events.ScreenEvent
import com.oxygen.analytics.domain.events.SetUserPropertyEvent

@JvmInline
value class TrackerId(private val id: String)

interface TrackerAdapter {
    fun getTrackerId(): TrackerId
    fun trackEvent(event: ActionEvent)
    fun trackScreen(screenEvent: ScreenEvent) {}
    fun trackLocation(locationEvent: LocationEvent) {}
    fun trackException(throwable: Throwable) {}
    fun trackPurchaseEvent(event: PurchaseEvent) {}
    fun setCustomerId(id: String) {}
    fun setUserProperty(event: SetUserPropertyEvent) {}
}
