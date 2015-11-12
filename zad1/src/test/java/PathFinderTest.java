import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class PathFinderTest {
    private static final double DELTA = 1e-15;


    @Test
    public void shouldWorkBasicCase() throws Exception {
        PathFinder pathFinder = new PathFinder();
        RoomInterface roomInterface = new RoomSample(true, 0);
        pathFinder.setMaxThreads(5);
        pathFinder.registerObserver(getFound(new CountDownLatch(1)));
        pathFinder.entranceToTheLabyrinth(roomInterface);

        assertTrue(pathFinder.exitFound());
        assertEquals(0.0, pathFinder.getShortestDistanceToExit(), DELTA);
    }

    @Test
    public void shouldFindONEBestPath() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        PathFinder pathFinder = new PathFinder();
        RoomInterface c = new RoomSample(true, 6);
        RoomInterface e = new RoomSample(true, 1, c);
        RoomInterface f = new RoomSample(true, 5);
        RoomInterface g = new RoomSample(false, 4, e, f);
        RoomInterface d = new RoomSample(false, 7, g);
        RoomInterface b = new RoomSample(true, 2, c, d);
        RoomInterface a = new RoomSample(false, 0, b);
        RoomInterface entrance = new RoomSample(false, 0, a);


        pathFinder.setMaxThreads(5);
        pathFinder.registerObserver(getFound(countDownLatch));
        pathFinder.entranceToTheLabyrinth(entrance);

        countDownLatch.await();
        assertTrue(pathFinder.exitFound());
        assertEquals(1, pathFinder.getShortestDistanceToExit(), DELTA);

    }

    private Runnable getFound(final CountDownLatch countDownLatch) {
        return new Runnable() {
            public void run() {
                System.out.println("found");
                countDownLatch.countDown();
            }
        };
    }
}