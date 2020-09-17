package com.disney.miguelmunoz.challenge.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.RequestFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T04:00:38.477Z")

@Component
public class ApiOriginFilter implements javax.servlet.Filter {
  private static final Logger log = LoggerFactory.getLogger(ApiOriginFilter.class);
  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain) throws IOException, ServletException {
    HttpServletResponse res = (HttpServletResponse) response;
    Enumeration<String> aNames = request.getAttributeNames();
    
    if (log.isDebugEnabled()) {
      if (request instanceof RequestFacade) {
        RequestFacade facade = (RequestFacade) request;
        final String requestURI = facade.getRequestURI();
        // filter out calls to documentation
        if (!requestURI.contains("springfox")) {
          log.debug("Request URI: {}", charFilter(requestURI));
        }
      } else {
        log.debug("Request of {}", request.getClass());
      }
    }

    res.addHeader("Access-Control-Allow-Origin", "*");
    res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
    res.addHeader("Access-Control-Allow-Headers", "Content-Type");
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  /**
   *  Replace %7B and %7D with curly braces in url paths
   * @param request The request string
   * @return the corrected String
   */
  private static final String charFilter(final String request) {
    return request
        .replaceAll("%7B", "{")
        .replaceAll("%7D", "}");
  }
}
