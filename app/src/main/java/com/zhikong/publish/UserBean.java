package com.zhikong.publish;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserBean {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;

    public UserBean() {
    }

    public UserBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
