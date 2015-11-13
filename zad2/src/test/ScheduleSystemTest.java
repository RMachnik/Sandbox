import org.junit.Test;

/**
 * machnikr on 11/13/2015.
 */
public class ScheduleSystemTest {


    @Test
    public void shouldWork() throws Exception {
        SystemInterface scheduleSystem = new ScheduleSystem();
        scheduleSystem.setNumberOfQueues(5);
        scheduleSystem.setThreadsLimit(new int[]{1, 1, 1, 1, 1});
        scheduleSystem.addTask(new TaskSample(0, 2, 1, true));
        scheduleSystem.addTask(new TaskSample(0, 3, 4, true));
        scheduleSystem.addTask(new TaskSample(0, 3, 2, false));
        scheduleSystem.addTask(new TaskSample(0, 1, 3, true));

        Thread.sleep(100 * 1000);
    }
}
