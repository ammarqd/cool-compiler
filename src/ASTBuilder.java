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
        Symbol name = new Symbol(ctx.TYPEID(0).getText(), ctx.getStart().getLine());
        Symbol parent = new Symbol(
                ctx.INHERITS() != null ? ctx.TYPEID(1).getText() : "Object",
                ctx.getStart().getLine()
        );
        Symbol filename = new Symbol(ctx.start.getInputStream().getSourceName(), ctx.getStart().getLine());

        ClassNode class_node = new ClassNode(1, name, parent, filename);
        for (CoolParser.FeatureContext f : ctx.feature()) {
            class_node.add((FeatureNode) visitFeature(f));
        }
        return class_node;
    }

    @Override
    public Tree visitFeature(CoolParser.FeatureContext ctx) {
        if (ctx.PARENT_OPEN() != null) {
            return methodNode(ctx);
        } else {
            return attributeNode(ctx);
        }
    }

    private MethodNode methodNode(CoolParser.FeatureContext ctx) {
        Symbol name = new Symbol(ctx.OBJECTID().getText(), ctx.getStart().getLine());
        List<FormalNode> formals = new ArrayList<>();
        if (ctx.formal() != null) {
            for (CoolParser.FormalContext f : ctx.formal()) {
                formals.add((FormalNode) visitFormal(f));
            }
        }
        Symbol return_type = new Symbol(ctx.TYPEID().getText(), ctx.getStart().getLine());
        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr());
        return new MethodNode(1, name, formals, return_type, expr);
    }

    private AttributeNode attributeNode(CoolParser.FeatureContext ctx) {
        Symbol name = new Symbol(ctx.OBJECTID().getText(), ctx.getStart().getLine());
        Symbol type_decl = new Symbol(ctx.TYPEID().getText(), ctx.getStart().getLine());
        ExpressionNode expr = ctx.expr() != null ?
                (ExpressionNode) visitExpr(ctx.expr()) :
                new NoExpressionNode(1);
        return new AttributeNode(1, name, type_decl, expr);
    }

    @Override
    public Tree visitFormal(CoolParser.FormalContext ctx) {
        Symbol name = new Symbol(ctx.OBJECTID().getText(), ctx.getStart().getLine());
        Symbol type_decl = new Symbol(ctx.TYPEID().getText(), ctx.getStart().getLine());
        return new FormalNode(1, name, type_decl);
    }

    @Override
    public Tree visitExpr(CoolParser.ExprContext ctx) {
        if (ctx.LET() != null) return letNode(ctx);
        if (ctx.ASSIGN_OPERATOR(0) != null) return assignNode(ctx);
        if (ctx.AT() != null) return staticDispatchNode(ctx);
        if (ctx.OBJECTID(0) != null && ctx.PARENT_OPEN() != null) return dispatchNode(ctx);
        if (ctx.IF() != null) return condNode(ctx);
        if (ctx.WHILE() != null) return loopNode(ctx);
        if (ctx.CURLY_OPEN() != null) return blockNode(ctx);
        if (ctx.CASE() != null) return caseNode(ctx);
        if (ctx.NEW() != null) return newNode(ctx);
        if (ctx.ISVOID() != null) return isVoidNode(ctx);
        if (ctx.PLUS_OPERATOR() != null) return plusNode(ctx);
        if (ctx.MINUS_OPERATOR() != null) return subNode(ctx);
        if (ctx.MULT_OPERATOR() != null) return mulNode(ctx);
        if (ctx.DIV_OPERATOR() != null) return divideNode(ctx);
        if (ctx.INT_COMPLEMENT_OPERATOR() != null) return negNode(ctx);
        if (ctx.LESS_OPERATOR() != null) return ltNode(ctx);
        if (ctx.LESS_EQ_OPERATOR() != null) return leqNode(ctx);
        if (ctx.EQ_OPERATOR() != null) return eqNode(ctx);
        if (ctx.NOT() != null) return compNode(ctx);
        if (ctx.PARENT_OPEN() != null) return visitExpr(ctx.expr(0));
        if (ctx.OBJECTID(0) != null) return objectNode(ctx);
        if (ctx.INT_CONST() != null) return intConstNode(ctx);
        if (ctx.STR_CONST() != null) return stringConstNode(ctx);
        if (ctx.TRUE() != null) return boolConstNode(true);
        if (ctx.FALSE() != null) return boolConstNode(false);
        return null;
    }

    private LetNode letNode(CoolParser.ExprContext ctx) {

        Symbol identifier = new Symbol(ctx.OBJECTID(0).getText(), ctx.getStart().getLine());
        Symbol type_decl = new Symbol(ctx.TYPEID(0).getText(), ctx.getStart().getLine());

        ExpressionNode init = (ctx.ASSIGN_OPERATOR(0) != null) ?
                (ExpressionNode) visitExpr(ctx.expr(0)) :
                new NoExpressionNode(1);

        for (int i = 1; i < ctx.OBJECTID().size(); i++) {
            Symbol next_identifier = new Symbol(ctx.OBJECTID(i).getText(), ctx.getStart().getLine());
            Symbol next_type_decl = new Symbol(ctx.TYPEID(i).getText(), ctx.getStart().getLine());
            ExpressionNode next_init = (ctx.expr(i) != null ?
                    (ExpressionNode) visitExpr(ctx.expr(i)) :
                    new NoExpressionNode(1));
            init = new LetNode(1, next_identifier, next_type_decl, next_init, init);
        }
        ExpressionNode body = (ExpressionNode) visitExpr(ctx.expr(ctx.expr().size() - 1));
        return new LetNode(1, identifier, type_decl, init, body);
    }

    private AssignNode assignNode(CoolParser.ExprContext ctx) {
        return new AssignNode(1, new Symbol(ctx.OBJECTID(0).getText(), ctx.getStart().getLine()), (ExpressionNode) visitExpr(ctx.expr(0)));
    }

    private DispatchNode dispatchNode(CoolParser.ExprContext ctx) {
        List<ExpressionNode> actuals = new ArrayList<>();
        ExpressionNode expr;
        if (!ctx.expr().isEmpty()) {
            expr = (ExpressionNode) visitExpr(ctx.expr(0));
            for (int i = 1; i < ctx.expr().size(); i++)
                actuals.add((ExpressionNode) visitExpr(ctx.expr(i)));
        } else
            expr = new ObjectNode(1, new Symbol("self", ctx.getStart().getLine()));
        return new DispatchNode(1, expr, new Symbol(ctx.OBJECTID(0).getText(), ctx.getStart().getLine()), actuals);
    }

    private StaticDispatchNode staticDispatchNode(CoolParser.ExprContext ctx) {
        List<ExpressionNode> actuals = new ArrayList<>();
        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr(0));
        for (int i = 1; i < ctx.expr().size(); i++) {
            actuals.add((ExpressionNode) visitExpr(ctx.expr(i)));
        }
        Symbol methodName = new Symbol(ctx.OBJECTID(0).getText(), ctx.getStart().getLine());
        Symbol typeName = new Symbol(ctx.TYPEID(0).getText(), ctx.getStart().getLine());
        return new StaticDispatchNode(
                1,  // Line number
                expr,                      // Expression (left-hand side)
                typeName,                  // Type (specified after '@')
                methodName,                // Method name
                actuals                    // Method arguments
        );
    }

    private CondNode condNode(CoolParser.ExprContext ctx) {
        return new CondNode(1, (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)), (ExpressionNode) visitExpr(ctx.expr(2)));
    }

    private LoopNode loopNode(CoolParser.ExprContext ctx) {
        return new LoopNode(1, (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)));
    }

    private CaseNode caseNode(CoolParser.ExprContext ctx) {

        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr(0));
        List<BranchNode> branches = new ArrayList<>();
        int caseBranchCount = ctx.OBJECTID().size();
        for (int i = 0; i < caseBranchCount; i++) {
            Symbol identifier = new Symbol(ctx.OBJECTID(i).getText(), ctx.getStart().getLine());
            Symbol type_decl = new Symbol(ctx.TYPEID(i).getText(), ctx.getStart().getLine());
            ExpressionNode branch_expr = (ExpressionNode) visitExpr(ctx.expr(i + 1));
            branches.add(new BranchNode(1, identifier, type_decl, branch_expr));
        }
        return new CaseNode(1, expr, branches);
    }


    private BlockNode blockNode(CoolParser.ExprContext ctx) {

        List<ExpressionNode> expr_list = new ArrayList<>();
        for (CoolParser.ExprContext e : ctx.expr()) {
            expr_list.add((ExpressionNode) visitExpr(e));
        }
        return new BlockNode(1, expr_list);
    }

    private NewNode newNode(CoolParser.ExprContext ctx) {
        return new NewNode(1, new Symbol(ctx.TYPEID(0).getText(), ctx.getStart().getLine()));
    }

    private IsVoidNode isVoidNode(CoolParser.ExprContext ctx) {
        return new IsVoidNode(1, (ExpressionNode) visitExpr(ctx.expr(0)));
    }

    private PlusNode plusNode(CoolParser.ExprContext ctx) {
        return new PlusNode(1, (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)));
    }

    private SubNode subNode(CoolParser.ExprContext ctx) {
        return new SubNode(1, (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)));
    }

    private MulNode mulNode(CoolParser.ExprContext ctx) {
        return new MulNode(1, (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)));
    }

    private DivideNode divideNode(CoolParser.ExprContext ctx) {
        return new DivideNode(1, (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)));
    }

    private NegNode negNode(CoolParser.ExprContext ctx) {
        return new NegNode(1, (ExpressionNode) visitExpr(ctx.expr(0)));
    }

    private LTNode ltNode(CoolParser.ExprContext ctx) {
        return new LTNode(1, (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)));
    }

    private LEqNode leqNode(CoolParser.ExprContext ctx) {
        return new LEqNode(1, (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)));
    }

    private EqNode eqNode(CoolParser.ExprContext ctx) {
        return new EqNode(1, (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)));
    }

    private CompNode compNode(CoolParser.ExprContext ctx) {
        return new CompNode(1, (ExpressionNode) visitExpr(ctx.expr(0)));
    }

    private ObjectNode objectNode(CoolParser.ExprContext ctx) {
        return new ObjectNode(1, new Symbol(ctx.OBJECTID(0).getText(), ctx.getStart().getLine()));
    }

    private IntConstNode intConstNode(CoolParser.ExprContext ctx) {
        return new IntConstNode(1, new Symbol(ctx.INT_CONST().getText(), ctx.getStart().getLine()));
    }

    private StringConstNode stringConstNode(CoolParser.ExprContext ctx) {

        String rawString = ctx.STR_CONST().getText().substring(1, ctx.STR_CONST().getText().length() - 1);
        rawString = rawString.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\b", "\b")
                .replace("\\f", "\f")
                .replace("\\r", "\r")
                .replace("\\\\", "\\");
        return new StringConstNode(1, new Symbol(rawString, ctx.getStart().getLine()));
    }

    private BoolConstNode boolConstNode(boolean value) {
        return new BoolConstNode(1, value);
    }
}
