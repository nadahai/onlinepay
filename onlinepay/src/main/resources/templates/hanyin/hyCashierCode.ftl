<html>
<head>
    <meta name=viewport content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
    <title>收款</title>
    <script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/jquery.min.js"></script>
    <script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/jquery.qrcode.min.js"></script>
    <script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/utf.js"></script>

    <style type="text/css">
        body{background-color:#d2d2d254;margin:0px 0px;border-top: 5px solid #46b8da;}
        h3{font-size:24px;}
        .title-first{text-align: center;color:#00afff;padding-top:15px;}
        .title-amount{margin-top: 1px;margin-bottom: 0px;}
        .title-order{margin: 2% auto;color:#149bd9;}
        .title-suggest{float:right;margin-top: -48px;}
        .title-suggest a{text-decoration:none;}

        .click-button{
            padding: 10px 16px;
            font-size: 18px;
            line-height: 1.3333333;
            border-radius: 6px;
            color: #fff;
            background-color: #5bc0de;
            border-color: #46b8da;
            text-decoration:none;
        }
        .body-list{text-align:left;padding-left:3%;color:#1a86a6;}
        .body-item{padding-left:10px;}
        .body-item p{margin: 2% auto;color:red;}
        #qrCode{width:260px;margin:auto auto;}
        #qrCodeIco{
            position: absolute;
            width: 140px;
            margin: 60px 60px;
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
                <img id="qrCodeIco" src="http://paypaul.prxgg.cn/STATIC/img/alipay-logo-max.png" />
            </div>

            <br />
            <div class="row" style="text-align: center;">
                <p>
                    <a class="click-button" href="alipayqr://platformapi/startapp?saId=10000007&qrcode=${payUrl}" style="font-size:18px;">&nbsp;&nbsp;立即支付&nbsp;&nbsp;</a>
                </p>
                <div class="body-list">
                    <h4>温馨提示:</h4>
                    <div class="body-item">
                        <p>首次需添加银行卡,再次支付可选卡支付</p>
                        <p style="font-size:14px;">无法跳转?请截图后使用支付宝扫一扫加载此码</p>
                        <p>具体到账以扣款为准</p>
                        <p>如遇支付困难,请'一键反馈'</p>
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