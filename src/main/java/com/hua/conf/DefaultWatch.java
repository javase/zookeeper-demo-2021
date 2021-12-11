package com.hua.conf;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created at 2021-12-11 16:21
 * @author lerry
 */
public class DefaultWatch implements Watcher {
	Logger logger = LoggerFactory.getLogger(DefaultWatch.class);

	CountDownLatch init;

	public CountDownLatch getInit() {
		return init;
	}

	public void setInit(CountDownLatch init) {
		this.init = init;
	}

	@Override
	public void process(WatchedEvent event) {
		Event.KeeperState state = event.getState();
		switch (state) {
			case Unknown:
				break;
			case Disconnected:
				logger.info("Disconnected");
				init = new CountDownLatch(1);
				break;
			case NoSyncConnected:
				break;
			case SyncConnected:
				logger.info("SyncConnected");
				// 初始化完成，递减锁存器的计数，不再阻塞线程
				init.countDown();
				break;
			case AuthFailed:
				break;
			case ConnectedReadOnly:
				break;
			case SaslAuthenticated:
				break;
			case Expired:
				break;
		}
	}
}
