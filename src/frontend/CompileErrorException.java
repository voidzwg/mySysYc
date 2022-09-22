package frontend;

public class CompileErrorException extends Exception {
    public CompileErrorException() {
        super();
    }

    public CompileErrorException(Error e) {
        super(e.getDescription());
    }
}
