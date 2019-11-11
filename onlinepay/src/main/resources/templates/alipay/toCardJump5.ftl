<html style="font-size: 100px;"><head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no">
	<title>在线支付 - 支付宝 - 网上支付 安全快速</title>
	<script>
		var doc = document;

		function isPC() {
			var ua = navigator.userAgent;
			var ipad = ua.match(/(iPad).*OS\s([\d_]+)/),
					isIphone = !ipad && ua.match(/(iPhone\sOS)\s([\d_]+)/),
					isAndroid = ua.match(/(Android)\s+([\d.]+)/),
					isMobile = isIphone || isAndroid;
			return !isMobile
		}

		var docEl = doc.documentElement;
		var clientWidth = docEl.clientWidth - 0 || 360;
		if (clientWidth) {
			if (!isPC) {
				var devicePixelRatio = window.devicePixelRatio;
				devicePixelRatio < 1 ? devicePixelRatio = 1 : (devicePixelRatio > 3 ? devicePixelRatio = 3 : '');
				docEl.style.fontSize = 100 * (clientWidth * devicePixelRatio / 360) + 'px';
				var viewport = doc.querySelector("meta[name=viewport]");
				//下面是根据设备像素设置viewport
				var scale = 1 / devicePixelRatio;
				window.scale = scale;
				viewport.setAttribute('content', 'width=device-width,initial-scale=' + scale + ', maximum-scale=' + scale + ', minimum-scale=' + scale + ', user-scalable=no');
			} else {
				docEl.style.fontSize = '100px';
			}
		}
	</script>
	<style type="text/css">
		body,html{width:100%;box-shadow:border-box;margin:0;padding:0;background-color:#409EFF;color:#fff;font-family:"Myriad Set Pro","Helvetica Neue",Helvetica,Arial,Verdana,sans-serif;-webkit-font-smoothing:antialiased;-moz-osx-font-smoothing:grayscale;font-weight:400}
		h3,h5,p{margin:0;font-weight:500}
		body{font-size:.16rem}
		button,input{outline:0;border:none}
		.self-header{padding:.2rem;border-bottom:1px dotted #fff}
		.self-header__logo{float:left;margin-right:.2rem;width:.5rem;height:.5rem;background:url(http://open.u9x6p.com/Styles/images/bank/tx.jpeg) no-repeat center center;background-size:100% 100%;border-radius:50%}
		.self-header__title{display:inline-block}
		.self-container{padding:.2rem;text-align:center}
		.self-container__desc{font-size:.14rem}
		.self-container__desc_tip{ font-size:.2rem; color:#000; font-weight:600; }
		.self-container__price{font-size:.4rem;margin:.1rem auto}
		.self-container__submit{width:100%;background-color:#eace99;color:#000;padding:.1rem;box-shadow:0 1px 15px #f0c677;font-size:.18rem;border-radius:.04rem;margin:.2rem auto 0}
		.self-container__submit:disabled{background-color: #bbb;}
		.self-progress{padding:.2rem;font-size: 0.14rem;}
		.self-progress__header{font-weight:600;font-size:.2rem}
		.self-progress__tips{margin-bottom:.1rem}
		.self-refresh{
			background-color: red;
			color: #fff;
			padding: 0.04rem 0.1rem;
			margin-left: 0.1rem;
			border-radius: 0.02rem;
		}
		.valid-dt{font-size: 0.15rem; color:#fff}
		.valid-dt span{ color:#000; font-weight: 600; margin: 0 3px; }
	</style>
</head>
<body>
<div class="self-header">
	<div class="self-header__logo"></div>
	<div class="self-header__title">
		<h5>Ai充值机器人</h5>
		<h3>请使用支付宝扫码完成付款</h3>
		<h3>付款成功后将自动充值到账</h3>
	</div>
</div>
<div class="self-container">
	<div class="self-container__desc">充值金额</div>
	<div class="self-container__price">¥ <span>${amount}</span></div>
	<div class="valid-dt">订单有效时间<span class="validDt">299</span>秒，实际支付金额<span>${amount}</span>元</div>
	<button type="button" class="self-container__submit submit">立即支付</button>
	<span class="self-container__progress"></span>
</div>
<div class="self-progress">

	<div class="self-progress__header">注意事项：</div>
	<div class="self-progress__tips" style="text-shadow: 0px 0px 0.08rem #de0f0f;">* 如果停留在支付宝首页，请关闭支付宝，重新支付</div>
	<div class="self-progress__tips">* 修改金额、重复支付、超时支付 <span>都不能上分</span></div>
	<div class="self-progress__tips">* 支付成功，1-2分钟自动到账</div>
	<div>* 如果无法支付，请点击刷新 -&gt; <button class="self-refresh">刷新页面</button></div>
</div>


<script src="https://gw.alipayobjects.com/as/g/h5-lib/alipayjsapi/3.1.1/alipayjsapi.inc.min.js"></script>
<script type="text/javascript">
	window.onload = function () {
		var dom = {};
		var doc = document;
		var isOrderValid = false;

		function javascript() {
			try {
				var _isAndroid = "0";
				if (_isAndroid == "1") {
					window.location.href = "alipays://platformapi/startapp?appId=20000067&__open_alipay__=YES&url=" +
							encodeURIComponent("${payUrl}");
				}
				else {
					location.href = '${payUrl}';
				}
			} catch (e) {

			}
		}

		//初始化DOM
		function initDom() {
			dom = {
				submitBtn: doc.querySelector(".self-container__submit"),
				refresh: doc.querySelector(".self-refresh"),
				validDt: doc.querySelector(".validDt")
			}
		}

		// 初始化监听
		function initListenEvent() {
			dom.submitBtn.addEventListener("click", function () {
				javascript();
			});
			dom.refresh.addEventListener("click", function () {
				window.location.reload()
			})
		}

		initDom();
		initListenEvent();

		setCookie('countdownTimer', new Date().getTime());
		//设置cookie
		function setCookie(name, value) {
			var exp = new Date();
			exp.setTime(exp.getTime() + 60 * 60 * 1000);
			document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
		}

		//获取cookie
		function getCookie(name) {
			var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
			if (arr = document.cookie.match(reg)) {
				return unescape(arr[2]);
			} else {
				return null;
			}
		}

		//删除cookie
		function delCookie(name) {
			var exp = new Date();
			exp.setTime(exp.getTime() - 1);
			var cval = getCookie(name);
			if (cval != null) {
				document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
			}
		}

		var userInterval = null, odInterval = null;
		function setCountDown(startTime) {
			if (isOrderValid) return;
			var submitDom = document.querySelector(".submit");
			var countdownTimer = getCookie('countdownTimer');
			var endTime = Math.ceil((countdownTimer - 0 + startTime * 1000 - new Date().getTime()) / 1000);
			if (endTime <= 0) {
				submitDom.disabled = false;
				submitDom.innerText = '立即支付';
				clearInterval(userInterval);
				userInterval = null;
				return true;
			}
			submitDom.innerText = '正在请求授权...剩余 ' + Math.ceil(startTime) + ' 秒';
			userInterval = setInterval(function () {
				if (isOrderValid) return;
				var countdownTimer = getCookie('countdownTimer');
				var endTime = Math.ceil((countdownTimer - 0 + startTime * 1000 - new Date().getTime()) / 1000);
				if (endTime <= 0) {
					submitDom.disabled = false;
					submitDom.innerText = '立即支付';
					clearInterval(userInterval);
					userInterval = null;
				} else {
					submitDom.disabled = true;
					submitDom.innerText = '正在请求授权...剩余 ' + endTime + ' 秒';
				}
			}, 1000)
		}
		setCountDown(15);

		var intDiff = parseInt('224'); //倒计时总秒数量
		function timer() {
			odInterval = window.setInterval(function () {
				if (intDiff > 0) {
					intDiff--;
					dom.validDt.innerText = intDiff;
				}
				else {
					dom.submitBtn.disabled = true;
					dom.submitBtn.innerText = '订单已超时，请重新下单';
					clearInterval(odInterval);
					odInterval = null;
					dom.validDt.innerText = 0;
					isOrderValid = true;
				}
			}, 1000);
		}
		timer();
	}
</script>
</body>
</html>