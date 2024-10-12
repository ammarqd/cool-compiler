import ast.ClassNode;
import ast.ProgramNode;
import ast.*;

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

}
