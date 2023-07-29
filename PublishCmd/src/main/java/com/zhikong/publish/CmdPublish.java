package com.zhikong.publish;

import com.zhikong.publish.log.LogBase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
public class CmdPublish extends LogBase {
    protected final String TAG = tag();
    protected static CmdPublish instance;
    protected Map<String, CmdMethod> cmdMethodMap = new HashMap<>();
    private final Executor executorSingle = Executors.newSingleThreadExecutor();
    private final Executor executorIO = Executors.newCachedThreadPool();
    private HandlerWrapper handlerWrapper = new HandlerWrapper();

    protected JsonAdapter jsonAdapter;
    private final List<Intercept> intercepts = new ArrayList<>();

    protected CmdPublish() {
    }

    public static CmdPublish getInstance() {
        if (instance == null) {
            instance = new CmdPublish();
        }
        return instance;
    }

    public CmdPublish setJsonAdapter(JsonAdapter jsonAdapter) {
        this.jsonAdapter = jsonAdapter;
        return this;
    }

    /**
     * 注册对象，使注解方法能得到调用。
     * Register the object that contains Cmd annotation method.
     *
     * @param object cmd object
     */
    public CmdPublish register(Object object) {
        logUtil.d(TAG, "register:" + object);
        Method[] methods = object.getClass().getMethods();
        for (Method m : methods) {
            cacheCmdAnnotation(m, object);
        }
        for (Method m : methods) {
            cacheCmdInterceptAnnotation(m, object);
        }
        return this;
    }

    /**
     * 解除注册对象。
     * Unregister the object that contains Cmd annotation method.
     *
     * @param object cmd object
     */
    public CmdPublish unregister(Object object) {
        logUtil.d(TAG, "unregister:" + object);
        Iterator<Map.Entry<String, CmdMethod>> it = cmdMethodMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CmdMethod> next = it.next();
            if (next.getValue().object == object) {
                it.remove();
            }
        }
        return this;
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
     * 发布并解析json数据到Cmd注解方法，json即是Cmd注解方法的解析类。
     * <p>
     * Publish a cmd by json. The json will be parse to be bean, which is parameter of cmd method.
     *
     * @param cmdName cmd name
     * @param json    json String
     */
    public CmdPublish publishJson(String cmdName, String json) {
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

    /**
     * 销毁单例。
     * Destroy the single instance.
     */
    public void destroy() {
        instance = null;
    }

    /**
     * 缓存注解相关信息。
     * Cache all info about Cmd annotation method.
     *
     * @param method cmd annotation method
     * @param object cmd object
     */
    private void cacheCmdAnnotation(Method method, Object object) {
        CmdAnnotation ca = CmdAnnotation.getCmdName(method);
        if (ca == null) {
            return;
        }
        String methodName = ca.name;
        CmdMethod cmdMethod = cmdMethodMap.get(Objects.requireNonNull(methodName));
        if (cmdMethod == null) {
            cmdMethodMap.put(methodName, cmdMethod = new CmdMethod(methodName, object, method, ca.threadMode));
            logUtil.d(TAG, "cacheCmdAnnotation:" + cmdMethod);
        } else {
            throw new RuntimeException("Cmd name has registered,You can only register once << " + methodName + "," + object);
        }
        cmdMethod.aClass = getParameterTypes(method);
    }

    /**
     * 缓存CmdIntercept注解方法。
     * Cache all info about CmdIntercept annotation method.
     *
     * @param method cmd annotation method
     * @param object cmd object
     */
    private void cacheCmdInterceptAnnotation(Method method, Object object) {
        //找CmdIntercept注解。
        CmdInterceptAnnotation ca = CmdInterceptAnnotation.getCmdInterceptName(method);
        if (ca == null) {
            return;
        }
        String[] anName = ca.name;
        if (anName == null || anName.length == 0) {
            throw new RuntimeException("CmdIntercept annotation name[] can not be null << " + method + "," + object);
        }
        CmdInterceptMethod interceptMethod = new CmdInterceptMethod(method.getName(), object, method, ca.threadMode);
        interceptMethod.aClass = getParameterTypes(method);
        if (interceptMethod.aClass.length > 0) {
            throw new RuntimeException("CmdIntercept method should not has parameter << " + method + "," + object);
        }
        for (String name : anName) {
            //找cmd缓存数据。
            CmdMethod cmdMethod = cmdMethodMap.get(Objects.requireNonNull(name));
            if (cmdMethod == null) {
                throw new RuntimeException("CmdIntercept annotation name should be the name of Cmd annotation or method << " + name + ",object:" + object);
            }
            interceptMethod.cmdNames.add(cmdMethod.name);
            //将cmd与intercept进行关联。
            cmdMethod.cmdInterceptMethods.add(interceptMethod);
        }
    }

    /**
     * 获取Cmd注解方法的参数类型。
     * Get parameter types of method.
     *
     * @param method method
     * @return class[]
     */
    private Class<?>[] getParameterTypes(Method method) {
        return method.getParameterTypes();
    }

    private void invokeIntercept(CmdMethod cmdMethod) {
        for (CmdInterceptMethod cim : cmdMethod.cmdInterceptMethods) {
            Runnable runnable = () -> cim.invoke();
            switch (cmdMethod.threadMode) {
                case ASYNC:
                    executorIO.execute(runnable);
                    return;
                case PUBLISH:
                    runnable.run();
                    return;
                case MAIN:
                    handlerWrapper.post(runnable);
                    return;
                case SINGLE:
                    executorSingle.execute(runnable);
                    return;
                default:
                    break;
            }
        }
    }

    /**
     * 实际调用Cmd注解方法。
     * <p>
     * Intercept and invoke cmd annotation method by reflect.
     *
     * @param cmdMethod cmd method object
     * @param parameter all parameter from Publish
     */
    protected void invoke(CmdMethod cmdMethod, Object... parameter) {
        invokeIntercept(cmdMethod);
        invoke0(cmdMethod, parameter);
    }

    /**
     * 调用Cmd注解方法以各种线程。
     * <p>
     * Invoke cmd annotation method in all kinds of thread.
     *
     * @param cmdMethod cmd method object
     * @param parameter all parameter from Publish
     */
    private void invoke0(CmdMethod cmdMethod, Object... parameter) {
        Runnable runnable = () -> cmdMethod.invoke(parameter);
        switch (cmdMethod.threadMode) {
            case ASYNC:
                executorIO.execute(runnable);
                return;
            case PUBLISH:
                runnable.run();
                return;
            case MAIN:
                handlerWrapper.post(runnable);
                return;
            case SINGLE:
                executorSingle.execute(runnable);
                return;
            default:
                break;
        }
    }

    /**
     * 发布调用命令。
     * <p>
     * Publish cmd by name.
     *
     * @param cmdName 命令的名字 cmd name
     * @param object  Cmd注解方法对应的参数 Corresponding to the cmd annotation method parameters.
     */
    public CmdPublish publish(String cmdName, Object... object) {
        w(TAG, "publish cmdName:" + cmdName + ",parameters:" + Arrays.toString(object) + ",thread:" + Thread.currentThread().getName());
        CmdMethod cmdMethod = getCmdMethod(cmdName);
        if (cmdMethod == null) {
            w(TAG, "error:cmdMethod==null << " + cmdName);
            return this;
        }
        invoke(cmdMethod, object);
        return this;
    }

    /**
     * 获取Cmd注解的方法封装。
     * Get cmd annotation method object by name.
     *
     * @param name 命令名字
     * @return Cmd注解的方法封装
     */
    protected CmdMethod getCmdMethod(String name) {
        return cmdMethodMap.get(Objects.requireNonNull(name));
    }

    /**
     * LogUtil的tag。
     * Tag of LogUtil.
     *
     * @return
     */
    protected String tag() {
        return CmdPublish.class.getSimpleName();
    }

}
