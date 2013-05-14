/**
 * 
 */
package by.bsuir.zuyeu.admin.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.admin.api.SocketCasterPacket;
import by.bsuir.zuyeu.admin.model.CasterClient;
import by.bsuir.zuyeu.admin.model.CommandPacket;

/**
 * @author Fieryphoenix
 * 
 */
public class ConnectServerManager extends Observable implements Closeable {
    class ConnectListenTask implements Runnable {
	@Override
	public void run() {
	    while (!isClosed) {
		try {
		    final Socket socket = serverSocket.accept();
		    final CasterClient client = new CasterClient(socket);
		    logger.debug("new connection client = {}", client);
		    clients.put(client);
		    dispatchNewConnection(client);
		} catch (final IOException | InterruptedException | ClassNotFoundException e) {
		    logger.error("run()", e);
		}
	    }
	}
    }

    class CheckAliveClientSchedule extends TimerTask {

	@Override
	public void run() {
	    final List<CasterClient> closedSockets = new ArrayList<>();
	    for (final CasterClient client : clients) {
		if (client.getClientSocket().isClosed()) {
		    closedSockets.add(client);
		}
	    }
	    clients.removeAll(closedSockets);
	}
    }

    private static final Logger logger = LoggerFactory.getLogger(ConnectServerManager.class);

    public static final int MAX_CLIENTS_LOAD = 50;
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 10556;

    public static final int ALL_TASK_DELAY = 1000;

    private ServerSocket serverSocket;
    private final BlockingQueue<CasterClient> clients = new ArrayBlockingQueue<>(MAX_CLIENTS_LOAD);
    private final String host;
    private final int port;
    private boolean isClosed = false;

    public ConnectServerManager(final String host, final int port) {
	this.port = port;
	this.host = host;
	init();
    }

    public ConnectServerManager() {
	this(DEFAULT_HOST, DEFAULT_PORT);
    }

    protected void init() {
	logger.info("init() - start;");
	logger.trace("server host = {}, port = {}", new Object[] { host, port });
	try {
	    serverSocket = new ServerSocket(port, MAX_CLIENTS_LOAD);
	    final Runnable connectTask = new ConnectListenTask();
	    final Thread t = new Thread(connectTask);
	    t.setDaemon(true);
	    t.start();
	    final Timer timer = new Timer(true);
	    timer.schedule(new CheckAliveClientSchedule(), 0, ALL_TASK_DELAY);
	} catch (final IOException e) {
	    logger.error("init()", e);
	    throw new RuntimeException(e);
	}
	logger.info("init() - end;");
    }

    protected CasterClient findClientBySocket(final Socket socket) {
	logger.trace("findClientBySocket() - start;");
	CasterClient result = null;
	for (final CasterClient candidate : clients) {
	    if (candidate.getClientSocket() == socket) {
		result = candidate;
		break;
	    }
	}
	logger.trace("findClientBySocket() - end;");
	return result;
    }

    protected void dispatchNewConnection(final CasterClient client) throws IOException, ClassNotFoundException {
	logger.trace("dispatchNewConnection() - start;");
	final SocketCasterPacket socketPacket = readSocketPacket(client.getClientSocket());
	final CommandPacket packet = new CommandPacket();
	packet.setClient(client);
	packet.setCommandType(socketPacket.getCommandType());
	packet.setData(socketPacket.getData());
	this.setChanged();
	notifyObservers(packet);
	logger.trace("dispatchNewConnection() - end;");
    }

    protected SocketCasterPacket readSocketPacket(final Socket socket) throws IOException, ClassNotFoundException {
	logger.trace("readSocketPacket() - start;");
	SocketCasterPacket packet = null;
	final ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
	packet = (SocketCasterPacket) fromClient.readObject();

	socket.setKeepAlive(true);
	logger.trace("readSocketPacket() - end: packet = {}", packet);
	return packet;
    }

    public void writeResultToSocket(final CommandPacket packet) throws IOException {
	logger.trace("readSocketCommand() - start;");
	final ObjectOutputStream toClient = new ObjectOutputStream(packet.getClient().getClientSocket().getOutputStream());
	final SocketCasterPacket socketPacket = new SocketCasterPacket();
	socketPacket.setCommandType(packet.getCommandType());
	socketPacket.setData(packet.getData());
	toClient.writeObject(socketPacket);
	toClient.flush();
	logger.trace("readSocketCommand() - end;");
    }

    private void shutdownConnections() {
	logger.trace("shutdownConnections() - start;");
	for (final CasterClient client : clients) {

	    try {
		if (!client.getClientSocket().isClosed()) {
		    client.getClientSocket().close();
		}
	    } catch (final IOException e) {
		logger.error("shutdownConnections()", e);
	    }
	}
	logger.trace("shutdownConnections() - end;");
    }

    @Override
    public void close() throws IOException {
	if (!serverSocket.isClosed() && !isClosed) {
	    serverSocket.close();
	}
	shutdownConnections();
	isClosed = true;
    }
}
