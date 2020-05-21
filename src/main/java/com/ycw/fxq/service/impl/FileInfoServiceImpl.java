 package com.ycw.fxq.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ycw.fxq.bean.FileInfo;
import com.ycw.fxq.bean.FileInfoListVO;
import com.ycw.fxq.common.constants.CommonConstants;
import com.ycw.fxq.common.constants.FileTypeEnum;
import com.ycw.fxq.common.exception.MsgException;
import com.ycw.fxq.common.page.PageParams;
import com.ycw.fxq.common.response.ResponseCode;
import com.ycw.fxq.mapper.FileInfoMapper;
import com.ycw.fxq.service.IFileInfoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements IFileInfoService {

	@Autowired
	private FileInfoMapper fileInfoMapper;

	private static final String FILE_DIR = "C:/temp/";

	/**
	 * 文件列表查询
	 * @author yuminjun
	 * @date 2020/05/20 20:16:48
	 * @param searchMap 查询参数
	 * @param pageParams 分页参数
	 * @return
	 */
	@Override
	public List<FileInfoListVO> findFileInfoList(Map<String,String> searchMap, PageParams pageParams) {
		return this.fileInfoMapper.findFileInfoList(searchMap);
    }

	/**
	 * 文件上传
	 * @author yuminjun
	 * @date 2020/05/20 20:53:06
	 * @param file 文件
	 * @param fileName 文件名称
	 * @return
	 */
	@Override
	public Long upload(MultipartFile file, String fileName) {
		String filePath = createFile(file);
		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileName(fileName);
		fileInfo.setFilePath(filePath);
		fileInfo.setFileType(FileTypeEnum.LEGAL_LITERATURE.getCode());
		fileInfo.setCreateTime(LocalDateTime.now());
		fileInfo.setUpdateTime(LocalDateTime.now());
		fileInfoMapper.insert(fileInfo);
		return fileInfo.getId();
	}

	private String createFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new MsgException(ResponseCode.ERR_418.getCode(), "文件名为空");
		}
		String fileName = file.getOriginalFilename(); // 文件名
		String suffixName = StringUtils.substring(fileName, fileName.lastIndexOf('.')); // 后缀名
		fileName = UUID.randomUUID() + suffixName; // 新文件名
		String filePath = FILE_DIR + fileName;
		File dest = new File(filePath);
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		try {
			file.transferTo(dest);
		} catch (IOException e) {
			log.error("文件写入失败", e);
			throw new MsgException(ResponseCode.ERR_500.getCode(), "文件上传失败");
		}
		return filePath;
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
	@Override
	public HttpServletResponse download(String path, HttpServletResponse response) throws IOException {
		try (
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
		){
			// path是指欲下载的文件的路径
			File file = new File(path);
			// 取得文件名
			String filename = file.getName();

			// 以流的形式下载文件
			byte[] buffer = new byte[fis.available()];
			while (fis.read(buffer) > 0);
			// 清空response
			response.reset();
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
			response.addHeader("Content-Length", "" + file.length());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
		} catch (IOException e) {
			log.error("读取文件失败", e);
		}
		return response;
	}

	/**
	 * 文件删除
	 * @author yuminjun
	 * @date 2020/05/21 20:15:14
	 * @param fileId
	 * @throws IOException
	 */
	@Override
	public void deleteFile(Integer fileId) throws IOException {
		FileInfo fileInfo = this.fileInfoMapper.selectById(fileId);
		fileInfo.setDelInd(CommonConstants.INT_NO);
		fileInfo.setUpdateTime(LocalDateTime.now());
		this.fileInfoMapper.updateById(fileInfo);
		Files.delete(Paths.get(fileInfo.getFilePath()));
	}

}
