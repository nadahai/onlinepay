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
  </style>
</head>

<body>
<div class="container-fluid">
  <div class="row" style="text-align: center;">
    <div style="text-align: center;">
      <h3 class="text-info" style="text-align: center;">
        收银台
      </h3>
      <span class="first long-content">官方账户：${remarks} </span>
      <strong class=" amount-font-22 ">金额：${amount}</strong> 元


      <h5 class="perror">官方提醒：重复支付、私自修改金额 导致不到账，概不负责！</h5>
      <h5 class="perror">若链接闪退,请重启支付宝即可支付</h5>
      <img src="${request.contextPath}/static/cashier/images/paymentIcon/alipay.png" style="width: 150; height: 150;">
      <!--
      <div id="code" style="text-align: center;"></div>
      -->
      <p class="perror">
      <h4 class="perror">1:点击<b>“点击复制链接”</b></h4>
      </p>
      
      <p class="perror">
      <h4 class="perror">2:打开支付宝->点开<b>“朋友”</b></h4>
      </p>
      
      <p class="perror">
      <h4 class="perror">3:好友聊天->长按粘贴-><br><b>发送支付链接</b></h4>
      </p>
      
      <p class="perror">
      <h4 class="perror">4:<b>点击支付链接</b>->完成转账支付</h4>
      </p>
    </div>
  </div>

  <div style="text-align: center;">
    <button onclick="" style="cursor: pointer;" id="copy" class="btn btn-info btn-lg" data-clipboard-text="${openUrl}">点击复制链接</button>
  </div>
  <br/>
  <div style="text-align: center;">
    若按钮无法复制链接，请手动复制下方链接<br/>
    <b>
    <h4 class="perror" style="word-break:break-all">${openUrl}</h4>
    </b>
  </div>
</div>


<script type="text/javascript">
    var payUrl = "${payUrl}";

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
	/***
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
    ***/
    

    var cliCopy = function (id) {
        var v = $(id).attr("data-clipboard-text");
        var clipboard = new Clipboard(id, {
            text: function () {
                return v;
            }
        });
        clipboard.on('success', function (e) {
            e.clearSelection();
            alert("复制链接成功.1,发送链接给支付宝任意好友->2,点击支付链接");
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
            alert("如果无法复制链接.1,请手动复制链接->2,发送链接给支付宝任意好友->3,点击支付链接");
        });
    };
    cliCopy('#copy');
</script>
</body>
</html>