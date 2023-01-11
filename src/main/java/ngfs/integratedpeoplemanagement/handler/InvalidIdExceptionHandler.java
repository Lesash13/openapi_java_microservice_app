package ngfs.integratedpeoplemanagement.handler;

import ngfs.integratedpeoplemanagement.exception.InvalidIdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Component
public class InvalidIdExceptionHandler implements ExceptionMapper<InvalidIdException> {
    private final static Logger LOGGER = LoggerFactory.getLogger(InvalidIdExceptionHandler.class);

    @Override
    public Response toResponse(InvalidIdException exception) {
        LOGGER.error(exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}