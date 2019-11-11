<!DOCTYPE html>
<html lang="en">

<head>
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/amazeui.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/index.css">
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.bootcss.com/animate.css/3.5.2/animate.css" rel="stylesheet">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/layer.css">
    <link rel="stylesheet" type="text/css" href="${request.contextPath}/static/css/hy/wechat/keyboard.css">
    <link rel="stylesheet" type="text/css" href="${request.contextPath}/static/css/hy/wechat/keyboard_pwd.css">
    <title>微信支付</title>
</head>

<body>
<input type="hidden" id="staticContextPath" value="${request.contextPath}">
<script type="text/x-handlebars-template" id="wechatPay">
    {{>header header}}
</script>
<div class="pay-container">
    <p id="pay-what">商品</p>
    <p id="pay-money">￥${amount}</p>
    <p class="pay-who-container">收款方<span id="pay-who">梦飞科技</span></p>
    <button class=" am-btn am-btn-success am-btn-block" type="button" id="pay-button">付款</button>
</div>
<div class="am-modal am-modal-no-btn" tabindex="-1" id="pay-pwd-modal">
    <div class="am-modal-dialog modal-position">
        <div class="am-modal-hd">
            <a href="javascript: void(0)" class="am-close am-close-spin closebtn"
               style="left: 10px;top:10px;font-size: 30px;font-weight: inherit;opacity: 1;"
               data-am-modal-close>&times;</a>
            请输入银行卡密码
        </div>
        <div class="am-modal-bd">
            <#--<p style="margin: 10px 0 0 0;">付款给梦飞科技</p>-->
            <p class="confirm-money">￥</p>
            <p class="pay-way-container">支付方式
            </p>
            <div class="container-fluid">
                <div class="keyboard-show-text_1"></div>
                <div class="keyboard-box_1">
                </div>
            </div>
            <lable id="cardId" style="display:none"></lable>
            <lable id="idNo" style="display:none"></lable>
            <label id="successMoney" style="display:none">${amount}</label>
            <label id="encryptNo" style="display:none">${encryptNo}</label>
        </div>
    </div>
</div>
<div class="am-modal am-modal-no-btn" tabindex="-1" id="nowpay-modal">
    <div class="am-modal-dialog modal-position">
        <div class="am-modal-hd">
            <a href="javascript: void(0)" class="am-close am-close-spin closebtn" style="left: 10px;top:10px;font-size: 30px;font-weight: inherit;opacity: 1;"
               data-am-modal-close>&times;</a>
            确认付款
        </div>
        <div class="am-modal-bd">
            <p style="margin: 10px 0 0 0;">付款给梦飞科技</p>
            <p class="confirm-money">￥</p>
            <p class="pay-way-container" onclick="getPayWay()">支付方式
                <span class="am-icon-angle-right" style="float: right"></span>
                <span style="float:right">
                        <img src="http://paypaul.prxgg.cn/onlinepay/static/images/payment_card_24px_505044_easyicon.net.png" alt="bank-log" class="bank-log chos-bank-log">
                        <span class="pay-way" data-if="notChoose">请选择银行卡</span>
                    </span>
            </p>
            <button class=" am-btn am-btn-success am-btn-block" type="button" id="nowpay-button" disabled style="width: 85%;border-radius: 5px;margin: 10px auto;outline: none;">立即支付</button>
        </div>
    </div>
</div>
<div class="am-modal am-modal-no-btn" tabindex="-1" id="pay-captcha-modal">
    <div class="am-modal-dialog modal-position">
        <div class="am-modal-hd">
            <a href="javascript: void(0)" class="am-close am-close-spin closebtn"
               style="left: 10px;top:10px;font-size: 30px;font-weight: inherit;opacity: 1;"
               data-am-modal-close>&times;</a>
            请输入验证码
        </div>
        <div class="am-modal-bd">
            <p style="margin: 10px 0 0 0;">付款给梦飞科技</p>
            <p class="confirm-money">￥</p>
            <p class="pay-way-container">支付方式
                <span class="am-icon-angle-right" style="float: right"></span>
                <span style="float:right">
                        <img src="http://paypaul.prxgg.cn/onlinepay/static/images/payment_card_24px_505044_easyicon.net.png"
                             alt="bank-log" class="bank-log chos-bank-log">
                    <span class="pay-way" data-if="notChoose">请选择银行卡</span>
                    </span>
            </p>
            <div class="container-fluid">
                <div class="keyboard-show-text"></div>
                <div class="keyboard-box">
                </div>
            </div>
        </div>
    </div>
</div>

<div class="am-modal am-modal-no-btn" tabindex="-1" id="pay-way-modal">
    <div class="am-modal-dialog modal-position">
        <div class="am-modal-hd">
            <a href="javascript: void(0)" class="am-close am-close-spin"
               style="left: 10px;top:10px;font-size: 30px;font-weight: inherit;opacity: 1;" data-am-modal-close
               id="payway-close">&times;</a>
            选择支付方式
        </div>
        <div class="am-modal-bd">
            <div class="radio-container">
                <label class="am-radio am-success">
                </label>
                <hr data-am-widget="divider" style="" class="am-divider am-divider-default"/>
            </div>
            <p class="new-card-pay"><a href="${request.contextPath}/hyh5api/jumpAdd/${encryptNo}?merchNo=123456&payType=2">使用新卡支付</a>
                <a href="${request.contextPath}/hyh5api/jumpDel/${encryptNo}?userId=${userId}&payType=2" style="float: right">银行卡管理</a></p>
        </div>
    </div>
</div>
<div class="am-modal am-modal-prompt" tabindex="-1" id="password-false">
    <div class="am-modal-dialog modal-position" style="margin-top: 180px;">
        <div class="am-modal-hd" style="padding: 30px;">支付密码错误，请重试</div>
        <div class="am-modal-bd" style="padding: 0;">
        </div>
        <div class="am-modal-footer">
            <span class="am-modal-btn" data-am-modal-cancel>重试</span>
            <span class="am-modal-btn" data-am-modal-confirm style="width: 50%;">忘记密码</span>
        </div>
    </div>
</div>
<div class="am-modal am-modal-prompt" tabindex="-1" id="captcha-false">
    <div class="am-modal-dialog modal-position" style="margin-top: 180px;">
        <div class="am-modal-hd" style="padding: 30px;">验证码错误，请重试</div>
        <div class="am-modal-bd" style="padding: 0;">
        </div>
        <div class="am-modal-footer">
            <span class="am-modal-btn" data-am-modal-cancel>重试</span>
        </div>
    </div>
</div>

<script src="${request.contextPath}/static/js/hy/wechat/jquery.min.js"></script>
<script src="${request.contextPath}/static/js/hy/wechat/amazeui.js"></script>
<script src="${request.contextPath}/static/js/hy/wechat/handlebars.min.js"></script>
<script src="${request.contextPath}/static/js/hy/wechat/amazeui.widgets.helper.js"></script>
<script src="${request.contextPath}/static/js/hy/wechat/layer.js"></script>
<script type="text/javascript" src="${request.contextPath}/static/js/hy/wechat/keyboard.js"></script>
<script type="text/javascript" src="${request.contextPath}/static/js/hy/wechat/keyboard_pwd.js"></script>
<script type="text/javascript" charset="utf-8" src="${request.contextPath}/static/js/phone.encryptpd.js"></script>
<script>
    // var flag = localStorage.getItem("flag");
    window.localeStr = "zh_CN";
    window.publicKey = "";
    window.exponent = "";
    window.visiablepan = "";

    var allFlag = "";    //设置的全局变量状态
    var allPass = "";   //加密的卡密态
    var changeCard = true;

    var $payWayModal = $('#pay-way-modal');
    $(function () {
        var $tpl = $('#wechatPay'),
                tpl = $tpl.text(),
                template = Handlebars.compile(tpl),
                data = {},
                html = template(data);

        $tpl.before(html);
        var cardList = getCardInfos("1");
        if (cardList.length >= 1) {
            var cardName = cardList[cardList.length - 1].cardName;
            //TODO: 下单时赋值
            $(".pay-way").text(cardList[cardList.length - 1].cardName);
            $(".pay-way").attr("data-if","choose");
            $("#nowpay-button").attr("disabled",false);
        }
        var setPaywayContent = '';
        for (var i = 0; i < cardList.length; i++) {
            setPaywayContent += '<label class="am-radio am-success">';
            setPaywayContent += '<input type="radio" name="bankCard" data-idno="' + cardList[i].idNo +'" data-id="' + cardList[i].cardName + '" value="'+ cardList[i].id + '" data-am-ucheck>';
            setPaywayContent += '<img src="http://paypaul.prxgg.cn/onlinepay/static/images/payment_card_24px_505044_easyicon.net.png" alt="bank-log" class="bank-log">';
            setPaywayContent += cardList[i].cardName;
            setPaywayContent += '</label><hr data-am-widget="divider" style="" class="am-divider am-divider-default" />';
        }
        $(".radio-container").html(setPaywayContent);

        $("input[type='radio'], input[type='radio']").click(function (e) {
            var cardId = e.target.value;
            var bankCardName = e.target.getAttribute("data-id");
            var idNo = e.target.getAttribute("data-idno");
            $payWayModal.modal('close');
            // clearInput();
            $('#nowpay-modal').modal({
                closeViaDimmer: false
            });
            $(".pay-way").text(bankCardName);
            $("#cardId").text(cardId);
            $("#idNo").text(idNo);
        });
        var selectCard = getQueryString("card");
        var bankIcon = getQueryString("icon");
        var money = getQueryString("money");
        if (selectCard) {
            $(".pay-way").text(selectCard);
            $(".chos-bank-log").attr("src", bankIcon);
            $(".confirm-money").text(`￥${money}`);
            $('#pay-pwd-modal').modal({
                closeViaDimmer: false
            });
        }
    });
    function payPassWord() {
        var $modal = $('#pay-captcha-modal');
        layer.open({ type: 0, content: '已发送短信验证码，请填入！', shadeClose: false, btn: ['我知道了'], shade: 0.2, style:"font-size:17px"});
        var load = layer.open({
            type: 2, content: '正在发送验证码', shadeClose: false
        });
        setTimeout(() => {
            layer.close(load);
            $("#nowpay-modal").modal("close");
            var money = $("#pay-money").text();
            $(".confirm-money").text(money);
            $modal.modal({
                closeViaDimmer: false
            });
    },
        500;
    )
        orderSubmit();
    }
    $('#nowpay-button').click(function () {
        payPassWord();
    });
    $('#pay-button').click(function () {
        var $modal = $('#nowpay-modal');
        var money = $("#pay-money").text();
        $(".confirm-money").text(money);
        $modal.modal({
            closeViaDimmer: false
        });
    });
    $(".keyboard-box").KeyBoard({
        random: false, // 随机键盘1
        type: "password", // 密码 password or 金额 money
        show: $(".keyboard-show-text"), // 展示区域
        safe: false // 加密显示
    });
    $(".keyboard-box_1").KeyBoard_1({
        random: false, // 随机键盘1
        type: "password", // 密码 password or 金额 money
        show: $(".keyboard-show-text_1"), // 展示区域
        safe: true // 加密显示
    });
    function getPayWay() {
        if (!changeCard) {
            layer.open({ content: "下单完成，请勿更换支付卡！", skin: 'msg', time: 2 });
            return;
        }
        var $modal = $('#nowpay-modal');
        $modal.modal('close');
        // clearInput();
        $payWayModal.modal({
            closeViaDimmer: false
        });
    }

    function getQueryString(name) {
        let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        let r = decodeURIComponent(window.location.search).substr(1).match(reg);
        if (r != null) {
            return unescape(r[2]);
        }
        return null;
    }

    function closeGc() {
        $("#pay-captcha-modal").modal("close");
        clearInput();
    }

    function cloasePass() {
        $("#pay-pwd-modal").modal("close");
        clearInput_1();
    }


    function getCardInfos(flag){
        var userId = "${userId}";
        var data = {"userId":userId};
        var url = "${request.contextPath}/hyh5api/selectCardInfos/${encryptNo}";
        var cardList = [];
        $.ajax({
            url:url,
            type:'post',
            data:data ,
            async: false,
            dataType : 'json',
            success : function(result){
                if(!result){
                    layer.open({content: "查询卡信息失败！" ,skin: 'msg' ,time: 2 });

                } else {
                    <!-- 循环遍历银行卡信息-->
                    var cardInfo = JSON.parse(result.bankInfo);
                    var index = 0;
                    if(cardInfo.length !=0){
                        $('#cardTypes').html('');
                        for (var i = 0;i< cardInfo.length; i++) {
                            index++;
                            if(cardInfo[i].bankType==2){
                                continue;
                            }
                            var bankName = cardInfo[i].bankName;
                            if ( bankName.length>7 ){
                                bankName = bankName.substring(0,7)+"…";
                            }
                            cardList.push(
                                    {id: cardInfo[i].id, cardName:bankName+"("+cardInfo[i].cardNo.substring(cardInfo[i].cardNo.length-4)+")",
                                        cardNo: cardInfo[i].cardNo, idNo: cardInfo[i].idNo}
                            );
                        }

                        if( flag=="1" ){
                            <!-- 设置默认银行卡信息（选择新增的最后一张） -->
                            $("#cardId").text(cardInfo[index - 1].id);
                            $("#idNo").text(cardInfo[index - 1].idNo);
                        }
                    } else {
                        $("#cardId").text("000");
                    }
                }
            }
        });
        return cardList;
    }

    $("#payway-close").on("click", function () {
        $('#nowpay-modal').modal({
            closeViaDimmer: false
        });
    });
    // 模态框关闭时清空输入内容
    $(".am-close").on("click", function () {
        clearInput();
        localStorage.removeItem("remodal");
    });

    function orderSubmit() {
        var cardId = $("#cardId").text();
        var data = { "cardId": cardId };
        var url = "${request.contextPath}/hyh5api/order/${encryptNo}";
        $.ajax({
            url: url,
            type: 'post',
            data: data,
            dataType: 'json',
            success: function (result) {
                changeCard = false;
                if (!result || !result.code) {
                    layer.open({ content: result.message, skin: 'msg', time: 3 }); return;
                }
                if (result.code == "520000") {
                    if (result.status == "1") {
                        //表示已经处于下单中的状态
                        loadDfpCer("${encryptNo}");
                    } else if (result.status == "3") {
                        //下单中
                        loadDfpCer("${encryptNo}");
                    } else if (result.status == "2" || result.status == "5") {
                        layer.open({ content: result.message, skin: 'msg', time: 3 });
                        paySuccess("${encryptNo}", "2", result.message, "2");
                        allFlag = 1;   //下单失败
                        return;
                    }
                    if (result.rules) {
                        if (JSON.parse(result.rules).password == true || JSON.parse(result.rules).password == "true") {
                            window.publicKey = result.publicKey;
                            window.exponent = result.exponent;
                            window.visiablepan = result.visiablepan;
                            allFlag = 0;  //表示需要输入卡密
                        } else {
                            allFlag = 2;
                        }
                    }
                    return;
                }
                layer.open({ content: result.message, skin: 'msg', time: 3 });
                paySuccess("${encryptNo}", "2", result.message, "2");
                allFlag = false;

            }
        });
        if (status == "3") {
            //表示已经处于下单中的状态
            $("#cardTypeClink").hide();
            $("#cardTypeClink2").show();
        }
    }

    /**
     * @描述 异步加载银联设备资源
     */
    try {
        var script = document.createElement('script');
        script.async = "async";
        script.src = "https://device.95516.com/dcs_svc/gateway/scripts/dcs_gateway.js";
        document.body.appendChild(script);
    } catch (e) {
        console.log(e);
    }
</script>
<script src="${request.contextPath}/static/js/hy/custom.hy.alipay.js"></script>
<script src="${request.contextPath}/static/js/nadacall.js"></script>
<script src="${request.contextPath}/static/js/common/online.nada.util.common.js"></script>
</body>
</html>