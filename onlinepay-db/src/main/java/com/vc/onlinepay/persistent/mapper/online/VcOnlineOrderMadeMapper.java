/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.online;

import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * 订单转换DAO接口
 * @author 订单转换
 * @version 2018-08-21
 */
@Repository
public interface VcOnlineOrderMadeMapper {
	
	/**
	 * @描述:查找
	 * @时间:2018年8月21日 上午10:33:52
	 */
	VcOnlineOrderMade findOrderByOrderNo(String orderNo);
	
	/**
	 * @描述:查找历史订单记录
	 * @时间:2018年8月21日 上午10:33:52
	 */
	VcOnlineOrderMade getRecordOrder(VcOnlineOrderMade made);

	/**
	 * @描述:修改
	 * @时间:2018年1月31日 下午2:37:24
	 */
	int updateByOrderNo(VcOnlineOrderMade vcOnlineOrder);
	
	/**
	 * @描述:修改模板信息
	 * @时间:2018年1月31日 下午2:37:24
	 */
	int updateTemplateByOrderNo(VcOnlineOrderMade vcOnlineOrder);
	
	int updateRemarksByOrderNo(VcOnlineOrderMade vcOnlineOrder);

	/**
	 * @描述:保存
	 * @时间:2018年1月31日 下午2:32:08
	 */
	int save(VcOnlineOrderMade vcOnlineOrder);
	
	/**
	 * @描述:保存
	 * @时间:2018年1月31日 下午2:32:08
	 */
	int saveTemplate(VcOnlineOrderMade vcOnlineOrder);
	
	int updateQrcodelByOrderNo(VcOnlineOrderMade vcOnlineOrder);
	
	/**
	 * @描述:根据订单信息查找订单
	 * @作者:ChaiJing THINK
	 * @时间:2018/8/24 15:17
	 */
    List<VcOnlineOrderMade> getOrderInfo(VcOnlineOrderMade vcOnlineOrderMade);
	/**
	 * @描述:查找5分钟内付临门订单号
	 * @作者:ChaiJing THINK
	 * @时间:2018/8/24 15:17
	 */
	List<VcOnlineOrderMade> findFlmOrders(VcOnlineOrderMade vcOnlineOrderMade);

}