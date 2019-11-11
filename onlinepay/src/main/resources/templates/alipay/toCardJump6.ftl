<html style="font-size: 100px;">
<head>
	<meta charset="UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
	<title>在线支付 - 支付宝 - 网上支付 安全快速</title>
	<link href="http://open.u9x6p.com/Styles/css/swiper.min.css" rel="stylesheet" />
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
	<style>
		body,
		html {
			width: 100%;
			box-shadow: border-box;
			margin: 0;
			padding: 0
		}

		body {
			font-size: .16rem;
			padding-bottom: 0.2rem;
		}

		button,
		input {
			outline: 0;
			border: none
		}

		.self-container {
			background-color: #fff
		}

		.self-container .self-header {
			border-bottom: 1px dotted #aaa
		}

		.self-header__logo {
			width: 1rem;
			height: .34rem;
			background: url(http://open.u9x6p.com/Styles/images/bank/zfb_T1HHFG.png) no-repeat center center;
			background-size: 100% 100%;
			margin: .1rem auto
		}

		.self-info {
			height: .9rem;
			margin: .1rem .1rem 0;
		}

		.self-info__txt {
			text-align: center;
			font-size: .12rem;
			color: #fff;
			background-color: #bd0b0b;
			padding: 0.04rem;
			line-height: .2rem
		}

		.self-info__subTxt {
			font-size: 0.14rem;
			padding: 0.04rem 0.06rem;
			border: 0.01rem dotted #666;
			margin-top: 0.08rem;
			border-radius: 0.04rem;
			overflow: hidden;
		}

		.self-info__price {
			text-align: center
		}

		.self-info__price span {
			color: #f50;
			font-size: .26rem;
			margin-right: .06rem
		}

		.self-submit {
			padding: .1rem
		}

		.self-submit__btn {
			display: block;
			width: 100%;
			background-color: #39c;
			color: #fff;
			font-size: .18rem;
			line-height: .4rem;
			border-radius: .034rem
		}

		.self-tips {
			color: #FF4500;
			background-color: #f5f5f5;
			padding: .15rem
		}

		.self-tips b {
			display: block;
			width: 100%;
			text-align: center;
			line-height: .24rem
		}

		.self-qrcode .loading {
			box-sizing: border-box;
			width: 100%;
			height: 100%;
			vertical-align: top;
			line-height: .28rem;
			overflow: hidden;
			text-align: center;
			padding: .2rem
		}

		.loading .rotate {
			-webkit-animation: mescrollRotate .6s linear infinite;
			animation: mescrollRotate .6s linear infinite
		}

		.loading .progressed {
			display: inline-block;
			width: .16rem;
			height: .16rem;
			border-radius: 50%;
			border: .01rem solid #777;
			border-bottom-color: transparent;
			vertical-align: top;
			margin: .04rem .06rem 0 0
		}

		100% {
			-webkit-transform: rotate(360deg);
			transform: rotate(360deg)
		}

		.self-qrcode {
			padding: .2rem
		}

		.self-qrcode img {
			display: none;
			width: 1.6rem;
			height: 1.6rem;
			margin: 0 auto
		}

		.self-footer {
			color: red;
			text-align: center;
			font-weight: 600;
			padding: .05rem
		}

		.self-layer {
			display: none;
			z-index: 111;
			background-color: rgba(0, 0, 0, .3);
			top: 0;
			left: 0;
			width: 100%;
			height: 100%;
			position: fixed;
			pointer-events: auto
		}

		.self-layer.active {
			display: block
		}

		.self-layer__dialog {
			position: absolute;
			left: .5rem;
			top: 2rem;
			top: calc((100% - 2rem)/2);
			width: 2.6rem;
			background-color: #fff;
			-webkit-background-clip: content;
			border-radius: 2px;
			box-shadow: 1px 1px 50px rgba(0, 0, 0, .3);
			margin: 0;
			padding: 0
		}

		.self-layer__title {
			background-color: #f8f8f8;
			border-bottom: 1px solid #eee;
			height: .4rem;
			line-height: .4rem;
			color: #333;
			font-size: .14rem;
			padding: 0 .2rem
		}

		.self-layer__title .close {
			float: right;
			font-size: .18rem;
			color: #888
		}

		.self-layer__content {
			font-size: .24rem;
			font-weight: 500;
			padding: .2rem;
			color: red;
			text-align: center;
		}

		.self-layer__action {
			padding: 0 .2rem .1rem
		}

		.self-layer__sure {
			font-size: .14rem;
			background-color: #1E9FFF;
			color: #fff;
			padding: .1rem .1rem;
			display: block;
			width: 100%;
		}

		.clearfix {
			overflow: auto;
			zoom: 1
		}

		.self-toast {
			position: fixed;
			top: .5rem;
			background: rgba(0, 0, 0, .8);
			color: #fff;
			width: 1rem;
			height: .3rem;
			line-height: .3rem;
			text-align: center;
			border-radius: .04rem;
			font-size: .14rem;
			left: 1.3rem
		}

		.swiper-tips {
			font-size: 0.14rem;
			text-align: center;
			color: red;
			margin-bottom: 0.04rem;
		}

		.swiper-container img {
			display: block;
			width: 80%;
			margin: 0 auto;
		}
		/* return top */
		#btnTop {
			display: none;
			position: fixed;
			bottom: 20px;
			right: 10px;
			z-index: 99;
			border: none;
			outline: none;
			background-color: #89cff0;
			color: white;
			cursor: pointer;
			padding: 15px;
			border-radius: 10px;
		}
		.swiper-container2 img {
			display: block;
			width: 80%;
			margin: 0 auto;
		}
		.btnTop:hover {
			background-color: #1E90FF;
		}
	</style>
</head>

<body>
<div class="self-container">
	<div class="self-header">
		<div class="self-header__logo"></div>
	</div>
	<div class="self-info">
		<div class="self-info__txt">点击复制按钮，把复制内容发给任意支付宝好友，在聊天框里打开链接支付</div>
		<div class="self-info__subTxt">${copyUrl}</div>
	</div>
	<div class="self-submit">
		<button class="self-submit__btn" data-clipboard-text="点击 ${copyUrl} 付款，如遇提示已停止该网页访问，请返回多点击几次链接即可付款">复制付款链接</button>
	</div>
</div>
<div class="swiper-tips">左右或上下滑动查看付款流程</div>
<div style="position: relative;">
	<!-- Swiper -->
	<div class="swiper-container swiper-container-initialized swiper-container-horizontal swiper-container-android">
		<div class="swiper-wrapper" style="transform: translate3d(-2160px, 0px, 0px); transition-duration: 0ms;">
			<div class="swiper-slide" style="width: 360px;">
				<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_1.png">
			</div>
			<div class="swiper-slide" style="width: 360px;">
				<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_2.png">
			</div>
			<div class="swiper-slide" style="width: 360px;">
				<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_3.png">
			</div>
			<div class="swiper-slide" style="width: 360px;">
				<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_4.png">
			</div>
			<div class="swiper-slide" style="width: 360px;">
				<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_5.png">
			</div>
			<div class="swiper-slide swiper-slide-prev" style="width: 360px;">
				<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_6.jpg">
			</div>
			<div class="swiper-slide swiper-slide-active" style="width: 360px;">
				<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_7.jpg">
			</div>
		</div>
		<!-- Add Arrows -->
		<div class="swiper-button-next swiper-button-disabled" tabindex="0" role="button" aria-label="Next slide" aria-disabled="true"></div>
		<div class="swiper-button-prev" tabindex="0" role="button" aria-label="Previous slide" aria-disabled="false"></div>
		<span class="swiper-notification" aria-live="assertive" aria-atomic="true"></span></div>
</div>
<div class="swiper-container2">
	<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_2.png">
	<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_3.png">
	<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_4.png">
	<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_5.png">
	<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_6.jpg">
	<img src="http://open.u9x6p.com/Styles/images/bank/b_tip_7.jpg">
</div>
<button id="btnTop" class="btnTop" title="返回顶部" style="display: none;">返回顶部</button>
<div class="self-layer">
	<div class="self-layer__dialog">
		<div class="self-layer__title">温馨提示<span class="close">X</span></div>
		<div class="self-layer__content">复制成功</div>
		<div class="self-layer__action clearfix">
			<button class="self-layer__sure">我知道了</button>
		</div>
	</div>
</div>

<script type="text/javascript" src="http://open.u9x6p.com/Scripts/clipboard.min.js"></script>
<script type="text/javascript" src="http://open.u9x6p.com/Scripts/swiper.min.js"></script>
<script type="text/javascript">
	window.onload = function() {
		var dom = {};
		var doc = document;
		var swiper = new Swiper('.swiper-container', {
			navigation: {
				nextEl: '.swiper-button-next',
				prevEl: '.swiper-button-prev',
			},
		});
		//初始化DOM
		function initDom() {
			dom = {
				layer: doc.querySelector(".self-layer"),
				layerCloseBtn: doc.querySelector(".self-layer .close"),
				sureBtn: doc.querySelector(".self-layer .self-layer__sure"),
				submitBtn: doc.querySelector(".self-submit__btn"),
				btnTop: doc.querySelector(".btnTop"),
			}
		}
		var clipboard = new ClipboardJS('.self-submit__btn');
		clipboard.on('success', function(e) {
			e.clearSelection();
		});
		clipboard.on('error', function(e) {
			console.error('Action:', e.action);
			console.error('Trigger:', e.trigger);
		});
		// 初始化监听
		function initListenEvent() {
			dom.submitBtn.addEventListener("click", function() {
				triggleLayer()
			});
			dom.layerCloseBtn.addEventListener("click", function() {
				triggleLayer(true)
			});
			dom.sureBtn.addEventListener("click", function() {
				console.log('sureBtn', true);
				triggleLayer(true)

			});
			dom.btnTop.addEventListener("click", function() {
				document.body.scrollTop = 0;
				document.documentElement.scrollTop = 0;
			})
		}

		// 打开／关闭 弹窗
		function triggleLayer(close) {
			if(close) {
				dom.layer.classList.remove('active')
			} else {
				dom.layer.classList.add('active')
			}
		}
		initDom();
		initListenEvent();

		// 当网页向下滑动 20px 出现"返回顶部" 按钮
		window.onscroll = function() {
			scrollFunction()
		};

		function scrollFunction() {
			if (document.body.scrollTop > 90 || document.documentElement.scrollTop > 90) {
				document.getElementById("btnTop").style.display = "block";
			} else {
				document.getElementById("btnTop").style.display = "none";
			}
		}
	}
</script>
</body>
</html>