import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Vein extends ActiveEntity{

    private static final Random rand = new Random();
    private static final String ORE_KEY = "ore";

    private static final String ORE_ID_PREFIX = "ore -- ";
    private static final int ORE_CORRUPT_MIN = 20000;
    private static final int ORE_CORRUPT_MAX = 30000;

    public Vein(
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
        Optional<Point> openPt = world.findOpenAround( this.getPosition());

        if (openPt.isPresent() ) {
            Ore ore = Factory.createOre(ORE_ID_PREFIX + this.getId(), openPt.get(),
                    ORE_CORRUPT_MIN + rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                    Functions.getImageList(imageStore, ORE_KEY));
            world.addEntity( ore);
            ore.scheduleActions(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent( this,
                Factory.createActivityAction( this, world, imageStore),
                this.getActionPeriod());
    }

}
