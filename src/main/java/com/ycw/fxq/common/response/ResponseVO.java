package com.ycw.fxq.common.response;

import java.io.Serializable;

import com.ycw.fxq.common.utils.SpringUtils;

import io.lettuce.core.tracing.Tracer;
import lombok.ToString;

@ToString
public class ResponseVO<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int SUCCESS = 200;

	private Integer code;

	private String message;

	private T data;

	private Long timestamp = System.currentTimeMillis();

	private String traceId;

	private ResponseVO() {
	}

	public static <D> ResponseVO<D> success(D data) {
		return success(data, (String) null);
	}

	public static <D> ResponseVO<D> success(D data, String message) {
		ResponseVO<D> responseVO = new ResponseVO<>();
		responseVO.setCode(SUCCESS);
		responseVO.setMessage(message);
		responseVO.setData(data);
		return responseVO;
	}

	public static <D> ResponseVO<D> fail(int code, String message) {
		ResponseVO<D> responseVO = new ResponseVO<>();
		responseVO.setCode(code);
		responseVO.setMessage(message);
		return responseVO;
	}

	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return this.data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isSuccess() {
		return this.code == SUCCESS;
	}

	public String getTraceId() {
		return this.traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
}
