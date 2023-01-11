package ngfs.integratedpeoplemanagement.exception;

public class PeopleNotExistException extends Exception {

    public PeopleNotExistException() {
    }

    @Override
    public String getMessage() {
        return "No people with required parameters exist in ngfs.integratedpeoplemanagement.repository";
    }
}
