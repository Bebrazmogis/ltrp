package lt.ltrp

import lt.ltrp.job.`object`.Job

/**
 * Created by Bebras on 2016-10-25.
 * A singleton(kind of) container that holds all the references to Job objects
 */
object JobContainer {

    private val jobs = mutableListOf<Job>()

    fun get(): List<Job> {
        return jobs
    }

    fun add(job: Job) {
        jobs.add(job)
    }

    fun remove(job: Job) {
        jobs.remove(job)
    }

}