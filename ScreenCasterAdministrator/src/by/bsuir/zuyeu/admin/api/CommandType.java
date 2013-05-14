/**
 * 
 */
package by.bsuir.zuyeu.admin.api;

/**
 * @author Fieryphoenix
 * 
 */
public enum CommandType implements Value {
    REGISTER_NEW_ROOM(1), //
    UNREGISTER_ROOM(2), //
    GET_ROOM_ADDRESS(3), //
    ROOM_NOT_REGISTERED(4), //
    START_PUNCHING(5), //
    SUCCESS_PUNCHING(6), //
    STOP_PUNCHING(7), //
    REGISTER_SUCCESS(8), //
    REGISTER_FAIL(9), //
    ADDRESS_EXIST(10);

    private long value;

    CommandType(final long value) {
	this.value = value;
    }

    @Override
    public long getValue() {
	return value;
    }

    public static CommandType valueOf(final long value) {
	CommandType result = null;
	for (final CommandType candidate : CommandType.values()) {
	    if (candidate.getValue() == value) {
		result = candidate;
		break;
	    }
	}
	return result;
    }

}
