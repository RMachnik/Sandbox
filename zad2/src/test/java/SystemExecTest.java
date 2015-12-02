import org.junit.Test;

public class SystemExecTest {


    @Test
    public void shouldWork() throws Exception {
        SystemInterface scheduleSystem = new SystemExec();
        scheduleSystem.setNumberOfQueues(5);
        scheduleSystem.setThreadsLimit(new int[]{3, 3, 3, 3});
        scheduleSystem.addTask(new TaskSample(0, 4, 1, true));
        scheduleSystem.addTask(new TaskSample(0, 4, 2, false));
        scheduleSystem.addTask(new TaskSample(0, 4, 3, true));
        scheduleSystem.addTask(new TaskSample(1, 4, 4, true));


        Thread.sleep(100 * 1000);
    }
}
