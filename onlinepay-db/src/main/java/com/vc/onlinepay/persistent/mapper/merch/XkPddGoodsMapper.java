/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.merch;

import com.vc.onlinepay.persistent.entity.merch.XkPddGoods;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XkPddGoodsMapper {

	
    /**
     * @描述:获取商品
     */
    public List<XkPddGoods> getPddGoodsList(XkPddGoods xkPddGoods);

    /**
     * @描述:更新金额
     */
    public Integer updateTradeAmount(XkPddGoods xkPddGoods);

    /**
     * 日交易金额清零
     */
    public Integer cleanDayTradeAmount();
   
}