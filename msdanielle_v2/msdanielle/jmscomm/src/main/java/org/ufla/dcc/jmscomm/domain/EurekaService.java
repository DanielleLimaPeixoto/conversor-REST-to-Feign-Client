package org.ufla.dcc.jmscomm.domain;

import java.util.Set;

public class EurekaService {
	
	private static final String COMM = "Comm";
	
	private String serviceName;
	
	private Set<String> hostnameServices;
	
	public String getServiceName() {
		return serviceName;
	}
	
	public String getAttrName() {
		return Character.toLowerCase(serviceName.charAt(0)) + serviceName.substring(1) + COMM;
	}
	
	public String getInterfaceName() {
		return Character.toUpperCase(serviceName.charAt(0)) + serviceName.substring(1) + COMM;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Set<String> getHostnameServices() {
		return hostnameServices;
	}

	public void setHostnameServices(Set<String> hostnameServices) {
		this.hostnameServices = hostnameServices;
	}


}
