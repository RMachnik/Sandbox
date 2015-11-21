import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


class PathFinder implements PathFinderInterface {
    private volatile boolean exitFound;
    private volatile double shortestDistanceToExit = Double.MAX_VALUE;
    private Runnable observer;
    private ForkJoinPool forkJoinPool;
    private final Object lock = new Object();

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
        synchronized (lock) {
            if (exitFound()) {
                if (observer == null) {
                    throw new RuntimeException("Observer should be initialised.");
                }
                observer.run();
            }
        }
    }

    private Consumer<RoomInterface> findExit(final ForkJoinPool forkJoinPool) {
        return room -> forkJoinPool.execute(new RoomExplorer(room));
    }

    public void registerObserver(Runnable code) {
        observer = code;
    }

    public boolean exitFound() {
        synchronized (lock) {
            return exitFound;
        }
    }

    public double getShortestDistanceToExit() {
        synchronized (lock) {
            return shortestDistanceToExit;
        }
    }

    class RoomExplorer implements Runnable {
        private RoomInterface room;

        public RoomExplorer(RoomInterface roomToExplore) {
            room = roomToExplore;
        }

        public void run() {
            synchronized (lock) {
                if (room.isExit()) {
                    try {
                        if (getShortestDistanceToExit() > room.getDistanceFromStart()) {
                            shortestDistanceToExit = room.getDistanceFromStart();
                        }
                        return;
                    } finally {
                        exitFound = exitFound || room.isExit();
                    }
                }
            }
            if (!exitFound() || (getShortestDistanceToExit() > room.getDistanceFromStart() && room.corridors() != null)) {
                Arrays.stream(room.corridors()).forEach(findExit(forkJoinPool));
            }
        }
    }
}