/**
 * 
 */
package by.bsuir.zuyeu.model;

import javafx.application.Platform;
import javafx.concurrent.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fieryphoenix
 * 
 */
public abstract class UIChainTask<V> extends Task<V> {
    private static final Logger logger = LoggerFactory.getLogger(UIChainTask.class);

    private Task<V> uiTask;

    public UIChainTask(final Task<V> task) {
	this.uiTask = task;
    }

    @Override
    protected void failed() {
	logger.warn("task cancelled - do not skip ui task");
	if (uiTask != null) {
	    Platform.runLater(uiTask);
	}
    }

    @Override
    protected void succeeded() {
	if (uiTask != null) {
	    Platform.runLater(uiTask);
	}
    }

    public Task<V> getUiTask() {
	return uiTask;
    }

    public void setUiTask(Task<V> uiTask) {
	this.uiTask = uiTask;
    }
}
