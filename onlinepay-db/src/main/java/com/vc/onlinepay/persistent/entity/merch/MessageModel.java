package com.vc.onlinepay.persistent.entity.merch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.enums.MessageTypeEnum;

/**
 * @author Hiutung
 * @create 2019-05-13 10:51
 * @desc 发送消息Model
 * @copyright vcgroup.cn
 */
public class MessageModel {

    /**
     * 消息类型
     */
    private MessageTypeEnum type;
    private String pushTitle;//标题
    private String pushMsg;//消息内容

    private MessageModel() {
    }

    public MessageModel(MessageTypeEnum type) {
        this.type = type;
    }

    public MessageModel(MessageTypeEnum type, String msg) {
        this.type = type;
        this.pushMsg = msg;
    }

    public MessageModel(MessageTypeEnum type, String title, String msg) {
        this.type = type;
        this.pushTitle = title;
        this.pushMsg = msg;
    }

    public MessageModel(String title, String msg) {
        this.type = MessageTypeEnum.WINDOW;
        this.pushTitle = title;
        this.pushMsg = msg;
    }
    
    public MessageTypeEnum getType() {
        return type;
    }

    public void setType(MessageTypeEnum type) {
        this.type = type;
    }

    public String getTitle() {
        return pushTitle;
    }

    public void setTitle(String title) {
        this.pushTitle = title;
    }

    public String getMsg() {
        return pushMsg;
    }

    public void setMsg(String msg) {
        this.pushMsg = msg;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "type=" + type +
                ", title='" + pushTitle + '\'' +
                ", msg='" + pushMsg + '\'' +
                '}';
    }

    public JSONObject toJSONObject() {
        return (JSONObject) JSONObject.toJSON(this);
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }
}
