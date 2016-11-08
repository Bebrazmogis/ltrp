package lt.ltrp.job.event

import lt.ltrp.job.`object`.Job

/**
 * Created by Bebras on 2016-10-25.
 * Dispatched when a job is loaded from persistent storage
 */
class JobLoadedEvent(job: Job): JobEvent(job) {
}