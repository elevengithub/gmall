package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/lock")
public class LockTestController {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient client;

    private int i = 0;

    //测试Redis分布式锁
    @GetMapping("/redislock")
    public Result redisLock(){
        //加锁
        String token = lock();
        //从Redis中获取值
        String test = redisTemplate.opsForValue().get("test");
        //操作值
        int num = Integer.parseInt(test);
        num++;
        //操作完成后重新存入
        redisTemplate.opsForValue().set("test",num + "");
        //解锁
        unlock(token);
        return Result.ok(num);
    }

    /**
     * 使用Redis实现分布式锁之加锁
     * @return
     */
    private String lock(){
        String token = UUID.randomUUID().toString();
        //加锁
        while (!redisTemplate.opsForValue().setIfAbsent("lock", token, 10, TimeUnit.SECONDS)){
            //自旋加锁，保证一定加锁成功
        }
        return token;
    }

    /**
     * 使用Redis实现分布式锁之释放锁
     * @param token  加锁时使用的token
     */
    private void unlock(String token){
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1]  then return redis.call('del',KEYS[1]); else  return 0;end;";
        redisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class),
                Arrays.asList("lock"),
                token);
    }

    /**
     * 测试Redisson普通锁的使用
     * @return
     */
    @GetMapping("/common")
    public Result commonLock(){
        RLock lock = client.getLock("lockKey");
        lock.lock();
        System.out.println("加锁成功");
        try {
            System.out.println("执行任务");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("任务执行完毕，释放锁");
        lock.unlock();
        return Result.ok();
    }

    /**
     * 测试Redisson闭锁的使用
     * @return
     */
    @GetMapping("/countdownlatch1")
    public Result countDownLatchTest1(){
        //指定一个闭锁
        RCountDownLatch cdlLock = client.getCountDownLatch("cdl");
        //执行任务，任务完成后数量减一
        cdlLock.countDown();
        return Result.ok("已经完成了一项任务");
    }

    @GetMapping("/countdownlatch2")
    public Result countDownLatchTest2(){
        //指定一个闭锁
        RCountDownLatch cdlLock = client.getCountDownLatch("cdl");
        //指定闭锁需要完成的任务
        cdlLock.trySetCount(3);
        try {
            //阻塞等待所有指定的任务数完成
            cdlLock.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //所有任务完成，执行最终操作
        return Result.ok("所有任务已执行完，执行最后操作。");
    }

    /**
     * 测试读写锁
     * 写锁
     * @return
     */
    @GetMapping("/rwl/write")
    public Result writeLockTest() throws InterruptedException {
        RReadWriteLock rwl = client.getReadWriteLock("rwl");
        RLock rLock = rwl.writeLock();
        rLock.lock();
        Thread.sleep(10000);
        i = 888;
        rLock.unlock();
        return Result.ok();
    }

    /**
     * 测试读写锁
     * 读锁
     */
    @GetMapping("/rwl/read")
    public Result readLockTest(){
        RReadWriteLock rwl = client.getReadWriteLock("rwl");
        RLock rLock = rwl.readLock();
        rLock.lock();
        int x = i;
        rLock.unlock();
        return Result.ok(x);
    }
}