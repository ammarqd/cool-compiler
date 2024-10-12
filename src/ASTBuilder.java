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
            p.add((ClassNode)visitCoolClass(c));
        }
        return p;
    }

    @Override
    public Tree visitCoolClass(CoolParser.CoolClassContext ctx) {

        Symbol className = new Symbol(ctx.TYPEID(0).getText(), ctx.getStart().getLine());
        Symbol parentName = new Symbol(
                ctx.INHERITS() != null ? ctx.TYPEID(1).getText() : "Object",
                ctx.getStart().getLine()
        );
        Symbol fileName = new Symbol(ctx.start.getInputStream().getSourceName(), ctx.getStart().getLine());

        ClassNode classNode = new ClassNode(1, className, parentName, fileName);
        for (CoolParser.FeatureContext f : ctx.feature()) {
            classNode.add((FeatureNode)visitFeature(f));
        }
        return classNode;
    }

    @Override
    public Tree visitFormalList(CoolParser.FormalListContext ctx) {
        // Instead of returning a List<FormalNode>, we will process the formals and return a Tree-compatible type
        List<FormalNode> formals = new ArrayList<>();
        for (CoolParser.FormalContext f : ctx.formal()) {
            formals.add((FormalNode) visitFormal(f));
        }
        return formals.isEmpty() ? null : formals.get(0);  // Returning the first FormalNode, since we can't return a list directly
    }

    @Override
    public Tree visitFeature(CoolParser.FeatureContext ctx) {

        if (ctx.PARENT_OPEN() != null) {

            Symbol featureName = new Symbol(ctx.OBJECTID().getText(), ctx.getStart().getLine());
            List<FormalNode> formals = ctx.formalList() != null ? (List<FormalNode>) visitFormalList(ctx.formalList()) : null;
            Symbol return_type = new Symbol(ctx.TYPEID().getText(), ctx.getStart().getLine());
            ExpressionNode expr = (ExpressionNode) visitExpr(ctx.expr());

            return new MethodNode(1, featureName, formals, return_type, expr);
        }
        return null;
    }

    @Override
    public Tree visitFormal(CoolParser.FormalContext ctx) {
        Symbol formalName = new Symbol(ctx.OBJECTID().getText(), ctx.getStart().getLine());
        Symbol formalType = new Symbol(ctx.TYPEID().getText(), ctx.getStart().getLine());
        return new FormalNode(1, formalName, formalType);
    }

}
