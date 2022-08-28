package com.atguigu.gmall.item;

import org.aspectj.weaver.ast.Var;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AsyncTest {

    private static ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception {
        //使用CompletableFuture异步编排
        /*runAsync(): CompletableFuture<Void> 无返回值
                runAsync(Runnable r)使用默认线程池ForkJoinPool
                runAsync(Runnable r,Executor e)使用自定义线程池
        */
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
//                System.out.println(Thread.currentThread().getName() + " : Hello CompletableFuture!"),
//                executor);
        /*supplyAsync(): CompletableFuture<String> 有返回值
                supplyAsync(Supply s)使用默认线程池ForkJoinPool
                supplyAsync(Supply s,Executor e)使用自定义线程池
         */
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return Thread.currentThread().getName() + " : Hello CompletableFuture!";
        },executor);
        System.out.println(future.get());

        /*
        thenAccept(): 可以使用上一步操作得到的结果。
        thenAccept(Consumer c): 使用当前主线程执行本次任务 CompletableFuture<Void> 无返回值
        thenAcceptAsync(Consumer c): 异步使用默认线程池ForkJoinPool
        thenAcceptAsync(Consumer c,Executor e): 异步使用自定义线程池
         */

//        CompletableFuture<Void> accept = future.thenAcceptAsync(s -> {
//            System.out.println(Thread.currentThread().getName() + "拿到上一步执行结果");
//        },executor);

        /*
        thenRun(): 不使用上一步得到的结果，等上一步执行完再执行本次任务。
        thenRun(Runnable r): CompletableFuture<Void> 无返回值，使用当前主线程执行任务
        thenRunAsync(Runnable r): 异步使用默认线程池ForkJoinPool
        thenRunAsync(Runnable r,Executor e): 异步使用自定义线程池
         */
//        CompletableFuture<Void> thenRun = future.thenRunAsync(() -> {
//            System.out.println(Thread.currentThread().getName() + "执行第二步");
//        },executor);

        /*
        thenApply(): 使用上一步得到的结果，并且本次任务执行完有返回值 CompletableFuture<String>
        thenApply(Function fn): 使用当前主线程执行本次任务
        thenApplyAsync(Function fn): 使用自定义线程池ForkJoin
        thenApplyAsync(Function fn,Executor e): 使用自定义线程池
         */
        CompletableFuture<String> thenApply = future.thenApplyAsync(s -> {
            return Thread.currentThread().getName() + "执行第二步任务";
        },executor);
        System.out.println(thenApply.get());
        Thread.sleep(10000);

    }
}
