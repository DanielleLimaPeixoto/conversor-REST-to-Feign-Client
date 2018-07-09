package org.ufla.dcc.jmscomm.extractor.astvisitor;

import org.eclipse.jdt.core.dom.ASTNode;
import org.ufla.dcc.jmscomm.domain.Range;

public final class ASTNodeRangeFactory {
	
	private ASTNodeRangeFactory() {}
	
	public static Range createRange(ASTNode node) {
		Range range = new Range();
		range.setStart(node.getStartPosition());
		range.setEndByLength(node.getLength());
		return range;
	}

}
