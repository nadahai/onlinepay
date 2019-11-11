<!DOCTYPE html>
<html lang="en">
<head>
    <title>收银台-网关支付</title>
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
        .mask {
            position: relative;
        }
        .center{
            position: absolute;
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
            <p class="weui_media_desc">支付类型：网关支付</p>

        </div>
    </div>
    <div class="weui_panel_ft">交易金额：<span style="color:#56b16f">¥${amount}</span></div>
</div>

<div class="weui_cells_title" style="text-align: center;">请选择支付银行</div>
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
        {"bankName":"邮储银行","bankCode":"PSBC"},
        {"bankName":"工商银行","bankCode":"ICBC"},
        {"bankName":"招商银行","bankCode":"CMB"},
        {"bankName":"农业银行","bankCode":"ABC"},
        {"bankName":"建设银行","bankCode":"CCB"},
        {"bankName":"北京银行","bankCode":"BOB"},
        {"bankName":"交通银行","bankCode":"BCOM"},
        {"bankName":"兴业银行","bankCode":"CIB"},
        {"bankName":"民生银行","bankCode":"CMBC"},
        {"bankName":"光大银行","bankCode":"CEB"},
        {"bankName":"中国银行","bankCode":"BOC"},
        {"bankName":"平安银行","bankCode":"PABC"},
        {"bankName":"中信银行","bankCode":"CITIC"},
        {"bankName":"深圳发展银行","bankCode":"SDB"},
        {"bankName":"广东发展银行","bankCode":"GDB"},
        {"bankName":"上海银行","bankCode":"BOFS"},
        {"bankName":"上海浦东发展银行","bankCode":"SPDB"},
        {"bankName":"华夏银行","bankCode":"HXB"},
        {"bankName":"北京农商","bankCode":"BRCB"},
        {"bankName":"南京银行","bankCode":"BON"},
        {"bankName":"宁波银行","bankCode":"NBCB"}
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
        var quickPayIng = layer.open({type: 2, content: '正在发起支付，请稍后...', shadeClose: false});
        $.ajax({
            url: 'cashier',
            type: 'POST',
            data: {
                "cmd":"gateway",
                "amount": "${amount}",
                "mode":"${mode}",
                "merchantId": "${merchInfo.merchNo?c}",
                "bankAbbr":channel.bankCode,
                "remark":"${remark}"
            },
            dataType: 'json',
            success: function (data) {
                if (data && data.code == "10000") {
                    var form = $("<form method='post'></form>");
                    form.attr({ "action": "gatewayPayApi" });
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
        html += "<a href='javascript:;' class='weui_cell' id='sel' onclick='" + strfunction + "'>";
        html += "	<div class='weui_cell_hd'></div>";
        html += "	<div class='weui_cell_bd weui_cell_primary' style='color:#888;'>";
        html += "		<p style='font-size:.9rem;color:black;'>" + channel.bankName + "<small></small></p>";
        html += "		<p><small style='color:#f60'><small style='color:#f60'>　</small></small></p>";
        html += "	</div>";
        html += "	<div class='weui_cell_ft'></div>";
        html += "</a>";
        return html;
    }
</script>
</html>