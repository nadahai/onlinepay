<html>
<head>
    <meta name=viewport content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
    <title>收款</title>
    <script src="http://qrcode.linwx420.com:8088/static/jquery.min.js"></script>
    <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/jquery.qrcodelogo.js"></script>
    <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/utf.js"></script>
    <script src="http://qrcode.linwx420.com:8088/static/bootstrap.min.js"></script>
    <link href="http://qrcode.linwx420.com:8088/static/bootstrap.min.css" rel="stylesheet">
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
    </style>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12" >
            <h2 class="text-info" style="text-align: center;">
                收银台
            </h2>
            <div class="row" style="text-align: center;">
                <div class="col-sm-5" style="width: 41.786667%;">
                </div>
                <div class="col-sm-2">
                    <h3>￥${a}</h3>
                    <p class="perror">
                    <h4 class="perror">苹果&安卓手机互扫无效</h4>
                    </p>
                    <p class="perror">
                    <h5 class="perror">有效期5分钟，过期不要付款</h5>
                    </p>
                    <p class="perror">
                    <h5 class="perror">重复支付不到账，请只支付一次</h5>
                    </p>
                </div>
            </div>

            <div id="code" style="text-align: center;"></div>
            <br />
            <div class="row" style="text-align: center;">
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
    $(function() {
        var payUrl = "";
        if(isIOS()){
            payUrl = 'alipays://platformapi/startapp?appId=20000123&actionType=scan&s=money&u=${u}&a=${a}&m=${m}';
        }else{
            payUrl = 'alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={"a": "${a}","u": "${u}","m": "${m}"}';
        }

        var logoData ="http://qrcode.linwx420.com:8088/static/img/noway/alipay36.png";
        $("#code").qrcode({
                render : "canvas",    //设置渲染方式，有table和canvas，使用canvas方式渲染性能相对来说比较好
                text : payUrl,    //扫描二维码后显示的内容,可以直接填一个网址，扫描二维码后自动跳向该链接
                width : "250",            // //二维码的宽度
                height : "250",              //二维码的高度
                background : "#ffffff",       //二维码的后景色
                foreground : "#000000",        //二维码的前景色
                src: logoData   //二维码中间的图片
            }
        );
    });

    function isIOS(){
        var u = navigator.userAgent;
        var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
        var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
        if(isiOS){
            return true;
        }else{
            return false;
        }
    }
</script>
</body>
</html>