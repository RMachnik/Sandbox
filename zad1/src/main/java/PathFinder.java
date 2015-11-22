import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


class PathFinder implements PathFinderInterface {
    private volatile boolean exitFound;
    private volatile double shortestDistanceToExit = Double.MAX_VALUE;
    private Runnable observer;
    private ForkJoinPool forkJoinPool;
    private final Object exitLock = new Object();
    private final Object distanceLock = new Object();

    public PathFinder() {
    }

    public void setMaxThreads(int i) {
        forkJoinPool = new ForkJoinPool(--i);
    }

    public void entranceToTheLabyrinth(RoomInterface startingRoom) {
        //better than executors due to more effective work-stealing algorithms
        if (forkJoinPool == null) {
            throw new RuntimeException("ForkJoinPool is not initialised. Please invoke first setMaxThreads.");
        }
        try {
            forkJoinPool.execute(new RoomExplorer(startingRoom));
            forkJoinPool.awaitQuiescence(10, TimeUnit.MINUTES);
            forkJoinPool.shutdownNow();
        } catch (RuntimeException r) {
        }
        synchronized (exitLock) {
            if (exitFound()) {
                if (observer == null) {
                    throw new RuntimeException("Observer should be initialised.");
                }
                observer.run();
            }
        }
    }

    private Consumer<RoomInterface> findExit(final ForkJoinPool forkJoinPool, RoomInterface oldRoom) {
        return room -> {
            if (getShortestDistanceToExit() > oldRoom.getDistanceFromStart())
                forkJoinPool.execute(new RoomExplorer(room));
        };
    }

    public void registerObserver(Runnable code) {
        observer = code;
    }

    public boolean exitFound() {
        synchronized (exitLock) {
            return exitFound;
        }
    }

    public double getShortestDistanceToExit() {
        synchronized (distanceLock) {
            return shortestDistanceToExit;
        }
    }

    class RoomExplorer implements Runnable {
        private RoomInterface room;

        public RoomExplorer(RoomInterface roomToExplore) {
            room = roomToExplore;
        }

        public void run() {
            synchronized (exitLock) {
                if (room.isExit()) {
                    try {
                        return;
                    } finally {
                        exitFound = exitFound || room.isExit();
                        if (getShortestDistanceToExit() > room.getDistanceFromStart()) {
                            shortestDistanceToExit = room.getDistanceFromStart();
                        }
                    }
                }
            }
            if (getShortestDistanceToExit() > room.getDistanceFromStart() && room.corridors() != null) {
                Arrays.stream(room.corridors()).forEach(findExit(forkJoinPool, room));
            }
        }
    }
}