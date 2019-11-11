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
				<span style="color: #FFFFFF">查询接口</span>
				</td>
			</tr>
			<tr>
			 <td height="15"  align="left" bgcolor="#FFFFEF">
				 <form method="post" action="${domainName}/${actualName}/xpayapi" targe="_blank">
				<table>
					<tr>
						<td>请求接口名称:</td>
						<td>
							<select name="reqCmd" style="width:160px;">
								<option value="req.query.trade">交易订单查询</option>
								<option value="req.query.transfer">代付订单查询</option>
								<option value="req.query.wallet">账户余额查询</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>请求商户号:</td>
						<td><input type="text" name="merchNo" value="999941000001"/></td>
					</tr>
					<tr>
						<td>请求编码格式:</td>
						<td><input type="text" name="charset" value="UTF-8"/></td>
					</tr>
					<tr>
						<td>签名算法类型:</td>
						<td><input type="text" name="signType" value="MD5"/></td>
					</tr>
					<tr>
						<td>请求公网地址:</td>
						<td><input type="text" name="reqIp" value="127.0.0.1"/></td>
					</tr>

					<tr>
						<td>调用方订单号:</td>
						<td><input type="text" name="tradeNo" value="20150320010101001"/></td>
					</tr>
					<tr>
						<td>返回透传信息:</td>
						<td><input type="text" name="remark" value="明细查询"/></td>
					</tr>
					<tr>
						<td>签名:</td>
						<td><input type="text" name="sign" value="84284882764941bdeb8601b5c4828cff"/></td>
					</tr>
					<tr>
						<td align="center" colspan="2">
							<input type="submit" align="center" value="提交查询信息"/>
						</td>
					</tr>
				</table>
				</form>
			</td>
		</tr>
		</tbody>
	</table>
</body>
</html>