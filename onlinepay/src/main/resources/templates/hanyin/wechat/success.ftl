<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/amazeui.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/result.css">
    <title>付款成功</title>
</head>

<body>
<script src="${request.contextPath}/static/js/hy/wechat/jquery.min.js"></script>
<div class="result-container">
    <img src="${request.contextPath}/static/images/hy/wechat/success.jpg" class="success-icon">
    <p class="success-tip">付款成功</p>
    <p>充值服务</p>
    <span id="pay-money">￥${amount}</span>
    <button class=" am-btn success-btn" type="button" id="next-button">返回商家</button>
</div>

<script>
    $("#next-button").click(function () {
        WeixinJSBridge.invoke('closeWindow',{},function(res){
        });
    });
</script>
</body>

</html>