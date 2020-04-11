package com.ycw.fxq.bean;

import lombok.Data;

/**
 *
 */
@Data
public class CaseInfoVo {
    private Integer id;
    private String caseName;
    private Integer caseType;
    private String caseTypeName;
    private String caseCharger;
    private String detail;
}
