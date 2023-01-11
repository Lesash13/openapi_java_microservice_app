package ngfs.integratedpeoplemanagement.exception;

import javax.ws.rs.WebApplicationException;

public class InvalidIdException extends WebApplicationException {

    public InvalidIdException() {
    }

    @Override
    public String getMessage() {
        return "Invalid ID supplied";
    }
}
