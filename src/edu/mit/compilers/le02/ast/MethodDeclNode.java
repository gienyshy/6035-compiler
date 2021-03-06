package edu.mit.compilers.le02.ast;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.le02.DecafType;
import edu.mit.compilers.le02.SourceLocation;

public final class MethodDeclNode extends DeclNode {
  private List<VarDeclNode> params;
  private BlockNode body;
  
  public MethodDeclNode(SourceLocation sl, DecafType type,
                        String id, List<VarDeclNode> params, BlockNode body) {
    super(sl, type, id);
    this.params = params;
    this.body = body;
  }

  @Override
  public List<ASTNode> getChildren() {
    List<ASTNode> children = new ArrayList<ASTNode>(params);
    children.add(body);
    return children;
  }

  public List<VarDeclNode> getParams() {
    return params;
  }

  public BlockNode getBody() {
    return body;
  }

  @Override
  public <T> T accept(ASTNodeVisitor<T> v) { 
    return v.visit(this); 
  }
}
