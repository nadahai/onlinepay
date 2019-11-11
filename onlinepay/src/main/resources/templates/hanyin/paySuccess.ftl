<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
  <meta name="description" content="" />
  <meta name="keywords" content="" />
  <meta name="apple-mobile-web-app-capable" content="yes" />
  <meta name="apple-mobile-web-app-status-bar-style" content="black" />
  <meta name="format-detection" content="telephone=no, email=no" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0" />
<title>受理成功</title>
<script>
    var docEl = document.documentElement;
    docEl.style.fontSize = 100 / 375 * docEl.clientWidth  + 'px';
    window.addEventListener('resize', function() {
      docEl.style.fontSize = 100 / 375 * docEl.clientWidth + 'px';
    });
 </script>
<script src="http://paypaul.prxgg.cn/STATIC/jquery.min.js"></script>

<script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/utf.js"></script>

<script src="http://paypaul.prxgg.cn/STATIC/bootstrap.min.js"></script>
<script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/clipboard.min.js"></script>
<link href="http://paypaul.prxgg.cn/STATIC/bootstrap.min.css" rel="stylesheet">
<link href="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/dpl/widget/tips.css" rel="stylesheet">




<!-- 主文件 -->
<link rel="stylesheet" href="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/dpl/antui.css"/>

<!-- 组件 -->
<link rel="stylesheet" href="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/??dpl/widget/message.css,dpl/icon/message.css,dpl/widget/search.css"/>
<link  rel="stylesheet" href="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/dpl/widget/footer.css"/>
<link  rel="stylesheet" href="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/dpl/widget/process.css"/>
<link  rel="stylesheet" href="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/dpl/widget/loading.css"/>
<link  rel="stylesheet" href="https://gw.alipayobjects.com/as/g/antui/antui/10.1.32/dpl/widget/toast.css"/>






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

#queryStatus1Table {
			height:100%;
			width:100%;
			background-color:#FFFFFF;
			position:fixed;
			bottom:0px;
			z-index:888;
			display:none;
}

#waitPay{
			position:fixed;
			top:6%;
			z-index:999;
			display:none;
			margin-left:8%;
}

#successPay{
			position:fixed;
			top:6%;
			z-index:999;
			display:none;
			margin-left:8%;
}

#faieldPay{
			position:fixed;
			top:6%;
			z-index:999;
			display:none;
			margin-left:8%;
}

#loading{
			z-index:999;
			display:none;
			position:fixed;
			top:26%;
			left:44%;
}

#backPage {
       		position:fixed;
			top:60%;
			z-index:3000;
}

#refreshData {
			position:fixed;
			top:70%;
			
}
.admonish {
            color:#A0A0A0;
			position:fixed;
			top:50%;
		
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
		<div class="am-message result" id = "payPay" role="alert" style="display:show">
		  <i class="am-icon result pay" aria-hidden="true"></i>
		  <div class="am-message-main">支付成功</div>
		  </br>
		  <div class="am-message-sub" style="font-size:30px;color:#707070;">￥${amount}</div>
		 
		  </br>
		  <div class="am-message-sub">${dataResult}</div>
		   <div class="am-message-sub">1～2分钟后到账，结果以实际扣款为准</div>
		</div>
		
	<div class="am-message result" id = "payWait" style="display:none">
		<i class="am-icon result wait"></i>
		<div class="am-message-main">等待结果</div>
		 </br>
		  <div class="am-message-sub" style="font-size:25px;color:#707070;">￥${amount}</div>
		 
		  </br>
		  <div class="am-message-sub">${dataResult}</div>
		  <div class="am-message-sub">已提交申请，等待银行处理</div>
	</div>
	
	<div class="am-button-wrap">
			<button class="btn btn-info btn-lg btn J_demo am-button blue">完成</button>
			<button class="am-button white" id = "queryStatus1" >查询进度</button>
		</div>
	</div>
	
	<div id="tips" class="am-tips">
	    <div class="am-tips-wrap">
	        <div class="am-tips-content">
	                                  如遇到问题可一键反馈
	        </div>
	    </div>
	</div>
	
	<footer class="am-footer am-fixed am-fixed-bottom" style="z-index:20000">
	    <div class="am-footer-interlink am-footer-top">
	        <a class="am-footer-link" href="/onlinepay/suggest/index?orderNo=${orderNo}">一键反馈</a>
	    </div>
	    <div class="am-footer-copyright"> 2004-2019 Alipay.com. All rights reserved.</div>
	</footer>


<div id="queryStatus1Table">

		<div id="loading" class="am-loading page">
		  <i class="am-icon loading" aria-hidden="true"></i>
		  <div class="am-loading-text">加载中...</div>
		</div>
		<!--处理中-->
	  <div id="waitPay" class="statusDiv">
				<div class="am-process">
				  <div class="am-process-item pay">
					<i class="am-icon process success" aria-hidden="true"></i>
					<div class="am-process-content">
					  <div class="am-process-main">提交中</div>
					  <div class="am-process-brief"> </div>
					</div>
					<div class="am-process-down-border"></div>
				  </div>
				  <div class="am-process-item unpay">
					<i class="am-icon process unpay" aria-hidden="true"></i>
					<div class="am-process-content">
					  <div class="am-process-main">受理成功</div>
					  <div class="am-process-brief"> </div>
					</div>
					<div class="am-process-up-border"></div>
					<div class="am-process-down-border"></div>
				  </div>
				  <div class="am-process-item unpay">
					<i class="am-icon process unpay" aria-hidden="true"></i>
					<div class="am-process-content">
					  <div class="am-process-main">支付成功</div>
					  <div class="am-process-brief"> </div>
					</div>
					<div class="am-process-up-border"></div>
				  </div>
					</div>
   </div>
	  <!--支付成功-->
	  <div id="successPay" class="statusDiv">
				<div class="am-process">
				  <div class="am-process-item pay">
					<i class="am-icon process pay" aria-hidden="true"></i>
					<div class="am-process-content">
					  <div class="am-process-main">提交成功</div>
					  <div class="am-process-brief"> </div>
					</div>
					<div class="am-process-down-border"></div>
				  </div>
				  <div class="am-process-item pay">
					<i class="am-icon process success" aria-hidden="true"></i>
					<div class="am-process-content">
					  <div class="am-process-main">受理成功</div>
					  <div class="am-process-brief"> </div>
					</div>
					<div class="am-process-up-border"></div>
					<div class="am-process-down-border"></div>
				  </div>
				  <div class="am-process-item success">
					<i class="am-icon process success" aria-hidden="true"></i>
					<div class="am-process-content">
					  <div class="am-process-main">支付成功</div>
					</div>
					<div class="am-process-up-border"></div>
				  </div>
				</div>
	  </div>
	  <!--支付失败-->
	  <div id="faieldPay" class="statusDiv">
			<div class="am-process">
			  <div class="am-process-item pay">
				<i class="am-icon process pay" aria-hidden="true"></i>
				<div class="am-process-content">
				  <div class="am-process-main">提交成功</div>
				  <div class="am-process-brief"> </div>
				</div>
				<div class="am-process-down-border"></div>
			  </div>
			  <div class="am-process-item pay">
				<i class="am-icon process success" aria-hidden="true"></i>
				<div class="am-process-content">
				  <div class="am-process-main">受理成功</div>
				  <div class="am-process-brief"> </div>
				</div>
				<div class="am-process-up-border"></div>
				<div class="am-process-down-border"></div>
			  </div>
			  <div class="am-process-item fail">
				<i class="am-icon process fail" aria-hidden="true"></i>
				<div class="am-process-content">
				  <div class="am-process-main">支付失败</div>
				</div>
				<div class="am-process-up-border"></div>
			  </div>
			</div>
	  
	  </div>
	  
	  <div class="admonish" style="width:80%;margin-left:10%">　　请耐心等候支付结果，点击刷新结果获取实时结果(请勿频繁操作)。</div>
	  <button class="am-button blue" id = "backPage" style="width:90%;margin-left:5%">返回</button>
	  <button class="am-button blue" id = "refreshData" style="width:90%;margin-left:5%">刷询结果</button>
	  
	  <div class="am-toast" id="querying" role="alert" aria-live="assertive" style="display:none">
		  <div class="am-toast-text">
			<div class="am-loading-indicator white">
			  <div class="am-loading-item"></div>
			  <div class="am-loading-item"></div>
			  <div class="am-loading-item"></div>
			</div>
			查询中...
		  </div>
	</div>
	
	<div class="am-toast" id="queryError" role="alert" aria-live="assertive" style="display:none">
		<div class="am-toast-text">
			<span class="am-icon toast network" aria-hidden="true"></span> 网络不给力
		</div>
	</div>
	
</div>
	   
	
	<script type="text/javascript">
		function ready(callback) {
		  var dataResult = '${dataResult}';
		  if ( dataResult.indexOf("受理") >= 0 || dataResult.indexOf("处理") >= 0 || dataResult.indexOf("等待") >= 0) {
		     $("#payPay").hide();
			 $("#payWait").show();
		  }
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
		
		var flag = 0;
		
		$("#queryStatus1").on('click',function(){
			$("#queryStatus1Table").show();
		   if ( flag == 0 ) { 
			   $(".admonish").hide();
			   $("#refreshData").hide();	   
			   $("#loading").show();
			   flag = flag + 1;
			   queryStauts();
		   }	   
		});
		
		
		$("#backPage").on('click',function(){
		   $("#queryStatus1Table").hide();
		});
		
		$("#refreshData").on('click',function(){
		   btnCheck("30");
		   $("#querying").show();
		   queryStauts();
		});
		
		
		function queryStauts() {
		    var url = "${request.contextPath}/hyh5api/selectOrderStatus/${orderNo}";
				$.ajax({
					url:url,
					type:'post',
					data:{},
					dataType : 'json',
					async:true,
						success : function(data){
						$("#querying").hide();
						$("#loading").hide();
						$("#refreshData").show();
						$(".admonish").show();
						if (data) {
						   if (data.bankNo) {
								$(".am-process-brief").text("银行卡号:"+data.bankNo);
						   } else {
								$(".am-process-brief").text("");
						   }

						   if ( data.hyStatus ) {
							  $(".statusDiv").hide();
							  if ( data.hyStatus == "6" ) {
								 $("#waitPay").show();
							  } else if ( data.hyStatus == "4" ) {
								$("#successPay").show();
							  } else if ( data.hyStatus == "5" ) {
								$("#faieldPay").show();
							  } else {
								$("#waitPay").show();
							  }							
						   } else {

						   }
						} else {

						} 	
					}
				});
        }
        function btnCheck(t) {
			  var timer = setInterval(function() {
				   if (t == 0) {
						clearInterval(timer);
						$("#refreshData").text('刷新进度');	
						$("#refreshData").attr('disabled',false);										
				   } else {			   
						$("#refreshData").text(t+"秒后重新刷新");
						$("#refreshData").attr('disabled',true);
						t--;
				   }
			  }, 1000);
		}
		
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