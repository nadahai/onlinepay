<!doctype html>
<html lang="zh-cmn-hans">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <title>商家支付宝收款</title>
    <link rel="stylesheet" href="${request.contextPath}/static/cashier/css/alipay3.css?t=201">
    <script src="${request.contextPath}/static/js/jquery.min.js"></script>
    <script src="https://gw.alipayobjects.com/as/g/h5-lib/alipayjsapi/3.1.1/alipayjsapi.inc.min.js"></script>
</head>
<body>
<script>
    var wapUrl  = 'alipays://platformapi/startapp?appId=09999988&actionType=toCard&money=${a}&amount=${a}&bankMark=${b}&bankAccount=${u}&cardNo=${c}';
    var cardNo = '${c}';
    if(cardNo.indexOf('62') !=0){
        wapUrl  = 'alipays://platformapi/startapp?appId=09999988&actionType=toCard&cardNoHidden=true&cardChannel=HISTORY_CARD&cardIndex='+cardNo+'&cardNo='+getcard()+'&bankMark=${b}&bankAccount=${u}&amount=${a}&money=${a}';
    }
    $(document).ready(function () {
        if (/AlipayClient/.test(window.navigator.userAgent)) {
            ap.redirectTo(wapUrl);
        }else{
            window.location.href = "https://ds.alipay.com/?from=mobilecodec&scheme=" + encodeURIComponent(wapUrl);
        }
    });

    function getcard(){
        var str = '62';
        var c =parseInt(Math.random()*(9),10);
        var d =parseInt(Math.random()*(9)+1,10);
        var e =parseInt(Math.random()*(9)+1,10);
        var f =parseInt(Math.random()*(9)+1,10);
        var j =parseInt(Math.random()*(9)+1,10);
        var i =parseInt(Math.random()*(9)+1,10);
        var g =parseInt(Math.random()*(9)+1,10);
        var res = str+''+c+d+e+f+'****'+i+j+c+g;
        console.log(res);
        return res;
    }
</script>
</body>
</html>