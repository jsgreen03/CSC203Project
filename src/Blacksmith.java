import processing.core.PImage;

import java.util.List;

public class Blacksmith extends Entity{

    public Blacksmith(String id, Point position, List<PImage> images)
    {
        super(id, position, images);
    }

    public static Blacksmith createBlacksmith(String id, Point position, List<PImage> images)
    {
        return new Blacksmith( id, position, images);
    }

}
