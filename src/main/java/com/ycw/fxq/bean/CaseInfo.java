package com.ycw.fxq.bean;

import com.ycw.fxq.common.base.BaseEntity;
import lombok.Data;

/**
 * 案件实体类
 */
@Data
public class CaseInfo extends BaseEntity {
//    private int id;
    private String caseName;
    private String caseType;
    private String caseCharger;
    private String detail;
//    private String delInd;
//    private int createUser;
//    private LocalDateTime createTime;
//    private int updateUser;
//    private LocalDateTime updateTime;
//    private int version;
}
