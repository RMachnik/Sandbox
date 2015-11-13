import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        Arrays.stream(maximumThreadsPerQueue).forEach((queueSize) -> executors.add(
                new ThreadPoolExecutor(
                        1,
                        queueSize,
                        3,
                        TimeUnit.SECONDS,
                        new PriorityBlockingQueue<>())
                )
        );
    }


    public void addTask(TaskInterface task) {
        if (executors.isEmpty()) {
            throw new RuntimeException("Executors configuration is not provided. Please do correct setup before submitting tasks.");
        }
        executors.get(task.getFirstQueue()).execute(new RunnableTask(task, task.getFirstQueue()));
    }

    private class RunnableTask implements Runnable, Comparable {
        private TaskInterface task;
        private int currentQueueNumber;

        public RunnableTask(TaskInterface task, int queueNumber) {
            this.task = task;
            this.currentQueueNumber = queueNumber;
        }

        @Override
        public void run() {
            TaskInterface nextTask = task.work(currentQueueNumber);
            if (task.getLastQueue() > currentQueueNumber) {
                executors.get(currentQueueNumber + 1).execute(new RunnableTask(nextTask, currentQueueNumber + 1));
            } else {
                System.out.println(String.format("Computing of task: %d completed on queue: %d.", task.getTaskID(), currentQueueNumber));
            }
        }

        public TaskInterface getTask() {
            return task;
        }

        @Override
        public int compareTo(Object o) {
            RunnableTask o2 = (RunnableTask) o;
            if (o2.getTask().keepOrder() == task.keepOrder()) {
                return Integer.compare(task.getTaskID(), o2.getTask().getTaskID());
            }
            if (!task.keepOrder()) {
                return -1;
            }
            if (!o2.getTask().keepOrder()) {
                return 1;
            }
            return 0;
        }
    }
}
