package ngfs.integratedpeoplemanagement.handler;

import ngfs.integratedpeoplemanagement.exception.InvalidStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Component
public class InvalidStatusExceptionHandler implements ExceptionMapper<InvalidStatusException> {
    private final static Logger LOGGER = LoggerFactory.getLogger(PeopleAlreadyExistExceptionHandler.class);

    @Override
    public Response toResponse(InvalidStatusException exception) {
        LOGGER.error(exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
