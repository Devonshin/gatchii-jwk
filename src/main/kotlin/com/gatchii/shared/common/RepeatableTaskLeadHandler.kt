package com.gatchii.shared.common

import java.util.*
import kotlin.concurrent.timerTask

/** Package: com.gatchii.shared.common Created: Devonshin Date: 01/12/2024 */

class RepeatableTaskLeadHandler(
    private val taskName: String,
    private val repeatInSecond: Int = Integer.MAX_VALUE,
    private val task: () -> Unit
): TaskLeadHandler() {
    init {
        if(repeatInSecond <= 0 && repeatInSecond > Integer.MIN_VALUE) {
            throw Exception("Repeat second must be greater than 0")
        }
    }
    override fun taskName(): String {
        return taskName
    }

    override fun doTask() {
        Timer().scheduleAtFixedRate(
            timerTask {
                task()
            },
            0L,
            repeatInSecond.toLong() * 1000
        )
    }
}