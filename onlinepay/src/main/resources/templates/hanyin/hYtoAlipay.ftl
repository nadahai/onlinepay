<!DOCTYPE html>
<html>

<head>
	<meta name=viewport content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>收款</title>
	<script src="${request.contextPath}/static/js/jquery.min.js"></script>
	<script src="${request.contextPath}/static/js/common/online.nada.util.common.js"></script>
	<script src="${request.contextPath}/static/js/layer.js"></script>
	<link href="${request.contextPath}/static/css/layer.css" rel="stylesheet">
	<script src="http://test.mall51.top:4000/STATIC/bootstrap.min.js"></script>
	<link href="http://test.mall51.top:4000/STATIC/bootstrap.min.css" rel="stylesheet">
	<script src="${request.contextPath}/static/js/nadacall.js"></script>

	<!-- <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet"> -->
	<link href="https://cdn.bootcss.com/animate.css/3.5.2/animate.css" rel="stylesheet">
	<link href="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/dpl/widget/dialog.css" rel="stylesheet">
	
	<link rel="stylesheet" href="${request.contextPath}/static/css/layui.css">

	<link rel="stylesheet" type="text/css" href="${request.contextPath}/static/css/keyboard.css">
	<script type="text/javascript" src="${request.contextPath}/static/js/keyboard.js"></script>
	<script type="text/javascript" charset="utf-8" src="${request.contextPath}/static/js/phone.encryptpd.js"></script>
	
	<script src="https://gw.alipayobjects.com/as/g/h5-lib/alipayjsapi/3.1.1/alipayjsapi.inc.min.js"></script>
	

	<style type="text/css">
		.text1 {
			border-top: 0.8px solid #F0F0F0;
			border-bottom: 0.8px solid #F0F0F0;
			font-size: 15px;
			height: 40px;
			line-height: 36px;
			font-weight: 400px;
			font-family: "Microsoft YaHei", tahoma, arial, Hiragino Sans GB;
		}

		#newPay {
			height: 470px;
			width: 100%;
			background-color: #FFFFFF;
			position: fixed;
			bottom: 0px;
			z-index: 1000;
			display: none;
			box-shadow: 0 0 10px rgba(0, 0, 0, 0.55);
		}

		#cardPassWord {
			height: 460px;
			width: 100%;
			background-color: #FFFFFF;
			position: fixed;
			bottom: 0px;
			z-index: 1000;
			display: none;
		}

		#cardType {
			height: 470px;
			width: 100%;
			background-color: #FFFFFF;
			position: fixed;
			bottom: 0px;
			z-index: 1000;
			display: none;
			box-shadow: 0 0 10px rgba(0, 0, 0, 0.55);
		}

		#takePassWord {
			height: 470px;
			width: 100%;
			background-color: #FFFFFF;
			position: fixed;
			bottom: 0px;
			z-index: 1000;
			display: none;
		}

		.newPayShow {
			animation: show 200ms 1;
		}

		.newPayHide {
			animation: hide 200ms 1;
			bottom: -470px !important;
		}

		@keyframes show {
			from {
				bottom: -470px;
			}

			to {
				bottom: 0px;
			}
		}

		@keyframes hide {
			from {
				bottom: 0px;
			}

			to {
				bottom: -470px;
			}
		}

		.mask {
			position: fixed;
			top: 0;
			bottom: 0;
			left: 0;
			right: 0;
			background: black;
			z-index: 100;
			opacity: 0.4;
		}

		/* <!--按钮颜色--> */
		.btn-info {
			color: #fff;
			background-color: #3890ea;
		}

		.layui-m-layercont {
			padding: 30px 30px;
			line-height: 10px;
			text-align: center;
		}

		/* <!--密码键盘--> */
		.flexable {
			display: -webkit-box;
			height: 68px;
		}

		.flexable>div {
			-webkit-box-flex: 1;
		}

		.flexable.password>div {
			opacity: 0
		}

		;

		.flexable.password>div.active {
			opacity: 1 !important;
		}

		.password {
			margin: 20px 25px !important;
		}

		.password>div {
			height: 46px;
			line-height: 46px;
			text-align: center
		}

		.password>div:first-child {
			border-top-left-radius: 5px;
			border-bottom-left-radius: 5px
		}

		.password>div:last-child {
			border-top-right-radius: 5px;
			border-bottom-right-radius: 5px
		}

		.input-box .flexable>div {
			height: 53px
		}

		.input-box .flexable>div:active {
			background: rgba(0, 200, 200, .5)
		}

		.list-block .item-title {
			font-weight: normal !important;
			font-size: 14px
		}

		ul li {
			position: relative
		}

		.close {
			position: absolute;
			top: 4px;
			left: 4px;
			font-size: 20px;
			width: 22px;
			text-align: center
		}

		h1.title {
			height: 50px;
			font-size: 18px;
			line-height: 50px;
			text-align: center;
			margin: 0;
		}

		.notice {
			height: 30px;
			line-height: 30px;
			font-size: 14px !important;
			text-align: center;
			margin-bottom: 15px;
			color: #00a9dd;
		}

		.keyboard-operation-submit {
			background: #3890ea;
		}
		
		.alertBox {
			background-color: #FFFFFF;
			height:130px;
			width:70%;
		    z-index:4000;
			position:fixed;
			bottom: 45%;
			left:15%;
			border-radius:1px 1px 1px 1px;
		    text-align:center;
		    display:none;
		}
		
	</style>

</head>

<body>
	<input type="hidden" id="staticContextPath" value="${request.contextPath}">
	<div class="container-fluid" style="position:relative">
		<!--窗体上半部分-->
		<div class="row-fluid">
			<div class="span12">
				<br />
				<span style="float:right;display:none;" class="suggest-item"><a style="color:red;"
						href="/onlinepay/suggest/index?orderNo=${orderNo}">一键反馈&nbsp;&nbsp;</a></span>
				<h3 style="text-align:center; font-size: 50px;margin-top:10%;">
					<strong><span style="font-size:30px;">￥</span>
						${amount}
					</strong>
				</h3>
				<span id="successMoney" style="display:none">
					${amount}
				</span>
				<h5 style="text-align: center;">
					${orderNo}
				</h5>

				<br /><br />

				<div class="text1" style="">
					<span style="float: left;letter-spacing:2px;">收款方</span><span
						style="float: right;letter-spacing:2px;">梦飞科技</span>
				</div>

				<br /><br /><br />

				<div style="text-align: center;" id="paying">
					<a href="javascript:" id="parOrder" class="btn btn-info btn-lg"
						style="background-color:#3890ea;border-radius: 3px;width: 100%;border: none;">付款</a>
				</div>
				
				<div class="row" style="text-align: center;">
					<div class="col-sm-5" style="width: 41.786667%;"></div>
				</div>

				<br />
			</div>
		</div>
	</div>

	<!-- 黑色阴影部分  -->
	<div class="mask"></div>

	<!-- 第一个向上弹框 -->
	<div id="newPay">
		<div style="text-align: center; font-size: 15px; border-bottom: 0.8px solid #F0F0F0;">
			<span class="iconfont icon-guanbi close closeAll"
				style="margin-top:5px;margin-left:6px;font-size:28px;font-weight:normal">×</span>
			<span style="line-height: 50px;letter-spacing:2.5px;font-size:16px;"><img
					src="http://qrcode.linwx420.com:8088/static/img/noway/alipay36.png" height="24px;" width="24px;"
					style="margin-right:8px;" /><strong>确认付款</strong><span>
		</div>
		<h4 style="text-align: center; font-size: 30px;">
			<strong style="line-height: 70px;"><span style="font-size:20px;">￥</span>${amount}</strong>
		</h4>
		<div
			style="height: 45px; border-bottom: 0.8px solid #F0F0F0; line-height: 45px; margin-left: 25px;font-size: 15px;">
			<span style="float: left;color:#909090;letter-spacing:1px;">订单信息</span>
			<span style="float: right; padding-right: 23px;letter-spacing:2px;"><strong>商品</strong></span>
		</div>
		<div id="cardTypeClink"
			style="height: 45px; border-bottom: 0.2px solid #F0F0F0; line-height: 45px; margin-left: 25px; display:block;font-size: 15px;">
			<span style="float: left;color:	#909090;letter-spacing:1px;">付款方式</span>
			<span style="float: right; padding-right: 20px;letter-spacing:2px;"><strong id="cardName"></strong>
				<lable id="cardId" style="display:none"></lable>
				<lable id="idNo" style="display:none"></lable>
				<!-- <i class="layui-icon layui-icon-face-smile" style="font-size: 30px; color: #1E9FFF;"></i>   -->
				<span class="layui-icon layui-icon-right"
					style="font-size:20px;font-weight:border;margin-left:10px;float:right;color:#888888;"></span>
		</div>
		<div id="cardTypeClink2"
			style="height: 45px; border-bottom: 0.8px solid #F0F0F0; line-height: 45px; margin-left: 25px; display:none;font-size: 15px;">
			<span style="float: left;color:	#909090;letter-spacing:1px;">付款方式</span>
			<span style="float: right; padding-right: 20px;letter-spacing:2px;"><strong id="cardName2"></strong>
				<lable id="cardId" style="display:none"></lable>
				<lable id="idNo" style="display:none"></lable>
				<span style="font-size:30px;color:#D0D0D0;font-weight:border;margin-left:30px;float:right">></span>
		</div>

		<br /><br /><br /><br /><br /><br /><br /><br />

		<div style="text-align: center;">
			<a href="javascript:" id="copy" class="btn btn-info btn-lg" data-clipboard-text=""
				style="background-color:#3890ea;letter-spacing:3px;margin-left: 25px;margin-right: 25px;display: block;border: none;" onclick="payPassWord()">　　　　　立即付款　　　　　</a>
		</div>
	</div>

	<!-- 添加银行卡向上弹窗 -->
	<div id="cardType">
		<div style="text-align: center; font-size: 15px; border-bottom: 0.8px solid #F0F0F0;height: 55px;line-height: 55px;">
			<i class="icon-guanbi close closeAll layui-icon layui-icon-left"
				style="font-size:27px;font-weight:normal;position: unset;float: left;height: 100%;line-height: 55px;margin-left:8px;">
			</i>
			<span style="letter-spacing:2.5px;font-size:16px;">
				<strong>选择付款方式</strong>
			</span>
			<a href='${request.contextPath}/hyh5api/jumpDel/${encryptNo}?userId=${userId}&payType=1'
				style="float:right;margin-right: 23px;">管理</a>
		</div>

		<div style="height: 50px; border-bottom: 0.8px solid #F0F0F0; line-height: 50px; margin-left: 25px;"
			onclick="window.location.href='${request.contextPath}/hyh5api/jumpAdd/${encryptNo}?merchNo=123456&payType=1&flag=1'">
			<span style="float: left;  letter-spacing:1px;">
				<!-- <img src="${request.contextPath}/static/images/payment_card_24px_505044_easyicon.net.png"/>　 -->
				添加银行卡</span>
			<i class="layui-icon layui-icon-right" style="float: right; padding-right: 23px;font-size: 20px !important;color:#D0D0D0;"></i>
		</div>
		<div id="cardTypes" style="overflow:auto;height:370px;">
		</div>
	</div>

	<!-- 输入卡密的键盘弹窗 -->
	<div id="cardPassWord" style="z-index:999">
		<div style="text-align: center; font-size: 15px; border-bottom: 0.8px solid #F0F0F0;">
			<span class="iconfont icon-guanbi close closeAll2"
				style="margin-top:5px;margin-left:6px;font-size:35px;font-weight:normal">×</span>
			<span style="line-height: 50px;letter-spacing:2.5px;font-size:16px;"><img
					src="http://qrcode.linwx420.com:8088/static/img/noway/alipay36.png" height="24px;" width="24px;"
					style="margin-right:8px;" /><strong style="font-size: 18px;">输入卡密</strong><span>
		</div>
		<div class="container-fluid">
			<div class="keyboard-show-text"></div>
			<div style="color:#00a9dd;text-align:center;height: 93px;line-height: 93px;">安全支付环境，请放心支付</div>
			<div class="keyboard-box"></div>
		</div>
	</div>
	
	<div class="alertBox">
	</br></br>
	   <span style="font-size:15px;">输入的卡信息不完整，请完善！</span>
	   </br></br><hr>
	   <div id="alertBoxOk" style="color:#3890ea;font-size:20px;"  onclick="">确定</div>
	</div>
	

	<!-- 输入支付密码向上弹窗 -->
	<div id="takePassWord"></div>

	<script type="text/javascript">
		window.localeStr = "zh_CN";
		window.publicKey = "";
		window.exponent = "";
		window.visiablepan = "";

		
		var allFlag = "";    //设置的全局变量状态
		var allPass = "";   //加密的卡密态


		$(document).ready(function () {
		
		$(".alertBox").hide();
		$("#alipayAlert1").hide();
			/*非扫码订单指定dom隐藏*/
			if ('${orderNo}'.startsWith("sc")) {
				document.querySelector(".suggest-item").style.display = "block";
			}

			$(".mask").hide();
			$("#newPay").hide();
			getCardInfos('1');

            $("#alertBoxOk").on('click', function () {
           		 $(".alertBox").hide();
           		 $(".mask").fadeOut(470);
            });
            
			//  <!-- 点击阴影部分关闭所有窗口 -->
			$(".mask").on('click', function () {
				$(".mask").fadeOut(470);
				$("#newPay").removeClass('newPayShow');
				$("#newPay").addClass('newPayHide');
				$("#cardType").removeClass('newPayShow');
				$("#cardType").addClass('newPayHide');
			});

			//  <!-- 点击叉关闭所有窗口 -->
			$(".closeAll").on('click', function () {
				$(".mask").fadeOut(470);
				$("#newPay").removeClass('newPayShow');
				$("#newPay").addClass('newPayHide');
				$("#cardType").removeClass('newPayShow');
				$("#cardType").addClass('newPayHide');
			});

			//  <!-- 点击叉关闭所有窗口 -->
			$(".closeAll2").on('click', function () {
				Global_show.find(".input_1").val("");
				Global_show.find(".input_2").val("");
				Global_show.find(".input_3").val("");
				Global_show.find(".input_4").val("");
				Global_show.find(".input_5").val("");
				Global_show.find(".input_6").val("");
				Global_count = 0;
				$(".mask").fadeOut(470);
				$("#cardPassWord").removeClass('newPayShow');
				$("#cardPassWord").addClass('newPayHide');
				$("#cardType").removeClass('newPayShow');
				$("#cardType").addClass('newPayHide');
				$('.keyboard-box').removeClass('animated fadeInUpBig');
				$('.keyboard-box').addClass('animated fadeOutDownBig');
			});

			//  <!-- 点击确认付款按钮  -->
			$("#paying").on('click', function () {
				$(".mask").fadeIn(470);
				$("#newPay").removeClass('newPayHide');
				$("#newPay").addClass('newPayShow');
				$("#newPay").show();
			});

			//  <!-- 点击获取银行卡方式 -->
			$("#cardTypeClink").on('click', function () {
				$(".mask").fadeIn(470);
				$("#newPay").fadeOut(300);
				$("#newPay").removeClass('newPayShow');
				$("#newPay").addClass('newPayHide');
				$("#cardType").fadeIn(300);
				$("#cardType").removeClass('newPayHide');
				$("#cardType").addClass('newPayShow');
				$("#cardType").show();

				getCardInfos('2');

				var id = $("#cardId").text();
				$(".yStyle").css("display", "none");
				$("#" + id).css("display", "block");
			});

			$("#cardTypeClink2").on('click', function () {
				layer.open({ content: "下单完成，请勿更换支付卡！", skin: 'msg', time: 2 });
			});

		});

		function payCardPassWord() {
			if ($("#cardId").text() == "000") {
				layer.open({ content: "请选择银行卡号再支付！", skin: 'msg', time: 3 });
				return;
			}
			// <!-- 弹出动态密码输入框，此地进行一次请求 -->
			$(".mask").fadeIn(470);
			$("#newPay").fadeOut(300);
			$("#newPay").removeClass('newPayShow');
			$("#newPay").addClass('newPayHide');

			// <!-- 此地请求一次下单接口 -->
			if (allFlag == 0 || allFlag == "0") {
				$("#takePassWord").fadeOut(300);
				$("#takePassWord").removeClass('newPayShow');
				$("#takePassWord").addClass('newPayHide');
				$('.keyboard-box').removeClass('animated fadeOutDownBig');
				$('.keyboard-box').addClass('animated fadeInUpBig');
				$("#cardPassWord").fadeIn(300);
				$("#cardPassWord").removeClass('newPayHide');
				$("#cardPassWord").addClass('newPayShow');
				$("#cardPassWord").show();
				//loadDfpCer("${encryptNo}");
				//layer.open({ type: 0, content: '请输入银行卡密码！', shadeClose: false, btn: ['我知道了'], shade: 0.2, style:"font-size:17px"});
				ap.alert({title: '提醒',content: '请输入银行卡密码！',buttonText: '我知道了'}, function(){});
			} else if (allFlag == 2 || allFlag == "2") {
				var data = {
					"smsCode": allPass,
					"cardId": $("#cardId").text(),
					"password": ""
				};
				payOrder(allPass, "${encryptNo}", data, "1");
			} else {
				//layer.open({ type: 0, content: '下单失败，请重新下单！', shadeClose: false, btn: ['我知道了'], shade: 0.2, style:"font-size:17px"});
				ap.alert({title: '提醒',content: '下单失败，请重新下单！',buttonText: '我知道了'}, function(){});
			}
		}

		// 支付或确认
		function submitBtn() {
			var pass = formatToHex(Global_show.find(".input_").val(), window.visiablepan);
			var data = {
				"smsCode": allPass,
				"cardId": $("#cardId").text(),
				"password": pass
			};
			payOrder(allPass, "${encryptNo}", data, "1");
        }
        function payPassWord() {
			if ($("#cardId").text() == "000") {
				layer.open({ content: "请选择银行卡号再支付！", skin: 'msg', time: 3 });
				return;
			}
			// <!-- 弹出动态密码输入框，此地进行一次请求 -->
			$(".mask").fadeIn(470);
			$("#newPay").fadeOut(300);
			$("#newPay").removeClass('newPayShow');
			$("#newPay").addClass('newPayHide');

			$("#takePassWord").fadeIn(300);
			$("#takePassWord").removeClass('newPayHide');
			$("#takePassWord").addClass('newPayShow');
			$("#takePassWord").show();
			//layer.open({ type: 0, content: '已发送短信验证码', shadeClose: false, btn: ['我知道了'], shade: 0.2, style:"font-size:17px;"});
			//ap.alert({title: '提醒',content: '已发送短信验证码,请填入',buttonText: '我知道了'}, function(){});
			var pwdBox = PwdBox = {
				template: '<style>.flexable{display: -webkit-box;} .flexable>div{-webkit-box-flex: 1;} .flexable.password>div{opacity: 0;height: 100%;line-height: 70px;};.flexable.password>div.active{opacity: 1 !important;}.password{ margin: 12px 25px;}.password>div{height:46px;line-height:46px;text-align:center}.password>div:first-child{border-top-left-radius:5px;border-bottom-left-radius:5px}.password>div:last-child{border-top-right-radius:5px;border-bottom-right-radius:5px}.input-box .flexable>div{height:100%}.input-box .flexable>div:active{background:#52575b1f}.list-block .item-title{font-weight:normal!important;font-size:14px}ul li{position:relative}.close{position:absolute;top:4px;left:4px;font-size:20px;width:22px;text-align:center} h1.title{height:50px;font-size:18px;line-height:50px;text-align:center;margin: 0;} .notice{height:30px;line-height:30px;font-size:12px;text-align:center;margin-bottom:15px;color: #00a9dd;}</style>' + '<div class="password-box" style="position: fixed;top:0;left:0;z-index: 100;width: 100%;height: 100%;display:none;">' + '<div class="inner-box" style="position: fixed;bottom: 0;left: 0;width: 100%;height: 470px;box-shadow: 0 0 10px rgba(0, 0, 0, 0.55);background: url(${request.contextPath}/static/images/keyboard.png) center bottom / 100% 100%;">' + '<span class="iconfont icon-guanbi close closeAll" onclick="closeGc()" style="margin-top:2px;margin-left:6px;font-size:35px;font-weight:normal">×</span>' + '<h1 class="title">支付密码</h1>' + '<div class="flexable password">' + '<div >●</div>' + '<div >●</div>' + '<div >●</div>' + '<div >●</div>' + '<div >●</div>' + '<div >●</div>' + '<!--●-->' + '</div>' + '<div class="notice color-lightblue">请输入支付密码！</div>' + '<div class="input-box">' + '<div class="flexable">' + '<div class="input-key" data-label="1" ></div><div class="input-key" data-label="2"></div><div class="input-key"  data-label="3"></div>' + '</div>' + '<div class="flexable">' + '<div class="input-key" data-label="4" ></div><div class="input-key" data-label="5"></div><div class="input-key" data-label="6"></div>' + '</div>' + '<div class="flexable">' + '<div class="input-key" data-label="7" ></div><div class="input-key" data-label="8"></div><div class="input-key" data-label="9"></div>' + '</div>' + '<div class="flexable">' + '<div></div><div class="input-key" data-label="0" ></div><div class="input-key" data-label="del"  ></div>' + '</div>' + '</div>' + '</div>' + '</div>',
				passwordOrg: '',
				password: '',
				inited: false,
				callback: function (res) {
					if (res) {
						document.querySelector('.password-box').style.display = 'none'
					} else {

					}
				},
				init: function (password, keyboard, title, notice) {
					if (pwdBox.inited) {
						return
					}
					console.log(document.getElementById('takePassWord'));
					document.getElementById('takePassWord').innerHTML += pwdBox.template;

					if (keyboard) {
						// <!--确认键盘背景-->
						document.querySelector('.password-box .inner-box').style.backgroundImage = keyboard
					}
					title && (document.querySelector('h1.title').innerText = title);
					notice && (document.querySelector('.password-box .notice').innerText = notice);
					password && (pwdBox.passwordOrg = password);
					document.querySelector('.close').addEventListener('click',
						function () {
							$(".mask").fadeOut(400);
							$("#takePassWord").fadeOut(900);
							$("#takePassWord").removeClass('newPayHide');
							$("#takePassWord").addClass('newPayShow');
							$("#takePassWord").hide();
							document.querySelector('.password-box').style.display = 'none';
							pwdBox.reset();
						});

					var inputs = document.querySelectorAll('.input-key');
					for (var i = 0; i < inputs.length; i++) {
						inputs[i].addEventListener('touchstart',
							function (e) {
								onTouch(this.getAttribute('data-label'))
							},
							true)
					}
					var onTouch = function (label) {
						if (label == 'del') {
							pwdBox.password = pwdBox.password.substr(0, pwdBox.password.length - 1);
							pwdBox.onChange()
						} else {
							pwdBox.password += label;
							pwdBox.onChange();
							if (pwdBox.password.length == 6) {
								pwdBox.callback({
									status: true,
									password: pwdBox.password
								})

							}
						}
					};
					pwdBox.inited = true
				},
				onChange: function () {
					var texts = document.querySelectorAll('.password>div');
					for (var i = 0; i < texts.length; i++) {
						texts[i].style.opacity = 0
					}
					for (i = 0; i < pwdBox.password.length; i++) {
						texts[i].style.opacity = 1
					}
				},
				reset: function () {
					//密码输入完成后，关闭
					pwdBox.password = '';
					pwdBox.onChange();
					$(".mask").fadeOut(400);
					$("#takePassWord").fadeOut(900);
					$("#takePassWord").removeClass('newPayHide');
					$("#takePassWord").addClass('newPayShow');
					$("#takePassWord").hide();
					document.querySelector('.password-box').style.display = 'none'
				},
				show: function (callback) {
					if (callback) {
						pwdBox.callback = callback
					}
					document.querySelector('.password-box').style.display = 'block'
				}
			};

			PwdBox.init('', '${request.contextPath}/static/images/pwd_keyboard.png', '请输入短信验证码', '安全支付环境，请放心使用！');
			// <!-- 验证用户输入的密码 -->
			PwdBox.show(function (res) {
				if (res.status) {
					allPass = res.password;
					payCardPassWord();
				}
			});
			orderSubmit();
		}

		function orderSubmit() {
			var cardId = $("#cardId").text();
			var data = { "cardId": cardId };
			var url = "${request.contextPath}/hyh5api/order/${encryptNo}";
			//var status = bandToCard("${encryptNo}",data);
			$.ajax({
				url: url,
				type: 'post',
				data: data,
				dataType: 'json',
				success: function (result) {
					$("#cardTypeClink").hide();
					$("#cardTypeClink2").show();
					if (!result || !result.code) {
						layer.open({ content: result.message, skin: 'msg', time: 3 }); return;
					}
					if ( result.code == "5200AAA") {
						//表示卡信息输入不完整
						closeGc();
						$(".mask").fadeIn(10);
						$(".alertBox").show();
						$("#alertBoxOk").attr("onclick","window.location.href=\'${request.contextPath}/hyh5api/jumpAdd/${encryptNo}?merchNo=123456&payType=1&flag=2&cardId="+result.cardId+"\'");
						return;
						
					}
					ap.alert({title: '提醒',content: '已发送短信验证码,请填入',buttonText: '我知道了'}, function(){});
					if ( result.code == "520000" ) {
						if (result.status == "1") {
							//表示已经处于下单中的状态
							loadDfpCer("${encryptNo}");
						} else if (result.status == "3") {
							//下单中
							loadDfpCer("${encryptNo}");
						} else if (result.status == "2" || result.status == "5") {
							layer.open({ content: result.message, skin: 'msg', time: 3 });
							paySuccess("${encryptNo}", "2", result.message, "1");
							allFlag = 1;   //下单失败
							return;
						}
						if ( result.rules ) {
							if (JSON.parse(result.rules).password == true || JSON.parse(result.rules).password == "true") {
								window.publicKey = result.publicKey;
								window.exponent = result.exponent;
								window.visiablepan = result.visiablepan;
								allFlag = 0;  //表示需要输入卡密
							} else {
								allFlag = 2;
                            }
                        } else {
							allFlag = 2;
						}
						return
					}
					layer.open({ content: result.message, skin: 'msg', time: 3 });
					paySuccess("${encryptNo}", "2", result.message, "1");
					allFlag = false;

				}
			});
			if (status == "3") {
				//表示已经处于下单中的状态
				$("#cardTypeClink").hide();
				$("#cardTypeClink2").show();
			}
		}
		//选完卡后动画效果
		function bandCards(accNo, cardName, idNo) {
			$("#cardType").fadeOut(900);
			$("#cardType").addClass('newPayHide');
			$("#cardType").removeClass('newPayShow');
			$("#cardType").hide();
			$("#newPay").fadeIn(200);
			$("#newPay").addClass('newPayShow');
			$("#newPay").removeClass('newPayHide');
			$("#cardName").text(cardName);
			$("#cardName2").text(cardName);
			$("#cardId").text(accNo);
			$("#idNo").text(idNo);
		}

		function closeGc() {
			$(".password div").removeAttr('style');
			$(".mask").fadeOut(470);
			$(".mask").fadeOut(400);
			$("#takePassWord").fadeOut(900);
			$("#takePassWord").removeClass('newPayHide');
			$("#takePassWord").addClass('newPayShow');
			$("#takePassWord").hide();
			document.querySelector('.password-box').style.display = 'none';
		}

		function cloasePass() {
			Global_show.find(".input_1").val("");
			Global_show.find(".input_2").val("");
			Global_show.find(".input_3").val("");
			Global_show.find(".input_4").val("");
			Global_show.find(".input_5").val("");
			Global_show.find(".input_6").val("");
			Global_count = 0;
			$(".mask").fadeOut(470);
			$("#cardPassWord").removeClass('newPayShow');
			$("#cardPassWord").addClass('newPayHide');
			$("#cardType").removeClass('newPayShow');
			$("#cardType").addClass('newPayHide');
			$('.keyboard-box').removeClass('animated fadeInUpBig');
			$('.keyboard-box').addClass('animated fadeOutDownBig');
		}

		function getCardInfos(flag) {
			var userId = "${userId}";
			var data = { "userId": userId };
			var url = "${request.contextPath}/hyh5api/selectCardInfos/${encryptNo}";
			$.ajax({
				url: url,
				type: 'post',
				data: data,
				dataType: 'json',
				success: function (result) {
					if (!result) {
						layer.open({ content: "查询卡信息失败！", skin: 'msg', time: 2 });
					} else {
						// <!-- 循环遍历银行卡信息-->
						var cardInfo = JSON.parse(result.bankInfo);
						var index = 0;
						if (cardInfo.length != 0) {
							$('#cardTypes').html('');
							for (var i = 0; i < cardInfo.length; i++) {
								index++;
								if (cardInfo[i].bankType == 2) {
									//无效卡
									continue;
								}
								var bankName = cardInfo[i].bankName;
								if (bankName.length > 7) {
									bankName = bankName.substring(0, 7) + "…";
								}

								document.getElementById('cardTypes').innerHTML +=
									'<div id=\"c_' + cardInfo[i].id + '"/' + ' style="height: 50px; border-bottom: 0.8px solid #F0F0F0; line-height: 50px; margin-left: 25px;" onclick="bandCards(\'' + cardInfo[i].id + '\',' + '\'' + bankName + '(' + cardInfo[i].cardNo.substring(cardInfo[i].cardNo.length - 4) + ')\'' + ',\'' + cardInfo[i].idNo + '\')">'
									+ '<span style="float: left;  letter-spacing:1px;">'
									+ '<img src="${request.contextPath}/static/images/payment_card_24px_505044_easyicon.net.png"/>'
									+ '　' + cardInfo[i].bankName + '(' + cardInfo[i].cardNo.substring(cardInfo[i].cardNo.length - 4) + ')'
									+ '</span>'
									+ '<span id=\"' + cardInfo[i].id + '"/' + ' class="yStyle" style="float: right; padding-right: 23px;font-weight:border;font-size:20px;color:#409eff; display:none">'
									+ '✔'
									+ '</span>'
									+ '</div>'
							}
							if (flag == "1") {
								//  <!-- 设置默认银行卡信息（选择新增的最后一张） -->
								var bankName = cardInfo[index - 1].bankName;
								if (bankName.length > 7) {
									bankName = bankName.substring(0, 7) + "…";
								}
								$("#cardName").text(bankName + "(" + cardInfo[index - 1].cardNo.substring(cardInfo[index - 1].cardNo.length - 4) + ")");
								$("#cardName2").text(bankName + "(" + cardInfo[index - 1].cardNo.substring(cardInfo[index - 1].cardNo.length - 4) + ")");
								$("#cardId").text(cardInfo[index - 1].id);
								$("#idNo").text(cardInfo[index - 1].idNo);
							}
							var id = $("#cardId").text();
							$(".yStyle").css("display", "none");
							$("#" + id).css("display", "block");
						} else {
							$("#cardName").text("　　　　请添加银行卡");
							$("#cardId").text("000");
							$("#cardName2").text("　　　　请添加银行卡");
						}
					}
				}
			});
		}

		/**
		 * @描述 异步加载银联设备资源
		 */
		try {
			var script = document.createElement('script');
			script.async = "async";
			script.src = "https://device.95516.com/dcs_svc/gateway/scripts/dcs_gateway.js";
			document.body.appendChild(script);
		} catch (e) {
			console.log(e);
		}
	</script>

	<script type="text/javascript">
		$(".keyboard-box").KeyBoard({
			random: false, // 随机键盘
			type: "password", // 密码 password or 金额 money
			show: $(".keyboard-show-text"), // 展示区域
			safe: true // 加密显示
		});
	</script>
	<script src="${request.contextPath}/static/js/hy/custom.hy.alipay.js"></script>
</body>

</html>