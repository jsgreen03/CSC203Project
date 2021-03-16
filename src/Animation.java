public class Animation extends Action {

    public Animation(
            Entity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        super(entity, world, imageStore, repeatCount);
    }

    public void executeAction(EventScheduler scheduler)
    {
        getEntity().nextImage();

        if (getRepeatCount() != 1) {
            scheduler.scheduleEvent(getEntity(),
                    Factory.createAnimationAction( getEntity(), Math.max(getRepeatCount() - 1, 0)),
                    ((AnimationEntity)getEntity()).getAnimationPeriod());
        }
    }



}
