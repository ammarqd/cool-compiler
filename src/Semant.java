import ast.ProgramNode;
import ast.Symbol;

import java.util.HashMap;
import java.util.Map;

class Semant {

    public enum Kind {
        CLASS, METHOD, ATTRIBUTE, VARIABLE
    }

    private static ClassTable classTable;
    private static final Map<Kind, SymbolTable<Symbol>> tables;

    static {
        tables = new HashMap<>();
        for (Kind k : Kind.values()) {
            tables.put(k, new SymbolTable<>());
            tables.get(k).enterScope();
        }
    }

    public static ClassTable getClassTable() {
        return classTable;
    }

    public static SymbolTable<Symbol> getTable(Kind kind) {
        return tables.get(kind);
    }

    public static void analyze(ProgramNode program) {
        classTable = new ClassTable(program.getClasses());
        
        ScopeCheckingVisitor scopecheckVisitor = new ScopeCheckingVisitor();
        program.accept(scopecheckVisitor, null);
        TypeCheckingVisitor typecheckVisitor = new TypeCheckingVisitor();
        program.accept(typecheckVisitor, null);

        if (Utilities.errors()) {
            Utilities.fatalError(Utilities.ErrorCode.ERROR_SEMANT);
        }
    }

}
