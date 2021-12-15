package com.hua.lock;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.hua.conf.ZKConf;
import com.hua.conf.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created at 2021-12-14 06:53
 * @author lerry
 */
public class ZKLockDemo {
	private static Logger logger = LoggerFactory.getLogger(ZKLockDemo.class);

	ZKConf zkConf;

	ZooKeeper zk;

	@Before
	public void connect() {
		zkConf = new ZKConf();
		zkConf.setAddress("127.0.0.1:2181/zklock");
		zkConf.setSessionTime(1_000);
		ZKUtils.setConf(zkConf);
		zk = ZKUtils.getZK();
	}

	@After
	public void close() {
		ZKUtils.closeZK();
	}

	@Test
	public void lockDemo() {
		for (int i = 0; i < 10; i++) {
			new Thread(() -> {
				WatcherCallBack watcherCallBack = new WatcherCallBack();
				watcherCallBack.setZooKeeper(zk);
				watcherCallBack.setThreadName(Thread.currentThread().getName());
				watcherCallBack.tryLock();
				logger.info("抢到锁，开始工作");
				try {
					TimeUnit.SECONDS.sleep(1);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				watcherCallBack.unLock();
			}).start();
		}

		// 阻塞主线程
		try {
			System.in.read();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
