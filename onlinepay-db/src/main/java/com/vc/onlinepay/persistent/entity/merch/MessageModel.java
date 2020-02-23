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

    public MessageModel(MessageTypeEnum type, String pushMsg) {
        this.type = type;
        this.pushMsg = pushMsg;
    }

    public MessageModel(MessageTypeEnum type, String pushTitle, String pushMsg) {
        this.type = type;
        this.pushTitle = pushTitle;
        this.pushMsg = pushMsg;
    }

    public MessageModel(String pushTitle, String pushMsg) {
        this.type = MessageTypeEnum.WINDOW;
        this.pushTitle = pushTitle;
        this.pushMsg = pushMsg;
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

    public void setTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    public String getMsg() {
        return pushMsg;
    }

    public void setMsg(String pushMsg) {
        this.pushMsg = pushMsg;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "type=" + type +
                ", pushTitle='" + pushTitle + '\'' +
                ", pushMsg='" + pushMsg + '\'' +
                '}';
    }

    public JSONObject toJSONObject() {
        return (JSONObject) JSONObject.toJSON(this);
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }
}
