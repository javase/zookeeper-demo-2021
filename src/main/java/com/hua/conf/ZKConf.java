package com.hua.conf;

/**
 * ZooKeeper的配置信息
 * created at 2021-12-11 16:21
 * @author lerry
 */
public class ZKConf {
	private String address;

	private Integer sessionTime;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(Integer sessionTime) {
		this.sessionTime = sessionTime;
	}
}
