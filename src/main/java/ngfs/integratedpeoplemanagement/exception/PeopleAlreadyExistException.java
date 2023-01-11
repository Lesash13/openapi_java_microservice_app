package ngfs.integratedpeoplemanagement.exception;

public class PeopleAlreadyExistException extends Exception {

    private final String firstname;

    private final String lastname;

    public PeopleAlreadyExistException(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    @Override
    public String getMessage() {
        return "Person " + firstname + " " + lastname + " already exists";
    }

}
