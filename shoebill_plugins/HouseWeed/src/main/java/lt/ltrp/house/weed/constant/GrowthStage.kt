package lt.ltrp.house.weed.constant

import java.time.Duration

/**
 * Created by Bebras on 2016-10-14.
 * This enum represents the possible growth stages for a weed sapling
 * Each stage has a duration needed to progress up onto the next stage
 */
enum class GrowthStage(val modelId: Int, val growthDuration: Duration, val prettyName: String) {

    Seed(0, Duration.ofMinutes(30), "S�kla"),
    Tiny(19839, Duration.ofMinutes(60), "Labai Ma�as"),
    Small(19838, Duration.ofMinutes(60), "Ma�as"),
    Normal(19837, Duration.ofMinutes(60), "Vidutinis"),
    Grown(19473, Duration.ofMinutes(0), "U�aug�s");


    fun next(): GrowthStage {
        if(this == Grown)
            return Grown
        else
            return values()[this.ordinal + 1]
    }
}