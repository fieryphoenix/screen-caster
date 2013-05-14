/**
 * 
 */
package by.bsuir.zuyeu.admin.command;

import java.util.HashMap;
import java.util.Map;

import by.bsuir.zuyeu.admin.api.CommandType;

/**
 * @author Fieryphoenix
 * 
 */
public final class CommandDispacther {
    static class InstanceHolder {
	public static final CommandDispacther INSTANCE = new CommandDispacther();
    }

    private Map<CommandType, ICommand<? extends Object>> commandHelper;

    private CommandDispacther() {
	init();
    }

    private void init() {
	commandHelper = new HashMap<>(CommandType.values().length);
	commandHelper.put(CommandType.REGISTER_NEW_ROOM, new RegisterCommand());
    }

    public ICommand getCommand(final CommandType commandType) {
	return commandHelper.get(commandType);
    }

}
