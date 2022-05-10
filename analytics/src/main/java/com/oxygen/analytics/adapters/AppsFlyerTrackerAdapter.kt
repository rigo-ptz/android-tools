package com.oxygen.analytics.adapters

import android.content.Context
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.oxygen.analytics.domain.adapter.TrackerAdapter
import com.oxygen.analytics.domain.adapter.TrackerId
import com.oxygen.analytics.domain.events.ActionEvent
import com.oxygen.analytics.domain.events.LocationEvent
import com.oxygen.analytics.domain.events.PurchaseEvent
import java.util.Locale

class AppsFlyerTrackerAdapter(
    private val tracker: AppsFlyerLib,
    private val context: Context
) : TrackerAdapter {

    override fun getTrackerId(): TrackerId = ID

    override fun trackEvent(event: ActionEvent) {
        tracker.logEvent(context, event.eventName, event.data)
    }

    override fun setCustomerId(id: String) {
        tracker.setCustomerUserId(id)
    }

    override fun trackPurchaseEvent(event: PurchaseEvent) {
        val eventData = hashMapOf<String, Any>()
        eventData[AFInAppEventParameterName.CONTENT_ID] = event.itemName
        eventData[AFInAppEventParameterName.QUANTITY] = 1
        eventData[AFInAppEventParameterName.PRICE] = event.amount
        val currencyParam = if (event.isVirtualCurrency)
            AFInAppEventParameterName.VIRTUAL_CURRENCY_NAME
        else
            AFInAppEventParameterName.CURRENCY
        eventData[currencyParam] =
            event.currencyName.uppercase(Locale.getDefault())
        eventData[AFInAppEventParameterName.REVENUE] = event.amount
        tracker.logEvent(context, AFInAppEventType.PURCHASE, eventData)

    }

    override fun trackLocation(locationEvent: LocationEvent) {
        tracker.logLocation(context, locationEvent.latitude, locationEvent.longitude)
    }

    fun logSession() {
        tracker.logSession(context)
    }

    fun updateServerUninstallToken(appContext: Context, token: String) {
        tracker.updateServerUninstallToken(appContext, token)
    }

    companion object {
        val ID = TrackerId("AF")
    }

}
