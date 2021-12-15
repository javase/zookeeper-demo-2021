package com.hua.lock;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created at 2021-12-15 07:30
 * @author lerry
 */
public class WatcherCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.ChildrenCallback, AsyncCallback.StatCallback {

	private static Logger logger = LoggerFactory.getLogger(WatcherCallBack.class);

	private CountDownLatch countDownLatch = new CountDownLatch(1);

	private ZooKeeper zooKeeper;

	private String threadName;

	private String pathName;

	public void tryLock() {

		try {
			logger.info("{} create node", threadName);
			zooKeeper.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, threadName);
			countDownLatch.await();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 释放锁
	 */
	public void unLock() {
		try {
			zooKeeper.delete(pathName, -1);
			logger.info("{} over work", threadName);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (KeeperException e) {
			e.printStackTrace();
		}
	}

	/**
	 * create call back
	 * @param rc
	 * @param path
	 * @param ctx
	 * @param name
	 */
	@Override
	public void processResult(int rc, String path, Object ctx, String name) {
		if (name != null) {
			logger.info("{}创建了：{}", threadName, name);
			pathName = name;
			zooKeeper.getChildren("/", false, this, "after create");
		}
	}

	/**
	 * getChildren call back
	 * @param rc
	 * @param path
	 * @param ctx
	 * @param children
	 */
	@Override
	public void processResult(int rc, String path, Object ctx, List<String> children) {
		// 排序
		Collections.sort(children);
		int i = children.indexOf(pathName.substring(1));
		// 是不是第一个
		if (0 == i) {
			logger.info("{} I'm the first", threadName);
			try {
				zooKeeper.setData("/", threadName.getBytes(), -1);
				countDownLatch.countDown();
			}
			catch (KeeperException e) {
				e.printStackTrace();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else {
			// 不是第一个
			zooKeeper.exists("/" + children.get(i - 1), this, this, "222");
		}
	}

	/**
	 * exists call back  StatCallback
	 * @param rc
	 * @param path
	 * @param ctx
	 * @param stat
	 */
	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		// 暂不处理
	}

	/**
	 * exists call back  Watcher
	 * @param event
	 */
	@Override
	public void process(WatchedEvent event) {
		switch (event.getType()) {
			case None:
				break;
			case NodeCreated:
				break;
			case NodeDeleted:
				zooKeeper.getChildren("/", false, this, "333");
				break;
			case NodeDataChanged:
				break;
			case NodeChildrenChanged:
				break;
		}
	}

	public void setZooKeeper(ZooKeeper zooKeeper) {
		this.zooKeeper = zooKeeper;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
}
