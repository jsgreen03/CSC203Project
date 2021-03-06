import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import processing.core.*;

public final class VirtualWorld extends PApplet
{
    public static final int TIMER_ACTION_PERIOD = 100;

    public static final int VIEW_WIDTH = 640;
    public static final int VIEW_HEIGHT = 480;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;
    public static final int WORLD_WIDTH_SCALE = 2;
    public static final int WORLD_HEIGHT_SCALE = 2;

    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    public static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    public static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final String DEFAULT_IMAGE_NAME = "background_default";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;

    public static final String LOAD_FILE_NAME = "world.sav";

    public static final String FAST_FLAG = "-fast";
    public static final String FASTER_FLAG = "-faster";
    public static final String FASTEST_FLAG = "-fastest";
    public static final double FAST_SCALE = 0.5;
    public static final double FASTER_SCALE = 0.25;
    public static final double FASTEST_SCALE = 0.10;

    private static final int SEAL_ACTION_PERIOD = 2000;
    private static final int SEAL_ANIMATION_PERIOD = 100;

    public static double timeScale = 1.0;

    private int shiftX = 0;
    private int shiftY = 0;

    public ImageStore imageStore;
    public WorldModel world;
    public WorldView view;
    public EventScheduler scheduler;

    public long nextTime;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        this.imageStore = new ImageStore(
                createImageColored(TILE_WIDTH, TILE_HEIGHT,
                        DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
                createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH,
                TILE_HEIGHT);
        this.scheduler = new EventScheduler(timeScale);

        loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        loadWorld(world, LOAD_FILE_NAME, imageStore);

        scheduleActions(world, scheduler, imageStore);

        nextTime = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
    }

    public void draw() {
        long time = System.currentTimeMillis();
        if (time >= nextTime) {
            this.scheduler.updateOnTime( time);
            nextTime = time + TIMER_ACTION_PERIOD;
        }

        this.view.drawViewport();
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    shiftY -= 1;
                    break;
                case DOWN:
                    dy = 1;
                    shiftY += 1;
                    break;
                case LEFT:
                    dx = -1;
                    shiftX -= 1;
                    break;
                case RIGHT:
                    dx = 1;
                    shiftX += 1;
                    break;
            }
            this.view.shiftView( dx, dy);
        }
    }

    public void mousePressed()
    {
        ImageStore imst = new ImageStore(Functions.getImageList(imageStore , "tsunami").get(0));
        Background tsunami = new Background("tsunami" , imst.defaultImages);
        Point[] square = new Point[] {new Point((mouseX / TILE_WIDTH ) + view.viewport.col  , (mouseY / TILE_HEIGHT) + 2 + view.viewport.row) , new Point((mouseX / TILE_WIDTH) + view.viewport.col , (mouseY / TILE_HEIGHT) - 2 + view.viewport.row ) , new Point((mouseX / TILE_WIDTH) + 1 + view.viewport.col , (mouseY / TILE_HEIGHT) + 1 + view.viewport.row  ) , new Point((mouseX / TILE_WIDTH) + 1 + view.viewport.col, (mouseY / TILE_HEIGHT) - 1 + view.viewport.row)  , new Point((mouseX / TILE_WIDTH) - 1 + view.viewport.col  , (mouseY / TILE_HEIGHT) + 1 + view.viewport.row) , new Point((mouseX / TILE_WIDTH) - 1 + view.viewport.col , (mouseY / TILE_HEIGHT) - 1 + view.viewport.row ) , new Point((mouseX / TILE_WIDTH) + view.viewport.col , (mouseY / TILE_HEIGHT) + 1 + view.viewport.row)  , new Point((mouseX / TILE_WIDTH) + view.viewport.col  , (mouseY / TILE_HEIGHT) - 1 + view.viewport.row)};
        for (Point p : square)
        {
            if (world.withinBounds(p))
            {
                world.setBackground(p, tsunami);
                world.setOccupancyCell(p, new Obstacle("tsunami", p, null));
            }
        }
        makeNewSeal(new Point((mouseX / TILE_WIDTH) + view.viewport.col  , (mouseY / TILE_HEIGHT) + view.viewport.row ));
    }



    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME,
                Functions.getImageList(imageStore,
                        DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }

    private static void loadImages(
            String filename, ImageStore imageStore, PApplet screen)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in, screen);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void loadWorld(
            WorldModel world, String filename, ImageStore imageStore)
    {
        try {
            Scanner in = new Scanner(new File(filename));
            Functions.load(in, world, imageStore);
        }
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void scheduleActions(
            WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        for (Entity entity : world.entities) {
            if(entity instanceof ActiveEntity) {
                ((ActiveEntity) entity).scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    public static void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG:
                    timeScale = Math.min(FAST_SCALE, timeScale);
                    break;
                case FASTER_FLAG:
                    timeScale = Math.min(FASTER_SCALE, timeScale);
                    break;
                case FASTEST_FLAG:
                    timeScale = Math.min(FASTEST_SCALE, timeScale);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }

    private void makeNewSeal(Point mouse) {

        AnimationEntity soldier = Factory.createSeal("seal",mouse,imageStore.images.get("seal"), SEAL_ACTION_PERIOD, SEAL_ANIMATION_PERIOD);

        world.addEntity(soldier);
        soldier.scheduleActions(scheduler, world, imageStore);
    }
}