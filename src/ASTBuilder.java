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
        Symbol returnType = new Symbol(ctx.TYPEID().getText(), ctx.getStart().getLine());
        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr());
        return new MethodNode(1, name, formals, returnType, expr);
    }

    private AttributeNode attributeNode(CoolParser.FeatureContext ctx) {
        Symbol name = new Symbol(ctx.OBJECTID().getText(), ctx.getStart().getLine());
        Symbol typeDecl = new Symbol(ctx.TYPEID().getText(), ctx.getStart().getLine());
        ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr());
        return new AttributeNode(1, name, typeDecl, expr);
    }

    @Override
    public Tree visitFormal(CoolParser.FormalContext ctx) {
        Symbol name = new Symbol(ctx.OBJECTID().getText(), ctx.getStart().getLine());
        Symbol typeDecl = new Symbol(ctx.TYPEID().getText(), ctx.getStart().getLine());
        return new FormalNode(1, name, typeDecl);
    }

    @Override
    public Tree visitExpr(CoolParser.ExprContext ctx) {
        if (ctx.ASSIGN_OPERATOR(0) != null) return assignNode(ctx);
        if (ctx.IF() != null) return condNode(ctx);
        if (ctx.WHILE() != null) return loopNode(ctx);
        if (ctx.CURLY_OPEN() != null) return blockNode(ctx);
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

    private AssignNode assignNode(CoolParser.ExprContext ctx) {
        return new AssignNode(1, new Symbol(ctx.OBJECTID(0).getText(), ctx.getStart().getLine()), (ExpressionNode) visitExpr(ctx.expr(0)));
    }

    private CondNode condNode(CoolParser.ExprContext ctx) {
        return new CondNode(1, (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)), (ExpressionNode) visitExpr(ctx.expr(2)));
    }

    private LoopNode loopNode(CoolParser.ExprContext ctx) {
        return new LoopNode(1, (ExpressionNode) visitExpr(ctx.expr(0)), (ExpressionNode) visitExpr(ctx.expr(1)));
    }

    private BlockNode blockNode(CoolParser.ExprContext ctx) {
        List<ExpressionNode> exprList = new ArrayList<>();
        for (CoolParser.ExprContext e : ctx.expr()) {
            exprList.add((ExpressionNode) visitExpr(e));
        }
        return new BlockNode(1, exprList);
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
        return new StringConstNode(1, new Symbol(ctx.STR_CONST().getText(), ctx.getStart().getLine()));
    }

    private BoolConstNode boolConstNode(boolean value) {
        return new BoolConstNode(1, value);
    }
}
