import processing.core.PImage;

import java.util.List;

public class Factory {

    private static final String QUAKE_ID = "quake";
    private static final int QUAKE_ACTION_PERIOD = 1100;
    private static final int QUAKE_ANIMATION_PERIOD = 100;


    public static MinerFull createMinerFull(
            String id,
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new MinerFull( id, position, images,
                resourceLimit, resourceLimit, actionPeriod,
                animationPeriod);
    }

    public static MinerNotFull createMinerNotFull(
            String id,
            int resourceLimit,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new MinerNotFull( id, position, images,
                resourceLimit, 0, actionPeriod, animationPeriod);
    }


    public static Ore createOre(
            String id, Point position, int actionPeriod, List<PImage> images)
    {
        return new Ore( id, position, images, actionPeriod);
    }

    public static OreBlob createOreBlob(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new OreBlob( id, position, images, actionPeriod, animationPeriod);
    }

    public static Quake createQuake(
            Point position, List<PImage> images)
    {
        return new Quake( QUAKE_ID, position, images, QUAKE_ACTION_PERIOD, QUAKE_ANIMATION_PERIOD);
    }

    public static Vein createVein(
            String id, Point position, int actionPeriod, List<PImage> images)
    {
        return new Vein( id, position, images, actionPeriod);
    }

    public static Action createActivityAction(Entity entity, WorldModel world, ImageStore imageStore)
    {
        return new Activity( entity, world, imageStore, 0);
    }


    public static Action createAnimationAction( Entity entity, int repeatCount) {
        return new Animation( entity, null, null, repeatCount);
    }

    public static Seal createSeal(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        return new Seal(id, position, images, actionPeriod, animationPeriod);
    }

}
