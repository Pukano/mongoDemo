package mongo.MongoDemo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum EventType {
    DELETE_USER,
    CREATE_USER;
    @JsonValue
    public String getValue() {
        return name();
    }

    @JsonCreator
    public static EventType create(String scanEventStatus) {
        for (EventType e : EventType.values()) {
            if (StringUtils.equalsIgnoreCase(e.getValue(), scanEventStatus)) {
                return e;
            }
        }
        return null;
    }
}
