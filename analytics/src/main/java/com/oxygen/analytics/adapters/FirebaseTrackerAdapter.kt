package com.oxygen.analytics.adapters

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.oxygen.analytics.domain.AnalyticConstants
import com.oxygen.analytics.domain.LOG_TAG
import com.oxygen.analytics.domain.adapter.TrackerAdapter
import com.oxygen.analytics.domain.adapter.TrackerId
import com.oxygen.analytics.domain.events.ActionEvent
import com.oxygen.analytics.domain.events.PurchaseEvent
import com.oxygen.analytics.domain.events.ScreenEvent
import com.oxygen.analytics.domain.events.SetUserPropertyEvent

class FirebaseTrackerAdapter(private val tracker: FirebaseAnalytics) : TrackerAdapter {

    override fun getTrackerId(): TrackerId = ID

    override fun trackEvent(event: ActionEvent) {
        val name = event.eventName

        if (name.length > FIREBASE_ANALYTICS_EVENT_NAME_LIMIT) {
            Log.e(LOG_TAG, "Firebase analytics event name cannot be over 40 chars! [$name]")
        }

        Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, AnalyticConstants.Param.ACTION)
            putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        }
            .also {
                it.mergeWith(event.data)
            }
            .run {
                tracker.logEvent(name, this)
            }
    }

    override fun trackScreen(screenEvent: ScreenEvent) {
        // log screen as a separate event
        Bundle(2).apply {
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, AnalyticConstants.Param.SCREEN)
            putString(FirebaseAnalytics.Param.ITEM_NAME, screenEvent.screenName)
        }
            .also { it.mergeWith(screenEvent.data) }
            .run {
                tracker.logEvent(screenEvent.screenName, this)
            }

        // log screen as "screen_view" event (we just override screen name here)
        Bundle(2).apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenEvent.screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenEvent.classSimpleName)
        }
            .also { it.mergeWith(screenEvent.data) }
            .run {
                tracker.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, this)
            }
    }

    override fun trackPurchaseEvent(event: PurchaseEvent) {
        val currencyParam = if (event.isVirtualCurrency)
            FirebaseAnalytics.Param.VIRTUAL_CURRENCY_NAME
        else
            FirebaseAnalytics.Param.CURRENCY

        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_NAME, event.itemName)
            putString(currencyParam, event.currencyName)
            putInt(FirebaseAnalytics.Param.VALUE, event.amount.toInt())
        }

        event.data?.forEach { entry ->
            (entry.value as? Int)?.let { params.putInt(FirebaseAnalytics.Param.CONTENT, it) }
                ?: (entry.value as? String)?.let { params.putString(FirebaseAnalytics.Param.CONTENT, it) }
        }

        val eventName = if (event.isVirtualCurrency)
            FirebaseAnalytics.Event.SPEND_VIRTUAL_CURRENCY
        else
            FirebaseAnalytics.Event.PURCHASE

        tracker.logEvent(eventName, params)
    }

    override fun trackException(throwable: Throwable) {}

    override fun setCustomerId(id: String) {
        tracker.setUserId(id)
    }

    override fun setUserProperty(event: SetUserPropertyEvent) {
        (event.propertyValue as? String)?.apply {
            tracker.setUserProperty(event.propertyName, this)
        }
    }

    /**
     * Merge event's data into a bundle
     * Currently only supports String and Int
     */
    private fun Bundle.mergeWith(data: Map<String, Any>?) {
        data?.forEach { entry ->
            val key = if (entry.key == AnalyticConstants.Param.CONTENT) {
                FirebaseAnalytics.Param.CONTENT
            } else {
                entry.key
            }

            when (val value = entry.value) {
                is Int -> putInt(key, value)
                is String -> putString(key, value)
                else -> Unit
            }
        }
    }

    companion object {
        const val FIREBASE_ANALYTICS_EVENT_NAME_LIMIT = 40
        val ID = TrackerId("FirebaseAnalytics")
    }

}
