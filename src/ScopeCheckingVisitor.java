import ast.*;
import ast.visitor.BaseVisitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class ScopeContext {
    private final ClassNode currentClass;
    private final Map<Symbol, MethodNode> methodsMap;
    private final Map<Symbol, AttributeNode> attributesMap;

    public ScopeContext(ClassNode currentClass) {
        this.currentClass = currentClass;
        this.attributesMap = new HashMap<>();
        this.methodsMap = new HashMap<>();
    }

    public ClassNode getCurrentClass() {
        return currentClass;
    }

    public MethodNode getMethod(Symbol name) {
        return methodsMap.get(name);
    }

    public AttributeNode getAttribute(Symbol name) {
        return attributesMap.get(name);
    }

    public void addMethod(Symbol name, MethodNode method) {
        methodsMap.put(name, method);
    }

    public void addAttribute(Symbol name, AttributeNode attr) {
        attributesMap.put(name, attr);
    }

    public Map<Symbol, AttributeNode> getAttributesMap() {
        return attributesMap;
    }

    public Map<Symbol, MethodNode> getMethodsMap() {
        return methodsMap;
    }
}

public class ScopeCheckingVisitor extends BaseVisitor<Void, ScopeContext> {

    @Override
    public Void visit(ProgramNode node, ScopeContext context) {
        if (!Semant.classTable.isTypeDefined(TreeConstants.Main)) {
            Utilities.semantError().println("Class Main is not defined.");
        }

        ClassNode objectNode = Semant.classTable.getClassMap().get(TreeConstants.Object_);
        ArrayList<ClassNode> objectClasses = Semant.classTable.getInheritanceMap().get(TreeConstants.Object_);

        // Initial traversal to validate method overrides
        for (int i = objectClasses.size() - 1; i >= 0; i--) {
            ClassNode classNode = objectClasses.get(i);
            ScopeContext rootContext = new ScopeContext(classNode);

            Map<Symbol, Map<Symbol, AttributeNode>> classAttributesMap = Semant.classTable.getClassAttributesMap();
            Map<Symbol, Map<Symbol, MethodNode>> classMethodsMap = Semant.classTable.getClassMethodsMap();

            Map<Symbol, AttributeNode> attributesMap = new HashMap<>();
            Map<Symbol, MethodNode> methodsMap = new HashMap<>();

            // Add the default Object methods to methodMap
            for (FeatureNode feature : objectNode.getFeatures()) {
                rootContext.addMethod(((MethodNode)feature).getName(), (MethodNode)feature);
                methodsMap.put(((MethodNode)feature).getName(), (MethodNode)feature);
            }

            Symbol className = classNode.getName();

            for (FeatureNode feature : classNode.getFeatures()) {
                if (feature instanceof AttributeNode attribute) {
                    rootContext.addAttribute(attribute.getName(), attribute);
                    attributesMap.put(attribute.getName(), attribute);
                } else if (feature instanceof MethodNode method) {
                    rootContext.addMethod(method.getName(), method);
                    methodsMap.put(method.getName(), method);
                }
            }

            classAttributesMap.put(className, attributesMap);
            classMethodsMap.put(className, methodsMap);

            ArrayList<ClassNode> children = Semant.classTable.getInheritanceMap().get(classNode.getName());
            for (ClassNode child : children) {
                validateFeatureOverrides(child, rootContext);
            }
        }

        // Traverse the full inheritance hierarchy,
        for (int i = objectClasses.size() - 1; i >= 0; i--) {
            visitInheritanceHierarchy(objectClasses.get(i));
        }

        return null;
    }

    private void validateFeatureOverrides(ClassNode classNode, ScopeContext parentContext) {
        ScopeContext context = new ScopeContext(classNode);
        Map<Symbol, Map<Symbol, AttributeNode>> classAttributesMap = Semant.classTable.getClassAttributesMap();
        Map<Symbol, Map<Symbol, MethodNode>> classMethodsMap = Semant.classTable.getClassMethodsMap();

        context.getAttributesMap().putAll(parentContext.getAttributesMap());
        context.getMethodsMap().putAll(parentContext.getMethodsMap());

        Map<Symbol, AttributeNode> attributesMap = new HashMap<>(context.getAttributesMap());
        Map<Symbol, MethodNode> methodsMap = new HashMap<>(context.getMethodsMap());

        Symbol className = context.getCurrentClass().getName();

        for (FeatureNode feature : classNode.getFeatures()) {
            if (feature instanceof AttributeNode attribute) {
                if (!context.getAttributesMap().containsKey(attribute.getName())) {
                    context.addAttribute(attribute.getName(), attribute);
                    attributesMap.put(attribute.getName(), attribute);
                } else {
                    Utilities.semantError(classNode).println("Attribute " + attribute.getName()
                            + " is an attribute of an inherited class.");
                }
            } else if (feature instanceof MethodNode method) {
                if (!context.getMethodsMap().containsKey(method.getName())) {
                    context.addMethod(method.getName(), method);
                    methodsMap.put(method.getName(), method);
                } else {
                    MethodNode parentMethod = context.getMethod(method.getName());
                    if (parentMethod.getReturn_type() != method.getReturn_type()) {
                        Utilities.semantError(classNode).println("In redefined method " + method.getName()
                                + ", return type " + method.getReturn_type() + " is different from original return type "
                                + parentMethod.getReturn_type() + ".");
                    } else if (method.getFormals().size() != parentMethod.getFormals().size()) {
                        Utilities.semantError(classNode).println("Incompatible number of formal parameters in redefined method "
                                + method.getName() + ".");
                    } else {
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
                            methodsMap.put(method.getName(), method);
                        }
                    }
                }
            }
        }

        classAttributesMap.put(className, attributesMap);
        classMethodsMap.put(className, methodsMap);

        ArrayList<ClassNode> children = Semant.classTable.getInheritanceMap().get(classNode.getName());
        for (ClassNode child : children) {
            validateFeatureOverrides(child, context);
        }
    }

    private void visitInheritanceHierarchy(ClassNode classNode) {
        ScopeContext context = new ScopeContext(classNode);

        Map<Symbol, AttributeNode> classAttributes = Semant.classTable.getClassAttributesMap().get(classNode.getName());
        if (classAttributes != null) {
            context.getAttributesMap().putAll(classAttributes);
        }

        Map<Symbol, MethodNode> classMethods = Semant.classTable.getClassMethodsMap().get(classNode.getName());
        if (classMethods != null) {
            context.getMethodsMap().putAll(classMethods);
        }

        visit(classNode, context); // Visit current class, utilising the visitor pattern, and DFS traversal

        ArrayList<ClassNode> children = Semant.classTable.getInheritanceMap().get(classNode.getName());
        for (ClassNode child : children) {
            visitInheritanceHierarchy(child);
        }
    }

    @Override
    public Void visit(ClassNode node, ScopeContext context) {

        if (node.getName() == TreeConstants.Main &&
                Semant.classTable.getClassMethodsMap().get(TreeConstants.Main).get(TreeConstants.main_meth) == null) {
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
