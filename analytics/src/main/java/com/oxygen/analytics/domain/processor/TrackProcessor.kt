package com.oxygen.analytics.domain.processor

import com.oxygen.analytics.domain.worker.WorkWrapper

interface TrackProcessor {
    fun processEvent(work: WorkWrapper)
}
