<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/amazeui.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/index.css">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/cardManager.css">
    <title>银行卡管理</title>
</head>

<body>
<script src="${request.contextPath}/static/js/hy/wechat/jquery.min.js"></script>
<script src="${request.contextPath}/static/js/hy/wechat/amazeui.js"></script>
<script src="${request.contextPath}/static/js/hy/wechat/handlebars.min.js"></script>
<script src="${request.contextPath}/static/js/hy/wechat/amazeui.widgets.helper.js"></script>
<script type="text/x-handlebars-template" id="wechatPay">
    {{>header header}}
</script>
<div class="common-container">
    <ul class="manager-ul">
    </ul>
    <button class=" am-btn am-btn-success am-btn-block" type="button" id="return-button">返回</button>
</div>
<div class="am-modal am-modal-prompt" tabindex="-1" id="cancel-bind-modal">
    <div class="am-modal-dialog modal-position" style="margin-top: 180px;">
        <div class="am-modal-hd" style="padding: 30px;">确定要解除绑定吗？</div>
        <div class="am-modal-bd" style="padding: 0;">
        </div>
        <div class="am-modal-footer">
            <span class="am-modal-btn" data-am-modal-ok>取消</span>
            <span class="am-modal-btn" data-am-modal-cancel>确定</span>
        </div>
    </div>
</div>
<div class="am-modal am-modal-prompt" tabindex="-1" id="cancel-bind-result">
    <div class="am-modal-dialog modal-position" style="margin-top: 180px;">
        <div class="am-modal-hd" style="padding: 30px;">解绑成功</div>
        <div class="am-modal-bd" style="padding: 0;">
            <!-- 若失败，这里写失败原因 -->
        </div>
        <div class="am-modal-footer">
            <span class="am-modal-btn" data-am-modal-cancel>确定</span>
        </div>
    </div>
</div>

<script>
    $(function () {
        var $tpl = $('#wechatPay'),
                tpl = $tpl.text(),
                template = Handlebars.compile(tpl),
                data = {},
                html = template(data);

        $tpl.before(html);
        var cardList = [];
        var cardInfo = ${cardJson};
        for (var i = 0;i< cardInfo.length; i++) {
            // var bankInfo = JSON.parse(cardInfo[i]);
            // console.log(bankInfo);
            // if(bankINfo.bankType==2){
            //     continue;
            // }
            var bankName = cardInfo[i].bankName;
            // var bankName = bankInfo.bankName;
            if ( bankName.length>6 ){
                bankName = bankName.substring(0,6)+"…";
            }
            cardList.push(
                    {id: cardInfo[i].id, cardName:bankName+"("+cardInfo[i].cardNo.substring(cardInfo[i].cardNo.length-4)+")",
                        cardNo: cardInfo[i].cardNo, idNo: cardInfo[i].idNo}
            );
            // cardList.push(
            //         {id: bankInfo.id, cardName:bankName+"("+bankInfo.cardNo.substring(bankInfo.cardNo.length-4)+")",
            //             cardNo: bankInfo.cardNo, idNo: bankInfo.idNo}
            // );
        }
        if (cardInfo.length == 0 || cardList.length == 0) {
            return;
        }
        var setPaywayContent = '';
        for (var i = 0; i < cardList.length; i++) {
            setPaywayContent += '<li><span class="card-name">';
            setPaywayContent += cardList[i].cardName;
            setPaywayContent += '</span>';
            setPaywayContent += '<span class="cancel-bind" data-id="' + cardList[i].id + '">解除绑定</span></li>';
            setPaywayContent += '<hr data-am-widget="divider" style="" class="am-divider am-divider-default" />';
        }
        $(".manager-ul").html(setPaywayContent);

        // 解绑银行卡
        $(".cancel-bind").on("click", function (e) {
            var cardId = e.target.getAttribute("data-id");
            $("#cancel-bind-modal").modal({
                relatedTarget: this,
                closeViaDimmer: false,
                onCancel: function () {
                    // 这里写ajax请求，请求成功后跳出提示框，点击确定刷新页面
                    $("#cancel-bind-result").modal({
                        relatedTarget: this,
                        closeViaDimmer: false,
                        onCancel: function () {
                            // 这里写ajax请求，请求成功后刷新页面
                            var url = "${request.contextPath}/hyh5api/delCardByIdNo/${no}";
                            $.ajax({
                                url:url,
                                type:'post',
                                data:{
                                    "id":cardId,
                                    "payType": "2"
                                },
                                dataType : 'json',
                                success : function(data){
                                    if(data !=null){
                                        if (data.code == '520000'){
                                            // layer.open({content: "解绑成功！" ,skin: 'msg' ,time: 3 });
                                            history.go(0);
                                        }else{
                                            layer.open({content: "解绑失败！" ,skin: 'msg' ,time: 2 });
                                        }
                                    }else{
                                        layer.open({content: "解绑失败！" ,skin: 'msg' ,time: 2 });
                                    }
                                }
                            });
                        }
                    });
                }
            });
        });
    });
    $("#return-button").click(function(){
        location.href = "${request.contextPath}/hyh5api//cashier/${no}";
    })
</script>
</body>

</html>