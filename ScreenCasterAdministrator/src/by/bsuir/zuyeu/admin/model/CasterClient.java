/**
 * 
 */
package by.bsuir.zuyeu.admin.model;

import java.net.Socket;

/**
 * @author Fieryphoenix
 * 
 */
public class CasterClient {
    private final String host;
    private final int port;
    private final Socket clientSocket;

    public CasterClient(final Socket socket) {
	clientSocket = socket;
	host = socket.getInetAddress().getHostAddress();
	port = socket.getPort();
    }

    public String getHost() {
	return host;
    }

    public int getPort() {
	return port;
    }

    public Socket getClientSocket() {
	return clientSocket;
    }

    @Override
    public String toString() {
	return "CasterClient [host=" + host + ", port=" + port + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = (prime * result) + ((host == null) ? 0 : host.hashCode());
	result = (prime * result) + port;
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final CasterClient other = (CasterClient) obj;
	if (host == null) {
	    if (other.host != null) {
		return false;
	    }
	} else if (!host.equals(other.host)) {
	    return false;
	}
	if (port != other.port) {
	    return false;
	}
	return true;
    }

}
