package shared.dto;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private CommandType type;
    private final Object[] args;


    public Request(CommandType type, Object[] args) {
        this.type = type;
        this.args = args;
    }

    public Request(CommandType type) {
        this(type, null);
    }

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }



    public Object getArgs() {
        return args;
    }


    @Override
    public String toString() {
        return "Request{" +
                "type=" + type +
                ", args=" + args +
                '}';
    }
}
