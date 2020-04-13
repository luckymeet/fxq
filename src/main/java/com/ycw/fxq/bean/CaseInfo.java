package com.ycw.fxq.bean;

import com.ycw.fxq.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 案件实体类
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class CaseInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String caseName;
    private String caseType;
    private String caseCharger;
    private String detail;
}
