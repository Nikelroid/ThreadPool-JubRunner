package ir.sharif.math.ap.hw3;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class Main
{
    public static void main(String[] args) throws InterruptedException {

        // Creating the ThreadPool
        ThreadPool pool = new ThreadPool(5);

        // invokeLater
        for (int i = 0; i < 1000; i = i + 5)
        {
            Task task = new Task(i);
            pool.invokeLater(task);
        }

        // setThreadNumbers (increase)
        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        pool.setThreadNumbers(6);
        System.out.println("Thread numbers: " + pool.getThreadNumbers());

        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        pool.setThreadNumbers(7);
        System.out.println("Thread numbers: " + pool.getThreadNumbers());

        // setThreadNumbers (decrease)
        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        pool.setThreadNumbers(3);
        System.out.println("Thread numbers: " + pool.getThreadNumbers());

        try
        {
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        pool.setThreadNumbers(2);
        System.out.println("Thread numbers: " + pool.getThreadNumbers());

        // invokeAndWaitUninterruptible
        for (int i = 0; i < 999; i = i + 3)
        {

            Task task1 = new Task(i);
            Task task2 = new Task(i + 1);
            Task task3 = new Task(i + 2);

            Thread thread1 = new Thread()
            {
                public void run()
                {
                    try
                    {
                        pool.invokeAndWaitUninterruptible(task1);
                    }
                    catch (InvocationTargetException invocationTargetException)
                    {
                        invocationTargetException.printStackTrace();
                    }
                }
            };

            Thread thread2 = new Thread()
            {
                public void run()
                {
                    try
                    {
                        pool.invokeAndWaitUninterruptible(task2);
                    }
                    catch (InvocationTargetException invocationTargetException)
                    {
                        invocationTargetException.printStackTrace();
                    }
                }
            };

            Thread thread3 = new Thread()
            {
                public void run()
                {
                    try
                    {
                        pool.invokeAndWaitUninterruptible(task3);
                    }
                    catch (InvocationTargetException invocationTargetException)
                    {
                        invocationTargetException.printStackTrace();
                    }
                }
            };

            thread1.start();
            thread2.start();
            thread3.start();

            thread1.join();
            thread2.join();
            thread3.join();

            thread1 = null;
            thread2 = null;
            thread3 = null;
        }

        // Exit
        pool.setThreadNumbers(0);
    }

    static class Task implements Runnable
    {

        private final int num;

        public Task(int n)
        {
            num = n;
        }

        public void run()
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println(new Date() + "- job " + num + " - " + Thread.currentThread().getName());
        }
    }
}
