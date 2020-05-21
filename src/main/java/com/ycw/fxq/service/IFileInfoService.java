 package com.ycw.fxq.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ycw.fxq.bean.FileInfo;
import com.ycw.fxq.bean.FileInfoListVO;
import com.ycw.fxq.common.page.PageParams;

public interface IFileInfoService extends IService<FileInfo> {

	/**
	 * 文件列表查询
	 * @author yuminjun
	 * @date 2020/05/20 20:16:48
	 * @param searchMap 查询参数
	 * @param pageParams 分页参数
	 * @return
	 */
	List<FileInfoListVO> findFileInfoList(Map<String, String> searchMap, PageParams pageParams);

	/**
	 * 文件上传
	 * @author yuminjun
	 * @date 2020/05/20 20:53:06
	 * @param file 文件
	 * @return
	 */
	Long upload(MultipartFile file);

	/**
	 * 文件下载
	 * @author yuminjun
	 * @date 2020/05/21 20:00:47
	 * @param path
	 * @param response
	 * @return
	 * @throws IOException
	 */
	HttpServletResponse download(String path, HttpServletResponse response) throws IOException;

	/**
	 * 文件删除
	 * @author yuminjun
	 * @date 2020/05/21 20:15:14
	 * @param fileId
	 * @throws IOException
	 */
	void deleteFile(Integer fileId) throws IOException;

}
