
public class Activity extends Action {

    public Activity(Entity entity, WorldModel world, ImageStore imageStore, int repeatCount)
    {
        super(entity, world, imageStore, repeatCount);
    }


    public void executeAction(EventScheduler scheduler)
    {

        if (getEntity() instanceof MinerFull) {
            ((MinerFull) (getEntity())).executeActivity(getWorld(), getImageStore(), scheduler);
        }
        else if(getEntity() instanceof MinerNotFull) {
            ((MinerNotFull) (getEntity())).executeActivity(getWorld(), getImageStore(), scheduler);
        }
        else if(getEntity() instanceof Ore) {
            ((Ore)(getEntity())).executeActivity(getWorld(), getImageStore(), scheduler);
        }
        else if(getEntity() instanceof OreBlob) {
            ((OreBlob)(getEntity())).executeActivity(getWorld(), getImageStore(), scheduler);
        }
        else if(getEntity() instanceof Quake) {
            ((Quake)(getEntity())).executeActivity(getWorld(), getImageStore(), scheduler);
        }
        else if(getEntity() instanceof Vein) {
            ((Vein)(getEntity())).executeActivity(getWorld(), getImageStore(), scheduler);
        }
        else if(getEntity() instanceof Seal){
            ((Seal)(getEntity())).executeActivity(getWorld(), getImageStore(), scheduler);
        }
        else{
            throw new UnsupportedOperationException(String.format(
                    "executeActivityAction not supported for %s",
                    getEntity()));
        }
    }



}


