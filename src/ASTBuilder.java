import ast.ClassNode;
import ast.ProgramNode;
import ast.*;
import java.util.List;
import java.util.ArrayList;

public class ASTBuilder extends CoolParserBaseVisitor<Tree> {

    @Override
    public Tree visitProgram(CoolParser.ProgramContext ctx) {
        ProgramNode p = new ProgramNode(ctx.getStart().getLine());
        for (CoolParser.CoolClassContext c : ctx.coolClass()) {
            p.add((ClassNode) visitCoolClass(c));
        }
        return p;
    }

    @Override
    public Tree visitCoolClass(CoolParser.CoolClassContext ctx) {
        Symbol name = StringTable.idtable.addString(ctx.TYPEID(0).getText());
        Symbol parent = ctx.INHERITS() != null ?
            StringTable.idtable.addString(ctx.TYPEID(1).getText()) : TreeConstants.Object_;
        Symbol filename = StringTable.stringtable.addString(ctx.getStart().getTokenSource().getSourceName());

        ClassNode class_node = new ClassNode(ctx.getStart().getLine(), name, parent, filename);
        for (CoolParser.FeatureContext f : ctx.feature()) {
            class_node.add((FeatureNode) visit(f));
        }
        return class_node;
    }

    @Override
    public Tree visitMethod(CoolParser.MethodContext ctx) {
        Symbol name = StringTable.idtable.addString(ctx.OBJECTID().getText());
        List<FormalNode> formals = new ArrayList<>();
        for (CoolParser.FormalContext f : ctx.formal()) {
            formals.add((FormalNode) visitFormal(f));
        }
        Symbol return_type = StringTable.idtable.addString(ctx.TYPEID().getText());
        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr());
        return new MethodNode(ctx.getStart().getLine(), name, formals, return_type, expr);
    }

    @Override
    public Tree visitAttribute(CoolParser.AttributeContext ctx) {
        Symbol name = StringTable.idtable.addString(ctx.OBJECTID().getText());
        Symbol type_decl = StringTable.idtable.addString(ctx.TYPEID().getText());
        ExpressionNode expr = ctx.expr() != null ?
                (ExpressionNode) visitExpr(ctx.expr()) :
                new NoExpressionNode(ctx.getStart().getLine());
        return new AttributeNode(ctx.getStart().getLine(), name, type_decl, expr);
    }

    @Override
    public Tree visitFormal(CoolParser.FormalContext ctx) {
        Symbol name = StringTable.idtable.addString(ctx.OBJECTID().getText());
        Symbol type_decl = StringTable.idtable.addString(ctx.TYPEID().getText());
        return new FormalNode(ctx.getStart().getLine(), name, type_decl);
    }

    @Override
    public Tree visitExpr(CoolParser.ExprContext ctx) {
        if (ctx.comparisonExpr() != null) {
            return visitComparisonExpr(ctx.comparisonExpr());
        }
        return visit(ctx.defaultExpr());
    }

    @Override
    public Tree visitComparisonExpr(CoolParser.ComparisonExprContext ctx) {
        ExpressionNode left = (ExpressionNode) visit(ctx.defaultExpr(0));
        ExpressionNode right = (ExpressionNode) visit(ctx.defaultExpr(1));

        if (ctx.LESS_OPERATOR() != null) return new LTNode(ctx.getStart().getLine(), left, right);
        if (ctx.LESS_EQ_OPERATOR() != null) return new LEqNode(ctx.getStart().getLine(), left, right);
        if (ctx.EQ_OPERATOR() != null) return new EqNode(ctx.getStart().getLine(), left, right);

        return null;
    }

    @Override
    public Tree visitStaticDispatch(CoolParser.StaticDispatchContext ctx) {
        List<ExpressionNode> actuals = new ArrayList<>();
        ExpressionNode expr = (ExpressionNode) visit(ctx.defaultExpr());
        for (CoolParser.ExprContext e : ctx.expr()) {
            actuals.add((ExpressionNode) visitExpr(e));
        }
        Symbol type_name = StringTable.idtable.addString(ctx.TYPEID().getText());
        Symbol method_name = StringTable.idtable.addString(ctx.OBJECTID().getText());
        return new StaticDispatchNode(ctx.getStart().getLine(), expr, type_name, method_name, actuals);
    }

    @Override
    public Tree visitDynamicDispatch(CoolParser.DynamicDispatchContext ctx) {
        List<ExpressionNode> actuals = new ArrayList<>();
        ExpressionNode expr = (ExpressionNode) visit(ctx.defaultExpr());
        for (CoolParser.ExprContext e : ctx.expr()) {
            actuals.add((ExpressionNode) visitExpr(e));
        }
        return new DispatchNode(ctx.getStart().getLine(), expr, StringTable.idtable.addString(ctx.OBJECTID().getText()), actuals);
    }

    @Override
    public Tree visitMethodCall(CoolParser.MethodCallContext ctx) {
        List<ExpressionNode> actuals = new ArrayList<>();
        ExpressionNode expr = new ObjectNode(ctx.getStart().getLine(), StringTable.idtable.addString("self"));
        for (CoolParser.ExprContext e : ctx.expr()) {
            actuals.add((ExpressionNode) visitExpr(e));
        }
        return new DispatchNode(ctx.getStart().getLine(), expr, StringTable.idtable.addString(ctx.OBJECTID().getText()), actuals);
    }

    @Override
    public Tree visitComplement(CoolParser.ComplementContext ctx) {
        return new NegNode(ctx.getStart().getLine(), (ExpressionNode) visit(ctx.defaultExpr()));
    }

    @Override
    public Tree visitIsVoid(CoolParser.IsVoidContext ctx) {
        return new IsVoidNode(ctx.getStart().getLine(), (ExpressionNode) visit(ctx.defaultExpr()));
    }

    @Override
    public Tree visitParenthesis(CoolParser.ParenthesisContext ctx) {
        return visitExpr(ctx.expr());
    }

    @Override
    public Tree visitAddSub(CoolParser.AddSubContext ctx) {
        ExpressionNode left = (ExpressionNode) visit(ctx.defaultExpr(0));
        ExpressionNode right = (ExpressionNode) visit(ctx.defaultExpr(1));

        if (ctx.PLUS_OPERATOR() != null) return new PlusNode(ctx.getStart().getLine(), left, right);
        if (ctx.MINUS_OPERATOR() != null) return new SubNode(ctx.getStart().getLine(), left, right);

        return null;
    }

    @Override
    public Tree visitMultDiv(CoolParser.MultDivContext ctx) {
        ExpressionNode left = (ExpressionNode) visit(ctx.defaultExpr(0));
        ExpressionNode right = (ExpressionNode) visit(ctx.defaultExpr(1));

        if (ctx.MULT_OPERATOR() != null) return new MulNode(ctx.getStart().getLine(), left, right);
        if (ctx.DIV_OPERATOR() != null) return new DivideNode(ctx.getStart().getLine(), left, right);

        return null;
    }

    @Override
    public Tree visitNot(CoolParser.NotContext ctx) {
        return new CompNode(ctx.getStart().getLine(), (ExpressionNode) visitExpr(ctx.expr()));
    }

    @Override
    public Tree visitAssign(CoolParser.AssignContext ctx) {
        return new AssignNode(ctx.getStart().getLine(), StringTable.idtable.addString(ctx.OBJECTID().getText()), (ExpressionNode) visitExpr(ctx.expr()));
    }

    @Override
    public Tree visitLet(CoolParser.LetContext ctx) {
        List<Symbol> identifiers = new ArrayList<>();
        List<Symbol> type_decls = new ArrayList<>();
        List<ExpressionNode> inits = new ArrayList<>();
        for (int i = 0; i < ctx.OBJECTID().size(); i++) {
            Symbol identifier = StringTable.idtable.addString(ctx.OBJECTID(i).getText());
            Symbol type_decl = StringTable.idtable.addString(ctx.TYPEID(i).getText());
            ExpressionNode init = (ctx.ASSIGN_OPERATOR(i) != null)
                    ? (ExpressionNode) visitExpr(ctx.expr(i))
                    : new NoExpressionNode(ctx.getStart().getLine());
            identifiers.add(identifier);
            type_decls.add(type_decl);
            inits.add(init);
        }
        ExpressionNode body = (ExpressionNode) visitExpr(ctx.expr(ctx.expr().size() - 1));
        for (int i = identifiers.size() - 1; i >= 0; i--) {
            body = new LetNode(ctx.getStart().getLine(), identifiers.get(i), type_decls.get(i), inits.get(i), body);
        }
        return body;
    }

    @Override
    public Tree visitConditional(CoolParser.ConditionalContext ctx) {
        return new CondNode(ctx.getStart().getLine(), (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)), (ExpressionNode) visitExpr(ctx.expr(2)));
    }

    @Override
    public Tree visitLoop(CoolParser.LoopContext ctx) {
        return new LoopNode(ctx.getStart().getLine(), (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)));
    }

    @Override
    public Tree visitBlock(CoolParser.BlockContext ctx) {
        List<ExpressionNode> expr_list = new ArrayList<>();
        for (CoolParser.ExprContext e : ctx.expr()) {
            expr_list.add((ExpressionNode) visitExpr(e));
        }
        return new BlockNode(ctx.getStart().getLine(), expr_list);
    }

    @Override
    public Tree visitCase(CoolParser.CaseContext ctx) {
        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr(0));
        List<BranchNode> branches = new ArrayList<>();
        int caseBranchCount = ctx.OBJECTID().size();
        for (int i = 0; i < caseBranchCount; i++) {
            Symbol identifier = StringTable.idtable.addString(ctx.OBJECTID(i).getText());
            Symbol type_decl = StringTable.idtable.addString(ctx.TYPEID(i).getText());
            ExpressionNode branch_expr = (ExpressionNode) visitExpr(ctx.expr(i + 1));
            branches.add(new BranchNode(ctx.getStart().getLine(), identifier, type_decl, branch_expr));
        }
        return new CaseNode(ctx.getStart().getLine(), expr, branches);
    }

    @Override
    public Tree visitNew(CoolParser.NewContext ctx) {
        return new NewNode(ctx.getStart().getLine(), StringTable.idtable.addString(ctx.TYPEID().getText()));
    }

    @Override
    public Tree visitObject(CoolParser.ObjectContext ctx) {
        return new ObjectNode(ctx.getStart().getLine(), StringTable.idtable.addString(ctx.OBJECTID().getText()));
    }

    @Override
    public Tree visitInteger(CoolParser.IntegerContext ctx) {
        return new IntConstNode(ctx.getStart().getLine(), StringTable.inttable.addString(ctx.INT_CONST().getText()));
    }

    @Override
    public Tree visitString(CoolParser.StringContext ctx) {
        String rawString = ctx.STR_CONST().getText().substring(1, ctx.STR_CONST().getText().length() - 1);
        rawString = rawString.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\b", "\b")
                .replace("\\f", "\f")
                .replace("\\r", "\r")
                .replace("\\\\", "\\");
        return new StringConstNode(ctx.getStart().getLine(), StringTable.stringtable.addString(rawString));
    }

    @Override
    public Tree visitTrue(CoolParser.TrueContext ctx) {
        return new BoolConstNode(ctx.getStart().getLine(), true);
    }

    @Override
    public Tree visitFalse(CoolParser.FalseContext ctx) {
        return new BoolConstNode(ctx.getStart().getLine(), false);
    }

}