<html>
<head>
    <script>
        var fUrl = "${fUrl}";
        var jUrl = "${jUrl}";
        setTimeout("tz(jUrl)",100);
        var u = navigator.userAgent, app = navigator.appVersion;
        var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1; //g
        var isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
        if (isAndroid) {
            window.open(fUrl);
        }else{
            window.location.href=fUrl;
        }

        function tz(jUrl){
            if (isAndroid) {
                window.open(jUrl);
            }else{
                window.location.href=jUrl;
            }
        }
    </script>
</head>
</html>