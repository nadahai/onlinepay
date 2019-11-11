/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.online;


import com.vc.onlinepay.exception.OnlineServiceException;
import com.vc.onlinepay.persistent.entity.online.VcOnlineWallet;
import org.springframework.stereotype.Repository;

/**
 * 在线交易财富信息DAO接口
 * @author 李海
 * @version 2017-06-30
 */
@Repository
public interface VcOnlineWalletMapper{

    /**
     * @描述:查找商户所有信息
     * @作者:lihai 
     * @时间:2017年12月6日 下午12:41:40
     */
	public VcOnlineWallet findByMerchId(Long merchId);

	/**
     * @描述:查找商户所有信息
     * @作者:lihai 
     * @时间:2017年12月6日 下午12:41:40
     */
	public VcOnlineWallet findBymerchNo(String merchantNo);
	
	/**
     * @描述:更新商户D1财富信息
     * @作者:lihai 
     * @时间:2017年12月6日 下午12:41:40
     */
	public int updateD1SuccessOrder(VcOnlineWallet vcOnlineWallet);
	
	/**
     * @描述:更新直清财富
     * @作者:lihai 
     * @时间:2017年12月6日 下午12:41:40
     */
	public int updatStraightSuccessOrder(VcOnlineWallet vcOnlineWallet);
	
	
	/**
     * @描述:更新商户D0财富信息
     * @作者:lihai 
     * @时间:2017年12月6日 下午12:41:40
     */
	public int updateD0SuccessOrder(VcOnlineWallet vcOnlineWallet);
	
	/**
     * @描述:开始更新商户财富信息
     * @作者:lihai 
     * @时间:2017年12月6日 下午12:41:40
     */
    public int updateD0WalletCashStart(VcOnlineWallet vcOnlineWallet) throws OnlineServiceException;
    
    /**
     * @描述:开始更新商户财富信息
     * @作者:lihai 
     * @时间:2017年12月6日 下午12:41:40
     */
    public int updateD0WalletCashRollback(VcOnlineWallet vcOnlineWallet) throws OnlineServiceException;
    
    /**
     * @描述:开始更新商户财富信息
     * @作者:lihai 
     * @时间:2017年12月6日 下午12:41:40
     */
    public int updateD0WalletCashDone(VcOnlineWallet vcOnlineWallet) throws OnlineServiceException;
    
    /**
     * @描述:开始更新商户财富信息
     * @作者:lihai 
     * @时间:2017年12月6日 下午12:41:40
     */
    public int updateD1WalletCashStart(VcOnlineWallet vcOnlineWallet) throws OnlineServiceException;
    
    /**
     * @描述:开始更新商户财富信息
     * @作者:lihai 
     * @时间:2017年12月6日 下午12:41:40
     */
    public int updateD1WalletCashRollback(VcOnlineWallet vcOnlineWallet) throws OnlineServiceException;
    
    /**
     * @描述:开始更新商户财富信息
     * @作者:lihai 
     * @时间:2017年12月6日 下午12:41:40
     */
    public int updateD1WalletCashDone(VcOnlineWallet vcOnlineWallet) throws OnlineServiceException;
    
}