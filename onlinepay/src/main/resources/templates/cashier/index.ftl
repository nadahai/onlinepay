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
            /*margin: 5px auto;*/
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
            /*padding-top: 10px;*/
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
            /*top: 25%;*/
            left: 50%;
            padding-top: 10%;
            transform: translate(-50%, -50%)
        }
    </style>
</head>
<body>
<!-- 消费金额 -->
<article class="contanter">
    <div class="clear"></div>
    <div class="pay-input" style=" margin-top: 20px;">
        <p class="gray1">商户：<strong>${merchInfo.name}</strong></p>
        <div class="clear"></div>
    </div>
    <div class="pay-input" style="" id="showNums">
        <p class="gray1">输入金额：</p>
        <p class="gray">
            <span class="gray3">¥</span>
            <span class="gray2" id="money"></span>
            <i class="x-input-cursor" style="display: inline-flex;height: .3rem;"></i>
        </p>
        <div class="clear"></div>
    </div>
</article>
<!-- 消费描述 -->
<article class="contanter">
    <div class="clear"></div>
    <div class="pay-input" style=" margin-top: 20px;">
        <p class="gray1">消费描述：</p>
        <div class='textarea-wrapper' style="padding-bottom: 15px;">
            <div class='textarea-block'>
                <textarea class="textarea-item" id="remark" placeholder="请输入消费描述，不超过15字" maxlength="15"
                          rows="2"></textarea>
                <div class="textarea-count">
                    <span class="textareaInput">0</span>/<span class="textareaTotal">15</span>
                </div>
            </div>
        </div>
        <div class="clear"></div>
    </div>
</article>
<!-- 自定义键盘 start 加上x-mask-show显示-->
<div id="keyBoard" class="x-mask-box x-mask-show" data-id="mainMoney" style="background-color: rgba(0, 0, 0, 0);z-index:1" v-cloak="">
    <div class="x-slide-box pop-up-show">
        <div class="x-key-board" style="z-index:10">
            <div class="row">
                <div class="item js-key num">1</div>
                <div class="item js-key num">4</div>
                <div class="item js-key num">7</div>
                <div class="item js-key" onclick="showNum()">↓</div>
            </div>
            <div class="row">
                <div class="item js-key num">2</div>
                <div class="item js-key num">5</div>
                <div class="item js-key num">8</div>
                <div class="item js-key num">0</div>
            </div>
            <div class="row">
                <div class="item js-key num">3</div>
                <div class="item js-key num">6</div>
                <div class="item js-key num">9</div>
                <div class="item js-key num">.</div>
            </div>
            <div class="row">
                <div class="item no-border-right js-key x-key-del del">
                    <i class="back-icon"></i>
                </div>
                <div class="item2 no-border-bottom js-key no-border-right x-key-ok" id="confirm_pay"
                     style="cursor: pointer;">
                    <span style="line-height: 1.2; font-weight: 600; font-size: 18px; color:#fff;">支<br>付</span>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- 自定义键盘 end -->
<script>
    var qrCodeHtml;
    var moneyReg = /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/;
    var actions = new Array();
    var clientIP = returnCitySN["cip"];
    $(function () {
        // 消费描述长度检测
        $("#remark").keyup(function () {
            var remark = $('#remark').val();
            if (remark.length > 0 && remark.length <= 100) {
                $("#remark").val(remark.replace(/\n/g,"<br>").replace(/\s+/g, "").replace('#', "").replace('&', "").replace('=', ""));
                $('.textareaInput').html($('#remark').val().length);
            }
        });

        var winHeight = $(window).height();   //获取当前页面高度
        $(window).resize(function(){
            var thisHeight=$(this).height();
            if(winHeight - thisHeight >50){
                //当软键盘弹出，在这里面操作
                $("#keyBoard").removeClass("x-mask-show")
                // $("#keyBoard").fadeOut('fast');
            }else{
                //当软键盘收起，在此处操作
                // $("#keyBoard").show();
                $("#keyBoard").addClass('x-mask-show');
            }
        });
        <#assign havaQuick = "no">
        <#list merchChannels as merchChannel>
            <#if merchChannel.payType == 1>
                //微信扫码
                if(isWechat()){
                    actions.push({
                        text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/wechat.png" alt="" style="width:28px;"></div><div>微信扫码<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                        onClick: function () {
                            invokeScanPay('1','0002','${merchChannel.minTraPrice}','${merchChannel.maxTraPrice}');
                        }
                    });
                }
            </#if>
            <#if merchChannel.payType == 12>
                //微信H5
                actions.push({
                    text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/wechat.png" alt="" style="width:28px;"></div><div>微信H5<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                    onClick: function () {
                        invokeH5Pay('12','30','${merchChannel.settleType}','${merchChannel.minTraPrice}','${merchChannel.maxTraPrice}');
                    }
                });
            </#if>
            <#if merchChannel.payType == 20>
                //微信公众号直清
                if(isWechat()) {
                    actions.push({
                        text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/wechat.png" alt="" style="width:28px;"></div><div>微信公众号直清<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                        onClick: function () {
                            invokeScanPay('20', '010900','${merchChannel.minTraPrice}', '${merchChannel.maxTraPrice}');
                        }
                    });
                }
            </#if>
            <#if merchChannel.payType == 2>
                //支付宝扫码
                if(isAlipay()){
                    actions.push({
                        text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/alipay.png" alt="" style="width:28px;"></div><div>支付宝扫码<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                        onClick: function () {
                            invokeScanPay('2','0010','${merchChannel.minTraPrice}','${merchChannel.maxTraPrice}');
                        }
                    });
                }
            </#if>
            <#if merchChannel.payType == 10>
                //支付宝H5
                if(!isWechat()){
                    actions.push({
                        text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/alipay.png" alt="" style="width:28px;"></div><div>支付宝H5<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                        onClick: function () {
                            invokeH5Pay('10','22','${merchChannel.settleType}','${merchChannel.minTraPrice}','${merchChannel.maxTraPrice}');
                        }
                    });
                }
            </#if>
            <#if merchChannel.payType == 21>
                //支付宝服务窗
                if(isAlipay()){
                    actions.push({
                        text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/alipay.png" alt="" style="width:28px;"></div><div>支付宝服务窗<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                        onClick: function () {
                            invokeH5Pay('21','12','${merchChannel.settleType}','${merchChannel.minTraPrice}','${merchChannel.maxTraPrice}');
                        }
                    });
                }
            </#if>
            <#if (merchChannel.payType == 3||merchChannel.payType == 5) >
                //QQ扫码
                if(isQQ()){
                    actions.push({
                        text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/qq.png" alt="" style="width:28px;"></div><div>QQ扫码<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                        onClick: function () {
                            invokeScanPay('${merchChannel.payType}','0015','${merchChannel.minTraPrice}','${merchChannel.maxTraPrice}');
                        }
                    });
                }
            </#if>
            <#if merchChannel.payType == 11 >
                //QQH5
                actions.push({
                    text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/qq.png" alt="" style="width:28px;"></div><div>QQH5<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                    onClick: function () {
                        invokeH5Pay('11','31','${merchChannel.settleType}','${merchChannel.minTraPrice}','${merchChannel.maxTraPrice}');
                    }
                });
            </#if>
            <#if merchChannel.payType == 16 >
                //JD H5
                actions.push({
                    text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/jingdong.png" alt="" style="width:28px;"></div><div>京东H5<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                    onClick: function () {
                        invokeH5Pay('16','34','${merchChannel.settleType}','${merchChannel.minTraPrice}','${merchChannel.maxTraPrice}');
                    }
                });
            </#if>
            <#if merchChannel.payType == 7 >
                //银联快捷
                <#assign havaQuick = "yes">
            </#if>
            <#if merchChannel.payType == 8 >
                //银联二维码
                actions.push({
                    text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/yinlianerweima.png" alt="" style="width:28px;"></div><div>${merchChannel.channelNickName}<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                    onClick: function () {
                        invokeScanPay('8','010800','${merchChannel.minTraPrice}','${merchChannel.maxTraPrice}');
                    }
                });
            </#if>
            <#if merchChannel.payType == 4 >
                //京东钱包
                actions.push({
                    text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/jingdong.png" alt="" style="width:28px;"></div><div>京东钱包<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                    onClick: function () {
                        invokeScanPay('4','010700','${merchChannel.minTraPrice}','${merchChannel.maxTraPrice}');
                    }
                });
            </#if>
            <#if merchChannel.payType == 13 >
                //银联直冲
                actions.push({
                    text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/yinlianzc.png" alt="" style="width:28px;"></div><div>银联直冲<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                    onClick: function () {
                        invokeUnionPay('13','${merchChannel.settleType}','${merchChannel.minTraPrice}','${merchChannel.maxTraPrice}');
                    }
                });
            </#if>
            <#if merchChannel.payType == 15 >
                //网关支付
                actions.push({
                    text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/wangguan.png" alt="" style="width:28px;"></div><div>网关支付<small style="color:red;font-size:10px;">　（￥${merchChannel.minTraPrice}-￥${merchChannel.maxTraPrice}）</small></div></div>',
                    onClick: function () {
                        var money = $("#money").html();
                        if(money<${merchChannel.minTraPrice}){
                            $.alert("网关支付单笔最少${merchChannel.minTraPrice}元", "提示");
                            return;
                        }
                        var remark=$("#remark").val().replace(/\s+/g, "").replace(/<\/?.+?>/g,"");
                        if(remark.length<1){
                            $.alert("请输入消费描述", "提示");
                            return;
                        }
                        location.href="cashier?cmd=gateWay&remark="+remark+"&amount="+money+"&mode=${merchChannel.settleType}&merchNo=${merchInfo.merchNo?c}";
                    }
                });
            </#if>
        </#list>
        <#if havaQuick=="yes" >
            //银联快捷
            actions.push({
                text: '<div style="flex-direction: row;display: inline-flex;"><div><img src="static/cashier/images/paymentIcon/yinlian.png" alt="" style="width:28px;"></div><div>银联快捷<small style="color:red;font-size:10px;">　（￥10-￥50000）</small></div></div>',
                onClick: function () {
                    var money = $("#money").html();
                    if(money<10){
                        $.alert("银联快捷单笔最少10元", "提示");
                        return;
                    }
                    var remark=$("#remark").val().replace(/\s+/g, "").replace(/<\/?.+?>/g,"");
                    if(remark.length<1){
                        $.alert("请输入消费描述", "提示");
                        return;
                    }
                    location.href="cashier?cmd=quickPay&remark="+remark+"&amount="+money+"&merchNo=${merchInfo.merchNo?c}";
                }
            });
        </#if>

        $(".num").on("touchstart",function(event){
            event.preventDefault();
            var text=$("#money").text();
            var dotIndex=text.indexOf('.');
            var n=$(this).text();
            // console.log(text);
            var newMoney=text+n;
            if(text.length<1&&(n=='0'||n=='00')){
                layer.open({content: '单笔交易金额不能小于￥2', skin: 'msg', time: 3});
                return;
            }
            if(text!=''&&parseInt(newMoney)>50000 && n!='.'&&dotIndex<0){
                layer.open({content: '单笔交易金额不能大于￥50000', skin: 'msg', time: 3});
                return;
            }
            if(n=='00'&&!typeof(text.split('.')[1])=="undefined"&&text.split('.')[1].length>0){
                newMoney=text+'0';
            }
            if(n=='.' && (text==''||dotIndex>=0)){
                // layer.open({content: '请输入正确的金额', skin: 'msg', time: 3});
                return;
            }
            if(dotIndex>=0 && text.length-dotIndex>=3){
                // layer.open({content: '请输入正确的金额', skin: 'msg', time: 3});
                return;
            }
            $("#money").text(newMoney);
            $(this).focus();
        });

        $("#showNums").on("click",function(){
            $("#keyBoard").addClass('x-mask-show');
        });


        $(".del").on("touchstart",function(event){
            event.preventDefault();
            var text=$("#money").text();
            if(text.length<=1){
                $("#money").text('');
            }else{
                var subtext=text.substr(0,text.length-1);
                $("#money").text(subtext);
            }

        });

        $(document).on("click", "#confirm_pay", function () {
            var m = $("#money").html();
            if (!moneyReg.test(m.toString())){
                $.alert("请输入正确的金额", "提示");
                return;
            }
            if (m < 2) {
                $.alert("单笔交易金额不能小于￥2", "提示");
                return;
            }
            if (m >= 50000) {
                $.alert("单笔交易金额不能大于￥50000", "提示");
                return;
            }
            var remark = $("#remark").val().replace(/\s+/g, "").replace(/<\/?.+?>/g,"");
            if (remark.length < 1 || remark.length > 100) {
                $.alert("请输入消费描述，100个字符以内", "提示");
                return;
            }
            $.actions({
                title: "选择支付方式",
                actions: actions
            });
        });

    })

    function showNum() {
        if($("#keyBoard").hasClass("x-mask-show")){
            $("#keyBoard").removeClass("x-mask-show")
        }else{
            $("#keyBoard").addClass('x-mask-show');
        }
    }

    //扫码支付入口
    function invokeScanPay(payType,service,minMoney,maxMoney) {
        var money = $("#money").html();
        // console.log("输入金额："+money+"；限额："+minMoney+"-"+maxMoney);
        // console.log(parseFloat(money)<minMoney||parseFloat(money)>maxMoney);
        if(parseFloat(money)<minMoney||parseFloat(money)>maxMoney){
            $.alert("该支付方式限额"+minMoney+"-"+maxMoney, "提示");
            return;
        }
        var payIng = layer.open({type: 2, content: '正在发起支付，请稍后...', shadeClose: false});
        $.ajax({
            url: 'cashier',
            type: 'POST',
            data: {
                "cmd":"scan",
                "clientIP":clientIP,
                "amount": money,
                "service":service,
                "merchantId": "${merchInfo.merchNo?c}",
                "payType":payType,
                "openId": "${openId}",
                "remark":$("#remark").val().replace(/\s+/g, "").replace(/<\/?.+?>/g,"")
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
                                if(payType=='1'||payType=='3'||payType=='5'||payType=='8'||payType=='20'){
                                    qrCodeHtml = layer.open({
                                        type: 1,content: $("#qrCodeHtml").html(),anim: 'up',
                                        style: 'position:fixed; left:0; top:0; width:100%; height:100%; border: none; -webkit-animation-duration: .5s; animation-duration: .5s;'
                                    });
                                    $("#qrMoney").text(money);
                                    $("#qrcodeImg").attr("src","https://api.qrserver.com/v1/create-qr-code/?size=180x180&data="+obj.bankUrl);
                                    // $('#payQrCode').qrcode({width: 200,height: 200,text: obj.bankUrl});

                                    if(payType=='8'){
                                        $("#placeholderText").text('请使用银联钱包扫码完成支付');
                                    }else if(payType=='1'||payType=='20'){
                                        $("#placeholderText").text('请在微信客户端截图扫一扫完成支付');
                                        $("#qrcodeRemarks").text('(支付金额请与下单金额保持一致!否则无法正常到账!)');
                                    }else if(payType=='3'||payType=='5'){
                                        $("#placeholderText").text('请使用QQ扫码完成支付');
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

    //H5支付入口
    function invokeH5Pay(payType,payTypeCode,settleType,minMoney,maxMoney) {
        var money = $("#money").text();
        if(parseFloat(money)<minMoney||parseFloat(money)>maxMoney){
            $.alert("该支付方式限额"+minMoney+"-"+maxMoney, "提示");
            return;
        }
        var h5PayIng = layer.open({type: 2, content: '正在发起支付，请稍后...', shadeClose: false});
        $.ajax({
            url: 'cashier',
            type: 'POST',
            data: {
                "cmd":"H5",
                "clientIP":clientIP,
                "amount": money,
                "payTypeCode":payTypeCode,
                "settleType":settleType,
                "merchantId": "${merchInfo.merchNo?c}",
                "payType":payType,
                "openId": "${openId}",
                "remark":$("#remark").val().replace(/\s+/g, "").replace(/<\/?.+?>/g,"")
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
                }
            }
        });
    }

    //银联直冲
    function invokeUnionPay(payType,settleType,minMoney,maxMoney) {
        var money = $("#money").text();
        if(parseFloat(money)<minMoney||parseFloat(money)>maxMoney){
            $.alert("该支付方式限额"+minMoney+"-"+maxMoney, "提示");
            return;
        }
        var h5PayIng = layer.open({type: 2, content: '正在发起支付，请稍后...', shadeClose: false});
        $.ajax({
            url: 'cashier',
            type: 'POST',
            data: {
                "cmd":"union",
                "clientIP":clientIP,
                "amount": money,
                "merchantId": "${merchInfo.merchNo?c}",
                "payType":payType,
                "mode":settleType,
                "remark":$("#remark").val().replace(/\s+/g, "").replace(/<\/?.+?>/g,"")
            },
            dataType: 'json',
            success: function (data) {
                if (data && data.code == "10000") {
                    var form = $("<form method='post'></form>");
                    form.attr({ "action": "unionPayApi" });
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
                }
            }
        });
    }

    //网关支付
    function invokeGateWayPay(payType,settleType,minMoney,maxMoney) {
        var money = $("#money").text();
        if(parseFloat(money)<minMoney||parseFloat(money)>maxMoney){
            $.alert("该支付方式限额"+minMoney+"-"+maxMoney, "提示");
            return;
        }
        var h5PayIng = layer.open({type: 2, content: '正在发起支付，请稍后...', shadeClose: false});
        $.ajax({
            url: 'cashier',
            type: 'POST',
            data: {
                "cmd":"gateway",
                "clientIP":clientIP,
                "amount": money,
                "merchantId": "${merchInfo.merchNo?c}",
                "payType":payType,
                "mode":settleType,
                "bankAbbr":bankAbbr,
                "remark":$("#remark").val().replace(/\s+/g, "").replace(/<\/?.+?>/g,"")
            },
            dataType: 'json',
            success: function (data) {
                if (data && data.code == "10000") {
                    var form = $("<form method='post'></form>");
                    form.attr({ "action": "nineBankPayment" });
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

    document.body.addEventListener('touchmove', function (e) {
        e.preventDefault();
    });
</script>
<script type="text/html" id="qrCodeHtml">
    <div class="wrap">
        <div class="edit_cash" style="text-align: center;padding: 0;">
            <p style="color: black;">商户：<strong>${merchInfo.name}</strong></p>
            <hr>
            <p style="margin-top: -35px;"><strong  id="placeholderText"></strong></p>
            <p style="margin-top: -35px;color: red"><strong  id="qrcodeRemarks"></strong></p>
            <p style="color: black;margin-top: -35px;">支付金额：￥<strong id="qrMoney"></strong></p>
            <div class="mask">
                <div id="payQrCode" class="center">
                    <img id="qrcodeImg" src="" style="width: 180px;height: 180px" >
                </div>
            </div>
            <hr>
            <button style="background-color:#40AFFE;" onclick="layer.close(qrCodeHtml);">关闭</button>
        </div>
    </div>
</script>
</body>
</html>