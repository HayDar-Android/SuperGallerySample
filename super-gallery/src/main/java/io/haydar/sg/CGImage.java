package io.haydar.sg;

import java.io.Serializable;

/**
 * Created by gjy on 16/4/28.
 */
public class CGImage implements Serializable{

    private String id;  //id
    private String path;    //地址
    private String size;    //大小
    private String name;    //名称
    private String type;    //类型
    private String bucketId;
    private String bucketName;  //文件夹名字
    private int width;      //高
    private int height;     //低
    private String thumbnails;  //缩率图
    private int thumbnailsWidth;
    private int thumbnailsHeight;

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


    public void setThumbnailsWidth(int thumbnailsWidth) {
        this.thumbnailsWidth = thumbnailsWidth;
    }

    public int getThumbnailsHeight() {
        return thumbnailsHeight;
    }

    public void setThumbnailsHeight(int thumbnailsHeight) {
        this.thumbnailsHeight = thumbnailsHeight;
    }

    public int getThumbnailsWidth() {
        return thumbnailsWidth;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("\nid:" + id);
        strBuilder.append("\npath:" + path);
        strBuilder.append("\nsize:" + size);
        strBuilder.append("\nname:" + name);
        strBuilder.append("\ntype:" + type);
        strBuilder.append("\nbucketId:" + bucketId);
        strBuilder.append("\nbucketName:" + bucketName);
        strBuilder.append("\nwidth:" + width);
        strBuilder.append("\nheight:" + height);
        strBuilder.append("\nthumbnails:" + thumbnails);
        strBuilder.append("\nthumbnailsWidth:" + thumbnailsWidth);
        strBuilder.append("\nthumbnailsHeight:" + thumbnailsHeight);
        return strBuilder.toString();
    }
}
