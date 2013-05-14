/**
 * 
 */
package by.bsuir.zuyeu.exeption;

/**
 * @author Fieryphoenix
 * 
 */
public enum ErrorMessages {
    APP_INIT_ERROR("Initialization Error\n Try to restart program."), SERVER_UNAVAILABLE("Remote server is unavailable.");
    private final String message;

    private ErrorMessages(final String message) {
	this.message = message;
    }

    public String getMessage() {
	return message;
    }
}
