package edu.mit.compilers.le02.stgenerator;

import edu.mit.compilers.le02.ast.ASTNode;
import edu.mit.compilers.le02.ast.ASTNodeVisitor;
import edu.mit.compilers.le02.ast.ArrayDeclNode;
import edu.mit.compilers.le02.ast.BlockNode;
import edu.mit.compilers.le02.ast.ClassNode;
import edu.mit.compilers.le02.ast.FieldDeclNode;
import edu.mit.compilers.le02.ast.MethodDeclNode;
import edu.mit.compilers.le02.ast.StatementNode;
import edu.mit.compilers.le02.ast.VarDeclNode;
import edu.mit.compilers.le02.symboltable.ClassDescriptor;
import edu.mit.compilers.le02.symboltable.Descriptor;
import edu.mit.compilers.le02.symboltable.FieldDescriptor;
import edu.mit.compilers.le02.symboltable.LocalDescriptor;
import edu.mit.compilers.le02.symboltable.MethodDescriptor;
import edu.mit.compilers.le02.symboltable.ParamDescriptor;
import edu.mit.compilers.le02.symboltable.SymbolTable;

public class SymbolTableGenerator extends ASTNodeVisitor<Descriptor> {
  private SymbolTable currParent = null;
  private boolean isField = false;
  private boolean isParam = false;

  /** Holds the SymbolTableVisitor singleton. */
  private static SymbolTableGenerator instance;

  /**
   * Retrieves the SymbolTableVisitor singleton, creating if necessary.
   */
  public static SymbolTableGenerator getInstance() {
    if (instance == null) {
      instance = new SymbolTableGenerator();
    }
    return instance;
  }

  /**
   * Generates an symbol table based on an input IR.
   */
  public static SymbolTable generateSymbolTable(ASTNode root)
      throws SymbolTableException {
    assert(root instanceof ClassNode);
    return getInstance().createClassST((ClassNode)root);
  }

  /**
   * Converts a ClassNode into a SymbolTable.  The root of all ASTNode trees 
   * must be a ClassNode, otherwise we should throw an exception
   * 
   * @param root The root of our AST tree
   * @return SymbolTable The expanded SymbolTable
   */
  public SymbolTable createClassST(ClassNode root) throws SymbolTableException {
    SymbolTable st = new SymbolTable(null);
    currParent = st;
    ClassDescriptor desc = (ClassDescriptor) root.accept(this);
    st.put(root.getName(), desc);
    
    // Set the descriptors for the AST
    ASTDescriptorVisitor v = new ASTDescriptorVisitor();
    v.setASTDescriptors(root, desc);
    return st;
  }
  
  @Override
  public Descriptor visit(ClassNode node) {
    SymbolTable parent = currParent;

    // Create and fill fieldSymbolTable
    SymbolTable fieldSymbolTable = new SymbolTable(parent);
    currParent = fieldSymbolTable;
    isField = true;
    for (FieldDeclNode n : node.getFields()) {
      fieldSymbolTable.put(n.getName(), n.accept(this));
    }
    isField = false;

    // Create and fill methodSymbolTable
    SymbolTable methodSymbolTable = new SymbolTable(fieldSymbolTable);
    currParent = methodSymbolTable;
    for (MethodDeclNode m : node.getMethods()) {
      methodSymbolTable.put(m.getName(), m.accept(this));
    }
    
    currParent = parent;
    return new ClassDescriptor(parent, node.getName(), fieldSymbolTable, 
                               methodSymbolTable);
  }

  @Override
  public Descriptor visit(MethodDeclNode node) {
    SymbolTable parent = currParent;

    // Create and fill paramSymbolTable
    SymbolTable paramSymbolTable = new SymbolTable(parent);
    currParent = paramSymbolTable;
    isParam = true;
    for (VarDeclNode v : node.getParams()) {
      paramSymbolTable.put(v.getName(), v.accept(this));
    }
    isParam = false;
    
    // Create the local table for this block (and any nested blocks)
    node.getBody().accept(this);
    
    currParent = parent;
    return new MethodDescriptor(parent, node.getName(), node.getType(),
                                paramSymbolTable, 
                                node.getBody());
  }
  
  @Override
  public Descriptor visit(BlockNode node) {
    SymbolTable parent = new SymbolTable(currParent);
    
    // Create and fill localSymbolTable
    SymbolTable localSymbolTable = new SymbolTable(parent);
    currParent = localSymbolTable;
    for (VarDeclNode v : node.getDecls()) {
      localSymbolTable.put(v.getName(), v.accept(this));
    }

    // Create the local symbol table for any nested blocks
    for (StatementNode s : node.getStatements()) {
      s.accept(this);
    }
    
    currParent = parent;
    node.setLocalSymbolTable(localSymbolTable);
    return null;
  }
  
  @Override
  public Descriptor visit(ArrayDeclNode node) {
    return new FieldDescriptor(currParent, node.getName(), node.getType());
  }

  @Override
  public Descriptor visit(VarDeclNode node) {
    if (isField) {
      return new FieldDescriptor(currParent, node.getName(), node.getType());
    }
    else if (isParam) {
      return new ParamDescriptor(currParent, node.getName(), node.getType());
    }
    else {
      return new LocalDescriptor(currParent, node.getName(), node.getType());
    }
  }
}
