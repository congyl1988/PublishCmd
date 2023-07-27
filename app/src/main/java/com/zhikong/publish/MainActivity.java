package com.zhikong.publish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 设定logUtil为 android log
         */
        CmdPublish.logUtil = new com.zhikong.androidlog.LogUtil();

        /**
         * 添加json解析操作
         */
        JsonInterceptPublish.getInstance().setJsonAdapter(new JsonAdapter() {
            private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

            @Override
            public Object fromJson(String json, Class<?> clazz) {
                return gson.fromJson(json, clazz);
            }
        });

        String json = "{\"name\":\"cong\",\"id\":2}";

        /**
         * 注册对象
         */
        JsonPublish.getInstance().register(this);

        /**
         * 发布命令,以json数据的方式
         */
        JsonPublish.getInstance().publish("t", json);
        /**
         * 发布命令，没有任务数据。
         */
        JsonPublish.getInstance().publish("empty");
        /**
         * 解注册对象
         */
        JsonPublish.getInstance().unregister(this);

        /**
         * 串行调用
         */
        JsonPublish.getInstance()
                .register(this)//注册对象，
                .publishJson("t", json) //发布json数据
                .publish("empty")//仅仅发布一个命令，没有任何数据
                .publish("hello", new UserBean())//发布一个对象数据
                .publish("someString", "hello", "cmdPublish")//发布多个字符串命令
                .unregister(this);//解注册对象
    }

    @Cmd(name = "t", threadMode = ThreadMode.PUBLISH)
    public void test(Object t) {
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