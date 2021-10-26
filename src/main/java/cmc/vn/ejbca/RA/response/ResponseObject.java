package cmc.vn.ejbca.RA.response;

public class ResponseObject<T> {
    private Boolean result;
    private Integer code;
    private String message;
    private T data;

    public ResponseObject(Boolean result, ResponseStatus status) {
        this.result = result;
        this.code = status.getCode();
        this.message = status.getMessage();
    }

    public ResponseObject(Boolean result, ResponseStatus status, String message) {
        this.result = result;
        this.code = status.getCode();
        this.message = message;
    }

    public ResponseObject(Boolean result, ResponseStatus status, T data) {
        this.result = result;
        this.code = status.getCode();
        this.message = status.getMessage();
        this.data = data;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}