package com.zhikong.publish;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright [2023] [roe]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @Author roe
 * @Date 2023-7-27 11:13
 * <p>
 * Adapter this situation:
 * {    //base message bean
 * "id":"123",
 * "json":"{    //logic json bean.
 * "name":"ZhiKong"
 * }"
 */
public class JsonInterceptPublish extends JsonPublish {
    private final List<Intercept> intercepts = new ArrayList<>();

    public static JsonInterceptPublish getInstance() {
        if (instance == null) {
            instance = new JsonInterceptPublish();
        }
        return (JsonInterceptPublish) instance;
    }

    /**
     * 两层json的结构，即zhikong采用的协议设计。
     * <p>
     * Publish a cmd by base message object and json.
     *
     * @param cmdName    cmd name
     * @param baseObject base message bean
     * @param json       logic json bean
     */
    public void publish(String cmdName, Object baseObject, String json) {
        CmdMethod cmdMethod = getCmdMethod(cmdName);
        if (cmdMethod == null) {
            w(TAG, "error:cmdMethod==null << " + cmdName);
            return;
        }
        if (json == null) {
            publish(cmdName);
        } else {
            logUtil.d(TAG, "publish " + cmdMethod);
            Class<?> aClass = cmdMethod.aClass[0];
            Object object = jsonAdapter.fromJson(json, aClass);
            intercept(baseObject, object);
            invoke(cmdMethod, object);
        }
    }

    /**
     * 具体业务消息有时候需要调用到基本消息的信息，所以添加了一个设定的机会。
     * When publish a cmd ,It will get an chance to operation baseObject and jsonObject.
     *
     * @param baseObject 基本消息
     * @param jsonObject 业务消息
     */
    protected void intercept(Object baseObject, Object jsonObject) {
        for (Intercept i : intercepts) {
            i.onIntercept(baseObject, jsonObject);
        }
    }

    /**
     * 例子，init消息，具体的业务消息又用到基本消息，所以设定了一个引用。
     * <p>
     * This is a sample to use intercept,When Init bean need the base message bean.
     * The Init bean hold the reference of the base message bean.
     * <p>
     * GsonInterceptPublish.getInstance().addIntercept(new Intercept() {
     *
     * @param intercept
     * @Override public void onIntercept(Object baseObject, Object jsonObject) {
     * if (jsonObject instanceof Init
     * && baseObject instanceof Message) {
     * ((Init) jsonObject).message = (Message) baseObject;
     * }
     * }
     * });
     */
    public void addIntercept(Intercept intercept) {
        intercepts.add(intercept);
    }

    @Override
    protected String tag() {
        return JsonInterceptPublish.class.getSimpleName();
    }
}
