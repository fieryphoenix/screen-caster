package by.bsuir.zuyeu.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.admin.api.SocketCasterPacket;
import by.bsuir.zuyeu.app.Constants;

public final class ConnectManagerClient {
    private static final Logger logger = LoggerFactory.getLogger(ConnectManagerClient.class);

    static class LazyInstanceHolder {
	private static final ConnectManagerClient INSTANCE = new ConnectManagerClient();

    }

    private Socket socket = null;

    public static ConnectManagerClient getInstance() {
	return LazyInstanceHolder.INSTANCE;
    }

    private ConnectManagerClient() {
    }

    public SocketCasterPacket dialogToServer(SocketCasterPacket packet) throws IOException, ClassNotFoundException {
	logger.info("dialogToServer() - start;");
	if (socket == null) {
	    try {
		socket = new Socket(InetAddress.getByName(Constants.MANAGER_HOST), Constants.MANAGER_PORT);
		socket.setKeepAlive(true);
	    } catch (final IOException e) {
		logger.error("ConnectManagerClient", e);
		throw e;
	    }
	}
	final SocketCasterPacket result = readWriteToServer(packet);
	logger.info("dialogToServer() - end: result = {}", result);
	return result;
    }

    private SocketCasterPacket readWriteToServer(final SocketCasterPacket packet) throws IOException, ClassNotFoundException {
	logger.trace("readWriteToServer() - start: packet = {}", packet);
	SocketCasterPacket resultPacket = null;
	try {

	    final ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());

	    toServer.writeObject(packet);
	    toServer.flush();

	    if (socket.isInputShutdown()) {
		try {
		    Thread.sleep(500);
		} catch (final InterruptedException e) {
		    logger.warn("readWriteToServer", e);
		}
	    }
	    final ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
	    resultPacket = (SocketCasterPacket) fromServer.readObject();

	} catch (final IOException e) {
	    throw e;
	}
	logger.trace("readWriteToServer() - end;");
	return resultPacket;
    }

    public void close() {
	if ((socket != null) && !socket.isClosed()) {
	    try {
		socket.close();
		socket = null;
	    } catch (final IOException e) {
		logger.error("close", e);
	    }
	}
    }
}
