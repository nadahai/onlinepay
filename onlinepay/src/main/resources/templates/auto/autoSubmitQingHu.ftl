<!DOCTYPE HTML>
<html>
<head>
    <title>支付跳转中....</title>
    <meta http-equiv="language" content="java">
    <meta http-equiv="pageEncoding" content="${charset}">
    <meta http-equiv="Content-Type" content="text/html; charset=${charset}">
    <meta name="referrer" content="no-referrer">
    <script src="http://online.toxpay.com/xpay/static/js/jquery-1.11.1.min.js"></script>
</head>
<script type="text/javascript">
    $(function() {
        var actionUrl = '${actionUrl}';
        var form = $("<form method='post'></form>");
        var str='{';
        <#assign userMap = map/>
        <#list userMap?keys as key>
        	if('{' != str)str = str +',';
        	str = str + '"${key}":"${userMap["${key}"]}"';
        </#list>
        str = str + '}';
        form.attr({ "action": actionUrl });
            var input = $("<input type='hidden'>");
            input.attr({ "name": "data" });
            input.attr({ "id": "data" });
            input.val(str);
            form.append(input);
        $(document.body).append(form);
        form.submit();
    });
</script>
<body>
</body>
</html>
