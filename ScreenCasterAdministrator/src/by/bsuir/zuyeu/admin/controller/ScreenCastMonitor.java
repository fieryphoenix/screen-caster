/**
 * 
 */
package by.bsuir.zuyeu.admin.controller;

import java.io.Closeable;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.admin.server.ConnectServerManager;

/**
 * @author Fieryphoenix
 * 
 */
public final class ScreenCastMonitor implements Closeable, Observer {
    private static final Logger logger = LoggerFactory.getLogger(ScreenCastMonitor.class);

    private ConnectServerManager serverManager;

    /**
     * @param args
     */
    public static void main(String[] args) {
	logger.info("main() - start;");
	final ScreenCastMonitor monitor = new ScreenCastMonitor();
	monitor.run();
	boolean breakFlag = false;
	while (!breakFlag) {
	    try {
		Thread.sleep(1000);
	    } catch (final InterruptedException e) {
		logger.error("main()", e);
		monitor.close();
		breakFlag = true;
	    }
	}
	logger.info("main() - end;");
    }

    public void run() {
	logger.info("run() - start;");
	serverManager = new ConnectServerManager();
	logger.info("run() - end;");
    }

    @Override
    public void close() {
	try {
	    serverManager.close();
	} catch (final IOException e) {
	    logger.error("close()", e);
	}
    }

    @Override
    public void update(Observable o, Object arg) {
	logger.info("update() - start: o = {}, arg = {}", new Object[] { o, arg });
	// TODO
	logger.info("update() - end;");
    }

}
