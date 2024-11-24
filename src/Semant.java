import ast.ProgramNode;
import ast.Symbol;

class Semant {

    private static ClassTable classTable;
    private static SymbolTable<Symbol> symTable;

    public static ClassTable getClassTable() {
        return classTable;
    }

    public static SymbolTable<Symbol> getTable() {
        return symTable;
    }

    public static void analyze(ProgramNode program) {
        classTable = new ClassTable(program.getClasses());
        symTable = new SymbolTable<>();

        ScopeCheckingVisitor scopecheckVisitor = new ScopeCheckingVisitor();
        program.accept(scopecheckVisitor, null);
        TypeCheckingVisitor typecheckVisitor = new TypeCheckingVisitor();
        program.accept(typecheckVisitor, null);

        if (Utilities.errors()) {
            Utilities.fatalError(Utilities.ErrorCode.ERROR_SEMANT);
        }
    }

}
