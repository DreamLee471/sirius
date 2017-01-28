package org.sirius.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sirius.core.pull.PullCore;
import org.sirius.domain.Config;

public class AsyncServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9068855300425242188L;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req,resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String namespace = req.getParameter("namespace");
		String name = req.getParameter("name");
		
		String type = req.getParameter("type");
		if("query".equals(type)){
			PullCore.addLongPull(req, resp);
		}else if("store".equals(type)){
			String content = req.getParameter("content");
			Config config = new Config(namespace, name, content);
			PullCore.store(config);
		}else if("alive".equals(type)){
			resp.getWriter().write("ok");
		}
		
	}
	
	
	@Override
	public void destroy() {
		PullCore.destory();
	}

}
