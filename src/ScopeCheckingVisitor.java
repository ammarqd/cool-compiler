import ast.*;
import ast.visitor.BaseVisitor;
import com.sun.jdi.Method;
import jdk.jshell.execution.Util;

import java.util.*;

class ScopeContext {
    private final ClassNode currentClass;
    private final Map<Symbol, MethodNode> methodMap;
    private final Map<Symbol, ArrayList<ClassNode>> classMap;
    private final ClassTable classTable;

    public ScopeContext(ClassNode currentClass, ClassTable classTable) {
        this.currentClass = currentClass;
        this.classTable = classTable;
        this.methodMap = new HashMap<>();
        this.classMap = classTable.getClassMap();
    }

    public ClassNode getCurrentClass() {
        return currentClass;
    }

    public ClassTable getClassTable() {
        return classTable;
    }

    public MethodNode getMethod(Symbol name) {
        return methodMap.get(name);
    }

}

public class ScopeCheckingVisitor extends BaseVisitor<Void, ScopeContext> {

    public ScopeCheckingVisitor() {
        ArrayList<ClassNode> rootClasses = Semant.classTable.getClassMap().get(TreeConstants.Object_);
        for (int i = rootClasses.size() - 1; i >= 3; i--) { // Skip classes that disallow inheritance: String, Int, Bool
            HashMap<Symbol, MethodNode> methodMap = new HashMap<>();
            for (FeatureNode feature : rootClasses.get(i).getFeatures()) {
                if (feature instanceof MethodNode method) {
                    if (!methodMap.containsKey(method.getName())) {
                        methodMap.put(method.getName(), method);
                    } else {
                        Utilities.semantError(rootClasses.get(i)).println("Method " + method.getName()
                        + "is multiply defined.");
                    }
                }
            }
            registerInheritedMethods(rootClasses.get(i), methodMap);
        }
    }

    private void registerInheritedMethods(ClassNode classNode, HashMap<Symbol, MethodNode> methodMap) {
        for (ClassNode className : Semant.classTable.getClassMap().get(classNode.getName())) {
            Set<Symbol> seenMethods = new HashSet<>();
            for (FeatureNode feature : className.getFeatures()) {
                if (feature instanceof MethodNode method) {
                    if (!methodMap.containsKey(method.getName())) {
                        methodMap.put(method.getName(), method);
                    } else if (seenMethods.contains(method.getName())) {
                        Utilities.semantError(className).println("Method " + method.getName()
                                + " is multiply defined.");
                    } else if (methodMap.get(method.getName()).getReturn_type() != method.getReturn_type()) {
                            Utilities.semantError(className).println("In redefined method " + method.getName()
                                    + ", return type " + method.getReturn_type() + " is different from original return type "
                                    + methodMap.get(method.getName()).getReturn_type() + ".");
                    }
                    seenMethods.add(method.getName());
                }
            }
            registerInheritedMethods(className, methodMap);
        }
    }

    @Override
    public Void visit(ProgramNode node, ScopeContext context) {
        if (!Semant.classTable.isValidType(TreeConstants.Main)) {
            Utilities.semantError().println("Class Main is not defined");
        }

        for (ClassNode classNode : node.getClasses()) {
            visit(classNode, new ScopeContext(classNode, Semant.classTable));
        }

        return null;
    }

    @Override
    public Void visit(ClassNode node, ScopeContext context) {

        if (node.getName().equals(TreeConstants.Main) &&
                context.getMethod(TreeConstants.main_meth) == null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("No 'main' method in class Main.");
        }

        Semant.symTable.enterScope();

        // Second pass: Check implementations
        for (FeatureNode feature : node.getFeatures()) {
            feature.accept(this, context);
        }

        Semant.symTable.exitScope();

        return null;
    }

    @Override
    public Void visit(MethodNode node, ScopeContext context) {

        Semant.symTable.enterScope();

        for (FormalNode formal : node.getFormals()) {
            visit(formal, context);
        }

        visit(node.getExpr(), context);

        Semant.symTable.exitScope();

        return null;
    }

    @Override
    public Void visit(AttributeNode node, ScopeContext context) {

        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be the name of an attribute.");
        }

        Semant.symTable.addId(node.getName(), node.getType_decl());

        visit(node.getInit(), context);

        return null;
    }

    @Override
    public Void visit(FormalNode node, ScopeContext context) {

        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be the name of a formal parameter.");
        }

        if (Semant.symTable.probe(node.getName()) != null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Formal parameter " + node.getName() + " is multiply defined.");
        } else {
            Semant.symTable.addId(node.getName(), node.getType_decl());
        }
        return null;
    }

    @Override
    public Void visit(LetNode node, ScopeContext context) {

        if (node.getIdentifier().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be bound in a 'let' expression.");
        }

        if (Semant.symTable.probe(node.getIdentifier()) != null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Let variable " + node.getIdentifier() + " is multiply defined.");
        } else {
            Semant.symTable.enterScope();
            Semant.symTable.addId(node.getIdentifier(), node.getType_decl());

            visit(node.getInit(), context);
            visit(node.getBody(), context);

            Semant.symTable.exitScope();
        }
        return null;
    }


    @Override
    public Void visit(ObjectNode node, ScopeContext context) {

        if (Semant.symTable.lookup(node.getName()) == null &&
                Semant.symTable.lookup(node.getName()) == null) {
            Utilities.semantError(context.getCurrentClass().getFilename(), node)
                    .println("Undeclared identifier " + node.getName() + ".");
        }
        return null;
    }

    @Override
    public Void visit(DispatchNode node, ScopeContext context) {

        visit(node.getExpr(), context);

        for (ExpressionNode actual : node.getActuals()) {
            visit(actual, context);
        }

        if (context.getMethod(node.getName()) == null
                && !Semant.classTable.isBuiltInMethod(node.getName())) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Dispatch to undefined method " + node.getName() + ".");
        }

        return null;
    }

    @Override
    public Void visit(StaticDispatchNode node, ScopeContext context) {

        visit(node.getExpr(), context);

        for (ExpressionNode actual : node.getActuals()) {
            visit(actual, context);
        }

        if (context.getMethod(node.getName()) == null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Static dispatch to undefined method " + node.getName() + ".");
        }

        return null;
    }

    @Override
    public Void visit(BlockNode node, ScopeContext context) {

        for (ExpressionNode expr : node.getExprs()) {
            visit(expr, context);
        }
        return null;
    }

    @Override
    public Void visit(CaseNode node, ScopeContext context) {

        visit(node.getExpr(), context);

        for (BranchNode branch : node.getCases()) {
            if (branch.getName().equals(TreeConstants.self)) {
                Utilities.semantError(context.getCurrentClass())
                        .println("'self' cannot be bound in a 'case' branch.");
            }

            Semant.symTable.enterScope();
            Semant.symTable.addId(branch.getName(), branch.getType_decl());
            visit(branch.getExpr(), context);
            Semant.symTable.exitScope();
        }
        return null;
    }

    @Override
    public Void visit(AssignNode node, ScopeContext context) {

        if (node.getName().equals(TreeConstants.self)) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Cannot assign to 'self'.");
        }

        visit(node.getExpr(), context);
        return null;
    }

}
