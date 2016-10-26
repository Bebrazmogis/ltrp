package lt.ltrp.job.event;

import lt.ltrp.job.`object`.Job
import net.gtaun.util.event.Event

/**
 * Created by Bebras on 2016-10-25.
 * The base class of all Job events
 */
abstract class JobEvent(val job: Job): Event() {
}
