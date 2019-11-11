/**
 * @类名称:MainController.java
 * @时间:2017年10月27日下午5:55:40
 * @作者:lihai
 * @版权:版权所有 Copyright (c) 2017
 */
package com.vc.onlinepay.web.gate;

import com.vc.onlinepay.persistent.entity.online.VcOnlineOrderMade;
import com.vc.onlinepay.persistent.service.online.VcOnlineOrderMadeService;
import com.vc.onlinepay.utils.Constant;
import com.vc.onlinepay.utils.StringUtil;
import com.vc.onlinepay.utils.happypay.BiuldUtils;
import com.vc.onlinepay.utils.http.HttpRequestTools;
import com.vc.onlinepay.utils.ninepie.HiDesUtils;
import com.vc.onlinepay.web.base.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Map;


/**
 * @ClassName:  IndexController
 * @Description: api接口调用
 * @author: lihai
 * @date: 2018年4月11日 下午2:36:50
 * @Copyright: 2018 www.guigu.com Inc. All rights reserved.
 * 注意：本内容仅限于本信息技术股份有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Controller
@RequestMapping("/openauth")
public class AlipayOpenauthController extends BaseController {

    public static final Logger logger = LoggerFactory.getLogger(AlipayOpenauthController.class);

    @Autowired
    private VcOnlineOrderMadeService onlineOrderMadeService;
    //鑫鹏
    //private static String ALIPAY_APP_ID = "2019022463364107";
    //private static String ALIPAY_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCq8uyOMszizxBNMai1OtOil+IWhbBl2yd2CqfL3YUqUANI/yfuQ6fllBmjbVQrz4j3pHe1qyw0B3PJ97La7PGr0yuqQgu/NnBzvGvqO4CNApnXBZjV1Xu/aNBWh9XFENf4ORU6nGNhj9jr4pJrN6l+5uJ8T5kYWqfHcUUYDMsbtu1YjQpSq/TIXetNKcZnuYsq5Qjera01odrJbhhDAoBs3RGhCWaBxv3MSFCz9xNumCmsoIQu29JhVz4/B55OdSebrTfATEzOwGxuFs4T0KXJj3dL10bQqHJqqDMnjaoJStart1F6mrgLkEKVs2aP09cVdbC1y/miCSI1y0diTgTdAgMBAAECggEATvEPPDtJUhO8u2kLN2WLBbKNvUjPRLoHZwNUqVgKfpekbLknf2fOyL2zeTyree/EmFdi0InTR9OJLOMtvNteXrKNn3oQYqSJGWkRjIEdxABHenwjL9v94U5NpyfjF7XHheEWZJKDcjIzQfrHEqwJoYiNXkqDsDNs9zTfa1O9F3aE8QJmYH5JFUT+k38mqR5DTMVgkLHwPzU8pdoEi9HGSuTZ2odFKRZ5/k/9Vu04hc5/fnUFIUomEiu3V8R8FzJjGrv2aCtGT5q5p1vGClYXsJEeeSEqX42o4CQEmBGPTrv0mAA5IeqU23hs9ktnx+8WAa+a/Ldpa1utdt2kfwaVIQKBgQD/BVWh9RPiAWUHUs60+Z0D/8bIdHZW1rKI1e5DKLLxc7FfBnuP2IjdkIDzXx96IqoM40lGaX1edroTgQB/DOuiuPKv3J0JJ2VNu82WNe+ObUmWUnhJTCBm/hLZKTsXi9KHiUPM4HtxTL3Dkud++KVUUVH6F8COGQP+nuiE0IHrXwKBgQCrmvQUfx6XHKkBi8zfazpEFKVHMsW1pGMBVCUh/tm5omPBxG7ZBs0WyWXRzXprl8w7Zky4z7qPBBxgPZ+NdhAY+XlalRqr18y6qSwS7ysJ57yDtEgnzlxB/FfdtvHxbiYPfX7x33Qld7kMUbKt+WHSreVZQyRWvJwskXIgdSV1QwKBgQCUEeDanJXiz7R9QBNM+PG19LjSguyDFz2qPayNyf+8OdRuvDDaIHu3ScPVixGXtLDPsthEzdNBGeaIlIpZOoNGg+RFP+7d9cXYEIcaBE8Hf2UOpuu4gz79DeCbvljVHxYqJAT93AlQi6JS/+Tx0CUOg/j5IPloiBXNrS6MjxQgOQKBgAdbm3+Ne8hK6EwyrFQgCt2EbRnCaYvCQqR58SWmAbvd5J0YSRBxJDYH6J+4Sbl3RsB9QGjkL0GWkYjm24J7P3FysOtbXUtk81hFjKg7LQM9tm2HO1jJllcV9MaC45jQej1LyjegtyAsI/kNP7YJ7VHVNvI+2L4HVELs8ZHGtBZ1AoGAJSXQ4l48ore33DmQ2HbZO6Yl+zgR4SW+2yjhaUTQso9JyCNEUMVokJOIfR9Ufos8BT9iOWZZS3UJLgQCgxWr47XoPCwOdRU2qcEqnmrqRAg72EAkvP35GiYhIcUFiI3gMVGme1Z35asA0zeP/1N467w4huxTO6KMDJNHhmf3vUk=";

    private static String ALIPAY_APP_ID = "2019022663445033";
    private static String ALIPAY_PRIVATE_KEY =   "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCW4dPoGLkBVQS//n7NvmxWqQq/JQXPDvO86//xtxOEvigTPv32tmwDNGV8ha6K3aM92FIci0jq/W8+kzVqgV+DaRKvZgKABKhnuRwF+lqZXBAUn3wb6o5+BYMz0LDTADkD1TXrKXP0lAboLuQ/e8MjpR82QjMfIiI+RIJe90exIrxYGaQ9g0CBMvBHtVU5XUH4/6YviiQMGy9+lJcY70cHjt0tz1fKXIvgqfH0xXt3IJNwtdRW1BH7jGj3ycZ4O67EJZjALE/3J83PsKEeDEmm7Nj1C0+lz3SSPlqZuU0IdqVOljDHl2chmSQ2RpcsLt8L1NhZWHwvAwLK/0DtpDmfAgMBAAECggEAfLXR6iAtIn28PFlFRBemAoccW9tlDr0EF15Vodu5pVlrVGUXkwk7A/cV/zObjiod2GtpizBRJ/IsNeOFHjAq2zpI+HLvlBLFg14MXKCOq/3dSL593bEk7+LsjDJtRSoE9jcPpv0PW9PSVa0Ueah5JYhA4R8clBophaAoxgfL2QtfsNwkUooYwrn5iC6wJChwnNu4ycDTucUE9JUdNJB8C9657+G5TINPqqp+C1J64TxRDf2GE4Qlp1BVU0hKf7er+2fE3NWx/w238yfbTRj7813UkM6qGf1W0a87I+XMvBzG/pQfhzvIURn0IPd97FSo07vJ2+Ya8aQMooTryink8QKBgQDXbFjgaBhPb4US8PBmyrJ6BKsxr9gyizKEpZc7RhRxn1F46HreXP7WpBe1mGxhgj103XMEnJ++SO24gkfXeziRJYSQUcJ9Bi3iohSKKny4JfK6TXULGyRQGKMg7s673QyoTMJZEnLOPRi44SiALyjWSMU4gKf/cMq8LE1mAQDfaQKBgQCzTVNkNdWoLUU/74oPBe69xy01LaPmetUeDwfVF9Jznbvh9ZX9RBPHcOpEdj5ohHS13Zon+Fl+6N5+/VVLEsxJYa8yAeJO49bmPEQMOntXUPZginNOO0QIWJczF95dzIYitFEPleXyi15ys1wIiYmZZda4ndsxE7n6b1VX3l83xwKBgCSp6g53aPvMDSgGHcRA51NhDAnu1ar0ieqNjEmoSb500Rb06kSCK1U4pFhh0sBdvDvxnKvCRGXMX7kxYyzHaf86FW/CmolQzepuj3RcetGUk9UlbbAwNGP1bX4jZsgOfSL/vWwPeb6kI1sD5zK51Ad0ZyyQqQcOmVKhb6LWtOFRAoGBAJoZWhNlJUSN69dgQvJYcfBO726l1Bhw9xsHZf7ho3DUXrZ7wDXUPX+Kbvz59vcS58+qLiQuEJS/BQJ1LcoA1Ow4dT7wWZTdwkNMBlvQI1NO9QDR3PmefAEESqaR2/4XmizXdleDyAYle73uoql/s2/QE7PFj63lO1CeQOXs6z7fAoGAOhLiEYBk5SF/qzKTX4RSkHQutK4ZTRafLZjh6B5QbK8WQYk8eqKvU4+zAs5DSohyVVs6pUS66cTBKelubaBcGekK9lSFvmUCAnP7AOYj+RaSw3l0Zg405RjOfGU4Ng2U0R9JX02IjilFtW1zdUWyKGr6Lhfhr0zBeK4deVq82hI=";


    /**
     * @描述:仅获取UserID返回
     * @作者:nada
     * @时间:2019/3/11
     **/
    @RequestMapping("returnAauth")
    public ModelAndView returnAlipayCode(HttpServletRequest request,HttpServletResponse response){
        ModelAndView mode = new ModelAndView("failure");
        try {
            logger.info ("获取Openauth自动回调");
            request.setCharacterEncoding(Constant.CHART_UTF);
            response.setCharacterEncoding(Constant.CHART_UTF);
            String code = request.getParameter("auth_code");
            String userid = request.getParameter("userid");
            String no = request.getParameter("o");
            logger.info("returnAlipayCode:code:{},userid:{},orderNo:{}",code,userid,no);
            if(StringUtils.isEmpty (userid)){
                userid = BiuldUtils.getAlipayUserId(code,ALIPAY_APP_ID,ALIPAY_PRIVATE_KEY);
            }
            if(StringUtil.isEmpty(no)){
                mode.addObject("message","订单信息错误");
                return mode;
            }
            String orderNo = HiDesUtils.desDeCode(no);
            if(StringUtil.isEmpty(orderNo) || "0".equalsIgnoreCase(orderNo)){
                mode.addObject("message","解析订单错误");
                return mode;
            }
            //验证订单有效性
            VcOnlineOrderMade made = onlineOrderMadeService.findOrderByOrderNo(orderNo);
            if (made == null) {
                mode.addObject("message","订单获取为空");
                return mode;
            }
            logger.info ("解析支付链接重定向至:{}",made.getQrcodeUrl());
            return  new ModelAndView("redirect:".concat(made.getQrcodeUrl()));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error ("获取用户支付异常",e);
            return mode;
        }
    }
}
