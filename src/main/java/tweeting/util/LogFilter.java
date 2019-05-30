package tweeting.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
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

    /* MDC Keys */
    public static final String TRANS_ID_KEY = "transID";
    public static final String REMOTE_IP_KEY = "remoteIP";
    public static final String METHOD_TYPE_KEY = "methodType";
    public static final String REQUEST_URI_KEY = "requestURI";
    public static final String PROTOCOL_KEY = "protocol";
    public static final String PARAMS_KEY = "params";

    @Inject
    public LogFilter() {

    }

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

            MDC.put(TRANS_ID_KEY, transactionId);
            MDC.put(REMOTE_IP_KEY, httpRequest.getRemoteAddr());
            MDC.put(METHOD_TYPE_KEY, httpRequest.getMethod());
            MDC.put(REQUEST_URI_KEY, httpRequest.getRequestURI());
            MDC.put(PROTOCOL_KEY, httpRequest.getProtocol());
            if (!httpRequest.getParameterMap().isEmpty()) {
                MDC.put(PARAMS_KEY, httpRequest.getParameterMap().toString() + " ");
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }
}
