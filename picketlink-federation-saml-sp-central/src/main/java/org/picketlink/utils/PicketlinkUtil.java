package org.picketlink.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author Rodrigo Ramalho da Silva
 * 		   hodrigohamalho@gmail.com
 */
public class PicketlinkUtil implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		filterChain.doFilter(request, response);
	}

	@Override
	public void destroy() {}
}
