<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <#include "/include/head.ftl">
    <meta name="format-detection" content="telephone=no, email=no">
    <title>收银台</title>
    <link rel="stylesheet" href="${request.contextPath}/static/cashier/css/gateway/BankLogo.css">
    <link rel="stylesheet" href="${request.contextPath}/static/cashier/css/gateway/style_pc.css">
    <link rel="stylesheet" href="${request.contextPath}/static/cashier/css/gateway/style_phone.css">
    <link rel="stylesheet" href="${request.contextPath}/static/css/layer.css">
    <script src="${request.contextPath}/static/js/jquery.min.js"></script>
    <script src="${request.contextPath}/static/js/layer.js"></script>
    <style>
        body{
            margin: 0;
            padding: 0;
        }
        p{
            margin: 0;
            padding: 0;
        }
    </style>

    <script type="text/javascript">
        $(function() {
            var form = $("#postForm");
            <#assign userMap = map/>
            <#list userMap?keys as key>
                var input = $("<input type='hidden'>");
                input.attr({ "name": "${key}" });
                input.attr({ "id": "${key}" });
                input.val('${userMap["${key}"]}');
                form.append(input);
            </#list>
        });
    </script>

</head>
<body>

    <div id="pc_pay">
        <!--头部开始-->
        <div class="pay_header">
            <div class="pay_top">
                <p style="text-align: -webkit-left;">
                    <em>收银台</em>
                </p>
            </div>
        </div>
        <!--头部结束-->
        <!--内容开始-->
        <div class="pay_content">
            <div class="tie_ct">
                <div class="tie_ct_l">
                    <div class="tie_text">
                        <h6 style="text-align: left;">订单提交成功，请您尽快付款！</h6>
                        <p>请您在订单提交后<em>5分钟</em>内完成付款，否则订单会自动取消。</p>
                        <p>订单号：${details.orderNo}</p>
                    </div>
                </div>
                <div class="tie_ct_r">
                    <h6>应付金额：<em>${details.amount}</em>元</h6>
                </div>
            </div>
            <div class="clear"></div>

            <div class="credit_box">
                <div class="pany_modes">
                    <p class="js_online_pany_one">个人网银</p>
                    <div class="clear"></div>
                </div>
                <div class="online_box" style="display: block;">
                    <div class="">
                        <ul class="SingleSelect" style="margin-top:2%;padding-left: 0;">
                            <li class="BankLogo_150_40_ICBC BankLogoLi" title="工商银行" bankcode="ICBCD" onclick="updateCardType(this)">
                                <span class="Radio"></span>
                            </li>
                            <li class="BankLogo_150_40_ABC BankLogoLi" title="农业银行" bankcode="ABCD" onclick="updateCardType(this)">
                                <span class="Radio"></span>
                            </li>
                            <li class="BankLogo_150_40_CCB BankLogoLi" title="建设银行" bankcode="CCBD" onclick="updateCardType(this)">
                                <span class="Radio"></span>
                            </li>
                            <li class="BankLogo_150_40_CMB BankLogoLi" title="招商银行" bankcode="CMBD" onclick="updateCardType(this)">
                                <span class="Radio"></span>
                            </li>
                            <li class="BankLogo_150_40_GDB BankLogoLi" title="广发银行" bankcode="GDBD" onclick="updateCardType(this)">
                                <span class="Radio"></span>
                            </li>
                            <li class="BankLogo_150_40_COMM BankLogoLi" title="交通银行" bankcode="BOCOMD" onclick="updateCardType(this)">
                                <span class="Radio"></span>
                            </li>
                            <li class="BankLogo_150_40_CMBC BankLogoLi" title="民生银行" bankcode="CMBCD" onclick="updateCardType(this)">
                                <span class="Radio"></span>
                            </li>
                            <li class="BankLogo_150_40_CEB BankLogoLi" title="光大银行" bankcode="CEBD" onclick="updateCardType(this)">
                                <span class="Radio"></span>
                            </li>
                            <li class="BankLogo_150_40_SHBANK BankLogoLi" title="上海银行" bankcode="BOSCD" onclick="updateCardType(this)">
                                <span class="Radio"></span>
                            </li>
                            <li class="BankLogo_150_40_PSBC BankLogoLi" title="邮政储蓄银行" bankcode="PSBCD" onclick="updateCardType(this)">
                                <span class="Radio"></span>
                            </li>
                            <li class="BankLogo_150_40_BJBANK BankLogoLi" title="北京银行" bankcode="BCCBD" onclick="updateCardType(this)">
                                <span class="Radio"></span>
                            </li>

                            <#--<li class="BankLogo_150_40_Unknow BankLogoLi" title="测试" bankcode="unionACPB2C" onclick="updateCardType(this)">-->
                                <#--<span class="Radio"></span>-->
                            <#--</li>-->
                        </ul>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="pay_form">
                    <br>
                    <form id = "postForm" action="${actionUrl}" method="POST" target="_self">
                        <input type="button" class="pay_form_submit" onclick="submitForm()" value="到网上银行支付">
                    </form>
                </div>

            </div>
        </div>
        <!--内容结束-->
        <!--尾部开始-->
        <div class="pay_footer">
        </div>
        <!--尾部结束-->
    </div>

    <script type="text/javascript">
        function updateCardType(obj) {
            $(".Radio").attr('class','Radio');
            var bankcode = obj.getAttribute('bankcode');
            var span = obj.children[0];
            span.setAttribute('class','Radio span_check')
            $("#paymentChannel").val(bankcode);
            $(".pay_form_submit").attr('class','pre_submit');
        }
        function submitForm() {
            debugger;
            var bank = $("#paymentChannel").val();
            if(!bank || bank.length<1){
                layer.open({content: '请选择交易银行!', skin: 'msg', time: 3});
                return false;
            }
            var form = $("#postForm");
            form.submit();
            return true;
        }
    </script>

</body>
</html>