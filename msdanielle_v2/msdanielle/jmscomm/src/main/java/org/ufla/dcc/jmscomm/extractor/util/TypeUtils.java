package org.ufla.dcc.jmscomm.extractor.util;

public final class TypeUtils {
	
	public static final String getSimpleType(String type) {
		return type.substring(type.lastIndexOf('.') + 1);
	}
	
	public static final boolean isPrimitiveType(String type) {
		return type.indexOf('.') == -1;
	}

}
