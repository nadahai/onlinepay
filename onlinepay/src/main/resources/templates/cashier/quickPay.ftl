<!DOCTYPE html>
<html lang="en">
<head>
    <title>收银台-银联快捷</title>
    <#include "/include/head.ftl">
    <link rel="stylesheet" href="${request.contextPath}/static/cashier/css/weuix.min.css"/>
     <link href="${request.contextPath}/static/css/layer.css" rel="stylesheet">
    <script src="${request.contextPath}/static/js/jquery.min.js"></script>
    <script src="${request.contextPath}/static/js/layer.js"></script>
    <script src="${request.contextPath}/static/cashier/js/zepto.min.js"></script>
    <style>
        button{
            display: inline-block;
            vertical-align: top;
            height: 36px;
            line-height: 36px;
            border-radius: 3px;
            color: #fff;
            border: none;
        }
        .weui_cells {
            margin-top: 0;
            line-height: 1.6;
            font-size: 15px;
        }
        .weui_cell{
            padding: 10px 50px 0 40px;
        }
        .weui_cells:after {
            border-bottom: 0;
        }
        .weui_panel_access .weui_panel_ft:after {
            content: none;
        }
        .weui_cell small{
            font-size:.7rem;
        }
        .kuaijiebankimg{
            border-radius: 50%;margin-top: -32px;width:30px;margin-right:5px;display:block;
        }
        .mask {
            position: relative;
        }
        .center{
            position: absolute;
            /*top: 25%;*/
            left: 50%;
            transform: translate(-50%, 20%);
        }
    </style>
</head>
<body style="background-color: #f8f8f8;">
<div class="weui_panel weui_panel_access">
    <div class="weui_panel_bd">
        <div class="weui_media_box weui_media_text">
            <h4 class="weui_media_title">商户：${merchInfo.name}</h4>
            <p class="weui_media_desc">支付类型：银联快捷</p>

        </div>
    </div>
    <div class="weui_panel_ft">交易金额：<span style="color:#56b16f">¥${amount}</span></div>
</div>

<div class="weui_cells_title" style="text-align: center;">请选择支付通道</div>
<div class="weui_cells weui_cells_access" id="kuaijieselectDiv">
</div>
<div class="mask">
    <div class="center">
        <button style="background-color:#40AFFE;width: 100px;" onclick="window.history.go(-1);">返回收银台</button>
    </div>
</div>
</body>
<script>
    var listKuaijie = [
        <#list merchChannels as merchChannel>
            {"channelId":"${merchChannel.channelId}","channelName":"${merchChannel.channelNickName}","maxTraPrice":${merchChannel.maxTraPrice?c},"minTraPrice":${merchChannel.minTraPrice?c},"mode":"${merchChannel.settleType}"},
        </#list>
    ];

    $(function(){
        //显示快捷通道列表
        $("#kuaijieselectDiv").html("");
        for (var i=0; i<listKuaijie.length; i++) {
            var channel = listKuaijie[i];
            var html = getKuaijieChannelDiv(channel,"channelselect(" + i + ")");
            $("#kuaijieselectDiv").append(html);

        }
    });

    /**
     * 选择快捷通道事件处理
     * @param index
     */
    function channelselect(index){
        var channel = listKuaijie[index];
        var money="${amount}";
        if(parseFloat(money)<channel.minTraPrice||parseFloat(money)>channel.maxTraPrice){
            $.alert("该支付通道限额"+channel.minTraPrice+"-"+channel.maxTraPrice, "提示");
            return;
        }
        var quickPayIng = layer.open({type: 2, content: '正在发起支付，请稍后...', shadeClose: false});
        $.ajax({
            url: 'cashier',
            type: 'POST',
            data: {
                "cmd":"quick",
                "amount": money,
                "mode":channel.mode,
                "merchantId": "${merchInfo.merchNo?c}",
                "remark":"${remark}"
            },
            dataType: 'json',
            success: function (data) {
                if (data && data.code == "10000") {
                    var form = $("<form method='post'></form>");
                    form.attr({ "action": "quickApi" });
                    for (var arg in data.data) {
                        var input = $("<input type='hidden'>");
                        input.attr({ "name": arg });
                        input.val(data.data[arg]);
                        form.append(input);
                    }
                    $(document.body).append(form);
                    form.submit();
                } else {
                    layer.close(quickPayIng);
                    layer.open({content: data.message, skin: 'msg', time: 10});
                }
            }
        });
    }

    function getKuaijieChannelDiv(channel,strfunction){
        var html = "";
        var moneyScope = "￥"+channel.minTraPrice + "-" + "￥"+channel.maxTraPrice;
        if (channel.maxTraPrice == 0 || channel.minTraPrice == 0) {
            moneyScope = "￥10-￥50000";
        }
        html += "<a href='javascript:;' class='weui_cell' id='sel' onclick='" + strfunction + "'>";
        html += "	<div class='weui_cell_hd'><img src='static/cashier/images/paymentIcon/yinlian.png' class='kuaijiebankimg'></div>";
        html += "	<div class='weui_cell_bd weui_cell_primary' style='color:#888;'>";
        html += "		<p style='font-size:.9rem;color:black;'>" + channel.channelName + "<small></small></p>";
        html += "		<small>通道限额：<small style='color:#56B16F'>"+moneyScope+"</small></small>";
        html += "		<small><small style='color:#56B16F'>　</small></small>";
        html += "		<p><small style='color:#f60'><small style='color:#f60'>　</small></small></p>";
        html += "	</div>";
        html += "	<div class='weui_cell_ft'></div>";
        html += "</a>";
        return html;
    }
</script>
</html>