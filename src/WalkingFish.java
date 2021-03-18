import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class WalkingFish extends Miner {

    private Miner oldMiner;

    private PathingStrategy strategy = new SingleStepPathingStrategy();

    public WalkingFish(String id, Point position,
                     List<PImage> images,
                     int actionPeriod, int animationPeriod, Miner oldMiner) {
        super(id, position, images, 0, 0, actionPeriod, animationPeriod);

        this.oldMiner = oldMiner;
    }

    public void executeActivity(WorldModel world,
                                ImageStore imageStore,
                                EventScheduler scheduler)
    {
        Entity blacksmith = new Blacksmith(getId(), getPosition(), getImages());
        Optional<Entity> fullTarget =
                world.findNearest(getPosition(), blacksmith);

        if (fullTarget.isPresent() && moveTo( this, world,
                fullTarget.get(), scheduler))
        {
            this.transform( world, scheduler, imageStore);
        }
        else {
            scheduler.scheduleEvent(this,
                    Factory.createActivityAction( this, world, imageStore),
                    getActionPeriod());
        }
    }

    public boolean transform(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (getResourceCount() >= getResourceLimit()) {

            MinerNotFull miner = Factory.createMinerNotFull(getId(), getResourceLimit(),
                    getPosition(), getActionPeriod(),
                    getAnimationPeriod(),
                    getImages());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions( scheduler, world, imageStore);

            return true;
        }

        return false;
    }


    public boolean moveTo(
            Entity miner,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (getPosition().adjacent(target.getPosition())) {
            return true;
        }
        else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant( nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents( occupant.get());
                }

                world.moveEntity( miner, nextPos);
            }
            return false;
        }
    }

}
