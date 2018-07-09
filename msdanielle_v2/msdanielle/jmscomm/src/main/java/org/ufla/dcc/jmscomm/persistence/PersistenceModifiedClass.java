package org.ufla.dcc.jmscomm.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.ufla.dcc.jmscomm.domain.EurekaService;
import org.ufla.dcc.jmscomm.domain.RestTemplateOccurrence;

public class PersistenceModifiedClass {
	
	private static final String AUTOWIRED_IMPORT = "import org.springframework.beans.factory.annotation.Autowired;";
	
	private String packageComm;
	
	private Map<File, Collection<RestTemplateOccurrence>> occurrences;

	public PersistenceModifiedClass(Map<File, Collection<RestTemplateOccurrence>> occurrences, String packageComm) {
		this.occurrences = occurrences;
		this.packageComm = packageComm;
	}
	
	private boolean hasOccurrenceToConvert(File file) {
		for (RestTemplateOccurrence occurrence : occurrences.get(file)) {
			if (!occurrence.isProblem()) {
				return true;
			}
		}
		return false;
	}
	
	private void defineFileProblem(File file) {
		for (RestTemplateOccurrence occurrence : occurrences.get(file)) {
			if (!occurrence.isProblem()) {
				occurrence.setProblem(true);
				occurrence.setProblemReason("Escrever arquivo");
			}
		}
	}
	
	public void persist() throws Exception {
		for (File file : occurrences.keySet()) {
			if (hasOccurrenceToConvert(file)) {
				if (!persistFile(file)) {
					defineFileProblem(file);
				}
			}
		}
	}
	
	private boolean copyFile(File file, File backupFile) {
		try {
			Files.copy(file.toPath(), backupFile.toPath());
		} catch (FileAlreadyExistsException alreadyExistsException) {
			try {
				Path backupPath = backupFile.toPath();
				Files.delete(backupPath);
				Files.copy(file.toPath(), backupPath);
			} catch (IOException exception) {
				exception.printStackTrace();
				System.out.println("Não foi possível fazer a cópia do arquivo '" + file.getAbsolutePath() + "'");
				return false;
			}
		} catch (IOException exception) {
			exception.printStackTrace();
			System.out.println("Não foi possível fazer a cópia do arquivo '" + file.getAbsolutePath() + "'");
			return false;
		}
		return true;
	}
	
	private String getFileContent(File file) throws Exception {
		try {
			return FileUtils.readFileToString(file, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Não foi possível ler o arquivo '" + file.getAbsolutePath() + "'!");
		}
	}
	
	private Set<EurekaService> extractServiceDependencies(File file) {
		Set<EurekaService> dependencies = new HashSet<>();
		for (RestTemplateOccurrence occurrence : occurrences.get(file)) {
			if (!occurrence.isProblem()) {
				dependencies.add(occurrence.getEurekaService());
			}
		}
		return dependencies;
	}
	
	private Set<String> extractImports(File file) {
		Set<String> imports = new TreeSet<>();
		for (RestTemplateOccurrence occurrence : occurrences.get(file)) {
			if (!occurrence.isProblem()) {
				imports.add(packageComm + "." + occurrence.getEurekaService().getInterfaceName());
			}
		}
		return imports;
	}
	
	private boolean persistFile(File file) throws Exception {
		File backupFile = new File(file.getAbsolutePath() + ".bak");
		if (!copyFile(file, backupFile)) {
			return false;
		}
		File modifiedFile = new File(file.getAbsoluteFile() + ".temp");

		String fileContent = getFileContent(file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(modifiedFile));
			int indexInsertImports = fileContent.indexOf(';') + 1;
			writer.append(fileContent.substring(0, indexInsertImports));
			// Write imports
			writer.append("\n\n// BEGIN - New imports\n");
			for (String importName : extractImports(file)) {
				writer.append("import ");
				writer.append(importName);
				writer.append(";\n");
			}
			if (!fileContent.contains(AUTOWIRED_IMPORT)) {
				writer.append(AUTOWIRED_IMPORT).append('\n');
			}
			writer.append("// END - New imports\n\n");
			int indexBeginClass = fileContent.indexOf("class", indexInsertImports);
			indexBeginClass = fileContent.indexOf('{', indexBeginClass) + 1;
			writer.append(fileContent.substring(indexInsertImports, indexBeginClass));
			// Write attr
			writer.append("\n\n\t// BEGIN - Attr dependencies\n");
			for (EurekaService dependencie : extractServiceDependencies(file)) {
				writer.append("\t@Autowired\n");
				writer.append("\tprivate ");
				writer.append(dependencie.getInterfaceName());
				writer.append(" ");
				writer.append(dependencie.getAttrName());
				writer.append(";\n");
			}
			writer.append("\t// END - Attr dependencies\n\n");
			// Write convert communication
			int actualIndex = indexBeginClass;
			for (RestTemplateOccurrence occurrence : occurrences.get(file)) {
				if (!occurrence.isProblem()) {
					writer.append(fileContent.substring(actualIndex, occurrence.getOccurenceRange().getStart()));
					writer.append(occurrence.getCallMethod());
					actualIndex = occurrence.getOccurenceRange().getEnd();
				}
			}
			writer.append(fileContent.substring(actualIndex));
		} catch (IOException exception) {
			exception.printStackTrace();
			throw new Exception("Não foi possível escrever o arquivo '" + file.getAbsolutePath() + "'");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		Path filePath = file.toPath();
		Files.delete(filePath);
		Files.move(modifiedFile.toPath(), filePath);
		return true;
	}

}
