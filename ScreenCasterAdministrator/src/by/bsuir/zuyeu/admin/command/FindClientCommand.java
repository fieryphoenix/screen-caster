/**
 * 
 */
package by.bsuir.zuyeu.admin.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.admin.api.CommandType;
import by.bsuir.zuyeu.admin.model.CasterClient;
import by.bsuir.zuyeu.admin.model.CommandPacket;
import by.bsuir.zuyeu.admin.room.RoomDispatcher;

/**
 * @author Fieryphoenix
 * 
 */
public class FindClientCommand implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(FindClientCommand.class);

    @Override
    public CommandPacket execute(CommandPacket packet) {
	logger.info("execute() - start;");
	CommandPacket result = null;
	final String roomNumber = (String) packet.getData();
	final CasterClient client = RoomDispatcher.getInstance().findClient(roomNumber);
	if (client != null) {
	    result = new CommandPacket();
	    result.setClient(packet.getClient());
	    result.setCommandType(CommandType.REGISTER_SUCCESS);
	    final Object[] data = new Object[2];
	    data[0] = client.getHost();
	    data[1] = client.getPort();
	    result.setData(data);
	} else {
	    result = new CommandPacket();
	    result.setClient(packet.getClient());
	    result.setData(roomNumber);
	    result.setCommandType(CommandType.ROOM_NOT_REGISTERED);
	}
	logger.info("execute() - end: result = {}", result);
	return result;
    }

}
