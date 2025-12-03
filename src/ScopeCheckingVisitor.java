import ast.*;
import ast.visitor.BaseVisitor;

import java.util.*;

class ScopeContext {
    private final ClassNode currentClass;
    private final Map<Symbol, MethodNode> methodsMap;
    private final Map<Symbol, AttributeNode> attributesMap;

    public ScopeContext(ClassNode currentClass) {
        this.currentClass = currentClass;
        this.attributesMap = new HashMap<>();
        this.methodsMap = new HashMap<>();

        for (FeatureNode feature : currentClass.getFeatures()) {
            if (feature instanceof AttributeNode attribute) {
                attributesMap.put(attribute.getName(), attribute);
            } else if (feature instanceof MethodNode method) {
                methodsMap.put(method.getName(), method);
            }
        }
    }

    public ScopeContext(ClassNode currentClass, ScopeContext parent) {
        this.currentClass = currentClass;
        this.attributesMap = new HashMap<>();
        this.methodsMap = new HashMap<>();

        this.attributesMap.putAll(parent.attributesMap);
        this.methodsMap.putAll(parent.methodsMap);

        for (FeatureNode feature : currentClass.getFeatures()) {
            if (feature instanceof AttributeNode attribute) {
                attributesMap.put(attribute.getName(), attribute);
            } else if (feature instanceof MethodNode method) {
                methodsMap.put(method.getName(), method);
            }
        }
    }

    public ClassNode getCurrentClass() {
        return currentClass;
    }

    public AttributeNode getAttribute(Symbol name) {
        return attributesMap.get(name);
    }

    public MethodNode getMethod(Symbol name) {
        return methodsMap.get(name);
    }

    public void addAttribute(Symbol name, AttributeNode attr) {
        attributesMap.put(name, attr);
    }

    public void addMethod(Symbol name, MethodNode method) {
        methodsMap.put(name, method);
    }

    public Set<Symbol> getAttributeNames() {
        return Collections.unmodifiableSet(attributesMap.keySet());
    }

    public Set<Symbol> getMethodNames() {
        return Collections.unmodifiableSet(methodsMap.keySet());
    }

}

public class ScopeCheckingVisitor extends BaseVisitor<Void, ScopeContext> {

    @Override
    public Void visit(ProgramNode node, ScopeContext rootContext) {
        if (!Semant.classTable.isTypeDefined(TreeConstants.Main)) {
            Utilities.semantError().println("Class Main is not defined.");
        }

        ArrayList<ClassNode> objectClasses = Semant.classTable.getInheritanceMap().get(TreeConstants.Object_);

        for (int i = objectClasses.size() - 1; i >= 0; i--) {
            ClassNode classNode = objectClasses.get(i);
            ScopeContext context = new ScopeContext(classNode, rootContext);

            ArrayList<ClassNode> children = Semant.classTable.getInheritanceMap().get(classNode.getName());
            for (ClassNode child : children) {
                validateFeatureOverrides(child, context);
            }
        }

        // Traverse the full inheritance hierarchy,
        for (int i = objectClasses.size() - 1; i >= 0; i--) {
            visitInheritanceHierarchy(objectClasses.get(i));

        }

        return null;
    }

    private void validateFeatureOverrides(ClassNode classNode, ScopeContext parentContext) {
        ScopeContext context = new ScopeContext(classNode, parentContext);
        Map<Symbol, Map<Symbol, AttributeNode>> classAttributesMap = Semant.classTable.getClassAttributesMap();
        Map<Symbol, Map<Symbol, MethodNode>> classMethodsMap = Semant.classTable.getClassMethodsMap();

        for (FeatureNode feature : classNode.getFeatures()) {
            if (feature instanceof AttributeNode attribute) {
                if (context.getAttribute(attribute.getName()) != null) {
                    Utilities.semantError(classNode).println("Attribute " + attribute.getName()
                            + " is an attribute of an inherited class.");
                    continue;
                }
                context.addAttribute(attribute.getName(), attribute);
            }

            else if (feature instanceof MethodNode method) {
                if (context.getMethod(method.getName()) == null) {
                    context.addMethod(method.getName(), method);
                    continue;
                }

                MethodNode parentMethod = context.getMethod(method.getName());

                if (method.getFormals().size() != parentMethod.getFormals().size()) {
                    Utilities.semantError(classNode).println("Incompatible number of formal parameters in redefined method "
                            + method.getName() + ".");
                    continue;
                }

                if (parentMethod.getReturn_type() != method.getReturn_type()) {
                    Utilities.semantError(classNode).println("In redefined method " + method.getName()
                            + ", return type " + method.getReturn_type() + " is different from original return type "
                            + parentMethod.getReturn_type() + ".");
                    continue;
                }

                boolean error = false;
                for (int i = 0; i < method.getFormals().size(); i++) {
                    Symbol currentParamType = method.getFormals().get(i).getType_decl();
                    Symbol parentParamType = parentMethod.getFormals().get(i).getType_decl();
                    if (currentParamType != parentParamType) {
                        error = true;
                        Utilities.semantError(classNode).println("In redefined method " +
                                method.getName() + ", parameter type " + currentParamType
                                + " is different from original type " + parentParamType);
                    }
                }
                if (!error) {
                    context.addMethod(method.getName(), method);
                }
            }
        }

        Map<Symbol, AttributeNode> attributes = new HashMap<>();
        for (Symbol name : context.getAttributeNames()) {
            attributes.put(name, context.getAttribute(name));
        }
        classAttributesMap.put(classNode.getName(), attributes);

        Map<Symbol, MethodNode> methods = new HashMap<>();
        for (Symbol name : context.getMethodNames()) {
            methods.put(name, context.getMethod(name));
        }
        classMethodsMap.put(classNode.getName(), methods);

        ArrayList<ClassNode> children = Semant.classTable.getInheritanceMap().get(classNode.getName());
        for (ClassNode child : children) {
            validateFeatureOverrides(child, context);
        }
    }

    private void visitInheritanceHierarchy(ClassNode classNode) {
        ScopeContext context = new ScopeContext(classNode);

        Map<Symbol, AttributeNode> classAttributes = Semant.classTable.getClassAttributesMap().get(classNode.getName());
        if (classAttributes != null) {
            for (Symbol name : classAttributes.keySet()) {
                context.addAttribute(name, classAttributes.get(name));
            }
        }

        Map<Symbol, MethodNode> classMethods = Semant.classTable.getClassMethodsMap().get(classNode.getName());
        if (classMethods != null) {
            for (Symbol name : classMethods.keySet()) {
                context.addMethod(name, classMethods.get(name));
            }
        }

        visit(classNode, context); // Visit current class, utilising the visitor pattern, and DFS traversal

        ArrayList<ClassNode> children = Semant.classTable.getInheritanceMap().get(classNode.getName());
        for (ClassNode child : children) {
            visitInheritanceHierarchy(child);
        }
    }

    @Override
    public Void visit(ClassNode node, ScopeContext context) {

        if (node.getName() == TreeConstants.Main
                && context.getMethod(TreeConstants.main_meth) == null) {
            Utilities.semantError(context.getCurrentClass())
                    .println("No 'main' method in class Main.");
        }


        Semant.symTable.enterScope();

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

        if (node.getName() == TreeConstants.self) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be the name of an attribute.");
        }

        visit(node.getInit(), context);
        return null;
    }

    @Override
    public Void visit(FormalNode node, ScopeContext context) {

        if (node.getName() == TreeConstants.self) {
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

        if (node.getIdentifier() == TreeConstants.self) {
            Utilities.semantError(context.getCurrentClass())
                    .println("'self' cannot be bound in a 'let' expression.");
        }

        Semant.symTable.enterScope();
        Semant.symTable.addId(node.getIdentifier(), node.getType_decl());

        visit(node.getInit(), context);
        visit(node.getBody(), context);

        Semant.symTable.exitScope();

        return null;
    }


    @Override
    public Void visit(ObjectNode node, ScopeContext context) {

        if (node.getName() == TreeConstants.self) {
            return null;
        }

        if (Semant.symTable.lookup(node.getName()) == null && context.getAttribute(node.getName()) == null) {
            Utilities.semantError(context.getCurrentClass().getFilename(), node)
                    .println("Undeclared identifier " + node.getName() + ".");
        }
        return null;
    }

    @Override
    public Void visit(CaseNode node, ScopeContext context) {

        visit(node.getExpr(), context);

        for (BranchNode branch : node.getCases()) {
            if (branch.getName() == TreeConstants.self) {
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

        if (node.getName() == TreeConstants.self) {
            Utilities.semantError(context.getCurrentClass())
                    .println("Cannot assign to 'self'.");
        }

        visit(node.getExpr(), context);
        return null;
    }

}
