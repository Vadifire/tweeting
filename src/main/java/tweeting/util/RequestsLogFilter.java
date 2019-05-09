package tweeting.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/*
 * Intercepts requests to add Logging context
 */

public class RequestsLogFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestsLogFilter.class);

    public void init(FilterConfig config) {

    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
             logger.warn("Log Filter only supports HTTP requests/responses");
             return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String transactionId = UUID.randomUUID().toString();

        MDC.put("transID", transactionId);
        MDC.put("remoteIP", httpRequest.getRemoteAddr());
        MDC.put("methodType", httpRequest.getMethod());
        MDC.put("requestURI", httpRequest.getRequestURI());
        MDC.put("protocol", httpRequest.getProtocol());
        if (!httpRequest.getParameterMap().isEmpty()){
            MDC.put("params", httpRequest.getParameterMap().toString() + " ");
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear(); // Reset context after every Request
        }

    }
}
