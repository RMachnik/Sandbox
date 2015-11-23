import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class ScheduleSystem implements SystemInterface {
    private List<BlockingQueue<TaskWrapper>> queueList = new ArrayList<>();
    private List<Executor> executorList = new ArrayList<>();


    @Override
    public void setNumberOfQueues(int queues) {
        for (int i = 0; i < queues; i++) {
            queueList.add(new PriorityBlockingQueue<>());
        }
    }

    @Override
    public void setThreadsLimit(int[] maximumThreadsPerQueue) {
        Arrays.stream(maximumThreadsPerQueue).forEach((threads) -> {
            ExecutorService executorService = Executors.newFixedThreadPool(threads);
            executorList.add(executorService);
            for (int i = 0; i < threads; i++) {
                executorService.execute(new QueueWorker(i));
            }
        });
    }


    @Override
    public void addTask(TaskInterface task) {
        TaskWrapper wrapper = new TaskWrapper(task);
        for (int i = task.getFirstQueue(); i < task.getLastQueue(); i++) {
                queueList.get(i).add(wrapper);
        }
    }

    private class TaskWrapper implements Comparable {
        private Object lock = new Object();
        AtomicInteger completed;
        TaskInterface task;


        public TaskWrapper(TaskInterface task) {
            this.task = task;
            completed = new AtomicInteger(task.getFirstQueue());
        }

        public void work(int queue) {
            synchronized (lock) {
                try {
                    System.out.println(String.format("queue %d working on task: %d", completed.get(), task.getTaskID()));
                    task = task.work(queue);
                } finally {
                    completed.incrementAndGet();
                }
            }
        }

        public boolean canStart() {
            return task.getFirstQueue() == completed.get();
        }

        @Override
        public int compareTo(Object o) {
            TaskWrapper taskWrapper = (TaskWrapper) o;
            if (task.keepOrder() == task.keepOrder()) {
                return Integer.compare(task.getTaskID(), taskWrapper.task.getTaskID());
            }
            if (!task.keepOrder()) {
                return 1;
            } else if (!taskWrapper.task.keepOrder()) {
                return -1;
            }
            return 0;
        }
    }

    private class QueueWorker implements Runnable {

        private final int queueNumber;
        private final BlockingQueue<TaskWrapper> workingQueue;

        public QueueWorker(int queueNumber) {
            this.queueNumber = queueNumber;
            this.workingQueue = queueList.get(queueNumber);
        }

        @Override
        public void run() {
            while (true) {
                if (!workingQueue.isEmpty() && workingQueue.peek().canStart()) {
                    TaskWrapper poll = null;
                    try {
                        poll = workingQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (poll != null)
                            poll.work(queueNumber);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
