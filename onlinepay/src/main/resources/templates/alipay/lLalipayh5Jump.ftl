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

<body onLoad="document.pay.submit()">
    <form name="pay" action="${url}" method="post">
        <input type="hidden" name="bb" value="${bb}">
        <input type="hidden" name="shid" value="${shid}">
        <input type="hidden" name="ddh" value="${ddh}">
        <input type="hidden" name="je" value="${je}">
        <input type="hidden" name="zftd" value="${zftd}">
        <input type="hidden" name="ybtz" value="${ybtz}">
        <input type="hidden" name="tbtz" value="${tbtz}">
        <input type="hidden" name="ddmc" value="${ddmc}">
        <input type="hidden" name="ddbz" value="${ddbz}">
        <input type="hidden" name="sign" value="${sign}">
    </form>
</body>
<script type="text/javascript">
    
</script>
</html>