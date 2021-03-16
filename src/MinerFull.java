import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinerFull extends Miner{

    public MinerFull(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod);
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


    //fix!
    /*
    public Point nextPosition(WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - getPosition().x);
        Point newPos = new Point(getPosition().x + horiz, getPosition().y);

        if (horiz == 0 || world.isOccupied(newPos)) {
            int vert = Integer.signum(destPos.y - getPosition().y);
            newPos = new Point(getPosition().x, getPosition().y + vert);

            if (vert == 0 || world.isOccupied( newPos)) {
                newPos = getPosition();
            }
        }

        return newPos;
    }

     */

}
