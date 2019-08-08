package utils;

import java.io.IOException;
/*

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginFilter implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
		Object userName = httpRequest.getSession().getAttribute("userName");
		if(httpRequest.getRequestURL().toString().contains("UserLogin.jsp")){
			chain.doFilter(request, response);
		}
		else if(IsNull.isNull(userName) || IsNull.isNull(userName.toString())){
			httpServletResponse.sendRedirect("http://localhost:8080/WebBuilde/Login/UserLogin.jsp");
		}
		else{
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
	}
}
*/
