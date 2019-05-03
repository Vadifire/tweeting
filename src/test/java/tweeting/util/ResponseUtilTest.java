package tweeting.util;

import org.junit.Before;
import org.junit.Test;
import twitter4j.*;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

public class ResponseUtilTest {

    // Mocked TwitterException
    TwitterException mockedException;

    // Class under test
    ResponseUtil responseUtil;

    String attemptedAction;

    @Before
    public void setUp() {
        mockedException = mock(TwitterException.class);

        responseUtil = new ResponseUtil(attemptedAction); // Provide dummy resUtil for TwitterEx
        attemptedAction = "some action";
    }

    @Test
    public void testBadAuthError() {
        when(mockedException.getErrorCode()).thenReturn(ResponseUtil.TwitterErrorCode.BAD_AUTH_DATA.getCode());

        Response response = responseUtil.catchTwitterException(mockedException);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(responseUtil.getAuthFailErrorMessage(), response.getEntity().toString());
    }

    @Test
    public void testCouldNotAuthError() {
        when(mockedException.getErrorCode()).thenReturn(ResponseUtil.TwitterErrorCode.COULD_NOT_AUTH.getCode());

        Response response = responseUtil.catchTwitterException(mockedException);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(responseUtil.getAuthFailErrorMessage(), response.getEntity().toString());
    }

    @Test
    public void testNetworkError() {
        when(mockedException.getErrorCode()).thenReturn(-1); // Test some other error code
        when(mockedException.isCausedByNetworkIssue()).thenReturn(true); // Don't rely on Twitter4J impl.

        Response response = responseUtil.catchTwitterException(mockedException);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(responseUtil.getNetworkErrorMessage(), response.getEntity().toString());
    }

    @Test
    public void testOtherError() {
        String dummyCauseMessage = "Some error cause";

        when(mockedException.getErrorCode()).thenReturn(-1); // Test some other error code
        when(mockedException.isCausedByNetworkIssue()).thenReturn(false); // Don't rely on Twitter4J impl.
        when(mockedException.getErrorMessage()).thenReturn(dummyCauseMessage);

        Response response = responseUtil.catchTwitterException(mockedException);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(responseUtil.getOtherErrorMessage(dummyCauseMessage), response.getEntity().toString());
    }


}
