package io.haydar.sg.bean;

/**
 * Created by gjy on 16/5/9.
 */
public class SGFolder {

    private String id;
    private String name;
    private int count;

    public SGFolder(String i, String str) {
        this.id = i;
        this.setName(str);
    }

    public SGFolder() {

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setName(String name) {
        this.name = name;
    }
}
