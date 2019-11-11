package com.vc.onlinepay.utils.alibaba;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @描述: 线程定义
 * @作者:nada
 * @时间:2018/12/21
 **/
public class NamedThreadFactory implements ThreadFactory {

    private static final Logger logger = LoggerFactory.getLogger (NamedThreadFactory.class);
    /**
     * @描述: 默认线程名称
     * @作者:nada
     * @时间:2018/12/21
     **/
    final private static String DEFAULT_NAME = "online-worker";
    /**
     * @描述: 线程名称
     * @作者:nada
     * @时间:2018/12/21
     **/
    final private String name;
    /**
     * @描述: 线程守护进程
     * @作者:nada
     * @时间:2018/12/21
     **/
    final private boolean daemon;
    /**
     * @描述: 线程组
     * @作者:nada
     * @时间:2018/12/21
     **/
    final private ThreadGroup group;
    /**
     * @描述: 线程数
     * @作者:nada
     * @时间:2018/12/21
     **/
    final private AtomicInteger threadNumber = new AtomicInteger (0);
    final static UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler () {
        @Override
        public void uncaughtException (Thread t, Throwable e) {
            if (e instanceof InterruptedException || (e.getCause () != null && e.getCause () instanceof InterruptedException)) {
                return;
            }
            logger.error ("from " + t.getName (), e);
        }
    };

    /**
     * @描述: 创建默认带名字的线程池
     * @作者:nada
     * @时间:2018/12/21
     **/
    public NamedThreadFactory () {
        this (DEFAULT_NAME, true);
    }

    /**
     * @描述: 创建带名字的线程池
     * @作者:nada
     * @时间:2018/12/21
     **/
    public NamedThreadFactory (String name) {
        this (name, true);
    }

    /**
     * @描述: 创建带名字的线程池
     * @作者:nada
     * @时间:2018/12/21
     **/
    public NamedThreadFactory (String name, boolean daemon) {
        this.name = name;
        this.daemon = daemon;
        SecurityManager s = System.getSecurityManager ();
        group = (s != null) ? s.getThreadGroup () : Thread.currentThread ().getThreadGroup ();
    }

    /**
     * @描述: 覆盖创建线程
     * @作者:nada
     * @时间:2018/12/21
     **/
    @Override
    public Thread newThread (Runnable r) {
        Thread t = new Thread (group, r, name + "-" + threadNumber.getAndIncrement (), 0);
        t.setDaemon (daemon);
        if (t.getPriority () != Thread.NORM_PRIORITY) {
            t.setPriority (Thread.NORM_PRIORITY);
        }
        t.setUncaughtExceptionHandler (uncaughtExceptionHandler);
        return t;
    }

}
