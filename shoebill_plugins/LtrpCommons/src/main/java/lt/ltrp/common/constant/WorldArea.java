package lt.ltrp.common.constant;

import net.gtaun.shoebill.data.Area3D;

/**
 * @author Bebras
 *         2016.03.29.
 */
public final class WorldArea {

    private String name;
    private Area3D[] areas;
    private WorldZone[] zones;


    private WorldArea(String name, float... positions) {
        this.name = name;
        areas = new Area3D[positions.length / 6];
        for(int i = 0; i < positions.length; i += 6) {
            areas[i] = new Area3D(positions[i], positions[i+1], positions[i+2], positions[i+3], positions[i+4], positions[i+5]);
        }

        for(WorldZone z : WorldZone.WORLD_ZONES) {
            for(Area3D area : areas) {
                Area3D zoneArea = z.getArea();
                if(zoneArea.minX >= area.minX && zoneArea.maxX <= zoneArea.maxX
                        && zoneArea.minY >= area.minY && zoneArea.maxY <= zoneArea.maxY
                        && zoneArea.minZ >= area.minZ && zoneArea.maxZ <= zoneArea.maxZ) {
                    addZone(z);
                }
            }
        }
    }

    private void addZone(WorldZone zone) {
        if(zones != null) {
            WorldZone[] tmp = new WorldZone[areas.length + 1];
            for(int i = 0; i < zones.length; i++)
                tmp[i+1] = zones[i];
            tmp[0] = zone;
        } else {
            zones = new WorldZone[1];
            zones[0] = zone;
        }

    }

    public String getName() {
        return name;
    }

    public Area3D[] getAreas() {
        return areas;
    }

    public WorldZone[] getZones() {
        return zones;
    }

    public static final WorldArea[] WORLD_AREAS = {
            new WorldArea("Los Santos",     44.60f,-2892.90f,-242.90f,2997.00f,-768.00f,900.00f),
            new WorldArea("Las Venturas",   869.40f,596.30f,-242.90f,2997.00f,2993.80f,900.00f),
            new WorldArea("Bone County",    -480.50f,596.30f,-242.90f,869.40f,2993.80f,900.00f),
            new WorldArea("Tierra Robada",  -2997.40f,1659.60f,-242.90f,-480.50f,2993.80f,900.00f, -1213.90f,596.30f,-242.90f,-480.50f,1659.60f,900.00f),
            new WorldArea("San Fierro",     -2997.40f,-1115.50f,-242.90f,-1213.90f,1659.60f,900.00f),
            new WorldArea("Red County",     -1213.90f,-768.00f,-242.90f,2997.00f,596.30f,900.00f),
            new WorldArea("Flint County",   -1213.90f,-2892.90f,-242.90f,44.60f,-768.00f,900.00f),
            new WorldArea("Whetstone",      -2997.40f,-2892.90f,-242.90f,-1213.90f,-1115.50f,900.0f)

    };
}
