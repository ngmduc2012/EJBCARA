package cmc.vn.ejbca.RA.response;

public enum ResponseStatus {

    DO_SERVICE_SUCCESSFUL(1000, "Success"),
    UNHANDLED_ERROR(1004, "Unhandled error"),
    OBJECT_INVALID(1005, "Object invalid"),
    CALL_HTTP_HAS_ERROR(1006, "The system has error, please try again later"),
    KEY_CONFIG_NOT_FOUND(1007, "Config Not Found"),
    JOB_NOT_FOUND(1008, "Job Not Found"),
    FIXED_DELAY_INVALID(1009, "Job not fixed delay type"),
    FIXED_TIME_INVALID(1010, "Job not fixed time type"),
    UNAUTHORIZE(401, "401 Unauthorized")
    ;

    public int code;
    public String message;
    private String messageFormat;

    ResponseStatus(int code, String message, String messageFormat) {
        this.code = code;
        this.message = message;
        this.messageFormat = messageFormat;
    }

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseStatus formatMessage(Object... str) {
        if (this.messageFormat != null) {
            this.message = String.format(this.messageFormat, str);
        }
        return this;
    }

    public ResponseStatus[] getListResponseStatus() {
        return ResponseStatus.values();
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageFormat() {
        return messageFormat;
    }
}