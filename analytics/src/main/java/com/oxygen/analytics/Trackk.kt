package com.oxygen.analytics

import android.util.Log
import com.oxygen.analytics.domain.AnalyticConstants
import com.oxygen.analytics.domain.LOG_TAG
import com.oxygen.analytics.domain.adapter.TrackerAdapter
import com.oxygen.analytics.domain.adapter.TrackerId
import com.oxygen.analytics.domain.events.ActionEvent
import com.oxygen.analytics.domain.events.PurchaseEvent
import com.oxygen.analytics.domain.events.ScreenEvent
import com.oxygen.analytics.domain.events.SetUserPropertyEvent
import com.oxygen.analytics.domain.processor.TrackProcessor
import com.oxygen.analytics.domain.processor.TrackThreadProcessor
import com.oxygen.analytics.domain.worker.TrackWorker

object Trackk {

    private lateinit var trackWorker: TrackWorker

    /**
     * Initializes tracking on application startup.
     * Adds tracker adapters to TrackWorker.
     * @use - call in App.onCreate
     */
    fun init(processor: TrackProcessor = TrackThreadProcessor(), block: TrackWorker.() -> Unit) {
        trackWorker = TrackWorker(processor)
        block.invoke(trackWorker)
        Log.d(LOG_TAG, "WhimTrack::init")
    }

    /**
     * @see Trackk.init
     * @param screenName name of the current screen
     * @param eventName name of the event
     * @param data map of param name to any data
     * @param exceptAdapters event will be tracked by all adapters except the given
     */
    fun trackEvent(
        screenName: String,
        eventName: String,
        data: Map<String, Any> = emptyMap(),
        exceptAdapters: List<TrackerId>,
    ) {
        val event = ActionEvent(
            screenName,
            eventName,
            data.toMutableMap().apply {
                put(AnalyticConstants.Param.SCREEN_NAME, screenName)
            }
        )

        trackWorker.trackEvent(event, exceptAdapters)
        Log.d(LOG_TAG, "Trackk::trackEvent <except $exceptAdapters> ($event)")
    }

    /**
     * Tracks purchase event with Braze and AF adapters
     *
     * @see Trackk.init
     * @param screenName name of the current screen from AnalyticConstants.Screens
     * @param itemName name of the item
     * @param price price of the purchase
     * @param currency currency of the price
     */
    fun trackPurchaseEventInLocalCurrency(
        screenName: String,
        itemName: String,
        price: Double,
        currency: String,
        isVirtualCurrency: Boolean,
        exceptAdapters: List<TrackerId>,
    ) {
        val event = PurchaseEvent(
            screenName = screenName,
            itemName = itemName,
            currencyName = currency,
            amount = price,
            isVirtualCurrency = isVirtualCurrency
        )
        Log.d(LOG_TAG, "Trackk::trackPurchaseEventInLocalCurrency ($event)")
        trackWorker.trackEvent(event, exceptAdapters)
    }

    /**
     * Tracks screen as event
     */
    fun trackScreen(
        screenName: String,
        classSimpleName: String,
        data: Map<String, Any> = emptyMap(),
        exceptAdapters: List<TrackerId>,
    ) {
        val screenEvent = ScreenEvent(
            screenName = screenName,
            classSimpleName = classSimpleName,
            data = data
        )
        Log.d(LOG_TAG, "Trackk::trackScreen<except ${exceptAdapters}> (name=$screenName, class=$classSimpleName, data=$data)")
        trackWorker.trackEvent(screenEvent, exceptAdapters)
    }

    fun setCustomerId(customerId: String) {
        Log.d(LOG_TAG, "Trackk::setCustomerId($customerId)")
        trackWorker.setCustomerId(customerId)
    }

    fun setUserProperty(
        userPropertyName: String,
        value: Any,
        exceptAdapters: List<TrackerId>,
    ) {
        Log.d(LOG_TAG, "Trackk::setUserProperty<except ${exceptAdapters}>($userPropertyName, $value)")
        if ((value as? String)?.isEmpty() == true) return
        trackWorker.trackEvent(SetUserPropertyEvent(userPropertyName, value), exceptAdapters)
    }

    private inline fun <reified T : TrackerAdapter> getTrackerAdapter(id: TrackerId): T? =
        trackWorker.getTracker(id) as? T


}
