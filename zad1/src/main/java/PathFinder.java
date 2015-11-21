import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


class PathFinder implements PathFinderInterface {
    private volatile boolean exitFound;
    private volatile double shortestDistanceToExit = Double.MAX_VALUE;
    private Runnable observer;
    private ForkJoinPool forkJoinPool;

    public PathFinder() {
    }

    public void setMaxThreads(int i) {
        forkJoinPool = new ForkJoinPool(--i);
    }

    public void entranceToTheLabyrinth(RoomInterface mi) {
        if (mi.isExit()) {
            exitFound = true;
            shortestDistanceToExit = mi.getDistanceFromStart();
        } else {
            //better than executors due to more effective work-stealing algorithms
            if (forkJoinPool == null) {
                throw new RuntimeException("ForkJoinPool is not initialised. Please invoke first setMaxThreads.");
            }
            if (mi.corridors() != null)
                Arrays.stream(mi.corridors()).forEach(findExit(forkJoinPool));
            forkJoinPool.awaitQuiescence(10, TimeUnit.MINUTES);
        }
        if (exitFound) {
            if (observer == null) {
                throw new RuntimeException("Observer should be initialised.");
            }
            observer.run();
        }
    }

    private Consumer<RoomInterface> findExit(final ForkJoinPool forkJoinPool) {
        return room -> forkJoinPool.execute(new RoomExplorer(room));
    }

    public void registerObserver(Runnable code) {
        observer = code;
    }

    public boolean exitFound() {
        return exitFound;
    }

    public double getShortestDistanceToExit() {
        return shortestDistanceToExit;
    }

    class RoomExplorer implements Runnable {
        private RoomInterface room;

        public RoomExplorer(RoomInterface roomToExplore) {
            room = roomToExplore;
        }

        public void run() {
            if (room.isExit()) {
                exitFound = true;
                if (shortestDistanceToExit > room.getDistanceFromStart()) {
                    shortestDistanceToExit = room.getDistanceFromStart();
                }
                return;
            } else {
                if (shortestDistanceToExit > room.getDistanceFromStart() && room.corridors() != null) {
                    shortestDistanceToExit = room.getDistanceFromStart();
                    Arrays.stream(room.corridors()).forEach(findExit(forkJoinPool));
                }
            }

        }
    }
}