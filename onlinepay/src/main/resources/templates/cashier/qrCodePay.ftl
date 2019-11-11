<!DOCTYPE html>
<html lang="en">
<head>
    <title>收银台</title>
    <#include "/include/head.ftl">
    <link rel="stylesheet" href="${request.contextPath}/static/cashier/css/style.css">
    <link rel="stylesheet" href="${request.contextPath}/static/cashier/css/paybase.css">
    <link href="${request.contextPath}/static/css/layer.css" rel="stylesheet">
    <link rel="stylesheet" href="${request.contextPath}/static/cashier/css/weuix.min.css?v=2.0"/>
    <script src="${request.contextPath}/static/js/jquery.min.js"></script>
    <script src="${request.contextPath}/static/js/layer.js"></script>
    <script src="${request.contextPath}/static/js/jquery.qrcode.min.js"></script>
    <script src="${request.contextPath}/static/cashier/js/common.js"></script>
    <script src="${request.contextPath}/static/cashier/js/zepto.min.js"></script>
    <script src="http://pv.sohu.com/cityjson?ie=utf-8"></script>
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
            transform: translate(-50%, -50%)
        }
    </style>
</head>
<body>
    <script>
        var merchNo="${merchInfo.merchNo?c}";
        var amount="${amount}";
        var clientIP = returnCitySN["cip"];
        var qrCodeHtml;

        //扫码所需参数
        var payChannel="Other";
        var service="010800";
        var payType="8";
        //H5所需参数
        var h5PayType="12";
        var payTypeCode="30";
        $(function () {
            init();
            openPay();
        })
        function openPay() {
            var cmdcode="${cmd}";
            if(merchNo=='999941000001'){
                invokeScanQrcodePay('8','010800');return;
            }
            if(cmdcode=='scan'){
                invokeScanPay();return;
            }
            if(cmdcode=='qrcode'){
                invokeH5Pay();return;
            }
            layer.open({
                content: '请选择支付方式',
                shadeClose: false,
                btn: ['扫码支付', 'H5支付'],
                yes: function(index){
                    invokeScanPay();
                    layer.close(index);
                },
                no: function(index){
                    invokeH5Pay();
                    layer.close(index);
                }
            });
        }
        //初始化
        function init() {
            if(isQQ()){
                payChannel="QQ";
                service="0015";
                payType="3";
                //
                h5PayType="11";
                payTypeCode="31";
                isFrame="1";
            }
            if(isWechat()){
                payChannel="WXP";
                service="0002";
                payType="1";
                //
                h5PayType="12";
                payTypeCode="30";
                isFrame="1";
            }
            if(isAlipay()){
                payChannel="ALP";
                service="0010";
                payType="2";
                //
                h5PayType="10";
                payTypeCode="22";
            }
            if(isJdpay()){
                service="010700";
                payType="4";
            }
        }

        //扫码支付执行
        function invokeScanPay() {
            var payIng = layer.open({type: 2, content: '正在发起支付，请稍后...', shadeClose: false});
            $.ajax({
                url: 'cashier',
                type: 'POST',
                data: {
                    "cmd":"scan",
                    "amount": amount,
                    "payChannel":payChannel,
                    "corpOrg":payChannel,
                    "service":service,
                    "merchantId": merchNo,
                    "orderId":"${orderId}",
                    "payType":payType,
                    "remark":"固定码收款"
                },
                dataType: 'json',
                success: function (data) {
                    layer.close(payIng);
                    if (data) {
                        $.ajax({
                            url: 'scanPayApi',
                            type: 'POST',
                            data: data,
                            dataType: 'json',
                            success: function (obj) {
                                console.log(obj);
                                if (obj && obj.code == "10000") {
                                    location.href=obj.bankUrl;
                                } else {
                                    layer.close(payIng);
                                    layer.open({content: obj.message, skin: 'msg', time: 10});
                                }
                            }
                        });
                    } else {
                        layer.open({content: data.message, skin: 'msg', time: 10});
                        openPay()
                    }
                }
            });
        }

        //H5支付入口
        function invokeH5Pay() {
            var h5PayIng = layer.open({type: 2, content: '正在发起支付，请稍后...', shadeClose: false});
            $.ajax({
                url: 'cashier',
                type: 'POST',
                data: {
                    "cmd":"H5",
                    "amount": amount,
                    "payTypeCode":payTypeCode,
                    "settleType":"1",
                    "merchantId": "${merchInfo.merchNo?c}",
                    "payType":h5PayType,
                    "remark":"固定码收款"
                },
                dataType: 'json',
                success: function (data) {
                    if (data && data.code == "10000") {
                        var form = $("<form method='post'></form>");
                        form.attr({ "action": "h5PayApi" });
                        for (var arg in data.data) {
                            var input = $("<input type='hidden'>");
                            input.attr({ "name": arg });
                            input.val(data.data[arg]);
                            form.append(input);
                        }
                        $(document.body).append(form);
                        form.submit();
                    } else {
                        layer.close(h5PayIng);
                        layer.open({content: data.message, skin: 'msg', time: 10});
                        openPay()
                    }
                }
            });
        }

        //二维码支付入口
        function invokeScanQrcodePay(payType,service) {
            var payIng = layer.open({type: 2, content: '正在发起支付，请稍后...', shadeClose: false});
            $.ajax({
                url: 'cashier',
                type: 'POST',
                data: {
                    "cmd":"scan",
                    "clientIP":clientIP,
                    "amount": amount,
                    "service":service,
                    "merchantId": merchNo,
                    "payType":payType,
                    "openId": "",
                    "remark":"固定收款"
                },
                dataType: 'json',
                success: function (data) {
                    if (data) {
                        $.ajax({
                            url: 'scanPayApi',
                            type: 'POST',
                            data: data,
                            dataType: 'json',
                            success: function (obj) {
                                console.log(obj);
                                layer.close(payIng);
                                if (obj && obj.code == "10000") {
                                    if(payType=='8'||payType=='1'){
                                        qrCodeHtml = layer.open({
                                            type: 1,
                                            content: $("#qrCodeHtml").html(),
                                            anim: 'up',
                                            style: 'position:fixed; left:0; top:0; width:100%; height:100%; border: none; -webkit-animation-duration: .5s; animation-duration: .5s;background: #F0EFF5;'
                                        });
                                        $("#qrMoney").text(amount);
                                        $('#payQrCode').qrcode({
                                            width: 200,
                                            height: 200,
                                            text: obj.bankUrl
                                        });
                                        if(payType=='8'){
                                            $("#placeholderText").text('请使用银联钱包扫码完成支付');
                                        }else if(payType=='1'||payType=='20'){
                                            $("#placeholderText").text('请使用微信扫码完成支付');
                                        }
                                    }else{
                                        location.href=obj.bankUrl;
                                    }
                                } else {
                                    layer.open({content: obj.message, skin: 'msg', time: 10});
                                }
                            }
                        });
                    } else {
                        layer.close(payIng);
                        layer.open({content: data.message, skin: 'msg', time: 10});
                    }
                }
            });
        }

        //判断是QQ的浏览器
        function isQQ() {
            var userAgent = navigator.userAgent.toLowerCase();
            if (userAgent.match(/qq\//i) == "qq/") {
                return true;
            } else {
                return false;
            }
        }

        //判断是微信app的浏览器
        function isWechat() {
            var userAgent = navigator.userAgent.toLowerCase();
            if (userAgent.match(/MicroMessenger/i) == "micromessenger") {
                return true;
            } else {
                return false;
            }
        }

        //判断是支付宝app的浏览器
        function isAlipay() {
            var userAgent = navigator.userAgent.toLowerCase();
            if (userAgent.match(/Alipay/i) == "alipay") {
                return true;
            } else {
                return false;
            }
        }

        //判断是京东app的浏览器
        function isJdpay() {
            var userAgent = navigator.userAgent.toLowerCase();
            if (userAgent.match(/jdpay/i) == "jdpay") {
                return true;
            } else {
                return false;
            }
        }
    </script>
    <script type="text/html" id="qrCodeHtml">
        <div class="wrap">
            <div class="edit_cash" style="text-align: center;padding: 0;">
                <p style="margin-top: -35px;"><strong  id="placeholderText"></strong></p>
                <p style="color: black;margin-top: -35px;">支付金额：￥<strong id="qrMoney"></strong></p>
                <div class="mask">
                    <div id="payQrCode" class="center">

                    </div>
                </div>
                <hr>
                <button style="background-color:#40AFFE;" onclick="layer.close(qrCodeHtml);">关闭</button>
            </div>
        </div>
    </script>
</body>
</html>