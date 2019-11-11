/**
 * @类名称:SpringBootDemoRedis3ApplicationTests.java
 * @时间:2018年5月29日下午3:34:53
 * @版权:公司 Copyright (c) 2018
 */
package com.vc.onlinepay;

import com.vc.onlinepay.persistent.entity.merch.SupplierSubno;
import com.vc.onlinepay.persistent.entity.online.VcOnlineOrder;
import com.vc.onlinepay.persistent.service.channel.MerchChannelServiceImpl;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderServiceImpl;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @描述:数据持久测试
 * @时间:2018年5月29日 下午3:34:53 
 */
/*@RunWith (SpringRunner.class)
@SpringBootTest*/
public class TestDb {

    private static final Logger logger = LoggerFactory.getLogger (TestDb.class);

    @Autowired
    private VcOnlineOrderServiceImpl vcOnlineOrderService;

    @Autowired
    private MerchChannelServiceImpl merchChannelService;


    @Ignore
    public void test_order () {
        String orderNo = "h5181228141102309007";
        VcOnlineOrder  vcOnlineOrder = vcOnlineOrderService.findOrderByOrderNo(orderNo);

        VcOnlineOrder order = new VcOnlineOrder (orderNo);
        order.setSettleStatus (1);
        int r = vcOnlineOrderService.updateOrderSettleStatus (order);
        logger.info ("更新订单结算结果{},{}", orderNo, r);

        SupplierSubno supplierSubno = new SupplierSubno(vcOnlineOrder.getUpMerchNo(),vcOnlineOrder.getUpMerchKey(),null);
        r = merchChannelService.updateAlipaySubNoAmount(supplierSubno);
        logger.info ("更新支付宝子账号信息{},{}",orderNo,r);
    }
}

