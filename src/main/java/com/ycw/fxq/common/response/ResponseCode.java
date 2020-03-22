package com.ycw.fxq.common.response;

/**
 * @类名称 ResponseCode.java
 * @类描述 响应码枚举类
 * @作者 yuminjun yuminjun@lexiangbao.com
 * @创建时间 2019年9月12日 下午3:44:12
 * @版本 1.00
 *
 * @修改记录
 * <pre>
 *     版本                       修改人 		修改日期 		 修改内容描述
 *     ----------------------------------------------
 *     1.00 	yuminjun 	2019年9月12日
 *     ----------------------------------------------
 * </pre>
 */
public enum ResponseCode {

	SUCCESS (200, "SUCCESS", "成功"),
	ERR_400 (400, "ERR_400", "错误请求"),
	ERR_401 (401, "ERR_401", "未授权"),
	ERR_403 (403, "ERR_403", "禁止"),
	ERR_404 (404, "ERR_404", "未找到"),
	ERR_405 (405, "ERR_405", "方法禁用"),
	ERR_406 (406, "ERR_406", "不接受"),
	ERR_407 (407, "ERR_407", "需要代理授权"),
	ERR_408 (408, "ERR_408", "请求超时"),
	ERR_409 (409, "ERR_409", "冲突"),
	ERR_410 (410, "ERR_410", "已删除"),
	ERR_411 (411, "ERR_411", "需要有效长度"),
	ERR_412 (412, "ERR_412", "未满足前提条件"),
	ERR_415 (415, "ERR_415", "不支持的媒体类型"),
	ERR_416 (416, "ERR_416", "请求范围不符合要求"),
	ERR_417 (417, "ERR_417", "请求参数不合法"),
	ERR_418 (418, "ERR_418", "缺少请求参数"),
	ERR_500 (500, "ERR_500", "服务器内部错误"),

	ERR_999 (999, "ERR_999", "其他错误");

	/**
	 *编码
	 */
	private final Integer code;

	/**
	 *名称
	 */
	private final String name;

	/**
	 *描述
	 */
	private final String desc;

	ResponseCode(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

	public Integer getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

}
