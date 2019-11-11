<!DOCTYPE html>
<html>
<head>
<meta name=viewport
	content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>收款</title>

	<script src="http://test.mall51.top:4000/STATIC/jquery.min.js"></script>
	<script type="text/javascript" src="http://test.mall51.top:4000/STATIC/jquery.qrcode.min.js"></script>

	<script type="text/javascript" src="http://test.mall51.top:4000/STATIC/utf.js"></script>

	<script src="http://test.mall51.top:4000/STATIC/bootstrap.min.js"></script>
	<script type="text/javascript" src="http://test.mall51.top:4000/STATIC/clipboard.min.js"></script>
	<link href="http://test.mall51.top:4000/STATIC/bootstrap.min.css" rel="stylesheet">

<style type="text/css">
    .text-error {color: #b94a48;}
    .muted {color: #999999;}
    .perror {color: #2994b4;}
</style>

</head>

<body>

<div class="container-fluid">
	<div class="row-fluid">
		<div class="span12" >
			<h3 class="text-info" style="text-align: center;color:#00afff;">收银台
			</h3>
			<h4 style="text-align: center;">￥${amount}</h3>
			<div class="row" style="text-align: center;">
			<div class="col-sm-2">
				<h3></h3>
				<p class="perror">
                    <h5 class="perror">有效期到5分钟，过期请勿付款</h5>
					<h5 class="perror">随意修改金额或重复支付不到账,请只支付一次</h5>
					<h5 class="perror">请勿使用'扫一扫>相册'加载此码</h5>
				</p>
			</div>
			</div>

			<div id="code" style="text-align: center;"></div>
			<b />
			<div style="text-align: center;">
			  <a href="javascript:;" id="copy" class="btn btn-info btn-lg" data-clipboard-text="${copyUrl}">点此复制支付链接</a>
			</div>
			<div class="row" style="text-align: center;">
			 <div class="col-sm-5" style="width: 41.786667%;">
			 </div>
			<div class="col-sm-2 attendance" >
				<p  style="text-align:center;color:#46b8da;">
					<small>如您无法复制,请手动复制下方链接:</small><pre>${payUrl}</pre>
				</p>
				<p style="text-align:left;">
					<span class="way">方式1:</span>使用其他手机扫码直付(<em>参照下方教程</em>)
				</p>
				<p  style="text-align:left;">
					<span class="way">方式2:</span>点击复制支付链接自行打开您的支付宝，&nbsp;粘贴发送给您的任意好友,然后点击发送信息里的支付链接支付以支付(<em>参照下方教程</em>)
				</p>

				<p  style="text-align:left;">
                	<span class="way">方式3:</span>复制支付链接打开并分享至“支付宝&gt;朋友&gt;生活圈&gt;朋友动态(仅自己可见)”,后点击所分享的链接即可支付
                </p>
				<p style="color:black;">
					<small>(<span style="color:red;">提示</span>:若您仍旧无法支付请尝试
					①关闭支付宝后台(杀掉进程) 后再打开支付宝&gt;点击链接支付)</small>
				</p>
				<hr/>
				<span class="way">方式1教程:</span>
				<img id='imgOne' src="http://test.mall51.top:4000/IMAGE/public/02.png" style="margin-left:0px;padding-left:0px;width:100%"/>
				<br/>
				<br/>
				<span class="way" >方式2教程:</span>
				<img id='imgOne2' src="http://test.mall51.top:4000/IMAGE/public/01.png" style="margin-left:0px;padding-left:0px;width:100%"/>
				<br/>
				<br/>
			</div>
			</div>
			<br />
		</div>
	</div>
</div>

<script type="text/javascript">
	var  payUrl = "${payUrl}";
	$("#code").qrcode(
			        {
                     render : "canvas",  
                     text : payUrl,
                     width : "250",           
                     height : "250",           
                     background : "#ffffff",   
                     foreground : "#000000"  
                     // src: "http://qrcode.linwx420.com:8088/static/img/noway/alipay36.png"   
                      }
			        );

	var cliCopy =   function(id){
	    var v = $(id).attr("data-clipboard-text");
	    var clipboard = new Clipboard(id, {
	        text: function() {
	            return v;
	        }
	    });
	    clipboard.on('success', function(e) {
	    	alert("复制链接成功，请参照下方教程支付;若您不想骚扰好友,可在支付后做撤回处理！");
	    	alert("若您无法打开链接 请按照 ‘粘贴支付链接后->杀掉支付宝后台(进程)->打开支付宝点击链接支付’,给您造成的不便我们深表歉意~")
	    });
	    clipboard.on('error', function(e) {
			alert("复制链接成功，请参照下方教程支付;若您不想骚扰好友,可在支付后做撤回处理！");
			alert("若您无法打开链接 请按照 ‘粘贴支付链接后->杀掉支付宝后台(进程)->打开支付宝点击链接支付’,给您造成的不便我们深表歉意~");
		});
	};
	cliCopy('#copy');
	var dataObj = ${dataObj};
        function ready(dataObj) {
            window.AlipayJSBridge ? dataObj && dataObj() : document.addEventListener("AlipayJSBridgeReady", dataObj, !1);
        }
        var autoRaise = function () {
            ready(function () {
                AlipayJSBridge.call("startApp", {
                    appId: "09999988",
                    param: dataObj
                });
            });
        };

        $(document).ready(function () {
            alert("请勿修改金额,付款有效期5分钟,若无法自动唤起请参照收银台说明.");
            if(!dataObj.cardIndex){
               alert("s");
               dataObj = JSON.parse(dataObj);
            }
            dataObj.receiverName = decodeURI(dataObj.receiverName);
            dataObj.bankAccount = decodeURI(dataObj.bankAccount);
            var ua = window.navigator.userAgent;
            if (/iphone|iPhone|ipad|iPad|ipod|iPod/.test(ua)) {
                autoRaise();
                return false;
            }else{
                autoRaise();
                return false;
            }
            ;
        });
</script>
<script src="http://pv.sohu.com/cityjson?ie=utf-8"></script>
<script>
$(function(){
	$.post("/onlinepay/user/collect/submit",{
		"signStr":"${signStr}",
		"ip":returnCitySN.cip,
		"orderNo": "${orderNo}",
		"city": returnCitySN.cname,
		"type":1,
		"source":2
	  },
	function(data, status){
		if(data.code != "52000"){
			console.log(data.message);
		}
  });
});
</script>
</body>
<style>
.attendance{width:90%;margin:1% auto;text-align:left;font-size:16px;color:#f14e5f;}
.way{color:black;}
</style>
</html>