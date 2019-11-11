<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>支付宝</title>
    <script>
        !function(e){function t(n){if(i[n])return i[n].exports;var r=i[n]={exports:{},id:n,loaded:!1};return e[n].call(r.exports,r,r.exports,t),r.loaded=!0,r.exports}var i={};return t.m=e,t.c=i,t.p="",t(0)}([function(e,t){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var i=window;t["default"]=i.vl=function(e,t){var n=e||100,r=t||750,a=i.document,d=navigator.userAgent,o=d.match(/Android[\S\s]+AppleWebkit\/(\d{3})/i),l=d.match(/U3\/((\d+|\.){5,})/i),s=l&&parseInt(l[1].split(".").join(""),10)>=80,u=a.documentElement,c=1;if(o&&o[1]>534||s){u.style.fontSize=n+"px";var p=a.createElement("div");p.setAttribute("style","width: 1rem;display:none"),u.appendChild(p);var m=i.getComputedStyle(p).width;if(u.removeChild(p),m!==u.style.fontSize){var v=parseInt(m,10);c=100/v}}var f=a.querySelector('meta[name="viewport"]');f||(f=a.createElement("meta"),f.setAttribute("name","viewport"),a.head.appendChild(f)),f.setAttribute("content","width=device-width,user-scalable=no,initial-scale=1,maximum-scale=1,minimum-scale=1");var h=function(){u.style.fontSize=n/r*u.clientWidth*c+"px"};h(),i.addEventListener("resize",h)},e.exports=t["default"]}]);vl(100, 750);
    </script>
    <script>
        window._to = {autoStart: true};
    </script>
    <script src="https://gw.alipayobjects.com/as/g/h5-lib/alipayjsapi/3.1.1/alipayjsapi.inc.min.js"></script>
    <link rel="stylesheet" href="https://gw.alipayobjects.com/os/s/prod/i/index-488c3.css">
</head>
<script type="text/javascript">
    var wapUrl='';
    if(isIOS()){
        wapUrl = 'alipays://platformapi/startapp?appId=20000123&actionType=scan&s=money&u=${u}&a=${a}&m=${m}';
    }else{
        wapUrl = 'alipays://platformapi/startapp?appId=20000123&actionType=scan&biz_data={"a": "${a}","u": "${u}","m": "${m}"}';
    }
    //var url = 'alipays://platformapi/startapp?appId=60000148&url=' + encodeURIComponent(wapUrl);
    function goPay() {
        ap.showLoading();
        ap.onPagePause(function(){
            ap.showLoading();
        });
        ap.onPageResume(function(res){
            ap.hideLoading();
        });
        ap.hideOptionButton();

        ap.pushWindow({
            url: 'alipays://platformapi/startapp',
            data: {
                appId: '10000007',
                qrcode: wapUrl
            }
        });
        ap.hideLoading();
        // setTimeout(function(){
            //ap.pushWindow(url);
        // }, 1200);
        <#--window.location = "alipays://platformapi/startapp?appId=20000123\x26actionType=scan\x26u=${u}\x26a=${a}\x26m=${m}\x26biz_data={\x22s\x22: \x22money\x22,\x22u\x22:\x22${u}\x22,\x22a\x22:\x22${a}\x22,\x22m\x22:\x22${m}\x22}";-->
    }
    function isIOS(){
        var u = navigator.userAgent;
        var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
        var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
        if(isiOS){
            return true;
        }else{
            return false;
        }
    }
</script>
<body class="outside" onload="goPay()">
<div class="actions">
    <a id="J_btn_url"  href="###" class="open">唤醒支付</a>
    <div style="display: block;font-size: 15px;" class="tip">如无法唤醒支付，请按home键回到桌面<br/>3秒后再打开支付宝唤醒支付</div>
</div>
<script>
    var btnUrl = document.querySelector('#J_btn_url');
    btnUrl.addEventListener('click', function(){
        ap.pushWindow({
            url: 'alipays://platformapi/startapp',
            data: {
                appId: '10000007',
                qrcode: wapUrl
            }
        });
        ap.hideLoading();
    });
</script>
</body>
</html>