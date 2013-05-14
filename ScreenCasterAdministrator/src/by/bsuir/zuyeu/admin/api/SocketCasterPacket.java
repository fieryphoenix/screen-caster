/**
 * 
 */
package by.bsuir.zuyeu.admin.api;

import java.io.Serializable;

/**
 * @author Fieryphoenix
 * 
 */
public class SocketCasterPacket implements Serializable {
    private static final long serialVersionUID = -3705318711210073690L;

    private CommandType commandType;
    private Object data;

    public CommandType getCommandType() {
	return commandType;
    }

    public void setCommandType(CommandType commandType) {
	this.commandType = commandType;
    }

    public Object getData() {
	return data;
    }

    public void setData(Object data) {
	this.data = data;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = (prime * result) + ((commandType == null) ? 0 : commandType.hashCode());
	result = (prime * result) + ((data == null) ? 0 : data.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SocketCasterPacket other = (SocketCasterPacket) obj;
	if (commandType != other.commandType) {
	    return false;
	}
	if (data == null) {
	    if (other.data != null) {
		return false;
	    }
	} else if (!data.equals(other.data)) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "SocketCasterPacket [commandType=" + commandType + ", data=" + data + "]";
    }

}
