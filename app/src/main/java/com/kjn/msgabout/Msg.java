package com.kjn.msgabout;

/**
 * Created by kjnijk on 2016-09-04.
 */
public class Msg {
    public static  final int TYPE_RECEIVED=0;
    public  static final int TYPE_SEND=1;
    private String content;
    private int imageId;
    private int type;
    public Msg(int imageId ,String content,int type){
        this.imageId=imageId;
        this.content=content;
        this.type=type;
    }

    public int getImageId() {
        return imageId;
    }
    public String getContent(){
        return content;
    }
    public int getType() {
        return type;
    }
}
