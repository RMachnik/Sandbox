
public class RoomSample implements RoomInterface {
    private boolean isExit;
    private double distanceFromStart;
    private RoomInterface[] corridors;

    public RoomSample(boolean isExit, double distanceFromStart, RoomInterface... corridors) {
        this.isExit = isExit;
        this.distanceFromStart = distanceFromStart;
        this.corridors = corridors;
    }

    public boolean isExit() {
        return isExit;
    }

    public double getDistanceFromStart() {
        return distanceFromStart;
    }

    public RoomInterface[] corridors() {
        return corridors;
    }
}
