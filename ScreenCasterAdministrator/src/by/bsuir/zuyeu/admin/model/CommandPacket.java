/**
 * 
 */
package by.bsuir.zuyeu.admin.model;

import by.bsuir.zuyeu.admin.api.CommandType;

/**
 * @author Fieryphoenix
 * 
 */
public final class CommandPacket {
    private CommandType commandType;
    private CasterClient client;

    public CommandType getCommandType() {
	return commandType;
    }

    public void setCommandType(CommandType commandType) {
	this.commandType = commandType;
    }

    public CasterClient getClient() {
	return client;
    }

    public void setClient(CasterClient client) {
	this.client = client;
    }

}
