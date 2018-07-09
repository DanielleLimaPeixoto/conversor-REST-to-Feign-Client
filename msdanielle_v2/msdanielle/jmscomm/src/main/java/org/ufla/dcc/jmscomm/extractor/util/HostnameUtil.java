package org.ufla.dcc.jmscomm.extractor.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public final class HostnameUtil {
	
	public final static Set<String> extractLocalhostIps() {
		Set<String> localhostIps = new HashSet<>();
		Enumeration<NetworkInterface> networkInterfaces = null;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
			throw new RuntimeException("Não foi possível recuperar os ips do localost.");
		}
		while (networkInterfaces.hasMoreElements()){
		    Enumeration<InetAddress> inetAddress =  networkInterfaces.nextElement().getInetAddresses();
		    while (inetAddress.hasMoreElements()) {
		    	localhostIps.add(inetAddress.nextElement().getHostAddress());
		    }
		}
//		System.out.println("LOCALHOST_IPS");
//		for (String localhostIp : localhostIps) {
//			System.out.println(localhostIp);
//		}
//		System.out.println("------------------");
		return localhostIps;
	}
	
	public final static String extractPort(String hostname) {
		int start = hostname.indexOf(':') + 1;
		return hostname.substring(start);
	}
	

}
