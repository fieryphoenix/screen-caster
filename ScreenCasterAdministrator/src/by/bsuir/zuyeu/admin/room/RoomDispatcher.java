/**
 * 
 */
package by.bsuir.zuyeu.admin.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.admin.model.CasterClient;

/**
 * @author Fieryphoenix
 * 
 */
public final class RoomDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(RoomDispatcher.class);
    public static final int MAX_ROOMS = 100;

    private Map<String, CasterClient> bookedRooms;
    private List<String> hostedRooms;

    private static final class InstanceHolder {
	private static final RoomDispatcher DISPATCHER = new RoomDispatcher();
    }

    private RoomDispatcher() {
	init();
    }

    public static RoomDispatcher getInstance() {
	return InstanceHolder.DISPATCHER;
    }

    private void init() {
	logger.trace("init() - start;");
	bookedRooms = new HashMap<>();
	hostedRooms = new ArrayList<>();
	// TODO: need to perform new algorithm
	long roomNumber = 111111111;
	final long skip = 35753;
	for (int i = 0; i < MAX_ROOMS; i++) {
	    final int part1 = (int) (roomNumber % 1000);
	    final int part2 = (int) ((roomNumber / 1000) % 1000);
	    final int part3 = (int) (roomNumber / 1000000);
	    final String nextRoomNumber = String.format("%d-%d-%d", part1, part2, part3);
	    hostedRooms.add(nextRoomNumber);
	    logger.debug("next room number = {}", nextRoomNumber);
	    roomNumber += skip;
	}
	logger.trace("init() - end;");
    }

    public String registerClient(final CasterClient client) {
	logger.info("registerClient() - start;");
	String roomNumber = null;
	for (final String candidateRoom : hostedRooms) {
	    if (!bookedRooms.containsKey(candidateRoom)) {
		bookedRooms.put(candidateRoom, client);
		roomNumber = candidateRoom;
		break;
	    }
	}
	logger.info("registerClient() - end;");
	return roomNumber;
    }

    public void freeRoom(final String roomNumber) {
	logger.info("freeRoom() - start;");
	if (bookedRooms.containsKey(roomNumber)) {
	    bookedRooms.remove(roomNumber);
	}
	logger.info("freeRoom() - end;");
    }

    public CasterClient findClient(final String roomNumber) {
	logger.info("findClient() - start;");
	final CasterClient client = bookedRooms.get(roomNumber);
	logger.info("findClient() - end;");
	return client;
    }

}
