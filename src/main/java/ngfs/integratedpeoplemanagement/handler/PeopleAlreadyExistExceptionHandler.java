package ngfs.integratedpeoplemanagement.handler;

import ngfs.integratedpeoplemanagement.exception.PeopleAlreadyExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Component
public class PeopleAlreadyExistExceptionHandler implements ExceptionMapper<PeopleAlreadyExistException> {
    private final static Logger LOGGER = LoggerFactory.getLogger(PeopleAlreadyExistExceptionHandler.class);

    @Override
    public Response toResponse(PeopleAlreadyExistException exception) {
        LOGGER.error(exception.getMessage());
        return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
    }
}