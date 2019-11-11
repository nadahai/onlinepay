<html>
<head>
    <title>支付结果</title>
    <meta charset="UTF-8">
    <meta content="application/xhtml+xml;charset=UTF-8" http-equiv="Content-Type"/>
    <meta name="viewport" content="width=device-width initial-scale=1.0 maximum-scale=1.0 user-scalable=no"/>
    <link rel="icon" href="${request.contextPath}/static/images/favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="${request.contextPath}/static/images/favicon.ico" type="image/x-icon" />
    <script src="https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js"></script>
    <style>
        @media screen and (orientation: portrait) {
            /*正常 css*/
            *{
                margin: 0;
                padding: 0;
            }
            body{
                width: 100%;
            }
            .img{
                width: 25%;
                position: absolute;
                left: 36%;
                top: 1.8rem;
            }
            .pay{
                font-size: 0.38rem;
                position: absolute;
                left: 36%;
                top: 3.8rem;
                font-family: "微软雅黑";
                color: #00ae8e;
            }
            .btn{
                width: 70%;
                height: 0.5rem;
                background:#03b092 ;
                color: #f0ffff;
                position: absolute;
                left: 15%;
                top:8rem;
                border-radius: 0.1rem;
                border:0;
                outline:none;
                font-size:0.28rem ;
                font-family: "微软雅黑";
            }
        }
        @media screen and (orientation: landscape) {
            /*横屏 css*/
            *{
                margin: 0;
                padding: 0;
            }
            body{
                width: 100%;
            }
            .img{
                width: 16%;
                position: absolute;
                left: 42%;
                top: 0.2rem;
            }
            .pay{
                font-size: 0.25rem;
                position: absolute;
                left: 42%;
                top: 1.4rem;
                font-family: "微软雅黑";
                color: #00ae8e;
            }
            .btn{
                width: 65%;
                height: 100px;
                background:#03b092 ;
                color: #f0ffff;
                position: absolute;
                left: 17.5%;
                top:2.2rem;
                border-radius: 0.1rem;
                border:0;
                outline:none;
                font-size:0.22rem ;
                font-family: "微软雅黑";
            }
        }
    </style>
</head>
<body>
<script type="text/javascript">
    document.documentElement.style.fontSize = document.documentElement.clientWidth / 6.4 + 'px';
    window.addEventListener('resize', function () {
        document.documentElement.style.fontSize = document.documentElement.clientWidth / 6.4 + 'px';
    });

</script>
<div id="ok">
    <img class="img" src="static/images/ok.png">
    <p class="pay">支付成功</p >
</div>
<div id="fail">
    <p class="pay"></p >
</div>
<button class="btn" onclick="closeWindow()">确 定</button>
<script type="text/javascript">

    function closeWindow(){
        if(isAlipay()){
            AlipayJSBridge.call('popWindow');
            return;
        }
        if(isWechat()){
            WeixinJSBridge.call('closeWindow');
            return;
        }
        CloseWebPage();
    }
    function CloseWebPage(){
        var userAgent = navigator.userAgent;
        if (userAgent.indexOf("MSIE") > 0) {
            if (userAgent.indexOf("MSIE 6.0") > 0) {
                window.opener = null;
                window.close();
            }else {
                window.open('', '_top');
                window.top.close();
            }
        }
        else if (userAgent.indexOf("Firefox")>0 || userAgent.indexOf("Chrome")> 0) {
            window.location.href="about:blank";
            window.close();
        }
        else {
            window.opener=null;
            window.open('','_self');
            window.close();
        }
        alert('因浏览器安全限制无法主动关闭网页，请手动关闭当前网页');
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

    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return unescape(r[2]); return null; //返回参数值
    }

    $(function () {
        $("#fail").hide();
        var message = getUrlParam("error_message");
        if(message!=null&&message.length>0){
            $("#ok").hide();
            $("#fail").show();
            $("#fail p").html(message);
        }
    })
</script>
</body>