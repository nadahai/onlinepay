package com.vc.onlinepay.persistent.mapper.online;

import com.vc.onlinepay.persistent.entity.online.VcOnlinePaymentCard;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface VcOnlinePaymentCardMapper {

    /**
     * @描述:保存已经代付过的卡
     * @时间:2018年1月11日 下午4:56:10
     */
    int save(VcOnlinePaymentCard card);

    /**
     * @描述:查询已存在代付过的卡
     * @时间:2018年1月11日 下午4:55:38
     */
    List<VcOnlinePaymentCard> findDisableBank(VcOnlinePaymentCard card);
}