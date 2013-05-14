package by.bsuir.zuyeu.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.admin.api.CommandType;
import by.bsuir.zuyeu.app.Constants;

public final class ConnectManagerClient {
    private static final Logger logger = LoggerFactory.getLogger(ConnectManagerClient.class);

    static class LazyInstanceHolder {
	private static final ConnectManagerClient INSTANCE = new ConnectManagerClient();

    }

    public static ConnectManagerClient getInstance() {
	return LazyInstanceHolder.INSTANCE;
    }

    private ConnectManagerClient() {
    }

    public CommandType dialogToServer(CommandType command) throws IOException {
	logger.info("dialogToServer() - start;");
	final CommandType result = readWriteToServer(command.getValue());
	logger.info("dialogToServer() - end: result = {}", result);
	return result;
    }

    public static void main(String[] args) {
	final ConnectManagerClient client = new ConnectManagerClient();
	try {
	    client.readWriteToServer(1L);
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private CommandType readWriteToServer(final Object o) throws IOException {
	logger.trace("readWriteToServer() - start: object = {}", o);
	CommandType commandType = null;
	Socket socket = null;
	try {

	    socket = new Socket(InetAddress.getByName(Constants.MANAGER_HOST), Constants.MANAGER_PORT);

	    final ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
	    final ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

	    toServer.writeLong((Long) o);
	    toServer.flush();
	    final Long data = fromServer.readLong();
	    if (data != null) {
		logger.warn("read data = {}", data);
		commandType = CommandType.valueOf(data);
	    }

	} catch (final IOException e) {
	    throw e;
	} finally {
	    if ((socket != null) && !socket.isClosed()) {
		socket.close();
	    }
	}
	logger.trace("readWriteToServer() - end;");
	return commandType;
    }
}
