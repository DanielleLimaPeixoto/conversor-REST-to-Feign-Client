package org.ufla.dcc.jmscomm.extractor.astvisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.ufla.dcc.jmscomm.domain.Range;

public class ExtractUrlTemplateVisitor extends ASTVisitor {

	private StringBuilder urlTemplate;

	private Collection<Range> urlParamRanges;
	
	private List<String> urlParamTypes;

	public ExtractUrlTemplateVisitor() {
		urlTemplate = new StringBuilder();
		urlParamRanges = new ArrayList<>();
		urlParamTypes = new ArrayList<>();
	}

	private void appendUrlParam(Expression node, String param) {
		urlTemplate.append('{').append(param).append('}');
		urlParamRanges.add(ASTNodeRangeFactory.createRange(node));
		urlParamTypes.add(node.resolveTypeBinding().getQualifiedName());
	}
	
	public Collection<Range> getUrlParamRanges() {
		return urlParamRanges;
	}
	
	public String getUrlTemplate() {
		return urlTemplate.toString();
	}

	@Override
	public boolean visit(MethodInvocation node) {
		appendUrlParam(node, node.getName().getFullyQualifiedName());
		return false;
	}

	@Override
	public boolean visit(ParenthesizedExpression node) {
		appendUrlParam(node, "parenthesized");
		return false;
	}

	@Override
	public boolean visit(QualifiedName node) {
		appendUrlParam(node, node.getName().getFullyQualifiedName());
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		appendUrlParam(node, node.getFullyQualifiedName());
		return false;
	}

	@Override
	public boolean visit(StringLiteral node) {
		urlTemplate.append(node.getLiteralValue());
		return false;
	}

	public List<String> getUrlParamTypes() {
		return urlParamTypes;
	}

}
