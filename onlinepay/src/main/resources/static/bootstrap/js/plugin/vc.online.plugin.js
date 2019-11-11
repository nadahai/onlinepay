//step01 定义JQuery的作用域，闭包限定命名空间
(function($) {
	var defaults = {
		prevId : 'prevBtn',
		prevText : 'Previous',
		nextId : 'nextBtn',
		nextText : 'Next'
	};

	var ajaxPpostDefaults = {
		data : {}
	};

	// step02 插件的扩展方法名称
	$.fn.easySlider = function(options) {
		var options = $.extend(defaults, options);
		// step5 支持链式调用
		return this.each(function() {
			$(this).append(function() {
				return "(" + $(obj).attr("href") + ")"
			});
		});
	}

	// 异步post请求
	$.fn.ajax.post = function(options) {
		var options = $.extend(ajaxPpostDefaults, options);
		$.ajax({
			url : options.url,
			type : 'POST',
			data : options.data,
			dataType : 'json',
			success : function(data) {
				return data;
			},
			error : function(request) {
				alert("Connection error");
			}
		});
	}

	// 异步get请求
	$.fn.ajax.post = function(options) {
		var options = $.extend(ajaxPpostDefaults, options);
		$.ajax({
			url : options.url,
			type : 'POST',
			data : options.data,
			dataType : 'json',
			success : function(data) {
				return data;
			},
			error : function(request) {
				alert("Connection error");
			}
		});
	}

})(jQuery);