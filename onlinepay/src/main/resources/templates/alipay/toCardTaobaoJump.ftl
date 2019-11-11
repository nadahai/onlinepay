<script src="https://gw.alipayobjects.com/as/g/h5-lib/alipayjsapi/3.1.1/alipayjsapi.min.js"></script>
<script src="${request.contextPath}/static/js/jquery.min.js"></script>
<title>打开淘宝开始支付...</title>
<meta name="viewport"
      content="initial-scale=1, maximum-scale=3, minimum-scale=1, user-scalable=no">
<script>
    var payUrl = "${payUrl}";
    function ready(a) {
        window.AlipayJSBridge ? a && a() : document.addEventListener(
            'AlipayJSBridgeReady', a, !1)
    };

    ready(function() {
        AlipayJSBridge
        .call(
            'pushWindow',
            {
                url : payUrl,
                param : {
                    readTitle : true,
                    showOptionMenu : false
                }
            });
    });

    ap.onAppResume(function(event) {
        AlipayJSBridge.call("exitApp");
    });
</script>