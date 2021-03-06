package edu.mit.compilers.le02.ast;

import java.util.Arrays;
import java.util.List;

import edu.mit.compilers.le02.SourceLocation;

public abstract class ASTNode {
  protected SourceLocation sourceLoc;

  public ASTNode(SourceLocation sl) {
    this.sourceLoc = sl;
  }

  public SourceLocation getSourceLoc() {
    return sourceLoc;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() 
           + Arrays.toString(getChildren().toArray());
  }

  abstract public List<ASTNode> getChildren();
  abstract public <T> T accept(ASTNodeVisitor<T> v);
}
