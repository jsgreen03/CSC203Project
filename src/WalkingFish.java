import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class WalkingFish extends AnimationEntity {

    private PathingStrategy strategy = new AStarPathingStrategy();

    public WalkingFish(String id, Point position,
                     List<PImage> images,
                     int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
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
            this.transform(world, scheduler, imageStore);
        }
        else {
            scheduler.scheduleEvent(this,
                    Factory.createActivityAction( this, world, imageStore),
                    getActionPeriod());
        }
    }


    private Point nextPosition(WorldModel world, Point destPos)
    {
        List<Point> pts = strategy.computePath(getPosition(), destPos, pt -> world.withinBounds(pt) && !world.isOccupied(pt), (pt1, pt2) -> withinReach(pt1, pt2), PathingStrategy.CARDINAL_NEIGHBORS);
        if(pts.isEmpty())
        {
            return getPosition();
        }
        return pts.get(0);
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

    private boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        if (nextToBlacksmith(world))
        {
            OreBlob oreBlob = Factory.createOreBlob(getId(),
                    getPosition() , getActionPeriod(),
                    getAnimationPeriod() , Functions.getImageList(imageStore , "seagull")
            );

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(oreBlob);
            oreBlob.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    private boolean nextToBlacksmith(WorldModel world)
    {
        Point pos = getPosition();
        if (pos.equals(new Point(-1 , -1)))
            return true;
        Point[] adjacent = new Point[] {new Point( pos.x + 1 , pos.y) , new Point(pos.x - 1 , pos.y) , new Point(pos.x , pos.y + 1) , new Point(pos.x , pos.y - 1)};
        for (Point p : adjacent)
        {
            if (world.withinBounds(p)) {
            if (!(world.getOccupancyCell(p) == null) && world.getOccupancyCell(p).getClass().equals(Blacksmith.class))
                return true;
        }
        }
        return false;
    }

    private static boolean withinReach(Point pt1, Point pt2) {
        // checks if destination has been reached by moving to neighbors
        boolean n1 = (pt1.x + 1 == pt2.x && pt1.y == pt2.y);
        boolean n2 = (pt1.x - 1 == pt2.x && pt1.y == pt2.y);
        boolean n3 = (pt1.x == pt2.x && pt1.y + 1 == pt2.y);
        boolean n4 = (pt1.x == pt2.x && pt1.y -1 == pt2.y);
        return n1 || n2 || n3 || n4;
    }

}
