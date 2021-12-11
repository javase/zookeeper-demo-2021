package com.hua;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.hua.conf.MyConf;
import com.hua.conf.WatcherCallBack;
import com.hua.conf.ZKConf;
import com.hua.conf.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created at 2021-12-11 16:30
 * @author lerry
 */
public class TestZK {
	Logger logger = LoggerFactory.getLogger(TestZK.class);

	ZooKeeper zk;

	WatcherCallBack watcherCallBack = new WatcherCallBack();

	ZKConf zkConf;

	MyConf confMsg = new MyConf();

	@Before
	public void connect() {
		zkConf = new ZKConf();
		zkConf.setAddress("127.0.0.1:2181/testNode");
		zkConf.setSessionTime(1_000);
		ZKUtils.setConf(zkConf);
		zk = ZKUtils.getZK();
	}

	@After
	public void close() {
		ZKUtils.closeZK();
	}

	@Test
	public void getConfigFromZK() throws InterruptedException {

		//程序的配置来源：本地文件系统，数据库，redis，zk。。一切程序可以连接的地方
		//配置内容的提供、变更、响应：本地，数据库等等，都是需要心跳判断，或者手工调用触发

		//我是程序A 我需要配置：1，zk中别人是不是填充了配置；2，别人填充、更改了配置之后我怎么办
		watcherCallBack.setConfMsg(confMsg);
		watcherCallBack.setInit(new CountDownLatch(1));
		watcherCallBack.setZk(zk);
		watcherCallBack.setWatchPath("/AppConf");

		watcherCallBack.aWait();
		while (true) {
			// 节点不存在
			if ("".equalsIgnoreCase(confMsg.getConf()) || null == confMsg.getConf()) {
				logger.error("config missing...");
				// 等待节点被设置
				watcherCallBack.aWait();
			}
			else {
				// 获取节点值
				logger.info("配置信息为：{}", confMsg.getConf());
			}
			TimeUnit.SECONDS.sleep(3);
		}
	}
}
