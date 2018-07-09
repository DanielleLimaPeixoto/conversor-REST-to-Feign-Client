package org.ufla.dcc.jmscomm.extractor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.ufla.dcc.jmscomm.domain.RestTemplateOccurrence;
import org.ufla.dcc.jmscomm.extractor.astvisitor.RestTemplateVisitor;
import org.ufla.dcc.jmscomm.extractor.util.ExtractSourcepathAndClasspathUtil;

public class ExtractRestTemplateOccurrences {
	
	private static final String ENCODING = "UTF-8";
	
	private String projectDirectoryStr;
	
	private String[] sourcepathEntries;
	
	private String[] sourcepathEncodings;
	
	private String[] classpathEntries;
	
	private Map<File, Collection<RestTemplateOccurrence>> restTemplateOccurancesPerFile;
	
	public ExtractRestTemplateOccurrences(String projectDirectoryStr) {
		this.projectDirectoryStr = projectDirectoryStr;
		this.sourcepathEntries = ExtractSourcepathAndClasspathUtil.extractSourcepathEntries(projectDirectoryStr);
		this.classpathEntries = ExtractSourcepathAndClasspathUtil.extractClasspathEntries(projectDirectoryStr);
		defineSourcepathEncodings();
	}
	
	public static void main(String...args) throws Exception {
		String projectTest = System.getProperty("user.home") + "/workspaces/microservices/msdanielle/toyexample/TesteSale";
		String fileTest = projectTest + "/src/main/java/com/controller/SaleController.java";
		ExtractRestTemplateOccurrences extraRestOccurrences = new ExtractRestTemplateOccurrences(projectTest);
		extraRestOccurrences.restTemplateOccurancesPerFile = new HashMap<>();
		extraRestOccurrences.extractRestTemplateOccurencesInFile(new File(fileTest));
		System.out.println("OK");
	}
	
	private void defineSourcepathEncodings() {
		this.sourcepathEncodings = new String[sourcepathEntries.length];
		Arrays.fill(sourcepathEncodings, ENCODING);
	}
	
	public Map<File, Collection<RestTemplateOccurrence>> extractRestTemplateOccurences() throws Exception {
		if (restTemplateOccurancesPerFile == null) {
			
			extractRestTemplateOccurencesIntern();
		}
		return restTemplateOccurancesPerFile;
	}
	
	private void extractRestTemplateOccurencesIntern() throws Exception {
		System.out.println("--------------------------------------------------");
		System.out.println("Extraindo ocorrências de uso de métodos RestTemplate no projeto...");
		restTemplateOccurancesPerFile = new HashMap<>();
		File projectDirectory = new File(projectDirectoryStr);
		Deque<File> directories = new ArrayDeque<>();
		directories.push(projectDirectory);
		while (!directories.isEmpty()) {
			for (File file : directories.pop().listFiles()) {
				if (file.isDirectory()) {
					directories.push(file);
				} else {
					if (file.getName().endsWith(".java")) {
						extractRestTemplateOccurencesInFile(file);
					}
				}
			}
		}
		System.out.println("Ocorrências de uso de métodos RestTemplate no projeto extraídas com sucesso.");
		System.out.println("--------------------------------------------------");
	}
	
	private String getFileContent(File file) throws Exception {
		try {
			return FileUtils.readFileToString(file, Charset.forName(ENCODING));
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Não foi possível ler o arquivo '" + file.getAbsolutePath() + "'!");
		}
	}
	
	private void extractRestTemplateOccurencesInFile(File file) throws Exception {
		String fileContent = getFileContent(file);
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		Map<String, String> options = JavaCore.getDefaultOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		parser.setCompilerOptions(options);
		parser.setEnvironment(classpathEntries, sourcepathEntries, sourcepathEncodings, true);
		parser.setSource(fileContent.toCharArray());
		parser.setUnitName("ExtractRestTemplate");
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
 
		CompilationUnit comUnit = (CompilationUnit) parser.createAST(null);
 
		RestTemplateVisitor restTemplateVisitor = new RestTemplateVisitor();
		comUnit.accept(restTemplateVisitor);
		
		Collection<RestTemplateOccurrence> restTemplateOccurrences = restTemplateVisitor.getRestTemplateOccurrences();
		if (restTemplateOccurrences.isEmpty()) {
			return;
		}
		
		restTemplateOccurancesPerFile.put(file, restTemplateOccurrences);
		
	}

}
