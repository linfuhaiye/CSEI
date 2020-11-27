/**
 * Copyright (C) 2003-2020, Foxit Software Inc..
 * All Rights Reserved.
 * <p>
 * http://www.foxitsoftware.com
 * <p>
 * The following code is copyrighted and is the proprietary of Foxit Software Inc.. It is not allowed to
 * distribute any parts of Foxit PDF SDK to third party or public without permission unless an agreement
 * is signed between Foxit Software Inc. and customers to explicitly grant customers permissions.
 * Review legal.txt for additional license and legal information.
 */
package com.foxit.uiextensions.utils.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AppThreadManager {
    private Handler mMainThreadHandler;
    private int             mKeyCount;
    private final Semaphore mKeys;
    private ArrayList<Long> mKeyUsers;
    private HashMap<Long, Integer> mThreadRequestTimes;
    private ArrayList<Long> mWaitingKeyUsers;
    private long            mUiThreadId;
    private ArrayList<Long> mWaitingUITaskThreadIds;

    private static AppThreadManager mThreadManager;
    public static AppThreadManager getInstance() {
        if (mThreadManager == null) {
            mThreadManager = new AppThreadManager();
        }
        return mThreadManager;
    }

    private AppThreadManager() {
        mKeyCount = 2;
        mKeys = new Semaphore(mKeyCount, true);
        mKeyUsers = new ArrayList<>();
        mThreadRequestTimes = new HashMap<>();
        mWaitingKeyUsers = new ArrayList<>();
        mUiThreadId = Thread.currentThread().getId();
        mWaitingUITaskThreadIds = new ArrayList<>();

        try {
            mKeys.acquire();
        } catch (Exception e) {
        }
    }

    public void requestKey() {
        long threadId = Thread.currentThread().getId();
        boolean threadAlreadyGotKey;
        synchronized (mKeys) {
            threadAlreadyGotKey = mKeyUsers.contains(threadId);
        }
        if (!threadAlreadyGotKey) {
            synchronized (mKeys) {
                mWaitingKeyUsers.add(threadId);
            }
            try {
                mKeys.acquire();
            } catch (Exception e) {
            }
            synchronized (mKeys) {
                mKeyUsers.add(threadId);
                mWaitingKeyUsers.remove(threadId);
            }
        } else {
            synchronized (mKeys) {
                if (mThreadRequestTimes.get(threadId) == null) {
                    mThreadRequestTimes.put(threadId, 2);
                } else {
                    int times = mThreadRequestTimes.get(threadId) + 1;
                    mThreadRequestTimes.put(threadId, times);
                }
            }
        }
    }

    public void releaseKey() {
        long threadId = Thread.currentThread().getId();
        int times = 1;
        synchronized (mKeys) {
            Integer requestTimes = mThreadRequestTimes.get(threadId);
            if (requestTimes != null) {
                times = requestTimes;
            }
        }
        if (times == 1) {
            synchronized (mKeys) {
                mKeyUsers.remove(threadId);
                mThreadRequestTimes.put(threadId, null);
            }
            try {
                mKeys.release();
            } catch (Exception e) {

            }
        } else {
            synchronized (mKeys) {
                mThreadRequestTimes.put(threadId, times - 1);
            }
        }
    }

    public boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public Handler getMainThreadHandler() {
        if (mMainThreadHandler == null) {
            mMainThreadHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Runnable runnable = (Runnable) msg.obj;
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            };
        }
        return mMainThreadHandler;
    }

    public void startThread(Runnable runnable) {
        ThreadFactory namedThreadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = Executors.defaultThreadFactory().newThread(r);
                thread.setName("uiextension-task-pool");
                return thread;
            }
        };

        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());

        singleThreadPool.execute(runnable);
        singleThreadPool.shutdown();
    }

    public void startThread(AppAsyncTask task, Object... params) {
        task.execute(params);
    }

    boolean isUIThreadWaitingTaskThrad() {
        synchronized (mKeys) {
            if (!mWaitingKeyUsers.contains(mUiThreadId)) {
                return false;
            }
            return !mKeyUsers.contains(mUiThreadId);
        }
    }

    boolean isDeadLock() {
        synchronized (mKeys) {
            if (isUIThreadWaitingTaskThrad()) {
                long curThreadId = Thread.currentThread().getId();
                return mKeyUsers.contains(curThreadId);
            }
            return false;
        }
    }

    public void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            getMainThreadHandler().post(runnable);
        }
    }

    public int runOnUiThreadAndWait(final Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            if (isDeadLock()) {
                return -1;
            }
            final AppLatch latch = new AppLatch();
            getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                    latch.countDown();
                }
            });
            latch.await();
        }
        return 0;
    }
}
