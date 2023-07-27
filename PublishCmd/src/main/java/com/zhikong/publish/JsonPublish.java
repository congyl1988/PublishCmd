package com.zhikong.publish;

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
 */
public class JsonPublish extends CmdPublish {
    protected JsonAdapter jsonAdapter;

    public static JsonPublish getInstance() {
        if (instance == null) {
            instance = new JsonPublish();
        }
        return (JsonPublish) instance;
    }

    public JsonPublish setJsonAdapter(JsonAdapter jsonAdapter) {
        this.jsonAdapter = jsonAdapter;
        return this;
    }

    @Override
    public JsonPublish register(Object object) {
        return (JsonPublish) super.register(object);
    }

    /**
     * 发布并解析json数据到Cmd注解方法，json即是Cmd注解方法的解析类。
     * <p>
     * Publish a cmd by json. The json will be parse to be bean, which is parameter of cmd method.
     *
     * @param cmdName cmd name
     * @param json    json String
     */
    public JsonPublish publishJson(String cmdName, String json) {
        CmdMethod cmdMethod = getCmdMethod(cmdName);
        if (cmdMethod == null) {
            w(TAG, "error:cmdMethod==null << " + cmdName);
            return this;
        }
        if (json == null) {
            publish(cmdName);
        } else {
            Class<?> aClass = cmdMethod.aClass[0];
            Object object = jsonAdapter.fromJson(json, aClass);
            invoke(cmdMethod, object);
        }
        return this;
    }

    @Override
    protected String tag() {
        return JsonPublish.class.getSimpleName();
    }
}
