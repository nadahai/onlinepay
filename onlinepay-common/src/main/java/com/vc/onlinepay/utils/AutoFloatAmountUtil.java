package com.vc.onlinepay.utils;

import com.vc.onlinepay.cache.RedisCacheApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Service
@Component
public class AutoFloatAmountUtil {

    public static final Logger logger = LoggerFactory.getLogger(AutoFloatAmountUtil.class);

    public static final BigDecimal MAX_FLOAT_AMOUNT = new BigDecimal("0.30");

    public static final BigDecimal bigDecimalZero = new BigDecimal("0");

    public static final DecimalFormat amountFormat = new DecimalFormat("0.00");

    @Autowired
    private RedisCacheApi redisCacheApi;

    /**
     * @描述:自动获取浮动金额
     * @时间:2018年9月5日 下午6:24:36
     */
    public String getAutoAmount(String account, String price,String orderNo) {
        logger.info("开始自动浮动金额订单:{},金额price:{},account{}", orderNo,price, account);
        BigDecimal bigPrice = new BigDecimal(price);
        if(bigPrice.compareTo(AutoFloatAmountUtil.MAX_FLOAT_AMOUNT) < 0){
            return price;
        }
        if (!existsOrderKey(getRedisKey(account, price))) {
            return price;
        }
        //上浮.0
        String amount = this.addFlotAmount(account, price, bigPrice,orderNo);
        if (amount != null && !"".equals(amount)) {
            return amount;
        }
        //下浮
        return subFlotAmount(account, price, bigPrice,orderNo);

       /* //下浮
        String amount = this.subFlotAmount(account, price, bigPrice,orderNo);
        if (amount != null && !"".equals(amount)) {
            return amount;
        }
        //上浮
        return addFlotAmount(account, price, bigPrice,orderNo);*/
    }

    /**
     * @描述:上浮金额,极限是0.30元
     * @时间:2018年9月5日 下午6:51:25
     */
    public String addFlotAmount(String account, String amount, BigDecimal price,String orderNo) {
        logger.info("开始上浮订单:{},金额price:{},amount{}", orderNo,price, amount);
        BigDecimal money = new BigDecimal(amount);
        amount = AutoFloatAmountUtil.amountFormat.format(money.add(new BigDecimal("0.01")).doubleValue());
        money = new BigDecimal(amount);
        if (money.subtract(price).compareTo(AutoFloatAmountUtil.MAX_FLOAT_AMOUNT) >= 0) {
            return null;
        }
        if (existsOrderKey(getRedisKey(account, amount))) {
            return addFlotAmount(account, amount, price,orderNo);
        }
        return amount;
    }

    /**
     * @描述:下浮金额，极限是0.30元
     * @时间:2018年9月5日 下午6:51:25
     */
    public String subFlotAmount(String account, String amount, BigDecimal price,String orderNo) {
        logger.info("开始下浮订单:{},金额price:{},amount{}", orderNo,price, amount);
        BigDecimal money = new BigDecimal(amount);
        amount = AutoFloatAmountUtil.amountFormat.format(money.subtract(new BigDecimal("0.01")).doubleValue());
        money = new BigDecimal(amount);
        if (price.subtract(money).compareTo(AutoFloatAmountUtil.MAX_FLOAT_AMOUNT) >= 0) {
            return null;
        }
        if (existsOrderKey(getRedisKey(account, amount))) {
            return subFlotAmount(account, amount, price,orderNo);
        }
        return amount;
    }

    /**
     * @描述:格式化金额
     * @时间:2018年9月6日 下午3:30:55
     */
    public String amountFormat(String amount) {
        return amountFormat.format(new BigDecimal(amount).doubleValue());
    }

    /**
     * @描述:格式化金额
     * @时间:2018年9月6日 下午3:30:55
     */
    public String amountFormat(double amount) {
        return amountFormat.format(amount);
    }

    /**
     * @描述:获取accountNO+&+amount作为key
     * @时间:2018年9月6日 下午7:53:49
     */
    public String getRedisKey(String accountNo,String amount) {
        return accountNo+"_"+amountFormat(amount);
    }

    /**
     * @描述:是否存在map金额
     * @时间:2017年8月31日 下午6:42:03
     */
    public boolean existsOrderKey(String redisKey) {
        try {
            if (StringUtils.isBlank(redisKey)) {
                return false;
            }
            //去除整数
            if(redisKey.endsWith(".00")){
                return true;
            }
            return redisCacheApi.exists(redisKey);
        } catch (Exception e) {
            logger.error("获取map缓存 异常", e);
            return false;
        }
    }

    /**
     * @描述:删除账户金额
     * @时间:2017年8月31日 上午11:56:24
     */
    public boolean removeOrder(String redisKey) {
        try {
            return redisCacheApi.remove(redisKey);
        } catch (Exception e) {
            logger.error("删除缓存异常", e);
        }
        return false;
    }

}
