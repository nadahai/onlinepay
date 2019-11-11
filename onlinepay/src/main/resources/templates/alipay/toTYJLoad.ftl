<!DOCTYPE html>
<html>
<head>
  <title>正在等待支付宝授权，请稍候……</title>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no,minimal-ui">
  <!-- Windows Phone -->
  <meta name="msapplication-navbutton-color" content="#118eea">
  <!-- iOS Safari -->
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
  <meta name="apple-mobile-web-app-capable" content="yes"/>
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
      } else {
        // 如果没有注入则监听注入的事件
        //window.location.href = 'http://fixed.linwx420.com:8088/scan/noAlipay'
      }
    }
  </script>
</head>
<body>
<div class="title">
</div>
<div class="qrcode_body">
	<br>
  <a class="alipay" id="alipay">授权中。。。</a>
</div>



<script language="javascript" type="text/javascript">
  var payUrl = "${openUrl}";
  
  $(function() {
        getRunTime(3);
    });
  
  var getRunTime = function (t) {
    let m = t || 180;
    setInterval(function () {
      if (m > 0) {
        $("#alipay").text('等待支付宝授权,请等待(' + m-- + ')秒...')
      } else {
        m = t;
        getAjax();
      }
    }, 1000)
  };

  var getAjax = function () {
    setTimeout(function () {
      var u = navigator.userAgent, app = navigator.appVersion;
      var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1; //g
      var isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
      
      if (isAndroid) {
        window.open(payUrl);
      } else {
        window.location.href = payUrl;
      }
      
    }, 200)
  }
  
</script>
</body>
</html>
