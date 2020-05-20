package com.ycw.fxq.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ycw.fxq.common.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("case_info")
public class FileInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 文件类型
	 */
	private Integer fileType;

	/**
	 * 文件路径
	 */
	private String filePath;

}
