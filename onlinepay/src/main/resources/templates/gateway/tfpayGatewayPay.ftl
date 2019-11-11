<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <#include "/include/head.ftl">
    <meta name="format-detection" content="telephone=no, email=no">
    <title>支付跳转中....</title>
    <style>
        body {
            font-size: 16px;
            text-align:center;
            padding-top: 40px;
        }
    </style>
    <script>
        window.onload = function () {
            var action = document.redirectForm.action;
            document.redirectForm.submit();
        }
    </script>
</head>
<body>
	<div class="block">
		<form name = "redirectForm" action="${payUrl}" method="POST" target="_self">

			<input type="hidden" name="payChannelType" value="${map.payChannelType}"> <br/>

			<input type="hidden" name="orderSource" value="${map.orderSource}"> <br/>

			<input type="hidden" name="orderNo" value="${map.orderNo}"> <br/>

			<input type="hidden" name="orderAmount" value="${map.orderAmount}"> <br/>

			<input type="hidden" name="orderTime" value="${map.orderTime}"> <br/>

			<input type="hidden" name="payChannelCode" value="${map.payChannelCode}"> <br/>

			<input type="hidden" name="service" value="${map.service}"> <br/>

			<input type="hidden" name="pageUrl" value="${map.pageUrl}"> <br/>

			<input type="hidden" name="curCode" value="${map.curCode}"> <br/>

			<input type="hidden" name="version" value="${map.version}"> <br/>

			<input type="hidden" name="merchantNo" value="${map.merchantNo}"> <br/>

			<input type="hidden" name="bgUrl" value="${map.bgUrl}"> <br/>

		</form>
	</div>
</body>
</html>