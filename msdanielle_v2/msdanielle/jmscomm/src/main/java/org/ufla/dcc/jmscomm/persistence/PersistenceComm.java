package org.ufla.dcc.jmscomm.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ufla.dcc.jmscomm.domain.EurekaService;
import org.ufla.dcc.jmscomm.domain.FeignInterfaceMethod;

public class PersistenceComm {

	public static final String PACKAGE_COMM = "org.ufla.dcc.jmscomm";
	public static final String[] SOURCE_DIRECTORIES = { "src" + File.separator + "main" + File.separator + "java",
			"src" };

	private static final String FEIGN_CLIENT_CONFIGURATION_CLASS = "FeignConfiguration";

	private static final String FEIGN_CLIENT_CONFIGURATION = "import org.springframework.cloud.netflix.feign.EnableFeignClients;\n"
			+ "import org.springframework.context.annotation.Configuration;\n\n" + "@Configuration\n"
			+ "@EnableFeignClients\n" + "public class FeignConfiguration {\n\n}\n";

	private String packageComm;

	private String projectDirectory;

	private String sourceDirectory;

	private Map<EurekaService, Set<FeignInterfaceMethod>> serviceToMethods;

	public PersistenceComm(String projectDirectory, String packageComm,
			Map<EurekaService, Set<FeignInterfaceMethod>> serviceToMethods) throws Exception {
		this.projectDirectory = projectDirectory;
		this.serviceToMethods = serviceToMethods;
		this.packageComm = packageComm;
		defineSourceDirectory();
	}

	private void defineSourceDirectory() throws Exception {
		for (String sourceDirectory : SOURCE_DIRECTORIES) {
			File file = new File(projectDirectory + File.separator + sourceDirectory);
			if (file.exists() && file.isDirectory()) {
				this.sourceDirectory = file.getAbsolutePath();
				return;
			}
		}
		throw new Exception("Não foi encontrado o diretório de código fonte!");
	}

	public String getPackageCommDirectory() {
		if (File.separator.equals("\\")) {
			return packageComm.replaceAll("\\.", "\\\\");
		}
		return packageComm.replaceAll("\\.", File.separator);
	}

	private String getPackageDirectory() {
		return sourceDirectory + File.separator + getPackageCommDirectory();
	}

	private void createPackageDirectory() {
		File packageDirectory = new File(getPackageDirectory());
		packageDirectory.mkdirs();
	}

	private Set<String> extractAnnotationImports(EurekaService eurekaService) {
		Set<String> annotationImports = new TreeSet<>();
		for (FeignInterfaceMethod method : serviceToMethods.get(eurekaService)) {
			annotationImports.addAll(method.getAnnotationDependencies());
		}
		return annotationImports;
	}

	private Set<String> extractImports(EurekaService eurekaService) {
		Set<String> imports = new TreeSet<>();
		for (FeignInterfaceMethod method : serviceToMethods.get(eurekaService)) {
			imports.addAll(method.getTypeDependencies());
		}
		return imports;
	}

	public void persist() throws Exception {
		createPackageDirectory();
		persistConfigurationClass();
		for (EurekaService eurekaService : serviceToMethods.keySet()) {
			if (!serviceToMethods.get(eurekaService).isEmpty()) {
				persistInterface(eurekaService);
			}
		}

	}

	private String getConfiguratioClassFilename() {
		return getPackageDirectory() + File.separator + FEIGN_CLIENT_CONFIGURATION_CLASS + ".java";
	}

	private void persistConfigurationClass() throws IOException {
		File configurationlass = new File(getConfiguratioClassFilename());
		if (configurationlass.exists()) {
			Files.delete(configurationlass.toPath());
			configurationlass = Files.createFile(configurationlass.toPath()).toFile();
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(configurationlass));
			// Write Package
			writer.append("package ");
			writer.append(packageComm);
			writer.append(";\n\n");
			writer.append(FEIGN_CLIENT_CONFIGURATION);
		} catch (IOException exception) {
			throw exception;
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

	}

	private String getMsInterfaceFilename(EurekaService eurekaService) {
		return getPackageDirectory() + File.separator + eurekaService.getInterfaceName() + ".java";
	}

	private void persistInterface(EurekaService eurekaService) throws IOException {
		File fileInterface = new File(getMsInterfaceFilename(eurekaService));
		if (fileInterface.exists()) {
			Files.delete(fileInterface.toPath());
			fileInterface = Files.createFile(fileInterface.toPath()).toFile();
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(fileInterface));
			// Write Package
			writer.append("package ");
			writer.append(packageComm);
			writer.append(";\n\n");
			// Write imports
			for (String importName : extractAnnotationImports(eurekaService)) {
				writer.append("import ");
				writer.append(importName);
				writer.append(";\n");
			}
			writer.append("\n");
			for (String importName : extractImports(eurekaService)) {
				writer.append("import ");
				writer.append(importName);
				writer.append(";\n");
			}
			// Write interface declaration
			writer.append("\n@FeignClient(value=\"");
			writer.append(eurekaService.getServiceName());
			writer.append("\")\npublic interface ");
			writer.append(eurekaService.getInterfaceName());
			writer.append(" {\n\n");
			// Write interface methods
			for (FeignInterfaceMethod method : serviceToMethods.get(eurekaService)) {
				writer.append(method.createMethod());
				writer.append("\n\n");
			}
			writer.append("}\n");
		} catch (IOException exception) {
			throw exception;
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
