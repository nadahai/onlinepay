
var baseUrl = "http://tpay.tapgo.me/Tpay/";
var bank_imgs=new Array();
bank_imgs['中国工商银行']='zggs';
bank_imgs['中国农业银行']='zgnyyh';
bank_imgs['中国银行']='zgyh';
bank_imgs['中国建设银行']='zgjs';
bank_imgs['中信银行']='zxyh';
bank_imgs['中国光大银行']='gzgdyh';
bank_imgs['华夏银行']='hxyh';
bank_imgs['中国民生银行']='zgmsyh';
bank_imgs['招商银行']='zsyh';
bank_imgs['兴业银行']='xyyh';
bank_imgs['浦发银行']='pfyh';
bank_imgs['邮政储蓄银行']='zgyz';
bank_imgs['交通银行']='jtyh';
bank_imgs['农村信用社']='ncxys';
bank_imgs['厦门农商银行']='ncxys';
bank_imgs['广发银行']='gfyh';
bank_imgs['平安银行']='payh';

/**
 * 
 * @param url 访问路径
 * @param data json字符串
 * @param success 访问成功返回方法
 * @param error
 */
function postAjax(url, data, success, error){
	$.ajax({  
        url : url,  
        type : 'POST',  
        data : data,  
        dataType : 'json',  
        contentType : 'application/json',  
        success : success,  
        Error : error
    }); 
}

function postajax(className,methodName,data,success,error){
	var url = baseUrl + className + "/" + methodName;
	postAjax(url, data, success, error);
}
function getLevelNameByLevel(level){
	var name="";
	switch (level) {
	case 1:name="贫民";
		break;
	case 2:name="温饱";
	break;
	case 3:name="小康";
	break;
	case 4:name="小资";
	break;
	case 5:name="中产";
	break;
	case 6:name="土豪";
	break;
	case 7:name="富豪";
	break;
	default:
		break;
	}
	return name;
}

/**
 * 判断是不是微信app的浏览器
 * @returns {Boolean}
 */
function isweixin(){  
    var ua = navigator.userAgent.toLowerCase();  
    if(ua.match(/MicroMessenger/i)=="micromessenger") {  
        return true;  
    } else {  
        return false;  
    }  
} 

/**
 * 判断是不是支付宝app的浏览器
 * @returns {Boolean}
 */
function isali(){
	var ua = navigator.userAgent.toLowerCase();
	if(ua.match(/Alipay/i)=="alipay"){
		return true;
    }else{
    	return false;
    }
}


/**
 * 根据宽度和图片大小初始化canvas
 * @param canvas
 * @param img
 * @param w
 */
function canvasInit(canvas,img,w){
	var ctx = canvas.getContext("2d");
	canvas.width = w;
	canvas.height = canvas.width*img.height/img.width;
	ctx.drawImage(img,0,0,canvas.width,canvas.height);
}

/**
 * 
 * @param canvas
 * @param obj
 */
function drawText(canvas,obj){//{text,x,y,size,color,maxLen}
	var count = getStrLen(obj.text);
	var len = count * obj.size;
	if (len > canvas.width*obj.maxLen) {
		len = canvas.width*obj.maxLen;
		obj.size = len/count;
	}
	var ctx = canvas.getContext("2d");
	ctx.fillStyle = obj.color;
	ctx.font="normal " + obj.size + "px 微软雅黑";
	ctx.fillText(obj.text, canvas.width*obj.x - len/2, canvas.height*obj.y + obj.size/2, len);
}

/**
 * 往canvas里的中间画图 (圆形)
 * @param canvas
 * @param img
 * @param set {r,lineW,lineColor} r,lineW相对于canvas.width
 */
function qcodeArc(canvas,img,set){
	var centerPoint = {x:canvas.width/2, y:canvas.height/2};
	var ctx = canvas.getContext("2d");
	var r = set.r * canvas.width;
	if (set.lineW != undefined) {
		var lineW = set.lineW * canvas.width;
		ctx.save();
		ctx.beginPath();
		ctx.arc(centerPoint.x, centerPoint.y, r + lineW, 0, Math.PI*2);//0-2Pi
		ctx.fillStyle = "white";
		if (set.lineColor != undefined) {
			ctx.fillStyle = set.lineColor;
		}
		ctx.fill();
		ctx.restore();
	}
	
	ctx.save();
	ctx.beginPath();
	ctx.arc(centerPoint.x, centerPoint.y, r, 0, Math.PI*2);//0-2Pi
	ctx.clip();
	ctx.drawImage(img,0,0,img.width,img.height,centerPoint.x - r,centerPoint.y - r,2*r,2*r);
	ctx.restore();
}

/**
 * 往canvas里再画图 (正方形)
 * @param canvas
 * @param qcode 要画的图
 * @param x 要画的图的中心点在canvas中的x坐标
 * @param y 要画的图的中心点在canvas中的y坐标
 * @param r 要画的图的半径(正方形)
 */
function drawQcode(canvas,qcode,set){
	var r = canvas.width * set.r;
	var ctx = canvas.getContext("2d");
	ctx.drawImage(qcode, 0, 0, qcode.width, qcode.height, canvas.width*set.x-r, canvas.height*set.y-r, 2*r, 2*r);
}

/**
 * 
 * @param str
 * @returns {Number}
 */
function getStrLen(str){
	var badChar ="ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
	badChar += "abcdefghijklmnopqrstuvwxyz"; 
	badChar += "0123456789"; 
	badChar += " "+"　";//半角与全角空格 
	badChar += "`~!@#$%^&()-_=+]\\\\|:;\"\\\'<,>?/";//不包含*或.的英文符号
	
	var len=0;//一个非汉字字符长度为0.5
	for(var i=0;i<str.length;i++){
		var c=str.charAt(i);
		if(badChar.indexOf(c)==-1){
			len++;
		}else{
			len+=0.5;
		}
	}
	return len;
}

var lzh_error = {};
//银行卡号校验
//Description: 银行卡号Luhm校验
//Luhm校验规则：16位银行卡号（19位通用）:
//1.将未带校验位的 15（或18）位卡号从右依次编号 1 到 15（18），位于奇数位号上的数字乘以 2。
//2.将奇位乘积的个十位全部相加，再加上所有偶数位上的数字。
//3.将加法和加上校验位能被 10 整除。
function luhmCheck(bankno){
	if (bankno.length < 16 || bankno.length > 19) {
		lzh_error.msg = "银行卡号长度必须在16到19之间";
		return false;
	}
	var num = /^\d*$/; //全数字
	if (!num.exec(bankno)) {
		lzh_error.msg = "银行卡号必须全为数字";
		return false;
	}
	//开头6位
	var strBin="10,18,30,35,37,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,58,60,62,65,68,69,84,87,88,94,95,98,99";
	if (strBin.indexOf(bankno.substring(0, 2))== -1) {
		lzh_error.msg = "银行卡号开头6位不符合规范";
		return false;
	}
	var lastNum=bankno.substr(bankno.length-1,1);//取出最后一位（与luhm进行比较）
	var first15Num=bankno.substr(0,bankno.length-1);//前15或18位
	var newArr=new Array();
	for(var i=first15Num.length-1;i>-1;i--){ //前15或18位倒序存进数组
		newArr.push(first15Num.substr(i,1));
	}
	var arrJiShu=new Array(); //奇数位*2的积 <9
	var arrJiShu2=new Array(); //奇数位*2的积 >9
	var arrOuShu=new Array(); //偶数位数组
	for(var j=0;j<newArr.length;j++){
		if((j+1)%2==1){//奇数位
			if(parseInt(newArr[j])*2<9)
				arrJiShu.push(parseInt(newArr[j])*2);
			else
				arrJiShu2.push(parseInt(newArr[j])*2);
		}
		else //偶数位
		arrOuShu.push(newArr[j]);
	}
	var jishu_child1=new Array();//奇数位*2 >9 的分割之后的数组个位数
	var jishu_child2=new Array();//奇数位*2 >9 的分割之后的数组十位数
	for(var h=0;h<arrJiShu2.length;h++){
		jishu_child1.push(parseInt(arrJiShu2[h])%10);
		jishu_child2.push(parseInt(arrJiShu2[h])/10);
	}
	var sumJiShu=0; //奇数位*2 < 9 的数组之和
	var sumOuShu=0; //偶数位数组之和
	var sumJiShuChild1=0; //奇数位*2 >9 的分割之后的数组个位数之和
	var sumJiShuChild2=0; //奇数位*2 >9 的分割之后的数组十位数之和
	var sumTotal=0;
	for(var m=0;m<arrJiShu.length;m++){
		sumJiShu=sumJiShu+parseInt(arrJiShu[m]);
	}
	for(var n=0;n<arrOuShu.length;n++){
		sumOuShu=sumOuShu+parseInt(arrOuShu[n]);
	}
	for(var p=0;p<jishu_child1.length;p++){
	sumJiShuChild1=sumJiShuChild1+parseInt(jishu_child1[p]);
	sumJiShuChild2=sumJiShuChild2+parseInt(jishu_child2[p]);
	}
	//计算总和
	sumTotal=parseInt(sumJiShu)+parseInt(sumOuShu)+parseInt(sumJiShuChild1)+parseInt(sumJiShuChild2);
	//计算Luhm值
	var k= parseInt(sumTotal)%10==0?10:parseInt(sumTotal)%10;
	var luhm= 10-k;
	if(lastNum==luhm){
		lzh_error.msg = "Luhm验证通过";
		return true;
	}
	else{
		lzh_error.msg = "银行卡号输入有误，请检查";
		return false;
	}
}


function submitForm(url,objList){
	var form = $("<form></form>");  
    form.attr('action', url);  
    form.attr('method', 'post');  
    form.attr('target', '_self');
    
    for (var i=0; i < objList.length; i++) {
    	var obj = objList[i];
    	var input = $("<input type='hidden' name='" + obj.name + "' />");  
        input.attr('value', obj.value);  
        form.append(input);
    }
    
    form.css('display', 'none');  
    form.appendTo("body");  
    form.submit();
}

/**
 * 判断当前时间是否在两个时间之间
 * @param startTime 5:00
 * @param endTime 23:15
 * @returns {Boolean}
 */
function isBetweenTime(startTime, endTime){
	var startTimes = startTime.split(":");
	var endTimes = endTime.split(":");
	var startMinutes = parseInt(startTimes[0])*60 + parseInt(startTimes[1]);
	var endMinutes = parseInt(endTimes[0])*60 + parseInt(endTimes[1]);
	var myDate = new Date();
	var hourNow = myDate.getHours();
	var minuteNow = myDate.getMinutes();
	var nowMinutes = hourNow * 60 + minuteNow;
	if (startMinutes <= nowMinutes && endMinutes >= nowMinutes) {
		return true;
	}
	return false;
}