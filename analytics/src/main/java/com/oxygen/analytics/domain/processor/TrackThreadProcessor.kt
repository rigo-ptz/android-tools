package com.oxygen.analytics.domain.processor

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import com.oxygen.analytics.domain.LOG_TAG
import com.oxygen.analytics.domain.events.ActionEvent
import com.oxygen.analytics.domain.events.LocationEvent
import com.oxygen.analytics.domain.events.PurchaseEvent
import com.oxygen.analytics.domain.events.ScreenEvent
import com.oxygen.analytics.domain.events.SetUserPropertyEvent
import com.oxygen.analytics.domain.worker.WorkWrapper
import java.util.LinkedList

class TrackThreadProcessor : HandlerThread("TrackThreadProcessor"), TrackProcessor {

    private lateinit var handler: Handler
    private val queue = LinkedList<WorkWrapper>()

    init {
        start()
    }

    @Synchronized
    override fun processEvent(work: WorkWrapper) {
        if (this::handler.isInitialized) {
            Log.v(LOG_TAG, "Trackk::TrackThreadProcessor::processEvent send work to handler")
            sendWorkToHandler(work)

            if (queue.isNotEmpty()) {
                while (true) {
                    val queuedWork = queue.poll() ?: break
                    Log.v(LOG_TAG, "Trackk::TrackThreadProcessor::processEvent send queuedWork to handler")
                    sendWorkToHandler(queuedWork)
                }
            }
        } else {
            Log.v(LOG_TAG, "Trackk::TrackThreadProcessor::processEvent handler is not initialized, add work to queue")
            queue.add(work)
        }
    }

    override fun onLooperPrepared() {
        super.onLooperPrepared()

        handler = Handler(looper) {
            val work = it.obj as WorkWrapper
            val event = work.event

            Log.d(LOG_TAG, "Trackk::TrackThreadProcessor $work")

            when (event) {
                is ActionEvent -> work.trackers.forEach { tracker -> tracker.trackEvent(event) }
                is ScreenEvent -> work.trackers.forEach { tracker -> tracker.trackScreen(event) }
                is PurchaseEvent -> work.trackers.forEach { tracker -> tracker.trackPurchaseEvent(event) }
                is LocationEvent -> work.trackers.forEach { tracker -> tracker.trackLocation(event) }
                is SetUserPropertyEvent -> work.trackers.forEach { tracker -> tracker.setUserProperty(event) }
            }

            false
        }
    }

    private fun sendWorkToHandler(work: WorkWrapper) {
        val msg = Message.obtain().apply {
            obj = work
        }
        handler.sendMessage(msg)
    }

}
