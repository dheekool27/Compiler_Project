package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class SimpleParserTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}



	/**
	 * Simple test case with an empty parse.  This test 
	 * expects an SyntaxException because all legal parses must
	 * have at least an identifier
	 *   
	 * @throws LexicalException
	 * @throws SyntaxException 
	 */
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = "";  //The input is the empty string.  This is not legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //Create a parser
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}

	
	/** Another example.  This is a legal parse and should pass when 
	 * your parser is implemented.
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */

	@Test
	public void testDec1() throws LexicalException, SyntaxException {
		String input = "prog int k;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();
	}
	

	/**
	 * This example invokes the method for expression directly. 
	 * Effectively, we are viewing Expression as the start
	 * symbol of a sub-language.
	 *  
	 * Although a compiler will always call the parse() method,
	 * invoking others is useful to support incremental development.  
	 * We will only invoke expression directly, but 
	 * following this example with others is recommended.  
	 * 
	 * @throws SyntaxException
	 * @throws LexicalException
	 */
	@Test
	public void expression1() throws SyntaxException, LexicalException {
		String input = "Kw_a";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();  //Call expression directly.  
	}
	
	
	@Test
	public void testcase1() throws SyntaxException, LexicalException {
		String input = "2";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}

	
	@Test
	public void testcase2() throws SyntaxException, LexicalException {
		String input = "a bcd";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase3() throws SyntaxException, LexicalException {
		String input = "cart_x cart_y";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase4() throws SyntaxException, LexicalException {
		String input = "prog int 2";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase5() throws SyntaxException, LexicalException {
		String input = "prog image[filepng,png] imageName <- imagepng"; //Error as there is not semicolon for ending the statement
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		//parser.parse();
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase6() throws SyntaxException, LexicalException {
		String input = "imageDeclaration image[\"abcd\"] "; //Should fail for image[""]
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}

	
	@Test
	public void testcase7() throws SyntaxException, LexicalException {
		String input = "prog image[filepng,png] imageName <- imagepng; \n boolean ab=true;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void testcase8() throws SyntaxException, LexicalException {
		String input = "prog image[filepng,jpg] imageName;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void testcase9() throws SyntaxException, LexicalException {
		String input = "prog image imageName;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void testcase10() throws SyntaxException, LexicalException {
		String input = "prog @expr k=12;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase11() throws SyntaxException, LexicalException {
		String input = "prog \"abcded\" boolean a=true;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase12() throws SyntaxException, LexicalException {
		String input = "isBoolean boolean ab=true; boolean cd==true; abcd=true ? return true: return false;"; //Should fail for ==
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	@Test
	public void testcase13() throws SyntaxException, LexicalException {
		String input = "isBoolean boolean ab=true; boolean cd==true; abcd=true ? return true: return false;"; //Should fail for =
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
	}
	
	
	@Test
	public void testcase14() throws SyntaxException, LexicalException {
		String input = "isUrl url filepng=\"abcd\"; \n @expr=12; url awesome=@expr; \n url filepng=abcdefg"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}  
	}
	
	@Test
	public void testcase15() throws SyntaxException, LexicalException {
		String input = "isUrl url filepng=\"abcd\" \n @expr=12; url awesome=@expr; \n url filepng=abcdefg"; //Should fail for ; in line one
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}  
	}
	

	@Test
	public void testcase16() throws SyntaxException, LexicalException {
		String input = "isFile file filepng=\"abcd\"; \n @expr=12; url filepng=@expr; \n url filepng=abcdefg"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}  
	}
	
	@Test
	public void testcase17() throws SyntaxException, LexicalException {
		String input =  "isFile file filepng=\"abcd\" \n @expr=12; url filepng=@expr; \n url filepng=abcdefg";  //Should fail for ; in line one
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}  
	}
	
	@Test
	public void testcase18() throws SyntaxException, LexicalException {
		String input =  "isurl url urlname;";  //Should fail for url as url can only be initalised
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}  
	}
	
	@Test
	public void testcase19() throws SyntaxException, LexicalException {
		String input =  "declaration int xyz;\n boolean zya;\n image imagename;";  
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.parse();  //Parse the parse
	}
	
	@Test
	public void testcase20() throws SyntaxException, LexicalException {
		String input =  "imageProgram image imageName;"
				+ "\n imageName->abcdpng; "
				+ "\n imageName -> SCREEN; "
				+ "\n imageName <- \"awesome\";"
				+ "\n imageName <- @express; \n"
				+ "\n imageName <- abcdpng;";  // Image related Test cases
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.parse();  //Parse the parse
	}
	
	@Test
	public void testcase21() throws SyntaxException, LexicalException { 
		String input =  "assign int abc=123456;\n"
				+ "abc[[x,y]]=123456;\n"
				+ "abc[[r,A]]=123244;\n";//Assignment statement
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.parse();  //Parse the parse
	}


	@Test
	public void testcase22() throws SyntaxException, LexicalException {
		String input =  "assign int abc=123456;\n"
				+ "abc[[x]]=123456;\n"
				+ "abc[[r,A]]=123244;\n";  //Error
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}  
	}
	
	@Test
	public void testcase23() throws SyntaxException, LexicalException {
		String input =  "assign int abc=123456;\n"
				+ "abc[[x,y]]=123456;\n"
				+ "abc[[A]]=123244;\n";//Error
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
		parser.parse();  //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}  
	}
	
	// Function Name Testcases
	@Test
	public void testcase24() throws SyntaxException, LexicalException {
		String input =  " sin \n cos \n atan \n abs \n cart_x \n cart_y \n polar_a \n polar_r\n";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.functionName(); //Parse the parse
	}
	
	//LhsSelector ::= LSQUARE  ( XySelector  | RaSelector  )   RSQUARE
	
	@Test
	public void testcase25() throws SyntaxException, LexicalException {
		String input =  "[x,y] \n [r,A] \n []";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.lhsSelector();  //Parse the parse
	}
	
	@Test
	public void testcase26() throws SyntaxException, LexicalException {
		String input =  "[x,]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.lhsSelector();   //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
		 
	}
	
	@Test
	public void testcase27() throws SyntaxException, LexicalException {
		String input =  "[,A]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.lhsSelector();   //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		} 
		 
	}
	//		XySelector ::= KW_x COMMA KW_y
	@Test
	public void testcase28() throws SyntaxException, LexicalException {
		String input =  "x,y";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.xySelector(); 
	}
	
	@Test
	public void testcase29() throws SyntaxException, LexicalException {
		String input =  "x";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.lhsSelector();   //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	//		RaSelector ::= KW_r COMMA KW_A
	@Test
	public void testcase30() throws SyntaxException, LexicalException {
		String input =  "r,A";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.raSelector(); 
	}
	
	@Test
	public void testcase31() throws SyntaxException, LexicalException {
		String input =  ",A";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.raSelector();   //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	

	@Test
	public void testcase32() throws SyntaxException, LexicalException {
		String input =  "r,";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.raSelector();   //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
//	
	// Expression 
	@Test
	public void testcase33() throws SyntaxException, LexicalException {
		String input =  "x y r a X Y Z A R DEF_X DEF_Y";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();   //Parse the parse
	}
	
	@Test
	public void testcase34() throws SyntaxException, LexicalException {
		String input =  "5+3*4+5%3";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();   //Parse the parse
	}
	
	/**
	 UnaryExpression ::= OP_PLUS UnaryExpression 
             | OP_MINUS UnaryExpression 
             | UnaryExpressionNotPlusMinus
             
	UnaryExpressionNotPlusMinus ::=  OP_EXCL  UnaryExpression  | Primary 
| IdentOrPixelSelectorExpression | KW_x | KW_y | KW_r | KW_a | KW_X | KW_Y | KW_Z | KW_A | KW_R | KW_DEF_X | KW_DEF_Y
	
	Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN | FunctionApplication | BOOLEAN_LITERAL 
	
	IdentOrPixelSelectorExpression::=  IDENTIFIER LSQUARE Selector RSQUARE   | IDENTIFIER
	
	FunctionApplication ::= FunctionName LPAREN Expression RPAREN  | FunctionName  LSQUARE Selector RSQUARE 	
	
	FunctionName ::= KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x | KW_cart_y | KW_polar_a | KW_polar_r
	
	Selector ::=  Expression COMMA Expression  

	 */
	
	@Test
	public void testcase35() throws SyntaxException, LexicalException {
		String input =  "x+y+y+r+X-Y-Z-A+R+DEF_X+DEF_Y+!(5+6+(true+false))\n"
				+ " abcd[(x+tyxzsd),(12+123+45)] \n"
				+ " sin(x+y) cos(x+y) atan(x+y) abs(x+y) cart_x(x+y) cart_y(x+y) polar_a(x+y) polar_r(r+a)";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();   //Parse the parse
	}
	
	
	@Test
	public void testcase36() throws SyntaxException, LexicalException {
		String input =  "r==IdentOrPixelSelectorExpression[5,6]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();   //Parse the parse
	}
		
	@Test
	public void testcase37() throws SyntaxException, LexicalException {
		String input =  "sin(x)+cos(x)-atan(x)+cart_x(x)+cart_y(y)+polar_a(a)+polar_r(a)";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();   //Parse the parse
	}
	
	@Test
	public void testcase38() throws SyntaxException, LexicalException {
		String input =  "sin(x)+cos(x)-atan(x)+cart_x(x)+cart_y(y)+polar_a(a)+polar_r(a)";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();   //Parse the parse
	}
	
	@Test
	public void testcase39() throws SyntaxException, LexicalException {
		String input =  "sin[(x+y),(x+z)]+cos[(x+y),(x+z)]+polar_r[vyz+xx,z+xx]";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression(); 
	}
	
	
	/**
	 OrExpression ::= AndExpression   (  OP_OR  AndExpression)*
	 
	 AndExpression ::= EqExpression ( OP_AND  EqExpression )*

     EqExpression ::= RelExpression  (  (OP_EQ | OP_NEQ )  RelExpression )*
     
     RelExpression ::= AddExpression (  ( OP_LT  | OP_GT |  OP_LE  | OP_GE )   AddExpression)*
     
     AddExpression ::= MultExpression   (  (OP_PLUS | OP_MINUS ) MultExpression )*

     MultExpression := UnaryExpression ( ( OP_TIMES | OP_DIV  | OP_MOD ) UnaryExpression )*
     
      UnaryExpression ::= OP_PLUS UnaryExpression 
             | OP_MINUS UnaryExpression 
             | UnaryExpressionNotPlusMinus
             
	UnaryExpressionNotPlusMinus ::=  OP_EXCL  UnaryExpression  | Primary 
| IdentOrPixelSelectorExpression | KW_x | KW_y | KW_r | KW_a | KW_X | KW_Y | KW_Z | KW_A | KW_R | KW_DEF_X | KW_DEF_Y
	
	Primary ::= INTEGER_LITERAL | LPAREN Expression RPAREN | FunctionApplication | BOOLEAN_LITERAL 
	
	IdentOrPixelSelectorExpression::=  IDENTIFIER LSQUARE Selector RSQUARE   | IDENTIFIER
	
	FunctionApplication ::= FunctionName LPAREN Expression RPAREN  | FunctionName  LSQUARE Selector RSQUARE 	
	
	FunctionName ::= KW_sin | KW_cos | KW_atan | KW_abs | KW_cart_x | KW_cart_y | KW_polar_a | KW_polar_r
	
	Selector ::=  Expression COMMA Expression  
 
	 */
	
	@Test
	public void testcase40() throws SyntaxException, LexicalException {
		String input =  "y*Y";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression(); 
	}
	
	@Test
	public void testcase41() throws SyntaxException, LexicalException {
		String input =  "y*+"; //Should through an error
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();   //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testcase42() throws SyntaxException, LexicalException {
		String input =  "y/(x+Y*Y)"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void testcase43() throws SyntaxException, LexicalException {
		String input =  "y/(x+Y*Y-sin(x+y)*cos(x+y)%2)"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void testcase44() throws SyntaxException, LexicalException {
		String input =  "y/(x+Y*Y-sin(x+y)*cos(x+y)%2)"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void testcase45() throws SyntaxException, LexicalException {
		String input =  "5+5-(x+y)*2%2"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void testcase46() throws SyntaxException, LexicalException {
		String input =  "(5+10)>(6*2+4+sin(x)-atan(y)-x/y/z)"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void testcase47() throws SyntaxException, LexicalException {
		String input =  "(5+10)<(6/2/3/4*2)"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void testcase48() throws SyntaxException, LexicalException {
		String input =  "(6*2/23/4*22*sin(x))<=(abs(6*2*12)+cart_x[x,y]+cart_y[(6/23),(7/23)]+polar_a[6/2/2,2/3/4]+polar_r(z))"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void testcase49() throws SyntaxException, LexicalException {
		String input =  "(6*2/23/4*22*sin(x))<=(abs(6*2*12)+cart_x[x,y]+cart_y[(6/23),(7/23)]+polar_a[6/2/2,2/3/4]+polar_r(z,y,x))";  //Should fail as () can hold only expression
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();   //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testcase50() throws SyntaxException, LexicalException {
		String input =  "(6*2/23/4*22*sin(x))>=(abs(6*2*12)+cart_x[x,y]+cart_y[(6/23),(7/23)]+polar_a[6/2/2,2/3/4]+polar_r(z,y,x))";  //Should fail as () can hold only expression
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();   //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testcase51() throws SyntaxException, LexicalException {
		String input =  "(6*2/23/4*22*sin(x))==(abs(6*2*12)+cart_x[x,y]+cart_y[(6/23),(7/23)]+polar_a[6/2/2,2/3/4]+polar_r(z))";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();   //Parse the parse
	}
	
	@Test
	public void testcase52() throws SyntaxException, LexicalException {
		String input =  "(6*2/23/4*22*sin(x))!=(abs(6*2*12)+cart_x[x,y]+cart_y[(6/23),(7/23)]+polar_a[6/2/2,2/3/4]+polar_r(z))"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();   //Parse the parse
	}
	
	@Test
	public void testcase53() throws SyntaxException, LexicalException {
		String input =  "(6*2/23/4*22*sin(x))&(abs(6*2*12)+cart_x[x,y]+cart_y[(6/23),(7/23)]+polar_a[6/2/2,2/3/4]+polar_r(z))|false"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();   //Parse the parse
	}
	
	@Test
	public void testcase54() throws SyntaxException, LexicalException {
		String input =  "(6*2/23/4*22*sin(x))|(abs(6*2*12)+cart_x[x,y]+cart_y[(6/23),(7/23)]+polar_a[6/2/2,2/3/4]+polar_r(z))&true"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();   //Parse the parse
	}
	

	@Test
	public void testcase55() throws SyntaxException, LexicalException {
		String input =  "(6*2/23/4*22*sin(x))||(abs(6*2*12)+cart_x[x,y]+cart_y[(6/23),(7/23)]+polar_a[6/2/2,2/3/4]+polar_r(z))&true"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();   //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testcase56() throws SyntaxException, LexicalException {
		String input =  "(6*2/23/4*22*sin(x))|(abs(6*2*12)+cart_x[x,y]+cart_y[(6/23),(7/23)]+polar_a[6/2/2,2/3/4]+polar_r(z))&&true"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			parser.expression();   //Parse the parse
		}
		catch (SyntaxException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void testcase57() throws SyntaxException, LexicalException {
		String input =  "x idntifier";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.expression();   //Parse the parse
	}
	
	@Test
	public void testcase58() throws SyntaxException, LexicalException {
		String input =  "prog int abcd=(1000*1000+2);\n int b=(100-2+3);";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		parser.parse();   //Parse the parse
	}
}
