package cop5556fa17;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.State;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

public class TypeCheckVisitor implements ASTVisitor {

	public HashMap<String, ASTNode> symbolTable = new HashMap<>();

	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super("line " + t.line + " pos " + t.pos_in_line + ": " + message);
			this.t = t;
		}
	}

	/**
	 * The program name is only used for naming the class. It does not rule out
	 * variables with the same name. It is returned for convenience.
	 * 
	 * @throws Exception
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node : program.decsAndStatements) {
			node.visit(this, arg);
		}
		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {

		String name = declaration_Variable.name;
		if (symbolTable.containsKey(name)) {
			throw new SemanticException(declaration_Variable.firstToken, name + "already in symbol table");
		} else {
			symbolTable.put(name, declaration_Variable);
		}
		declaration_Variable.type_attribute = TypeUtils.getType(declaration_Variable.type);
		Expression expression = declaration_Variable.e;
		if (declaration_Variable.e != null) {
			expression.visit(this, arg);
			Type expression_type = expression.type_attribute;
			Type declarationVariable_type = declaration_Variable.type_attribute;
			if (expression_type != declarationVariable_type) {
				throw new SemanticException(declaration_Variable.firstToken,
						name + "Type mismatch between expression and declaration variable");
			}
		}
		return declaration_Variable.type_attribute;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		Kind op = expression_Binary.op;
		expression_Binary.e0.visit(this, arg);
		expression_Binary.e1.visit(this, arg);
		if (op == Kind.OP_EQ || op == Kind.OP_NEQ) {
			expression_Binary.type_attribute = Type.BOOLEAN;
		} else if ((op == Kind.OP_GE || op == Kind.OP_GT || op == Kind.OP_LT || op == Kind.OP_LE)
				&& expression_Binary.e0.type_attribute == Type.INTEGER) {
			expression_Binary.type_attribute = Type.BOOLEAN;
		} else if ((op == Kind.OP_AND || op == Kind.OP_OR) && (expression_Binary.e0.type_attribute == Type.INTEGER
				|| expression_Binary.e0.type_attribute == Type.BOOLEAN)) {
			expression_Binary.type_attribute = expression_Binary.e0.type_attribute;
		} else if ((op == Kind.OP_DIV || op == Kind.OP_MINUS || op == Kind.OP_MOD || op == Kind.OP_PLUS
				|| op == Kind.OP_POWER || op == Kind.OP_TIMES)
				&& (expression_Binary.e0.type_attribute == Type.INTEGER)) {
			expression_Binary.type_attribute = Type.INTEGER;
		} else {
			expression_Binary.type_attribute = null;
		}

		if (expression_Binary.e0.type_attribute != expression_Binary.e1.type_attribute) {
			throw new SemanticException(expression_Binary.firstToken,
					"In expression binary, e0 and e1 type must be same");
		}
		if (expression_Binary.type_attribute == null) {
			throw new SemanticException(expression_Binary.firstToken,
					"In expression binary, expression binary type cannot be null");
		}
		return expression_Binary.type_attribute;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		Kind op = expression_Unary.op;
		expression_Unary.e.visit(this, arg);
		Type t = expression_Unary.e.type_attribute;
		if (op == Kind.OP_EXCL && (t == Type.BOOLEAN || t == Type.INTEGER)) {
			expression_Unary.type_attribute = t;
		} else if ((op == Kind.OP_PLUS || op == Kind.OP_MINUS) && t == Type.INTEGER) {
			expression_Unary.type_attribute = Type.INTEGER;
		} else {
			expression_Unary.type_attribute = null;
		}
		if (expression_Unary.type_attribute == null) {
			throw new SemanticException(expression_Unary.firstToken, "expression unary type cannot be null");
		}
		return expression_Unary.type_attribute;
	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {

		index.e0.visit(this, arg);
		index.e1.visit(this, arg);
		if (index.e0.type_attribute != Type.INTEGER || index.e1.type_attribute != Type.INTEGER) {
			throw new SemanticException(index.firstToken,
					"expression0 and expresssion1 in Index must be of Integer type");
		}

		index.setCartesian(!(index.e0.getClass().equals(Expression_PredefinedName.class)
				&& index.e1.getClass().equals(Expression_PredefinedName.class) && index.e0.firstToken.kind == Kind.KW_r
				&& index.e1.firstToken.kind == Kind.KW_a));
		return index.type_attribute;
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {

		String name = expression_PixelSelector.name;
		expression_PixelSelector.index.visit(this, arg);
		if (symbolTable.containsKey(name)) {
			ASTNode decl = symbolTable.get(name);
			if (decl.type_attribute == Type.IMAGE) {
				expression_PixelSelector.type_attribute = Type.INTEGER;
			} else if (expression_PixelSelector.index == null) {
				expression_PixelSelector.type_attribute = decl.type_attribute;
			} else {
				throw new SemanticException(expression_PixelSelector.firstToken,
						"visitExpression_PixelSelector undefined type");
			}
		} else {
			throw new SemanticException(expression_PixelSelector.firstToken,
					"visitExpression_PixelSelector variable not already declared");
		}
		if (expression_PixelSelector.type_attribute == null) {
			throw new SemanticException(expression_PixelSelector.firstToken,
					"visitExpression_PixelSelector type_attribute is null");
		}
		return expression_PixelSelector.type_attribute;

	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		expression_Conditional.condition.visit(this, arg);
		expression_Conditional.trueExpression.visit(this, arg);
		expression_Conditional.falseExpression.visit(this, arg);
		expression_Conditional.type_attribute = expression_Conditional.trueExpression.type_attribute;
		if (expression_Conditional.type_attribute != Type.BOOLEAN
				|| expression_Conditional.trueExpression.type_attribute != expression_Conditional.falseExpression.type_attribute) {
			throw new SemanticException(expression_Conditional.firstToken,
					"In expression_conditional, type mismatch betwen true and false expressions");
		}
		return expression_Conditional.type_attribute;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {

		declaration_Image.type_attribute = Type.IMAGE;
		String name = declaration_Image.name;
		declaration_Image.source.visit(this, arg);

		if (symbolTable.containsKey(name)) {
			throw new SemanticException(declaration_Image.firstToken, name + " already in symbol table");
		} else {
			symbolTable.put(name, declaration_Image);
		}
		if (declaration_Image.xSize != null) {
			if (declaration_Image.ySize == null) {
				throw new SemanticException(declaration_Image.firstToken,
						" In declaration_image, ySize cannot be null when xSize is not null");
			}
			declaration_Image.xSize.visit(this, arg);
			declaration_Image.ySize.visit(this, arg);

			if (declaration_Image.ySize.type_attribute != Type.INTEGER
					|| declaration_Image.xSize.type_attribute != Type.INTEGER) {
				throw new SemanticException(declaration_Image.firstToken,
						" In declaration_image, xsize and ysize must be of type integer");
			}
		}
		return declaration_Image.type_attribute;
	}

	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {

		try {
			new URL(source_StringLiteral.fileOrUrl);
			source_StringLiteral.type_attribute = Type.URL;
		} catch (MalformedURLException e) {
			source_StringLiteral.type_attribute = Type.FILE;
		}
		return source_StringLiteral.type_attribute;
	}

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		source_CommandLineParam.paramNum.visit(this, arg);
		source_CommandLineParam.type_attribute = source_CommandLineParam.paramNum.type_attribute;
		Type source_CommandLineParam_type = source_CommandLineParam.type_attribute;
		if (source_CommandLineParam_type != Type.INTEGER) {
			throw new SemanticException(source_CommandLineParam.firstToken,
					"source_commandLineParam expects an integer data type");
		}

		return source_CommandLineParam_type;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {

		if (symbolTable.containsKey(source_Ident.name) == false) {
			throw new SemanticException(source_Ident.firstToken,
					source_Ident.name + " not declared before use in source_Ident ");
		}
		source_Ident.type_attribute = symbolTable.get(source_Ident.name).type_attribute;
		if (source_Ident.type_attribute == Type.FILE || source_Ident.type_attribute == Type.URL) {
			return source_Ident.type_attribute;
		} else {
			throw new SemanticException(source_Ident.firstToken, "source_Ident expects a File or URL data type");
		}
	}

	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {

		String name = declaration_SourceSink.name;
		if (symbolTable.containsKey(name)) {
			throw new SemanticException(declaration_SourceSink.firstToken, name + " already in symbol table");
		} else {
			symbolTable.put(name, declaration_SourceSink);
		}
		declaration_SourceSink.type_attribute = TypeUtils.getType(declaration_SourceSink.type);
		declaration_SourceSink.source.visit(this, arg);

		if (declaration_SourceSink.source.type_attribute != declaration_SourceSink.type_attribute) {
			throw new SemanticException(declaration_SourceSink.firstToken,
					"type of source and sourcsink declaration are different");
		}
		return declaration_SourceSink.type_attribute;
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		expression_IntLit.type_attribute = Type.INTEGER;
		return expression_IntLit.type_attribute;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		expression_FunctionAppWithExprArg.arg.visit(this, arg);
		expression_FunctionAppWithExprArg.type_attribute = Type.INTEGER;

		if (expression_FunctionAppWithExprArg.arg.type_attribute != Type.INTEGER) {
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken,
					"In FunctionAppWithExprArg, expression type has t");
		}
		return expression_FunctionAppWithExprArg.type_attribute;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		expression_FunctionAppWithIndexArg.type_attribute = Type.INTEGER;
		return expression_FunctionAppWithIndexArg.type_attribute;
	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		expression_PredefinedName.type_attribute = Type.INTEGER;
		return expression_PredefinedName.type_attribute;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		statement_Out.sink.visit(this, arg);
		ASTNode decl = symbolTable.get(statement_Out.name);

		if (decl != null && (((decl.type_attribute == Type.INTEGER || decl.type_attribute == Type.BOOLEAN)
				&& statement_Out.sink.type_attribute == Type.SCREEN)
				|| (decl.type_attribute == Type.IMAGE && (statement_Out.sink.type_attribute == Type.FILE
						|| statement_Out.sink.type_attribute == Type.SCREEN)))) {
			return statement_Out.type_attribute;
		} else {
			if (decl == null) {
				throw new SemanticException(statement_Out.firstToken, "In statement out, decl is null");
			} else {
				throw new SemanticException(statement_Out.firstToken, "In statement out, required codition not met");
			}
		}
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		statement_In.source.visit(this, arg);
		ASTNode decl = symbolTable.get(statement_In.name);

		if (decl == null) {
			throw new SemanticException(statement_In.firstToken, "In statement in, name not declaed before use");
		}

		statement_In.setDec((Declaration) decl);

		if ((decl != null) && (decl.type_attribute == statement_In.source.type_attribute)) {
			return statement_In.type_attribute;
		} else {
			throw new SemanticException(statement_In.firstToken, "In statement in, required condition not met");
		}
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		statement_Assign.lhs.visit(this, arg);
		statement_Assign.e.visit(this, arg);

		statement_Assign.setCartesian(statement_Assign.lhs.isCartesian);

		if (statement_Assign.lhs.type_attribute != statement_Assign.e.type_attribute) {
			throw new SemanticException(statement_Assign.firstToken,
					"In statement assign, LHS and expression should be of same type");
		}
		return statement_Assign.type_attribute;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {

		if (symbolTable.containsKey(lhs.name) == false) {
			throw new SemanticException(lhs.firstToken, lhs.name + " not declared before use in LHS ");
		}

		lhs.declaration = (Declaration) symbolTable.get(lhs.name);
		lhs.type_attribute = lhs.declaration.type_attribute;
		lhs.index.visit(this, arg);
		lhs.isCartesian = lhs.index.isCartesian();
		return lhs.type_attribute;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {

		sink_SCREEN.type_attribute = Type.SCREEN;
		return sink_SCREEN.type_attribute;
	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		if (symbolTable.containsKey(sink_Ident.name) == false) {
			throw new SemanticException(sink_Ident.firstToken,
					sink_Ident.name + " not declared before use in sink_ident ");
		}
		sink_Ident.type_attribute = symbolTable.get(sink_Ident.name).type_attribute;
		if (sink_Ident.type_attribute == Type.FILE) {
			return sink_Ident.type_attribute;
		} else {
			throw new SemanticException(sink_Ident.firstToken, " sink_ident expects a file type");
		}
	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {

		expression_BooleanLit.type_attribute = Type.BOOLEAN;
		return expression_BooleanLit.type_attribute;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident, Object arg) throws Exception {
		if (symbolTable.containsKey(expression_Ident.name) == false) {
			throw new SemanticException(expression_Ident.firstToken,
					expression_Ident.name + " not declared before use in expression_Ident ");
		}
		expression_Ident.type_attribute = symbolTable.get(expression_Ident.name).type_attribute;
		return expression_Ident.type_attribute;
	}
}
