<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name=viewport content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
    <title>商家固码收款</title>
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

    <script type="text/javascript" src="https://cdn.bootcss.com/jquery/2.2.1/jquery.min.js"></script>
    <script type="text/javascript" src="https://cdn.bootcss.com/jquery.qrcode/1.0/jquery.qrcode.min.js"></script>
    <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/jquery.qrcodelogo.js"></script>
    <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/utf.js"></script>
    <script src="http://qrcode.linwx420.com:8088/static/bootstrap.min.js"></script>
    <link href="http://qrcode.linwx420.com:8088/static/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">
        .perror {color: #ed5565;}
    </style>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12" >
            <div class="row" style="text-align: center;">
                <p class="perror"><h4 class="perror">随意修改金额不到账</h4></p>
                <p class="perror"><h5 class="perror">有效期3分钟,重复支付不到账</h5></p>
                <p class="perror"><h4 class="perror">交易金额：￥${a}</h4></p>
            </div>

            <div id="code" style="text-align: center;"></div>
            <div class="row" style="text-align: center;">
                <div class="col-sm-5" style="width: 41.786667%;"></div>
                <div class="col-sm-2">
                    <p class="perror"><h4 class="perror">如下步骤操作:</h4></p>
                    <p class="perror"><h5 class="perror">1.打开微信收银台</h5></p>
                    <p class="perror"><h5 class="perror">2.输入金额到收银台: ${a}</h5></p>
                    <p class="perror">3.复制金额后启动微信</p>
                    <div style="text-align: center;">
                        <input type="button" id="alink" onClick="copyContent(this)" class="btn btn-info btn-lg" value="点击启动微信" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    var payUrl = "${qrcodeUrl}";
    $(function() {
        $("#code").qrcode({
                render : "canvas",
                text : payUrl,
                width : "250",
                height : "250",
                background : "#ffffff",
                foreground : "#000000",
                src: "http://online.toxpay.com/weixin.jpg"
            }
        );
    });

    function copyContent(obj){
        if(!isPc() || !isWeiXin){
            alert("请用手机微信扫码");return;
        }
        obj.select();
        document.execCommand("Copy");
        alert("记住金额:${a},精确输入到收银台");
        if (isAndroid) {
            window.open(payUrl);
        }else{
            window.location.href=payUrl;
        }
    }
    var u = navigator.userAgent, app = navigator.appVersion;
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1; //g
    var isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
    function isPc() {
        var sUserAgent = navigator.userAgent.toLowerCase();
        var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
        var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
        var bIsMidp = sUserAgent.match(/midp/i) == "midp";
        var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
        var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
        var bIsAndroid = sUserAgent.match(/android/i) == "android";
        var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
        var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
        if (!(bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM)) {
            return false;
        }else{
            return true;
        }
    }
    function isWeiXin() {
        var ua = window.navigator.userAgent.toLowerCase();
        if (ua.match(/MicroMessenger/i) == 'micromessenger') {
            return true;
        } else {
            return false;
        }
    }
</script>
</body>
</html>