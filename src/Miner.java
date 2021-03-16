import processing.core.PImage;

import java.util.List;

public abstract class Miner extends AnimationEntity{
    private int resourceLimit;
    private int resourceCount;

    public Miner(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod){
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
    }

    protected Point nextPosition(WorldModel world, Point destPos){
        PathingStrategy pathstrat = new AStarPathingStrategy();
        //PathingStrategy pathstrat = new SingleStepPathingStrategy();

        List<Point> pts = pathstrat.computePath(getPosition(), destPos, pt -> world.withinBounds(pt) && !world.isOccupied(pt), (pt1, pt2) -> withinReach(pt1, pt2), PathingStrategy.CARDINAL_NEIGHBORS);

        if(pts.isEmpty()){
            return getPosition();
        }
        return pts.get(0);
    }

    protected static boolean withinReach(Point pt1, Point pt2) {
        // checks if destination has been reached by moving to neighbors
        boolean n1 = (pt1.x + 1 == pt2.x && pt1.y == pt2.y);
        boolean n2 = (pt1.x - 1 == pt2.x && pt1.y == pt2.y);
        boolean n3 = (pt1.x == pt2.x && pt1.y + 1 == pt2.y);
        boolean n4 = (pt1.x == pt2.x && pt1.y -1 == pt2.y);
        return n1 || n2 || n3 || n4;
    }


    protected int getResourceLimit(){
        return resourceLimit;
    }

    protected int getResourceCount(){
        return resourceCount;
    }

    protected void setResourceCount(int resourceCount){
        this.resourceCount = resourceCount;
    }

}
