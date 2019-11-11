<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<title>onlinepay order</title>
<head>
  <title>onlinepay</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <link type="text/css" href="static/bootstrap/css/yeepaytest.css" rel="stylesheet"/>
  <script type="text/javascript" src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
</head>
<body>
<table width="98%" style="border: solid 1px #107929" align="center" cellpadding="5" cellspacing="1">
  <tbody>
  <tr>
    <td height="15" colspan="3" bgcolor="#6BBE18">
      <span style="color: #FFFFFF">交易接口</span>
    </td>
  </tr>
  <tr>
    <td height="15" align="left" bgcolor="#FFFFEF">
      <form method="post" action="${domainName}/${actualName}/xpayapi">
        <table>
          <tr>
            <td>请求接口名称:</td>
            <td><input type="text" name="reqCmd" value="req.trade.order"/></td>
          </tr>
          <tr>
            <td>请求商户号:</td>
            <td><input type="text" name="merchNo" value="999941000001"/></td>
          </tr>
          <tr>
            <td>请求编码格式:</td>
            <td><input type="text" name="charset" value="UTF-8"/></td>
          </tr>
          <tr>
            <td>签名算法类型:</td>
            <td><input type="text" name="signType" value="MD5"/></td>
          </tr>
          <tr>
            <td>请求公网地址:</td>
            <td><input type="text" name="reqIp" value="127.0.0.1"/></td>
          </tr>

          <tr>
            <td>支付类型:</td>
            <td>
              <select name="payType" style="width:160px;">
                <option value="1">微信扫码支付</option>
                <option value="2">支付宝扫码支付</option>
                <option value="3">QQ钱包扫码支付</option>
                <option value="4">京东钱包扫码支付</option>
                <option value="5">微信公众号支付</option>
                <option value="6">支付宝公众号支付</option>
                <option value="7">快捷支付</option>
                <option value="8">银联二维码支付</option>
                <option value="9">网关支付</option>
                <option value="10">支付宝H5支付</option>
                <option value="11">QQH5支付</option>
                <option value="12">微信H5支付</option>
                <option value="13">银联直冲支付</option>
                <option value="14">快捷直冲支付</option>
                <option value="15">手机网关支付</option>
                <option value="16">京东h5支付</option>
              </select>
            </td>
          <tr>
          <tr>
            <td>商户订单号:</td>
            <td><input type="text" id="tradeNo" name="tradeNo" value="2019343434343434"/></td>
          </tr>
          <tr>
            <td>币种单位:</td>
            <td><input type="text" name="currency" value="CNY"/></td>
          </tr>
          <tr>
            <td>金额(元):</td>
            <td><input type="text" name="amount" value="18.66"/></td>
          </tr>
          <tr>
            <td>商户用户ID:</td>
            <td><input type="text" name="userId" value="106621"/></td>
          </tr>
          <tr>
            <td>异步通知url:</td>
            <td><input type="text" name="notifyUrl" value="http://api.test/notify"/></td>
          </tr>
          <tr>
            <td>同步响应url:</td>
            <td><input type="text" name="returnUrl" value="20150320010101001"/></td>
          </tr>
          <tr>
            <td>商品名称:</td>
            <td><input type="text" name="goodsName" value="一条裤子"/></td>
          </tr>
          <tr>
            <td>商品描述:</td>
            <td><input type="text" name="goodsDesc" value="灰色牛仔裤"/></td>
          </tr>
          <tr>
            <td>备注信息:</td>
            <td><input type="text" name="remark" value="淘宝网购买"/></td>
          </tr>
          <tr>
            <td>银行名称:</td>
            <td><input type="text" name="bankName" value=""/></td>
          </tr>
          <tr>
            <td>银行卡号:</td>
            <td><input type="text" name="bankCard" value=""/></td>
          </tr>
          <tr>
            <td>签名:</td>
            <td><input type="text" name="sign" value="84284882764941bdeb8601b5c4828cff"/></td>
          </tr>
          <tr>
            <td align="center" colspan="2">
              <input type="submit" align="center" value="提交交易订单"/>
            </td>
          </tr>
        </table>
      </form>
    </td>
  </tr>
  </tbody>
</table>
<script type="text/javascript">
  $(function() {
    var timestamp = (new Date()).getTime();
    debugger
    $("#tradeNo").val(timestamp);
  });
</script>
</body>
</html>