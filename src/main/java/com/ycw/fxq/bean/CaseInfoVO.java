package com.ycw.fxq.bean;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

/**
 *
 */
@Data
public class CaseInfoVO {

    private Integer id;
    private String num;
    private String caseName;
    private Integer caseType;
    private String caseTypeName;
    private String caseCharger;
    private String detail;

	public String getNum() {
		return id == null ? "" : "AJ" + StringUtils.leftPad(String.valueOf(id), 8, '0');
	}

}
