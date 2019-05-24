package tweeting.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.MDC;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MDC.class)
public class LogFilterTest {

    // Class under test
    LogFilter filter;

    // Params
    HttpServletRequest request;
    HttpServletResponse response;
    FilterChain chain;


    // Dummy vars
    Map<String, String[]> parameterMap;
    String dummyParamKey;
    String[] dummyParamVal;
    String dummyRemoteIP;
    String dummyMethodType;
    String dummyRequestURI;
    String dummyProtocol;

    @Before
    public void setUp() throws Exception {
        filter = new LogFilter();

        PowerMockito.spy(MDC.class);
        PowerMockito.doNothing().when(MDC.class, "clear"); // Allows testing of MDC before clear
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);

        dummyRemoteIP = "ip";
        dummyMethodType = "method";
        dummyRequestURI = "uri";
        dummyProtocol = "protocol";

        parameterMap = new HashMap<>();
        dummyParamKey = "key";
        dummyParamVal = new String[]{"val"};
        parameterMap.put(dummyParamKey, dummyParamVal);

        when(request.getRemoteAddr()).thenReturn(dummyRemoteIP);
        when(request.getMethod()).thenReturn(dummyMethodType);
        when(request.getRequestURI()).thenReturn(dummyRequestURI);
        when(request.getProtocol()).thenReturn(dummyProtocol);
    }

    @Test
    public void testMDCSetupNoParams() throws IOException, ServletException {
        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertEquals(dummyRemoteIP, MDC.get(LogFilter.REMOTE_IP_KEY));
        assertEquals(dummyMethodType, MDC.get(LogFilter.METHOD_TYPE_KEY));
        assertEquals(dummyRequestURI, MDC.get(LogFilter.REQUEST_URI_KEY));
        assertEquals(dummyProtocol, MDC.get(LogFilter.PROTOCOL_KEY));
        assertNotNull(MDC.get(LogFilter.TRANS_ID_KEY));
    }

    @Test
    public void testMDCParams() {
        when(request.getParameterMap()).thenReturn(parameterMap);

        filter.doFilter(request, response, chain);
        assertEquals(parameterMap.toString() + " ", MDC.get(LogFilter.PARAMS_KEY));
    }

}
