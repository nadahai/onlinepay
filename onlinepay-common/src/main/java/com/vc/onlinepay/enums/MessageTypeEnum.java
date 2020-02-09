package com.vc.onlinepay.enums;

/**
 * @author Hiutung
 * @create 2019-04-28 19:15
 * @desc
 * @copyright vcgroup.cn
 */
public enum MessageTypeEnum {
    WINDOW("弹窗提示"),
    PAYMENT_APPROVE("代付审核"),
    ORDER_FAILED("下单失败"),
    ORDER_REPAY("异常订单")
    ;


    private String desc;

    MessageTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
