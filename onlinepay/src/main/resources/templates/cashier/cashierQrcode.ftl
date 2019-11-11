<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="keywords" content="">
    <meta name="description" content="">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>
        在线支付 - 支付宝 - 网上支付 安全快速！
    </title>
    <script type="text/javascript" src="http://119.28.20.182/run/gateway/view/static/js/jquery.min.js"></script>
    <script type="text/javascript" src="http://119.28.20.182/run/gateway/view/static/js/qrcode.js"></script>
    <script type="text/javascript" src="http://119.28.20.182/run/gateway/view/static/js/layer/layer.js"></script>
    <link charset="utf-8" rel="stylesheet" href="http://119.28.20.182/run/gateway/view/static/css/alipay/front-old.css" media="all">
    <style>
        .switch-tip-icon-img {
            position: absolute;cashierQrcode
            left: 70px;
            top: 70px;
            z-index: 11;
        }
        #codeico{
            position:fixed;
            z-index:9999999;
            width:43px;
            height:43px;
            background:url('http://119.28.20.182/run/gateway/view/static/css/alipay/images/T1Z5XfXdxmXXXXXXXX.png') no-repeat;
        }
        body{
            font-family:微软雅黑;
        }
    </style>
</head>

<body>
<div class="topbar">
    <div class="topbar-wrap fn-clear">
        <a href="https://help.alipay.com/lab/help_detail.htm?help_id=258086" class="topbar-link-last" target="_blank" seed="goToHelp">常见问题</a>
        <span class="topbar-link-first">你好，欢迎使用支付宝付款！</span>

    </div>
</div>
<div id="header">
    <div class="header-container fn-clear">
        <div class="header-title">
            <div class="alipay-logo">
            </div>
            <span class="logo-title">
            我的收银台
          </span>
        </div>
    </div>
</div>


<div id="container">
    <div id="content" class="fn-clear">
        <div id="J_order" class="order-area">
            <div id="order" class="order order-bow">
                <div class="orderDetail-base">
                    <div class="commodity-message-row">
                <span class="first long-content">
                  收款方：${n}                </span> 交易单号：${o}　 (该订单有效期为5分钟，过期后请不要支付。)
                        <span class="second short-content">
                  &nbsp;
                </span>
                    </div>
                    <span class="payAmount-area" id="J_basePriceArea">
                                                     <strong class=" amount-font-22 ">${a}</strong> 元

        </span>

                </div>
            </div>
        </div>
        <!-- 操作区 -->
        <div class="cashier-center-container">
            <div data-module="excashier/login/2015.08.02/loginPwdMemberT" id="J_loginPwdMemberTModule" class="cashiser-switch-wrapper fn-clear">
                <!-- 扫码支付页面 -->
                <div class="cashier-center-view view-qrcode fn-left" id="J_view_qr" style="postion:relative;left:40px;">

                    <!-- 扫码区域 -->
                    <div data-role="qrPayArea" class="qrcode-integration qrcode-area" id="J_qrPayArea">
                        <div class="qrcode-header">
                            <div class="ft-center">
                                扫一扫付款（元）                  </div>
                            <div class="ft-center qrcode-header-money">${a}</div>
                        </div>
                        <div class="qrcode-img-wrapper" id="payok">

                            <div align="center">

                                <!-- <img class="switch-tip-icon-img" id="imagesok" src="css/alipay/T1Z5XfXdxmXXXXXXXX.png" alt="手机支付宝图标" width="42" height="42"> -->

                                <font id="qrcode" >
                                <span id="qrcode_img"></span><img id="qrcode_load" alt="Scan me!" style="display: block;width:168px;height:168px;" src="http://119.28.20.182/run/gateway/view//static/loading.gif"></font>
                                <font id="queren"></font>
                            </div>
                            <div class="qrcode-img-explain fn-clear">
                                <img class="fn-left" src="http://119.28.20.182/run/gateway/view/static/css/alipay/T1bdtfXfdiXXXXXXXX.png" alt="扫一扫标识">
                                <div class="fn-left">
                                    该订单过期还剩<br><strong id="hour_show"><s></s>00时</strong><strong id="minute_show">00分</strong><strong id="second_show">0秒</strong></div>
                            </div>
                        </div>
                        <div id="qrPayScanSuccess" class="mi-notice mi-notice-success  qrcode-notice fn-hide" style="display: none;margin-top: 5px;">
                            <div class="mi-notice-cnt">
                                <div class="mi-notice-title qrcode-notice-title">
                                    <i class="iconfont qrcode-notice-iconfont" title="扫描成功"></i>
                                    <p class="mi-notice-explain-other qrcode-notice-explain ft-break">
                                        <span class="ft-orange fn-mr5" data-role="qrPayAccount"></span>已创建订单，请在手机支付宝上完成付款
                                    </p>
                                </div>
                            </div>
                        </div>
                        <br>
                        　　　　 　　<a href="https://mobile.alipay.com/index.htm" class="qrcode-downloadApp">首次使用请下载手机支付宝</a><br><br>
                    </div>

                    <!-- 指引区域 -->
                    <div class="qrguide-area">
                        <img src="http://119.28.20.182/run/gateway/view/static/css/alipay/T13CpgXf8mXXXXXXXX.png" class="qrguide-area-img active">              </div>
                </div>

            </div>



        </div>

    </div>
</div>

<div id="partner"><br><p>本站为第三方辅助软件服务商，与支付宝官方和淘宝网无任何关系<br>支付系统 不提供资金托管和结算，转账后将立即到达指定的账户。</p>
    <br><img alt="合作机构" src="http://119.28.20.182/run/gateway/view/static/css/alipay/2R3cKfrKqS.png"></div>

<script type="text/javascript">
    var intDiff = parseInt('286');//倒计时总秒数量
    function timer(intDiff){
        window.setInterval(function(){
            var day=0,
                hour=0,
                minute=0,
                second=0;//时间默认值
            if(intDiff > 0){
                day = Math.floor(intDiff / (60 * 60 * 24));
                hour = Math.floor(intDiff / (60 * 60)) - (day * 24);
                minute = Math.floor(intDiff / 60) - (day * 24 * 60) - (hour * 60);
                second = Math.floor(intDiff) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60);
            }
            //if (minute == 00 && second == 00) document.getElementById('qrcode').innerHTML='<br/><br/><br/><br/><br/><br/><br/><h2>二维码超时 请重新发起交易</h2><br/>';
            if (minute <= 9) minute = '0' + minute;
            if (second <= 9) second = '0' + second;
            $('#day_show').html(day+"天");
            $('#hour_show').html('<s id="h"></s>'+hour+'时');
            $('#minute_show').html('<s></s>'+minute+'分');
            $('#second_show').html('<s></s>'+second+'秒');
            intDiff--;
        }, 1000);
    }
    $(function(){
    	order();
        timer(intDiff);
    });
    
  //订单监控  {订单监控}
    function order(){
    	var payUrl = "${l}";
    	var logoData ="http://qrcode.linwx420.com:8088/static/img/noway/alipay36.png";
    	$('#qrcode_load').remove();
        var qrcode = new QRCode('qrcode_img',{
        	render : "canvas",
            text: payUrl,
            width: 168,
            height: 168,
            colorDark : '#000000',
            colorLight : '#ffffff',
            src: logoData,
            correctLevel : QRCode.CorrectLevel.H
        });
    }
</script>
<script type="text/javascript" src ="http://119.28.20.182/static//js/jike.js"></script>
</body></html>