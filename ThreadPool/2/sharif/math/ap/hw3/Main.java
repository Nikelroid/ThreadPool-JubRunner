package ir.sharif.math.ap.hw3;

import java.util.*;

public class Main
{
    public static void main(String[] args) throws InterruptedException {
        HashMap<String,Integer> resources = new HashMap<String,Integer>(){
            {
                put("a", 1);
                put("b" , 2);
                put("ccc" , 2);
            }
        };
        ArrayList<Job> jobs = new ArrayList<Job>() {{
            add(new Job(new NewRunnable() {
                @Override
                public long run() {
                    System.out.println(new Date() + "  -  job0 started");
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(new Date() + "  -  job0 done");
                    return 4000;
                }
            } ,  "ccc","a","b"));
            add(new Job(new NewRunnable() {
                @Override
                public long run() {
                    System.out.println(new Date() + "  -  job1 started");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(new Date() +"  -  job1 done");
                    return 2000;
                }
            } , "a"));
            add(new Job(new NewRunnable() {
                @Override
                public long run() {
                    System.out.println(new Date() + "  -  job2 started");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(new Date() +"  -  job2 done");
                    return 3000;
                }
            } , "b" ,"a"));
            add(new Job(new NewRunnable() {
                @Override
                public long run() {
                    System.out.println(new Date() + "  -  job3 started");
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(new Date() +"  -  job3 done");
                    return 4000;
                }
            } ,  "ccc"));
            add(new Job(new NewRunnable() {
                @Override
                public long run() {
                    System.out.println(new Date() + "  -  job4 started");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(new Date() +"  -  job4 done");
                    return 2000;
                }
            } ,  "ccc"));
        }
        };
        JobRunner jobRunner = new JobRunner(resources,jobs,2);
        Thread.sleep(9000);
       jobRunner.setThreadNumbers(3);


    }
}


 /*
    OUT PUT:
    job0 done
    job3 done
    ** 8 second delay **
    job1 done
    job4 done
    ** 4 second delay **
    job2 done
*/