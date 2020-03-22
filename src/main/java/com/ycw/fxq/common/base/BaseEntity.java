package com.ycw.fxq.common.base;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;

import lombok.Data;

@Data
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 删除标识：0-未删除；1-删除
	 */
	@TableLogic
	@TableField("del_ind")
	private Integer delInd;

	/**
	 * 创建人
	 */
	@TableField("create_user")
	private Long createUser;

	/**
	 * 创建时间
	 */
	@TableField("create_time")
	private LocalDateTime createTime;

	/**
	 * 更新人
	 */
	@TableField("update_user")
	private Long updateUser;

	/**
	 * 更新时间
	 */
	@TableField("update_time")
	private LocalDateTime updateTime;

	/**
	 * 版本号
	 */
	@Version
	@TableField("version")
	private Integer version;

}
