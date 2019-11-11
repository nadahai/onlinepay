<!DOCTYPE HTML>
<html>
<head>
    <title>支付跳转中....</title>
    <meta http-equiv="language" content="java">
    <meta http-equiv="pageEncoding" content="${charset}">
    <meta http-equiv="Content-Type" content="text/html; charset=${charset}">
    <!-- 
    <meta name="referrer" content="no-referrer">
    -->
    <script src="${request.contextPath}/static/js/jquery-1.11.1.min.js"></script>
</head>
<script type="text/javascript">
    $(function() {
        var actionUrl = '${actionUrl}';
        var form = $("<form method='post'></form>");
        form.attr({ "action": actionUrl });
        <#assign userMap = map/>
        <#list userMap?keys as key>
            var input = $("<input type='hidden'>");
            input.attr({ "name": "${key}" });
            input.attr({ "id": "${key}" });
            input.val('${userMap["${key}"]}');
            form.append(input);
        </#list>
        $(document.body).append(form);
        form.submit();
    });
</script>
<body>
</body>
</html>
