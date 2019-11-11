/**
 * Copyright &copy; 2012-2016 <a href="http://www.vc-group.cn">vc-group</a> All rights reserved.
 */
package com.vc.onlinepay.exception;

/**
 * Service层公用的Exception, 从由Spring管理事务的函数中抛出时会触发事务回滚.
 * @author ThinkGem
 */
public class OnlineServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OnlineServiceException() {
		super();
	}

	public OnlineServiceException(String message) {
		super(message);
	}

	public OnlineServiceException(Throwable cause) {
		super(cause);
	}

	public OnlineServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
