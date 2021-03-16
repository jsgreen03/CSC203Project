import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerNotFull extends Miner{

    public MinerNotFull(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        // should this be activeEntity?
        Entity ore = new Ore(getId(), getPosition(), getImages(), 0);
        Optional<Entity> notFullTarget =
                world.findNearest(getPosition(), ore);

        if (!notFullTarget.isPresent() || !this.moveTo( this, world, notFullTarget.get(), scheduler)
                || !this.transform( world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this, Factory.createActivityAction( this, world, imageStore), getActionPeriod());
        }
    }


    public boolean transform(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.getResourceCount() >= this.getResourceLimit()) {

            //MinerFull miner = Factory.createMinerFull(getId(), getPosition(), getImages(), getActionPeriod(), getAnimationPeriod(), getResourceLimit(), getResourceCount());

            MinerFull miner = Factory.createMinerFull(getId(), getResourceLimit(),
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
            Entity entity,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (getPosition().adjacent( target.getPosition())) {
            //set resource count
            setResourceCount(getResourceCount() + 1);
            world.removeEntity(target);
            scheduler.unscheduleAllEvents( target);

            return true;
        }
        else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant( nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents( occupant.get());
                }

                world.moveEntity( entity, nextPos);
            }
            return false;
        }
    }


}
