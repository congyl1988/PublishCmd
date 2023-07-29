package com.zhikong.publish;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhikong.publish.log.PrintLogUtil;

import org.junit.Test;

public class XCmdTest {

    @Test
    public void test() {

        String json = "{\"name\":\"cong\",\"id\":2}";

        CmdT<UserBean> cmdT = new CmdT<>();

        JsonInterceptPublish.logUtil = new PrintLogUtil();

        /**
         *
         */
        CmdPublish.getInstance().register(cmdT)
                .publish("empty")
                .unregister(cmdT)
                .destroy();

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

        /**
         * 注册对象
         */
        JsonPublish.getInstance().register(cmdT);

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
        JsonPublish.getInstance().unregister(cmdT);

        /**
         * 串行调用
         */
        JsonPublish.getInstance()
                .register(cmdT)//注册对象，
                .publishJson("t", json) //发布json数据
                .publish("empty")//仅仅发布一个命令，没有任何数据
                .publish("hello", new UserBean())//发布一个对象数据
                .publish("someString", "hello", "cmdPublish")//发布多个字符串命令
                .unregister(cmdT);//解注册对象

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}