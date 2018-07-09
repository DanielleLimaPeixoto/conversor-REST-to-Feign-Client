package org.ufla.dcc.jmscomm.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.ufla.dcc.jmscomm.domain.RestTemplateOccurrence;

public class GenerateReport {

	private String projectDirectory;

	private Map<File, Collection<RestTemplateOccurrence>> occurrences;

	public GenerateReport(String projectDirectory, Map<File, Collection<RestTemplateOccurrence>> occurrences) {
		this.projectDirectory = projectDirectory;
		this.occurrences = occurrences;
	}

	public void generate() throws Exception {
		File reportFile = new File(projectDirectory + File.separator + "report.tsv");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(reportFile));
			writer.append(RestTemplateOccurrence.HEADER_REPORT).append('\n');
			for (File file : occurrences.keySet()) {
				for (RestTemplateOccurrence occurence : occurrences.get(file)) {
					writer.append(occurence.reportEntryTsv(file)).append('\n');
				}
			}
			System.out.println("Foi gerado um relatório no arquivo '" + reportFile.getAbsolutePath()
					+ "' listando as ocorrências de uso de métodos RestTemplate que foram encontradas e possíveis problemas encontrados para converter algumas delas para a interface FeignClient.\n");
		} catch (IOException exception) {
			exception.printStackTrace();
			throw new Exception(
					"Não foi possível escrever o relatório no arquivo '" + reportFile.getAbsolutePath() + "'");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
