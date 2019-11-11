<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/amazeui.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/css/hy/wechat/result.css">
    <link href="https://cdn.bootcss.com/animate.css/3.5.2/animate.css" rel="stylesheet">
    <title>付款失败</title>
</head>

<body>
<script src="${request.contextPath}/static/js/hy/wechat/jquery.min.js"></script>
<div class="result-container">
    <span class="am-icon-times-circle am-icon-lg failed-icon"></span>
    <p class="failed-tip">付款失败</p>
    <p>${dataResult}</p>
    <p>${orderNo}</p>
    <button class=" am-btn failed-btn" type="button" id="next-button">返回商家</button>
</div>

<script>
    $("#next-button").click(function () {
        WeixinJSBridge.invoke('closeWindow',{},function(res){
        });
    });
    // function ready(callback) {
    //     // 如果jsbridge已经注入则直接调用
    //     if (window.AlipayJSBridge) {
    //         callback && callback();
    //     } else {
    //         // 如果没有注入则监听注入的事件
    //         document.addEventListener('AlipayJSBridgeReady', callback, false);
    //     }
    // }
    // ready(function() {
    //     document.querySelector('.J_demo').addEventListener('click', function() {
    //         AlipayJSBridge.call('exitApp');
    //     });
    // });
    // WeixinJSBridge.call('closeWindow');
</script>
</body>

</html>