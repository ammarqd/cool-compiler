import ast.*;

import java.util.*;


/**
 * This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.
 */
class ClassTable {

    private final Map<Symbol, ClassNode> classMap = new HashMap<>();
    private final Map<Symbol, ArrayList<ClassNode>> inheritanceMap = new HashMap<>();
    private final Map<Symbol, Map<Symbol, AttributeNode>> classAttributesMap = new HashMap<>();
    private final Map<Symbol, Map<Symbol, MethodNode>> classMethodsMap = new HashMap<>();

    /**
     * Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String).  Please note: as is this method does not
     * do anything useful; you will need to edit it to make if do what
     * you want.
     */
    private void installBasicClasses() {
        Symbol filename
                = StringTable.stringtable.addString("<basic class>");

        LinkedList<FormalNode> formals;

        // The following demonstrates how to create dummy parse trees to
        // refer to basic Cool classes.  There's no need for method
        // bodies -- these are already built into the runtime system.

        // IMPORTANT: The results of the following expressions are
        // stored in local variables.  You will want to do something
        // with those variables at the end of this method to make this
        // code meaningful.

        // The Object class has no parent class. Its methods are
        //        cool_abort() : Object    aborts the program
        //        type_name() : Str        returns a string representation
        //                                 of class name
        //        copy() : SELF_TYPE       returns a copy of the object

        ClassNode Object_class =
                new ClassNode(0,
                        TreeConstants.Object_,
                        TreeConstants.No_class,
                        filename);

        Object_class.add(new MethodNode(0,
                TreeConstants.cool_abort,
                new LinkedList<FormalNode>(),
                TreeConstants.Object_,
                new NoExpressionNode(0)));

        Object_class.add(new MethodNode(0,
                TreeConstants.type_name,
                new LinkedList<FormalNode>(),
                TreeConstants.Str,
                new NoExpressionNode(0)));

        Object_class.add(new MethodNode(0,
                TreeConstants.copy,
                new LinkedList<FormalNode>(),
                TreeConstants.SELF_TYPE,
                new NoExpressionNode(0)));

        // The IO class inherits from Object. Its methods are
        //        out_string(Str) : SELF_TYPE  writes a string to the output
        //        out_int(Int) : SELF_TYPE      "    an int    "  "     "
        //        in_string() : Str            reads a string from the input
        //        in_int() : Int                "   an int     "  "     "

        ClassNode IO_class =
                new ClassNode(0,
                        TreeConstants.IO,
                        TreeConstants.Object_,
                        filename);

        formals = new LinkedList<FormalNode>();
        formals.add(
                new FormalNode(0,
                        TreeConstants.arg,
                        TreeConstants.Str));

        IO_class.add(new MethodNode(0,
                TreeConstants.out_string,
                formals,
                TreeConstants.SELF_TYPE,
                new NoExpressionNode(0)));


        formals = new LinkedList<FormalNode>();
        formals.add(
                new FormalNode(0,
                        TreeConstants.arg,
                        TreeConstants.Int));
        IO_class.add(new MethodNode(0,
                TreeConstants.out_int,
                formals,
                TreeConstants.SELF_TYPE,
                new NoExpressionNode(0)));

        IO_class.add(new MethodNode(0,
                TreeConstants.in_string,
                new LinkedList<FormalNode>(),
                TreeConstants.Str,
                new NoExpressionNode(0)));

        IO_class.add(new MethodNode(0,
                TreeConstants.in_int,
                new LinkedList<FormalNode>(),
                TreeConstants.Int,
                new NoExpressionNode(0)));

        // The Int class has no methods and only a single attribute, the
        // "val" for the integer.

        ClassNode Int_class =
                new ClassNode(0,
                        TreeConstants.Int,
                        TreeConstants.Object_,
                        filename);

        Int_class.add(new AttributeNode(0,
                TreeConstants.val,
                TreeConstants.prim_slot,
                new NoExpressionNode(0)));

        // Bool also has only the "val" slot.
        ClassNode Bool_class =
                new ClassNode(0,
                        TreeConstants.Bool,
                        TreeConstants.Object_,
                        filename);

        Bool_class.add(new AttributeNode(0,
                TreeConstants.val,
                TreeConstants.prim_slot,
                new NoExpressionNode(0)));

        // The class Str has a number of slots and operations:
        //       val                              the length of the string
        //       str_field                        the string itself
        //       length() : Int                   returns length of the string
        //       concat(arg: Str) : Str           performs string concatenation
        //       substr(arg: Int, arg2: Int): Str substring selection

        ClassNode Str_class =
                new ClassNode(0,
                        TreeConstants.Str,
                        TreeConstants.Object_,
                        filename);
        Str_class.add(new AttributeNode(0,
                TreeConstants.val,
                TreeConstants.Int,
                new NoExpressionNode(0)));

        Str_class.add(new AttributeNode(0,
                TreeConstants.str_field,
                TreeConstants.prim_slot,
                new NoExpressionNode(0)));
        Str_class.add(new MethodNode(0,
                TreeConstants.length,
                new LinkedList<FormalNode>(),
                TreeConstants.Int,
                new NoExpressionNode(0)));

        formals = new LinkedList<FormalNode>();
        formals.add(new FormalNode(0,
                TreeConstants.arg,
                TreeConstants.Str));
        Str_class.add(new MethodNode(0,
                TreeConstants.concat,
                formals,
                TreeConstants.Str,
                new NoExpressionNode(0)));

        formals = new LinkedList<FormalNode>();
        formals.add(new FormalNode(0,
                TreeConstants.arg,
                TreeConstants.Int));
        formals.add(new FormalNode(0,
                TreeConstants.arg2,
                TreeConstants.Int));

        Str_class.add(new MethodNode(0,
                TreeConstants.substr,
                formals,
                TreeConstants.Str,
                new NoExpressionNode(0)));

	/* Do something with Object_class, IO_class, Int_class,
           Bool_class, and Str_class here */

        // Only add these two classes to the inheritance map, since they allow inheritance
        inheritanceMap.put(TreeConstants.Object_, new ArrayList<>());
        inheritanceMap.get(TreeConstants.Object_).add(IO_class);
        inheritanceMap.put(TreeConstants.IO, new ArrayList<>());

        // For access to built-in class nodes, also child -> parent access through getParent()
        classMap.put(TreeConstants.Object_, Object_class);
        classMap.put(TreeConstants.IO, IO_class);
        classMap.put(TreeConstants.Str, Str_class);
        classMap.put(TreeConstants.Int, Int_class);
        classMap.put(TreeConstants.Bool, Bool_class);

        // Add built-in class's methods and attributes for fast access during type-checking.
        classMethodsMap.put(Object_class.getName(), new HashMap<>());
        for (FeatureNode feature : Object_class.getFeatures()) {
            classMethodsMap.get(Object_class.getName()).put(((MethodNode) feature).getName(), (MethodNode) feature);
        }

        // Add Object class's methods to all built in class's method maps
        Map<Symbol, MethodNode> objectMethods = classMethodsMap.get(TreeConstants.Object_);

        classMethodsMap.put(IO_class.getName(), new HashMap<>());
        classMethodsMap.get(IO_class.getName()).putAll(objectMethods);

        for (FeatureNode feature : IO_class.getFeatures()) {
            classMethodsMap.get(IO_class.getName()).put(((MethodNode) feature).getName(), (MethodNode) feature);
        }

        classAttributesMap.put(Int_class.getName(), new HashMap<>());
        classMethodsMap.put(Int_class.getName(), new HashMap<>());
        classMethodsMap.get(Int_class.getName()).putAll(objectMethods);

        for (FeatureNode feature : Int_class.getFeatures()) {
            classAttributesMap.get(Int_class.getName()).put(((AttributeNode) feature).getName(), (AttributeNode) feature);
        }

        classAttributesMap.put(Bool_class.getName(), new HashMap<>());
        classMethodsMap.put(Bool_class.getName(), new HashMap<>());
        classMethodsMap.get(Bool_class.getName()).putAll(objectMethods);

        for (FeatureNode feature : Bool_class.getFeatures()) {
            classAttributesMap.get(Bool_class.getName()).put(((AttributeNode) feature).getName(), (AttributeNode) feature);
        }

        classMethodsMap.put(Str_class.getName(), new HashMap<>());
        classMethodsMap.get(Str_class.getName()).putAll(objectMethods);
        classAttributesMap.put(Str_class.getName(), new HashMap<>());

        for (FeatureNode feature : Str_class.getFeatures()) {
            if (feature instanceof MethodNode) {
                classMethodsMap.get(Str_class.getName()).put(((MethodNode) feature).getName(), (MethodNode) feature);
            } else if (feature instanceof AttributeNode) {
                classAttributesMap.get(Str_class.getName()).put(((AttributeNode) feature).getName(), (AttributeNode) feature);
            }
        }

    }

    public ClassTable(List<ClassNode> cls) {
        installBasicClasses();
        checkClassRedefinitions(cls);
        checkParentValidity(cls);
        if (Utilities.errors()) {
            Utilities.fatalError(Utilities.ErrorCode.ERROR_SEMANT);
        }
        checkInheritanceIsAcyclic(cls);
        if (Utilities.errors()) {
            Utilities.fatalError(Utilities.ErrorCode.ERROR_SEMANT);
        }
    }

    private void checkClassRedefinitions(List<ClassNode> cls) {
        for (ClassNode c : cls) {
            Symbol className = c.getName();

            if (className == TreeConstants.Object_
                    || className == TreeConstants.IO
                    || className == TreeConstants.Str
                    || className == TreeConstants.Int
                    || className == TreeConstants.Bool
                    || className == TreeConstants.SELF_TYPE) {
                Utilities.semantError(c).println("Redefinition of basic class " + className + ".");
                continue;
            }

            if (classMap.containsKey(className)) {
                Utilities.semantError(c).println("Class " + className + " was previously defined.");
                continue;
            }

            classMap.put(className, c);
            inheritanceMap.put(className, new ArrayList<>());
        }
    }

    private void checkParentValidity(List<ClassNode> cls) {
        for (int i = cls.size() - 1; i >= 0; i--) {
            ClassNode c = cls.get(i);
            Symbol className = c.getName();
            Symbol parent = c.getParent();

            // Ignore classes that aren't in classMap, so we don't double report errors
            if (c != classMap.get(className)) {
                continue;
            }

            if (parent == TreeConstants.Str
                    || parent == TreeConstants.Int
                    || parent == TreeConstants.Bool
                    || parent == TreeConstants.SELF_TYPE) {
                Utilities.semantError(c).println("Class " + className + " cannot inherit class " + parent + ".");
                continue;
            }

            if (!classMap.containsKey(parent)) {
                Utilities.semantError(c).println("Class " + className + " inherits from an undefined class " + parent + ".");
                continue;
            }

            inheritanceMap.get(parent).add(c);
        }
    }

    private void checkInheritanceIsAcyclic(List<ClassNode> cls) {
        final int WHITE = 0;  // Unvisited node
        final int GREY = 1;   // Node currently being visited (part of DFS path)
        final int BLACK = 2;  // Fully visited node (no cycles found in its path)

        Map<Symbol, Integer> colorMap = new HashMap<>();
        Set<Symbol> cycleClasses = new HashSet<>();

        for (ClassNode c : cls) {
            colorMap.put(c.getName(), WHITE);
        }

        for (ClassNode c : cls) {
            if (colorMap.get(c.getName()) == WHITE) {
                detectCycles(c.getName(), colorMap, cycleClasses, GREY, BLACK);
            }
        }

        for (int i = cls.size() - 1; i >= 0; i--) {
            ClassNode c = cls.get(i);
            if (cycleClasses.contains(c.getName())) {
                Utilities.semantError(c).println("Class " + c.getName() + ", or an ancestor of " + c.getName() + ", is involved in an inheritance cycle.");
            }
        }
    }

    private void detectCycles(Symbol className, Map<Symbol, Integer> colorMap, Set<Symbol> cycleClasses, int GREY, int BLACK) {
        if (colorMap.get(className) == GREY) {
            markDescendants(className, cycleClasses);
            return;
        }

        if (colorMap.get(className) == BLACK) {
            return;
        }

        colorMap.put(className, GREY);

        // DFS Traversal
        for (ClassNode child : inheritanceMap.get(className)) {
            detectCycles(child.getName(), colorMap, cycleClasses, GREY, BLACK);
        }

        colorMap.put(className, BLACK);
    }

    private void markDescendants(Symbol currentClass, Set<Symbol> cycleClasses) {
        for (ClassNode child : inheritanceMap.get(currentClass)) {
            if (cycleClasses.add(child.getName())) {
                markDescendants(child.getName(), cycleClasses);
            }
        }
    }

    public Map<Symbol, ClassNode> getClassMap() {
        return classMap;
    }

    public Map<Symbol, ArrayList<ClassNode>> getInheritanceMap() {
        return inheritanceMap;
    }

    public Map<Symbol, Map<Symbol, AttributeNode>> getClassAttributesMap() {
        return classAttributesMap;
    }

    public Map<Symbol, Map<Symbol, MethodNode>> getClassMethodsMap() {
        return classMethodsMap;
    }

    public boolean isTypeDefined(Symbol type) {
        if (type == TreeConstants.SELF_TYPE) return true;
        return classMap.containsKey(type);
    }

    public boolean isSubType(Symbol sub, Symbol supertype, Symbol className) {
        if (sub == TreeConstants.No_type) return true;

        if (sub == TreeConstants.SELF_TYPE) {
            if (supertype == TreeConstants.SELF_TYPE) {
                return true;
            }
            sub = className;
        }
        ClassNode currentClass = classMap.get(sub);

        while (currentClass.getName() != TreeConstants.Object_ && currentClass.getName() != supertype) {
            currentClass = classMap.get(currentClass.getParent());
        }

        return currentClass.getName() == supertype;
    }

    public Symbol getLeastUpperBound(Symbol type1, Symbol type2, Symbol className) {
        if (type1 == TreeConstants.SELF_TYPE) {
            type1 = className;
        }
        if (type2 == TreeConstants.SELF_TYPE) {
            type2 = className;
        }
        if (type1 == type2) return type1;

        Set<Symbol> visited = new HashSet<>();

        ClassNode first = classMap.get(type1);
        while (first != null) {
            visited.add(first.getName());
            first = classMap.get(first.getParent());
        }

        ClassNode second = classMap.get(type2);
        while (!visited.contains(second.getName())) {
            second = classMap.get(second.getParent());
        }

        return second.getName();
    }

}


