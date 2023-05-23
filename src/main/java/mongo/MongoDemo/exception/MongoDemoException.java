package mongo.MongoDemo.exception;

public class MongoDemoException extends RuntimeException {
    private final String code;

    public MongoDemoException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
