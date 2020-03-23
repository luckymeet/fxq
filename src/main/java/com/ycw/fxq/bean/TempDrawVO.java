package com.ycw.fxq.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TempDrawVO extends TempDraw {

	private String color;

}
