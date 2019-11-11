<html>
<title>跳转到支付宝</title>
<head>
    <script>
        var fUrl = "${fUrl}";
        var jUrl = "${jUrl}";
        AlipayJSBridge.call("pushWindow", {url: fUrl});
        setTimeout("tz()",1000);
        function tz(){ window.location.href = jUrl;}
    </script>
</head>
</html>