package org.ufla.dcc.jmscomm.extractor.util;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;

public final class ExtractSourcepathAndClasspathUtil {
	
	private ExtractSourcepathAndClasspathUtil() {}
	
	/**
	 * Recupera todos jars que estão definidos no projeto, no Java e no Maven de um sistema.
	 * 
	 * @param projectDirectory
	 * @return
	 */
	public static String[] extractClasspathEntries(String projectDirectory) {
		Collection<String> jars = new ArrayList<>();
		includeClasspathEntriesRec(projectDirectory, jars);
		includeClasspathEntriesRec(System.getProperty("java.home"), jars);
		includeClasspathEntriesRec(System.getProperty("user.home") + File.separator + ".m2", jars);
		return jars.toArray(new String[jars.size()]);
	}
	
	/**
	 * Inclui na coleção de jars, todos jars definidos dentro de um determinado diretório.
	 * 
	 * @param rootDirectory
	 * @param jars
	 */
	public static void includeClasspathEntriesRec(String rootDirectory, Collection<String> jars) {
		Deque<File> directories = new ArrayDeque<>();
		File rootDirectoryFile = new File(rootDirectory);
		if (!rootDirectoryFile.exists()) {
			return;
		}
		directories.push(rootDirectoryFile);
		while (!directories.isEmpty()) {
			File directory = directories.pop();
			for (File child : directory.listFiles()) {
				if (child.isDirectory()) {
					directories.push(child);
				} else if (child.getName().endsWith(".jar")) {
					jars.add(child.getAbsolutePath());
				}
			}
		}
	}
	
	/**
	 * Recupera todos o caminho de todos diretórios de código fonte de um projeto.
	 * 
	 * @param projectDirectory
	 * @return
	 */
	public static String[] extractSourcepathEntries(String projectDirectory) {
		Collection<String> sourceDirectories = new ArrayList<>();
		Deque<File> directories = new ArrayDeque<>();
		directories.push(new File(projectDirectory));
		while (!directories.isEmpty()) {
			File directory = directories.pop();
			for (File child : directory.listFiles()) {
				if (child.isDirectory()) {
					if (child.getName().endsWith("src")) {
						sourceDirectories.add(child.getAbsolutePath());
					} else {
						directories.push(child);
					}
				}
			}
		}
		return sourceDirectories.toArray(new String[sourceDirectories.size()]);
	}

}
