<!DOCTYPE HTML>
<html>
<head>
    <title>支付跳转中....</title>
    <meta http-equiv="language" content="java">
    <meta http-equiv="pageEncoding" content="${charset}">
    <meta http-equiv="reffer" content="${reffer}">
    <meta name="referrer" content="no-referrer">
    <meta http-equiv="Content-Type" content="text/html; charset=${charset}">
    <script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
</head>
<script type="text/javascript">
    $(function() {
        var actionUrl = '${buttonUrl}';
        var anchor = $("<a href='"+actionUrl+"'><button id='button'>自动提交。。。</button></a>");
        $(document.body).append(anchor);
        document.getElementById('button').click();
    });
</script>
<body>

</body>
</html>
