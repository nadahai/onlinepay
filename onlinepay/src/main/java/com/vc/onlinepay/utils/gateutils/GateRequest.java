package com.vc.onlinepay.utils.gateutils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.vc.onlinepay.utils.Constant;
import java.io.Serializable;
import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GateRequest implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(GateRequest.class);

    private String merchNo;
    private String method;
    private String ipaddress;
    private String timestamp;
    private String version;
    private String format;
    private String charset;
    private String signType;
    private JSONObject content;
    private String sign;

    //初始化
    public GateRequest (String jsonStr) throws JSONException,NullPointerException,IllegalArgumentException{
        try {
            JSONObject json = JSONObject.parseObject(jsonStr);
            StringBuilder builder = new StringBuilder();
            boolean loseField = false;
            for (Field field : GateRequest.class.getDeclaredFields()) {
                if("jsonObject".equals(field.getName())|| "signSource".equals(field.getName())||"signKey".equals(field.getName())){
                    continue;
                }
                if(!json.containsKey(field.getName()) || StringUtils.isBlank(json.getString(field.getName()))){
                    loseField = true;builder.append(",").append(field.getName());
                }
            }
            if(loseField) {
                throw new IllegalArgumentException("[" + builder.toString().substring(1) + "]参数缺失！");
            }
            this.merchNo = json.getString("merchNo");
            this.method = json.getString("reqCmd");
            this.ipaddress = json.getString("ipaddress");
            this.timestamp = json.getString("timestamp");
            this.version = json.getString("version");
            this.format = json.getString("format");
            this.charset = json.getString("charset");
            this.signType = json.getString("signType");
            this.sign = json.getString("sign");
            this.content = json.getJSONObject("content");
        } catch (JSONException je) {
            throw new IllegalArgumentException("json格式错误！");
        }
    }

    /**
     * @描述:初始化设置报文请求响应编码格式
     * @时间:2017年12月18日 下午5:45:02
     */
    public static void initHttpServletRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (request != null) {
                request.setCharacterEncoding(Constant.CHART_UTF);
            }
            if (response != null) {
                response.setCharacterEncoding(Constant.CHART_UTF);
                //response.setContentType("application/json;charset=utf-8");
            }
        } catch (Exception e) {
            logger.error("初始化设置报文请求响应编码格式异常", e);
        }
    }



    public String getMerchNo() {
        return merchNo;
    }

    public void setMerchNo(String merchNo) {
        this.merchNo = merchNo;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

}
