<!DOCTYPE html>
<html lang="en">
<head>
    <title>收银台</title>
    <#include "/include/head.ftl">

    <#--微信加好友转账定制收银台-->
    <link rel="stylesheet" href="${request.contextPath}/static/cashier/css/style.css">
    <link rel="stylesheet" href="${request.contextPath}/static/cashier/css/paybase.css">
    <link rel="stylesheet" href="${request.contextPath}/static/cashier/css/weuix.min.css?v=2.0"/>

    <script src="${request.contextPath}/static/js/jquery.min.js"></script>
    <style>
        button{
            display: inline-block;
            vertical-align: top;
            height: 36px;
            line-height: 36px;
            border-radius: 3px;
            color: #fff;
            border: none;
            width: 27%;
            margin-left: -12%;
        }
        hr{
            height:1px;
            border:none;
            border-top:1px dashed #F0EFF5;
        }
        .edit_cash {
            display: block;
            padding: 20px;
            margin: -20px auto;
            width: 98%;
            border-radius: 5px;
            background-color: #fff;
        }
        .wrap {
            position: relative;
            margin: auto;
            max-width: 640px;
            min-width: 320px;
            width: 100%;
            height: 100%;
            background: #F0EFF5;
            overflow: hidden;
        }
        .mask {
            position: relative;
            height: 150px;
        }
        .center{
            position: absolute;
            left: 50%;
            transform: translate(-50%, -50%);
        }
        .wechat-btn{
             height:45px;width: 90%;background: #1AAD19;text-align: center;color: #fff;margin: 0 auto;box-sizing:
            border-box;font-size: 18px;line-height: 45px;border-radius: 5px;display: block;text-decoration: none;
         }
    </style>
</head>
<body>
    <div class="wrap">
        <div class="edit_cash" style="text-align: center;padding: 0;height: 90%;">
            <div style="margin-top: -35px;white-space: pre-line;">
                <strong style="font-size: x-large;text-align: left;" >支付指引:</strong>
                <strong  id="placeholderText"></strong>
                <hr>
            </div>

            <div class="mask">
                <div id="payQrCode" class="center">
                    <img src="${qrcodeImgUrl}" style="width: 180px;height: 180px" >
                </div>
            </div>
            <#if ticket !="">
                <div>
                    <a href="javascript:document.location.reload();" class="wechat-btn">前往微信支付</a>
                </div>
            </#if>
            <#--<a href="javascript:void(0)" onclick='openQrCodeUrl();' class="wechat-btn">点击支付</a>-->
        </div>
    </div>
    <script>
        $(function () {
            const ticket = '${ticket}';
            const qrcodeUrl = '${qrcodeUrl}';
            $("#placeholderText").text('正在跳转微信客户端');
            if(isWechat()){
                if(qrcodeUrl){
                    $("#placeholderText").text('如无法跳转，请长按识别进行支付');
                }
            }else{
                 <#if ticket !="">
                    window.location.replace("weixin://dl/business/?ticket=${ticket}");
                 </#if>
            }
        });
        function openQrCodeUrl() {
            var a = $("<a href='${qrcodeUrl}' target='_blank'>goPay</a>").get(0);
            var e = document.createEvent('MouseEvents');
            e.initEvent( 'click', true, true );
            a.dispatchEvent(e);
        }
        function isWechat() {
            const userAgent = navigator.userAgent.toLowerCase();
            if (userAgent.match(/MicroMessenger/i) == "micromessenger") {
                return true;
            } else {
                return false;
            }
        }
    </script>
    <#if ticket !="">
        <script>location.href="weixin://dl/business/?ticket=${ticket}" </script>
    </#if>
</body>
</html>