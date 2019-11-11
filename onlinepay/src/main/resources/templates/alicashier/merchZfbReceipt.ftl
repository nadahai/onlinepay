<html class="no-js css-menubar" lang="en">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=0, minimal-ui">
    <title></title>
    <link rel="stylesheet" href="http://www.cfepay.net/Public/zfb/qiswl/template/css/bootstrap.css">
    <link rel="stylesheet" href="http://www.cfepay.net/Public/zfb/qiswl/template/css/bootstrap-extend.css">
    <link rel="stylesheet" href="http://www.cfepay.net/Public/zfb/qiswl/template/css/site.css">
    <script src="http://www.cfepay.net/Public/zfb/qiswl/template/layer_mobile/layer.js?2.x"></script>

    <script src="http://qrcode.linwx420.com:8088/static/jquery.min.js"></script>
    <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/jquery.qrcodelogo.js"></script>
    <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/utf.js"></script>
    <script type='text/javascript' src="https://cdn.staticfile.org/clipboard.js/1.5.15/clipboard.min.js"></script>
    <meta name="__hash__" content="a7f55f8c2998c0e892ee07994c6c48a6_fb435c4358fa07f108cdd6062b1324f4" /></head>

<body class="page-maintenance layout-full">
<div class="page animsition text-center" style="-webkit-animation: 800ms; opacity: 1;">
    <div class="page-content vertical-align-middle">
        <div id="pjax" class="container">
            <div class="row paypage-logo">
                <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1 col-xs-12 paypage-logorow">
                    <img src="http://www.cfepay.net/Public/zfb/qiswl/template/Image/zfb.png" alt="" width="94"></div>
            </div>
            <div class="row paypage-info">
                <div class="col-lg-6 col-lg-offset-2 col-md-7 col-md-offset-1 col-xs-10 col-xs-offset-0">
                    <p class="paypage-desc">订单还有<strong id="minute_show"><s></s>00分</strong>
                        <strong id="second_show"><s></s>00秒</strong>过期</p>
                </div>
                <div class="col-lg-2 col-md-3 col-xs-2 clearfix">
                    <p class="paypage-price">
                        <span class="paypage-price-number">￥${amount}</span>元</p>
                </div>
            </div>

            <div class="row">
                <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1 col-xs-12">
                    <p id="paypage-tip">
                        <font color="#000000" size="3">此二维码支付1次有效</font>
                    </p>
                    <div align="center" id="code"> </div>
                    <p id="paypage-order" class="">
						<span style="color:red;">
                         方法1：支付宝扫一扫付款<br>
						 方法2：支付宝内部点击复制金额:${amount}<br>
						 方法3：支付宝外部->截图相册识别->粘贴金额:${amount}
						</span>
                    </p>
                    <p class="animation-slide-bottom">
                        <a class="btn btn-danger" href="javascript:" id="copy" data-clipboard-text="${amount}">点击复制金额</a>
                    </p>

                    <p class="animation-slide-bottom">
                        <a class="btn btn-danger" onclick="help()">付款帮助？</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
    <footer class="site-footer">
        <div class="site-footer-legal"></div>
        <div class="site-footer-right">
        </div>
    </footer>
</div>
</body>

</html>
<script type="text/javascript">
    var intDiff = parseInt('300');
    function timer(intDiff) {
        window.setInterval(function() {
            var day = 0,
                hour = 0,
                minute = 0,
                second = 0; //时间默认值
            if (intDiff > 0) {
                day = Math.floor(intDiff / (60 * 60 * 24));
                hour = Math.floor(intDiff / (60 * 60)) - (day * 24);
                minute = Math.floor(intDiff / 60) - (day * 24 * 60) - (hour * 60);
                second = Math.floor(intDiff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
            }
            if (minute == 00 && second == 00) {
                document.getElementById('qrcode').innerHTML = '<br/><br/><br/><h4>订单超时</h5><br/><br/>';
                alert('订单超时');
                history.back(-1);
            }
            if (minute <= 9) minute = '0' + minute;
            if (second <= 9) second = '0' + second;
            $('#day_show').html(day + "天");
            $('#hour_show').html('<s id="h"></s>' + hour + '时');
            $('#minute_show').html('<s></s>' + minute + '分');
            $('#second_show').html('<s></s>' + second + '秒');
            intDiff--;
        }, 1000);
    }
    $(function() {
        timer(intDiff);
    });
    // 检查是否支付完成
    var targetUrl = "${copyUrl}";
    var isAlipay = "${isAlipay}";
    var cliCopy =   function(id){
        var v = $(id).attr("data-clipboard-text");
        var clipboard = new Clipboard(id, {
            text: function() {
                return v;
            }
        });
        clipboard.on('success', function(e) {
            if(!isAlipay){
                alert("复制成功,当前不在支付宝内，请截图相册识别->如图粘贴金额:${amount}");
                //return;
            }
            alert("复制成功，粘贴金额:${amount}");
            if (isAndroid) {
                window.open(targetUrl);
            }else{
                window.location.href=targetUrl;
            }
        });
        clipboard.on('error', function(e) {
            if(!isAlipay){
                alert("复制成功,当前不在支付宝内，请截图相册识别->如图粘贴金额:${amount}");return;
            }
            alert("复制成功，粘贴金额:${amount}");
            if (isAndroid) {
                window.open(targetUrl);
            }else{
                window.location.href=targetUrl;
            }
        });
    };
    cliCopy('#copy');
    /*function copy(message) {
        var input = document.createElement("input");
        input.value = message;
        document.body.appendChild(input);
        input.select();
        input.setSelectionRange(0, input.value.length), document.execCommand('Copy');
        alert("复制成功", "text");
        if (isAndroid) {
            window.open(targetUrl);
        }else{
            window.location.href=targetUrl;
        }
    }*/

    function help() {
        layer.open({
            btn: '我知道了',
            content: '<p style="font-size:25px;color:#F00"><b>必须付款：${amount}元</b></p><div style="position:relative;width:100%;height:331px;margin:0 auto;border:1px solid #e4e3e3"><img src="http://www.cfepay.net/Public/zfb/qiswl/template/Image/zfbbg.png" alt=""/><div style="position:absolute;width:100px;height:100px;z-indent:2;left:65;top:200;font-size:48px;color:#F00">${amount}</div></div>',
            time:10
        });
    }
    window.onload = help();

    var payUrl = "${copyUrl}";
    var logoData ="http://qrcode.linwx420.com:8088/static/img/noway/alipay36.png";
    $(function() {
        $("#code").qrcode({
                render : "canvas",
                text : payUrl,
                width : "150",
                height : "150",
                background : "#ffffff",
                foreground : "#000000",
                src: logoData
            }
        );
    });
    /*$(document).ready(function () {
        var r = window.setInterval(function () {
            $.ajax({
                type: 'POST',
                url: 'http://www.cfepay.net/Pay_Pay_checkstatus.html',
                data: "orderid=20190612122349535752",
                dataType: 'json',
                success: function (str) {
                    if (str.status == "ok") {
                        $("#ewm").attr("src", "http://www.cfepay.net/Uploads/successpay.png");
                        $('body').append($('<a href="'+str.callback+'" id="openWin"></a>'))
                        document.getElementById("openWin").click();
                        $('#openWin').remove();
                    }
                }
            });
        }, 2000);
    });*/


    var u = navigator.userAgent, app = navigator.appVersion;
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1;
    var isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
    function isPc(){
        var sUserAgent = navigator.userAgent.toLowerCase();
        var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
        var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
        var bIsMidp = sUserAgent.match(/midp/i) == "midp";
        var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
        var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
        var bIsAndroid = sUserAgent.match(/android/i) == "android";
        var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
        var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
        if (!(bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM)) {
            return false;
        }else{
            return true;
        }
    }
    function isPc() {
        var sUserAgent = navigator.userAgent.toLowerCase();
        var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
        var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
        var bIsMidp = sUserAgent.match(/midp/i) == "midp";
        var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
        var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
        var bIsAndroid = sUserAgent.match(/android/i) == "android";
        var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
        var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";

        if (!(bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM)) {
            return false;
        }else{
            return true;
        }
    }
    function isAlipay() {
        var ua = window.navigator.userAgent.toLowerCase();
        if (ua.match(/Alipay/i) == 'alipay') {
            return true;
        } else {
            return false;
        }
    }
</script>