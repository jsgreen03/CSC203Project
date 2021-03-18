import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class OreBlob extends AnimationEntity{

    public static final String QUAKE_KEY = "quake";
    public static final String BLOB_ID_SUFFIX = " -- blob";
    public static final int BLOB_PERIOD_SCALE = 4;
    public static final String BLOB_KEY = "blob";
    public static final int BLOB_ANIMATION_MIN = 50;
    public static final int BLOB_ANIMATION_MAX = 150;
    public static final Random rand = new Random();


    public OreBlob(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        ActiveEntity Vein = new Vein(getId(), getPosition(), getImages(), 0);
        Optional<Entity> blobTarget =
                world.findNearest( this.getPosition(), Vein);
        long nextPeriod = this.getActionPeriod();

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().getPosition();

            if (nextToTsunami(world))
                transform(world, scheduler, imageStore);

            else
                {
            if (this.moveTo( this, world, blobTarget.get(), scheduler)) {
                Quake quake = Factory.createQuake(tgtPos,
                        Functions.getImageList(imageStore, QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
                }
        }

        scheduler.scheduleEvent( this,
                Factory.createActivityAction(this, world, imageStore),
                nextPeriod);
    }

    public boolean moveTo(Entity entity, WorldModel world, Entity target, EventScheduler scheduler)
    {
        if (this.getPosition().adjacent(target.getPosition())) {
            world.removeEntity( target);
            scheduler.unscheduleAllEvents( target);
            return true;
        }
        else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant( nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents( occupant.get());
                }

                world.moveEntity(entity, nextPos);
            }
            return false;
        }
    }

    private Point nextPosition(WorldModel world, Point destPos){
        PathingStrategy pathstrat = new AStarPathingStrategy();

        List<Point> pts = pathstrat.computePath(getPosition(), destPos, pt -> world.withinBounds(pt) && !world.isOccupied(pt), (pt1, pt2) -> withinReach(pt1, pt2), PathingStrategy.CARDINAL_NEIGHBORS);

        if(pts.isEmpty()){
            return getPosition();
        }
        return pts.get(0);
    }

    private static boolean withinReach(Point pt1, Point pt2) {
        // checks if destination has been reached by moving to neighbors
        boolean n1 = (pt1.x + 1 == pt2.x && pt1.y == pt2.y);
        boolean n2 = (pt1.x - 1 == pt2.x && pt1.y == pt2.y);
        boolean n3 = (pt1.x == pt2.x && pt1.y + 1 == pt2.y);
        boolean n4 = (pt1.x == pt2.x && pt1.y -1 == pt2.y);
        return n1 || n2 || n3 || n4;
    }

    private boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {

            WalkingFish fish = Factory.createWalkingFish("walkingfish",
                    getPosition(), Functions.getImageList(imageStore , "walkingfish") , getActionPeriod(),
                    getAnimationPeriod()
                    );

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(fish);
            fish.scheduleActions(scheduler, world, imageStore);

            return true;
    }

    private boolean nextToTsunami(WorldModel world)
    {
        Point pos = getPosition();
        System.out.println("Point:" + pos);
        if (pos.equals(new Point(-1 , -1))) return true;
        Point[] adjacent = new Point[] {new Point( pos.x + 1 , pos.y) , new Point(pos.x - 1 , pos.y) , new Point(pos.x , pos.y + 1) , new Point(pos.x , pos.y - 1)};
        for (Point p : adjacent)
        {
            System.out.println(p);
                if (!(world.getOccupancyCell(p) == null) && (world.withinBounds(p)) && world.getOccupancyCell(p).getId().equals("tsunami"))
                {System.out.print("Found!");
                    return true;}
        }
        return false;
    }


}
