<html>
<head>
    <meta name=viewport content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
    <title>收款</title>
    <script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/jquery.min.js"></script>
    <script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/jquery.qrcode.min.js"></script>
    <script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/utf.js"></script>

    <style type="text/css">
        body{background-color:#d2d2d254;margin:0px 0px;border-top: 5px solid #5eb95e;}
        h3{font-size:24px;}
        .title-first{text-align: center;color:#5eb95e;padding-top:15px;}
        .title-amount{margin-top: 1px;margin-bottom: 0px;}
        .title-order{margin: 2% auto;color:#5eb95e;}
        .title-suggest{float:right;margin-top: -48px;}
        .title-suggest a{text-decoration:none;}

        .click-button{
            padding: 10px 16px;
            font-size: 18px;
            line-height: 1.3333333;
            border-radius: 6px;
            color: #fff;
            background-color: #5eb95e;
            border-color: #5eb95e;
            text-decoration:none;
        }
        .body-list{text-align:left;padding-left:3%;color:#5eb95e;}
        .body-item{padding-left:10px;}
        .body-item p{margin: 2% auto;color:red;}
        #qrCode{width:260px;margin:auto auto;}
        #qrCodeIco{
            position: absolute;
            width: 100px;
            margin: 80px 80px;
            /*
            width: 40%;
            margin: 18% 18%;
            */
        }
    </style>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12" >
            <h3 class="title-first">收银台</h3>
            <span class="title-suggest"><a style="color:red;" href="/onlinepay/suggest/index?orderNo=${orderNo}">一键反馈&nbsp;&nbsp;</a></span>
            <div class="row" style="text-align: center;">
                <div class="col-sm-2">
                    <h3 class="title-amount">￥${amount}</h3>
                    <h4 class="title-order"><span style="font-weight:normal;">订单号：</span>${orderNo}</h4>
                </div>
            </div>

            <div id="qrCode" style="text-align: center;">
                <img id="qrCodeIco" src="${request.contextPath}/static/images/hy/wechat/logo.png" />
            </div>

            <br />
            <div class="row" style="text-align: center;">
                <#--<p>-->
                    <#--<a class="click-button" href="alipayqr://platformapi/startapp?saId=10000007&qrcode=http://test.mall51.top/onlinepay/hyh5api/cashier/4986d047b40c837706e69615f81c25b13f02db507377f0f5" style="font-size:18px;">&nbsp;&nbsp;立即支付&nbsp;&nbsp;</a>-->
                <#--</p>-->
                <div class="body-list">
                    <h4>温馨提示:</h4>
                    <div class="body-item">
                        <p style="font-size:14px;">请截图后使用微信扫一扫加载此码</p>
                    </div>
                </div>
            </div>
            <br />
        </div>
    </div>
</div>

<script type="text/javascript">
    var payUrl = "${payUrl}";
    $(function() {
        $("#qrCode").qrcode({
                    render : "canvas",
                    text : payUrl,
                    width : "260",
                    height : "260",
                    background : "#f0f0f0",
                    foreground : "#000000"
                    // src: "http://qrcode.linwx420.com:8088/static/img/noway/alipay36.png"
                    // src: "./11.png"
                }
        );
    });
    /*
    var margin = ($("#qrCode").height() - $("#qrCodeIco").height()) / 2;
    $("#qrCodeIco").css("margin", margin);
    */
</script>
</body>
</html>