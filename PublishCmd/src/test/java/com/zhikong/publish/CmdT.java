package com.zhikong.publish;

public class CmdT<T> {

    @Cmd(name = "t", threadMode = ThreadMode.PUBLISH)
    public void test(T t) {
        System.out.println("test----------------------------" + t);
    }

    @Cmd(name = "empty", threadMode = ThreadMode.PUBLISH)
    public void empty() {
        System.out.println("empty----------------------------");
    }

    @Cmd(threadMode = ThreadMode.PUBLISH)
    public void hello(UserBean userBean) {
        System.out.println("hello----------------------------" + userBean);
    }

    @Cmd(threadMode = ThreadMode.PUBLISH)
    public void someString(String s1, String s2) {
        System.out.println("hello----------------------------" + s1 + "," + s2);
    }

    @CmdIntercept(cmdNames = {"t"})
    public void before() {
        System.out.println("before----------------------------");
    }
}
