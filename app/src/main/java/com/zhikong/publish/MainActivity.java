package com.zhikong.publish;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CmdPublish.getInstance().register(this);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishJson();
            }
        });
    }

    public void publishJson() {
        /**
         * 添加json解析操作
         */
        CmdPublish.getInstance().setJsonAdapter(new JsonAdapter() {
            private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

            @Override
            public Object fromJson(String json, Class<?> clazz) {
                return gson.fromJson(json, clazz);
            }
        });

        CmdPublish.getInstance().publish("hello",new UserBean(123,"hello"));

        String json = "{\"name\":\"cong\",\"id\":2}";

        /**
         * 发布命令,以json数据的方式
         */
        CmdPublish.getInstance().publishJson("hello", json);
        /**
         * 发布命令，没有任务数据。
         */
        CmdPublish.getInstance().publish("empty");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CmdPublish.getInstance().unregister(this);
    }

    @Cmd(name = "hello", threadMode = ThreadMode.PUBLISH)
    public void xxx(UserBean t) {
        CmdPublish.logUtil.w("cong", "test----------------------------" + t);
    }

    @Cmd(name = "empty", threadMode = ThreadMode.PUBLISH)
    public void empty() {
        CmdPublish.logUtil.w("cong", "empty----------------------------");
    }

    @CmdIntercept(cmdNames = {"hello","empty"})
    public void intercept() {
        CmdPublish.logUtil.w("cong", "before----------------------------");
    }
}