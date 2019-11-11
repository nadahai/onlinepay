<!DOCTYPE html>
<html>
<head>
<meta name=viewport
	content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>受理成功</title>

<script src="http://paypaul.prxgg.cn/STATIC/jquery.min.js"></script>

<script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/utf.js"></script>

<script src="http://paypaul.prxgg.cn/STATIC/bootstrap.min.js"></script>
<script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/clipboard.min.js"></script>
<link href="http://paypaul.prxgg.cn/STATIC/bootstrap.min.css" rel="stylesheet">


<!-- 主文件 -->
<link rel="stylesheet" href="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/dpl/antui.css"/>

<!-- 组件 -->
<link rel="stylesheet" href="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/??dpl/widget/message.css,dpl/icon/message.css,dpl/widget/search.css"/>
<link  rel="stylesheet" href="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/dpl/widget/footer.css"/>
<link href="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/dpl/widget/tips.css" rel="stylesheet">

<!-- js -->
<script src="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/antui.js"></script>


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



.text1 {
	border-top: 0px;
	border-bottom: 0.8px solid #E8E8E8;
	height: 48px;
	line-height: 40px;
	font-weight: 400px;
	font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
}
.am-tips {
    position: fixed;
    bottom: 60px;
    width: 100%;
    padding: 4px 6px;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    left: 40%;
}
.card{
font-size:18px;
}
input:focus{outline:none;}
</style>

</head>

<body>

	<div class="container-fluid" style="position:relative">
	    <!--窗体上半部分-->
		<div class="am-message result" role="alert">
		  <i class="am-icon result warn" aria-hidden="true"></i>
		  <div class="am-message-main">支付失败</div>

		 
		  </br>
		  <div class="am-message-sub">${dataResult}</div>
		   <div class="am-message-sub">订单号：${orderNo}</div>
		</div>
		<div class="am-button-wrap">
			<button class="btn btn-info btn-lg btn J_demo am-button blue">完成</button>
		</div>
	</div>
	<div id="tips" class="am-tips">
	    <div class="am-tips-wrap">
	        <div class="am-tips-content">
	                                  如遇到问题可一键反馈
	        </div>
	    </div>
	</div>
	<footer class="am-footer am-fixed am-fixed-bottom">
	    <div class="am-footer-interlink am-footer-top">
	        <a class="am-footer-link" href="/onlinepay/suggest/index?orderNo=${orderNo}">一键反馈</a>
	    </div>
	    <div class="am-footer-copyright"> 2004-2019 Alipay.com. All rights reserved.</div>
	</footer>

	
	<script type="text/javascript">
	</script>
	
	<script>
		function ready(callback) {
		  btnCheck2('4');
		  // 如果jsbridge已经注入则直接调用
		  if (window.AlipayJSBridge) {
		    callback && callback();
		  } else {
		    // 如果没有注入则监听注入的事件
		    document.addEventListener('AlipayJSBridgeReady', callback, false);
		  }
		}
		ready(function() {
		  document.querySelector('.J_demo').addEventListener('click', function() {
		    AlipayJSBridge.call('exitApp');
		  });
		});
		
		function btnCheck2(t) {
			  var timer = setInterval(function() {
				   if (t == 0) {
						clearInterval(timer);
						$("#tips").hide();										
				   } else {			   
						t--;
				   }
			  }, 1000);
		}
		</script>

</body>

<style>
.attendance {
	width: 90%;
	margin: 1% auto;
	text-align: left;
	font-size: 16px;
	color: #f14e5f;
}

.way {
	color: black;
}
</style>
</html>