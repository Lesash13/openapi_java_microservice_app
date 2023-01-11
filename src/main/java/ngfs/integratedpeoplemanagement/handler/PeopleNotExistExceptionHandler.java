package ngfs.integratedpeoplemanagement.handler;

import ngfs.integratedpeoplemanagement.exception.PeopleNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Component
public class PeopleNotExistExceptionHandler implements ExceptionMapper<PeopleNotExistException> {
    private final static Logger LOGGER = LoggerFactory.getLogger(PeopleNotExistExceptionHandler.class);

    @Override
    public Response toResponse(PeopleNotExistException exception) {
        LOGGER.error(exception.getMessage());
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}