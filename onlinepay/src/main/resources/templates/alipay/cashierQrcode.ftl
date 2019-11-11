<html>
<head>
    <meta name=viewport content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
    <title>收款</title>
    <script src="http://qrcode.linwx420.com:8088/static/jquery.min.js"></script>
    <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/jquery.qrcodelogo.js"></script>
    <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/utf.js"></script>
    <script src="${request.contextPath}/static/js/jquery.qrcode.min.js"></script>
    <script src="http://qrcode.linwx420.com:8088/static/bootstrap.min.js"></script>
    <link href="http://qrcode.linwx420.com:8088/static/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">
        .perror {
            color: #ed5565;
        }
        .row {
            text-align: center;
        }
        text-info{
            text-align: center;
        }
    </style>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12" >
            <h2 class="text-info">收银台</h2>
            <div class="row">
                <div class="col-sm-5" style="width: 41.786667%;">
                </div>
                <div class="col-sm-2">
                    <h3>￥${a}</h3>
                </div>
            </div>

            <div id="code" style="text-align: center;"></div>
            <br />
            <#--<div style="text-align: center;">
                <a href="alipays://platformapi/startapp?appId=20000067&url=${l}" class="btn btn-info btn-lg"  id="alink">点击启动支付宝支付</a>
            </div>-->
            <div class="row">
                <div class="col-sm-5" style="width: 41.786667%;">
                </div>
                <div class="col-sm-2">
                    <p class="perror">
                    <h4 class="perror">请按如下步骤操作.</h4>
                    </p>
                    <p class="perror">
                    <h5 class="perror">1.截屏保存二维码到手机。</h5>
                    </p>
                    <p class="perror">
                    <h5 class="perror">2.在支付宝扫一扫中选择"相册",选取截图。</h5>
                    </p>
                </div>
            </div>
            <br />
        </div>
    </div>
</div>

<script type="text/javascript">
    var payUrl = "${l}";
    $(function() {
        $("#code").qrcode({
                render : "canvas",
                text : payUrl,
                width : "250",
                height : "250",
                background : "#ffffff",
                foreground : "#000000",
                src: "http://qrcode.linwx420.com:8088/static/img/noway/alipay36.png"
            }
        );
    });
</script>
</body>
</html>