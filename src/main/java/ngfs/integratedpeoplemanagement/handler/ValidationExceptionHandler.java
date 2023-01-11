package ngfs.integratedpeoplemanagement.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Component
public class ValidationExceptionHandler implements ExceptionMapper<ValidationException> {
    private final static Logger logger = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    private final static String RESPONSE_MESSAGE = "Validation exception - ";

    @Override
    public Response toResponse(ValidationException exception) {
        logger.error(RESPONSE_MESSAGE + exception.getMessage());
        return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
    }
}
