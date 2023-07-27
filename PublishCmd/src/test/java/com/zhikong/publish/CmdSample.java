package com.zhikong.publish;

public class CmdSample {

    @Cmd(name = "test", threadMode = ThreadMode.POSTING)
    public void test(String str) {
        System.out.println("test:" + str + ":" + Thread.currentThread().getName());
    }

    @Cmd(name = "test1", threadMode = ThreadMode.ASYNC)
    public void test1(String str) {
        System.out.println("test1:" + str + ":" + Thread.currentThread().getName());
    }

    @Cmd(name = "user", threadMode = ThreadMode.ASYNC)
    public void user(UserBean str) {
        System.out.println("user:" + str + ":" + Thread.currentThread().getName());
    }
}
