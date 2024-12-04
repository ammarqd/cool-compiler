import ast.*;

import java.util.*;


/**
 * This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.
 */
class ClassTable {

    private final Map<Symbol, ArrayList<ClassNode>> classMap = new HashMap<>();

    private static final Set<Symbol> NON_REDEFINABLE_CLASSES = Set.of(
            TreeConstants.IO,
            TreeConstants.Str,
            TreeConstants.Int,
            TreeConstants.Bool
    );

    private static final Set<Symbol> NON_INHERITABLE_CLASSES = Set.of(
            TreeConstants.Str,
            TreeConstants.Int,
            TreeConstants.Bool
    );

    private static final Set<Symbol> builtInMethods = Set.of(
            TreeConstants.cool_abort,
            TreeConstants.type_name,
            TreeConstants.copy,
            TreeConstants.length,
            TreeConstants.concat,
            TreeConstants.substr,
            TreeConstants.out_string,
            TreeConstants.out_int,
            TreeConstants.in_string,
            TreeConstants.in_int
    );

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

        classMap.put(TreeConstants.Object_, new ArrayList<>(Arrays.asList(
                IO_class,
                Str_class,
                Int_class,
                Bool_class
        )));

        classMap.put(TreeConstants.IO, new ArrayList<>());
        classMap.put(TreeConstants.Str, null);
        classMap.put(TreeConstants.Int, null);
        classMap.put(TreeConstants.Bool, null);
    }

    public ClassTable(List<ClassNode> cls) {
        installBasicClasses();
        checkClassRedefinitions(cls);
        checkParentValidity(cls);
        if (Utilities.errors()) {
            Utilities.fatalError(Utilities.ErrorCode.ERROR_SEMANT);
        }
        checkInheritanceCycles(cls);
    }

    private void checkClassRedefinitions(List<ClassNode> cls) {
        for (ClassNode c : cls) {
            Symbol className = c.getName();
            if (classMap.containsKey(className)) {
                if (NON_REDEFINABLE_CLASSES.contains(className)) {
                    Utilities.semantError(c).println("Redefinition of basic class " + className + ".");
                } else {
                    Utilities.semantError(c).println("Class " + className + " was previously defined.");
                }
                classMap.put(className, null); // mark as null to avoid repeat errors, first error takes priority
                continue;
            }
            classMap.put(className, new ArrayList<>());
        }
    }

    private void checkParentValidity(List<ClassNode> cls) {
        for (int i = cls.size() - 1; i >= 0; i--) {
            ClassNode c = cls.get(i);
            Symbol className = c.getName();
            Symbol parent = c.getParent();

            if (classMap.get(className) == null) { // skip classes that already had errors
                continue;
            }

            if (!classMap.containsKey(parent) && !parent.equals(TreeConstants.No_class)) {
                Utilities.semantError(c).println("Class " + className + " inherits from an undefined class " + parent + ".");
                continue;
            }

            if (NON_INHERITABLE_CLASSES.contains(parent)) {
                Utilities.semantError(c).println("Class " + className + " cannot inherit class " + parent + ".");
                continue;
            }

            if (classMap.get(parent) != null) {
                classMap.get(parent).add(c);
            }
        }
    }

    private void checkInheritanceCycles(List<ClassNode> cls) {
        Set<Symbol> cycleClasses = new HashSet<>();
        Set<Symbol> visited = new HashSet<>();

        for (ClassNode c : cls) {
            if (!visited.contains(c.getName())) {
                detectCycles(c.getName(), visited, cycleClasses);
            }
        }

        for (int i = cls.size() - 1; i >= 0; i--) {
            ClassNode c = cls.get(i);
            if (cycleClasses.contains(c.getName())) {
                Utilities.semantError(c).println("Class " + c.getName() + ", or an ancestor of " + c.getName() + ", is involved in an inheritance cycle.");
            }
        }
    }

    private void detectCycles(Symbol className, Set<Symbol> visited, Set<Symbol> cycleClasses) {

        if (visited.contains(className)) {
            markDescendants(className, cycleClasses);
            return;
        }

        visited.add(className);

        for (ClassNode child : classMap.get(className)) {
            detectCycles(child.getName(), visited, cycleClasses);
        }
    }

    private void markDescendants(Symbol currentClass, Set<Symbol> cycleClasses) {
        for (ClassNode child : classMap.get(currentClass)) {
            if (cycleClasses.add(child.getName())) {
                markDescendants(child.getName(), cycleClasses);
            }
        }
    }

    public boolean isValidType(Symbol type) {
        return classMap.containsKey(type);
    }

    public Map<Symbol, ArrayList<ClassNode>> getClassMap() {
        return classMap;
    }

    public boolean isBuiltInMethod(Symbol methodName) {
        return builtInMethods.contains(methodName);
    }

}


