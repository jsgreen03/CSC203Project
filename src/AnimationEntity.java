import processing.core.PImage;
import java.util.List;

public abstract class AnimationEntity extends ActiveEntity {

    private int animationPeriod;

    public AnimationEntity(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod){
        super(id, position, images, actionPeriod);
        this.animationPeriod = animationPeriod;
    }

    protected int getAnimationPeriod(){
        return animationPeriod;
    }

    protected void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), getActionPeriod());
        scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), getAnimationPeriod());
    }

    protected void nextImage(){
        setImageIndex((getImageIndex() + 1) % this.getImages().size());
    }
}
