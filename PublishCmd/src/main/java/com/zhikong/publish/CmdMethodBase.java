package com.zhikong.publish;

import com.zhikong.publish.log.LogBase;

import java.lang.reflect.Method;
import java.util.Arrays;

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
public class CmdMethodBase extends LogBase {
    private final String TAG = this.getClass().getSimpleName();
    String name;//Cmd注解name or 方法名字。 Name of Cmd annotation.
    Object object;//Cmd注解方法所在类的对象。 The Object that contains cmd annotation method.
    Class<?>[] aClass;//Cmd注解方法参数类型。All parameters of the cmd annotation method.
    Method method;
    ThreadMode threadMode;

    public CmdMethodBase(String name, Object object, Method method, ThreadMode threadMode) {
        this.name = name;
        this.object = object;
        this.method = method;
        this.threadMode = threadMode;
    }

    public void invoke(Object... parameter) {
        try {
            method.invoke(object, parameter);
        } catch (Exception e) {
            e(TAG, "error invoke << " + this + "," + Arrays.toString(parameter), e);
        }
    }

    @Override
    public String toString() {
        return "CmdMethodBase{" +
                "name='" + name + '\'' +
                ", object=" + object +
                ", aClass=" + Arrays.toString(aClass) +
                ", method=" + method +
                ", threadMode=" + threadMode +
                '}';
    }
}
