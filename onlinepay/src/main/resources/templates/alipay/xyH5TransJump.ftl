<!doctype html>
<html lang="zh-cmn-hans">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no,minimal-ui">
    <meta name="msapplication-navbutton-color" content="#118eea">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta http-equiv="X-UA-Compatible" content="ie=edge, chrome=1">
    <meta name="browsermode" content="application">
    <meta name="x5-page-mode" content="app">
    <meta name="HandheldFriendly" content="true">
    <link rel="stylesheet" href="${request.contextPath}/static/cashier/css/alipay3.css?t=201">
    <script src="${request.contextPath}/static/js/jquery.min.js"></script>
    <title>支付宝付款</title>
</head>
<body>
    <div class="title">
        <div>
        </div>
    </div>
    <div class="qrcode_body">
        <div class="qrcode_title">
            <div class="qrcode_titlejz">
                <span></span>
                <a href="javascript:void(0);">支付宝</a>
            </div>
        </div>
        <div class="wrap">
            <div class="load_qrcode">
                <img src="${qrImgUrl}" class="load_qrcodeImg" >
            </div>
        </div>
    </div>
    <div class="bootshi">
        <span>温馨提示：</span>
        <p>若未自动唤醒支付宝，请保存二维码从支付宝扫一扫识别</p>
    </div>

    <script>
        $(document).ready(function () {
            if(browserRedirect()){
                if (/AlipayClient/.test(window.navigator.userAgent)) {
                    var wapUrl  = null;
                    if(isIOS()){
                        wapUrl = 'alipays://platformapi/startapp?appId=20000123&actionType=scan&s=money&u=${u}&a=${a}&m=${m}';
                    }else{
                        wapUrl = 'alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={"a": "${a}","u": "${u}","m": "${m}"}';
                    }
                    window.location=wapUrl;
                }else{
                    <#--var url =window.location.href+"/alipayJump?a=${a}&u=${u}&m=${m}";-->
                    <#--var mainUrl ="https://ds.alipay.com/?from=mobilecodec&scheme=";-->
                    <#--var alipaysUrl ="alipays://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=";-->
                    <#--var orderUrl = encodeURIComponent(url);-->
                    <#--// + '_s%253Dweb-other'-->
                    <#--window.location.href = mainUrl + encodeURIComponent(alipaysUrl + orderUrl);-->

                    decodeURIComponent("");
                }
            }else{
                alert("请用手机支付");
            }
        });

        function browserRedirect() {
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

        function isIOS(){
            var u = navigator.userAgent;
            var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
            var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
            if(isiOS){
                return true;
            }else{
                return false;
            }

        }
    </script>



</body>
</html>
