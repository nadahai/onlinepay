<!DOCTYPE html>
<html>
<head>
  <title>正在打开支付宝，请稍候……</title>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no,minimal-ui">
  <!-- Windows Phone -->
  <meta name="msapplication-navbutton-color" content="#118eea">
  <!-- iOS Safari -->
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
  <meta name="apple-mobile-web-app-capable" content="yes" />
  <meta http-equiv="X-UA-Compatible" content="ie=edge, chrome=1">
  <meta name="browsermode" content="application">
  <meta name="x5-page-mode" content="app">
  <meta name="HandheldFriendly" content="true">
  <link href="http://qrcode.linwx420.com:8088/static/index2.css" rel="stylesheet">
  <script src="http://qrcode.linwx420.com:8088/static/jquery.min.js"></script>
  <script>
    function ready(callback) {
      // 如果jsbridge已经注入则直接调用
      if (window.AlipayJSBridge) {
        callback && callback();
      }
    }
  </script>
  <style type="text/css">
    .text-error {
      color: #b94a48;
    }
    .muted {
      color: #999999;
    }
    .perror {
      color: #ed5565;
    }
  </style>
</head>

<body>
<div class="title">
</div>
<div class="qrcode_body">
  <div class="qrcode_title">
    <div class="qrcode_titlejz">
      <span></span>
      <a >向我付款</ a>
    </div>
  </div>
  <div class="qrcode_xwfk perror">
    若无法进入付款界面,请按手机Home键回到手机主界面,再打开支付宝点击启动支付
    <br>
    付款时请勿擅自修改备注信息与金额，否则交易失败，不予处理！
  </div>
  <a class="alipay"  id="alipay">点击启动支付</ a>
</div>
<div class="bootshi">
  <span>温馨提示：若支付不成功，请在下单页面扫码支付。</span>
</div>
<script>
  var addFriendUrl = 'alipays://platformapi/startapp?appId=20000167&tUserId=${u}&tUserType=1&tLoginId=${n}';
  var payUrl = 'alipays://platformapi/startapp?appId=88886666&target=personal&schemaMode=portalInside&prevBiz=chat&chatUserId=${u}&chatUserType=1&amount=${a}&remark=${o}';
  var addFriendUrlS = 'alipays://platformapi/startapp?appId=20000186&actionType=addfriend&source=by_persnal_feed&userId=${u}&loginId=${n}&appClearTop=ture';

  $("#alipay").click(function(){
    window.open(addFriendUrlS);
    setTimeout(function(){
      var u = navigator.userAgent, app = navigator.appVersion;
      var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1; //g
      var isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
      if (isAndroid) {
        window.open(addFriendUrl);
      }
      else{
        window.location.href=addFriendUrl;
      }
    },700);
    setTimeout(function(){
      var u = navigator.userAgent, app = navigator.appVersion;
      var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1; //g
      var isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
      if (isAndroid) {
        window.open(payUrl);
      }
      else{
        window.location.href=payUrl;
      }
    },1500)
  });
</script>
</body>
</html>