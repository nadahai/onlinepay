(function($){
	// 公共函数
	// 表单验证
	$.isPhone = function(num){
		return /^1[3|4|5|8][0-9]\d{8}$/.test(num);
	};

	$.isTel = function(str){
		return /^\d{3,4}-\d{5,9}(-\d{3,6})?$/.test(str);
	};

	// 是否电子邮箱
	$.isEmail = function(str){
		return /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/.test(str);
	};

	// 是否验证码
	$.isCode = function(str){
		return /^\d{4,6}$/.test(str);
	};

	// 是否密码
	$.isPassword = function(str){
		return /^[0-9a-zA-Z_]{6,20}$/.test(str);
	};

	// 是否姓名
	$.isName = function(str){
		return /^[\u4e00-\u9fa5]{2,8}$/.test(str);
	};

	// 显示清空表单按钮
	$.fn.showDelbtn = function(){
		return this.each(function(){
			if($(this).val() !== ''){
				$(this).siblings('.icon-del').show();
			}
			$(this).keyup(function(){
				$(this).siblings('.icon-del').css('display',$(this).val() !== '' ? 'block' : 'none');
			});
		});
	};

	// 清空表单数据
	$.fn.delText = function(){
		return this.each(function(){
			$(this).click(function(){
				$(this).hide().siblings('.text').val('').focus();
			});
		});
	};

	// 页面跳转
	$.openUrl = function(url){
		window.location.href = url;
	};

	// 加载进度
	$.loading = function(){
		if(!$('#mask').length){
			$('<div id="mask" />').appendTo('body');
		}
		if(!$('#loading').length){
			$('<div id="loading" />').appendTo('body');
		}
		$('#mask,#loading').show();
	};

	// 加载完成
	$.loaded = function(){
		// setTimeout(function(){
			$('#mask,#loading').hide();
		// }, 500);
	};

	// 获取cookie
	$.getCookie = function(name){
		var name = name + "=";
		var ca = document.cookie.split(';');
		for (var i = 0; i < ca.length; i++) {
			var c = ca[i];
			while (c.charAt(0) == ' ') c = c.substring(1);
			if (c.indexOf(name) != -1) return c.substring(name.length, c.length);
		}
		return "";
	};

	// 设置cookie
	$.setCookie = function(name,value,exdays){
		var d = new Date();
		d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
		var expires = "expires=" + d.toUTCString();
		document.cookie = name + "=" + value + "; " + expires;
	};

	// 删除cookie
	$.delCookie = function(name){
		$.setCookie(name, "", -1);  
	};

	// 判断是否登录
	$.checkLogin = function(){
		var loginName = $.getCookie('loginName');
		var token = $.getCookie('token');

		if(loginName && token){			
			return true;		
		}
		return false;
	};

	// 退出登录
	$.logout = function(){
		$.delCookie('loginName');
		$.delCookie('token');
	};

	// 判断是否微信
	$.isWeixin = function(){
		var ua = window.navigator.userAgent.toLowerCase();
		if (ua.indexOf('micromessenger') !== -1) {
			return true;
		} else {
			return false;
		}
	};

	// 获取url参数
	$.getQueryString = function(name) { 
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
		var r = window.location.search.substr(1).match(reg); 
		if (r != null) return unescape(r[2]); return null; 
	} 

	// 获取数据
	$.getData = function(url,params,callback){
		if(arguments.length == 2){
			callback = params;
			params = {};
		}
		$.get(url,params,function(data){
			data = JSON.parse(data);
			callback(data);
		});
	};

	$.getLoginData = function(api,params,callback){
		if(arguments.length == 2){
			callback = params;
			params = {};
		}
		$.post('/api/' + api + '_data.php',params,function(data){
			data = JSON.parse(data)['data'];
			callback(data);
		});
	};

	// 获取localStorage
	$.getLocal = function(name){
		return localStorage.getItem(name);
	};

	// 设置localStorage
	$.setLocal = function(name,value){
		localStorage.setItem(name,value);
	};

	// 删除localStorage
	$.delLocal = function(name){
		localStorage.removeItem(name);
	};

	// 获取sessionStorage
	$.getSession = function(name){
		return sessionStorage.getItem(name);
	};

	// 设置sessionStorage
	$.setSession = function(name,value){
		sessionStorage.setItem(name,value);
	};

	// 删除sessionStorage
	$.delSession = function(name){
		sessionStorage.removeItem(name);
	};

	// 获取城市id
	$.getCityCode = function(){
		return $.getLocal('cityCode');
	};

	// 上传图片
	$.fn.uploadImg = function(callback){
		return this.each(function(){
			$(this).change(function(){
				var _self = this;
				var val = $(this).val();
				var oFile = this.files[0];
				// 判断上传的文件是否符合类型
				var types = '.jpg,.jpeg,.png,.gif';
				var fileExt = val.substring(val.lastIndexOf('.')).toLowerCase();
				var result = types.indexOf(fileExt);

				// 判断是否符合上传条件
				if(this.files.length == 0) return;
				if(result < 0){
					alert('文件格式不正确！');
					return;
				}
				if(oFile.size > 2048 * 1024){
					alert('文件过大，请重试！');
					return;
				}

				// 上传图片
				var oFReader = new FileReader();
				oFReader.readAsDataURL(oFile);
				oFReader.onload = function(ev){
					callback.call(_self,ev.target.result);
				};
			});
		});
	};

	$.fn.getSize = function(){
		var cssText = getComputedStyle(this[0],null);
		var pos = cssText['position'];
		var display = cssText['display'];

		$(this).css({
			position : 'absolute',
			display : 'block',
			visibility : 'hidden'
		});

		var size = {
			width : $(this).width(),
			height : $(this).height()
		};

		$(this).css({
			position : pos,
			display : display,
			visibility : 'visible'
		});

		return size;
	};

	// 弹窗
	$.alert = function(options){
		var opt = {
			title : '标题',
			content : '内容',
			txt : '按钮',
			callback : function(){}
		};
		opt = $.extend({},opt,options);

		var dialog = $('<div class="dialog"><h2 class="dialog-title">'+opt.title+'</h2><div class="dialog-content">'+opt.content+'</div><div class="dialog-btns clearfix"><a href="javascript:;" >'+opt.txt +'</a><em class="close-x"></em></div>');
		$('.mask').fadeIn();
		$('body').append(dialog);

		dialog.css({
			marginTop : - dialog.getSize().height  / 2,
			display : 'block'
		});

		$('.dialog-btns a').click(function(){
			hideDialog();
			opt.callback();
		});

		$('.close-x').click(function(){
			hideDialog();
		});

		function hideDialog(){
			$('.mask').fadeOut();
			dialog.remove();
		}
	};

	// 确定框
	$.confirm = function(options){
		var opt = {
			title : '标题',
			content : '内容',
			txt : ['确定','取消'],
			enter : function(){},
			cancel : function(){}
		};
		opt = $.extend({},opt,options);

		var dialog = $('<div class="dialog dialog-confirm"><h2 class="dialog-title">'+opt.title+'</h2><div class="dialog-content">'+opt.content+'</div><div class="dialog-btns clearfix"><a href="javascript:;">'+opt.txt[0]+'</a><a href="javascript:;">'+opt.txt[1]+'</a></div>');
		if(!$('#mask').length){
			$('body').append('<div id="mask" class="mask"></div>');
		}
		$('.mask').show();
		$('body').append(dialog);

		dialog.css({
			marginTop : - dialog.getSize().height / 2,
			display : 'block'
		});

		$('.dialog-btns a').eq(0).click(function(){
			hideDialog();
			opt.enter();
		});

		$('.dialog-btns a').eq(1).click(function(){
			hideDialog();
			// opt.cancel();
		});

		$('.close-x').click(function(){
			hideDialog();
		});

		function hideDialog(){
			$('.mask').hide();
			dialog.remove();
		}
	};


	// 消息框
	$.message = function(str){
		if($('.ui-message').length){
			return;
		} else {
			var message = $('<div class="ui-message">'+str+'</div>');
			$('body').append(message);
		}
		
		message.css({
			left : ($(window).width() - message.outerWidth()) / 2,
			top : ($(window).height() - message.outerHeight()) / 2
		}).fadeIn();
		var timer = setTimeout(function(){
			message.fadeOut();
			setTimeout(function(){
				message.remove();
			}, 400);
		}, 3000);
	};

	// 区间函数
	$.range = function(now,min,max){
		if(now < min){
			now = min;
		} else if(now > max){
			now = max;
		}
		return now;
	};

	// 回顶部
	// $('<div id="backTop"><i></i></div>').appendTo('body').click(function(){
	// 	$('html,body').animate({scrollTop : 0});
	// });

	// $(window).scroll(function(){
	// 	$('#backTop').css('display',$(window).scrollTop() > 120 ? 'block' : 'none');
	// });


	$(document).ready(function(){

		$('.text').after('<i class="icon-del" />').showDelbtn();
		$('.icon-del').delText();

		$('.icon-eye').click(function(){
			$(this).toggleClass('cu');
			var input = $(this).siblings('.text');
			input.attr('type',input.attr('type') == 'text' ? 'password' : 'text');
		});
	});
})(jQuery);
