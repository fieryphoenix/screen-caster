/**
 * 
 */
package by.bsuir.zuyeu.admin.model;

import by.bsuir.zuyeu.admin.api.SocketCasterPacket;

/**
 * @author Fieryphoenix
 * 
 */
public final class CommandPacket extends SocketCasterPacket {
    private static final long serialVersionUID = 7008121657429197737L;

    private CasterClient client;

    public CasterClient getClient() {
	return client;
    }

    public void setClient(CasterClient client) {
	this.client = client;
    }

    @Override
    public String toString() {
	return "CommandPacket [client=" + client + ", catserPacket =" + super.toString() + "]";
    }

}
