package org.ufla.dcc.jmscomm;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.ufla.dcc.jmscomm.eureka.FindEurekaServices;
import org.ufla.dcc.jmscomm.extractor.MapOccurrenceToMs;
import org.ufla.dcc.jmscomm.persistence.GenerateReport;
import org.ufla.dcc.jmscomm.persistence.PersistenceComm;
import org.ufla.dcc.jmscomm.persistence.PersistenceModifiedClass;

public class JMsCommApp {

	private static String HELP_MESSAGE = "Para executar a aplicação utilize o comando abaixo:\n"
			+ "\tjava -jar jmscomm.jar <projectDirectory> <eurekaHostname> <packagecomm>\n" + "onde:\n"
			+ "<projectDirectory> - o caminho do diretório do projeto que será analisado e terá suas comunicações de métodos RestTemplate convertidos na Interface FeignClient.\n"
			+ "<eurekaHostname> - o hostname (junto com o ip) de onde o servidor eureka está executando, e.g., 'http://localhost:8961';\n"
			+ "<packagecomm> - o pacote onde serão criadas as novas interfaces de comunicação FeignClient.\n";

	private static void showHelpMessage() {
		System.out.println(HELP_MESSAGE);
	}

	private static void showErrorArgsLength(int argsLength) {
		System.out.println("ERRO.\nA aplicação foi executada com '" + argsLength
				+ "' argumentos, sendo que ela precisa de 3 argumentos: <projectDirectory>, <eurekaHostname> e <packagecomm>.");
		System.out.println("----------------------------------------------------");
		showHelpMessage();
		System.exit(0);
	}

	private static void verifyArgsErrors(String... args) throws MalformedURLException {
		if (args.length != 3) {
			showErrorArgsLength(args.length);
		}
		File file = new File(args[0]);
		if (!file.exists() || !file.isDirectory()) {
			System.out.println("ERRO.\nO caminho do diretório passado como o argumento <projectDirectory>, '" + args[0]
					+ "',não existe ou não é diretório.\n");
			System.out.println("----------------------------------------------------");
			showHelpMessage();
			System.exit(0);
		}
		String eurekaHostname = args[1];
		String urlStr = eurekaHostname + FindEurekaServices.EUREKA_APPS_PATH;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Content-Type", "application/json");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			con.setRequestMethod("GET");
			con.connect();
			IOUtils.toString(con.getInputStream(), "UTF-8");
			con.disconnect();
		} catch (Exception exception) {
			System.out.println("ERRO.\nNão foi possível comunicar com o Eureka pelo hostname informado '"
					+ eurekaHostname + "'.\nFoi tentado realizar uma requisição HTTP GET na URL '" + urlStr
					+ "', no entanto ocorreu um erro na requisição.\n");
			System.out.println("----------------------------------------------------");
			showHelpMessage();
			System.exit(0);
		}

	}

	public static void main(String... args) throws Exception {
//		args = new String[] {
//				System.getProperty("user.home") + "/workspaces/microservices/msdanielle/toyexample/TesteSale",
//				"http://localhost:8761", "com.mscomm" };
		verifyArgsErrors(args);
		String projectDirectory = args[0];
		String eurekaHostname = args[1];
		String packageComm = args[2];
		MapOccurrenceToMs identifyOccurrenceMs = new MapOccurrenceToMs(projectDirectory, eurekaHostname);
		PersistenceComm persistenceComm = new PersistenceComm(projectDirectory, packageComm, 
				identifyOccurrenceMs.extractServiceToMethods());
		persistenceComm.persist();
		PersistenceModifiedClass persistenceModifiedClass = new PersistenceModifiedClass(
				identifyOccurrenceMs.extractRestTemplateOccurences(), packageComm);
		persistenceModifiedClass.persist();
		GenerateReport generateReport = new GenerateReport(projectDirectory,
				identifyOccurrenceMs.extractRestTemplateOccurences());
		generateReport.generate();

	}
}
