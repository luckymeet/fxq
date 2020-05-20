package com.ycw.fxq.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ycw.fxq.bean.FileInfo;
import com.ycw.fxq.bean.FileInfoListVO;
import com.ycw.fxq.common.base.BaseCrudMapper;

@Mapper
public interface FileInfoMapper extends BaseCrudMapper<FileInfo> {

    /**
     * 文件列表查询
     * @author yuminjun
     * @date 2020/05/20 19:58:40
     * @param searchMap 查询参数
     * @return
     */
    List<FileInfoListVO> findFileInfoList(Map<String,String> searchMap);
}
