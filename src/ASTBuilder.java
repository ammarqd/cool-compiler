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
        return visitAssignmentExpr(ctx.assignmentExpr());
    }

    @Override
    public Tree visitAssignmentExpr(CoolParser.AssignmentExprContext ctx) {
        if (ctx.negationExpr() != null) {
            return visitNegationExpr(ctx.negationExpr());
        }
        if (ctx.LET() != null) {
            List<Symbol> identifiers = new ArrayList<>();
            List<Symbol> typeDecls = new ArrayList<>();
            List<ExpressionNode> inits = new ArrayList<>();
            for (int i = 0; i < ctx.OBJECTID().size(); i++) {
                Symbol identifier = new Symbol(ctx.OBJECTID(i).getText(), ctx.getStart().getLine());
                Symbol typeDecl = new Symbol(ctx.TYPEID(i).getText(), ctx.getStart().getLine());
                ExpressionNode init = (ctx.ASSIGN_OPERATOR(i) != null)
                        ? (ExpressionNode) visitExpr(ctx.expr(i))
                        : new NoExpressionNode(1);
                identifiers.add(identifier);
                typeDecls.add(typeDecl);
                inits.add(init);
            }
            ExpressionNode body = (ExpressionNode) visitExpr(ctx.expr(ctx.expr().size() - 1));
            for (int i = identifiers.size() - 1; i >= 0; i--) {
                body = new LetNode(1, identifiers.get(i), typeDecls.get(i), inits.get(i), body);
            }
            return body;
        }
        return null;
    }

    @Override
    public Tree visitNegationExpr(CoolParser.NegationExprContext ctx) {
        if (ctx.NOT() != null) {
            return new CompNode(1, (ExpressionNode) visitExpr(ctx.expr()));
        }
        return visitComparisonExpr(ctx.comparisonExpr());
    }

    @Override
    public Tree visitComparisonExpr(CoolParser.ComparisonExprContext ctx) {
        if (ctx.LESS_OPERATOR() == null && ctx.LESS_EQ_OPERATOR() == null && ctx.EQ_OPERATOR() == null) {
            return visitDefaultExpr(ctx.defaultExpr(0));
        }

        ExpressionNode left = (ExpressionNode) visitDefaultExpr(ctx.defaultExpr(0));
        ExpressionNode right = (ExpressionNode) visitDefaultExpr(ctx.defaultExpr(1));

        if (ctx.LESS_OPERATOR() != null) return new LTNode(1, left, right);
        if (ctx.LESS_EQ_OPERATOR() != null) return new LEqNode(1, left, right);
        if (ctx.EQ_OPERATOR() != null) return new EqNode(1, left, right);

        return null;
    }

    @Override
    public Tree visitDefaultExpr(CoolParser.DefaultExprContext ctx) {
        if (ctx.MULT_OPERATOR() != null) {
            return new MulNode(1,
                    (ExpressionNode) visitDefaultExpr(ctx.defaultExpr(0)),
                    (ExpressionNode) visitDefaultExpr(ctx.defaultExpr(1))
            );
        }
        if (ctx.DIV_OPERATOR() != null) {
            return new DivideNode(1,
                    (ExpressionNode) visitDefaultExpr(ctx.defaultExpr(0)),
                    (ExpressionNode) visitDefaultExpr(ctx.defaultExpr(1))
            );
        }
        if (ctx.PLUS_OPERATOR() != null) {
            return new PlusNode(1,
                    (ExpressionNode) visitDefaultExpr(ctx.defaultExpr(0)),
                    (ExpressionNode) visitDefaultExpr(ctx.defaultExpr(1))
            );
        }
        if (ctx.MINUS_OPERATOR() != null) {
            return new SubNode(1,
                    (ExpressionNode) visitDefaultExpr(ctx.defaultExpr(0)),
                    (ExpressionNode) visitDefaultExpr(ctx.defaultExpr(1))
            );
        }
        if (ctx.NOT() != null) {
            return new CompNode(1, (ExpressionNode) visitExpr(ctx.expr(0)));
        }
        if (ctx.LET() != null) {
            List<Symbol> identifiers = new ArrayList<>();
            List<Symbol> typeDecls = new ArrayList<>();
            List<ExpressionNode> inits = new ArrayList<>();
            for (int i = 0; i < ctx.OBJECTID().size(); i++) {
                Symbol identifier = new Symbol(ctx.OBJECTID(i).getText(), ctx.getStart().getLine());
                Symbol typeDecl = new Symbol(ctx.TYPEID(i).getText(), ctx.getStart().getLine());
                ExpressionNode init = (ctx.ASSIGN_OPERATOR(i) != null)
                        ? (ExpressionNode) visitExpr(ctx.expr(i))
                        : new NoExpressionNode(1);
                identifiers.add(identifier);
                typeDecls.add(typeDecl);
                inits.add(init);
            }
            ExpressionNode body = (ExpressionNode) visitExpr(ctx.expr(ctx.expr().size() - 1));
            for (int i = identifiers.size() - 1; i >= 0; i--) {
                body = new LetNode(1, identifiers.get(i), typeDecls.get(i), inits.get(i), body);
            }
            return body;
        }
        if (ctx.OBJECTID(0) != null && ctx.ASSIGN_OPERATOR() != null) {
            return new AssignNode(1,
                    new Symbol(ctx.OBJECTID(0).getText(), ctx.getStart().getLine()),
                    (ExpressionNode) visitExpr(ctx.expr(0)));
        }
        if (ctx.INT_CONST() != null) {
            return new IntConstNode(1, new Symbol(ctx.INT_CONST().getText(), ctx.getStart().getLine()));
        }
        return null;
    }

}