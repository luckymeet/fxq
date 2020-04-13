package com.ycw.fxq.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;

import com.ycw.fxq.common.exception.MsgException;

public class BeanHandleUtils extends BeanUtils {

	/**
	 * @方法名称 beanCopy
	 * @功能描述 bean转换方法
	 * @作者     yuminjun
	 * @创建时间 2019年8月29日 下午6:00:11
	 * @param <T>
	 * @param source
	 * @param targetClass
	 * @return
	 * @throws MsgException
	 */
	public static <T> T beanCopy(Object source, Class<T> targetClass) {
		if (null == source) {
			return null;
		}
		T target = null;
		try {
			target = targetClass.newInstance();
			copyProperties(source, target);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("bean转换异常：【" + source.getClass().getName() + "】转换成【" + targetClass.getName() + "】失败", e);
		}
		return target;
	}

	/**
	 * @方法名称 listCopy
	 * @功能描述 bean转换方法
	 * @作者     yuminjun
	 * @创建时间 2019年8月29日 下午6:00:11
	 * @param source
	 * @param targetClass
	 * @return
	 * @throws MsgException
	 */
    public static <E> List<E> listCopy(List<?> source, Class<E> targetBean) {
        try {
            if (null == source || source.isEmpty()) {
                return Collections.emptyList();
            }
            List<E> target = new ArrayList<E>(source.size());
            for (Object o : source) {
                E e = targetBean.newInstance();
                BeanUtils.copyProperties(o, e);
                target.add(e);
            }
            return target;
        } catch (Exception e) {
            return Collections.emptyList();
        }

    }

	/**
	 * @方法名称 mapToBean
	 * @功能描述 map转换成bean方法
	 * @作者     yuminjun
	 * @创建时间 2019年8月30日 下午1:54:54
	 * @param <T>
	 * @param map
	 * @param targetClass
	 * @return
	 * @throws MsgException
	 */
	public static <T> T mapToBean(Map<String, Object> map, Class<T> targetClass) {
		if (map == null) {
			return null;
		}
		T obj = null;
		try {
			obj = targetClass.newInstance();
			org.apache.commons.beanutils.BeanUtils.populate(obj, map);
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
			throw new RuntimeException("map转换异常：【" + map.getClass().getName() + "】转换成【" + targetClass.getName() + "】失败", e);
		}
		return obj;
	}

	/**
	 * @方法名称 beanToMap
	 * @功能描述 bean转换成map方法
	 * @作者     yuminjun
	 * @创建时间 2019年8月30日 下午1:55:26
	 * @param source
	 * @return
	 * @return
	 */
	public static Map<String, Object> beanToMap(Object source) {
		if (source == null) {
			return null;
		}
		return BeanMap.create(source);
	}

}
