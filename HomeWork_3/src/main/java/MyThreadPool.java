import java.util.LinkedList;
import java.util.stream.IntStream;

public class MyThreadPool {

    private final LinkedList<Runnable> tasksQueue;
    private final WorkerThread[] threads;
    private final Object monitor = new Object();;
    private volatile boolean isRunning = true;

    public MyThreadPool(int capacity) {
        tasksQueue = new LinkedList<>();
        threads = new WorkerThread[capacity];

        IntStream.range(0, capacity).forEach(i -> {
            threads[i] = new WorkerThread();
            threads[i].start();
        });
    }

    public static void main(String[] args) {
        MyThreadPool pool = new MyThreadPool(5);

        IntStream.range(0, 10).<Runnable>mapToObj(taskNumber -> () -> {
            System.out.println("Task " + taskNumber + " is running");
            System.out.println("Task " + taskNumber + " is completed");
        }).forEach(pool::execute);

        pool.shutdown();
        try {
            /*pool.execute(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.fillInStackTrace();
                }
            });*/
            pool.awaitTermination();
        } catch (InterruptedException e) {
            e.fillInStackTrace();
        }
        System.out.println("All tasks are completed.");
    }

    public void execute(Runnable task) {
        if (!isRunning) {
            throw new IllegalStateException("My ThreadPool is shutdown. No new tasks can be added.");
        }
        synchronized (monitor) {
            tasksQueue.add(task);
            monitor.notify();
        }
    }

    public void shutdown() {
        isRunning = false;
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    public void awaitTermination() throws InterruptedException {
        for (WorkerThread thread : threads) {
            thread.join();
        }
    }

    private class WorkerThread extends Thread {
        @Override
        public void run() {
            while (isRunning || !tasksQueue.isEmpty()) {
                Runnable task;
                synchronized (monitor) {
                    while (tasksQueue.isEmpty() && isRunning) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    if (!isRunning && tasksQueue.isEmpty()) {
                        break;
                    }
                    task = tasksQueue.poll();
                }
                if (task != null) {
                    task.run();
                }
            }
        }
    }
}
