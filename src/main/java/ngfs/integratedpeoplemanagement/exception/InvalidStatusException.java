package ngfs.integratedpeoplemanagement.exception;

import javax.ws.rs.WebApplicationException;

public class InvalidStatusException extends WebApplicationException {

    public InvalidStatusException() {
    }

    @Override
    public String getMessage() {
        return "Only alive/not alive values are supported for status";
    }
}
