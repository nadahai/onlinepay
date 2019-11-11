<html class="normal "><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <title>支付宝</title>
    <#include "/include/head.ftl">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="format-detection" content="telephone=no">
    <meta name="format-detection" content="email=no">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0">
    <script src="${request.contextPath}/static/js/jquery.min.js"></script>
    <#--<script src="https://cdn.bootcss.com/crypto-js/3.1.9-1/crypto-js.min.js"></script>-->
    <meta http-equiv="refresh" content="30;url=alipays://platformapi/startapp?appId=20000123&amp;actionType=scan&amp;u=${userId}&amp;a=${amount}&amp;m=${orderId}&amp;biz_data={&quot;s&quot;: &quot;money&quot;,&quot;u&quot;:&quot;${userId}&quot;,&quot;a&quot;:&quot;${amount}&quot;,&quot;m&quot;:&quot;${orderId}&quot;}">
    <script>
        var count = 3;
        var fn = function(){
            var userId = "${userId}";var amount = "${amount}";var orderId = "${orderId}";
            var a = {actionType:"scan",u:userId,a:amount,m:orderId,biz_data:{s:"money",u:userId,a:amount,m:orderId}};
            AlipayJSBridge.call("startApp", {appId: "20000123",param: a}, function(e) {
                if('4' == e.errorCode){count = 30;}
            });
        }
        function ready(a) {
            window.AlipayJSBridge ? a && a() : document.addEventListener("AlipayJSBridgeReady", a, false)
        }
        ready(function () {
            fn();
            AlipayJSBridge.call("hideOptionMenu");
        });
        function exitAlipay() {
            var userAgent = navigator.userAgent.toLowerCase();
            if(userAgent.match(/android/i) == "android"){
                AlipayJSBridge.call('popWindow');
            }
        }

        var interval = setInterval("downloadBtncount()",1000);
        function downloadBtncount() {
            count--;
            $("#J_downloadBtn").text('点击支付('+count+')');
            if(count < 0){
                $("#J_downloadBtn").text('如无法自动跳转，请点击立即支付');
                $("#J_downloadBtn").removeAttr("disabled");
                clearInterval(interval);
                exitAlipay();
            }
        }
    </script>
    <style>
        *,
        :before,
        :after {
            -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
        }
        body, div, dl, dt, dd, ul, ol, li, h1, h2, h3, h4, h5, h6, form, fieldset, legend, input, textarea, p, blockquote,button, th, td {
            margin: 0;
            padding: 0;
        }

        table {
            border-collapse: collapse;
            border-spacing: 0;
        }

        fieldset, img {
            border: 0;
        }

        li {
            list-style: none;
        }

        caption, th {
            text-align: left;
        }

        q:before, q:after {
            content: "";
        }

        input:password {
            ime-mode: disabled;
        }

        :focus {
            outline: 0;
        }

        html, body {
            text-align: center;
            -webkit-user-select: none;
            user-select: none;
            font-family: "Helvetica Neue", Helvetica, STHeiTi, sans-serif;
            font-size: 12px;
            line-height: 1.5;
            text-align: center;
        }

        html {
            background: #181c27;
        }

        .download-cover {
            display: block;
            height: 320px;
            background-position: center 0;
            background-repeat: no-repeat;
            -webkit-background-size: 320px auto;
            -moz-background-size: 320px auto;
            -ms-background-size: 320px auto;
            -o-background-size: 320px auto;
            background-size: 320px auto;
            margin: 0 auto;
            overflow: hidden;
        }

        .download-cover .download-cover-slogan,
        .download-cover .download-cover-picture {
            display: none;
        }

        .download-interaction {
            margin-top: 20px;
            height: 42px;
            padding-bottom: 20px;

        }

        .download-interaction .download-button {
            display: none;
            text-decoration: none;
            font-size: 16px;
            color: #ffffff;
            letter-spacing: 2px;
            margin: 0 48px;
            background: #181c27;
            height: 42px;
            line-height: 42px;
            text-align: center;
            border: 1px solid #7f7f87;
            border-top-left-radius: 2px;
            border-top-right-radius: 2px;
            border-bottom-left-radius: 2px;
            border-bottom-right-radius: 2px;
            -webkit-background-clip: padding-box;
            background-clip: padding-box;
        }

        .download-interaction .download-opening,
        .download-interaction .download-asking {
            display: none;
            color: #fff;
            font-size: 15px;
        }

        .download-interaction.download-interaction-asking .download-asking,
        .download-interaction.download-interaction-opening .download-opening,
        .download-interaction.download-interaction-button .download-button {
            display: block;
        }

        .download-putcenter,
        .copyright {
            font-size: 12px;
            color: #999;
            text-align: center;
        }
        /*.base-info{*/
        /*position: absolute;*/
        /*bottom: 5px;*/
        /*width: 100%;*/
        /*}*/

        .download-putcenter {
            padding-top: 10px;
        }

        .download-putcenter .version,
        .download-putcenter .date,
        .download-putcenter .size {
            margin-left: 3px;
        }

        .copyright {
            padding-bottom: 10px;
        }

        a,button {
            color: #0af;
            text-decoration: none;
        }
        .normal .download-cover {
            background-image: url("${request.contextPath}/static/images/qr_alipay.png");
        }

        html {
            background-color: #019fe8;
        }

        a {
            color: #8cffff;
        }

        .download-interaction .download-button {
            background: #ff8f00;
            border: 1px solid #fff;
        }
        .download-button{
            text-align: center;
        }
        .hide-button{
            display: none;!important;
        }
        .download-putcenter, .copyright {
            color: #fff;
        }
    </style>
</head>
<body ryt14421="1">

<div class="download-view-wrap" id="downloadViewWrap">
    <div class="wrap-view-addon-1"></div>
    <div class="wrap-view-addon-2"></div>
    <div class="wrap-view-addon-3"></div>
    <div class="wrap-view-addon-4"></div>
    <div class="download-inner-view" id="downloadInnerView">
        <div class="inner-view-addon-1"></div>
        <div class="inner-view-addon-2"></div>
        <div class="inner-view-addon-3"></div>
        <div class="inner-view-addon-4"></div>
        <div class="download-view" id="downloadView">
            <div class="download-view-addon-1"></div>
            <div class="download-view-addon-2"></div>
            <div class="download-view-addon-3"></div>
            <div class="download-view-addon-4"></div>
            <div class="download-cover" id="downloadCover" style="background-image: url(&quot;${request.contextPath}/static/images/qr_alipay.png&quot;);"
                 static="" payapi="" images="" >
                <div class="download-cover-logo" id="downloadCoverLogo"></div>
                <div class="download-cover-slogan" id="downloadCoverSlogan"></div>
                <div class="download-cover-picture" id="downloadCoverPicture">
                    <div class="download-cover-picture-1"></div>
                    <div class="download-cover-picture-2"></div>
                    <div class="download-cover-picture-3"></div>
                    <div class="download-cover-picture-4"></div>
                </div>
            </div>
            <div class="download-view-addon-6" style="padding-left: 20px;padding-right: 20px;">
                <#--<img src="${qrImgUrl}">-->
                <br>
                <strong style="color:yellow;font-size: 14px;text-align: center;">${remarks}</strong>
            </div>
            <div id="J_downloadInteraction" class="download-interaction download-interaction-button">
                <div class="inner-interaction" style="text-align: center">
                    <button id="J_downloadBtn" disabled="disabled" class="download-button" style="width:70%;display: none;" onclick="goUrl()">点击支付(30)</button>
                    <br>
                </div>
            </div>

        </div>
    </div>
</div>

<div class="base-info">
    <p class="copyright">版权所有 © 2004 - 2018</p>
</div>

<script>
    if(isMobile()){
        $("#J_downloadBtn").show();
    }
    function goUrl() {
        window.location.replace("alipays:\/\/platformapi\/startapp?appId=20000123\x26actionType=scan\x26u=${userId}\x26a=${amount}\x26m=${orderId}\x26biz_data={\x22s\x22: \x22money\x22,\x22u\x22:\x22${userId}\x22,\x22a\x22:\x22${amount}\x22,\x22m\x22:\x22${orderId}\x22}");
        $("#J_downloadBtn").attr("disabled","disabled");
        exitAlipay();
    }
    function isMobile() {
        var userAgent = navigator.userAgent.toLowerCase();
        if (userAgent.match(/iphone/i) == "iphone"||userAgent.match(/ipad/i) == "ipad"||userAgent.match(/android/i) == "android"||userAgent.match(/alipay/i) == "alipay") {
            return true;
        } else {
            return false;
        }
    }
</script>
</body>
</html>
