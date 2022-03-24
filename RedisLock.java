package com.jd.mlaas.ump.api.domain.common.util;

import com.jd.legion.redislike.RedisLike;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

public class RedisLock {

    private static final int DEFAULT_ACQUIRY_RESOLUTION_MILLIS = 100;
    private RedisLike redisComponent;
    private String lockKey;

    /**
     * 锁超时时间，防止线程在入锁以后，无限的执行等待
     */
    private long expireMillisCond = 60 * 1000;

    /**
     * 锁等待时间，防止线程饥饿
     */
    private int timeoutMillisCond = 10 * 1000;

    private volatile boolean isLocked = false;

    public RedisLock(RedisLike redisComponent, String lockKey) {
        this.redisComponent = redisComponent;
        this.lockKey = lockKey;
    }

    public RedisLock(RedisLike redisComponent, String lockKey, int timeoutMillisCond) {
        this(redisComponent, lockKey);
        this.timeoutMillisCond = timeoutMillisCond;
    }

    public RedisLock(RedisLike redisComponent, String lockKey, int timeoutMillisCond, long expireMillisCond) {
        this(redisComponent, lockKey, timeoutMillisCond);
        this.expireMillisCond = expireMillisCond;
    }

    public RedisLock(RedisLike redisComponent, int expireMillisCond, String lockKey) {
        this(redisComponent, lockKey);
        this.expireMillisCond = expireMillisCond;
    }

    public String getLockKey() {
        return lockKey;
    }

    public synchronized boolean lock() throws InterruptedException {
        int timeout = timeoutMillisCond;

        boolean flag;

        while (timeout >= 0) {
            //设置所得到期时间
            Long expires = System.currentTimeMillis() + expireMillisCond;
            String expiresStr = String.valueOf(expires);

            //原来redis里面没有锁，获取锁成功
            if (this.redisComponent.set(lockKey, expiresStr, expireMillisCond, TimeUnit.MILLISECONDS, false)) {
                isLocked = true;
                return true;
            }

            flag = compareLock(expiresStr);

            if (flag) {
                return true;
            }

            timeout -= DEFAULT_ACQUIRY_RESOLUTION_MILLIS;

            /*
                延迟100 毫秒,  这里使用随机时间可能会好一点,可以防止饥饿进程的出现,即,当同时到达多个进程,
                只会有一个进程获得锁,其他的都用同样的频率进行尝试,后面有来了一些进程,也以同样的频率申请锁,这将可能导致前面来的锁得不到满足.
                使用随机的等待时间可以一定程度上保证公平性
             */
            if (timeout >= 0){
                Thread.sleep(DEFAULT_ACQUIRY_RESOLUTION_MILLIS);
            }
        }
        return false;
    }

    /**
     * 排他锁。作用相当于 synchronized 同步快
     *
     * @return
     */
    public synchronized boolean excludeLock() {

        //设置所得到期时间
        Long expires = System.currentTimeMillis() + expireMillisCond;
        String expiresStr = String.valueOf(expires);

        //原来redis里面没有锁，获取锁成功
        if (this.redisComponent.set(lockKey, expiresStr, expireMillisCond, TimeUnit.MILLISECONDS, false)) {
            isLocked = true;
            return true;
        }

        return compareLock(expiresStr);
    }

    /**
     * 比较是否可以获取锁
     * 锁超时时 获取
     *
     * @param expiresStr
     * @return
     */
    private boolean compareLock(String expiresStr) {
        String currentValueStr = this.redisComponent.get(lockKey);

        if (StringUtils.isNotEmpty(currentValueStr) && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {

            String oldValue = this.redisComponent.getSet(lockKey, expiresStr);
            this.redisComponent.expire(lockKey, expireMillisCond, TimeUnit.MILLISECONDS);
            if (StringUtils.isNotEmpty(oldValue) && StringUtils.equals(oldValue, currentValueStr)) {
                isLocked = true;
                return true;
            }
        }
        return false;
    }

    /**
     * 释放锁
     */
    public synchronized void unlock() {
        if (isLocked) {
            this.redisComponent.del(lockKey);
            isLocked = false;
        }
    }
}