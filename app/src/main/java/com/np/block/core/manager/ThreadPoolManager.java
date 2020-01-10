package com.np.block.core.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程池管理类
 * @author fengxin
 */
public class ThreadPoolManager {

    /**
     * 不推荐使用Executors创建线程池 但是为了方便使用定时任务 于是用这个方法创建
     *
     * corePoolSize: 该线程池中核心线程的数量。
     * maximumPoolSize：该线程池中最大线程数量。(区别于corePoolSize)
     * keepAliveTime:从字面上就可以理解，是非核心线程空闲时要等待下一个任务到来的时间，当任务很多，每个任务执行时间很短的情况下调大该值有助于提高线程利用率。注意：当allowCoreThreadTimeOut属性设为true时，该属性也可用于核心线程。
     * unit:上面时间属性的单位
     * workQueue:任务队列，后面详述。
     * threadFactory:线程工厂，可用于设置线程名字等等，一般无须设置该参数。
     * 1.execute一个线程之后，如果线程池中的线程数未达到核心线程数，则会立马启用一个核心线程去执行。
     * 2.execute一个线程之后，如果线程池中的线程数已经达到核心线程数，且workQueue未满，则将新线程放入workQueue中等待执行。
     * 3.execute一个线程之后，如果线程池中的线程数已经达到核心线程数但未超过非核心线程数，且workQueue已满，则开启一个非核心线程来执行任务。
     * 4.execute一个线程之后，如果线程池中的线程数已经超过非核心线程数，则拒绝执行该任务，采取饱和策略，并抛出RejectedExecutionException异常。
     * demo中设置的任务队列长度为100，所以不会开启额外的5-3=2个非核心线程，如果将任务队列设为25，则前三个任务被核心线程执行，剩下的30-3=27个任务进入队列会满，此时会开启2个非核心线程来执行剩下的两个任务。
     */
    private static class Inner {
//        private static ThreadPoolExecutor instance =
//                new ThreadPoolExecutor(3,5,1, TimeUnit.SECONDS,
//                new LinkedBlockingQueue<>(100), r -> new Thread(r, "MyThread"));
        private static ScheduledExecutorService instance =
                Executors.newScheduledThreadPool(3, r -> new Thread(r, "MyThread"));
    }

    /**私有化构造方法*/
    private ThreadPoolManager(){
    }

    /**提供一个共有的可以返回类对象的方法*/
    public static ScheduledExecutorService getInstance(){
        return Inner.instance;
    }


}
