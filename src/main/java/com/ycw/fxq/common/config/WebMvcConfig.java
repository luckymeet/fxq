package com.ycw.fxq.common.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ycw.fxq.common.interceptor.web.PageParamsMethodArgumentResolver;

/**
 * @类名称 WebMvcConfig.java
 * @类描述 Web拦截器配置类
 * @作者 yuminjun yuminjun@lexiangbao.com
 * @创建时间 2019年9月12日 下午3:24:51
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
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private PageParamsMethodArgumentResolver pageParamsMethodArgumentResolver;

	@Autowired
	private StaticPagePathFinder staticPagePathFinder;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(pageParamsMethodArgumentResolver);// 分页参数处理
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		try {
			registry.addViewController("/").setViewName("static/index");
			registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
			for (StaticPagePathFinder.PagePaths pagePaths : staticPagePathFinder.findPath()) {
				String urlPath = pagePaths.getUrlPath();
				registry.addViewController(urlPath).setViewName("static/" + pagePaths.getFilePath());
				if (!urlPath.isEmpty()) {
					registry.addViewController(urlPath).setViewName("static/" + pagePaths.getFilePath());
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to locate static pages:" + e.getMessage(), e);
		}
	}

}
