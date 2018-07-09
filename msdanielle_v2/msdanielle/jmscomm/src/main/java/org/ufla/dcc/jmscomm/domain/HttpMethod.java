package org.ufla.dcc.jmscomm.domain;

public enum HttpMethod {
	
	GET("@GetMapping(\"{url}\")", FeignInterfaceMethod.ANNOTATION_GET_MAPPING), 
	POST("@PostMapping(\"{url}\")", FeignInterfaceMethod.ANNOTATION_POST_MAPPING);
	
	
	private String methodTemplate;
	private String annotationDependency;
	
	private HttpMethod(String methodTemplate, String annotationDependency) {
		this.methodTemplate = methodTemplate;
		this.annotationDependency = annotationDependency;
	}

	public String getMethodTemplate() {
		return methodTemplate;
	}

	public String getAnnotationDependency() {
		return annotationDependency;
	}

}
