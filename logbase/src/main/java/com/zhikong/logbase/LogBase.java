package com.zhikong.logbase;

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
 */
public class LogBase {
    public static ILog logUtil;

    public static int w(String tag, String msg) {
        return logUtil.d(tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        return logUtil.e(tag, msg, tr);
    }

    public interface ILog {
        int v(String tag, String msg);

        int v(String tag, String msg, Throwable tr);

        int d(String tag, String msg);

        int d(String tag, String msg, Throwable tr);

        int i(String tag, String msg);

        int i(String tag, String msg, Throwable tr);

        int w(String tag, String msg);

        int w(String tag, String msg, Throwable tr);

        int w(String tag, Throwable tr);

        int e(String tag, String msg);

        int e(String tag, String msg, Throwable tr);
    }

}
