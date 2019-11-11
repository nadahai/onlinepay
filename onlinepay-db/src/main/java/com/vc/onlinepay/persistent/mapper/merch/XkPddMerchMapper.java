/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.merch;

import com.vc.onlinepay.persistent.entity.merch.XkPddMerch;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XkPddMerchMapper {

	
    /**
     * @描述:获取 PDD商家列表
     */
    public List<XkPddMerch> getPddMerchList(XkPddMerch xkPddMerch);


    /**
     * @描述:更新金额
     */
    public Integer updateTradeAmount(XkPddMerch xkPddMerch);

    /**
     * 日交易金额清零
     */
    public Integer cleanDayTradeAmount();


}