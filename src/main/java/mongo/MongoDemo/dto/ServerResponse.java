package mongo.MongoDemo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record ServerResponse<T>(T payload, CustomError error) {
    @JsonIgnore
    public boolean isOk(){ return error != null;}

    public static <T> ServerResponse<T> ok(T payload) { return new ServerResponse<>(payload, null);}
    public static ServerResponse<Void> ok() { return new ServerResponse<>(null, null);}

    public static ServerResponse<Void> error(String message, String code){ return new ServerResponse<>(null, new CustomError(message, code));}

    public static ServerResponse<Void> error(CustomError error){ return new ServerResponse<>(null, error);}
}
