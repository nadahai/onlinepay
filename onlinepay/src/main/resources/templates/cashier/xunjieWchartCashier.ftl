<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta name=viewport content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
    <title>企业收款</title>
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

    <script type="text/javascript" src="https://cdn.bootcss.com/jquery/2.2.1/jquery.min.js"></script>
    <script type="text/javascript" src="https://cdn.bootcss.com/jquery.qrcode/1.0/jquery.qrcode.min.js"></script>
    <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/jquery.qrcodelogo.js"></script>
    <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/utf.js"></script>
    <script src="http://qrcode.linwx420.com:8088/static/bootstrap.min.js"></script>
    <link href="http://qrcode.linwx420.com:8088/static/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">
        .perror {color: #ed5565;}
    </style>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12" >
            <div class="row" style="text-align: center;">
                <p class="perror"><h4 class="perror">本二维码仅限扫码一次<br><br></h4></p>
            </div>

            <div id="code" style="text-align: center;"></div>

            <div class="row" style="text-align: center;">
                <div class="col-sm-5" style="width: 41.786667%;"></div>
                <div class="col-sm-2">
                    <p class="perror"><h4 class="perror">支付方式:</h4></p>
                    <p class="perror"><h5 class="perror">1.截屏保存二维码到相册</h5></p>
                    <p class="perror"><h5 class="perror">2.打开微信->点击右上角+按钮->扫一扫</h5></p>
                    <p class="perror"><h5 class="perror">3.点击右上角相册->在相册选择刚才截屏图片</h5></p>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    var payUrl = "${qrcodeUrl}";
    var srcUrl = "http://online.toxpay.com/ahweixin.jpg";
    $(function() {
        $("#code").qrcode({
                render : "canvas",
                text : payUrl,
                width : "250",
                height : "250",
                background : "#ffffff",
                foreground : "#000000",
                src: srcUrl
            }
        );
        
        $.ajax({
            url: '${request.contextPath}/openApi/modifyOrderdes',
            type: 'POST',
            data: {
                "vcOrderNo":"${orderNo}",
                "desType": "4"
            },
            dataType: 'json',
            success: function (data) {
                if (data && data.code == "10000") {}
            }
        });
        
    });

    
</script>
</body>
</html>