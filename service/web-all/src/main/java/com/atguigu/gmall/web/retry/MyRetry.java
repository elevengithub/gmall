package com.atguigu.gmall.web.retry;

import feign.RetryableException;
import feign.Retryer;

public class MyRetry implements Retryer {

    @Override
    public void continueOrPropagate(RetryableException e) {
        //连接超时直接抛异常
        throw e;
    }

    @Override
    public Retryer clone() {
        return this;
    }
}
