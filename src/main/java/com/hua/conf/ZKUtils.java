package com.hua.conf;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.ZooKeeper;

/**
 * created at 2021-12-11 16:26
 * @author lerry
 */
public class ZKUtils {
	static ZooKeeper zk;

	static ZKConf conf;

	static DefaultWatch watch;

	static CountDownLatch c = new CountDownLatch(1);

	public static void setConf(ZKConf conf) {
		ZKUtils.conf = conf;
	}

	public static void setWatch(DefaultWatch watch) {
		watch.setInit(c);
		ZKUtils.watch = watch;
	}

	/**
	 * 会初始化ZooKeeper对象
	 * @return
	 */
	public static ZooKeeper getZK() {
		try {
			zk = new ZooKeeper(conf.getAddress(),
					conf.getSessionTime(),
					watch);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return zk;
	}

	public static void closeZK() {
		if (zk != null) {
			try {
				zk.close();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
