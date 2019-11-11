/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.persistent.mapper.online;

import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderAa;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * aa订单收款DAO接口
 * @author nada
 * @version 2019-04-24
 */
@Repository
public interface VcOnlineOrderAaMapper {

    /**
     * @描述:查询所有下单中的交易订单号
     * @作者:nada
     * @时间:2019/4/26
     **/
    List<String> findAllOkTradNos(VcOnlineOrderAa vcOnlineOrderAa);


    List<VcOnlineOrderAa> findCallByUserIds(VcOnlineOrderAa vcOnlineOrderAa);

    /**
     * @描述 获取一条AA库
     * @作者 nada
     * @时间 2019/5/19 22:34
     */
    List<VcOnlineOrderAa> findOneOkAATradNo(VcOnlineOrderAa vcOnlineOrderAa);

    /**
     * @描述:根据tradNo查询账号信息
     * @作者:nada
     * @时间:2019/4/26
     **/
    VcOnlineOrderAa findAAByTradNo(VcOnlineOrderAa vcOnlineOrderAa);

    VcOnlineOrderAa findAAByZhOrder(VcOnlineOrderAa vcOnlineOrderAa);

    int update(VcOnlineOrderAa vcOnlineOrderAa);

    int updateOrderSuccess(VcOnlineOrderAa vcOnlineOrderAa);

    int updateStatus(VcOnlineOrderAa vcOnlineOrderAa);

    int updateUserId(VcOnlineOrderAa vcOnlineOrderAa);

    int updateToken(VcOnlineOrderAa vcOnlineOrderAa);

    int insert(VcOnlineOrderAa vcOnlineOrderAa);

    List<VcOnlineOrderAa> findAllList(VcOnlineOrderAa vcOnlineOrderAa);

    List<VcOnlineOrderAa> findByUserId(VcOnlineOrderAa vcOnlineOrderAa);


    VcOnlineOrderAa findOtherUserId(VcOnlineOrderAa vcOnlineOrderAa);


    List<VcOnlineOrderAa> findCallList(VcOnlineOrderAa vcOnlineOrderAa);

    int delete(VcOnlineOrderAa vcOnlineOrderAa);
    
    //List<String> findUpMerchNosByAmount(VcOnlineOrderAa vcOnlineOrderAa);

    /**
     * 批次插入数据
     */
    int insertBatch(@Param("entityList") List<VcOnlineOrderAa> entityList);

    /**
    * @描述 单个保存数据
    * @作者 nada
    * @时间 2019/5/19 15:58
    */
    int insertOne(VcOnlineOrderAa vcOnlineOrderAa);

    /**
     *  查找重复的
     * @param amount    金额
     * @param upMerchNo 收款账号
     * @param reason    收款原因
     * @return
     */
    int selectRepeat(@Param("amount") Integer amount, @Param("upMerchNo") String upMerchNo, @Param("reason") String reason);

    /**
     *  查找当日订单笔数
     * @param upMerchNo
     * @return
     */
    int selectOrderCount(@Param("upMerchNo") String upMerchNo);




}