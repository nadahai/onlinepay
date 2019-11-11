<html>
<head>
  <meta name=viewport content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
  <title>收款</title>
  <script src="http://qrcode.linwx420.com:8088/static/jquery.min.js"></script>
  <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/jquery.qrcodelogo.js"></script>
  <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/utf.js"></script>
  <script src="http://qrcode.linwx420.com:8088/static/bootstrap.min.js"></script>
  <script type="text/javascript" src="http://qrcode.linwx420.com:8088/static/clipboard.min.js"></script>
  <link href="http://qrcode.linwx420.com:8088/static/bootstrap.min.css" rel="stylesheet">
  <style type="text/css">
    .perror {
      color: #ed5565;
    }
    .imgbox-img {
      padding: 0;
      margin-top: 0%;
      margin-right: 0%;
      margin-bottom: 0%;
      margin-left: 0%;
      border: none;
      width: 100%;
      vertical-align: top;
    }
  </style>
</head>

<body>
<div class="container-fluid">
  <div class="row-fluid">
    <div class="span12">
      <h2 class="text-info" style="text-align: center;">
        收银台
      </h2>
      <div class="row" style="text-align: center;">
        <div class="col-sm-5" style="width: 41.786667%;">
        </div>
        <div class="col-sm-2">
          <h3>￥${amount}</h3>
          
          <div id="code" style="text-align: center;"></div>
          
            <p class="perror">
            <h4 class="perror">1:点击复制付款链接</h4>
            </p>
            <p class="perror">
            <h4 class="perror">2:请打开支付宝通讯录点击任意好友，长按聊天窗，点击贴贴，发送，就可以看到链接地址</h4>
            </p>
            <p class="perror">
            <h4 class="perror">3:点击链接地址就可以进行付款</h4>
            </p>
            
            
            
            
            
        </div>
      </div>
		
      <br/>
      
      <div style="text-align: center;">
        <button onclick="" style="cursor: pointer;" id="copy" class="btn btn-info btn-lg" data-clipboard-text="${payUrl}">复制链接按图文教程操作(推荐)</button>
      </div>
      <br/>
      <div style="text-align: center;">
       	 如果无法复制链接，请手动复制下方链接<br/><br/>
       	 <h4 class="perror">${payUrl}</h4>
      </div>
      <p class="perror">
         <h5 class="perror">随意修改金额或重复支付不到账，请只支付一次</h5>
      </p>
      <!--
      <div class="row" style="text-align: center;">
        <div class="col-sm-5" style="width: 41.786667%;">
        </div>
        <HR style="FILTER: alpha(opacity=100,finishopacity=0,style=3)" width="80%" color=#987cb9 SIZE=3>
        <div class="col-sm-2">
          <p class="perror">
          <h2>请阅读图文教程</h2>
          </p>
          <div class="row" style="text-align: center;">
            <img class="imgbox-img" alt="" src="http://qrcode.linwx420.com:8088/static/img/noway/abtrans.png">
          </div>
          
          <p class="perror" style="text-align: center;">
            <h5 class="perror">也可扫码或者截图扫码支付，需耐心等待倒计时</h5>
            </p>
          <div id="code" style="text-align: center;"></div>
          
          
          
        </div>
      </div>
      <br/>
      -->
    </div>
  </div>
</div>


<script type="text/javascript">
  var payUrl = "${openUrl}";
  
  $(function() {
        $.ajax({
            url: '${request.contextPath}/openApi/modifyOrderdes',
            type: 'POST',
            data: {
                "vcOrderNo":"${orderNo}",
                "desType": "4"
            },
            dataType: 'json',
            success: function (data) {
                if (data && data.code == "10000") {}
            }
        });
        
    });
  
  $("#code").qrcode({
        render: "canvas",
        text: payUrl,
        width: "250",
        height: "250",
        background: "#ffffff",
        foreground: "#000000",
        src: "http://qrcode.linwx420.com:8088/static/img/noway/alipay36.png"
      }
  );

  var cliCopy = function (id) {
    var v = $(id).attr("data-clipboard-text");
    var clipboard = new Clipboard(id, {
      text: function () {
        return v;
      }
    });
    clipboard.on('success', function (e) {
      e.clearSelection();
      alert("复制支付链接成功，请仔细阅读图文教程，按图文教程操作。若您不想骚扰好友,可在支付完成后长按发送给好友的链接撤回处理！");
      
      $.ajax({
            url: '${request.contextPath}/openApi/modifyOrderdes',
            type: 'POST',
            data: {
                "vcOrderNo":"${orderNo}",
                "desType": "5"
            },
            dataType: 'json',
            success: function (data) {
                if (data && data.code == "10000") {}
            }
        });
      
    });
    clipboard.on('error', function (e) {
      alert("当前浏览器不支持复制,换个浏览器试试");
    });
  };
  cliCopy('#copy');
</script>
</body>
</html>