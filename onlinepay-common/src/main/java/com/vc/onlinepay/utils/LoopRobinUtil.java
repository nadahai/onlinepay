/**
 * @类名称:LoopRobinUtil.java
 * @时间:2018年9月14日上午11:51:31
 * @版权:公司 Copyright (c) 2018 
 */
package com.vc.onlinepay.utils;

import com.vc.onlinepay.exception.OnlineServiceException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * @描述:轮询算法工具类
 * @时间:2018年9月14日 上午11:51:31
 */
@Service
public class LoopRobinUtil {
	
	public String random(Map<String, Integer> ipMap) {
        Map<String, Integer> ipServerMap = new ConcurrentHashMap<>(ipMap);
		Set<String> ipSet = ipServerMap.keySet();
		// 定义一个list放所有server
        ArrayList<String> ipArrayList = new ArrayList<String>(ipSet);
		// 循环随机数
		Random random = new Random();
		// 随机数在list数量中取（1-list.size）
		int pos = random.nextInt(ipArrayList.size());
        return ipArrayList.get(pos);
	}

	
	private Integer pos2 = 0;
	private String weightrobin(Map<String, Integer> ipMap) {
        Map<String, Integer> ipServerMap = new ConcurrentHashMap<>(ipMap);

		Set<String> ipSet = ipServerMap.keySet();
		Iterator<String> ipIterator = ipSet.iterator();

		// 定义一个list放所有server
		ArrayList<String> ipArrayList = new ArrayList<String>();

		// 循环set，根据set中的可以去得知map中的value，给list中添加对应数字的server数量
		while (ipIterator.hasNext()) {
			String serverName = ipIterator.next();
			Integer weight = ipServerMap.get(serverName);
			for (int i = 0; i < weight; i++) {
				ipArrayList.add(serverName);
			}
		}
		String serverName = null;
		if (pos2 >= ipArrayList.size()) {
			pos2 = 0;
		}
		serverName = ipArrayList.get(pos2);
		// 轮询+1
		pos2++;
		return serverName;
	}
	
	private String robinRandom(Map<String, Integer> ipMap) {
        Map<String, Integer> ipServerMap = new ConcurrentHashMap<>(ipMap);

		Set<String> ipSet = ipServerMap.keySet();
		Iterator<String> ipIterator = ipSet.iterator();

		// 定义一个list放所有server
		ArrayList<String> ipArrayList = new ArrayList<String>();

		// 循环set，根据set中的可以去得知map中的value，给list中添加对应数字的server数量
		while (ipIterator.hasNext()) {
			String serverName = ipIterator.next();
			Integer weight = ipServerMap.get(serverName);
			for (int i = 0; i < weight; i++) {
				ipArrayList.add(serverName);
			}
		}

		// 循环随机数
		Random random = new Random();
		// 随机数在list数量中取（1-list.size）
		int pos = random.nextInt(ipArrayList.size());
        return ipArrayList.get(pos);
	}

	private Integer pos = 0;
	private String roundRobin(Map<? extends String, ? extends Integer> noMap) {
        Map<String, Integer> ipServerMap = new ConcurrentHashMap<>(noMap);
		// 2.取出来key,放到set中
		Set<String> ipset = ipServerMap.keySet();
		// 3.set放到list，要循环list取出
        ArrayList<String> iplist = new ArrayList<>(ipset);
		String serverName = null;
		// 4.定义一个循环的值，如果大于set就从0开始
		synchronized (pos) {
			if (pos >= ipset.size()) {
				pos = 0;
			}
			serverName = iplist.get(pos);
			// 轮询+1
			pos++;
		}
		return serverName;
	}
	
	private String ipHash(Map<String, Integer> ipMap, String clientIP) {
        Map<String, Integer> ipServerMap = new ConcurrentHashMap<>(ipMap);
		// 2.取出来key,放到set中
		Set<String> ipset = ipServerMap.keySet();
		// 3.set放到list，要循环list取出
        ArrayList<String> iplist = new ArrayList<>(ipset);
		// 对ip的hashcode值取余数，每次都一样的
		int hashCode = clientIP.hashCode();
		int serverListsize = iplist.size();
		int pos = hashCode % serverListsize;
		return iplist.get(pos);
	}

	/**
	 * @描述:获取供应商账号
	 * @时间:2018年9月14日 上午10:59:27
	 */
	public String loopRobin(Map<String, Integer> noMap, int tryNum) throws OnlineServiceException {
		String no = this.roundRobin(noMap);
		if (StringUtil.isEmpty(no) && tryNum > 0) {
			return loopRobin(noMap, tryNum--);
		}
		return no;
	}

    /**
     * @描述:获取供应商账号
     * @时间:2018年9月14日 上午10:59:27
     */
   public String loopRobin(Map<String, Integer> noMap, int loopRobin,int tryNum) throws OnlineServiceException {
       // 1.序号轮询 2.公平轮询 3.加权轮询 4.Hash轮询 5.健康轮询 6.随机轮询 7.加权随机
       String upmerchNo = "";
       if (loopRobin == 1) {
           upmerchNo = this.roundRobin(noMap);
       } else if (loopRobin == 2) {// 2.公平轮询
           upmerchNo = this.roundRobin(noMap);
       } else if (loopRobin == 3) {// 3.加权轮询
           upmerchNo = this.weightrobin(noMap);
       } else if (loopRobin == 5) {// 5.健康轮询
           upmerchNo = this.roundRobin(noMap);
       } else if (loopRobin == 6) {// 6.随机轮询
           upmerchNo = this.random(noMap);
       } else if (loopRobin == 7) {// 7.加权随机
           upmerchNo = this.robinRandom(noMap);
       } else {
           upmerchNo = this.roundRobin(noMap);
       }
       if (StringUtil.isEmpty(upmerchNo) && tryNum > 0) {
           return loopRobin(noMap, loopRobin, tryNum--);
       }
		return upmerchNo;
	}

	/**
	 * @描述:获取供应商账号
	 * @时间:2018年9月14日 上午10:59:27
	 */
   /*public String loopRobin(Map<String, Integer> noMap, int loopRobin, String hash, int tryNum, String clientIp)
			throws OnlineServiceException {
		// 1.序号轮询 2.公平轮询 3.加权轮询 4.Hash轮询 5.健康轮询 6.随机轮询 7.加权随机
		String upmerchNo = "";
		if (loopRobin == 1) {
			upmerchNo = this.roundRobin(noMap);
		} else if (loopRobin == 2) {// 2.公平轮询
			upmerchNo = this.roundRobin(noMap);
		} else if (loopRobin == 3) {// 3.加权轮询
			upmerchNo = this.weightrobin(noMap);
		} else if (loopRobin == 4) {// 4:Hash轮询
			if (StringUtil.isEmpty(clientIp)) {
				clientIp = hash;
			}
			upmerchNo = this.ipHash(noMap, clientIp);
		} else if (loopRobin == 5) {// 5.健康轮询
			upmerchNo = this.roundRobin(noMap);
		} else if (loopRobin == 6) {// 6.随机轮询
			upmerchNo = this.random(noMap);
		} else if (loopRobin == 7) {// 7.加权随机
			upmerchNo = this.robinRandom(noMap);
		} else {
			upmerchNo = this.roundRobin(noMap);
		}
		if (StringUtil.isEmpty(upmerchNo) && tryNum > 0) {
			return loopRobin(noMap, loopRobin, hash, tryNum--, clientIp);
		}
		return upmerchNo;
	}*/

	/**
	 * 获取map中第一个数据值
	 */
	private static String getFirstValue(Map<String, Integer> map) {
		if (map == null || map.isEmpty()) {
			return "";
		}
		for (String key : map.keySet()) {
			if (StringUtil.isNotEmpty(key)) {
				return key;
			}
		}
		return "";
	}
}
