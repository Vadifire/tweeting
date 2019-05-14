package tweeting.util;

import org.junit.Before;
import org.junit.Test;
import tweeting.services.TwitterErrorCode;
import tweeting.services.TwitterServiceException;

import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

public class TwitterExceptionHandlerTest {

    // Mocked TwitterException
    TwitterServiceException mockedException;

    // Class under test
    TwitterExceptionHandler exceptionHandler;

    // Dummy data
    String attemptedAction;

    @Before
    public void setUp() {
        mockedException = mock(TwitterServiceException.class);
        attemptedAction = "some action";

        exceptionHandler = new TwitterExceptionHandler(attemptedAction);
    }

    @Test
    public void testBadAuthError() {
        when(mockedException.getErrorCode()).thenReturn(TwitterErrorCode.BAD_AUTH_DATA.getCode());

        Response response = exceptionHandler.catchTwitterException(mockedException);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(attemptedAction), response.getEntity().toString());
    }

    @Test
    public void testCouldNotAuthError() {
        when(mockedException.getErrorCode()).thenReturn(TwitterErrorCode.COULD_NOT_AUTH.getCode());

        Response response = exceptionHandler.catchTwitterException(mockedException);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(attemptedAction), response.getEntity().toString());
    }

    @Test
    public void testNetworkError() {
        when(mockedException.getErrorCode()).thenReturn(-1); // Test some other error code
        when(mockedException.isCausedByNetworkIssue()).thenReturn(true); // Don't rely on Twitter4J impl.

        Response response = exceptionHandler.catchTwitterException(mockedException);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getNetworkErrorMessage(attemptedAction), response.getEntity().toString());
    }

    @Test
    public void testOtherError() {
        String dummyCauseMessage = "Some error cause";

        when(mockedException.getErrorCode()).thenReturn(-1); // Test some other error code
        when(mockedException.isCausedByNetworkIssue()).thenReturn(false); // Don't rely on Twitter4J impl.
        when(mockedException.getMessage()).thenReturn(dummyCauseMessage);

        Response response = exceptionHandler.catchTwitterException(mockedException);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals(ResponseUtil.getOtherErrorMessage(attemptedAction, dummyCauseMessage),
                response.getEntity().toString());
    }

    @Test
    public void testGeneralException() {
        RuntimeException dummyRuntimeException = mock(RuntimeException.class);

        when(mockedException.getErrorCode()).thenThrow(dummyRuntimeException);

        Response actualResponse = exceptionHandler.catchTwitterException(mockedException);

        assertNotNull(actualResponse);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), actualResponse.getStatus()); // Verify code
        assertEquals(ResponseUtil.getServiceUnavailableErrorMessage(attemptedAction),
                actualResponse.getEntity().toString());

    }


}
