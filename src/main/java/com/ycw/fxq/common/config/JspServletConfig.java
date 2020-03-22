package com.ycw.fxq.common.config;

import javax.servlet.annotation.WebServlet;

import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@WebServlet(urlPatterns = "*.jsp", name = "JspServlet")
public class JspServletConfig extends org.apache.jasper.servlet.JspServlet {

	private static final long serialVersionUID = 1L;

}
