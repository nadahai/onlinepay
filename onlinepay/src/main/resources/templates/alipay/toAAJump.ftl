<!doctype html>
<html lang="zh-cmn-hans">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <title>支付宝付款</title>
</head>
<body>
<script src="${request.contextPath}/static/js/jquery.min.js"></script>
<script>
    var wapUrl  = '${qrcodeUrl}';
    $(document).ready(function () {
        if(isPc()){
            if (/AlipayClient/.test(window.navigator.userAgent)) {
                window.location=wapUrl;
            }else{
                window.location.href = "https://ds.alipay.com/?from=mobilecodec&scheme=" + encodeURIComponent(wapUrl);
            }
        }else{
            alert("请用手机支付");
        }
    });

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
</script>
</body>
</html>
