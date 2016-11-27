package org.sirius.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sirius.core.pull.PullCore;
import org.sirius.core.pull.PullKey;
import org.sirius.core.pull.PullKeyType;
import org.sirius.core.store.impl.StandardTransientStore;
import org.sirius.domain.Config;

public class AsyncServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9068855300425242188L;
	private static StandardTransientStore store = new StandardTransientStore(null);
	
	
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
			PullCore.addLongPull(new PullKey(namespace+"_"+name, PullKeyType.NAMESPACE_NAME), req);
		}else if("store".equals(type)){
			String content = req.getParameter("content");
			Config config = new Config(namespace, name, content);
			store.store(config);
		}
		
	}

}
