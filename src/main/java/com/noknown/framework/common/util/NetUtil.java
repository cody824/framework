package com.noknown.framework.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author guodong
 * @date 2019/3/6
 */
public class NetUtil {

	/**
	 * 获取Local的mac地址
	 *
	 * @return
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public static String getLocalMac() throws SocketException, UnknownHostException {
		InetAddress ia = InetAddress.getLocalHost();

		return getMac(ia);
	}

	/**
	 * 获取网卡MAC地址
	 *
	 * @param ia
	 * @return
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public static String getMac(InetAddress ia) throws SocketException {
		//获取网卡，获取地址
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			//字节转换为整数
			int temp = mac[i] & 0xff;
			String str = Integer.toHexString(temp);
			if (str.length() == 1) {
				sb.append("0" + str);
			} else {
				sb.append(str);
			}
		}
		return sb.toString().toUpperCase();
	}
}
