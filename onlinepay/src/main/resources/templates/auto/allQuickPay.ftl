<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
	<meta name="referrer" content="origin">
    <title>收银台</title>
	<link rel="stylesheet" type="text/css" href="static/bootstrap/css/main.css">
	<link rel="stylesheet" type="text/css" href="static/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="static/bootstrap/css/bootstrap-theme.min.css">
	<link rel="stylesheet" type="text/css" href="static/bootstrap/css/layer.css">
	<script src="static/js/jquery-1.11.1.min.js"></script>
	<script src="static/bootstrap/js/bootstrap.min.js"></script>
	<script src="static/bootstrap/js/layer.js"></script>
	
	<style type="text/css">
		.lable{
			line-height: 30px;
		} 
		#container {
		  position: absolute;
		  left: 50%;
		  top: 40%;
		}
		
		.item {
		  padding: 3px 5px;
		  cursor: pointer;
		}
		.selectform{
			width:100%;
			height: 36px;
			border-radius: 10px 10px 0px 0px;
			border-style: hidden;
			background-color: #ccc;
		}
		</style>
  </head>

  <body style="overflow-y: auto;">
	<!--顶部-->
	<div class="col-xs-12 gateway-topbg" style="background-color: #4094DC;">
		<div style="margin:0;padding-right: 0;margin-top: 9px;text-align: center;">
			<div style="font-size: 22px;color:white;">${payrequest.channelSource}号收银台</div>
		</div>
	</div>
	<!-- 中部底部 -->
	<div class="container-fluid fl" style="padding-right: 5px; padding-left: 5px;width:100%">
		<!--订单信息-->
		<div class="row gateway-info" style="line-height: 24px">
			<div class="col-xs-10 col-md-7  col-lg-7 col-md-offset-2  mtb10" style="padding-left:30px">
				<div class="col-xs-12 col-md-6 col-lg-6">订单号：${payrequest.vcOrderNo}</div>
				<div class="col-xs-12 col-md-6 col-lg-6">商品名称：${payrequest.goodsName}</div>
				<div class="col-xs-12 col-md-6 col-lg-6">应收金额：<span class="text-danger">￥${payrequest.amount}</span></div>
				
			</div>
		</div>
		
		<!--绑卡支付选择-->
		<div class="col-xs-12 col-md-offset-2  mb10" style="padding: 0px;margin-bottom: 0;" >
			<div class="col-xs-6 col-md-4 mb5" style="padding: 0px;margin-bottom: 0;">
				<input type="button" value="绑定银行卡" onclick="showThis(1)" class="selectform">
			</div>
			<div class="col-xs-6 col-md-4 mb5" style="padding: 0px;margin-bottom: 0;">
				<input type="button" value="选择付款银行卡" onclick="showThis(2)" class="selectform">
			</div>
		</div>
		
		<!--绑卡信息  -->
		<div id ="form_1" class="col-xs-12 col-md-8 col-md-offset-2" style="padding: 0;height: 260px">
			    <input id="merchantNo" name="merchantNo" type="hidden" value="${payrequest.merchantNo}">
				<input id="memberId" name="memberId" type="hidden" value="${payrequest.memberId}">
			    <input id="checkCode" name="checkCode" type="hidden" value="">
			     <input id="bindMsgNo" name="bindMsgNo" type="hidden" value="">
			    
			    
				<!--卡类型-->
				<div class="col-xs-12 mb10" id="bankType" style="margin-top: 10px;">
					<div class="col-xs-4 col-md-4 lable" style="padding-right:0">卡片类型</div>
					<div class="col-xs-4 col-md-4 lable">
					储蓄卡
					</div>
				</div>
				<!-- 银行卡号-->
				<div class="col-xs-12 mb10">
				 <div class="col-xs-4 col-md-4 mb5 lable" >银行卡号</div>
				 <div class="col-xs-8 col-md-4 mb5">
					    <div id="content" class="row">
					    	<span><input id="pcCardNo" name="pcCardNo" class="form-control" onKeyup="javascript:creditCheck(this);" value="4581231314628892"/>
						</span>
							<span id="errorcardNo" class="text-danger lh32"> </span>
					      	<div id="append"></div>
					    </div>
				 </div>
				</div>
				<!-- 姓名 -->
				<div class="col-xs-12 mb10" id="divname">
					<div class="col-xs-4 col-md-4 mb5 lable">真实姓名</div>
					<div class="col-xs-8 col-md-4 mb5 ">
						<div class="row">
							<span>
								<input type="text" name="pdName" id="pdName" class="form-control" value="溪晓亚" >
							</span> <span id="errorpayerName" class="text-danger lh32"> <span
								class="icon icon-error-sm"></span>
							</span>
						</div>
					</div>
				</div>
				<!-- 证件号 -->
				<div class="col-xs-12 mb10" id="divcode">
					<div class="col-xs-4 col-md-4 mb5 lable">身份证件号</div>
					<div class="col-xs-8 col-md-4 mb5 ">
						<div class="row">
							<span > 
								<input type="text" name="peIdNum" id="peIdNum" class="form-control" onblur="javascript:checkCredNo();" value="372926199202026022"></span> 
								<span id="errorcredNo" class="text-danger lh32">
								<span class="icon icon-error-sm"></span>
							</span>
						</div>
					</div>
				</div>
				<!-- 手机号 -->
				<div class="col-xs-12 mb10">
					<div class="col-xs-4 col-md-4 mb5 lable" style="padding-right: 0">银行预留手机号</div>
					<div class="col-xs-8 col-md-4 mb5 ">
						<div class="row">
							<span class="col-xs-12">
								<div class="row">
									<span > 
										<input type="text" name="payerPhone" id="payerPhone" value="13869709662" class="form-control fl" onblur="javascript:checkPhone();">
									</span> 
								</div>
							</span> 
							<span class=""><span id="errorpayerPhone" class="text-danger lh32"></span></span>
						</div>
					</div>
				</div>
				<!--绑卡短信验证码  -->
				<div class="col-xs-12 mb10" id="divcode">
					<div class="col-xs-4 col-md-4 mb5 lable">短信验证码</div>
					<div class="col-xs-8 col-md-4 mb5 ">
						<div class="row">
							<span class="col-sm-6 col-xs-8"> 
								<input type="text" id="bindSmsCode" class="form-control">
							</span>
							<span class="col-sm-6 col-xs-4"> 
								<input type="button" value="获取验证码" id="smsCodebutton" class="fr" onclick="javascript:bindSmsCode();" style="width:80px; height:34px; border-radius:3px;">
							</span>
						</div>
					</div>
				</div>
				<!-- 确认绑卡 -->
				<div class="col-xs-12 col-sm-4 col-md-offset-4 col-sm-offset-1 mb20" style="margin-top:10px;">
					<input id="button_bind" value="确认信息并绑定银行卡" type="button" class="bankbtn-submit col-xs-12" onclick="invokeBind();">
				</div>
		</div>
		
		<div id="form_2" style="display:none;">
			<!--银行卡列表 -->
			<div id="bank_item" class="col-xs-12 col-md-8 col-md-offset-2" style="background-color:#dddddd;padding:10px;overflow:scroll;"></div>
			<!--短信验证码  -->
			<div id="button_sms" class="col-xs-12 col-md-8 col-md-offset-2 mb10" style="margin-top: 10px;">
				<div class="col-xs-3 col-md-4 mb5 lable" style="padding:0">短信验证码</div>
				<div class="col-xs-9 col-md-4 mb5" style="padding:0">
					<div class="row">
						<span class="col-sm-6 col-xs-8"> 
							<input type="text" id="smsCode" class="form-control">
						</span>
						<span class="col-sm-6 col-xs-4"> 
							<input type="button" value="获取验证码" id="smsPaybutton" class="fr" onclick="javascript:getSmsCode();" style="width:80px; height:34px; border-radius:3px;">
						</span>
					</div>
				</div>
			</div>
			<!--确认支付-->
			<div id="button_pay" class="col-xs-12 col-sm-4 col-md-offset-4 col-sm-offset-1 mb20">
				<input name="button" id="payButton" value="提交订单" type="button" class="bankbtn-submit col-xs-12" onclick="invokePay();">
			</div>
		</div>
</body>
</html>

<script type="text/javascript">
	//第一步：绑定银行卡
	//获取绑卡验证码
	function bindSmsCode() {
		if(!checkCardNo() ||  !checkName() || !checkPhone() || !checkCredNo() ){
			layer.open({content: "绑卡信息不完整" ,skin: 'msg' ,time: 3 });
			return;
		}
		var data = {
			"memberId" : "${payrequest.memberId}",
			"channelKey" : "${payrequest.channelKey}",
			"userId" : "${payrequest.userVcId}",
			"merchantNo":"${payrequest.merchantNo}",
			"upperType":"${payrequest.channelSource}",
			"orderId" : "${payrequest.vcOrderNo}",
			"idNo" : $("#peIdNum").val(),//身份证
			"userName" : $("#pdName").val(),//名称
			"phone" : $("#payerPhone").val(),//电话
			"cardNo" : $("#pcCardNo").val()//卡号
		};
		$("#button_bind").attr('disabled', true);
		var index = layer.open({type: 2,content: '正在发送短信，请稍后...',shadeClose: false});
		$.ajax({
			url : 'SandBindCardSmsApi',
			type : 'POST',
			data : data,
			dataType : 'json',
			success : function(data){
				btnInterval();
				layer.close(index);
				$("#button_bind").attr('disabled',false);
				if(data !=null && data.code == "10000"){
					$("#bindMsgNo").val(data.message);
					layer.open({content: "发送短信成功" ,skin: 'msg' ,time: 3 });
				}else{
					layer.open({content: data.message ,skin: 'msg' ,time: 3 });
				}
			}
		});
	}
	//确定绑卡
	function invokeBind() {
		if(!checkCardNo() ||  !checkName() || !checkPhone() || !checkCredNo() ){
			layer.open({content: "绑卡信息不完整" ,skin: 'msg' ,time: 3 });
			return;
		}
		if (!$("#bindSmsCode").val()) {
			layer.open({content: "请输入短信验证码" ,skin: 'msg' ,time: 4 });
			return false;
		}
		var data = {
			"quickOperType" : "quickPayBindInfo",
			"memberId" : "${payrequest.memberId}",
			"userId" : "${payrequest.userVcId}",
			"merchantNo":"${payrequest.merchantNo}",
			"upperType":"${payrequest.channelSource}",
			"orderId" : "${payrequest.vcOrderNo}",
			"bindMsgNo" : $("#bindMsgNo").val(),//上游返回信息
			"code" : $("#bindSmsCode").val(),//绑卡短信验证码
			"idNo" : $("#peIdNum").val(),//身份证
			"userName" : $("#pdName").val(),//名称
			"phone" : $("#payerPhone").val(),//电话
			"cardNo" : $("#pcCardNo").val()//卡号
		};
		$("#button_bind").attr('disabled', true);
		var index = layer.open({type: 2,content: '正在绑卡，请稍后...',shadeClose: false});
		$.ajax({
			url : 'quickPayServiceApi',
			type : 'POST',
			data : data,
			dataType : 'json',
			success : function(data){
				layer.close(index);
				$("#button_bind").attr('disabled',false);
				if(data !=null && data.code == "10000"){
					layer.open({content: "绑卡成功" ,skin: 'msg' ,time: 3 });
				}else{
					layer.open({content: data.message ,skin: 'msg' ,time: 3 });
				}
			}
		});
	}
	
	//第二步：获取短信验证码
	function getSmsCode() {
		var selectedBankId = $("input[name='selectedBank']:checked").val();
		if(!selectedBankId){
			layer.open({content: "请选择卡号" ,skin: 'msg' ,time: 4 });
			return;
		}
		var data = {
			"quickOperType" : "quickPaySendSms",
			"memberId" : "${payrequest.memberId}",
			"merchantNo":"${payrequest.merchantNo}",
			"upperType":"${payrequest.channelSource}",
			"orderId" : "${payrequest.vcOrderNo}",
			"selectedBankId" : selectedBankId
		}
		$("#button_bind").attr('disabled', true);
		var index = layer.open({type: 2,content: '正在发送短信，请稍后...',shadeClose: false});
		jQuery.ajax({
			url : "quickPayServiceApi",
			type : "POST",
			datatype : "json",
			data : data,
			contentType : "application/x-www-form-urlencoded;charset=gbk",
			success : function(data) {
				btnIntervalPay();
				layer.close(index);
				$("#button_bind").attr('disabled',false);
				if(data !=null && data.code == "10000"){
					layer.open({content: "验证码已经发送成功" ,skin: 'msg' ,time: 3 });
					$("#checkCode").val(data.contractId);
					return;
				}else{
					if(data.message){
						layer.open({content: data.message ,skin: 'msg' ,time: 3 });
					}else{
						layer.open({content: "验证码发送失败" ,skin: 'msg' ,time: 3 });
					}
				}
			},error : function(request, status, errorThrown) {
				showmessage("errorMsg", "系统异常，请刷新页面后重新操作.");
			}
		});
	}
	
	//第三步：发起支付
	function invokePay() {
		var selectedBankId = $("input[name='selectedBank']:checked").val();
		if(!selectedBankId){
			layer.open({content: "请选择支付银行卡" ,skin: 'msg' ,time: 4 });
			return;
		}
		if (!$("#smsCode").val()) {
			layer.open({content: "请输入短信验证码" ,skin: 'msg' ,time: 4 });
			return false;
		}
		$("#payButton").attr('disabled',true);
		document.getElementById("payButton").style.background="#CCCCCC";
		var index = layer.open({type: 2,content: '正在支付，请稍后...',shadeClose: false});
		 $.ajax({
			url : 'quickPayServiceApi',
			type : 'POST',
			data : {
				"quickOperType" :"quickPayOrder",
				"memberId" : "${payrequest.memberId}",
				"merchantNo":"${payrequest.merchantNo}",
				"upperType":"${payrequest.channelSource}",
				"orderId" : "${payrequest.vcOrderNo}",
				"checkCode" : $("#smsCode").val(),
				"selectedBankId" : selectedBankId
			},
			dataType : 'json',
			success : function(data){
				layer.close(index);
				document.getElementById("payButton").style.background="#4094DC";
				$("#payButton").attr('disabled',false);
				if(data !=null && data.code == "10000"){
					window.location.href = "/onlinepay/success.ftl";
				}else{
					layer.open({content: data.message ,skin: 'msg' ,time: 5 });
				}
			}
		});
	}

	//选择绑卡或者支付
	function showThis(type) {
		if(type == 2){
			//显示支付页面
			$('#form_2').show();
			$('#form_1').hide();
			var data = {
				"quickOperType" :"getPayBindInfo",
				"memberId" : "${payrequest.memberId}",
				"merchantNo":"${payrequest.merchantNo}",
				"upperType":"${payrequest.channelSource}",
				"orderId" : "${payrequest.vcOrderNo}"
			};
			$.ajax({
				url : 'quickPayServiceApi/bindInfo',
				type : 'POST',
				data : data ,
				dataType : 'json',
				success : function(data) {
					if(data !=null && data.code == "10000"){
						var bankItem = "";
						$.each(data.resObj, function(i, item){
							var bankNo = item.cardNo.substring(0,6)+" **** **** "+item.cardNo.substring(item.cardNo.length-4,item.cardNo.length);
							bankItem += "<div class='col-xs-12 mb10' style='background-color:#fff;border-radius:4px;padding:0px;'><div class='col-xs-3 col-md-4 mb5 lable' style='padding-left:10px'>"+item.bankName+"</div>";
							bankItem += "<div class='col-xs-6 col-md-4 mb5 lable'>"+bankNo+"</div><div class='col-xs-2 col-md-4 mb5 lable'><input type='radio' name='selectedBank' value="+item.id+"></div></div>";
						}); 
						document.getElementById("bank_item").innerHTML=bankItem;
					}else{
						layer.open({content : data.message ,skin : 'msg',time : 10 });
						return;
					}
				}
			});
		}else{
			//显示绑卡页面
			$('#form_1').show();
			$('#form_2').hide();
		}
	}
	
	var time = 60;
	function btnInterval() {
		var smsCodebutton = $("#smsCodebutton");
		if (time == 0) {
			smsCodebutton.val("获取验证码");
			smsCodebutton.attr('disabled', false);
			time = 60;
			return;
		}
		smsCodebutton.val((time--) + "秒");
		si = setTimeout('btnInterval()', 1000);
	}
	var timePay = 60;
	function btnIntervalPay() {
		var smsCodebutton = $("#smsPaybutton");
		if (timePay == 0) {
			smsCodebutton.val("获取验证码");
			smsCodebutton.attr('disabled', false);
			timePay = 60;
			return;
		}
		smsCodebutton.val((timePay--) + "秒");
		si = setTimeout('btnIntervalPay()', 1000);
	}
	
	//检查验证码
	function checkSmsCode() {
		var code = $("#smsCode").val();
		if (!code) {
			showmessage("smsCodeMsg", "请输入短信验证码");
			return false;
		} else {
			return true;
		}
	}
		
	//检查电话号码
	function checkPhone() {
		var phone = $("#payerPhone").val();
		var reg = /(^\d{11}$)/;
		if (reg.test(phone) === false) {
			showmessage("errorpayerPhone", "手机号输入不合法");
			return false;
		}
		showmessage("errorpayerPhone", "");
		return true;
	}

	//消息显示
	function showmessage(key, value) {
		var message = $("#" + key);
		message.html(value);
	}
	
	//检查名字
	function checkName() {
		var pdName = $("#pdName").val();
		if (!pdName) {
			showmessage("errorpayerName", "请输入名字");
			return false;
		} 
		showmessage("errorpayerName", "");
		return true;
	}

	//检查卡号
	function checkCardNo() {
		var cardNo = $("#pcCardNo").val();
		cardNo = cardNo.replace(new RegExp(" ", "gm"), "");
		var reg = /(^\d{15,21}$)/;
		if (reg.test(cardNo) === false) {
			showmessage("errorcardNo", "卡号输入不合法");
			return false;
		} else {
			showmessage("errorcardNo", "");
			return true;
		}
	}

	//检查身份证
    function checkCredNo() {
		var credNo = $("#peIdNum").val();
		showmessage("errorcredNo", "");
		// 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X  
		var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
		if (reg.test(credNo) === false) {
			showmessage("errorcredNo", "身份证输入不合法");
			return false;
		}
		return true;
	} 

	//检查信用卡输入，及时检查
	function creditCheck(textObject) {
		if (textObject != null) {
			$("#showCardNo").show();
			try {
				var textValue = "";
				textValue = textObject.value;
				var NumberString = "0123456789";
				var intNo;
				intNo = 0;
				textValue = textValue.replace(/[ ]/g, "");

				for (var i = 0; i < textValue.length; i++) { // loop all character
					if (NumberString.indexOf(textValue.substring(i, i + 1)) == -1) {
						break;
					}
					intNo = i + 1;
				}
				if (intNo == 0) { // set value = "" if value is "0"
					textValue = "";
				} else {
					if (intNo > 20) {
						var first4 = textValue.substring(0, 4) + ' ';
						var first8 = textValue.substring(4, 8) + ' ';
						var first12 = textValue.substring(8, 12) + ' ';
						var first16 = textValue.substring(12, 16) + ' ';
						var first20 = textValue.substring(16, 20)+' ';
						var first21 = textValue.substring(20, 21);
						textValue = first4 + first8 + first12 + first16
								+ first20 + first21;
					}else if (intNo > 16) {
						var first4 = textValue.substring(0, 4) + ' ';
						var first8 = textValue.substring(4, 8) + ' ';
						var first12 = textValue.substring(8, 12) + ' ';
						var first16 = textValue.substring(12, 16) + ' ';
						var first20 = textValue.substring(16, 20);
						textValue = first4 + first8 + first12 + first16
								+ first20;
					} else if (intNo > 12) {
						var first4 = textValue.substring(0, 4) + ' ';
						var first8 = textValue.substring(4, 8) + ' ';
						var first12 = textValue.substring(8, 12) + ' ';
						var first16 = textValue.substring(12, 16);
						textValue = first4 + first8 + first12 + first16;

					} else if (intNo > 8) {
						var first4 = textValue.substring(0, 4) + ' ';
						var first8 = textValue.substring(4, 8) + ' ';
						var first12 = textValue.substring(8, intNo);
						textValue = first4 + first8 + first12;
					} else if (intNo > 4) {
						var first4 = textValue.substring(0, 4) + ' ';
						var first8 = textValue.substring(4, intNo);
						textValue = first4 + first8;
					} else {
						textValue = textValue.substring(0, intNo);
					}
				}
				textObject.value = textValue;
			} catch (e) {
			}
		}
	}
</script>