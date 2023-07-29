# PublishCmd

Like event-bus, but different. method name or annotation is the topic, not parameter object.

```java
    //发布一个命令和参数对象。Publish a cmd and a parameter object.
public void publishCmd(){
        /**
         * 设定logUtil为 android log
         */
        CmdPublish.logUtil=new com.zhikong.androidlog.LogUtil();
//        CmdPublish.logUtil = new com.zhikong.logsystem.LogUtil();//Print by System.out.println();

        /**
         * 发布hello world命令.
         * publish the 'hello' and 'world' cmd.
         */
        CmdPublish.getInstance().register(this)
        .publish("hello",new UserBean());

        CmdPublish.getInstance()
        .publish("world",new UserBean())
        .destroy();
        }

//发布json，收到解析后的json对象。You will receive the object that is parsed from json published.
public void publishJson(){

        /**
         * 添加json解析操作
         */
        JsonInterceptPublish.getInstance().setJsonAdapter(new JsonAdapter(){
private final Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

@Override
public Object fromJson(String json,Class<?> clazz){
        return gson.fromJson(json,clazz);
        }
        });

        String json="{\"name\":\"cong\",\"id\":2}";

        /**
         * 注册对象
         */
        JsonPublish.getInstance().register(this);

        /**
         * 发布命令,以json数据的方式
         */
        JsonPublish.getInstance().publish("t",json);
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
         * call by one line.
         */
        JsonPublish.getInstance()
        .register(this)//注册对象，
        .publishJson("t",json) //发布json数据
        .publish("empty")//仅仅发布一个命令，没有任何数据
        .publish("hello",new UserBean())//发布一个对象数据
        .publish("someString","hello","cmdPublish")//发布多个字符串命令
        .unregister(this);//解注册对象
        }

/*如下会收到命令，Cmd will run in these methods.*/
@Cmd(name = "world", threadMode = ThreadMode.PUBLISH)
public void xxx(UserBean userBean){
        CmdPublish.logUtil.w("cong","world----------------------------"+userBean);
        }

@Cmd(name = "t", threadMode = ThreadMode.PUBLISH)
public void publishCmd(Object t){
        CmdPublish.logUtil.w("cong","test----------------------------"+t);
        }

@Cmd(name = "empty", threadMode = ThreadMode.PUBLISH)
public void empty(){
        CmdPublish.logUtil.w("cong","empty----------------------------");
        }

@Cmd(threadMode = ThreadMode.PUBLISH)
public void hello(UserBean userBean){
        CmdPublish.logUtil.w("cong","hello----------------------------"+userBean);
        }

@Cmd(threadMode = ThreadMode.PUBLISH)
public void someString(String s1,String s2){
        CmdPublish.logUtil.w("cong","hello----------------------------"+s1+","+s2);
        }

@CmdIntercept(cmdNames = {"t"})
public void before(){
        CmdPublish.logUtil.w("cong","before----------------------------");
        }
```

```java
//You can run the @cmd method in diffrent thread. 
public enum ThreadMode {
    ASYNC,
    MAIN,
    SINGLE,
    PUBLISH
}
```

