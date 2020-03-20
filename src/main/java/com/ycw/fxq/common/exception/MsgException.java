package com.ycw.fxq.common.exception;

/**
 * @类名称 MsgException.java
 * @类描述 消息推送的异常类
 * @作者 yuminjun yuminjun@lexiangbao.com
 * @创建时间 2019年9月12日 下午3:24:10
 * @版本 1.00
 *
 * @修改记录
 *
 *       <pre>
 *     版本                       修改人 		修改日期 		 修改内容描述
 *     ----------------------------------------------
 *     1.00 	yuminjun 	2019年9月12日
 *     ----------------------------------------------
 *       </pre>
 */
public class MsgException extends Exception {

	private static final long serialVersionUID = 1L;

	private final int code;

	public MsgException(int code, String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.code = code;
	}

	public MsgException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public MsgException(int code, String message) {
		super(message);
		this.code = code;
	}

	public MsgException(int code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
