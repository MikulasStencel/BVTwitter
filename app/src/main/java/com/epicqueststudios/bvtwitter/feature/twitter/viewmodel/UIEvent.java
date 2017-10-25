package com.epicqueststudios.bvtwitter.feature.twitter.viewmodel;


public class UIEvent {
    public enum TYPE {ON_ERROR, ADD_TWEET, SAVE_LAST_SEARCHED_QUERY};

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    private TYPE type;
    private Object data;

    public UIEvent(TYPE type, Object data){
        this.type = type;
        this.data = data;
    }

    public String toString() {
        return getClass().getName() + ":" + type.name()+", "+data.toString();
    }

}
