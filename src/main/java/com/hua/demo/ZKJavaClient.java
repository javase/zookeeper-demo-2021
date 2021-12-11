package com.hua.demo;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * Hello world!
 */
public class ZKJavaClient {
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
							System.out.println("打印event对象：" + event.toString());
							switch (state) {
								case Unknown:
									System.out.println("Event.KeeperState：Unknown");
									break;
								case Disconnected:
									System.out.println("Event.KeeperState：Disconnected");
									break;
								case NoSyncConnected:
									System.out.println("Event.KeeperState：NoSyncConnected");
									break;
								case SyncConnected:
									latch.countDown();
									System.out.println("Event.KeeperState：已连接");
									break;
								case AuthFailed:
									System.out.println("Event.KeeperState：AuthFailed");
									break;
								case ConnectedReadOnly:
									System.out.println("Event.KeeperState：ConnectedReadOnly");
									break;
								case SaslAuthenticated:
									System.out.println("Event.KeeperState：SaslAuthenticated");
									break;
								case Expired:
									System.out.println("Event.KeeperState：Expired");
									break;
							}
							switch (type) {
								case None:
									System.out.println("Event.EventType：None");
									break;
								case NodeCreated:
									System.out.println("Event.EventType：NodeCreated");
									break;
								case NodeDeleted:
									System.out.println("Event.EventType：NodeDeleted");
									break;
								case NodeDataChanged:
									System.out.println("Event.EventType：NodeDataChanged");
									break;
								case NodeChildrenChanged:
									System.out.println("Event.EventType：NodeChildrenChanged");
									break;
							}
						}
					});
			latch.await();
			System.out.println("latch wait...");

			ZooKeeper.States state = zooKeeper.getState();
			switch (state) {
				case CONNECTING:
					System.out.println("ZooKeeper.States02：连接中");
					break;
				case ASSOCIATING:
					break;
				case CONNECTED:
					System.out.println("ZooKeeper.States02：已连接");
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

			zooKeeper.getData("/ooxx", new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					System.out.println("getData watch:" + event.toString());
					try {
						// true default watch 被重新注册  new ZooKeeper的watch
						zooKeeper.getData("/ooxx", true, stat);
					}
					catch (KeeperException e) {
						e.printStackTrace();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}, stat);

			// 修改值  会触发getData的回调
			Stat stat1 = zooKeeper.setData("/ooxx", "newdata".getBytes(), 0);

			// 还会触发不？ 不会触发了，因为watch是一次性的
			Stat stat2 = zooKeeper.setData("/ooxx", "newdata01".getBytes(), 1);

			System.out.printf("第一次设置值后的Stat：%s\n第二次设置值后的Stat：%s\n", stat1, stat2);
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
