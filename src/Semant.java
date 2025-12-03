import ast.ClassNode;
import ast.ProgramNode;
import ast.Symbol;

class Semant {

    public static ClassTable classTable;
    public static SymbolTable<Symbol> symTable = new SymbolTable<>();

    public static void analyze(ProgramNode program) {
        classTable = new ClassTable(program.getClasses());

        ClassNode objectNode = classTable.getClass(TreeConstants.Object_);
        ScopeContext rootContext = new ScopeContext(objectNode);

        ScopeCheckingVisitor scopecheckVisitor = new ScopeCheckingVisitor();
        program.accept(scopecheckVisitor, rootContext);
        TypeCheckingVisitor typecheckVisitor = new TypeCheckingVisitor();
        program.accept(typecheckVisitor, null);

        if (Utilities.errors()) {
            Utilities.fatalError(Utilities.ErrorCode.ERROR_SEMANT);
        }
    }

}
