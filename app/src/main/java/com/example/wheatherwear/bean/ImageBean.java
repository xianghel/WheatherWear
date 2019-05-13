package com.example.wheatherwear.bean;

/**
 * @author lxh
 * @date 2019/5/13
 */
public class ImageBean {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public ImageBean(int id,String imagePath, String imageName) {
        this.id=id;
        this.imageName = imageName;
        this.imagePath = imagePath;
    }

    private String imagePath;
    private String imageName;
}
