package cop5556fa17;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import static cop5556fa17.Scanner.Kind.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import cop5556fa17.AST.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}
	}

	Scanner scanner;
	Token t;
	public static final HashSet<Kind> unaryExpressionStartSet;
	public static final HashSet<Kind> functionNameSet;

	static {
		unaryExpressionStartSet = new HashSet<Kind>(Arrays.asList(Kind.KW_x, Kind.KW_y, Kind.KW_r, Kind.KW_a, Kind.KW_X,
				Kind.KW_Y, Kind.KW_Z, Kind.KW_A, Kind.KW_R, Kind.KW_DEF_X, Kind.KW_DEF_Y));

		functionNameSet = new HashSet<Kind>(Arrays.asList(Kind.KW_sin, Kind.KW_cos, Kind.KW_atan, Kind.KW_abs,
				Kind.KW_cart_x, Kind.KW_cart_y, Kind.KW_polar_a, Kind.KW_polar_r));
	}

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input. Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	/**
	 * Program ::= IDENTIFIER ( Declaration SEMI | Statement SEMI )*
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		Token firstToken = t;
		ArrayList<ASTNode> decsAndStatements = new ArrayList<ASTNode>();
		match(IDENTIFIER);
		while (t.kind == Kind.KW_int || t.kind == Kind.KW_boolean || t.kind == Kind.KW_image || t.kind == Kind.KW_url
				|| t.kind == Kind.KW_file || t.kind == Kind.IDENTIFIER) {
			if (t.kind == Kind.KW_int || t.kind == Kind.KW_boolean || t.kind == Kind.KW_image || t.kind == Kind.KW_url
					|| t.kind == Kind.KW_file) {
				decsAndStatements.add(declaration());
			} else if (t.kind == Kind.IDENTIFIER) {
				decsAndStatements.add(statement());
			}
			match(SEMI);
		}
		return new Program(firstToken, firstToken, decsAndStatements);
	}

	Declaration declaration() throws SyntaxException {
		Declaration decl_statement = null;
		if (t.kind == Kind.KW_int || t.kind == Kind.KW_boolean) {
			decl_statement = variableDeclaration();
		} else if (t.kind == Kind.KW_image) {
			decl_statement = imageDeclaration();
		} else if (t.kind == Kind.KW_url || t.kind == Kind.KW_file) {
			decl_statement = sourceSinkDeclaration();
		} else {
			String message = "Expected start{Declaration} at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return decl_statement;
	}

	Declaration_Variable variableDeclaration() throws SyntaxException {
		Expression expr = null;
		Token firstToken = t;
		if (t.kind == Kind.KW_int) {
			match(Kind.KW_int);
		} else if (t.kind == Kind.KW_boolean) {
			match(Kind.KW_boolean);
		} else {
			String message = "Expected start{VariableDeclaration} at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		Token name = t;
		match(Kind.IDENTIFIER);
		if (t.kind == Kind.OP_ASSIGN) {
			match(OP_ASSIGN);
			expr = expression();
		}
		return new Declaration_Variable(firstToken, firstToken, name, expr);
	}

	Declaration_Image imageDeclaration() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		Source s = null;
		if (t.kind == Kind.KW_image) {
			match(Kind.KW_image);
		} else {
			String message = "Expected start{ImageDeclaration} at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		if (t.kind == Kind.LSQUARE) {
			match(Kind.LSQUARE);
			e0 = expression();
			match(COMMA);
			e1 = expression();
			match(RSQUARE);
		}
		Token name = t;
		match(IDENTIFIER);
		if (t.kind == OP_LARROW) {
			match(OP_LARROW);
			s = source();
		}
		return new Declaration_Image(firstToken, e0, e1, name, s);
	}

	Declaration_SourceSink sourceSinkDeclaration() throws SyntaxException {
		Token firstToken = t;
		Token type = null;
		if (t.kind == Kind.KW_url || t.kind == Kind.KW_file) {
			type = t;
			sourceSinkType();
		} else {
			String message = "Expected start{SourceSinkDeclaration} at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		Token name = t;
		match(Kind.IDENTIFIER);
		match(Kind.OP_ASSIGN);
		Source s = source();
		return new Declaration_SourceSink(firstToken, type, name, s);
	}

	void sourceSinkType() throws SyntaxException {
		if (t.kind == Kind.KW_url) {
			match(Kind.KW_url);
		} else if (t.kind == Kind.KW_file) {
			match(Kind.KW_file);
		} else {
			String message = "Expected start{SourceSinkType} at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
	}

	Source source() throws SyntaxException {
		Token firstToken = t;
		Source s = null;
		if (t.kind == Kind.STRING_LITERAL) {
			String fileOrUrl = t.getText();
			match(Kind.STRING_LITERAL);
			s = new Source_StringLiteral(firstToken, fileOrUrl);
		} else if (t.kind == Kind.OP_AT) {
			match(Kind.OP_AT);
			Expression paramNum = expression();
			s = new Source_CommandLineParam(firstToken, paramNum);
		} else if (t.kind == Kind.IDENTIFIER) {
			Token name = t;
			match(Kind.IDENTIFIER);
			s = new Source_Ident(firstToken, name);
		} else {
			String message = "Expected start{source} at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return s;
	}

	Statement statement() throws SyntaxException {
		Token firstToken = t;
		Statement s = null;
		match(IDENTIFIER);
		if (t.kind == OP_RARROW) {
			s = imageOutStatement(firstToken);
		} else if (t.kind == OP_LARROW) {
			s = imageInStatement(firstToken);
		} else if (t.kind == Kind.LSQUARE || t.kind == Kind.OP_ASSIGN) {
			s = assignmentStatement(firstToken);
		}
		return s;
	}

	Statement_Assign assignmentStatement(Token firstToken) throws SyntaxException {
		LHS lhs = lhs(firstToken);
		match(OP_ASSIGN);
		Expression e = expression();
		return new Statement_Assign(firstToken, lhs, e);
	}

	LHS lhs(Token firstToken) throws SyntaxException {
		Index index = null;
		if (t.kind == Kind.LSQUARE) {
			match(LSQUARE);
			index = lhsSelector();
			match(RSQUARE);
		}
		return new LHS(firstToken, firstToken, index);
	}

	Index lhsSelector() throws SyntaxException {
		Index index = null;
		match(LSQUARE);
		if (t.kind == KW_x) {
			index = xySelector();
		} else if (t.kind == KW_r) {
			index = raSelector();
		} else {
			String message = "Expected start{XySelector||RaSelector} at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		match(RSQUARE);
		return index;
	}

	Index xySelector() throws SyntaxException {
		Token firstToken1 = t;
		Expression e0 = new Expression_PredefinedName(firstToken1, t.kind);
		match(KW_x);
		match(COMMA);
		Token firstToken2 = t;
		Expression e1 = new Expression_PredefinedName(firstToken2, t.kind);
		match(KW_y);
		return new Index(firstToken1, e0, e1);
	}

	Index raSelector() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = new Expression_PredefinedName(firstToken, t.kind);
		match(KW_r);
		match(COMMA);
		Expression e1 = new Expression_PredefinedName(firstToken, t.kind);
		match(KW_A);
		return new Index(firstToken, e0, e1);
	}

	Statement_In imageInStatement(Token firstToken) throws SyntaxException {
		match(OP_LARROW);
		Source source = source();
		return new Statement_In(firstToken, firstToken, source);
	}

	Statement_Out imageOutStatement(Token firstToken) throws SyntaxException {
		match(OP_RARROW);
		Sink sink = sink();
		return new Statement_Out(firstToken, firstToken, sink);
	}

	Sink sink() throws SyntaxException {
		Token firstToken = t;
		Sink sink = null;
		if (t.kind == IDENTIFIER) {
			Token name = t;
			sink = new Sink_Ident(firstToken, name);
			match(IDENTIFIER);
		} else if (t.kind == Kind.KW_SCREEN) {
			match(KW_SCREEN);
			sink = new Sink_SCREEN(firstToken);
		} else {
			throw new SyntaxException(t, "Expected start{sink} but found:" + t.kind);
		}
		return sink;
	}

	void match(Kind kind) throws SyntaxException {
		if (kind == t.kind) {
			consume();
		} else {
			throw new SyntaxException(t, "Expected:" + kind + "but found:" + t.kind);
		}
	}

	void consume() throws SyntaxException {
		System.out.println("Consumed the token:" + t);
		t = scanner.nextToken();
	}

	/**
	 * Expression ::= OrExpression OP_Q Expression OP_COLON Expression |
	 * OrExpression
	 * 
	 * Our test cases may invoke this routine directly to support incremental
	 * development.
	 * 
	 * @throws SyntaxException
	 */
	Expression expression() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = orExpression();
		if (t.kind == OP_Q) {
			match(OP_Q);
			Expression trueExpression = expression();
			match(OP_COLON);
			Expression falseExpression = expression();
			e0 = new Expression_Conditional(firstToken, e0, trueExpression, falseExpression);
		}
		return e0;
	}

	Expression orExpression() throws SyntaxException {
		Token firstToken = t;
		Expression eb0 = andExpression();
		while (t.kind == Kind.OP_OR) {
			Token op = t;
			match(OP_OR);
			Expression eb1 = andExpression();
			eb0 = new Expression_Binary(firstToken, eb0, op, eb1);
		}
		return eb0;
	}

	Expression andExpression() throws SyntaxException {
		Token firstToken = t;
		Expression eb0 = eqExpression();
		while (t.kind == Kind.OP_AND) {
			Token op = t;
			match(OP_AND);
			Expression eb1 = eqExpression();
			eb0 = new Expression_Binary(firstToken, eb0, op, eb1);
		}
		return eb0;
	}

	Expression eqExpression() throws SyntaxException {
		Token firstToken = t;
		Expression eb0 = relExpression();
		while (t.kind == Kind.OP_EQ || t.kind == Kind.OP_NEQ) {
			Token op = t;
			if (t.kind == Kind.OP_EQ) {

				match(OP_EQ);
			} else if (t.kind == Kind.OP_NEQ) {
				match(OP_NEQ);
			}
			Expression eb1 = relExpression();
			eb0 = new Expression_Binary(firstToken, eb0, op, eb1);
		}
		return eb0;
	}

	Expression relExpression() throws SyntaxException {
		Token firstToken = t;
		Expression eb0 = addExpression();
		while (t.kind == Kind.OP_LT || t.kind == Kind.OP_GT || t.kind == Kind.OP_LE || t.kind == Kind.OP_GE) {
			Token op = t;
			if (t.kind == Kind.OP_LT) {
				match(OP_LT);
			} else if (t.kind == Kind.OP_GT) {
				match(OP_GT);
			} else if (t.kind == Kind.OP_LE) {
				match(OP_LE);
			} else if (t.kind == Kind.OP_GE) {
				match(OP_GE);
			}
			Expression eb1 = addExpression();
			eb0 = new Expression_Binary(firstToken, eb0, op, eb1);
		}
		return eb0;
	}

	Expression addExpression() throws SyntaxException {
		Token firstToken = t;
		Expression eb0 = multExpression();
		while (t.kind == Kind.OP_PLUS || t.kind == Kind.OP_MINUS) {
			Token op = t;
			if (t.kind == Kind.OP_PLUS) {
				match(OP_PLUS);
			} else if (t.kind == Kind.OP_MINUS) {
				match(OP_MINUS);
			}
			Expression eb1 = multExpression();
			eb0 = new Expression_Binary(firstToken, eb0, op, eb1);
		}
		return eb0;
	}

	Expression multExpression() throws SyntaxException {
		Token firstToken = t;

		Expression eu0 = unaryExpression();
		Expression e0 = eu0;
		while (t.kind == Kind.OP_TIMES || t.kind == Kind.OP_DIV || t.kind == Kind.OP_MOD) {
			Token op = t;
			if (t.kind == Kind.OP_TIMES) {
				match(OP_TIMES);
			} else if (t.kind == Kind.OP_DIV) {
				match(OP_DIV);
			} else if (t.kind == Kind.OP_MOD) {
				match(OP_MOD);
			}
			Expression eu1 = unaryExpression();
			e0 = new Expression_Binary(firstToken, e0, op, eu1);
		}
		return e0;
	}

	Expression unaryExpression() throws SyntaxException {
		Token firstToken = t;
		if (t.kind == Kind.OP_PLUS) {
			Token op = t;
			match(OP_PLUS);
			Expression e = unaryExpression();
			return new Expression_Unary(firstToken, op, e);

		} else if (t.kind == Kind.OP_MINUS) {
			Token op = t;
			match(OP_MINUS);
			Expression e = unaryExpression();
			return new Expression_Unary(firstToken, op, e);
		} else {
			return unaryExpressionNotPlusMinus();
		}
	}

	Expression unaryExpressionNotPlusMinus() throws SyntaxException {
		Token firstToken = t;
		if (unaryExpressionStartSet.contains(t.kind)) {
			Kind kind = t.kind;
			match(t.kind);
			return new Expression_PredefinedName(firstToken, kind);
		} else if (t.kind == Kind.OP_EXCL) {
			Token op = t;
			match(OP_EXCL);
			Expression e = unaryExpression();
			return new Expression_Unary(firstToken, op, e);
		} else if (t.kind == Kind.IDENTIFIER) {
			return identOrPixelSelectorExpression();
		} else {
			return primary();
		}
	}

	Expression primary() throws SyntaxException {
		Token firstToken = t;
		if (t.kind == INTEGER_LITERAL) {
			int value = t.intVal();
			match(Kind.INTEGER_LITERAL);
			return new Expression_IntLit(firstToken, value);
		} else if (t.kind == BOOLEAN_LITERAL) {
			String textValue = t.getText();
			boolean value = textValue.equals("true") ? true : false;
			match(Kind.BOOLEAN_LITERAL);
			return new Expression_BooleanLit(firstToken, value);
		} else if (t.kind == LPAREN) {
			match(LPAREN);
			Expression e = expression();
			match(RPAREN);
			return e;
		} else {
			return functionApplication();
		}
	}

	Expression functionApplication() throws SyntaxException {
		Token firstToken = t;
		Kind function = functionName();
		if (t.kind == LPAREN) {
			match(LPAREN);
			Expression arg = expression();
			match(RPAREN);
			return new Expression_FunctionAppWithExprArg(firstToken, function, arg);
		} else if (t.kind == LSQUARE) {
			match(LSQUARE);
			Index arg = selector();
			match(RSQUARE);
			return new Expression_FunctionAppWithIndexArg(firstToken, function, arg);
		} else {
			throw new SyntaxException(t, "LPAREN or LPARAM expected but found:" + t);
		}
	}

	Kind functionName() throws SyntaxException {
		if (functionNameSet.contains(t.kind)) {
			Kind kind = t.kind;
			match(t.kind);
			return kind;
		} else {
			throw new SyntaxException(t, "functionName expected but found:" + t);
		}
	}

	Expression identOrPixelSelectorExpression() throws SyntaxException {
		Index index = null;
		Token firstToken = t;
		Token ident = t;
		match(IDENTIFIER);
		if (t.kind == LSQUARE) {
			match(Kind.LSQUARE);
			index = selector();
			match(Kind.RSQUARE);
			return new Expression_PixelSelector(firstToken, firstToken, index);
		}
		return new Expression_Ident(firstToken, ident);
	}

	Index selector() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = expression();
		match(COMMA);
		Expression e1 = expression();
		return new Index(firstToken, e0, e1);
	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to
	 * get nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message = "Expected EOL at " + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}
}
