/**
 * 
 */
package by.bsuir.zuyeu.admin.command;

import by.bsuir.zuyeu.admin.api.CommandType;

/**
 * @author Fieryphoenix
 * 
 */
public interface ICommand<T> {
    public CommandType execute(final T arg);
}
