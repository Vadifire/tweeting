package tweeting.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/*
 * Intercepts requests to add Logging context
 */

public class LogFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);

    public void init(FilterConfig config) {

    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
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
            if (!httpRequest.getParameterMap().isEmpty()) {
                MDC.put("params", httpRequest.getParameterMap().toString() + " ");
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            MDC.clear();
        }

    }
}
