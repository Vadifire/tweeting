package tweeting.util;

import org.junit.Before;
import org.junit.Test;
import twitter4j.*;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

public class ResponseUtilTest {

    ResponseUtil responseUtil;
    String attemptedAction;

    @Before
    public void setUp() {
        responseUtil = new ResponseUtil(attemptedAction);
        attemptedAction = "some action";
    }

    @Test
    public void testBadAuthError() {

        Exception dummyCause = new Exception();
        TwitterException authException = spy(new TwitterException("Dummy String", dummyCause,
                Response.Status.UNAUTHORIZED.getStatusCode())); // Spy needed to stub

        when(authException.getErrorCode()).thenReturn(ResponseUtil.TwitterErrorCode.BAD_AUTH_DATA.getCode());

        Response response = responseUtil.catchTwitterException(authException);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(responseUtil.getAuthFailError(), response.getEntity().toString());
    }

    @Test
    public void testCouldNotAuthError() {

        Exception dummyCause = new Exception();
        TwitterException authException = spy(new TwitterException("Dummy String", dummyCause,
                Response.Status.UNAUTHORIZED.getStatusCode())); // Spy needed to stub

        when(authException.getErrorCode()).thenReturn(ResponseUtil.TwitterErrorCode.COULD_NOT_AUTH.getCode());

        Response response = responseUtil.catchTwitterException(authException);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(responseUtil.getAuthFailError(), response.getEntity().toString());
    }

    @Test
    public void testNetworkError() {
        IOException networkCause = new IOException();
        TwitterException networkException = spy(new TwitterException("Dummy String", networkCause, 0));

        when(networkException.isCausedByNetworkIssue()).thenReturn(true); // Don't rely on Twitter4J impl.

        Response response = responseUtil.catchTwitterException(networkException);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(responseUtil.getNetworkError(), response.getEntity().toString());
    }

    @Test
    public void testOtherError() {
        TwitterException dummyException = new TwitterException("Dummy String", new Exception(), 0);

        Response response = responseUtil.catchTwitterException(dummyException);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(responseUtil.getOtherError(dummyException.getErrorMessage()), response.getEntity().toString());
    }


}
