<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/amazeui.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/index.css">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/bindcard.css">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/layer.css">
    <title>新增卡片</title>
</head>

<body>
<script src="${request.contextPath}/static/js/hy/wechat/jquery.min.js"></script>
<script src="${request.contextPath}/static/js/hy/wechat/amazeui.js"></script>
<script src="${request.contextPath}/static/js/hy/wechat/handlebars.min.js"></script>
<script src="${request.contextPath}/static/js/hy/wechat/amazeui.widgets.helper.js"></script>
<script src="${request.contextPath}/static/js/hy/wechat/layer.js"></script>
<script type="text/x-handlebars-template" id="wechatPay">
    {{>header header}}
</script>
<div class="common-container">
    <p class="bind-notice">请绑定持卡人本人的卡<a id="support-bank" style="float: right">支持银行查询</a></p>
    <div id="card-form">
        <div class="bind-container">
            <#--<p style="border-bottom: 1px solid #ddd;"><label>持卡人</label><input type="text" class="no-border"-->
                                                                               <#--id="card-owner" maxlength="15">-->
                <#--<span class="am-icon-times" style="font-size: 16px;" id="clear-owner"></span>-->
            <#--</p>-->
            <p style="border-bottom: 1px solid #ddd;"><label>卡号</label><input type="text" class="no-border" id="inputCardNo" maxlength="23">
                <span class=" am-icon-times" style="font-size: 16px;" id="clear-card"></span>
            </p>
            <div id="textIdNo">
            <p style="border-bottom: 1px solid #ddd;"><label>证件号</label><input type="text" class="no-border" id="inputIdNo" maxlength="22">
                <span class=" am-icon-times" style="font-size: 16px;" id="clear-id"></span>
            </p>
            </div>
            <p style="border-bottom: 1px solid #ddd;"><label>预留手机</label><input type="text" class="no-border" id="inputPhoneNo" maxlength="13">
                <span class=" am-icon-times" style="font-size: 16px;" id="clear-phone"></span>
            </p>
        </div>
        <button class=" am-btn am-btn-success am-btn-block" type="button" id="next-button" disabled>下一步</button>
        <button class=" am-btn am-btn-success am-btn-block" type="button" id="return-button" style="width: 90%;margin: 10px auto;">返回继续支付</button>
    </div>
</div>
<script>
    var checkIdNo = false;
    $(function () {
        var $tpl = $('#wechatPay'),
                tpl = $tpl.text(),
                template = Handlebars.compile(tpl),
                data = {},
                html = template(data);
        $("#textIdNo").hide();
        $tpl.before(html);
        var msg = "暂不支持 中国工商银行、商业银行  等银行业务，请选择支持卡支付！";
        var msgContent = '<div class="am-modal-hd" style="padding: 10px;">公告</div><div class="am-modal-bd support-msg-content" style="padding: 0;">'
                + msg + '</div>';
        layer.open({
            content: msgContent
            , btn: ['确定']
            , className: "msg-modal"
            , style: "font-size:16px;"
            , yes: function (index) {
                layer.close(index);
            }
        });
    });
    $("#inputCardNo").on("input", function () {
        //获取当前光标的位置
        var caret = this.selectionStart;
        //获取当前的value
        var value = this.value;
        //从左边沿到坐标之间的空格数
        var sp = (value.slice(0, caret).match(/\s/g) || []).length;
        //去掉所有空格
        var nospace = value.replace(/\s/g, '');
        //重新插入空格
        var curVal = this.value = nospace.replace(/\D+/g, "").replace(/(\d{4})/g, "$1 ").trim();
        //从左边沿到原坐标之间的空格数
        var curSp = (curVal.slice(0, caret).match(/\s/g) || []).length;
        //修正光标位置
        this.selectionEnd = this.selectionStart = caret + curSp - sp;
        var cardNo = $("#inputCardNo").val();
        var phone = $("#inputPhoneNo").val();
        if (cardNo.length >= 16 && phone.length == 13) {
            $("#next-button").attr("disabled", false);
        } else {
            $("#next-button").attr("disabled", true);
        }
    });
    $("#inputIdNo").on("input", function () {
        //获取当前光标的位置
        var caret = this.selectionStart;
        //获取当前的value
        var value = this.value;
        //从左边沿到坐标之间的空格数
        var sp = (value.slice(0, caret).match(/\s/g) || []).length;
        //去掉所有空格
        var nospace = value.replace(/\s/g, '');
        //重新插入空格
        var curVal = this.value = nospace.replace(/[^xX0-9]/g, "").replace(/(\d{4})/g, "$1 ").trim();
        //从左边沿到原坐标之间的空格数
        var curSp = (curVal.slice(0, caret).match(/\s/g) || []).length;
        //修正光标位置
        this.selectionEnd = this.selectionStart = caret + curSp - sp;
        var cardNo = $("#inputCardNo").val();
        var phone = $("#inputPhoneNo").val();
        var idNo = $("#inputIdNo").val();
        cardNo = cardNo.replace( /\s+/g,"");
        phone = phone.replace( /\s+/g,"");
        idNo = idNo.replace( /\s+/g,"");
        if (cardNo.length >= 16 && phone.length == 11 && idNo.length == 18) {
            $("#next-button").attr("disabled", false);
        } else {
            $("#next-button").attr("disabled", true);
        }
    });
    $("#inputPhoneNo").on("input", function () {
        //获取当前光标的位置
        var caret = this.selectionStart;
        //获取当前的value
        var value = this.value;
        //从左边沿到坐标之间的空格数
        var sp = (value.slice(0, caret).match(/\s/g) || []).length;
        //去掉所有空格
        var nospace = value.replace(/\s/g, '');
        //重新插入空格
        var curVal = this.value = nospace.replace(/\D+/g, "").replace(/(\d{4})/g, "$1 ").trim();
        //从左边沿到原坐标之间的空格数
        var curSp = (curVal.slice(0, caret).match(/\s/g) || []).length;
        //修正光标位置
        this.selectionEnd = this.selectionStart = caret + curSp - sp;
        var cardNo = $("#inputCardNo").val();
        var phone = $("#inputPhoneNo").val();
        var idNo = $("#inputIdNo").val();
        cardNo = cardNo.replace( /\s+/g,"");
        phone = phone.replace( /\s+/g,"");
        idNo = idNo.replace( /\s+/g,"");
        if (checkIdNo) {
            if (cardNo.length >= 16 && phone.length == 11 && idNo == 18) {
                $("#next-button").attr("disabled", false);
            } else {
                $("#next-button").attr("disabled", true);
            }
        } else {
            if (cardNo.length >= 16 && phone.length == 11) {
                $("#next-button").attr("disabled", false);
            } else {
                $("#next-button").attr("disabled", true);
            }
        }

    });
    $("#next-button").click(function () {
        var cardNo = $("#inputCardNo").val();
        var idNo = $("#inputIdNo").val();
        var phone = $("#inputPhoneNo").val();
        cardNo = cardNo.replace( /\s+/g,"");
        phone = phone.replace( /\s+/g,"");
        // var owner = $("#card-owner").val();
        if (!isBankCard(cardNo)) {
            layer.open({ content: '交易卡号格式错误!', skin: 'msg', time: 3 });
            $("#inputCardNo").focus();
            return false;
        }
        else if (checkIdNo && !isIdNo(idNo)) {
            layer.open({ content: '身份证号格式错误!', skin: 'msg', time: 3 });
            $("#inputIdNo").focus();
            return false;
        }
        // else if(!isPeopleName(owner)){
        //     layer.open({ content: '持卡人格式错误!', skin: 'msg', time: 3 });
        //     $("#card-owner").focus();
        //     return false;
        // }
          else if (!isPhoneNo(phone)) {
            layer.open({ content: '手机号码格式错误!', skin: 'msg', time: 3 });
            $("#inputPhoneNo").focus();
            return false;
        } else {
            bindCard();
        }
    });
    $("#clear-owner").click(function(){
        $("#card-owner").val("");
    });
    $("#clear-card").click(function(){
        $("#inputCardNo").val("");
    });
    $("#clear-id").click(function(){
        $("#inputIdNo").val("");
    });
    $("#clear-phone").click(function(){
        $("#inputPhoneNo").val("");
    });
    function isPhoneNo(str) {
        var pattern = /^1\d{10}$/;
        if (pattern.test(str)) {
            return true;
        }
        return false;
    }
    function isPeopleName(str){
        return /^[\u4E00-\u9FA5]{2,8}$/.test(str);
    }
    function isBankCard(str) {
        var newStr = str.replace(/\s+/g, "");
        return /^([1-9]{1})(\d{15,18})$/.test(newStr);
    }
    function isIdNo(str) {
        return !str || !/^\d{6}(18|19|20)?\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i.test(str);
    }
    function getWhiteListContent() {
        var whiteCardInfo = ${whiteList};
        var content = "";
        if(whiteCardInfo.length !=0){
            for (var i = 0;i< whiteCardInfo.length; i++) {
                content += '<li class="support-li"><span class="card-name">' + whiteCardInfo[i].name +'</span>'
                + '50-3000<span>3W</span><span></span><span class="support-logo am-icon-check" style="color: lawngreen"></span></li>';
            }
        }
        return content;
    }
    function getBlackListContent() {
        var content = "";
        var blackCardInfo = ${blackList};
        if(blackCardInfo.length !=0){
            for (var i = 0;i< blackCardInfo.length; i++) {
                content += '<li class="support-li"><span class="card-name">' + blackCardInfo[i].name + '</span>'
                +'<span class="support-logo am-icon-times" style="color: red"></span></li>';
            }
        }
        return content;
    }

    var content = `
        <header data-am-widget="header" class="am-header am-header-default wp-header fixed-header">
                  <div class="am-header-left am-header-nav">
                      <a id="support-close" class="">
                          <span class="am-header-nav-title">
                            支持银行
                          </span>
                          <i class="am-header-icon am-icon-angle-left"></i>
                      </a>
                  </div>
            </header>
            <div class="common-container" style="margin-top: 49px;height: 100%;overflow: scroll;">
        <p class="support-notice">后续会陆续开放其他卡类型</p>
        <p class="support-header">支持卡类型 <span>单笔限额</span><span>日限额</span><span style="float: right">储蓄卡</span></p>
        <ul class="support-ul" id="support-ul">
        </ul>
        <p class="support-header"><span>不支持卡类型</span><span style="float: right">所有卡</span></p>
        <ul class="support-ul" id="not-support-ul">
        </ul>
        <div style="height: 80px">
        </div>
        </div>
        <footer class="support-footer">
            <button class="am-btn am-btn-success am-btn-block" type="button" id="return-banckcard">返回继续绑卡</button>
        </footer>
    `;

    var supportContent = getWhiteListContent();
    var notSupportContent = getBlackListContent();
    $("#support-bank").on("click", function (e) {
        e.preventDefault();
        var supportBank = layer.open({
            type: 1
            ,
            content: content
            ,
            anim: 'up'
            ,
            style: 'position:fixed; left:0; top:0; width:100%; height:100%; border: none; -webkit-animation-duration: .5s; animation-duration: .5s;background: #F2F2F4 !important;'
        });
        $(".layui-m-layercont").css("height","calc( 100% - 57px )");
        $("#support-ul").html(supportContent);
        $("#not-support-ul").html(notSupportContent);
        $("#return-banckcard").click(function () {
            layer.close(supportBank);
        });
        $("#support-close").click(function () {
            layer.close(supportBank);
        });
    });

    <!-- 公共参数 -->
    var allFlag = "0";
    function bindCard() {
        if (allFlag == "0" || allFlag == 0 ) {
            checkCardType();
            return;
        }
        var index = layer.open({type: 2 ,content: '处理中…',shadeClose: false});
        var idNo = $("#inputIdNo").val();
        var cardNo = $("#inputCardNo").val();
        var phone = $("#inputPhoneNo").val();
        cardNo = cardNo.replace( /\s+/g,"");
        phone = phone.replace( /\s+/g,"");
        var url = "${request.contextPath}/hyh5api/bindCard/${no}";
        $.ajax({
            url:url,
            type:'post',
            data:{
                "idNo":idNo,
                "cardNo":cardNo,
                "phone":phone,
                "merchNo":${merchNo},
                "payType": "2"
            },
            dataType : 'json',
            success : function(data){
                layer.close(index);
                if(data !=null){
                    if (data.code == '5200AAA') {
                        layer.open({content:data.message,skin: 'msg' ,time: 5 });
                        $("#inputIdNo").val("");
                        $("#inputCardNo").val("");
                        $("#inputPhoneNo").val("");
                    }else if (data.code == '520000'){
                        layer.open({content: "保存成功！" ,skin: 'msg' ,time: 1 });
                        window.history.back(-3);
                    }else{
                        layer.open({content: "保存失败！" ,skin: 'msg' ,time: 2 });
                    }
                }
            }
        });
    }

    function checkCardType() {
        var index = layer.open({type: 2 ,content: '处理中…',shadeClose: false});
        var cardNo = $("#inputCardNo").val();
        cardNo = cardNo.replace( /\s+/g,"");
        var url = "${request.contextPath}/hyh5api/checkCardType/${no}";
        $.ajax({
            url:url,
            type:'post',
            data:{
                "cardNo":cardNo
            },
            dataType : 'json',
            success : function(data){
                layer.close(index);
                if(data !=null){
                    if(data.checkFlag!=null){
                        if( data.checkFlag == "1" || data.checkFlag == 1){
                            $("#textIdNo").show();
                            checkIdNo = true;
                            $("#next-button").attr("disabled", true);
                            $("#inputIdNo").focus();
                            layer.open({content: "请输入预留身份证号" ,skin: 'msg' ,time: 3 });
                        } else {
                            allFlag = "1";
                            checkIdNo = false;
                            bindCard();
                        }
                        allFlag = "1";
                    }
                }
            }
        });
    }
    $("#return-button").click(function(){
        location.href = "${request.contextPath}/hyh5api//cashier/${no}";
    })
</script>
</body>

</html>