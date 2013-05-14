package by.bsuir.zuyeu.admin.command;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.admin.api.CommandType;
import by.bsuir.zuyeu.admin.model.CasterClient;
import by.bsuir.zuyeu.admin.model.CommandPacket;
import by.bsuir.zuyeu.admin.room.RoomDispatcher;

final class RegisterCommand implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(RegisterCommand.class);

    @Override
    public CommandPacket execute(final CommandPacket packet) {
	logger.info("execute() - start;");
	final CasterClient client = packet.getClient();
	// save address of future translation
	final Object[] data = (Object[]) packet.getData();
	client.setHost((String) data[0]);
	client.setPort((int) data[1]);
	CommandPacket result = null;
	final String roomNumber = RoomDispatcher.getInstance().registerClient(client);
	if (StringUtils.isNotBlank(roomNumber)) {
	    result = new CommandPacket();
	    result.setClient(client);
	    result.setCommandType(CommandType.REGISTER_SUCCESS);
	    result.setData(roomNumber);
	} else {
	    result = new CommandPacket();
	    result.setClient(client);
	    result.setCommandType(CommandType.REGISTER_FAIL);
	}
	logger.info("execute() - end: result = {}", result);
	return result;
    }

}
