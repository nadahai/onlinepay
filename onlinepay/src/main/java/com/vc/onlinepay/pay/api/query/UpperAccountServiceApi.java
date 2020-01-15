package com.vc.onlinepay.pay.api.query;

import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.pay.common.ReplaceServiceImpl;
import com.vc.onlinepay.pay.common.ResultListener;
import com.vc.onlinepay.pay.query.account.RemitAccountServiceImpl;
import com.vc.onlinepay.pay.replace.GaoYangReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.HuiYunReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.InsteadReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.JiaLiangReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.KuaiBaoReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.NaTieReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.PinDuoduoReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.SandReplaceServiceImpl;
import com.vc.onlinepay.pay.replace.YouFuReplaceServiceImpl;
import com.vc.onlinepay.persistent.entity.online.VcOnlineThirdBalance;
import com.vc.onlinepay.persistent.service.online.VcOnlineThirdBalanceServiceImpl;
import com.vc.onlinepay.utils.Constant;
import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class UpperAccountServiceApi{
	private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RemitAccountServiceImpl remitAccountService;
    @Autowired
    private VcOnlineThirdBalanceServiceImpl vcOnlineThirdBalanceService;
    @Autowired
    private VcOnlineThirdBalanceServiceImpl thirdBalanceService;
    @Autowired
    private ReplaceServiceImpl replaceService;
    @Autowired
    private InsteadReplaceServiceImpl insteadReplaceService;
    @Autowired
    private JiaLiangReplaceServiceImpl jiaLiangReplaceServiceImpl;
    @Autowired
    private HuiYunReplaceServiceImpl huiYunReplaceServiceImpl;
    @Autowired
    private GaoYangReplaceServiceImpl gaoYangReplaceService;
    @Autowired
    private SandReplaceServiceImpl sandReplaceService;
    @Autowired
    private PinDuoduoReplaceServiceImpl pinDuoduoReplaceServiceImpl;
    @Autowired
    private YouFuReplaceServiceImpl youFuReplaceServiceImpl;
    @Autowired
    private NaTieReplaceServiceImpl naTieReplaceServiceImpl;
    @Autowired
    private KuaiBaoReplaceServiceImpl kuaiBaoReplaceServiceImpl;
    


    /**
     * @描述:余额查询统一入口
     * @时间:2018年5月21日 上午9:46:05
     */
	public JSONObject doUpperQuery(JSONObject reqData) {
        try {
            logger.info("上游余额 入参:{}",reqData);
            String  vcService = reqData.getString("vcService");
            List<VcOnlineThirdBalance> thirdBalances = vcOnlineThirdBalanceService.findAllBalanceList(new VcOnlineThirdBalance(vcService));
            if(thirdBalances == null || thirdBalances.size()< 1){
                return Constant.failedMsg("不存在的上游余额查询通道:"+vcService);
            }
            JSONObject result = new JSONObject();
            StringBuilder msg = new StringBuilder();
            int cashMode = 0;
            for (VcOnlineThirdBalance balance:thirdBalances) {
            	try {
            		  cashMode = balance.getCashMode();
	            	  int channelSource = balance.getChannelSource();
	                  String channelLabel = balance.getBalanceLabel();
	                  reqData.put("channelMerchNo", StringUtils.deleteWhitespace(balance.getMerchNo()));
	                  reqData.put("channelKeyDes", StringUtils.deleteWhitespace(balance.getMerchKey()));
	                  //解密
                      replaceService.decodeChannelKey(reqData);
	                  reqData.put("channelLabel", channelLabel);
                      // 1:T0 2:T1
	                  reqData.put("cashMode", cashMode);
                      reqData.put("channelSource", channelSource);
                	  switch (channelSource) {
                      case 1:
                          result =  insteadReplaceService.walletQuery(reqData,this.getResultListener());
                          break;
                       case 13:
                          result =  gaoYangReplaceService.walletQuery(reqData,this.getResultListener());
                          break;
                      case 19:
                          result =  remitAccountService.walletQuery(reqData,this.getResultListener());
                      case 27:
                          //result =  sandReplaceService.walletQuery(reqData,this.getResultListener());
                          break;
                      case 28:
                          result =  jiaLiangReplaceServiceImpl.walletQuery(reqData,this.getResultListener());
                          break;
                      case 92:
                          result =  huiYunReplaceServiceImpl.walletQuery(reqData,this.getResultListener());
                          break;
                      case 93:
                          result =  pinDuoduoReplaceServiceImpl.walletQuery(reqData,this.getResultListener());
                      case 99:
                          result =  youFuReplaceServiceImpl.walletQuery(reqData,this.getResultListener());
                          break;
                      case 134:
                          result =  naTieReplaceServiceImpl.walletQuery(reqData,this.getResultListener());
                          break;
                      case 137:
                          result =  kuaiBaoReplaceServiceImpl.walletQuery(reqData,this.getResultListener());
                          break;
                      
                      default:
                    	  result =  Constant.failedMsg("不存上游余额查询通道:"+channelSource);
                          break;
	                  }
                  } catch (Exception e) {
                	  logger.error("上游余额查询失败",e);
                  }
                  if(result ==null || !result.containsKey("code") || !result.getString("code").equals(Constant.SUCCESSS) || (!result.containsKey("balance")&& !result.containsKey("T0balance") && !result.containsKey("T1balance"))){
                	  String message = result.containsKey("msg")?result.getString("msg"):"接口更新失败";
                      thirdBalanceService.updateBalance(VcOnlineThirdBalance.geThirdBalance(balance.getId(),balance.getBalanceLabel(),null,message));
                      msg.append(message);
                	  continue;
                  }
                  BigDecimal amount = new BigDecimal("0");
                  String message = "余额更新失败";
                  if(result.containsKey("balance")){
                     String balanceAmount = result.containsKey ("balance")?result.getString("balance"):"0";
                     if(StringUtils.isEmpty (balanceAmount)){
                         balanceAmount = "0";
                     }
                	  amount = new BigDecimal(balanceAmount);
                	  message = result.getString("msg");
                  }else if(result.containsKey("T0balance") && cashMode == 1){
                	  amount = new BigDecimal(result.getString("T0balance"));
                	  message = result.getString("msg");
                  }else if(result.containsKey("T1balance") && cashMode == 2){
                	  amount = new BigDecimal(result.getString("T1balance"));
                	  message = result.getString("msg");
                  }else{
                  	  message = result.containsKey("msg")?result.getString("msg"):"接口更新失败";
                  }
                  int num = thirdBalanceService.updateBalance(VcOnlineThirdBalance.geThirdBalance(balance.getId(),balance.getBalanceLabel(),amount,message));
                  if(num<1){
                      msg.append("保存余额信息失败");
                  }
			}
            return Constant.successMsg("查询余额完成["+msg+"]");
        } catch (Exception e) {
	        logger.error("上游余额查询接口异常{}",reqData, e);
	        return Constant.failedMsg( "上游余额单查询失败");
	    }
	}

    public static void main (String[] args) {
        String balanceAmount = "0";
        BigDecimal amount = new BigDecimal(balanceAmount);
        System.out.println (amount);
    }
	
	 /**
     * @描述:获取监听
     * @作者:nada
     * @时间:2017年12月19日 下午3:42:31
     */
    public ResultListener getResultListener(){
        return new ResultListener() {
            @Override
            public JSONObject successHandler(JSONObject resultData) {
                logger.info("获取余额查询监听successHandler结果:{}",resultData);
                return resultData;
            }
            @Override
            public JSONObject paddingHandler(JSONObject resultData) {
                logger.info("获取余额查询监听paddingHandler结果:{}",resultData);
                return resultData;
            }
            @Override
            public JSONObject failedHandler(JSONObject resultData) {
                logger.info("获取余额查询监听failedHandler结果:{}",resultData);
                return resultData;
            }
        };
    }

	/**
	 * @描述:验证余额查询请求入参
	 * @时间:2018年5月21日 上午9:59:00
	 */
	public JSONObject checkReqPrms(JSONObject reqData,HttpServletRequest request) {
        try {
            if(!reqData.containsKey("isMemo")){
                return Constant.failedMsg("参数为空");
            }
            if (reqData.containsKey("isMemo") && !"isMemo".equals(reqData.getString("isMemo"))) {
                return Constant.failedMsg("此接口不对外开放");
            }
            return Constant.successMsg("验证通过");
        } catch (Exception e) {
            logger.error("上游余额查询参数检查异常", e);
            return Constant.failedMsg("上游余额查询参数检查失败");
        }
	}
}
