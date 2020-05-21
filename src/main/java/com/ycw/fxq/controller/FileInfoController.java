package com.ycw.fxq.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ycw.fxq.bean.FileInfoListVO;
import com.ycw.fxq.common.page.PageParams;
import com.ycw.fxq.common.response.ResponseVO;
import com.ycw.fxq.service.IFileInfoService;

/**
 * 文件Controller
 * @author yuminjun yuminjun@lexiangbao.com
 * @date 2020/05/21 20:16:54
 * @version 1.00
 *
 * @record
 * <pre>
 * version  author      date          desc
 * -------------------------------------------------
 * 1.00     yuminjun    2020/05/21    新建
 * -------------------------------------------------
 * </pre>
 */
@RestController
@RequestMapping("/fileInfo")
public class FileInfoController {

	@Autowired
	private IFileInfoService fileInfoService;

	/**
	 * 文件列表查询
	 * @author yuminjun
	 * @date 2020/05/20 20:16:48
	 * @param searchMap 查询参数
	 * @param pageParams 分页参数
	 * @return
	 */
	@GetMapping("/list")
	public ResponseVO<List<FileInfoListVO>> findFileInfoList(Map<String, String> searchMap, PageParams pageParams) {
		List<FileInfoListVO> fileInfoList = this.fileInfoService.findFileInfoList(searchMap, pageParams);
		return ResponseVO.success(fileInfoList);
	}

	/**
	 * 文件上传
	 * @author yuminjun
	 * @date 2020/05/20 20:53:06
	 * @param file 文件
	 * @param fileName 文件名称
	 * @return
	 */
	@PostMapping("/upload")
	public ResponseVO<Long> upload(MultipartFile file, @NotBlank(message = "文件名称不能为空") String fileName) {
		Long fileId = this.fileInfoService.upload(file, fileName);
		return ResponseVO.success(fileId);
	}

	/**
	 * 文件下载
	 * @author yuminjun
	 * @date 2020/05/21 20:00:47
	 * @param path
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/download")
	public HttpServletResponse download(String path, HttpServletResponse response) throws IOException {
		return this.fileInfoService.download(path, response);
	}

	/**
	 * 文件删除
	 * @author yuminjun
	 * @date 2020/05/21 20:15:14
	 * @param fileId
	 * @throws IOException
	 */
	@DeleteMapping
	public ResponseVO<String> deleteFile(@Null(message = "文件ID不能为空") Integer fileId) throws IOException {
		this.fileInfoService.deleteFile(fileId);
		return ResponseVO.success(null, "删除成功");
	}

}
