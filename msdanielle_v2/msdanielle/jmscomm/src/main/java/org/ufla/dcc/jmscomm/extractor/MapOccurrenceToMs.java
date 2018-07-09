package org.ufla.dcc.jmscomm.extractor;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ufla.dcc.jmscomm.domain.EurekaService;
import org.ufla.dcc.jmscomm.domain.FeignInterfaceMethod;
import org.ufla.dcc.jmscomm.domain.RestTemplateOccurrence;
import org.ufla.dcc.jmscomm.eureka.FindEurekaServices;
import org.ufla.dcc.jmscomm.extractor.util.HostnameUtil;

public class MapOccurrenceToMs {
	
	private static final String LOCALHOST = "localhost";
	
	private static final Set<String> LOCALHOST_IPS = HostnameUtil.extractLocalhostIps();
	

	private ExtractRestTemplateOccurrences extractRestOccurrences;
	
	private FindEurekaServices findEurekaServices;
	
	private Map<EurekaService, Set<FeignInterfaceMethod>> serviceToMethods;
	
	
	public MapOccurrenceToMs(String projectPath, String eurekaHostname) {
		extractRestOccurrences = 
				new ExtractRestTemplateOccurrences(projectPath);
		findEurekaServices = new FindEurekaServices(eurekaHostname);
	}
	
	
	private Set<String> createSetLocalhostNames(String hostname) {
		Set<String> localhostNames = new HashSet<>();
		localhostNames.add(hostname);
		String port = ":" + HostnameUtil.extractPort(hostname);
		for (String ip : LOCALHOST_IPS) {
			localhostNames.add(ip + port);
		}
		return localhostNames;
	}
	
	public Map<File, Collection<RestTemplateOccurrence>> extractRestTemplateOccurences() throws Exception {
		return extractRestOccurrences.extractRestTemplateOccurences();
	}
	
	private EurekaService findServiceForOccurenceLocalhost(String hostname) throws Exception {
		Set<String> localhostNames = createSetLocalhostNames(hostname);
		for (EurekaService eurekaService : findEurekaServices.recoveryEurekaServices()) {
			for (String localhost : localhostNames) {
				if (eurekaService.getHostnameServices().contains(localhost)) {
					return eurekaService;
				}
			}
		}
		return null;
	}
	
	public static boolean isLocalhost(String hostname) {
		if (hostname.startsWith(LOCALHOST)) {
			return true;
		}
		for (String localhostname : LOCALHOST_IPS) {
			if (hostname.startsWith(localhostname)) {
				return true;
			}
		}
		return false;
	}
	
	public EurekaService findServiceForOccurrence(String hostname) throws Exception {
		if (isLocalhost(hostname)) {
			return findServiceForOccurenceLocalhost(hostname);
		}
		for (EurekaService eurekaService : findEurekaServices.recoveryEurekaServices()) {
			if (eurekaService.getHostnameServices().contains(hostname)) {
				return eurekaService;
			}
		}
		return null;
	}
	
	private void mapOccurrenceToMs() throws Exception {
		Map<File, Collection<RestTemplateOccurrence>> fileToOcurrences = extractRestOccurrences.extractRestTemplateOccurences();
		for (File file : fileToOcurrences.keySet()) {
			for (RestTemplateOccurrence occurence : fileToOcurrences.get(file)) {
				if (occurence.validateHostnameAndReplaceByIpAddress()) {
					EurekaService eurekaService = findServiceForOccurrence(occurence.getHostname());
					if (eurekaService == null) {
						occurence.setProblem(true);
						occurence.setProblemReason("Não há serviço no Eureka");
					} else {
						occurence.setEurekaService(eurekaService);
					}
				}
			}
		}
	}
	
	private void createServiceToMethods() throws Exception {
		mapOccurrenceToMs();
		serviceToMethods = new HashMap<>();
		for (EurekaService eurekaService : findEurekaServices.recoveryEurekaServices()) {
			serviceToMethods.put(eurekaService, new HashSet<>());
		}
		Map<File, Collection<RestTemplateOccurrence>> fileToOcurrences = extractRestOccurrences.extractRestTemplateOccurences();
		for (File file : fileToOcurrences.keySet()) {
			for (RestTemplateOccurrence occurence : fileToOcurrences.get(file)) {
				if (!occurence.isProblem()) {
					if (occurence.completeFeignMethod(file)) {
						serviceToMethods.get(occurence.getEurekaService()).add(occurence.getFeignInterfaceMethod());
					}
				}
			}
		}
	}

	public Map<EurekaService, Set<FeignInterfaceMethod>> extractServiceToMethods() throws Exception {
		if (serviceToMethods == null) {
			createServiceToMethods();
		}
		return serviceToMethods;
	}

	public Collection<EurekaService> recoveryEurekaServices() throws Exception {
		return findEurekaServices.recoveryEurekaServices();
	}

}
