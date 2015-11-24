import org.junit.Test;

public class ScheduleSystemTest {


    @Test
    public void shouldWork() throws Exception {
        SystemInterface scheduleSystem = new ScheduleSystem();
        scheduleSystem.setNumberOfQueues(5);
        scheduleSystem.setThreadsLimit(new int[]{2, 2, 2, 2});
        scheduleSystem.addTask(new TaskSample(0, 4, 1, true));
        scheduleSystem.addTask(new TaskSample(0, 4, 2, true));
        scheduleSystem.addTask(new TaskSample(0, 4, 3, true));
        scheduleSystem.addTask(new TaskSample(1, 4, 4, true));


        Thread.sleep(100 * 1000);
    }
}
