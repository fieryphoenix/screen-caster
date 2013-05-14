/**
 * 
 */
package by.bsuir.zuyeu.exeption;

/**
 * @author Fieryphoenix
 * 
 */
public enum ErrorMessages {
    APP_INIT_ERROR("Initialization Error\n Try to restart program."), //
    SERVER_UNAVAILABLE("Remote server is unavailable."), //
    SESSION_BUSY("Server is busy. Please, Try again later."), //
    BLANK_FIELD("There's no room number!"), //
    NO_SUCH_SESSION("There's no session with this room number");
    private final String message;

    private ErrorMessages(final String message) {
	this.message = message;
    }

    public String getMessage() {
	return message;
    }
}
