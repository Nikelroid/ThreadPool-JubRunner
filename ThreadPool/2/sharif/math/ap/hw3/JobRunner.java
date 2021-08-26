package ir.sharif.math.ap.hw3;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JobRunner {
    public  List<myJob> myJobs;
    public  Map<String, Integer> myResources;
    public  ThreadPool threadPool;
    public  final Handler handler = new Handler();

    public JobRunner(Map<String, Integer> resources, List<Job> jobs, int initialThreadNumber){
        myResources = new HashMap<>();
        myJobs = new ArrayList<>();


        for (int i = 0; i < jobs.size(); i++) {
            NewRunnable tempRun = jobs.get(i).getRunnable();
            List<String> tempRes = jobs.get(i).getResources();
            myJob tempJob = new myJob(tempRun, tempRes);
            myJobs.add(tempJob);
        }

        myResources = resources.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        threadPool = new ThreadPool(initialThreadNumber);
        handler.start();
    }

    public class myJob {
        private final NewRunnable runnable;
        private final List<String> resources;

        public myJob(NewRunnable runnable, List<String> resources) {
            this.runnable = runnable;
            this.resources = resources;
        }

        public NewRunnable getRunnable() {
            return runnable;
        }

        public List<String> getResources() {
            return resources;
        }
    }


    public synchronized void setThreadNumbers(int threadNumbers) {

        int deadLock=0;

        while (lock1 || lock3 || lock2){
            reserve1=true;
        }
        reserve1=false;
        try {
            lock(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int prime = threadPool.getThreadNumbers();
        threadPool.setThreadNumbers(threadNumbers);



        try {
            release(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (threadNumbers> prime)
            synchronized (handler) {
                handler.notifyAll();
            }
    }


    public  volatile boolean lock1 = false;
    public  volatile int lock2Num = 0;
    public  volatile boolean lock2 = false;
    public  volatile boolean lock3 = false;

    public  volatile boolean reserve1 = false;
    public  volatile boolean reserve2 = false;

    public  void lock(int priority) throws InterruptedException {





        switch (priority) {
            case 1:
                synchronized (object) {
                    while (lock1) {
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    lock1 = true;
                }
                break;
            case 2:
                synchronized (object) {
                    while (lock2) {
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    lock2 = true;
                }
                break;
            case 3:
                synchronized (object) {
                    while (lock3) {
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    lock3 = true;
                }
                break;
        }
    }

    public  final Object object = new Object();
    public  void release(int priority) throws InterruptedException {

        switch (priority) {
            case 1:
                synchronized (object) {
                    object.notifyAll();
                    lock1 = false;
                }
                break;
            case 2:
                synchronized (object) {
                    object.notifyAll();
                    lock2 = false;
                }
                break;
            case 3:
                synchronized (object) {
                    object.notifyAll();
                    lock3 = false;
                }
        }
    }

    public  class Handler extends Thread {

        @Override
        public void run() {

            while (true) {




                synchronized (threads) {
                    while (lock1 || lock2 || lock3 || reserve1 || reserve2) {

                    }


                    try {
                        lock(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (myJobs.size() == 0 || threads.size()==0) return;
                for (int i = 0; i < myJobs.size(); i++) {

                    for (int j = 0; j < threads.size(); j++) {

                        if (isEnoughRes(myJobs.get(i))) {
                            if (threads.get(j).getRunnable() == null) {
                                decreaseRes(myJobs.get(i));

                                threads.get(j).setRunnable(myJobs.get(i).getRunnable());
                                if (myJobs.size() == 0) return;
                                break;
                            }
                        }
                    }
                }
                synchronized (threads) {
                    try {
                        release(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    synchronized (handler) {
                        handler.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        private boolean isEnoughRes(myJob job){
            for (int k = 0; k < job.getResources().size(); k++) {
                for (int l = 0; l < myResources.size(); l++) {
                    if (!myResources.containsKey(job.getResources().get(k)))
                        return false;
                    if(myResources.get(job.getResources().get(k)) ==0)
                        return false;
                }
            }
            return true;
        }

        private synchronized void decreaseRes(myJob job){
            for (int k = 0; k < job.getResources().size(); k++) {
                if (myResources.containsKey(job.getResources().get(k)))
                    myResources.put(job.getResources().get(k),
                            myResources.get(job.getResources().get(k)) -1);
            }

        }

    }

    public  final ArrayList<ThreadPool.Employer> threads = new ArrayList<>();
     class ThreadPool {




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

                    int delNum = threads.size() - threadNumbers;

                    for (int i = 0; i < delNum; i++) {
                        threads.get(i).setFire(true);
                    }
                    for (int i = 0; i < delNum; i++) {
                        threads.remove(0);
                    }

                }
            }

        }





        private class Employer extends Thread {

            private boolean isBusy =false;

            public synchronized boolean isBusy() {
                return isBusy;
            }

            public synchronized void setBusy(boolean busy) {
                isBusy = busy;
            }

            private volatile NewRunnable runnable;

            public synchronized NewRunnable getRunnable() {
                return runnable;
            }

            public synchronized void setRunnable(NewRunnable runnable) {
                this.runnable = runnable;
            }


            private  synchronized void increaseRes(myJob job){
                for (int k = 0; k < job.getResources().size(); k++) {
                    if (myResources.containsKey(job.getResources().get(k)))
                        myResources.put(job.getResources().get(k),
                                myResources.get(job.getResources().get(k)) +1);
                }
            }


            private volatile boolean fire = false;

            public synchronized boolean isFire() {
                return fire;
            }

            public synchronized void setFire(boolean fire) {
                this.fire = fire;
            }

            public void run() {


                while (true) {

                    if (runnable != null) {
                        setBusy(true);

                        long sleepTime = runnable.run();
                        while (lock1 || lock3 || reserve1) {
                            reserve2 = true;
                        }
                        reserve2 = false;

                        synchronized (threads) {
                            try {
                                if (lock2Num == 0) {
                                    lock(2);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            lock2Num++;
                        }
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }






                        for (int j = 0; j < myJobs.size(); j++) {
                            synchronized (threads) {
                                if (myJobs.get(j).getRunnable().equals(getRunnable())) {

                                    increaseRes(myJobs.get(j));

                                    myJobs.remove(j);
                                    setRunnable(null);
                                    if (isFire()) {
                                        this.interrupt();
                                        return;
                                    }
                                    synchronized (threads) {
                                        lock2Num--;
                                        try {
                                            if (lock2Num == 0) {
                                                release(2);
                                                synchronized (handler) {
                                                    handler.notifyAll();
                                                }
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    break;
                                }

                            }
                        }
                    }
                    if (myJobs.size() == 0) {
                        setThreadNumbers(0);
                        return;
                    }
                }

            }

        }
    }
}
