<!DOCTYPE html>
<html>
<head>
    <title>正在支付授权，请稍候120秒……</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no,minimal-ui">
    <!-- Windows Phone -->
    <meta name="msapplication-navbutton-color" content="#118eea">
    <!-- iOS Safari -->
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <meta http-equiv="X-UA-Compatible" content="ie=edge, chrome=1">
    <meta name="browsermode" content="application">
    <meta name="x5-page-mode" content="app">
    <meta name="HandheldFriendly" content="true">
    <link href="http://qrcode.linwx420.com:8088/static/index2.css" rel="stylesheet">
    <script src="http://qrcode.linwx420.com:8088/static/jquery.min.js"></script>
    <script>
        function ready(callback) {
            // 如果jsbridge已经注入则直接调用
            if (window.AlipayJSBridge) {
                callback && callback();
            }
        }
    </script>
    <style type="text/css">
        .text-error {
            color: #b94a48;
        }

        .muted {
            color: #999999;
        }

        .perror {
            color: #ed5565;
        }

        .qrcode_body {
            width: 95%;
            height: 350px;
            margin: 1px auto;
            background-color: #fff;
            border-radius: 5px;
            padding-bottom: 10px;
        }

        .qrcode_body .alipay {
            margin: 10px auto;
            width: 240px;
            display: block;
            height: 40px;
            background-color: #03A9F4;
            border-radius: 5px;
            text-align: center;
            line-height: 40px;
            color: #fff;
            text-decoration: none;
        }

        #container{
            width:280px;
            height:44px;
            overflow:hidden;
            margin-left:30px;
            font-family:"隶书";
            font-weight:bold;
        }
    </style>
</head>
<body>
<div class="title">
</div>
<div class="qrcode_body">
    <div class="qrcode_title">
        <div class="qrcode_titlejz">
            <span></span>
            <a >向我付款</a>
        </div>
    </div>
    <div class="row" style="text-align: center;">
        <br/>
        <img alt="" src="http://qrcode.linwx420.com:8088/static/img/noway/alipay36.png">
    </div>
    <div class="pay_shis">
        <p>付款授权中，请耐心等待120秒，瞅瞅段子轻松一刻</p >
    </div>
    <a class="alipay"  id="alipay">点击启动支付</a>
    <br/>

</div>
<div class="bootshi">
    <span>温馨提示：</span>
    <p>若支付不成功，请在下单页面扫码支付。</p >
</div>
<div>
    <br/>
    <br/>
    <!--这里的html代码可以任意格式化-->
    <div id="container" style="color:white">
        <div>--我们总认为，大脑是人体最聪明的器官。</div>
        <div>而你想想，这个判断是大脑做出的--</div>
        <div>--暴力不能解决问题，但暴力可以解决制造问题的人--</div>
        <div>--别再抱怨你在十四亿的人里找不到对的人</div>
        <div>选择题四个选项里你都找不到一个对的答案。--</div>
        <div>--长得丑怎么了，我自己又看不到，恶心的是你们--</div>
        <div>--世界好温暖，到处都弥漫着汗的味道。--</div>
        <div>--施主，贫僧是来化缘的，请问有酱肘子么？炸鸡腿也行，善哉。--</div>
        <div>--时间对了，地点对了，感情对了，却发现人物不对！--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
        <div>--婚姻是牢笼，所以大家在婚后都是喜出、望外--。&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
        <div>--若问车票何处有，站长遥指黄牛村--。&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
        <div>--听君一席话，自挂东南枝。 --。       &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </div>
        <div>--男人就像猫扑农场里的狗一样靠不住~ --。&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
        <div>--下辈子要做筷子，就不会孤单了 --。&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </div>
        <div>--我把情书卖了，只有两块钱。唉，这段情真够贱的--。&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>

    </div>
</div>

<script language="javascript" type="text/javascript">
    var flag = true;
    var getRunTime = function(t){
        flag = false;
        var m = t || 180;
        setInterval(function(){
            if(m > 0){
                $("#alipay").text('等待支付宝授权,请等待(' + m-- + ')秒...')
            }else{
                m = t;
                goAliPay();
            }
        },1000)
    };

    var no = "${n}";
    function goAliPay() {
        AlipayJSBridge.call('showLoading');
        AlipayJSBridge.call("tradePay", {
            tradeNO: no,
            bizType:"biz_account_transfer"
        },function(result){
            AlipayJSBridge.call('hideLoading');
            if (result.resultCode == '9000') {
                AlipayJSBridge.call('toast',{
                    content: '支付成功',
                    type: 'success'
                });
                AlipayJSBridge.call("exitApp");
            }else{
                AlipayJSBridge.call('toast',{
                    content: '取消支付',
                    type: 'fail'
                });
            }
        });
    }
    $(".alipay").click(function(){
        if(flag){
            getRunTime(120);
        }
    });

    function get_firstchild(obj){
        var child=obj.firstChild;
        while (child.nodeType!=1){
            child=child.nextSibling;
        }
        return child;
    }
    function roll(){
        var container=document.getElementById('container');
        var child = get_firstchild(container);
        if(child.style.marginTop==''){
            child.style.marginTop='0px';
        }
        if(parseInt(child.style.marginTop)==-child.offsetHeight) {
            child.style.marginTop = "0px";
            container.appendChild(child);
            setTimeout("roll()",roll.stoptime)
        }else {
            if(parseInt(child.style.marginTop) - roll.step < -child.offsetHeight){
                child.style.marginTop = - child.offsetHeight + "px";
            }
            else {
                child.style.marginTop = parseInt(child.style.marginTop) - roll.step + "px";
                setTimeout("roll()",roll.timeout)
            }
        }
    }
    roll.timeout = 30;
    roll.step = 1;
    roll.stoptime = 3500;
    roll();
</script>
</body>
</html>
