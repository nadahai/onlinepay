<html>
<head>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta name="referrer" content="origin">
    <title>gwPay</title>
	<#include "/include/head.ftl">
    <script src="${request.contextPath}/static/js/jquery-1.11.1.min.js"></script>
</head>
<script type="text/javascript">
	function autoSubmit() {
		document.getElementById("gwFrom").submit();
	}
</script>
<body onload="autoSubmit();">
	<form action="https://gateway.gopay.com.cn/Trans/WebClientAction.do" method="post" id="gwFrom" accept-charset="GBK" onsubmit="document.charset='GBK';" >
	    <input type="hidden" name="version"   value="${map.version}">
		<input type="hidden" name="charset"   value="${map.charset}">
		<input type="hidden" name="language"   value="${map.language}">
		<input type="hidden" name="signType"   value="${map.signType}">
		<input type="hidden" name="tranCode"   value="${map.tranCode}">
		<input type="hidden" name="merchantID"   value="${map.merchantID}">
		<input type="hidden" name="merOrderNum"   value="${map.merOrderNum}">
		<input type="hidden" name="tranAmt"   value="${map.tranAmt}">
		<input type="hidden" name="feeAmt"   value="${map.feeAmt}">
		<input type="hidden" name="currencyType"   value="${map.currencyType}">
		<input type="hidden" name="frontMerUrl"   value="${map.frontMerUrl}">
		<input type="hidden" name="backgroundMerUrl"   value="${map.backgroundMerUrl}">
		<input type="hidden" name="tranDateTime"   value="${map.tranDateTime}">
		<input type="hidden" name="virCardNoIn"   value="${map.virCardNoIn}">
		<input type="hidden" name="tranIP"   value="${map.tranIP}">
		<input type="hidden" name="isRepeatSubmit"   value="${map.isRepeatSubmit}">
		<input type="hidden" name="goodsName"   value="${map.goodsName}">
		<input type="hidden" name="goodsDetail"   value="${map.goodsDetail}">
		<input type="hidden" name="buyerName"   value="${map.buyerName}">
		<input type="hidden" name="buyerContact"   value="${map.buyerContact}">
		<input type="hidden" name="merRemark1"   value="${map.merRemark1}">
		<input type="hidden" name="merRemark2"   value="${map.merRemark2}">
		<input type="hidden" name="bankCode"   value="${map.bankCode}">
		<input type="hidden" name="userType"   value="${map.userType}">
		<input type="hidden" name="VerficationCode"   value="${map.VerficationCode}">
		<input type="hidden" name="signValue"   value="${map.signValue}">
		<input type="hidden" name="gopayServerTime"   value="${map.gopayServerTime}">
	    <input type="submit" style="display: none;" value=""/><br/>
	</form>
</body>
</html>