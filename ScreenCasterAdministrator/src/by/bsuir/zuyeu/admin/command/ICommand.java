/**
 * 
 */
package by.bsuir.zuyeu.admin.command;

import by.bsuir.zuyeu.admin.model.CommandPacket;

/**
 * @author Fieryphoenix
 * 
 */
public interface ICommand {
    public CommandPacket execute(CommandPacket packet);
}
