/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.merch;

import com.vc.onlinepay.persistent.entity.merch.XkPddBuyer;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XkPddBuyerMapper {

	
    /**
     * @描述:获取星管家码商列表
     */
    List<XkPddBuyer> getPddBuyerList(XkPddBuyer xkPddBuyer);


    /**
     * @描述:更新金额
     */
    Integer updateTradeAmount(XkPddBuyer xkPddBuyer);

    /**
     * @描述:更新金额 一般是买家有问题关闭买家
     */
    Integer closePddBuyer(XkPddBuyer xkPddBuyer);

    /**
     * 日交易金额清零
     */
    Integer cleanDayTradeAmount();
   
}