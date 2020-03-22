package com.ycw.fxq.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @类名称 SpringUtils.java
 * @类描述 Spring工具类
 * @作者 yuminjun yuminjun@lexiangbao.com
 * @创建时间 2019年9月12日 下午5:15:50
 * @版本 1.00
 *
 * @修改记录
 * <pre>
 *     版本                       修改人 		修改日期 		 修改内容描述
 *     ----------------------------------------------
 *     1.00 	yuminjun 	2019年9月12日
 *     ----------------------------------------------
 * </pre>
 */
@Component
public class SpringUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (applicationContext == null) {
			SpringUtils.applicationContext = applicationContext;
		}

	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static Object getBean(String name) {
		return applicationContext == null ? null : getApplicationContext().getBean(name);
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext == null ? null : getApplicationContext().getBean(clazz);
	}

	public static <T> T getBean(String name, Class<T> clazz) {
		return applicationContext == null ? null : getApplicationContext().getBean(name, clazz);
	}
}
