package ir.sharif.math.ap.hw3;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ThreadPool {

    public final ArrayList<Employer> threads= new ArrayList<>();
    public final ArrayList<Quest> quest= new ArrayList<>();;
    public static final Queue<Runnable> queue = new LinkedList<>();


    public ThreadPool(int threadNumbers) {
        synchronized (threads) {
            for (int i = 0; i < threadNumbers; i++) {
                threads.add(new Employer());
                threads.get(i).start();
            }
        }
    }


    public int getThreadNumbers() {
        synchronized (threads) {
            return threads.size();
        }
    }

    public void setThreadNumbers(int threadNumbers) {

        synchronized (threads) {
            if (threadNumbers > threads.size()) {

                for (int i = threads.size(); i < threadNumbers; i++) {

                    threads.add(new Employer());
                    threads.get(i).start();
                }


            } else if (threadNumbers < threads.size()) {

                int delNum= threads.size()-threadNumbers;

                for (int i = 0; i < delNum; i++) {
                    threads.get(i).setFire(true);
                }
                for (int i = 0; i < delNum; i++) {
                    threads.remove(0);
                }
                synchronized (queue) {
                    queue.notifyAll();
                }

            }
        }

    }



    public void invokeLater(Runnable runnable) {
        int questNum =0;
        synchronized (queue) {
            queue.add(runnable);
            queue.notifyAll();
        }
        synchronized (quest) {
            quest.add(new Quest(runnable));
            questNum = quest.size()-1;
        }

    }

    public void invokeAndWait(Runnable runnable) throws InterruptedException, InvocationTargetException {
        int questNum =0;
        synchronized (queue) {
            queue.add(runnable);
            queue.notifyAll();
        }
        synchronized (quest) {
            quest.add(new Quest(runnable));
            questNum = quest.size()-1;
        }
        try {
            synchronized (quest.get(questNum)) {
                quest.get(questNum).wait();
                if (quest.get(questNum).getException() != null) {
                    throw new InvocationTargetException(quest.get(questNum).getException());
                }
            }
        } catch (InterruptedException e) {
            throw new InterruptedException(e.getMessage());
        }


    }

    public void invokeAndWaitUninterruptible(Runnable runnable) throws InvocationTargetException {

        int questNum = 0;
        synchronized (queue) {
            queue.add(runnable);
            queue.notifyAll();
        }
        synchronized (quest) {
            quest.add(new Quest(runnable));
            questNum = quest.size() - 1;
        }
        synchronized (quest.get(questNum)) {
            while (!quest.get(questNum).isIsDone()) {

                try {
                    quest.get(questNum).wait();
                } catch (InterruptedException ignored) {
                }
            }
            if (quest.get(questNum).getException() != null) {
                throw new InvocationTargetException(quest.get(questNum).getException());
            }
        }

    }

    private class Employer extends Thread {

        private volatile boolean fire = false;

        public synchronized boolean isFire() {
            return fire;
        }

        public synchronized void setFire(boolean fire) {
            this.fire = fire;
        }

        public void run() {

            Runnable task ;

            while (true) {

                synchronized (queue) {

                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (Throwable e) {

                            synchronized (threads) {
                                threads.notifyAll();
                            }
                            synchronized (queue) {
                                queue.notifyAll();
                            }
                        }

                        if (isFire()) {
                            this.interrupt();
                            return;
                        }

                    }


                    task = queue.poll();

                }

                try {
                    if (task != null) {
                        task.run();


                        for (int i = 0; i < quest.size(); i++) {
                            synchronized (quest.get(i)){
                                if(quest.get(i).getRunnable()==task) {
                                    quest.get(i).setIsDone(true);
                                    quest.get(i).notifyAll();
                                }
                            }
                        }


                        if (isFire()) {
                            this.interrupt();
                            return;
                        }


                    }
                } catch (Throwable e) {


                    for (int i = 0; i < quest.size(); i++) {
                        synchronized (quest.get(i)){
                            if(quest.get(i).getRunnable()==task) {
                                quest.get(i).setIsDone(true);
                                quest.get(i).notifyAll();
                                quest.get(i).setException(e);
                            }
                        }
                    }


                }

                synchronized (threads) {
                    threads.notifyAll();
                }

                synchronized (queue) {
                    queue.notifyAll();
                }
            }

        }

    }
    public static class Quest {

        private volatile Runnable runnable;
        private volatile Throwable exception;
        private volatile boolean isDone;

        public Quest(Runnable runnable) {
            this.runnable=runnable;
            this.isDone=false;
        }

        public synchronized Runnable getRunnable() {
            return runnable;
        }

        public synchronized void setRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

        public synchronized Throwable getException() {
            return exception;
        }

        public synchronized void setException(Throwable exception) {
            this.exception = exception;
        }

        public synchronized boolean isIsDone() {
            return isDone;
        }

        public synchronized void setIsDone(boolean isDone) {
            this.isDone = isDone;
        }
    }
}