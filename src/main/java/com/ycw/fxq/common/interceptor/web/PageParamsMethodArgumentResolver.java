package com.ycw.fxq.common.interceptor.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.ycw.fxq.common.page.PageParams;

/**
 * @类名称 PageParamsMethodArgumentResolver.java
 * @类描述 分页方法参数处理
 * @作者 yuminjun yuminjun@lexiangbao.com
 * @创建时间 2019年9月12日 下午3:25:22
 * @版本 1.00
 *
 * @修改记录
 *
 *       <pre>
 *     版本                       修改人 		修改日期 		 修改内容描述
 *     ----------------------------------------------
 *     1.00 	yuminjun 	2019年9月12日
 *     ----------------------------------------------
 *       </pre>
 */
@Component
public class PageParamsMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().isAssignableFrom(PageParams.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		/* json类型的请求不处理 */
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		if (request.getContentType() != null && request.getContentType().contains("application/json")) {
			return null;
		}

		/* 组装分页参数 */
		String pageNum = webRequest.getParameter("pageNum");
		String pageSize = webRequest.getParameter("pageSize");
		PageParams pageParams = null;
		if(StringUtils.isNotBlank(pageNum) && StringUtils.isNotBlank(pageSize)) {
			pageParams = new PageParams(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
		}

		return pageParams;
	}

}
