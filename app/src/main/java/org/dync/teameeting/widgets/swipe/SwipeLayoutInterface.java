package org.dync.teameeting.widgets.swipe;

import org.dync.teameeting.widgets.swipe.SwipeLayout.Status;

public interface SwipeLayoutInterface {

	Status getCurrentStatus();
	
	void close();
	
	void open();
}
