/**
 * 
 */
package by.bsuir.zuyeu.view.util;

import java.util.Timer;
import java.util.TimerTask;

import javafx.concurrent.Task;
import javafx.scene.control.Button;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fieryphoenix
 * 
 */
public final class HideButtonUtil {
    private static final Logger logger = LoggerFactory.getLogger(HideButtonUtil.class);

    private static final double OPACITY_DECREASE = 0.02;
    private static final int HIDE_DELAY = 100;
    private static final double MIN_OPACITY = 0.1;

    private final Button button;
    private Timer timer;
    private boolean isTimerStopped;

    public HideButtonUtil(final Button btn) {
	button = btn;
    }

    public Task<Void> startHiding(final long startTimeMillis) {
	logger.trace("startHiding() - start: startTimeMillis = {}", startTimeMillis);
	isTimerStopped = false;
	final Task<Void> task = new Task<Void>() {

	    @Override
	    protected Void call() throws Exception {
		logger.debug("call() - start;");
		timer = new Timer(true); // daemon timer
		timer.schedule(new TimerTask() {

		    @Override
		    public void run() {
			logger.debug("run timer task - opacity decrease");
			if ((button.getOpacity() < MIN_OPACITY) || isTimerStopped) {
			    this.cancel();
			    logger.debug("cancel timer");
			} else {
			    button.setOpacity(button.getOpacity() - OPACITY_DECREASE);
			}
			logger.debug("button opacity = {}", button.getOpacity());
		    }
		}, startTimeMillis, HIDE_DELAY);
		logger.debug("call() - end;");
		return null;
	    }
	};
	logger.trace("startHiding() - end: task = {}", task);
	return task;
    }

    public void stopHidding() {
	isTimerStopped = true;
	button.setOpacity(1);
    }
}
