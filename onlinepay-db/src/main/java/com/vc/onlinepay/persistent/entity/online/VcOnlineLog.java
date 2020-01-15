/**
 * @类名称:VcOnlineLog.java
 * @时间:2018年3月5日下午3:23:50
 * @作者:nada
 * @版权:公司 Copyright (c) 2018
 */
package com.vc.onlinepay.persistent.entity.online;

import java.math.BigDecimal;

/**
 * @描述:日志管理
 * @作者:nada
 * @时间:2018年3月5日 下午3:23:50
 */
public class VcOnlineLog {
    
    public static final int SYS_LOG = 1;
    public static final int FRAME_LOG = 2;
    public static final int BUS_LOG = 3;
    public static final int PAYAPI_LOG =4;
    
    public static final int EXCEPTION_ERROR =6;
    public static final int LEVEL_ERROR =5;
    public static final int LEVEL_WARN =3;
    public static final int LEVEL_INFO =1;

    private BigDecimal id;

    //日志级别: 1:info 2:debug 3:warn 4:failed 5:error 6:exception
    private int logLevel;

    //1:系统日志 2:框架日志  3:业务层日志 4:支付接口层日志
    private int type;

    private int status;

    private String logDes;

    private String remark;

    private String remarks;
    
    private String title;
    
    public VcOnlineLog() {
    }
    
    public VcOnlineLog(String logDes) {
        this.logLevel = 5;
        this.type = 3;
        this.status = 1;
        this.logDes = logDes;
    }

    public VcOnlineLog(int logLevel, int type,String title,String logDes) {
        this.logLevel = logLevel;
        this.type = type;
        this.status = 1;
        this.title = title;
        this.logDes = logDes;
    }

    public VcOnlineLog(int logLevel, int type,String title,String logDes,String remark,String remarks) {
        this.logLevel = logLevel;
        this.type = type;
        this.status = 1;
        this.title = title;
        this.logDes = logDes;
        this.remark = remark;
        this.remarks = remarks;
    }
    
    public VcOnlineLog(int logLevel, int type, String logDes,String remark,String remarks) {
        this.logLevel = logLevel;
        this.type = type;
        this.logDes = logDes;
        this.status = 1;
        this.remark = remark;
        this.remarks = remarks;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLogDes() {
        return logDes;
    }

    public void setLogDes(String logDes) {
        this.logDes = logDes;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public BigDecimal getId() {
        return id;
    }


    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
