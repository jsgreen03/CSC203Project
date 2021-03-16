import processing.core.PImage;

import java.util.List;

public class Ore extends ActiveEntity{


    public Ore(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod)
    {
        super(id, position, images, actionPeriod);
    }


    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Point pos = this.getPosition();

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        OreBlob blob = Factory.createOreBlob(this.getId() + OreBlob.BLOB_ID_SUFFIX, pos,
                this.getActionPeriod() / OreBlob.BLOB_PERIOD_SCALE,
                OreBlob.BLOB_ANIMATION_MIN + OreBlob.rand.nextInt(
                        OreBlob.BLOB_ANIMATION_MAX
                                - OreBlob.BLOB_ANIMATION_MIN),
                Functions.getImageList(imageStore, OreBlob.BLOB_KEY));

        world.addEntity(blob);
        blob.scheduleActions( scheduler, world, imageStore);
    }

}
