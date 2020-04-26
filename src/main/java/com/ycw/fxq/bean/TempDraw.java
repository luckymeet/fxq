package com.ycw.fxq.bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class TempDraw {

	private String bankName;
	private LocalDateTime time;
	private String inOrOut;
	private String name1;
	private String card1;
	private BigDecimal money;
	private String name2;
	private String card2;
	private String caseId;

}
