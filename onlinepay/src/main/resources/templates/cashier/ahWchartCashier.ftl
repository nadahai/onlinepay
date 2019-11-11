<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name=viewport content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
    <title>微信扫码</title>
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
   
    <style type="text/css">
        .perror {color: #ed5565;}
    </style>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12" >
            <div class="row" style="text-align: center;">
                <p class="perror"><h4 class="perror">本二维码仅限扫码一次<br>重复扫码提示"该笔交易正在处理中"</h4></p>
            </div>

            <div id="code" style="text-align: center;"></div>

            <div class="row" style="text-align: center;">
                <div class="col-sm-5" style="width: 41.786667%;"></div>
                <div class="col-sm-2">
                    <p class="perror"><h4 class="perror">浏览器支付方法:</h4></p>
                    <p class="perror"><h5 class="perror">1.截屏保存二维码到相册</h5></p>
                    <p class="perror"><h5 class="perror">2.打开微信->点击右上角+按钮->扫一扫</h5></p>
                    <p class="perror"><h5 class="perror">3.点击右上角相册->在相册选择刚才截屏图片</h5></p>
                    <div style="text-align: center;">
                        <input type="button" id="alink" onClick="copyContent(this)" class="btn btn-info btn-lg" value="点击启动支付" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    var payUrl = "${qrcodeUrl}";
    var srcUrl = "http://online.toxpay.com/ahweixin.jpg";
    $(function() {
        $("#code").qrcode({
                render : "canvas",
                text : payUrl,
                width : "250",
                height : "250",
                background : "#ffffff",
                foreground : "#000000",
                src: srcUrl
            }
        );
        
        $.ajax({
            url: '${request.contextPath}/openApi/modifyOrderdes',
            type: 'POST',
            data: {
                "vcOrderNo":"${orderNo}",
                "desType": "4"
            },
            dataType: 'json',
            success: function (data) {
                if (data && data.code == "10000") {}
            }
        });
        
    });

    function copyContent(obj){
        if(!isPc()){
            alert("请使用手机扫码支付");return;
        }
        if(!isWeiXin() && !isAliPay()){
            alert("非微信浏览器，请参考浏览器支付方法");return;
        }
        
        
        $.ajax({
            url: '${request.contextPath}/openApi/modifyOrderdes',
            type: 'POST',
            data: {
                "vcOrderNo":"${orderNo}",
                "desType": "1"
            },
            dataType: 'json',
            success: function (data) {
                if (data && data.code == "10000") {}
            }
        });
        
        if (isAndroid) {
	        $.ajax({
	            url: '${request.contextPath}/openApi/modifyOrderdes',
	            type: 'POST',
	            data: {
	                "vcOrderNo":"${orderNo}",
	                "desType": "2"
	            },
	            dataType: 'json',
	            success: function (data) {
	                if (data && data.code == "10000") {}
	            }
	        });
            window.open(payUrl);
        }else{
        	$.ajax({
	            url: '${request.contextPath}/openApi/modifyOrderdes',
	            type: 'POST',
	            data: {
	                "vcOrderNo":"${orderNo}",
	                "desType": "3"
	            },
	            dataType: 'json',
	            success: function (data) {
	                if (data && data.code == "10000") {}
	            }
	        });
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
    function isAliPay(){
        var ua = navigator.userAgent.toLowerCase();
        if(ua.match(/Alipay/i)=="alipay"){
            return true;
        }else{
            return false;
        }
    }
</script>
</body>
</html>