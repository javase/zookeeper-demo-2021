package com.hua.demo;

import java.io.IOException;
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
 * 异步方式获取数值
 */
public class ZKJavaClientAsyn {
	static Logger logger = LoggerFactory.getLogger(ZKJavaClientAsyn.class);

	public static void main(String[] args) {
		try {
			final CountDownLatch latch = new CountDownLatch(1);
			final ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181",
					3000,
					new Watcher() {
						@Override
						public void process(WatchedEvent event) {
							Event.KeeperState state = event.getState();
							Event.EventType type = event.getType();
							String path = event.getPath();
							logger.info("打印event对象：" + event.toString());
							switch (state) {
								case Unknown:
									logger.info("Event.KeeperState：Unknown");
									break;
								case Disconnected:
									logger.info("Event.KeeperState：Disconnected");
									break;
								case NoSyncConnected:
									logger.info("Event.KeeperState：NoSyncConnected");
									break;
								case SyncConnected:
									latch.countDown();
									logger.info("Event.KeeperState：已连接");
									break;
								case AuthFailed:
									logger.info("Event.KeeperState：AuthFailed");
									break;
								case ConnectedReadOnly:
									logger.info("Event.KeeperState：ConnectedReadOnly");
									break;
								case SaslAuthenticated:
									logger.info("Event.KeeperState：SaslAuthenticated");
									break;
								case Expired:
									logger.info("Event.KeeperState：Expired");
									break;
							}
							switch (type) {
								case None:
									logger.info("Event.EventType：None");
									break;
								case NodeCreated:
									logger.info("Event.EventType：NodeCreated");
									break;
								case NodeDeleted:
									logger.info("Event.EventType：NodeDeleted");
									break;
								case NodeDataChanged:
									logger.info("Event.EventType：NodeDataChanged");
									break;
								case NodeChildrenChanged:
									logger.info("Event.EventType：NodeChildrenChanged");
									break;
							}
						}
					});
			latch.await();
			logger.info("latch wait...");

			ZooKeeper.States state = zooKeeper.getState();
			switch (state) {
				case CONNECTING:
					logger.info("ZooKeeper.States02：连接中");
					break;
				case ASSOCIATING:
					break;
				case CONNECTED:
					logger.info("ZooKeeper.States02：已连接");
					break;
				case CONNECTEDREADONLY:
					break;
				case CLOSED:
					break;
				case AUTH_FAILED:
					break;
				case NOT_CONNECTED:
					break;
			}
			// 开始具体的操作

			// 创建一个节点  返回：the actual path of the created node
			String pathName = zooKeeper.create("/ooxx", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			final Stat stat = new Stat();

			logger.info("----------------async start ----------------");
			// public void getData(String path, boolean watch, DataCallback cb, Object ctx)
			zooKeeper.getData("/ooxx",
					false,
					new AsyncCallback.DataCallback() {
						@Override
						public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
							logger.info("----------------async call back ----------------");
							logger.info("ctx:{}",ctx.toString());
							logger.info("打印data的值：" + new String(data));
						}
					},
					"abc");
			logger.info("----------------async over ----------------");

		}
		catch (IOException | InterruptedException | KeeperException e) {
			e.printStackTrace();
		}


		try {
			System.in.read();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
