package ngfs.integratedpeoplemanagement.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.net.SocketTimeoutException;

@Component
public class SocketTimeoutExceptionHandler implements ExceptionMapper<ProcessingException> {
    private final static Logger LOGGER = LoggerFactory.getLogger(SocketTimeoutExceptionHandler.class);

    @Override
    public Response toResponse(ProcessingException exception) {
        LOGGER.error(exception.getMessage());
        if (exception.getCause() instanceof SocketTimeoutException) {
            return Response.status(Response.Status.GATEWAY_TIMEOUT).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
