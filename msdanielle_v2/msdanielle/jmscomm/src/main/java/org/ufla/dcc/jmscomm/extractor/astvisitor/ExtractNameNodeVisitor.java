package org.ufla.dcc.jmscomm.extractor.astvisitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;

public class ExtractNameNodeVisitor extends ASTVisitor {

	private String name;

	public ExtractNameNodeVisitor() {
		name = "bodyObject";
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		name = node.getName().getFullyQualifiedName();
		return false;
	}

	@Override
	public boolean visit(QualifiedName node) {
		name = node.getName().getFullyQualifiedName();
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		name = node.getFullyQualifiedName();
		return false;
	}

}
