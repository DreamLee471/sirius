package org.sirius.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Controller
@RequestMapping(value = "/")
public class SiriusController {
	
	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
	
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request){
		request.startAsync();
		final AsyncContext context = request.getAsyncContext();
		service.schedule(new Runnable() {
			
			@Override
			public void run() {
				try {
					context.getResponse().getOutputStream().write("hello".getBytes());
					context.getResponse().getOutputStream().flush();
					context.complete();
					System.out.println("dd");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 100, TimeUnit.MILLISECONDS);
		return null;
	}
	
	

}
