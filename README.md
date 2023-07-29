# PublishCmd

Like event-bus, but different. method name or annotation is the topic, not parameter object.

## Add PublishCmd to your project

```java
implementation 'io.github.congyl1988.publishcmd:publishcmd:1.0.3'
```

## PublishCmd in 2 steps

1. Prepare subscribers: Declare and annotate your subscribing method, optionally specify a [thread mode], 'hello' is the topic, a UserBean is the parameter.

```java
@Cmd(threadMode = ThreadMode.PUBLISH)
public void hello(UserBean userBean){
    //do something
}
```

or 'hello' is define in annotation, any method name is ok.

```java
@Cmd(name = "hello",threadMode = ThreadMode.PUBLISH)
public void xxx(UserBean userBean){
    //do something
}
```

you can do something before 'hello' runing.

```java
@CmdIntercept(cmdNames = {"hello"})
public void intercept() {
    //do itercepting
}
```

Register and unregister your cmd. For example on Android, activities and fragments should usually register according to their life cycle:

```java
 @Override
 public void onStart() {
     super.onStart();
     CmdPublish.getInstance().register(this);
 }

 @Override
 public void onStop() {
     super.onStop();
     CmdPublish.getInstance().unregister(this);
 }
```

2. Publish @cmd: 'hello' is the topic, a UserBean is the parameter.

   ```java
   CmdPublish.getInstance().publish("hello",new UserBean(123,"hello"));
   ```

   or json can be parsed to userBean. Add your json dependency, simple gson.

   ```java
   CmdPublish.getInstance().setJsonAdapter(new JsonAdapter(){
           private final Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
   
           @Override
           public Object fromJson(String json,Class<?> clazz){
               return gson.fromJson(json,clazz);
           }
   });
   String json = "{\"name\":\"cong\",\"id\":2}";
   CmdPublish.getInstance().publishJson("hello", json);
   ```

You can run the @cmd method in different thread. As below:

```java
//You can run the @cmd method in diffrent thread. 
public enum ThreadMode {
    ASYNC,
    MAIN,
    SINGLE,
    PUBLISH
}
```

