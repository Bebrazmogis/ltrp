package lt.ltrp.common.constant;

import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bebras
 *         2015.12.12.
 */
public class LtrpVehicleModel extends VehicleModel {

    private static final Map<Integer, Float> fuelTankSizes;
    private static final Map<Integer, Float> sirenZOffsets;
    private static final Map<Integer, Float> vehicleFuelConsumption;
    private static final List<Integer> vehiclesWithoutWindows;
    private static final List<Integer> vehiclesWithoutEngine;
    private static final List<Integer> bikes;

    public static float getFuelTankSize(int modelid) {
        if(fuelTankSizes.containsKey(modelid))
            return fuelTankSizes.get(modelid);
        else
            return 0.0f;
    }

    public static boolean HasWindows(int modelid) {
        VehicleType type = VehicleModel.getType(modelid);
        if(type == VehicleType.BICYCLE || type == VehicleType.MOTORBIKE) {
            return false;
        }
        return !vehiclesWithoutWindows.contains(modelid);
    }

    public static float getSirenZOffset(int modelid) {
        if(sirenZOffsets.containsKey(modelid)) {
            return sirenZOffsets.get(modelid);
        } else
            return 0.85f;
    }

    public static boolean HasNumberPlates(int modelId) {
        VehicleType type = getType(modelId);
        return isMotorVehicle(modelId) && (type == VehicleType.CAR || type == VehicleType.MOTORBIKE);
    }

    public static boolean isMotorVehicle(int modelid) {
        return !vehiclesWithoutEngine.contains(modelid);
    }

    public static boolean isBike(int modelid) {
        return bikes.contains(modelid);
    }

    public static float getFuelConsumption(int modelid) {
        return vehicleFuelConsumption.get(modelid);
    }

    static {
        vehicleFuelConsumption = new HashMap<>(211);

        vehicleFuelConsumption.put(400,	2f);
        vehicleFuelConsumption.put(401,	2f);
        vehicleFuelConsumption.put(402,	3f);
        vehicleFuelConsumption.put(403,	5f);
        vehicleFuelConsumption.put(404,	1f);
        vehicleFuelConsumption.put(405,	2f);
        vehicleFuelConsumption.put(406,	8f);
        vehicleFuelConsumption.put(407,	4f);
        vehicleFuelConsumption.put(408,	1f);
        vehicleFuelConsumption.put(409,	3f);
        vehicleFuelConsumption.put(410,	1f);
        vehicleFuelConsumption.put(411,	3f);
        vehicleFuelConsumption.put(412,	2f);
        vehicleFuelConsumption.put(413,	1f);
        vehicleFuelConsumption.put(414,	1f);
        vehicleFuelConsumption.put(415,	3f);
        vehicleFuelConsumption.put(416,	2f);
        vehicleFuelConsumption.put(417,	5f);
        vehicleFuelConsumption.put(418,	1f);
        vehicleFuelConsumption.put(419,	2f);
        vehicleFuelConsumption.put(420,	1f);
        vehicleFuelConsumption.put(421,	1f);
        vehicleFuelConsumption.put(422,	1f);
        vehicleFuelConsumption.put(423,	2f);
        vehicleFuelConsumption.put(424,	2f);
        vehicleFuelConsumption.put(425,	8f);
        vehicleFuelConsumption.put(426,	2f);
        vehicleFuelConsumption.put(427,	3f);
        vehicleFuelConsumption.put(428,	3f);
        vehicleFuelConsumption.put(429,	3f);
        vehicleFuelConsumption.put(430,	2f);
        vehicleFuelConsumption.put(431,	3f);
        vehicleFuelConsumption.put(432,	8f);
        vehicleFuelConsumption.put(433,	5f);
        vehicleFuelConsumption.put(434,	2f);
        vehicleFuelConsumption.put(435,	0f);
        vehicleFuelConsumption.put(436,	1f);
        vehicleFuelConsumption.put(437,	3f);
        vehicleFuelConsumption.put(438,	1f);
        vehicleFuelConsumption.put(439,	2f);
        vehicleFuelConsumption.put(440,	1f);
        vehicleFuelConsumption.put(441,	0f);
        vehicleFuelConsumption.put(442,	2f);
        vehicleFuelConsumption.put(443,	5f);
        vehicleFuelConsumption.put(444,	6f);
        vehicleFuelConsumption.put(445,	1f);
        vehicleFuelConsumption.put(446,	3f);
        vehicleFuelConsumption.put(447,	1f);
        vehicleFuelConsumption.put(448,	1f);
        vehicleFuelConsumption.put(449,	0f);
        vehicleFuelConsumption.put(450,	0f);
        vehicleFuelConsumption.put(451,	3f);
        vehicleFuelConsumption.put(452,	3f);
        vehicleFuelConsumption.put(453,	1f);
        vehicleFuelConsumption.put(454,	1f);
        vehicleFuelConsumption.put(455,	3f);
        vehicleFuelConsumption.put(456,	2f);
        vehicleFuelConsumption.put(457,	1f);
        vehicleFuelConsumption.put(458,	2f);
        vehicleFuelConsumption.put(459,	2f);
        vehicleFuelConsumption.put(460,	4f);
        vehicleFuelConsumption.put(461,	1f);
        vehicleFuelConsumption.put(462,	1f);
        vehicleFuelConsumption.put(463,	1f);
        vehicleFuelConsumption.put(464,	1f);
        vehicleFuelConsumption.put(465,	1f);
        vehicleFuelConsumption.put(466,	2f);
        vehicleFuelConsumption.put(467,	2f);
        vehicleFuelConsumption.put(468,	1f);
        vehicleFuelConsumption.put(469,	4f);
        vehicleFuelConsumption.put(470,	4f);
        vehicleFuelConsumption.put(471,	1f);
        vehicleFuelConsumption.put(472,	3f);
        vehicleFuelConsumption.put(473,	1f);
        vehicleFuelConsumption.put(474,	2f);
        vehicleFuelConsumption.put(475,	2f);
        vehicleFuelConsumption.put(476,	3f);
        vehicleFuelConsumption.put(477,	3f);
        vehicleFuelConsumption.put(478,	1f);
        vehicleFuelConsumption.put(479,	1f);
        vehicleFuelConsumption.put(480,	1f);
        vehicleFuelConsumption.put(481,	0f);
        vehicleFuelConsumption.put(482,	2f);
        vehicleFuelConsumption.put(483,	2f);
        vehicleFuelConsumption.put(484,	3f);
        vehicleFuelConsumption.put(485,	1f);
        vehicleFuelConsumption.put(486,	5f);
        vehicleFuelConsumption.put(487,	5f);
        vehicleFuelConsumption.put(488,	5f);
        vehicleFuelConsumption.put(489,	3f);
        vehicleFuelConsumption.put(490,	3f);
        vehicleFuelConsumption.put(491,	1f);
        vehicleFuelConsumption.put(492,	1f);
        vehicleFuelConsumption.put(493,	3f);
        vehicleFuelConsumption.put(494,	3f);
        vehicleFuelConsumption.put(495,	3f);
        vehicleFuelConsumption.put(496,	2f);
        vehicleFuelConsumption.put(497,	4f);
        vehicleFuelConsumption.put(498,	2f);
        vehicleFuelConsumption.put(499,	1f);
        vehicleFuelConsumption.put(500,	2f);
        vehicleFuelConsumption.put(501,	1f);
        vehicleFuelConsumption.put(502,	3f);
        vehicleFuelConsumption.put(503,	3f);
        vehicleFuelConsumption.put(504,	3f);
        vehicleFuelConsumption.put(505,	2f);
        vehicleFuelConsumption.put(506,	3f);
        vehicleFuelConsumption.put(507,	2f);
        vehicleFuelConsumption.put(508,	3f);
        vehicleFuelConsumption.put(509,	0f);
        vehicleFuelConsumption.put(510,	0f);
        vehicleFuelConsumption.put(511,	6f);
        vehicleFuelConsumption.put(512,	5f);
        vehicleFuelConsumption.put(513,	5f);
        vehicleFuelConsumption.put(514,	6f);
        vehicleFuelConsumption.put(515,	7f);
        vehicleFuelConsumption.put(516,	1f);
        vehicleFuelConsumption.put(517,	1f);
        vehicleFuelConsumption.put(518,	2f);
        vehicleFuelConsumption.put(519,	9f);
        vehicleFuelConsumption.put(520,	12f);
        vehicleFuelConsumption.put(521,	1f);
        vehicleFuelConsumption.put(522,	2f);
        vehicleFuelConsumption.put(523,	1f);
        vehicleFuelConsumption.put(524,	6f);
        vehicleFuelConsumption.put(525,	2f);
        vehicleFuelConsumption.put(526,	1f);
        vehicleFuelConsumption.put(527,	1f);
        vehicleFuelConsumption.put(528,	3f);
        vehicleFuelConsumption.put(529,	1f);
        vehicleFuelConsumption.put(530,	1f);
        vehicleFuelConsumption.put(531,	2f);
        vehicleFuelConsumption.put(532,	3f);
        vehicleFuelConsumption.put(533,	2f);
        vehicleFuelConsumption.put(534,	2f);
        vehicleFuelConsumption.put(535,	2f);
        vehicleFuelConsumption.put(536,	2f);
        vehicleFuelConsumption.put(537,	0f);
        vehicleFuelConsumption.put(538,	0f);
        vehicleFuelConsumption.put(539,	1f);
        vehicleFuelConsumption.put(540,	2f);
        vehicleFuelConsumption.put(541,	3f);
        vehicleFuelConsumption.put(542,	2f);
        vehicleFuelConsumption.put(543,	1f);
        vehicleFuelConsumption.put(544,	5f);
        vehicleFuelConsumption.put(545,	2f);
        vehicleFuelConsumption.put(546,	1f);
        vehicleFuelConsumption.put(547,	1f);
        vehicleFuelConsumption.put(548,	9f);
        vehicleFuelConsumption.put(549,	2f);
        vehicleFuelConsumption.put(550,	2f);
        vehicleFuelConsumption.put(551,	2f);
        vehicleFuelConsumption.put(552,	3f);
        vehicleFuelConsumption.put(553,	15f);
        vehicleFuelConsumption.put(554,	2f);
        vehicleFuelConsumption.put(555,	2f);
        vehicleFuelConsumption.put(556,	7f);
        vehicleFuelConsumption.put(557,	7f);
        vehicleFuelConsumption.put(558,	2f);
        vehicleFuelConsumption.put(559,	2f);
        vehicleFuelConsumption.put(560,	2f);
        vehicleFuelConsumption.put(561,	2f);
        vehicleFuelConsumption.put(562,	2f);
        vehicleFuelConsumption.put(563,	7f);
        vehicleFuelConsumption.put(564,	1f);
        vehicleFuelConsumption.put(565,	2f);
        vehicleFuelConsumption.put(566,	2f);
        vehicleFuelConsumption.put(567,	2f);
        vehicleFuelConsumption.put(568,	3f);
        vehicleFuelConsumption.put(569,	0f);
        vehicleFuelConsumption.put(570,	0f);
        vehicleFuelConsumption.put(571,	1f);
        vehicleFuelConsumption.put(572,	1f);
        vehicleFuelConsumption.put(573,	5f);
        vehicleFuelConsumption.put(574,	1f);
        vehicleFuelConsumption.put(575,	2f);
        vehicleFuelConsumption.put(576,	2f);
        vehicleFuelConsumption.put(577,	20f);
        vehicleFuelConsumption.put(578,	4f);
        vehicleFuelConsumption.put(579,	2f);
        vehicleFuelConsumption.put(580,	1f);
        vehicleFuelConsumption.put(581,	1f);
        vehicleFuelConsumption.put(582,	2f);
        vehicleFuelConsumption.put(583,	1f);
        vehicleFuelConsumption.put(584,	0f);
        vehicleFuelConsumption.put(585,	2f);
        vehicleFuelConsumption.put(586,	1f);
        vehicleFuelConsumption.put(587,	2f);
        vehicleFuelConsumption.put(588,	2f);
        vehicleFuelConsumption.put(589,	2f);
        vehicleFuelConsumption.put(590,	0f);
        vehicleFuelConsumption.put(591,	0f);
        vehicleFuelConsumption.put(592,	20f);
        vehicleFuelConsumption.put(593,	6f);
        vehicleFuelConsumption.put(594,	1f);
        vehicleFuelConsumption.put(595,	4f);
        vehicleFuelConsumption.put(596,	1f);
        vehicleFuelConsumption.put(597,	2f);
        vehicleFuelConsumption.put(598,	1f);
        vehicleFuelConsumption.put(599,	2f);
        vehicleFuelConsumption.put(600,	2f);
        vehicleFuelConsumption.put(601,	4f);
        vehicleFuelConsumption.put(602,	2f);
        vehicleFuelConsumption.put(603,	3f);
        vehicleFuelConsumption.put(604,	3f);
        vehicleFuelConsumption.put(605,	2f);
        vehicleFuelConsumption.put(606,	0f);
        vehicleFuelConsumption.put(607,	0f);
        vehicleFuelConsumption.put(608,	0f);
        vehicleFuelConsumption.put(609,	1f);
        vehicleFuelConsumption.put(610,	0f);
        vehicleFuelConsumption.put(611,	0f);

        bikes = new ArrayList<>();
        bikes.add(581);
        bikes.add(462);
        bikes.add(521);
        bikes.add(463);
        bikes.add(522);
        bikes.add(461);
        bikes.add(448);
        bikes.add(471);
        bikes.add(468);
        bikes.add(481);
        bikes.add(523);
        bikes.add(586);
        bikes.add(509);
        bikes.add(510);

        vehiclesWithoutEngine = new ArrayList<>();
        vehiclesWithoutEngine.add(509);
        vehiclesWithoutEngine.add(481);
        vehiclesWithoutEngine.add(510);

        sirenZOffsets = new HashMap<>();
        sirenZOffsets.put(596, 0.9f);	// PD
        sirenZOffsets.put(597, 0.9f);	// PD
        sirenZOffsets.put(598, 0.9f);	// PD
        sirenZOffsets.put(599, 1.1f);	// PD Rancher
        sirenZOffsets.put(541, 0.65f);	// Bullet
        sirenZOffsets.put(560, 0.85f);	// Sultan
        sirenZOffsets.put(566, 0.9f);	// Tahoma
        sirenZOffsets.put(490, 1.1f);	// FBI Rancher
        sirenZOffsets.put(426, 0.9f);	// Premier
        sirenZOffsets.put(558, 0.9f);	// Uranus
        sirenZOffsets.put(559, 0.65f);	// Jester


        fuelTankSizes = new HashMap<>();
        fuelTankSizes.put(400, 60.0f);
        fuelTankSizes.put(401, 50.0f);
        fuelTankSizes.put(402, 60.0f);
        fuelTankSizes.put(403, 160.0f);
        fuelTankSizes.put(404, 50.0f);
        fuelTankSizes.put(405, 60.0f);
        fuelTankSizes.put(406, 300.0f);
        fuelTankSizes.put(407, 120.0f);
        fuelTankSizes.put(408, 400.0f);
        fuelTankSizes.put(409, 90.0f);
        fuelTankSizes.put(410, 50.0f);
        fuelTankSizes.put(411, 70.0f);
        fuelTankSizes.put(412, 60.0f);
        fuelTankSizes.put(413, 80.0f);
        fuelTankSizes.put(414, 80.0f);
        fuelTankSizes.put(415, 70.0f);
        fuelTankSizes.put(416, 90.0f);
        fuelTankSizes.put(417, 120.0f);
        fuelTankSizes.put(418, 90.0f);
        fuelTankSizes.put(419, 70.0f);
        fuelTankSizes.put(420, 60.0f);
        fuelTankSizes.put(421, 50.0f);
        fuelTankSizes.put(422, 50.0f);
        fuelTankSizes.put(423, 90.0f);
        fuelTankSizes.put(424, 50.0f);
        fuelTankSizes.put(425, 150.0f);
        fuelTankSizes.put(426, 80.0f);
        fuelTankSizes.put(427, 100.0f);
        fuelTankSizes.put(428, 100.0f);
        fuelTankSizes.put(429, 80.0f);
        fuelTankSizes.put(430, 80.0f);
        fuelTankSizes.put(431, 80.0f);
        fuelTankSizes.put(432, 280.0f);
        fuelTankSizes.put(433, 180.0f);
        fuelTankSizes.put(434, 60.0f);
        fuelTankSizes.put(435, 15.0f);
        fuelTankSizes.put(436, 55.0f);
        fuelTankSizes.put(437, 120.0f);
        fuelTankSizes.put(438, 60.0f);
        fuelTankSizes.put(439, 65.0f);
        fuelTankSizes.put(440, 90.0f);
        fuelTankSizes.put(441, 2.0f);
        fuelTankSizes.put(442, 60.0f);
        fuelTankSizes.put(443, 130.0f);
        fuelTankSizes.put(444, 60.0f);
        fuelTankSizes.put(445, 60.0f);
        fuelTankSizes.put(446, 90.0f);
        fuelTankSizes.put(447, 45.0f);
        fuelTankSizes.put(448, 30.0f);
        fuelTankSizes.put(449, 0.0f);
        fuelTankSizes.put(450, 0.0f);
        fuelTankSizes.put(451, 80.0f);
        fuelTankSizes.put(452, 90.0f);
        fuelTankSizes.put(453, 50.0f);
        fuelTankSizes.put(454, 50.0f);
        fuelTankSizes.put(455, 85.0f);
        fuelTankSizes.put(456, 85.0f);
        fuelTankSizes.put(457, 30.0f);
        fuelTankSizes.put(458, 60.0f);
        fuelTankSizes.put(459, 80.0f);
        fuelTankSizes.put(460, 150.0f);
        fuelTankSizes.put(461, 40.0f);
        fuelTankSizes.put(462, 30.0f);
        fuelTankSizes.put(463, 45.0f);
        fuelTankSizes.put(464, 2.0f);
        fuelTankSizes.put(465, 2.0f);
        fuelTankSizes.put(466, 60.0f);
        fuelTankSizes.put(467, 60.0f);
        fuelTankSizes.put(468, 25.0f);
        fuelTankSizes.put(469, 80.0f);
        fuelTankSizes.put(470, 130.0f);
        fuelTankSizes.put(471, 35.0f);
        fuelTankSizes.put(472, 60.0f);
        fuelTankSizes.put(473, 45.0f);
        fuelTankSizes.put(474, 60.0f);
        fuelTankSizes.put(475, 75.0f);
        fuelTankSizes.put(476, 90.0f);
        fuelTankSizes.put(477, 80.0f);
        fuelTankSizes.put(478, 60.0f);
        fuelTankSizes.put(479, 55.0f);
        fuelTankSizes.put(480, 60.0f);
        fuelTankSizes.put(481, 0.0f);
        fuelTankSizes.put(482, 90.0f);
        fuelTankSizes.put(483, 90.0f);
        fuelTankSizes.put(484, 120.0f);
        fuelTankSizes.put(485, 50.0f);
        fuelTankSizes.put(486, 130.0f);
        fuelTankSizes.put(487, 130.0f);
        fuelTankSizes.put(488, 110.0f);
        fuelTankSizes.put(489, 90.0f);
        fuelTankSizes.put(490, 90.0f);
        fuelTankSizes.put(491, 68.0f);
        fuelTankSizes.put(492, 62.0f);
        fuelTankSizes.put(493, 110.0f);
        fuelTankSizes.put(494, 90.0f);
        fuelTankSizes.put(495, 100.0f);
        fuelTankSizes.put(496, 70.0f);
        fuelTankSizes.put(497, 150.0f);
        fuelTankSizes.put(498, 120.0f);
        fuelTankSizes.put(499, 90.0f);
        fuelTankSizes.put(500, 60.0f);
        fuelTankSizes.put(501, 1.0f);
        fuelTankSizes.put(502, 90.0f);
        fuelTankSizes.put(503, 90.0f);
        fuelTankSizes.put(504, 90.0f);
        fuelTankSizes.put(505, 90.0f);
        fuelTankSizes.put(506, 70.0f);
        fuelTankSizes.put(507, 62.0f);
        fuelTankSizes.put(508, 82.0f);
        fuelTankSizes.put(509, 0.0f);
        fuelTankSizes.put(510, 0.0f);
        fuelTankSizes.put(511, 80.0f);
        fuelTankSizes.put(512, 80.0f);
        fuelTankSizes.put(513, 60.0f);
        fuelTankSizes.put(514, 180.0f);
        fuelTankSizes.put(515, 200.0f);
        fuelTankSizes.put(516, 60.0f);
        fuelTankSizes.put(517, 50.0f);
        fuelTankSizes.put(518, 90.0f);
        fuelTankSizes.put(519, 250.0f);
        fuelTankSizes.put(520, 210.0f);
        fuelTankSizes.put(521, 40.0f);
        fuelTankSizes.put(522, 50.0f);
        fuelTankSizes.put(523, 45.0f);
        fuelTankSizes.put(524, 120.0f);
        fuelTankSizes.put(525, 130.0f);
        fuelTankSizes.put(526, 65.0f);
        fuelTankSizes.put(527, 60.0f);
        fuelTankSizes.put(528, 100.0f);
        fuelTankSizes.put(529, 80.0f);
        fuelTankSizes.put(530, 30.0f);
        fuelTankSizes.put(531, 50.0f);
        fuelTankSizes.put(532, 150.0f);
        fuelTankSizes.put(533, 65.0f);
        fuelTankSizes.put(534, 80.0f);
        fuelTankSizes.put(535, 85.0f);
        fuelTankSizes.put(536, 90.0f);
        fuelTankSizes.put(537, 82.0f);
        fuelTankSizes.put(538, 82.0f);
        fuelTankSizes.put(539, 30.0f);
        fuelTankSizes.put(540, 70.0f);
        fuelTankSizes.put(541, 80.0f);
        fuelTankSizes.put(542, 68.0f);
        fuelTankSizes.put(543, 55.0f);
        fuelTankSizes.put(544, 110.0f);
        fuelTankSizes.put(545, 60.0f);
        fuelTankSizes.put(546, 60.0f);
        fuelTankSizes.put(547, 60.0f);
        fuelTankSizes.put(548, 250.0f);
        fuelTankSizes.put(549, 86.0f);
        fuelTankSizes.put(550, 85.0f);
        fuelTankSizes.put(551, 98.0f);
        fuelTankSizes.put(552, 120.0f);
        fuelTankSizes.put(553, 400.0f);
        fuelTankSizes.put(554, 78.0f);
        fuelTankSizes.put(555, 72.0f);
        fuelTankSizes.put(556, 120.0f);
        fuelTankSizes.put(557, 120.0f);
        fuelTankSizes.put(558, 70.0f);
        fuelTankSizes.put(559, 80.0f);
        fuelTankSizes.put(560, 159.0f);
        fuelTankSizes.put(561, 75.0f);
        fuelTankSizes.put(562, 70.0f);
        fuelTankSizes.put(563, 200.0f);
        fuelTankSizes.put(564, 2.0f);
        fuelTankSizes.put(565, 65.0f);
        fuelTankSizes.put(566, 62.0f);
        fuelTankSizes.put(567, 73.0f);
        fuelTankSizes.put(568, 30.0f);
        fuelTankSizes.put(569, 0.0f);
        fuelTankSizes.put(570, 0.0f);
        fuelTankSizes.put(571, 20.0f);
        fuelTankSizes.put(572, 20.0f);
        fuelTankSizes.put(573, 150.0f);
        fuelTankSizes.put(574, 400.0f);
        fuelTankSizes.put(575, 68.0f);
        fuelTankSizes.put(576, 68.0f);
        fuelTankSizes.put(577, 500.0f);
        fuelTankSizes.put(578, 120.0f);
        fuelTankSizes.put(579, 90.0f);
        fuelTankSizes.put(580, 60.0f);
        fuelTankSizes.put(581, 40.0f);
        fuelTankSizes.put(582, 90.0f);
        fuelTankSizes.put(583, 20.0f);
        fuelTankSizes.put(584, 0.0f);
        fuelTankSizes.put(585, 70.0f);
        fuelTankSizes.put(586, 50.0f);
        fuelTankSizes.put(587, 75.0f);
        fuelTankSizes.put(588, 90.0f);
        fuelTankSizes.put(589, 60.0f);
        fuelTankSizes.put(590, 0.0f);
        fuelTankSizes.put(591, 0.0f);
        fuelTankSizes.put(592, 600.0f);
        fuelTankSizes.put(593, 120.0f);
        fuelTankSizes.put(594, 0.0f);
        fuelTankSizes.put(595, 130.0f);
        fuelTankSizes.put(596, 189.0f);
        fuelTankSizes.put(597, 82.0f);
        fuelTankSizes.put(598, 234.0f);
        fuelTankSizes.put(599, 199.0f);
        fuelTankSizes.put(600, 60.0f);
        fuelTankSizes.put(601, 130.0f);
        fuelTankSizes.put(602, 60.0f);
        fuelTankSizes.put(603, 85.0f);
        fuelTankSizes.put(604, 80.0f);
        fuelTankSizes.put(605, 50.0f);
        fuelTankSizes.put(606, 0.0f);
        fuelTankSizes.put(607, 0.0f);
        fuelTankSizes.put(608, 0.0f);
        fuelTankSizes.put(609, 400.0f);
        fuelTankSizes.put(610, 0.0f);
        fuelTankSizes.put(611, 0.0f);

        vehiclesWithoutWindows = new ArrayList<>();
        vehiclesWithoutWindows.add(424);
        vehiclesWithoutWindows.add(429);
        vehiclesWithoutWindows.add(430);
        vehiclesWithoutWindows.add(432);
        vehiclesWithoutWindows.add(439);
        vehiclesWithoutWindows.add(446);
        vehiclesWithoutWindows.add(452);
        vehiclesWithoutWindows.add(453);
        vehiclesWithoutWindows.add(454);
        vehiclesWithoutWindows.add(457);
        vehiclesWithoutWindows.add(471);
        vehiclesWithoutWindows.add(472);
        vehiclesWithoutWindows.add(473);
        vehiclesWithoutWindows.add(480);
        vehiclesWithoutWindows.add(484);
        vehiclesWithoutWindows.add(485);
        vehiclesWithoutWindows.add(486);
        vehiclesWithoutWindows.add(493);
        vehiclesWithoutWindows.add(530);
        vehiclesWithoutWindows.add(531);
        vehiclesWithoutWindows.add(533);
        vehiclesWithoutWindows.add(536);
        vehiclesWithoutWindows.add(539);
        vehiclesWithoutWindows.add(555);
        vehiclesWithoutWindows.add(567);
        vehiclesWithoutWindows.add(568);
        vehiclesWithoutWindows.add(571);
        vehiclesWithoutWindows.add(572);
        vehiclesWithoutWindows.add(575);
        vehiclesWithoutWindows.add(595);

    }


}
