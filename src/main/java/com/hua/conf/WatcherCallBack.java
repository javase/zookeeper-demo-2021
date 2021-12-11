package com.hua.conf;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created at 2021-12-11 16:44
 * @author lerry
 */
public class WatcherCallBack implements Watcher, AsyncCallback.DataCallback, AsyncCallback.StatCallback {

	Logger logger = LoggerFactory.getLogger(WatcherCallBack.class);

	ZooKeeper zk;

	String watchPath;

	CountDownLatch init;

	MyConf confMsg;



	public void aWait() throws InterruptedException {
		zk.exists(watchPath, this, this, "initExists");
		init.await();
	}


	@Override
	public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
		if (data != null) {
			confMsg.setConf(new String(data));
			init.countDown();
		}
	}

	@Override
	public void process(WatchedEvent event) {
		logger.info(event.toString());
		Event.EventType type = event.getType();
		switch (type) {
			case None:
				break;
			case NodeCreated:
				// 节点创建
				logger.info("getData watch@NodeCreated");
				zk.getData(watchPath, this, this, "NodeCreated");
				break;
			case NodeDeleted:
				confMsg.setConf("");
				init = new CountDownLatch(1);
				break;
			case NodeDataChanged:
				zk.getData(watchPath, this, this, "NodeDataChanged");
				break;
			case NodeChildrenChanged:
				break;
		}
	}

	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		if (stat != null) {
			zk.getData(watchPath, this, this, "ex");
		}
	}

	public void setZk(ZooKeeper zk) {
		this.zk = zk;
	}

	public void setWatchPath(String watchPath) {
		this.watchPath = watchPath;
	}

	public void setInit(CountDownLatch init) throws InterruptedException {
		logger.info("set init CountDownLatch");
		this.init = init;
		TimeUnit.SECONDS.sleep(1);
	}

	public void setConfMsg(MyConf confMsg) {
		this.confMsg = confMsg;
	}
}
