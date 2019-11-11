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
	.h5, h5 {
			font-size: 15px;
			font-family: "微软雅黑";
		}
        .text-error {
            color: #b94a48;
        }
        .muted {
            color: #999999;
        }
        .perror {
            color: #ed5565;
			font-family: "微软雅黑";
        }
		.btn-info {
			color: #fff;
			background-color: #BEBFC3;
			border-color: #BEBFC3;
        }
		.mod-title {
			height: 60px;
			line-height: 60px;
			text-align: center;
			border-bottom: 1px solid #ddd;
			background: #fff;
        }
		.ico_log {
			display: inline-block;
			width: 130px;
			height: 45px;
			vertical-align: middle;
			margin-right: 7px;
        }

		.ico-1 {
			background: url(https://t.alipayobjects.com/images/T1HHFgXXVeXXXXXXXX.png) no-repeat;
			background-size: cover;
		}
		
		.h3, h3 {
			font-size: 30px;
		}
    </style>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12" >
            <h1 class="mod-title">
				<span class="ico_log ico-1"></span>
			</h1>
			
            <div class="row" style="text-align: center;">
                <div class="col-sm-5" style="width: 41.786667%;">
                </div>
                <div class="col-sm-2">
                    <h3>￥${a}</h3>
                    <p class="perror">
                    <h5 class="perror">提醒：有效期为5分钟，请勿重复支付</h5>
					<br>
                    </p>
                </div>
            </div>

            <div id="code" style="text-align: center;">
			<div id="code2" style="display:none"></div>
			<img id='imgOne' style=''/>
			</div>    
			<br>
            <div style="text-align: center;">
                <a href="${l}" class="btn btn-info btn-lg"  id="alink">
                    点击启动支付宝支付
                </a>
            </div>
            <div class="row" style="text-align: center;">
                <div class="col-sm-5" style="width: 41.786667%;">
                </div>
                <div class="col-sm-2" style="border-top: 1px solid #ddd; margin-top:20px; padding-top:10px; width:330px; margin-left:auto; margin-right:auto">
                    <p class="perror">
                    <h4 class="perror" style="color:black;font-weight: bold; font-family: "微软雅黑"; margin-block-start: 0.67em;">请按如下步骤操作</h4>
                    </p>
                    <p class="perror">
                    <h5 class="perror" style="font-weight: bold; font-family: "微软雅黑";">截屏或长按 保存二维码</h5>
                    </p>
                    <p class="perror">
                    <h5 class="perror" style="font-weight: bold; font-family: "微软雅黑";">支付宝→扫一扫→相册中选择</h5>
                    </p>
                </div>
            </div>
            <br />
        </div>
    </div>
</div>

<script type="text/javascript">
var href2 = $("#alink").attr("href");
$("#alink").removeAttr("href");
$("#alink").text("Home键切换桌面后重新点击支付");
    var payUrl = "${l}";
    var logoData ="http://qrcode.linwx420.com:8088/static/img/noway/alipay36.png";
    $(function() {// 初始化内容
        var qrcode = $("#code2").qrcode({
                render : "canvas",    //设置渲染方式，有table和canvas，使用canvas方式渲染性能相对来说比较好
                text : payUrl,    //扫描二维码后显示的内容,可以直接填一个网址，扫描二维码后自动跳向该链接
                width : "200",            // //二维码的宽度
                height : "200",              //二维码的高度
                background : "#ffffff",       //二维码的后景色
                foreground : "#000000",        //二维码的前景色
                src: logoData   //二维码中间的图片
            }
        );
		var canvas=qrcode.find('canvas').get(0); 
		$('#imgOne').attr('src',canvas.toDataURL('image/jpg'))
    });
	
	document.addEventListener("visibilitychange", function(){
	   $("#alink").attr("href",href2);
	   $(".btn-info").css("background-color","#5bc0de");
	   $("#alink").text("　　　点击启动支付　　　");
}, false);
</script>
</body>
</html>