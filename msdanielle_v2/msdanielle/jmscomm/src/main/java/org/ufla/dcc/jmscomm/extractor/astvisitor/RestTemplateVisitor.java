package org.ufla.dcc.jmscomm.extractor.astvisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.ufla.dcc.jmscomm.domain.FeignInterfaceMethod;
import org.ufla.dcc.jmscomm.domain.HttpMethod;
import org.ufla.dcc.jmscomm.domain.Range;
import org.ufla.dcc.jmscomm.domain.RestTemplateOccurrence;

public class RestTemplateVisitor extends ASTVisitor {

	private static final String REST_TEMPLATE_TYPE = "org.springframework.web.client.RestTemplate";
	private static final String GET_FOR_OBJECT_METHOD = "getForObject";
	private static final String POST_FOR_OBJECT_METHOD = "postForObject";
	private static final int GET_FOR_OBJECT_ARGS_SIZE = 2;
	private static final int POST_FOR_OBJECT_ARGS_SIZE = 3;
	private static final String FIRST_ARG_TYPE = "java.lang.String";
	private static final Set<String> VALID_METHODS = new HashSet<>(
			Arrays.asList(GET_FOR_OBJECT_METHOD, POST_FOR_OBJECT_METHOD));

	private Collection<RestTemplateOccurrence> restTemplateOccurrences;

	public RestTemplateVisitor() {
		restTemplateOccurrences = new ArrayList<>();
	}

	public Collection<RestTemplateOccurrence> getRestTemplateOccurrences() {
		return this.restTemplateOccurrences;
	}

	private boolean isRestTemplate(ITypeBinding expressionType) {
		return REST_TEMPLATE_TYPE.equals(expressionType.getQualifiedName());
	}

	private boolean isValidRestTemplate(MethodInvocation node) {
		ITypeBinding expressionType = node.getExpression().resolveTypeBinding();
		return isRestTemplate(expressionType) && isValidRestTemplateMethod(node);
	}

	private boolean isValidRestTemplateMethod(MethodInvocation node) {
		String methodName = node.getName().getIdentifier();
		int argsSize = node.arguments().size();
		String firstArgType = null;
		if (argsSize > 0) {
			Expression firstArgument = (Expression) node.arguments().get(0);
			firstArgType = firstArgument.resolveTypeBinding().getQualifiedName();
		}
		if (!VALID_METHODS.contains(methodName)) {
			return false;
		}
		if (GET_FOR_OBJECT_METHOD.equals(methodName)) {
			return argsSize == GET_FOR_OBJECT_ARGS_SIZE && FIRST_ARG_TYPE.equals(firstArgType);
		} else if (POST_FOR_OBJECT_METHOD.equals(methodName)) {
			return argsSize == POST_FOR_OBJECT_ARGS_SIZE && FIRST_ARG_TYPE.equals(firstArgType);
		}
		return false;
	}
	
	private HttpMethod getHtttpMethod(MethodInvocation node) {
		String methodName = node.getName().getIdentifier();
		if (GET_FOR_OBJECT_METHOD.equals(methodName)) {
			return HttpMethod.GET;
		}
		return HttpMethod.POST;
		
	}
	
	private Range setbodyVariable(MethodInvocation node, FeignInterfaceMethod feignInterfaceMethod) {
		if (!POST_FOR_OBJECT_METHOD.equals(node.getName().getIdentifier())) {
			return null;
		}
		Expression arg = (Expression) node.arguments().get(1);
		ExtractNameNodeVisitor extractNameNodeVisitor = new ExtractNameNodeVisitor();
		arg.accept(extractNameNodeVisitor);
		feignInterfaceMethod.setBodyVariableName(extractNameNodeVisitor.getName());
		feignInterfaceMethod.setBodyVariableType(arg.resolveTypeBinding().getQualifiedName());
		return ASTNodeRangeFactory.createRange(arg);
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		if (!isValidRestTemplate(node)) {
			return true;
		}
		RestTemplateOccurrence restTemplateOccurrence = new RestTemplateOccurrence();
		FeignInterfaceMethod feignInterfaceMethod = new FeignInterfaceMethod();
		feignInterfaceMethod.setHttpMethod(getHtttpMethod(node));
		feignInterfaceMethod.setReturnType(node.resolveTypeBinding().getQualifiedName());
		restTemplateOccurrence.setBodyVariableRange(setbodyVariable(node, feignInterfaceMethod));
		restTemplateOccurrence.setFeignInterfaceMethod(feignInterfaceMethod);
		restTemplateOccurrence.setOccurence(node.toString());
		restTemplateOccurrence.setOccurenceRange(ASTNodeRangeFactory.createRange(node));
		restTemplateOccurrence.setExpressionRange(ASTNodeRangeFactory.createRange(node.getExpression()));
		restTemplateOccurrence.setNameRange(ASTNodeRangeFactory.createRange(node.getName()));
		ASTNode urlArg = (ASTNode) node.arguments().get(0);
		restTemplateOccurrence.setUrlRange(ASTNodeRangeFactory.createRange(urlArg));
		ExtractUrlTemplateVisitor extractUrlTemplateVisitor = new ExtractUrlTemplateVisitor();
		urlArg.accept(extractUrlTemplateVisitor);
		feignInterfaceMethod.setUrlParamTypes(extractUrlTemplateVisitor.getUrlParamTypes());
		restTemplateOccurrence.setUrlTemplate(extractUrlTemplateVisitor.getUrlTemplate());
		restTemplateOccurrence.setUrlParamRanges(extractUrlTemplateVisitor.getUrlParamRanges());
		restTemplateOccurrences.add(restTemplateOccurrence);
		return true;
	}

}
