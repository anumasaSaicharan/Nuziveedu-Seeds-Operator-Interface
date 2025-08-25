package com.nsl.operatorInterface;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter implements Filter {
	//private static final Logger LOG = LogManager.getLogger(CORSFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
		response.setHeader("Access-Control-Allow-Headers",
"Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, deviceType, userId,password,mobileNumber,Accept,appVersionCode,appVersionName,deviceId,deviceToken,fcmToken,mobileNumber,userId,userName,roleId,zoneId,stateId,regionId,territoryId,districtId,headquarterId,authType,reason");
		if (request.getMethod().equals("OPTIONS")) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
