package lt.ltrp.trucker.constant

import lt.ltrp.trucker.data.TruckerVehicleData
import net.gtaun.shoebill.data.Vector3D

/**
 * Created by Bebras on 2016-10-29.
 * This monstrosity is supposed to represent all the static data for trucker vehicles
 * The actual data is stored inside the [TruckerVehicleData]
 *
 */
enum class TruckerVehicleConstants(val modelId: Short,
                          val hours: Short,
                          val vehicleData: Array<TruckerVehicleData>) {



    FORKLIFT(
            530,
            0,
            mutableMapOf(
                Pair(TruckerCargoType.Box, 3.toShort())
            ),
            mutableMapOf(
                    Pair(
                            TruckerCargoType.Box,
                            Array(3, {
                                Pair(Vector3D(0.3079834f, 0.7f, 0.1f), Vector3D(0f, 0f, 0f))
                                Pair(Vector3D(-0.38916016f, 0.7f, 0.1f), Vector3D())
                                Pair(Vector3D(-0.045166016f, 0.7f, 0.8f), Vector3D())
                            })
                    )
            ),
            mutableMapOf(
                    Pair(
                            TruckerCargoType.Box,
                            Array(3, { false })
                    )
            )

    ),
    PICADOR(
            600,
            0,
            mutableMapOf(
                    Pair(TruckerCargoType.Box, 2.toShort())
            ),
            mutableMapOf(
                    Pair(
                            TruckerCargoType.Box,
                            Array(2, {
                                Pair(Vector3D(0.00865f, -0.97901f, -0.13754f), Vector3D())
                                Pair(Vector3D(0.00865f, -1.68238f, -0.13754f), Vector3D())
                            })
                    )
            ),
            mutableMapOf(Pair(TruckerCargoType.Box, Array(2, { true })))

    ),
    SADLER(
            543,
            0,
            mutableMapOf(
                    Pair(TruckerCargoType.Box, 2.toShort())
            ),
            mutableMapOf(
                    Pair(
                            TruckerCargoType.Box,
                            Array(2, {
                                Pair(Vector3D(-0.33167f, -2.18864f, -0.10093f), Vector3D())
                                Pair(Vector3D(0.34769f, -2.18864f, -0.10093f), Vector3D())
                            })
                    )
            ),
            mutableMapOf(Pair(TruckerCargoType.Box, Array(2, { true })))
    ),
    SADLER_SHIT(605, SADLER),
    BOBCAT(
            422,
            0,
            mapOf(
                    Pair(TruckerCargoType.Box, 3.toShort())
            ),
            mapOf(
                    Pair(
                            TruckerCargoType.Box,
                            Array(3, {
                                Pair(Vector3D(-0.33313f, -0.78529f, -0.28743f), Vector3D())
                                Pair(Vector3D(0.40613f, -0.76478f, -0.30857f), Vector3D())
                                Pair(Vector3D(0.02588f, -0.76478f, -0.30857f), Vector3D())
                            })
                    )
            ),
            mapOf(Pair(TruckerCargoType.Box, Array(3, { true })))
    ),
    WALTON(
            478,
            0,
            mapOf(
                    Pair(TruckerCargoType.Box, 4.toShort())
            ),
            mapOf(
                    Pair(
                            TruckerCargoType.Box,
                            Array(4, {
                                Pair(Vector3D(0.62209f, -2.19438f, -0.03901f), Vector3D())
                                Pair(Vector3D(-0.64597f, -2.15849f, -0.03901f), Vector3D())
                                Pair(Vector3D(-0.64353f, -1.39652f, -0.03901f), Vector3D())
                                Pair(Vector3D(0.18288f, -1.47685f, -0.03901f), Vector3D())
                            })
                    )
            ),
            mapOf(Pair(TruckerCargoType.Box, Array(4, { true })))
    ),
    YOSEMITE(
            554,
            0,
            mapOf(
                    Pair(TruckerCargoType.Box, 6.toShort()),
                    Pair(TruckerCargoType.Bricks, 1.toShort())
            ),
            mapOf(
                    Pair(
                            TruckerCargoType.Box,
                            Array(6, {
                                Pair(Vector3D(-0.39235f, -0.96633f, -0.24251f), Vector3D())
                                Pair(Vector3D(0.3314f, -0.97988f, -0.24251f), Vector3D())
                                Pair(Vector3D(-0.39235f, -1.66616f, -0.24251f), Vector3D())
                                Pair(Vector3D(0.3314f, -1.65933f, -0.24251f), Vector3D())
                                Pair(Vector3D(-0.39235f, -2.37844f, -0.24251f), Vector3D())
                                Pair(Vector3D(0.3314f, -2.39163f, -0.24251f), Vector3D())
                            })
                    ),
                    Pair(
                            TruckerCargoType.Bricks,
                            Array(1, { Pair(Vector3D(-0.01735f, -1.68338f, 0.48368f), Vector3D()) })
                    )
            ),
            mapOf(
                    Pair(TruckerCargoType.Box, Array(6, { true } )),
                    Pair(TruckerCargoType.Bricks, Array(1, { true }))
            )
    ),
    PONY(
            413,
            12,
            mapOf(
                    Pair(TruckerCargoType.Box, 10.toShort())
            ),
            mapOf(
                    Pair(
                            TruckerCargoType.Box,
                            Array(10, {
                                Pair(Vector3D(0.40747f, 0.06923f, -0.25233f), Vector3D())
                                Pair(Vector3D(-0.3114f, 0.06923f, -0.25233f), Vector3D())
                                Pair(Vector3D(0.40747f, -0.63732f, -0.25233f), Vector3D())
                                Pair(Vector3D(-0.3114f, -0.63732f, -0.25233f), Vector3D())
                                Pair(Vector3D(0.40747f, -1.33959f, -0.25233f), Vector3D())
                                Pair(Vector3D(-0.3114f, -1.33959f, -0.25233f), Vector3D())
                                Pair(Vector3D(0.40747f, -2.08287f, -0.25233f), Vector3D())
                                Pair(Vector3D(-0.3114f, -2.08287f, -0.25233f), Vector3D())
                                Pair(Vector3D(0.05444f, -0.00341f, 0.38135f), Vector3D())
                                Pair(Vector3D(0.05444f, -0.75621f, 0.38135f), Vector3D())
                            })
                    )
            ),
            mapOf(Pair(TruckerCargoType.Box, Array(10, { true } )))
    ),
    BERKLEYS_RC_VAN(
            459,
            12,
            mapOf(
                    Pair(TruckerCargoType.Box, 10.toShort())
            ),
            mapOf(
                    Pair(
                            TruckerCargoType.Box,
                            Array(10, {
                                Pair(Vector3D(0.5128174f, 0.0670166f, -0.26280022f), Vector3D())
                                Pair(Vector3D(-0.37316895f, 0.0670166f, -0.26282024f), Vector3D())
                                Pair(Vector3D(0.5062256f, -0.6333008f, -0.26280022f), Vector3D())
                                Pair(Vector3D(-0.375f, -0.6333008f, -0.26282024f), Vector3D())
                                Pair(Vector3D(0.5062256f, -1.3354492f, -0.26280022f), Vector3D())
                                Pair(Vector3D(-0.375f, -1.3354492f, -0.26280022f), Vector3D())
                                Pair(Vector3D(0.5062256f, -2.0534668f, -0.26280022f), Vector3D())
                                Pair(Vector3D(-0.375f, -2.0534668f, -0.26280022f), Vector3D())
                                Pair(Vector3D(0.076538086f, 0.008422852f, 0.36553955f), Vector3D())
                                Pair(Vector3D(0.057617188f, -0.7651367f, 0.37178993f), Vector3D())
                            })
                    )
            ),
            mapOf(
                    Pair(TruckerCargoType.Box, Array(10, { true} ))
            )
    ),
    BURRITO(
            482,
            12,
            Array(1, {
                TruckerVehicleData(
                        TruckerCargoType.Box,
                        10,
                        arrayOf(
                                Vector3D(0.51293945f, -0.23730469f, -0.48169994f),
                                Vector3D(-0.24816895f, -0.23730469f, -0.48169994f),
                                Vector3D(0.51293945f, -0.9420166f, -0.48174f),
                                Vector3D(-0.24816895f, -0.9420166f, -0.48169994f),
                                Vector3D(0.31293945f, -1.5184555f, -0.48169994f),
                                Vector3D(-0.24816895f, -1.6518555f, -0.48169994f),
                                Vector3D(0.4729004f, -0.23730469f, 0.024100304f),
                                Vector3D(-0.24719238f, -0.23730469f, 0.024100304f),
                                Vector3D(0.4729004f, -0.9572754f, 0.024100304f),
                                Vector3D(-0.24719238f, -0.9572754f, 0.024100304f)
                        ),
                        Array(10, {
                            true
                        })
                )
            })
    ),
    RUMPO(
            440,
            24,
            Array(1, {
                TruckerVehicleData(
                        TruckerCargoType.Box,
                        12,
                        Array(12, {
                            Vector3D(0.37438965f, 0.01586914f, -0.4368f)
                            Vector3D(-0.3656006f, 0.01586914f, -0.4368f)
                            Vector3D(0.37438965f, -0.70422363f, -0.4368f)
                            Vector3D(-0.3656006f, -0.70422363f, -0.4368f)
                            Vector3D(0.37438965f, -1.4241943f, -0.4368f)
                            Vector3D(-0.3656006f, -1.4241943f, -0.4368f)
                            Vector3D(0.37438965f, -2.144165f, -0.4368f)
                            Vector3D(-0.3656006f, -2.144165f, -0.4368f)
                            Vector3D(0.37438965f, 0.01586914f, 0.26609993f)
                            Vector3D(-0.3656006f, 0.01586914f, 0.26609993f)
                            Vector3D(0.37438965f, -0.70422363f, 0.26609993f)
                            Vector3D(-0.3656006f, -0.70422363f, 0.26609993f)
                        }),
                        Array(12, { true })
                )
            })
    ),
    BOXVILLE(
            498,
            24,
            arrayOf(
                    TruckerVehicleData(
                            TruckerCargoType.Box,
                            12,
                            arrayOf(
                                    Vector3D(0.039331f, -0.6726074f, -0.43187046f),
                                    Vector3D(0.21936035f, -0.6726074f, -0.43190002f),
                                    Vector3D(-0.1035156f, -0.6726074f, -0.43190002f),
                                    Vector3D(0.239331f, -1.3725586f, -0.43190002f),
                                    Vector3D(0.1936035f, -1.3725586f, -0.43190002f),
                                    Vector3D(-0.1035156f, -1.3725586f, -0.43190002f),
                                    Vector3D(-0.1035156f, -0.6726074f, 0.26809978f),
                                    Vector3D(0.21936035f, -0.6726074f, 0.26809978f),
                                    Vector3D(0.239331f, -0.6726074f, 0.26809978f),
                                    Vector3D(-0.1035156f, -1.3725586f, 0.26809978f),
                                    Vector3D(0.1936035f, -1.3725586f, 0.26809978f),
                                    Vector3D(0.039331f, -1.3725586f, 0.26809978f)
                            ),
                            Array(12, { true })
                    )
            )
    ),
    BOXBURG(609, BOXVILLE),
    BENSON(
            499,
            24,
            arrayOf(
                    TruckerVehicleData(
                            TruckerCargoType.Box,
                            16,
                            arrayOf(
                                    Vector3D(0.5916748f, 0.2919922f, 1.0380993f),
                                    Vector3D(-0.13635254f, 0.2919922f, 1.0380993f),
                                    Vector3D(-0.8084717f, 0.18005371f, 1.0380993f),
                                    Vector3D(-0.7453613f, -0.9719238f, -0.10730076f),
                                    Vector3D(-0.045288086f, -0.9719238f, -0.10730076f),
                                    Vector3D(0.6826172f, -0.9719238f, -0.10730076f),
                                    Vector3D(0.6826172f, -1.7279053f, -0.10730076f),
                                    Vector3D(-0.045288086f, -1.7279053f, -0.10730076f),
                                    Vector3D(-0.7453613f, -1.7279053f, -0.10730076f),
                                    Vector3D(-0.7453613f, -2.4278564f, -0.10730076f),
                                    Vector3D(-0.045288086f, -2.4278564f, -0.10730076f),
                                    Vector3D(0.6826172f, -2.4278564f, -0.10730076f),
                                    Vector3D(0.6826172f, -2.4278564f, 0.5647001f),
                                    Vector3D(-0.045410156f, -2.4278564f, 0.5927f),
                                    Vector3D(-0.7454834f, -2.4278564f, 0.5927f),
                                    Vector3D(-0.045288086f, -1.7279053f, 0.5927f)
                            ),
                            Array(16, { true })
                    ),
                    TruckerVehicleData(
                            TruckerCargoType.Bricks,
                            2,
                            arrayOf(
                                    Vector3D(-0.011474609f, -1.2894287f, 0.5930004f),
                                    Vector3D(-0.021728516f, -2.4053955f, 0.5930004f)
                            ),
                            Array(2, { true })
                    )
            )
    ),
    MULE(
            414,
            32,
            arrayOf(
                    TruckerVehicleData(
                            TruckerCargoType.Box,
                            18,
                            arrayOf(
                                    Vector3D(-0.7192383f, 1.5007324f, 1.3246002f),
                                    Vector3D(-0.019165039f, 1.5007324f, 1.3246002f),
                                    Vector3D(0.65283203f, 1.5007324f, 1.3246002f),
                                    Vector3D(-0.663208f, 0.10070801f, -0.10340023f),
                                    Vector3D(0.036743164f, 0.10070801f, -0.10340023f),
                                    Vector3D(0.7368164f, 0.10070801f, -0.10340023f),
                                    Vector3D(-0.663208f, -0.59924316f, -0.10340023f),
                                    Vector3D(0.036743164f, -0.59924316f, -0.10340023f),
                                    Vector3D(0.70874023f, -0.59924316f, -0.10340023f),
                                    Vector3D(-0.663208f, -1.2993164f, -0.10340023f),
                                    Vector3D(0.036743164f, -1.2993164f, -0.10340023f),
                                    Vector3D(0.68078613f, -1.2993164f, -0.10340023f),
                                    Vector3D(-0.663208f, -1.9992676f, -0.10340023f),
                                    Vector3D(0.036743164f, -1.9992676f, -0.10340023f),
                                    Vector3D(0.68078613f, -1.9992676f, -0.10340023f),
                                    Vector3D(0.7368164f, 0.10070801f, 0.5965996f),
                                    Vector3D(0.036743164f, 0.10070801f, 0.5965996f),
                                    Vector3D(-0.6352539f, 0.10070801f, 0.5965996f)
                            ),
                            Array(18, { true })
                    ),
                    TruckerVehicleData(
                            TruckerCargoType.Bricks,
                            3,
                            arrayOf(
                                    Vector3D(0.06567383f, -0.48010254f, 0.6166992f),
                                    Vector3D(0.06567383f, -2.2441406f, 0.6166992f),
                                    Vector3D(0.06567383f, -1.2081299f, 1.5407f)
                            ),
                            Array(3, { true })
                    )
            )
    ),
    YANKEE(
            456,
            48,
            arrayOf(
                    TruckerVehicleData(
                            TruckerCargoType.Box,
                            24,
                            arrayOf(
                                    Vector3D(0.8388672f, -0.15039062f, 0.058169365f),
                                    Vector3D(0.12792969f, -0.15039062f, 0.058199883f),
                                    Vector3D(-0.5831299f, -0.15039062f, 0.058199883f),
                                    Vector3D(0.8388672f, -0.8613281f, 0.058199883f),
                                    Vector3D(0.12792969f, -0.8613281f, 0.058199883f),
                                    Vector3D(-0.5831299f, -0.8613281f, 0.058199883f),
                                    Vector3D(0.8388672f, -1.5723877f, 0.058199883f),
                                    Vector3D(0.12792969f, -1.5723877f, 0.058199883f),
                                    Vector3D(-0.5831299f, -1.5723877f, 0.058199883f),
                                    Vector3D(0.8388672f, -2.2833252f, 0.058199883f),
                                    Vector3D(0.12792969f, -2.2833252f, 0.058199883f),
                                    Vector3D(-0.5831299f, -2.2833252f, 0.058199883f),
                                    Vector3D(0.8388672f, -2.9943848f, 0.058199883f),
                                    Vector3D(0.12792969f, -2.9943848f, 0.058199883f),
                                    Vector3D(-0.5831299f, -2.9943848f, 0.058199883f),
                                    Vector3D(-0.5831299f, -3.7053223f, 0.058199883f),
                                    Vector3D(0.12792969f, -3.7053223f, 0.058199883f),
                                    Vector3D(0.8388672f, -3.7053223f, 0.058199883f),
                                    Vector3D(0.8388672f, -0.15039062f, 0.7691994f),
                                    Vector3D(0.12792969f, -0.15039062f, 0.7691994f),
                                    Vector3D(-0.5831299f, -0.15039062f, 0.7691994f),
                                    Vector3D(0.8388672f, -0.8613281f, 0.7691994f),
                                    Vector3D(0.12792969f, -0.8613281f, 0.7691994f),
                                    Vector3D(-0.5831299f, -0.8613281f, 0.7691994f)
                            ),
                            Array(24, { true })
                    ),
                    TruckerVehicleData(
                            TruckerCargoType.Bricks,
                            4,
                            arrayOf(
                                    Vector3D(-0.045654297f, -0.5404053f, 0.8616495f),
                                    Vector3D(-0.045654297f, -3.2263184f, 0.8616991f)
                            ),
                            Array(4, { true; false; true; false; } )
                    )
            )
    ),
    FLATBED(
            455,
            32,
            arrayOf(
                    TruckerVehicleData(
                            TruckerCargoType.LooseMaterial,
                            1
                    )
            )
    ),
    DFT30(
            578,
            32,
            arrayOf(
                    TruckerVehicleData(
                            TruckerCargoType.Bricks,
                            3,
                            arrayOf(
                                    Vector3D(-0.068603516f, 1.3687744f, 0.49217987f),
                                    Vector3D(-0.068603516f, -0.9831543f, 0.4921999f),
                                    Vector3D(-0.068603516f, -3.2302246f, 0.4921999f)
                            ),
                            Array(3, { true })
                    ),
                    TruckerVehicleData(
                            TruckerCargoType.Logs,
                            1,
                            arrayOf(
                                    Vector3D(0.203125f, -4.814087f, 0.82102966f),
                                    Vector3D(0.00000f, 0.00000f, 4.53463f)
                            ),
                            arrayOf( true )
                    )
            )
    ),
    /*PACKER(
            443,

    )*/
    SECURICAR(
            428,
            32,
            arrayOf(
                    TruckerVehicleData(
                            TruckerCargoType.Box,
                            16,
                            arrayOf(
                                    Vector3D(0.7293701f, -0.4281006f, -0.15219975f),
                                    Vector3D(0.025268555f, -0.4281006f, -0.15219975f),
                                    Vector3D(-0.6697998f, -0.4281006f, -0.15219975f),
                                    Vector3D(-0.6697998f, -1.1330566f, -0.15219975f),
                                    Vector3D(0.020263672f, -1.1330566f, -0.15219975f),
                                    Vector3D(0.7102051f, -1.1330566f, -0.15219975f),
                                    Vector3D(-0.66955566f, -1.8280029f, -0.15219975f),
                                    Vector3D(0.020385742f, -1.8280029f, -0.15219975f),
                                    Vector3D(0.7104492f, -1.8280029f, -0.15219975f),
                                    Vector3D(-0.66955566f, -2.5179443f, -0.15219975f),
                                    Vector3D(0.020385742f, -2.5179443f, -0.15219975f),
                                    Vector3D(0.7104492f, -2.5179443f, -0.15219975f),
                                    Vector3D(0.7293701f, -0.4281006f, 0.53779984f),
                                    Vector3D(0.03930664f, -0.4281006f, 0.53779984f),
                                    Vector3D(-0.65063477f, -0.4281006f, 0.53779984f),
                                    Vector3D(0.03930664f, -1.1330566f, 0.53779984f)
                            ),
                            Array(16, { true })
                    ),
                    TruckerVehicleData(
                            TruckerCargoType.ValuableBox,
                            16,
                            arrayOf(
                                    Vector3D(0.0013427734f, -0.9185791f, -0.17819977f),
                                    Vector3D(0.0013427734f, -2.1785889f, -0.17819977f)
                            ),
                            Array(16, { true; false; false; false; false; false; false; false; true; })
                    )
            )
    ),
    LINERUNNER(
            403,
            48,
            arrayOf()
    ),
    TANKER(
            514,
            48,
            arrayOf()
    ),
    ROADTRAIN(
            515,
            48,
            arrayOf()
    ),
    ARTICLE_TRAILER(
            435,
            0,
            arrayOf(
                    TruckerVehicleData(TruckerCargoType.Box, 36)
            )
    ),
    ARTICLE_TRAILER2(
            450,
            0,
            arrayOf(
                    TruckerVehicleData(TruckerCargoType.LooseMaterial, 2)
            )
    ),
    PETROL_TRAILER(
            584,
            0,
            arrayOf(
                    TruckerVehicleData(TruckerCargoType.Liquid, 1)
            )
    ),
    ARTICLE_TRAILER3(
            591,
            0,
            arrayOf(
                    TruckerVehicleData(TruckerCargoType.Box, 36)
            )
    )
;

    constructor (modelId: Short, vehicle: TruckerVehicleConstants):
        this(modelId, vehicle.hours, vehicle.vehicleData) {

    }

    constructor(modelId: Short,
                        requiredHours: Short,
                        cargoLimit: Map<TruckerCargoType, Short>,
                        offsets: Map<TruckerCargoType, Array<Pair<Vector3D, Vector3D>>>,
                        visibility: Map<TruckerCargoType, Array<Boolean>>):
    this(modelId, requiredHours,
            cargoLimit.keys.plus(offsets.keys).plus(visibility.keys)
            .map { TruckerVehicleData(it,
                    cargoLimit[it] as Short,
                    offsets[it]?.map { it.first }?.toTypedArray(),
                    offsets[it]?.map { it.second }?.toTypedArray(),
                    visibility[it] as Array<Boolean>) }
            .toTypedArray() ){

    }

    companion object {
        fun isTruckerVehicle(modelid: Int): Boolean {
            values().forEach {
                if(it.modelId.toInt() == modelid)
                    return true
            }
            return false
        }

        fun getByModelId(modelId: Int): TruckerVehicleConstants? {
            values().forEach {
                if(modelId == it.modelId.toInt())
                    return it
            }
            return null
        }
    }

}

