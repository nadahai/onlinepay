<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<title>onlinepay order</title>
<style type="text/css">
.a_hover:hover{color:#CF0000; text-decoration:underline;font-weight:bold;}
</style>
<head>
    <title>onlinepay</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

	<link  type="text/css" href="static/bootstrap/css/yeepaytest.css"  rel="stylesheet" />
	<script type="text/javascript" src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
</head>

<body>
	<table width="98%" style="border: solid 1px #107929" align="center" cellpadding="5" cellspacing="1">
		<tbody>
			<tr>
				<td height="15" colspan="3" bgcolor="#6BBE18">
				<span style="color: #FFFFFF">代付接口</span>
				</td>
			</tr>
			<tr>
			 <td height="15"  align="left" bgcolor="#FFFFEF">
				 <form method="post" action="${domainName}/${actualName}/xpayapi" targe="_blank">
				<table>
					 <tr>
						<td>接口名称:</td>
						<td><input type="text" name="reqCmd" value="req.transfer.order"/></td>
					</tr>
					<tr>
						<td>商户号:</td>
						<td><input type="text" name="merchNo" value="999941000001"/></td>
					</tr>
					<tr>
						<td>编码格式:</td>
						<td><input type="text" name="charset" value="utf-8"/></td>
					</tr>
					<tr>
						<td>签名算法类型:</td>
						<td><input type="text" name="signType" value="MD5"/></td>
					</tr>
					<tr>
						<td>公网ip:</td>
						<td><input type="text" name="reqIp" value="127.0.0.1"/></td>
					</tr>

					<tr>
						<td>商户订单号:</td>
						<td><input type="text" id="tradeNo" name="tradeNo" value="20170320010101001"/></td>
					</tr>
					<tr>
						<td>金额(元):</td>
						<td><input type="text" name="amount" value="16.88"/></td>
					</tr>
					 <tr>
						<td>身份证号:</td>
						<td><input type="text" name="idCardNo" value="111111111111111111"/></td>
					</tr>
					<tr>
						<td>账户名称:</td>
						<td><input type="text" name="accountName" value="孙中山"/></td>
					</tr>
					 <tr>
						<td>银行卡号:</td>
						<td><input type="text" name="bankCard" value="621111111111111111"/></td>
					</tr>
					 <tr>
						<td>银行名称:</td>
						<td><input type="text" name="bankName" value="中国银行"/></td>
					</tr>
					<tr>
						<td>开户行名称:</td>
						<td><input type="text" name="bankSubName" value="中国银行三环支行"/></td>
					</tr>
					<tr>
						<td>省份名称</td>
						<td><input type="text" name="province" value="北京市"/></td>
					</tr>
					<tr>
						<td>城市名称:</td>
						<td><input type="text" name="city" value="北京市"/></td>
					</tr>
					<tr>
						<td>联行号:</td>
						<td><input type="text" name="bankLinked" value="425584018689"/></td>
					</tr>
					<tr>
						<td>收款人手机号码:</td>
						<td><input type="text" name="mobile" value="12111111111"/></td>
					</tr>
					<tr>
						<td>透传信息:</td>
						<td><input type="text" name="remark" value="孙中山拨款"/></td>
					</tr>
					<tr>
						<td>签名:</td>
						<td><input type="text" name="sign" value="84284882764941bdeb8601b5c4828cff"/></td>
					</tr>
					<tr>
					<tr>
						<td align="center" colspan="2">
							<input type="submit" align="center" value="提交代付订单"/>
						</td>
					</tr>
				</table>
				</form>
			</td>
		</tr>
		</tbody>
	</table>
	<script type="text/javascript">
		$(function() {
			var timestamp = (new Date()).getTime();
			debugger
			$("#tradeNo").val(timestamp);
		});
	</script>
</body>
</html>