package com.ycw.fxq.common.constants;

public enum FileTypeEnum {

	LEGAL_LITERATURE(1, "法律文献");

	private Integer code;

	private String desc;

	private FileTypeEnum(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public Integer getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

}
