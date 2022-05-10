package com.oxygen.analytics.domain.worker

import com.oxygen.analytics.domain.adapter.TrackerAdapter
import com.oxygen.analytics.domain.events.Event

data class WorkWrapper(
    val event: Event,
    val trackers: Set<TrackerAdapter>
)
