package com.zhikong.publish;

import java.lang.reflect.Method;
import java.util.Objects;

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
public class CmdAnnotation {
    String name;
    ThreadMode threadMode;

    public static CmdAnnotation getCmdName(Method method) {
        Cmd cmd = method.getAnnotation(Cmd.class);
        if (cmd == null) {
            return null;
        }
        String methodName;
        //注解没有定义，用方法的名字。
        if (Objects.isNull(cmd.name())
                || cmd.name().trim().equals("")) {
            methodName = method.getName();
        } else {
            methodName = cmd.name();
        }
        CmdAnnotation ca = new CmdAnnotation();
        ca.name = methodName;
        ca.threadMode = cmd.threadMode();
        return ca;
    }

}