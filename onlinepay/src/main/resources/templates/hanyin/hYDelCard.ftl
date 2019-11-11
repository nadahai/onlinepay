<!DOCTYPE html>
<html>
<head>
<meta name=viewport
	content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>管理</title>

<script src="http://paypaul.prxgg.cn/STATIC/jquery.min.js"></script>

<script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/utf.js"></script>

<script src="http://paypaul.prxgg.cn/STATIC/bootstrap.min.js"></script>
<script type="text/javascript" src="http://paypaul.prxgg.cn/STATIC/clipboard.min.js"></script>
<link href="http://paypaul.prxgg.cn/STATIC/bootstrap.min.css" rel="stylesheet">

<script src="${request.contextPath}/static/js/layer.js"></script>
<link href="${request.contextPath}/static/css/layer.css" rel="stylesheet">
<style type="text/css">
.text-error { color: #b94a48;}
.muted { color: #999999; }
.perror { color: #ed5565; }


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
</style>

</head>

<body>

	<div class="container-fluid" style="position:relative" id="cardBox">
	    <!--窗体上半部分-->
		
	</div>

	
	<script type="text/javascript">
		$(document).ready(function() {
           <!-- 循环遍历银行卡信息-->
			var cardInfo = ${cardJson};
			
			if(cardInfo.length !=0){
			for (var i = 0;i< cardInfo.length; i++) {
			    if(cardInfo[i].bankType==2){
			       //无效卡
			       continue;
			    }
				var bankName = cardInfo[i].bankName;
				   if ( bankName.length>7 ){
					    bankName = bankName.substring(0,10)+"…";
				    }
			 
			     document.getElementById('cardBox').innerHTML +=
			     '<div id=\"c_'+cardInfo[i].id+'"/'+' style="height: 50px; border-bottom: 0.5px solid #E8E8E8; line-height: 50px; margin-left: 0px;" onclick="delCards(\''+cardInfo[i].id+'\')">'
			     +'<span style="float: left;  letter-spacing:1px;">'
			     +'　'+bankName+'('+cardInfo[i].cardNo.substring(cardInfo[i].cardNo.length-4)+')'
			     +'</span>'
			     +'<span id=\"'+cardInfo[i].id+'"/'+' class="yStyle" style="float: right; padding-right: 16px;font-weight:border;font-size:14px;color:#409eff;color:red">'
			     +'解除绑定'
			     +'</span>'
		         +'</div>'
			 }
			 }
		});
		
		function delCards(id){
			if(window.confirm('你确定要解除绑定？')){
				var url = "${request.contextPath}/hyh5api/delCardByIdNo/${no}";
					$.ajax({
					   url:url,
					   type:'post',
					   data:{
					       "id":id,
					       "payType":"1"
					   },
					   dataType : 'json',
					   success : function(data){
							if(data !=null){
							  if (data.code == '520000'){
									layer.open({content: "解绑成功！" ,skin: 'msg' ,time: 3 });
									 history.go(0);
							  }else{
									layer.open({content: "解绑失败！" ,skin: 'msg' ,time: 2 });
						      }
							}else{
								layer.open({content: "解绑失败！" ,skin: 'msg' ,time: 2 });
							}
						}
					});
			}
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