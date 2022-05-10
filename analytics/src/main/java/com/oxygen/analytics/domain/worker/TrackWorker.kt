package com.oxygen.analytics.domain.worker

import android.util.Log
import com.oxygen.analytics.domain.LOG_TAG
import com.oxygen.analytics.domain.adapter.TrackerAdapter
import com.oxygen.analytics.domain.adapter.TrackerId
import com.oxygen.analytics.domain.events.Event
import com.oxygen.analytics.domain.processor.TrackProcessor

class TrackWorker(
    private val processor: TrackProcessor
) {

    private var trackers = HashMap<TrackerId, TrackerAdapter>()

    fun addTracker(trackerId: TrackerId, tracker: TrackerAdapter) {
        trackers[trackerId] = tracker
    }

    internal fun trackEvent(event: Event, exceptAdapters: List<TrackerId>) {
        val trackers = getTrackersForWork(exceptAdapters)
        if (trackers.isNotEmpty()) processor.processEvent(WorkWrapper(event, trackers))
    }

    internal fun setCustomerId(customerId: String) {
        trackers.forEach { (id, tracker) ->
            tracker.setCustomerId(customerId)
            Log.v(LOG_TAG,"Trackk::TrackWorker::setCustomerId id was set with $id")
        }
    }

    fun getTracker(trackerId: TrackerId) = trackers[trackerId]

    private fun getTrackersForWork(exceptAdapters: List<TrackerId>): Set<TrackerAdapter> =
        trackers.mapNotNullTo(mutableSetOf()) { (id, tracker) ->
            if (!exceptAdapters.contains(id))
                tracker
            else
                null
        }

}
