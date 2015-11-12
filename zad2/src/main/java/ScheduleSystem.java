import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ScheduleSystem implements SystemInterface {
    private List<Executor> executors = new ArrayList<>();

    public void setNumberOfQueues(int queues) {
        executors = new ArrayList<>(queues);
    }

    public void setThreadsLimit(final int[] maximumThreadsPerQueue) {
        if (maximumThreadsPerQueue.length < executors.size()) {
            throw new RuntimeException("Queues count is greater than maxThreadsPerQueue length. " +
                    "Please provide missing configuration.");
        }
        Arrays.stream(maximumThreadsPerQueue).forEach((size) -> executors.add(Executors.newFixedThreadPool(size)));
    }

    public void addTask(TaskInterface task) {
        if (executors.isEmpty()) {
            throw new RuntimeException("Executors configuration is not provided. Please do correct setup before submitting tasks.");
        }

        executors.get(task.getFirstQueue()).execute(scheduleTask(task, task.getFirstQueue()));
    }

    private Runnable scheduleTask(TaskInterface task, int queueNumber) {
        return () -> {
            TaskInterface nextTask = task.work(queueNumber);
            System.out.println(String.format("Work for task: %d has been started on queue %d.", task.getTaskID(), queueNumber));

            if (task.getLastQueue() != queueNumber) {
                executors.get(queueNumber + 1).execute(scheduleTask(nextTask, queueNumber + 1));
            } else {
                System.out.println(String.format("Computing of task: %d completed on queue: %d.", task.getTaskID(), queueNumber));
            }
        };
    }
}
