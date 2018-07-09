package org.ufla.dcc.jmscomm.eureka;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.ufla.dcc.jmscomm.domain.EurekaService;

public class FindEurekaServices {

	public static final String EUREKA_APPS_PATH = "/eureka/apps";

	private static final String START_APPLICATION = "<application>";
	private static final String END_APPLICATION = "</application>";

	private static final String START_INSTANCE = "<instance>";
	private static final String END_INSTANCE = "</instance>";

	private static final String START_IP_ADDR = "<ipAddr>";
	private static final String END_IP_ADDR = "</ipAddr>";

	private static final String START_PORT = "<port enabled=\"true\">";
	private static final String END_PORT = "</port>";
	
	private static final String START_VIP_ADDRESS = "<vipAddress>";
	private static final String END_VIP_ADDRESS = "</vipAddress>";

	// Teste de busca de serviços no eureka
	public static void main(String... args) throws Exception {
		String eurekaHostname = "http://localhost:8761";
		FindEurekaServices findEurekaServices = new FindEurekaServices(eurekaHostname);
		findEurekaServices.recoveryEurekaServices();
		System.out.println("OK");
	}

	private String eurekaHostname;
	
	private Collection<EurekaService> eurekaServices;

	public FindEurekaServices(String eurekaHostname) {
		this.eurekaHostname = eurekaHostname;
	}

	private void extractApplication(String response, int startApp, int endApp) {
		EurekaService eurekaService = new EurekaService();
		String vipAddress;
		Set<String> hostnameServices = new HashSet<>();
		eurekaService.setHostnameServices(hostnameServices);
		int startInstance = startApp;
		int endInstance = startInstance;
		startInstance = response.indexOf(START_INSTANCE, endInstance);
		while (startInstance < endApp && startInstance != -1) {
			startInstance += START_INSTANCE.length();
			endInstance = response.indexOf(END_INSTANCE, startInstance);
			vipAddress = insertInstance(response, startInstance, endInstance, hostnameServices);
			if (eurekaService.getServiceName() == null) {
				eurekaService.setServiceName(vipAddress);
			} else if (!eurekaService.getServiceName().equals(vipAddress)) {
				System.out.println("ERRO no parser do XML do Eureka");
				System.out.println("Instâncias do mesmo serviço com diferentes vipAddress");
				System.out.println(eurekaService.getServiceName() + " - " + vipAddress);
				System.exit(0);
			}
			endInstance += END_INSTANCE.length();
			startInstance = response.indexOf(START_INSTANCE, endInstance);
		}
		eurekaServices.add(eurekaService);
	}

	private void extractEurekaServices(String response) {
		int start = 0;
		int end = start;
		start = response.indexOf(START_APPLICATION, end);
		while (start != -1) {
			start += START_APPLICATION.length();
			end = response.indexOf(END_APPLICATION, start);
			extractApplication(response, start, end);
			end += END_APPLICATION.length();
			start = response.indexOf(START_APPLICATION, end);
		}
	}

	private String getAttr(String response, int start, int end, String startTag, String endTag) {
		int startAttr = response.indexOf(startTag, start);
		if (startAttr == -1) {
			System.out.println("ERRO no parser do XML do Eureka");
			System.exit(0);
		}
		startAttr += startTag.length();
		int endAttr = response.indexOf(endTag, startAttr);
		if (endAttr == -1 || endAttr > end) {
			System.out.println("ERRO no parser do XML do Eureka");
			System.exit(0);
		}
		return response.substring(startAttr, endAttr);
	}

	private String insertInstance(String response, int startInstance, int endInstance, Set<String> hostnameServices) {
		String hostname = getAttr(response, startInstance, endInstance, START_IP_ADDR, END_IP_ADDR) + ":"
				+ getAttr(response, startInstance, endInstance, START_PORT, END_PORT);
		hostnameServices.add(hostname);
		return getAttr(response, startInstance, endInstance, START_VIP_ADDRESS, END_VIP_ADDRESS);
	}

	public Collection<EurekaService> recoveryEurekaServices() throws Exception {
		if (eurekaServices == null) {
			try {
				recoveryEurekaServicesIntern();
			} catch (IOException e) {
				e.printStackTrace();
				throw new Exception("Não foi possível recuperar os serviços registrados no Eureka pela endereço '"
						+ eurekaHostname + "'!");
			}
		}
		return eurekaServices;
	}

	private void recoveryEurekaServicesIntern() throws IOException {
		System.out.println("--------------------------------------------------");
		System.out.println("Mapeando instâncias de microsserviços do Eureka...");
		eurekaServices = new ArrayList<>();
		URL url = new URL(eurekaHostname + EUREKA_APPS_PATH);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("Content-Type", "application/json");
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		con.setRequestMethod("GET");
		con.connect();
		String response = IOUtils.toString(con.getInputStream(), "UTF-8");
		con.disconnect();
		// System.out.println(response);
		extractEurekaServices(response);
		System.out.println("Mapeamento de instâncias de microsserviços do Eureka realizado com sucesso!");
		System.out.println("--------------------------------------------------");
	}

}
