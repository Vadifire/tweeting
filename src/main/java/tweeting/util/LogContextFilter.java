package tweeting.util;

import org.slf4j.MDC;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

/*
 * Intercepts requests to add Logging context
 */

public class LogContextFilter implements Filter {

    // TODO: FilterConfig useful/needed?

    public void init(FilterConfig config){
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String transactionId = UUID.randomUUID().toString();

        MDC.put("TRANS_ID", transactionId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear(); // Reset context after every Request
        }

    }
}
