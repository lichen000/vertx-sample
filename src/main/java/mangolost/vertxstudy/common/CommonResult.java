package mangolost.vertxstudy.common;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by mangolost on 2017-04-24
 */
public class CommonResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code = 200;
    private String message = "OK";
    private Object data = null;
    private long ts = System.currentTimeMillis();

    public CommonResult() {

    }

    public CommonResult(int code, String message, Object data, long ts) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.ts = ts;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public CommonResult setData(Object data) {
        this.data = data;
        return this;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    /**
     *
     * @param code
     * @param message
     * @return
     */
    public CommonResult setCodeAndMessage(int code, String message) {
        this.code = code;
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}