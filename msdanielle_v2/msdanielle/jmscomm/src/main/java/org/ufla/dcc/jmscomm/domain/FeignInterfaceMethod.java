package org.ufla.dcc.jmscomm.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.ufla.dcc.jmscomm.extractor.util.TypeUtils;

public class FeignInterfaceMethod {
	
	public static final String ANNOTATION_FEIGN_CLIENT = "org.springframework.cloud.netflix.feign.FeignClient";
	public static final String ANNOTATION_GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
	public static final String ANNOTATION_POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
	public static final String ANNOTATION_REQUEST_BODY = "org.springframework.web.bind.annotation.RequestBody";
	public static final String ANNOTATION_REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam";
	public static final String ANNOTATION_PATH_VARIABLE = "org.springframework.web.bind.annotation.PathVariable";


	private static final String URL_PARAM = "{url}";

	private String name;

	private String url;

	private HttpMethod httpMethod;

	private String returnType;

	private Collection<String> pathVariableNames;

	private Collection<String> requestParamNames;

	private List<String> urlParamTypes;

	private String bodyVariableName;

	private String bodyVariableType;

	public FeignInterfaceMethod() {
		pathVariableNames = new ArrayList<>();
		requestParamNames = new ArrayList<>();
	}
	
	public Set<String> getAnnotationDependencies() {
		Set<String> annotationDependencies = new TreeSet<>();
		annotationDependencies.add(ANNOTATION_FEIGN_CLIENT);
		annotationDependencies.add(httpMethod.getAnnotationDependency());
		if (bodyVariableType != null) {
			annotationDependencies.add(ANNOTATION_REQUEST_BODY);
		}
		if (!pathVariableNames.isEmpty()) {
			annotationDependencies.add(ANNOTATION_PATH_VARIABLE);
		}
		if (!requestParamNames.isEmpty()) {
			annotationDependencies.add(ANNOTATION_REQUEST_PARAM);
		}
		return annotationDependencies;
	}
	
	public Set<String> getTypeDependencies() {
		Set<String> typeDependencies = new TreeSet<>();
		if (!TypeUtils.isPrimitiveType(returnType)) {
			typeDependencies.add(returnType); 
		}
		if (bodyVariableType != null && !TypeUtils.isPrimitiveType(bodyVariableType)) {
			typeDependencies.add(bodyVariableType); 
		}
		for (String paramType : urlParamTypes) {
			if (!TypeUtils.isPrimitiveType(paramType)) {
				typeDependencies.add(paramType); 
			}
		}
		return typeDependencies;
	}

	public String createMethod() {
		StringBuilder method = new StringBuilder();
		method.append('\t').append(httpMethod.getMethodTemplate().replace(URL_PARAM, url)).append("\n\t")
				.append(TypeUtils.getSimpleType(returnType)).append(' ').append(name).append('(');
		if (bodyVariableName != null) {
			method.append("@RequestBody ").append(TypeUtils.getSimpleType(bodyVariableType)).append(' ')
					.append(bodyVariableName);
			if (!pathVariableNames.isEmpty() || !requestParamNames.isEmpty()) {
				method.append(", ");
			}
		}
		int paramIndex = 0;
		if (!pathVariableNames.isEmpty()) {
			for (String pathVariable : pathVariableNames) {
				method.append("@PathVariable(\"").append(pathVariable).append("\") ")
						.append(TypeUtils.getSimpleType(urlParamTypes.get(paramIndex))).append(' ').append(pathVariable)
						.append(", ");
				paramIndex++;
			}
			if (requestParamNames.isEmpty()) {
				int length = method.length();
				method.delete(length - 2, length);
			}
		}
		for (String requestParam : requestParamNames) {
			method.append("@RequestParam(\"").append(requestParam).append("\") ")
					.append(TypeUtils.getSimpleType(urlParamTypes.get(paramIndex))).append(' ').append(requestParam)
					.append(", ");
			paramIndex++;
		}
		if (!requestParamNames.isEmpty()) {
			int length = method.length();
			method.delete(length - 2, length);
		}
		return method.append(");\n").toString();
	}

	public String getBodyVariableName() {
		return bodyVariableName;
	}

	public String getBodyVariableType() {
		return bodyVariableType;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public String getName() {
		return name;
	}

	public Collection<String> getPathVariableNames() {
		return pathVariableNames;
	}

	public Collection<String> getRequestParamNames() {
		return requestParamNames;
	}

	public String getReturnType() {
		return returnType;
	}

	public String getUrl() {
		return url;
	}

	public void setBodyVariableName(String bodyVariableName) {
		this.bodyVariableName = bodyVariableName;
	}

	public void setBodyVariableType(String bodyVariableType) {
		if ("null".equals(bodyVariableType)) {
			this.bodyVariableType = "java.lang.Object";
		} else {
			this.bodyVariableType = bodyVariableType;
		}
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPathVariableNames(Collection<String> pathVariableNames) {
		this.pathVariableNames = pathVariableNames;
	}

	public void setRequestParamNames(Collection<String> requestParamNames) {
		this.requestParamNames = requestParamNames;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getUrlParamTypes() {
		return urlParamTypes;
	}

	public void setUrlParamTypes(List<String> urlParamTypes) {
		this.urlParamTypes = urlParamTypes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bodyVariableType == null) ? 0 : bodyVariableType.hashCode());
		result = prime * result + ((httpMethod == null) ? 0 : httpMethod.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((urlParamTypes == null) ? 0 : urlParamTypes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeignInterfaceMethod other = (FeignInterfaceMethod) obj;
		if (bodyVariableType == null) {
			if (other.bodyVariableType != null)
				return false;
		} else if (!bodyVariableType.equals(other.bodyVariableType))
			return false;
		if (httpMethod != other.httpMethod)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (urlParamTypes == null) {
			if (other.urlParamTypes != null)
				return false;
		} else if (!urlParamTypes.equals(other.urlParamTypes))
			return false;
		return true;
	}

}
