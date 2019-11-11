/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.channel;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.vc.onlinepay.persistent.entity.merch.SupplierSubno;

/**
 * 供应商账号管理DAO接口
 * @author 李海
 * @version 2018-09-12
 */
@Repository
public interface SupplierSubnoMapper{
	
	/**
	 * @描述:根据账号获取列表（只包含启用）
	 * @时间:2018年9月13日 下午12:12:24
	 */
    List<SupplierSubno> getByUpMerchNo (SupplierSubno subno);

    Integer updateUserId(SupplierSubno channelSubNo);

    /**
     * @描述:根据账号获取信息
     * @时间:2018年9月13日 下午12:12:24
     */
    SupplierSubno getSubnoById (SupplierSubno subno);

    /**
     * @描述:根据账号获取列表（包含禁用）
     * @时间:2018年9月13日 下午12:12:24
     */
    List<SupplierSubno> getOneByUpMerchNo (SupplierSubno subno);

    /**
     * @描述:获取子账号列表
     * @作者:nada
     * @时间:2018/12/26
     **/
    List<SupplierSubno> getAllSupplierList (SupplierSubno subno);

    /**
     * @描述:更新金额
     * @时间:2018年5月17日 下午7:19:07
     */
    Integer updateSubNoAmount (SupplierSubno channelSubNo);
    
    /**
     * @描述:更新结算金额
     * @时间:2018年5月17日 下午7:19:07
     */
    Integer updateSettleAmount (SupplierSubno channelSubNo);

    /**
     * @描述:商户更新下单时间
     * @时间:2018/8/13 14:36
     */
    Integer updateLastOrderTime(SupplierSubno channelSubNo);

    /**
     * @描述:更新账户状态
     * @时间:2018/8/13 14:36
     */
    Integer updateStatus(SupplierSubno channelSubNo);

    /**
     * @描述:更新银行卡信息
     * @作者:nada
     * @时间:2019/4/12
     **/
    Integer updateCardIdx(SupplierSubno channelSubNo);
	
}