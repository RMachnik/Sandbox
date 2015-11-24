import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
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
        final AtomicInteger qu = new AtomicInteger(0);
        Arrays.stream(maximumThreadsPerQueue).forEach((threads) -> {
            ExecutorService executorService = Executors.newFixedThreadPool(threads);
            executorList.add(executorService);
            for (int i = 0; i < threads; i++) {
                executorService.execute(new QueueWorker(qu.get()));
            }
            qu.getAndIncrement();
        });
    }


    @Override
    public void addTask(TaskInterface task) {
        TaskWrapper wrapper = new TaskWrapper(task);
        for (int i = task.getFirstQueue(); i < task.getLastQueue(); i++) {
            try {
                queueList.get(i).put(wrapper);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class TaskWrapper implements Comparable {
        private Object lock = new Object();
        private TaskInterface task;


        public TaskWrapper(TaskInterface task) {
            this.task = task;
        }

        public void work(int queue) {
            synchronized (lock) {
                task = task.work(queue);
            }
        }

        public boolean canStart(int queue) {
            synchronized (lock) {
                return task.getFirstQueue() == queue;
            }
        }

        @Override
        public int compareTo(Object o) {
            TaskWrapper taskWrapper = (TaskWrapper) o;
            if (task.keepOrder() == task.keepOrder()) {
                return Integer.compare(task.getTaskID(), taskWrapper.task.getTaskID());
            }
            if (!task.keepOrder()) {
                return -1;
            } else if (!taskWrapper.task.keepOrder()) {
                return 1;
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
                TaskWrapper peek = workingQueue.peek();
                if (peek != null)
                    synchronized (peek) {
                        if (!workingQueue.isEmpty() && peek.canStart(queueNumber)) {
                            TaskWrapper poll = null;
                            try {
                                poll = workingQueue.poll(100, TimeUnit.MILLISECONDS);
                                if (poll != null) {
                                    poll.work(queueNumber);
                                }
                            } catch (InterruptedException e) {
                            }
                        }
                    }
            }
        }
    }
}
