import ast.ProgramNode;
import ast.Symbol;

class Semant {

    public static ClassTable classTable;
    public static SymbolTable<Symbol> symTable;

    public static void analyze(ProgramNode program) {
        classTable = new ClassTable(program.getClasses());
        symTable = new SymbolTable<>();

        if (Utilities.errors()) {
            Utilities.fatalError(Utilities.ErrorCode.ERROR_SEMANT);
        }

        ScopeCheckingVisitor scopecheckVisitor = new ScopeCheckingVisitor();
        program.accept(scopecheckVisitor, null);
        TypeCheckingVisitor typecheckVisitor = new TypeCheckingVisitor();
        program.accept(typecheckVisitor, null);

        if (Utilities.errors()) {
            Utilities.fatalError(Utilities.ErrorCode.ERROR_SEMANT);
        }
    }

}
