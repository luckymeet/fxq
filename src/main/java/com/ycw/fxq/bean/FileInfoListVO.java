 package com.ycw.fxq.bean;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class FileInfoListVO {

	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 文件路径
	 */
	private String filePath;

	/**
	 * 上传时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String uploadTime;

	/**
	 * 操作人
	 */
	private String uploadPerson;

}
