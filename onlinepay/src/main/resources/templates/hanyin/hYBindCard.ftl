<!DOCTYPE html>
<html>
<head>
<meta name=viewport content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>添加银行卡</title>

<script src="http://paypaul.prxgg.cn/STATIC/jquery.min.js"></script>
<script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/jquery.qrcode.min.js"></script>
<script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/utf.js"></script>

<script src="http://paypaul.prxgg.cn/STATIC/bootstrap.min.js"></script>
<script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/clipboard.min.js"></script>
<script src="https://gw.alipayobjects.com/as/g/h5-lib/alipayjsapi/3.1.1/alipayjsapi.inc.min.js"></script>
<link href="http://paypaul.prxgg.cn/STATIC/bootstrap.min.css" rel="stylesheet">
<script src="${request.contextPath}/static/js/layer.js"></script>
<link href="${request.contextPath}/static/css/layer.css" rel="stylesheet">

<style type="text/css">
.text1 {
	border-top: 0px;
	border-bottom: 0.8px solid #E8E8E8;
	height: 48px;
	line-height: 40px;
	font-weight: 400px;
	font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
}
.card{
font-size:18px;
}
input:focus{outline:none;}

#SupportCard {
			height:100%;
			width:100%;
			background-color:#FFFFFF;
			position:fixed;
			bottom:0px;
			z-index:1000;
			display:none;
		}
		
#checkCardTypeDiv {
			height:160px;
			width:94%;
			background-color:#FFFFFF;
			z-index:1000;	
			display:none;
			position:fixed;
			bottom: 45%;
			left:3%;
			border-radius:7px 7px 7px 7px;

}

#checkCardTypeDiv2 {
			height:224px;
			width:94%;
			background-color:#FFFFFF;
			z-index:1000;	
			display:none;
			position:fixed;
			bottom: 45%;
			left:3%;
			border-radius:7px 7px 7px 7px;

}
		
.layui-m-layercont {
    padding: 10px 30px;
    line-height: 22px;
    text-align: center;
    padding-bottom:30px;
}

.layui-m-layerchild h3 {
    padding: 0 10px;
    height: 40px;
    font-size: 16px;
    font-weight: 400;
    border-radius: 5px 5px 0 0;
    text-align: center;
}

.mask {
			position:fixed;
			top:0;
			bottom:0;
			left:0;
			right:0;
			background:black;
			z-index:100;
			opacity: 0.4;
		}
	
</style>

</head>

<body>

	<div class="container-fluid" style="position:relative">
	    <!--窗体上半部分-->
		<div class="row-fluid">
			<div class="span12">
				<br /><br />
				<h3 style="text-align: center; font-size: 26px;">
					<strong style="color:#707070">支付安全保障</strong>
				</h3>
				<h5 style="text-align: center;color:#B8B8B8;line-height:30px;">首次绑定后下次无需再绑</h5>

				<br>
				<br>
				<div class="text1" style="">
					<span style="float: left;	width:50px; font-size:16px;padding-top:3px;">卡号</span><span style="float: left;margin-left:30px;">
					  <!-- <label id="newCardText" style="float:left"></label>-->
					  <input id="cardNo" class="card" style="height:47px;border:0px;float:left;" placeholder="请输入银行卡账号"  type="text" 
					  onkeyup="this.value=this.value.replace(/\D/g,'').replace(/....(?!$)/g,'$&amp; ')" onkeydown="this.value=this.value.replace(/\D/g,'').replace(/....(?!$)/g,'$&amp; ')" maxlength="30"></input>
					</span>
				</div>
				
				<div class="text1" style="">
					<span style="float: left;	width:50px; font-size:16px;padding-top:3px;">手机号</span><span style="float: left;margin-left:30px;">
					  <input id="phone" class="card" style="height:47px;border:0px;float:left;" placeholder="请输入预留手机号"  type="text" 
					  onkeyup="this.value=this.value.replace(/\D/g,'')" onkeydown="this.value=this.value.replace(/\D/g,'')" maxlength="20"></input>
					</span>
				</div>
				
				<div class="text1" style="" id="textIdNo">
					<span style="float: left;	width:50px; font-size:16px;padding-top:3px;">证件号</span><span style="float: left;margin-left:30px;">
					  <!-- <label id="newCardText" style="float:left"></label>-->
					  <input id="idNo2" class="card" style="height:47px;border:0px;float:left;" placeholder="请输入预留身份证号"  type="text" 
					  onkeyup="this.value=this.value.replace(/\W/g,'')" onkeydown="this.value=this.value.replace(/\W/g,'')" maxlength="30" readonly="readonly"></input>
					</span>
				</div>
				
				<div class="text1" style="" id="textIdNo2">
					<span style="float: left;	width:50px; font-size:16px;padding-top:3px;">证件号</span><span style="float: left;margin-left:30px;">
					  <!-- <label id="newCardText" style="float:left"></label>-->
					  <input id="idNo4" class="card" style="height:47px;border:0px;float:left;" placeholder="请输入预留身份证号"  type="text" 
					  onkeyup="this.value=this.value.replace(/\W/g,'')" onkeydown="this.value=this.value.replace(/\W/g,'')" maxlength="30" readonly="readonly"></input>
					</span>
				</div>
				
				<div class="text1" style="" id="textUserName">
					<span style="float: left;	width:50px; font-size:16px;padding-top:3px;">开户名</span><span style="float: left;margin-left:30px;">
					  <!-- <label id="newCardText" style="float:left"></label>-->
					  <input id="userName4" class="card" style="height:47px;border:0px;float:left;" placeholder="请输入预留开户名"  type="text" 
					   maxlength="30" readonly="readonly"></input>
					</span>
				</div>
				
				
				
				<br/>
				<span style="float:right;font-size:17px;"><a id="showBank">支持银行查询</a></span>

				<br>
				<br> <br />
				<div style="text-align: center;" id="paying">
					<a href="javascript:" id="parOrder" class="btn btn-info btn-lg" style="background-color:#409eff;" onclick="bindCard()"
						>　　　　　　下一步　　　　　　</a>
				</div>
				<div class="row" style="text-align: center;">
					<div class="col-sm-5" style="width: 41.786667%;"></div>

				</div>

				<br />
			</div>
		</div>
		
	</div>
		<div id="SupportCard" style="z-index:999">
			 <div style="text-align: center; font-size: 15px; border-bottom: 0.8px solid #E8E8E8; background-color:#409eff">
				<!--<span class="iconfont icon-guanbi close closeAll2"  style="margin-top:5px;margin-left:6px;font-size:35px;font-weight:normal">×</span>-->
				<span style="line-height: 50px;letter-spacing:2.5px;font-size:20px;color:#FFFFFF"><strong>支持银行</strong><span>
			</div>
			<div class="container-fluid" style="height:40px;">
	        	<div style="color:#00a9dd;text-align:center;padding-top:10px;">后续会陆续开放其它卡类型</div>
	        	<div class="keyboard-box"></div>
   		 	</div>
   		 	<div style="overflow:auto;height:608px;">
   		 	
   		 		<div style="width:100%;height:40px;background-color:#E8E8E8;padding-top:10px;color:#A0A0A0">
	   		 	    <span style="">　不支持卡类型</span>
	   		 	    <span style="float: right; padding-right: 5%;">所有卡</span>
	   		 	</div>
	   		 	
				<div id="black">
				
				</div>
				
				
	   		 	<div style="width:100%;height:40px;background-color:#E8E8E8;padding-bottom:10px;padding-top:10px;color:#A0A0A0">
	   		 	    <span style="">　支持卡类型</span>
	   		 	    <span style="float: right; padding-right: 5%;">储蓄卡</span>
	   		 	    <span style="float: right; padding-right: 9%;">日限额</span>
	   		 	    <span style="float: right; padding-right: 9.5%;">单笔限额</span>
	   		 	    
	   		 	    
	   		 	</div>
				
				<div id="white">
				
				</div>
				
				<div style="width:100%;height:100px;background-color:#E8E8E8;color:red;bottom: 0">
				
				</div>
				<div style="width:100%;height:65px;background-color:black;color:black;position:fixed;bottom: 0;text-align: center; opacity: 0.5;">
				<a href="javascript:" id="backBank" class="btn btn-info btn-lg" style="background-color:#409eff;margin-top:11px;opacity:1;z-index:10000"
						>　　　　返回继续绑卡　　　　</a>
				</div>
			</div>
		</div>
		
		<div id="checkCardTypeDiv" style="z-index:999">
		        <br/>
				<div class="text1" style="margin-top:16px;">
					<span style="float: left;	margin-left:10px;width:50px; font-size:16px;padding-top:3px;">证件号</span><span style="float: left;margin-left:30px;">
					  <!-- <label id="newCardText" style="float:left"></label>-->
					  <input id="idNo" class="card" style="height:47px;border:0px;float:left; font-size:15px;" placeholder="请输入预留身份证号"  type="text" 
					  onkeyup="this.value=this.value.replace(/\W/g,'')" onkeydown="this.value=this.value.replace(/\W/g,'')" maxlength="30"></input>
					</span>
				</div>
				<a href="javascript:" id="backOk" class="btn btn-info btn-lg" style="background-color:#409eff;margin-top:11px;opacity:1;z-index:10000;float:right;margin-right:14px;
				font-size:14px;padding-top:10px;padding-bottom:10px; margin-top:19px;"
						>　确定　</a>
		</div>
		
		<div id="checkCardTypeDiv2" style="z-index:999">
		        <br/>
				<div class="text1" style="margin-top:16px;">
					<span style="float: left;	margin-left:10px;width:50px; font-size:16px;padding-top:3px;">证件号</span><span style="float: left;margin-left:30px;">
					  <!-- <label id="newCardText" style="float:left"></label>-->
					  <input id="idNo3" class="card" style="height:47px;border:0px;float:left; font-size:15px;" placeholder="请输入预留身份证号"  type="text" 
					  onkeyup="this.value=this.value.replace(/\W/g,'')" onkeydown="this.value=this.value.replace(/\W/g,'')" maxlength="30"></input>
					</span>
				</div>
				<div class="text1" style="margin-top:16px;">
					<span style="float: left;	margin-left:10px;width:50px; font-size:16px;padding-top:3px;">开户名</span><span style="float: left;margin-left:30px;">
					  <input id="userName" class="card" style="height:47px;border:0px;float:left; font-size:15px;" placeholder="请输入预留开户名"  type="text" 
					   maxlength="10"></input>
					</span>
				</div>
				<a href="javascript:" id="backOk2" class="btn btn-info btn-lg" style="background-color:#409eff;margin-top:11px;opacity:1;z-index:10000;float:right;margin-right:14px;
				font-size:14px;padding-top:10px;padding-bottom:10px; margin-top:19px;"
						>　确定　</a>
		</div>
		
		<!-- 黑色阴影部分  -->
		<div class="mask"></div>
	
	<script type="text/javascript">
	    var updateFlag = 0;
	    var cardId = "";
		$(document).ready(function() {
		
		<#-- $("#checkCardTypeDiv").show();
		     $(".mask").show();
		 -->
		$(".mask").hide();
		$("#textIdNo").hide();
		$("#textIdNo2").hide();
		$("#textUserName").hide();
		ap.alert({title: '公告',content: '暂不支持 中国工商银行、商业银行等 受限银行业务， 请选择支持卡支付！',buttonText: '我知道了'}, function(){});
		
		var uFlag = "${uFlag}";
		if ( uFlag=="2" ) {
			$("#cardNo").val("${cardNo}");
			$("#phone").val("${phone}");
			$("#textIdNo2").show();
			$("#textUserName").show();
			updateFlag = 1;
			cardId = "${cardId}";
		}
		
				    
         <!-- 循环遍历银行卡信息-->
			var whiteCardInfo = ${whiteList};
			if(whiteCardInfo.length !=0){
			for (var i = 0;i< whiteCardInfo.length; i++) {
			    
				var bankName = whiteCardInfo[i].name;
				   
			     document.getElementById('white').innerHTML +=
			     '<div  style="height: 40px; border-bottom: 0.5px solid #E8E8E8; line-height: 40px; margin-left: 0px;">'
			     +'<span style="float: left;  letter-spacing:1px;">'
			     +'　'+bankName
			     +'</span>'
			     +'<span class="yStyle" style="float: right; padding-right: 8%;font-weight:border;font-size:17px;color:#409eff;color:#409eff">'
			     +'✔'
			     +'</span>'
			      +'<span class="yStyle" style="float: right; padding-right: 15%;font-size:13px;color:#808080;">'
			     +'3W'
			     +'</span>'
			      +'<span class="yStyle" style="float: right; padding-right: 15%;font-size:13px;color:#808080;">'
			     +'50-3000'
			     +'</span>'
		         +'</div>'
			 }
			 }
			 
			 
			 var blackCardInfo = ${blackList};
			if(blackCardInfo.length !=0){
			for (var i = 0;i< blackCardInfo.length; i++) {
			    
				var bankName = blackCardInfo[i].name;
				   
			     document.getElementById('black').innerHTML +=
			     '<div  style="height: 40px; border-bottom: 0.5px solid #E8E8E8; line-height: 40px; margin-left: 0px;">'
			     +'<span style="float: left;  letter-spacing:1px;">'
			     +'　'+bankName
			     +'</span>'
			     +'<span class="yStyle" style="float: right; padding-right: 8%;font-weight:border;font-size:22px;color:#409eff;color:#f65f5f">'
			     +'×'
			     +'</span>'
		         +'</div>'
			 }
			 }
			 
			 $("#showBank").on("click",function(){
			 	$("#SupportCard").show();
			 });
			 
			  $("#backBank").on("click",function(){
			 	$("#SupportCard").hide();
			 });
			 
			  $("#backOk").on("click",function(){
			  var idNo = $("#idNo").val();
			     if ( idNo == null || idNo == "" ){
			        layer.open({content: "身份证不能为空，请输入完整！", skin: 'msg', time: 3});
			        return;
			     }
			 	 allFlag = "1";
			  	$(".mask").hide();
			 	$("#checkCardTypeDiv").hide();
			 	var idNo1 = $("#idNo").val();
			 	$("#idNo2").val(idNo1);
			 	$("#textIdNo").show();
			 });
			 
			  $("#backOk2").on("click",function(){
			  	var idNo = $("#idNo3").val();
			  	var userName = $("#userName").val();
			     if ( idNo == null || idNo == "" ){
			        layer.open({content: "身份证不能为空，请输入完整！", skin: 'msg', time: 3});
			        return;
			     }
			     
			     if ( userName == null || userName == "" ){
			        layer.open({content: "开户名不能为空，请输入完整！", skin: 'msg', time: 3});
			        return;
			     }
			     
			 	allFlag = "1";
			  	$(".mask").hide();
			 	$("#checkCardTypeDiv2").hide();
			 	var idNo3 = $("#idNo3").val();
			 	$("#idNo4").val(idNo3);
			 	$("#userName4").val(userName);
			 	$("#textIdNo2").show();
			 	$("#textUserName").show();
			 });
			 
			
			 
			 $("#idNo2").on("click",function(){
			 	 $(".mask").show();
			 	$("#checkCardTypeDiv").show();
			 });
			 
			 $("#idNo4").on("click",function(){
			 	 $(".mask").show();
			 	$("#checkCardTypeDiv2").show();
			 });
			 
			 $("#textUserName").on("click",function(){
			 	 $(".mask").show();
			 	$("#checkCardTypeDiv2").show();
			 })
			 
		});
		
		<!-- 公共参数 -->
		var allFlag = "0";
		function bindCard() {
		        if (allFlag == "0" || allFlag == 0 ) {
					checkCardType();
					return;
				}
				var index = layer.open({type: 2 ,content: '处理中…',shadeClose: false});
					var idNo = "";
				    if ( $("#idNo2").val() == null || $("#idNo2").val() == "") {
				        idNo = $("#idNo4").val();
				    }else{
				        idNo = $("#idNo2").val();
				    }
		            var userName = $("#userName4").val();
		            var cardNo = $("#cardNo").val();
		            var phone = $("#phone").val();
					var url = "${request.contextPath}/hyh5api/bindCard/${no}";
					$.ajax({
					   url:url,
					   type:'post',
					   data:{
					       "idNo":idNo,
					       "cardNo":cardNo,
					       "phone":phone,
					       "merchNo":${merchNo},
					       "payType":"1",
					       "userName":userName,
					       "updateFlag":updateFlag,
					       "cardId":cardId
					   },
					   dataType : 'json',
					   success : function(data){
							layer.close(index);
							if(data !=null){
							  if (data.code == '5200AAA') {
							    	 layer.open({content:data.message,skin: 'msg' ,time: 5 });
							    	 $("#cardNo").val("");
							    	 $("#idNo").val("");
							    	 $("#phone").val("");
							    	 $("#idNo2").val("");
							  }else if (data.code == '520000'){
									layer.open({content: "保存成功！" ,skin: 'msg' ,time: 1 });
									window.history.back(-3); 
									//ap.popTo(-2);
							  }else{
									layer.open({content: "保存失败！" ,skin: 'msg' ,time: 2 });
						      }
						}
						}
					});
		}
		
		function checkCardType() {
					if ( $("#cardNo").val() == null || $("#cardNo").val() == ""
						 || $("#phone").val() == null || $("#phone").val() == ""){
						layer.open({content: "请输入完整！", skin: 'msg', time: 3});
						return;
					}
					var index = layer.open({type: 2 ,content: '处理中…',shadeClose: false});
		            var cardNo = $("#cardNo").val();
					var url = "${request.contextPath}/hyh5api/checkCardType/${no}";
					$.ajax({
					   url:url,
					   type:'post',
					   data:{
					       "cardNo":cardNo,
					   },
					   dataType : 'json',
					   success : function(data){
					   layer.close(index);
							if(data !=null){
							  if(data.checkFlag!=null){
							  	 if( data.checkFlag == "1" || data.checkFlag == 1){
							  	   $(".mask").show();
							  	   $("#checkCardTypeDiv").show();
							  	 } else if ( data.checkFlag == "2" || data.checkFlag == 2 ){
							  	  	$(".mask").show();
							  	   $("#checkCardTypeDiv2").show();
							  	 } else {
							  	  allFlag = "1";
							  	  bindCard();
							  	 }
							  	 allFlag = "1";
							  }
							}
						}
					});
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