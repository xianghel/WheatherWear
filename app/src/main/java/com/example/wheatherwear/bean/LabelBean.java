package com.example.wheatherwear.bean;

/**
 * @author lxh
 * @date 2019/5/13
 */
public class LabelBean {
    private String name;
    private int id;

    public LabelBean(String name, int id) {
        this.name = name;
        this.id = id;
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
        return "TestBean{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
