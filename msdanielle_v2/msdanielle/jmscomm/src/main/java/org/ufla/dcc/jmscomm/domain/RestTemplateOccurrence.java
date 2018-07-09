package org.ufla.dcc.jmscomm.domain;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Collection;

public class RestTemplateOccurrence {

	public static final String HEADER_REPORT = "filepath\toccurrence\toccurenceRange\tmicroservice\thasProblem\tproblemReason";

	private static final String URL_TEMPLATE_REGEX = "^https?://[a-zA-Z0-9\\-\\.]+:[0-9]{1,5}.*$";

	public static void main(String... args) {
		System.out.println("http://localhost:9092/getProducts".matches(URL_TEMPLATE_REGEX));
	}

	private String occurence;

	private Range occurenceRange;

	private Range expressionRange;

	private Range nameRange;

	private Range urlRange;

	private Range bodyVariableRange;

	private String urlTemplate;

	private Collection<Range> urlParamRanges;

	private boolean problem = false;

	private String problemReason;

	private EurekaService eurekaService;

	private FeignInterfaceMethod feignInterfaceMethod;

	private String callMethod;

	public RestTemplateOccurrence() {

	}

	public String reportEntryTsv(File file) {
		StringBuilder sb = new StringBuilder();
		sb.append(file.getAbsolutePath()).append('\t').append(occurence).append('\t').append(occurenceRange.toString())
				.append('\t').append(eurekaService == null ? "null" : eurekaService.getServiceName()).append('\t')
				.append(String.valueOf(problem)).append('\t').append(problemReason);
		return sb.toString();
	}

	public boolean completeFeignMethod(File file) throws Exception {
		int startUrl = urlTemplate.indexOf(':') + 1;
		startUrl = urlTemplate.indexOf(':', startUrl) + 1;
		startUrl = urlTemplate.indexOf('/', startUrl);
		int startRequestParam = urlTemplate.indexOf('?', startUrl);
		if (!extractRequestParam(startUrl, startRequestParam)) {
			return false;
		}
		int endUrl = (startRequestParam == -1) ? urlTemplate.length() : startRequestParam;
		String url = urlTemplate.substring(startUrl, endUrl);
		if (!extractPathVariable(url)) {
			return false;
		}
		feignInterfaceMethod.setName(urlToName(url));
		feignInterfaceMethod.setUrl(url);
		try {
			defineCallMethod(file);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Não foi possível ler o arquivo '" + file.getAbsolutePath() + "'!");
		}
		return true;
	}

	private String readFile(File file, Range range) throws IOException {
		RandomAccessFile raFile = new RandomAccessFile(file, "r");
		raFile.seek(range.getStart());
		byte[] bytes = new byte[range.getEnd() - range.getStart()];
		raFile.read(bytes);
		raFile.close();
		return new String(bytes, Charset.forName("UTF-8"));

	}

	public void defineCallMethod(File file) throws IOException {
		StringBuilder sb = new StringBuilder("\t\t");
		sb.append(eurekaService.getAttrName()).append('.').append(feignInterfaceMethod.getName()).append("(");
		if (bodyVariableRange != null) {
			sb.append(readFile(file, bodyVariableRange));
			if (!urlParamRanges.isEmpty()) {
				sb.append(", ");
			}
		}
		for (Range range : urlParamRanges) {
			sb.append(readFile(file, range)).append(", ");
		}
		if (!urlParamRanges.isEmpty()) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append(')');
		callMethod = sb.toString();
	}

	private boolean extractPathVariable(String url) {
		Collection<String> pathVariableNames = feignInterfaceMethod.getPathVariableNames();
		int startPathVar = url.indexOf('{');
		while (startPathVar != -1) {
			if (url.charAt(startPathVar - 1) != '/') {
				problem = true;
				problemReason = "PathVariable";
				return false;
			}
			startPathVar++;
			int endPathVar = url.indexOf('}', startPathVar);
			if (endPathVar != url.length() - 1 && url.charAt(endPathVar + 1) != '/') {
				problem = true;
				problemReason = "PathVariable";
				return false;
			}
			String pathVar = url.substring(startPathVar, endPathVar);
			pathVariableNames.add(pathVar);
			startPathVar = url.indexOf('{', endPathVar);
		}
		return true;
	}

	private boolean extractRequestParam(int startUrl, int startRequestParam) {
		Collection<String> requestParamNames = feignInterfaceMethod.getRequestParamNames();
		while (startRequestParam != -1) {
			startRequestParam++;
			int endRequestParam = urlTemplate.indexOf('=', startRequestParam);
			String requestParam = urlTemplate.substring(startRequestParam, endRequestParam);
			if (!validRequestParam(requestParam)) {
				problem = true;
				problemReason = "RequestParam";
				return false;
			}
			requestParamNames.add(requestParam);
			startRequestParam = urlTemplate.indexOf('&', endRequestParam);
		}
		return true;
	}

	public Range getBodyVariableRange() {
		return bodyVariableRange;
	}

	public String getCallMethod() {
		return callMethod;
	}

	public EurekaService getEurekaService() {
		return eurekaService;
	}

	public Range getExpressionRange() {
		return expressionRange;
	}

	public FeignInterfaceMethod getFeignInterfaceMethod() {
		return feignInterfaceMethod;
	}

	public String getHostname() {
		int startHostname = urlTemplate.indexOf("://") + 3;
		int endHostName = urlTemplate.indexOf(':', startHostname);
		endHostName = urlTemplate.indexOf('/', endHostName);
		return urlTemplate.substring(startHostname, endHostName);
	}

	public Range getNameRange() {
		return nameRange;
	}

	public String getOccurence() {
		return occurence;
	}

	public Range getOccurenceRange() {
		return occurenceRange;
	}

	public String getProblemReason() {
		return problemReason;
	}

	public Collection<Range> getUrlParamRanges() {
		return urlParamRanges;
	}

	public Range getUrlRange() {
		return urlRange;
	}

	public String getUrlTemplate() {
		return urlTemplate;
	}

	public boolean isProblem() {
		return problem;
	}

	public void setBodyVariableRange(Range bodyVariableRange) {
		this.bodyVariableRange = bodyVariableRange;
	}

	public void setCallMethod(String callMethod) {
		this.callMethod = callMethod;
	}

	public void setEurekaService(EurekaService eurekaService) {
		this.eurekaService = eurekaService;
	}

	public void setExpressionRange(Range expressionRange) {
		this.expressionRange = expressionRange;
	}

	public void setFeignInterfaceMethod(FeignInterfaceMethod feignInterfaceMethod) {
		this.feignInterfaceMethod = feignInterfaceMethod;
	}

	public void setNameRange(Range nameRange) {
		this.nameRange = nameRange;
	}

	public void setOccurence(String occurence) {
		this.occurence = occurence;
	}

	public void setOccurenceRange(Range occurenceRange) {
		this.occurenceRange = occurenceRange;
	}

	public void setProblem(boolean problem) {
		this.problem = problem;
	}

	public void setProblemReason(String problemReason) {
		this.problemReason = problemReason;
	}

	public void setUrlParamRanges(Collection<Range> urlParamRanges) {
		this.urlParamRanges = urlParamRanges;
	}

	public void setUrlRange(Range urlRange) {
		this.urlRange = urlRange;
	}

	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}

	private String urlToName(String url) {
		int endName = url.indexOf('{');
		if (endName == -1) {
			endName = url.length();
		}
		StringBuilder name = new StringBuilder(url.substring(1, endName));
		if (name.charAt(name.length() - 1) == '/') {
			name.deleteCharAt(name.length() - 1);
		}
		int div = name.indexOf("/");
		while (div != -1) {
			name.deleteCharAt(div);
			name.setCharAt(div, Character.toUpperCase(name.charAt(div)));
			div = name.indexOf("/", div);
		}
		return name.toString();
	}

	public boolean validateHostnameAndReplaceByIpAddress() {
		if (urlTemplate.matches(URL_TEMPLATE_REGEX) && validateHostnameAndReplaceByIpAddressIntern()) {
			return true;
		}
		problem = true;
		problemReason = "hostname/ip";
		return false;
	}

	private boolean validateHostnameAndReplaceByIpAddressIntern() {
		try {
			int start = urlTemplate.indexOf(':') + 3;
			int end = urlTemplate.indexOf(':', start);
			String hostname = urlTemplate.substring(start, end);
			InetAddress address = InetAddress.getByName(hostname);
			String ipAddress = address.getHostAddress();
			urlTemplate.replaceFirst(hostname, ipAddress);
			return true;
		} catch (UnknownHostException exception) {
			return false;
		}
	}

	private boolean validRequestParam(String requestParam) {
		return requestParam.indexOf('{') == -1 && requestParam.indexOf('}') == -1;
	}

}
