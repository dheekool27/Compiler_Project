package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.AST.*;

import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class ParserTest {

	// set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 * Simple test case with an empty program. This test expects an exception
	 * because all legal programs must have at least an identifier
	 * 
	 * @throws LexicalException
	 * @throws SyntaxException
	 */
	@Test
	public void testEmpty1() throws LexicalException, SyntaxException {
		String input = ""; // The input is the empty string. Parsing should fail
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the tokens
		Parser parser = new Parser(scanner); //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast = parser.parse();; //Parse the program, which should throw an exception
		} catch (SyntaxException e) {
			show(e);  //catch the exception and show it
			throw e;  //rethrow for Junit
		}
	}


	@Test
	public void testNameOnly1() throws LexicalException, SyntaxException {
		String input = "prog";  //Legal program with only a name
		show(input);            //display input
		Scanner scanner = new Scanner(input).scan();   //Create scanner and create token list
		show(scanner);    //display the tokens
		Parser parser = new Parser(scanner);   //create parser
		Program ast = parser.parse();          //parse program and get AST
		show(ast);                             //Display the AST
		assertEquals(ast.name, "prog");        //Check the name field in the Program object
		assertTrue(ast.decsAndStatements.isEmpty());   //Check the decsAndStatements list in the Program object.  It should be empty.
	}

	@Test
	public void testDec11() throws LexicalException, SyntaxException {
		String input = "prog int k;";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);  
		assertEquals(KW_int, dec.type.kind);
		assertEquals("k", dec.name);
		assertNull(dec.e);
	}
	
	@Test
	public void testDec3() throws LexicalException, SyntaxException {
		String input = "program int prad = true | false;";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "program"); 
		//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);  
		assertEquals(KW_int, dec.type.kind);
		assertEquals("prad", dec.name);
		Expression_Binary e = (Expression_Binary)dec.e;
		Expression_BooleanLit e0 = (Expression_BooleanLit)e.e0;
		assertEquals(true, e0.value);
		assertEquals(OP_OR, e.op);
		Expression_BooleanLit e1 = (Expression_BooleanLit)e.e1;
		assertEquals(false, e1.value);
	}

	
	@Test
	public void testDec4() throws LexicalException, SyntaxException {
		String input = "program image [ abc>=cde , jkll>aeiou] ace;"
						+"image [ abc>=cde , jkll>aeiou] ace <- \"abced\";"
				        +"url pradosa = \"source\";"
						+"file pradosa = @(atan(!p));"
				        +"boolean  _$prad = ggjhj&(!hghg);";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "program"); 
//		//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
		Declaration_Image dec = (Declaration_Image) ast.decsAndStatements
				.get(0);  
		Expression e0 = (Expression)dec.xSize;
		Expression_Binary eb= (Expression_Binary) e0;
		Expression_Ident ei0 = (Expression_Ident) eb.e0;
		Expression_Ident ei1 = (Expression_Ident) eb.e1;
		assertEquals("abc", ei0.name);
		assertEquals(OP_GE, eb.op);
		assertEquals("cde", ei1.name);
		Expression e1 = (Expression)dec.ySize;
		Expression_Binary eb0= (Expression_Binary) e1;
		Expression_Ident ei2 = (Expression_Ident) eb0.e0;
		Expression_Ident ei3 = (Expression_Ident) eb0.e1;
		assertEquals("jkll", ei2.name);
		assertEquals(OP_GT, eb0.op);
		assertEquals("aeiou", ei3.name);
		assertEquals("ace", dec.name);
		assertNull(dec.source);
		Declaration_Image dec1 = (Declaration_Image) ast.decsAndStatements
				.get(1);
		
		Expression e2 = (Expression)dec.xSize;
		Expression_Binary eb2= (Expression_Binary) e2;
		Expression_Ident ei4 = (Expression_Ident) eb2.e0;
		Expression_Ident ei5 = (Expression_Ident) eb2.e1;
		assertEquals("abc", ei4.name);
		assertEquals(OP_GE, eb.op);
		assertEquals("cde", ei5.name);
		Expression e3 = (Expression)dec.ySize;
		Expression_Binary eb3= (Expression_Binary) e3;
		Expression_Ident ei6 = (Expression_Ident) eb3.e0;
		Expression_Ident ei7 = (Expression_Ident) eb3.e1;
		assertEquals("jkll", ei6.name);
		assertEquals(OP_GT, eb3.op);
		assertEquals("aeiou", ei7.name);
		assertEquals("ace", dec1.name);
		Source_StringLiteral sl= (Source_StringLiteral) dec1.source;
		assertEquals("abced", sl.fileOrUrl);
		Declaration_SourceSink dec2 = (Declaration_SourceSink) ast.decsAndStatements
				.get(2);
		assertEquals("pradosa", dec2.name);
		assertEquals(KW_url, dec2.type);
		Source_StringLiteral s2= (Source_StringLiteral) dec2.source;
		assertEquals("source", s2.fileOrUrl);
		Declaration_SourceSink dec3 = (Declaration_SourceSink) ast.decsAndStatements
				.get(3);
		assertEquals("pradosa", dec3.name);
		assertEquals(KW_file, dec3.type);
		Source_CommandLineParam s3= (Source_CommandLineParam) dec3.source;
		Expression_FunctionAppWithExprArg ef= (Expression_FunctionAppWithExprArg) s3.paramNum;
		assertEquals(KW_atan, ef.function);
		Expression_Unary eu= (Expression_Unary) ef.arg;
		assertEquals(OP_EXCL, eu.op);
		Expression_Ident ei= (Expression_Ident) eu.e;
		assertEquals("p", ei.name);
		Declaration_Variable dec4 = (Declaration_Variable) ast.decsAndStatements
				.get(4);
		assertEquals("_$prad", dec4.name);
		Expression_Binary eb4 = (Expression_Binary)dec4.e;
		Expression_Ident eid0 = (Expression_Ident)eb4.e0;
		Expression_Unary eid1 = (Expression_Unary)eb4.e1;
		assertEquals("ggjhj",eid0.name);
		assertEquals(OP_AND, eb4.op);
		assertEquals(OP_EXCL, eid1.op);
		Expression_Ident  eid2= (Expression_Ident)eid1.e;
		assertEquals("hghg", eid2.name);
	}
	@Test
	public void exp1() throws SyntaxException, LexicalException {
	String input = "Z-old";
	Expression e = (new Parser(new Scanner(input).scan())).expression();
	show(e);
	assertEquals(Expression_Binary.class, e.getClass());
	Expression_Binary ebin = (Expression_Binary)e;
	assertEquals(Expression_PredefinedName.class, ebin.e0.getClass());
	assertEquals(KW_Z, ((Expression_PredefinedName)(ebin.e0)).kind);
	assertEquals(Expression_Ident.class, ebin.e1.getClass());
	assertEquals("old", ((Expression_Ident)(ebin.e1)).name);
	assertEquals(OP_MINUS, ebin.op);
	}
	
	
	@Test
	public void exp2() throws SyntaxException, LexicalException {
	String input = "2";
	Expression e = (new Parser(new Scanner(input).scan())).expression();
	show(e);
	assertEquals(Expression_IntLit.class, e.getClass());
	Expression_IntLit e0 = (Expression_IntLit)e;
	assertEquals(2, e0.value);
	}

	
	@Test
	public void exp3() throws SyntaxException, LexicalException {
	String input = "pradosa int b";
	Expression e = (new Parser(new Scanner(input).scan())).expression();
	show(e);
	assertEquals(Expression_Ident.class, e.getClass());
	Expression_Ident e0 = (Expression_Ident)e;
	assertEquals("pradosa", e0.name);
	}

	@Test
	public void exp4() throws SyntaxException, LexicalException {
	String input = "pradosa int b;";
	show(input);
	Scanner scanner = new Scanner(input).scan(); 
	show(scanner); 
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals(ast.name, "pradosa"); 
//	//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
	Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
			.get(0);  
	assertEquals(KW_int, dec.type.kind);
	assertEquals("b", dec.name);
	assertNull(dec.e);
	
	}

	
	@Test
	public void exp5() throws SyntaxException, LexicalException {
	String input = "shobhit image pratham;";
	show(input);
	Scanner scanner = new Scanner(input).scan(); 
	show(scanner); 
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals(ast.name, "shobhit"); 
//	//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
	Declaration_Image dec = (Declaration_Image) ast.decsAndStatements
			.get(0);  
	assertNull(dec.xSize);
	assertNull(dec.ySize);
	assertEquals("pratham", dec.name);
	assertNull(dec.source);
//	
	}

	
	@Test
	public void exp6() throws SyntaxException, LexicalException {
	String input = "prog image [m+p,b+y] jh;";
	show(input);
	Scanner scanner = new Scanner(input).scan(); 
	show(scanner); 
	Parser parser = new Parser(scanner);
	Program ast = parser.parse();
	show(ast);
	assertEquals(ast.name, "prog"); 
//	//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
	Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);  
	Expression_Binary ex = (Expression_Binary) dec.xSize;
	Expression_Ident eix0 = (Expression_Ident) ex.e0;
	assertEquals("m", eix0.name);
	assertEquals(OP_PLUS, ex.op);
	Expression_Ident eix1 = (Expression_Ident) ex.e1;
	assertEquals("p", eix1.name);
	Expression_Binary ey = (Expression_Binary) dec.ySize;
	Expression_Ident eiy0 = (Expression_Ident) ey.e0;
	assertEquals("b", eiy0.name);
	assertEquals(OP_PLUS, ey.op);
	Expression_PredefinedName eiy1 = (Expression_PredefinedName) ey.e1;
	assertEquals(KW_y, eiy1.kind);
	assertEquals("jh", dec.name);
	assertNull(dec.source);
//	
	}
	
	@Test
	public void testEmpty() throws LexicalException, SyntaxException {
		String input = ""; // The input is the empty string. Parsing should fail
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the tokens
		Parser parser = new Parser(scanner); //Create a parser
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast = parser.parse(); //Parse the program, which should throw an exception
		} catch (SyntaxException e) {
			show(e);  //catch the exception and show it
			throw e;  //rethrow for Junit
		}
	}


	@Test
	public void testNameOnly() throws LexicalException, SyntaxException {
		String input = "prog";  //Legal program with only a name
		show(input);            //display input
		Scanner scanner = new Scanner(input).scan();   //Create scanner and create token list
		show(scanner);    //display the tokens
		Parser parser = new Parser(scanner);   //create parser
		Program ast = parser.parse();          //parse program and get AST
		show(ast);                             //Display the AST
		assertEquals(ast.name, "prog");        //Check the name field in the Program object
		assertTrue(ast.decsAndStatements.isEmpty());   //Check the decsAndStatements list in the Program object.  It should be empty.
	}

	@Test
	public void testDec1() throws LexicalException, SyntaxException {
		String input = "prog int k;";
		show(input);
		Scanner scanner = new Scanner(input).scan(); 
		show(scanner); 
		Parser parser = new Parser(scanner);
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);  
		assertEquals(KW_int, dec.type.kind);
		assertEquals("k", dec.name);
		assertNull(dec.e);
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
			parser.program();  //Parse the program
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
			parser.program();  //Parse the program
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
			parser.program();  //Parse the program
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
			parser.program();  //Parse the program
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
		//Parser.program();
		thrown.expect(SyntaxException.class);
		try {
			parser.program();  //Parse the program
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
			parser.program();  //Parse the program
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
		Program ast=parser.program();
		show(ast);
		assertEquals("prog",ast.name);
		// First Declaration statement
		Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);  
		assertEquals(KW_image, dec.firstToken.kind);
		assertEquals("imageName", dec.name);
		Expression_Ident ei=(Expression_Ident)dec.xSize;
		assertEquals("filepng",ei.name);
		ei=(Expression_Ident)dec.ySize;
		assertEquals("png",ei.name);
		Source_Ident s=(Source_Ident) dec.source;
	    assertEquals("imagepng",s.name);
		// Second Declaration statement
	    Declaration_Variable dec2 = (Declaration_Variable) ast.decsAndStatements.get(1);  
		assertEquals("ab", dec2.name);
		assertEquals(KW_boolean, dec2.firstToken.kind);
		Expression_BooleanLit ebi=(Expression_BooleanLit)dec2.e;
		assertEquals(true,ebi.value);		
	}
	
	@Test
	public void testcase8() throws SyntaxException, LexicalException {
		String input = "prog image[filepng,jpg] imageName;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		Program ast=parser.program();
		show(ast);
		assertEquals("prog",ast.name);
		Declaration_Image dec1 = (Declaration_Image) ast.decsAndStatements.get(0); 
		assertEquals(dec1.name,"imageName");
		Expression_Ident exi=(Expression_Ident)dec1.xSize;
		Expression_Ident eyi=(Expression_Ident)dec1.ySize;
		assertEquals(exi.name,"filepng");
		assertEquals(eyi.name,"jpg");
		assertNull(dec1.source);
	}
	
	@Test
	public void testcase10() throws SyntaxException, LexicalException {
		String input = "prog @expr k=12;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		Program ast=parser.program();  //Parse the program
		show(ast);
		assertEquals(ast.name,"prog");
		assertEquals(ast.decsAndStatements.size(),0);
	}
	
	@Test
	public void testcase10parse() throws SyntaxException, LexicalException {
		String input = "prog @expr k=12;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast=parser.parse();  //Parse the program
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
		Program ast=parser.program();  //Parse the program
		show(ast);
		assertEquals(ast.name,"prog");
		assertEquals(ast.decsAndStatements.size(),0);
	}
	
	@Test
	public void testcase11_parse() throws SyntaxException, LexicalException {
		String input = "prog \"abcded\" boolean a=true;"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast=parser.parse();  //Parse the program
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
			Program ast=parser.program();  //Parse the program
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
			Program ast=parser.program();  //Parse the program
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
		Program ast=parser.program();  //Parse the program
		show(ast);
		assertEquals(ast.name,"isUrl");
		assertEquals(ast.decsAndStatements.size(),1);
		Declaration_SourceSink dss=(Declaration_SourceSink)ast.decsAndStatements.get(0);
		assertEquals(dss.name,"filepng");
		assertEquals(dss.type,KW_url);
		Source_StringLiteral s=(Source_StringLiteral)dss.source;
		assertEquals(s.fileOrUrl,"abcd");
	}
	
	@Test
	public void testcase14_parse() throws SyntaxException, LexicalException {
		String input = "isUrl url filepng=\"abcd\"; \n @expr=12; url awesome=@expr; \n url filepng=abcdefg"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast=parser.parse();  //Parse the program
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
			Program ast=parser.program();  //Parse the program
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
		Program ast=parser.program();  //Parse the program
		show(ast);
		assertEquals(ast.name,"isFile");
		assertEquals(ast.decsAndStatements.size(),1);
		assertEquals(ast.firstToken.kind,IDENTIFIER);
		
		// Declaration Statements
		Declaration_SourceSink ds=(Declaration_SourceSink)ast.decsAndStatements.get(0);
		assertEquals(ds.type,KW_file);
		assertEquals(ds.name,"filepng");
		Source_StringLiteral s=(Source_StringLiteral)ds.source;
		assertEquals(s.fileOrUrl,"abcd");
		//assertEquals(ast.)
	}
	
	@Test
	public void testcase16_parse() throws SyntaxException, LexicalException {
		String input = "isFile file filepng=\"abcd\"; \n @expr=12; url filepng=@expr; \n url filepng=abcdefg"; 
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);
		thrown.expect(SyntaxException.class);
		try {
			ASTNode ast=parser.parse();  //Parse the program
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
			Program ast=parser.program();  //Parse the program
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
			Program ast=parser.program();  //Parse the program
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
		Program ast=parser.program();  //Parse the program
		show(ast);
		assertEquals(ast.name,"declaration");
		assertEquals(ast.firstToken.kind,IDENTIFIER);
		
		//Declaration statements start
		Declaration_Variable dv1=(Declaration_Variable)ast.decsAndStatements.get(0);
		assertEquals(dv1.name,"xyz");
		assertEquals(dv1.type.kind,KW_int);
		assertNull(dv1.e);
		
		Declaration_Variable dv2=(Declaration_Variable)ast.decsAndStatements.get(1);
		assertEquals(dv2.name,"zya");
		assertEquals(dv2.type.kind,KW_boolean);
		assertNull(dv2.e);
		
		Declaration_Image dv3=(Declaration_Image)ast.decsAndStatements.get(2);	
		assertEquals(dv3.name,"imagename");
		assertNull(dv3.source);
		assertNull(dv3.xSize);
		assertNull(dv3.ySize);
		
		//Declaration statement end
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
		Program ast=parser.program();  //Parse the program
		show(ast);
		assertEquals(ast.name,"imageProgram");
		
		//Declaration statement start
		Declaration_Image dv1=(Declaration_Image)ast.decsAndStatements.get(0);
		assertEquals(dv1.name,"imageName");
		assertNull(dv1.xSize);
		assertNull(dv1.ySize);
		assertNull(dv1.source);
		
		Statement_Out dv2=(Statement_Out)ast.decsAndStatements.get(1);
		assertEquals(dv2.name,"imageName");
		Sink_Ident si2=(Sink_Ident)dv2.sink;
		assertEquals(si2.name,"abcdpng");
	}
	
	@Test
	public void testprog2() throws LexicalException, SyntaxException {
		String input = "p image k;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "p"); 
		//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
		Declaration_Image dec = (Declaration_Image) ast.decsAndStatements
				.get(0);  
		//assertEquals(KW_image, dec.type.kind);
		assertNull(dec.xSize);
		assertNull(dec.ySize);
		assertEquals("k", dec.name);
		assertNull(dec.source);
	}
	
	@Test
	public void testprog7() throws LexicalException, SyntaxException {
		String input = "prog int k=5;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  
		Program ast = parser.parse();
		show(ast);
		assertEquals(ast.name, "prog"); 
		//This should have one Declaration_Variable object, which is at position 0 in the decsAndStatements list
		Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements
				.get(0);  
		Expression_IntLit ex = (Expression_IntLit)dec.e;
		assertEquals(KW_int, dec.type.kind);
		
		assertEquals("k", dec.name);
		assertEquals(5,ex.value);
	}
		
	
	@Test
	public void expression88() throws SyntaxException, LexicalException {
		String input = "x|Z";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		Expression ast =parser.expression();  //Call expression directly. 
		show(ast);
		Expression_Binary dex = (Expression_Binary)ast;
		Expression_PredefinedName ex = (Expression_PredefinedName)dex.e0;
		Expression_PredefinedName ex2 = (Expression_PredefinedName)dex.e1;
		assertEquals(KW_x, ex.kind);
		assertEquals(OP_OR, dex.op);
		assertEquals(KW_Z, ex2.kind);
		
	}
	
	@Test
	public void testprog57() throws LexicalException, SyntaxException {
		String input = "a * a ?2:3";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		Expression ast =parser.expression();
		show(ast);
		Expression_Conditional dex = (Expression_Conditional)ast;
		Expression_Binary dex1 = (Expression_Binary)dex.condition;
		Expression_PredefinedName ex = (Expression_PredefinedName)dex1.e0;
		Expression_PredefinedName ex1 = (Expression_PredefinedName)dex1.e1;
		Expression_IntLit ex2 = (Expression_IntLit)dex.trueExpression;
		Expression_IntLit ex3 = (Expression_IntLit)dex.falseExpression;
		assertEquals(KW_a, ex.kind);
		assertEquals(OP_TIMES, dex1.op);
		assertEquals(KW_a, ex1.kind);
		assertEquals(2, ex2.value);
		assertEquals(3, ex3.value);
	}
	
	@Test
    public void testCase1() throws SyntaxException, LexicalException {
        String input = "class int testVar = x;";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);   
        Program ast = parser.parse();
        show(ast);
        assertEquals(ast.name, "class");
        Declaration_Variable dec = (Declaration_Variable) ast.decsAndStatements.get(0);
        assertEquals(KW_int, dec.type.kind);
        assertEquals("testVar", dec.name);
        assertEquals(dec.e.getClass(), Expression_PredefinedName.class);
        assertEquals(KW_x, ((Expression_PredefinedName)(dec.e)).kind);
    }
 
@Test
    public void testCase2() throws SyntaxException, LexicalException {
        String input = "type image[true,123] value;";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);   
        Program ast = parser.parse();
        show(ast);
        assertEquals(ast.name, "type");
        Declaration_Image dec = (Declaration_Image) ast.decsAndStatements.get(0);
        assertEquals(dec.getClass(), Declaration_Image.class);
        assertEquals(KW_image, dec.firstToken.kind);
        assertEquals(dec.xSize.getClass(), Expression_BooleanLit.class);
        assertEquals(((Expression_IntLit)(dec.ySize)).value, 123);
        assertEquals(dec.name, "value");
        assertEquals(dec.source, null);
    }
@Test
    public void testCase3() throws SyntaxException, LexicalException {
    String input = "boolean boolVar = *";
    show(input);
    Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
    show(scanner);   //Display the Scanner
    Parser parser = new Parser(scanner);   
    thrown.expect(SyntaxException.class);
    try {
        Program ast = parser.parse();
    }
    catch (SyntaxException e) {
        show(e);
        throw e;
    }
    }
@Test
    public void testCase4() throws SyntaxException, LexicalException {
    String input = "boolean  vvvvv  = hhhh";
    show(input);
    Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
    show(scanner);   //Display the Scanner   
    Declaration_Variable dec = (new Parser(new Scanner(input).scan())).variableDeclaration();
    show(dec);
    assertEquals(dec.type.kind, KW_boolean);
    assertEquals(dec.name, "vvvvv");
    assertEquals(dec.e.getClass(), Expression_Ident.class);
    assertEquals(((Expression_Ident)(dec.e)).name, "hhhh");
    }
@Test
    public void testCase6() throws SyntaxException, LexicalException {
        String input = "url wrongUrl  = /";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);   
        thrown.expect(SyntaxException.class);
        try {
            parser.sourceSinkDeclaration();   //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }
@Test
    public void testCase7() throws SyntaxException, LexicalException {
        String input = "file testFile = \"xyz\"";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);
        Declaration_SourceSink dec = parser.sourceSinkDeclaration();   //Parse the program
        show(dec);
        assertEquals(dec.type, KW_file);
        assertEquals(dec.name, "testFile");
        assertEquals(dec.source.getClass(), Source_StringLiteral.class);
        Source_StringLiteral src = (Source_StringLiteral)dec.source;
        assertEquals(src.fileOrUrl, "xyz");
    }
@Test
    public void testCase8() throws SyntaxException, LexicalException {
    String input = "image [img]";
    show(input);
    Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
    show(scanner);   //Display the Scanner
    Parser parser = new Parser(scanner);   
    thrown.expect(SyntaxException.class);
    try {
        parser.imageDeclaration();   //Parse the program
    }
    catch (SyntaxException e) {
        show(e);
        throw e;
    }
    }
@Test
    public void testCase11() throws SyntaxException, LexicalException {
        String input = "image i <- \"dd\"";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);
        Declaration_Image dec = parser.imageDeclaration();   //Parse the program
        show(dec);
        assertEquals(dec.getClass(), Declaration_Image.class);
        assertEquals(dec.name, "i");
        assertEquals(dec.source.getClass(), Source_StringLiteral.class);
        assertEquals(((Source_StringLiteral)(dec.source)).fileOrUrl, "dd");   
    }
@Test
    public void testCase12() throws SyntaxException, LexicalException {
        String input = "statement = true ? z; f";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);  
        thrown.expect(SyntaxException.class);
        try {
            parser.statement();   //Parse the program
        }
        catch (SyntaxException e) {
            show(e);
            throw e;
        }
    }
@Test
    public void testCase13() throws SyntaxException, LexicalException {
        String input = "statement = true ? z: f";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);
        Statement st = parser.statement();   //Parse the program
        show(st);
        assertEquals(st.getClass(), Statement_Assign.class);
        Statement_Assign st_assign = (Statement_Assign)st;
        assertEquals(((LHS)(st_assign.lhs)).name, "statement");
        Expression_Conditional st_assign_exp = (Expression_Conditional)st_assign.e;
        assertEquals(st_assign_exp.condition.getClass(), Expression_BooleanLit.class);
        assertEquals(((Expression_BooleanLit)(((Expression_Conditional)(st_assign_exp)).condition)).value,true);
        assertEquals(((Expression_Ident)(st_assign_exp.trueExpression)).name, "z");
        assertEquals(((Expression_Ident)(st_assign_exp.falseExpression)).name, "f");
    }
@Test
    public void testCase15() throws SyntaxException, LexicalException {
        String input = "prog img -> SCREEN;";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);
        Program ast = parser.parse();
        show(ast);
        assertEquals(ast.name, "prog");
        Statement_Out st = (Statement_Out)ast.decsAndStatements.get(0);
        assertEquals(st.name, "img");
        assertEquals(st.sink.getClass(), Sink_SCREEN.class);
        assertEquals(((Sink_SCREEN)(st.sink)).kind, KW_SCREEN);
    }
@Test
    public void testCase16() throws SyntaxException, LexicalException {
        String input = "sin(a+b)];";
        show(input);
        Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
        show(scanner);   //Display the Scanner
        Parser parser = new Parser(scanner);
        Expression exp = parser.expression();   //Parse the program
        show(exp);
        assertEquals(exp.getClass(), Expression_FunctionAppWithExprArg.class);
        Expression_FunctionAppWithExprArg exp_fn = (Expression_FunctionAppWithExprArg)exp;
        assertEquals(exp_fn.function, KW_sin);
        assertEquals(exp_fn.arg.getClass(), Expression_Binary.class);
        Expression_Binary exp_fn_arg = (Expression_Binary)exp_fn.arg;
        assertEquals(((Expression_PredefinedName)exp_fn_arg.e0).kind, KW_a);
        assertEquals(exp_fn_arg.op, OP_PLUS);
        assertEquals(((Expression_Ident)exp_fn_arg.e1).name, "b" );
    }
@Test
	public void testCase17() throws SyntaxException, LexicalException {
	String input = "myprog int def;";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	        Parser parser = new Parser(scanner);  
	Program ast = parser.parse();  //Parse the program 
	show(ast);
	assertEquals(ast.name, "myprog");
	Declaration_Variable dec = (Declaration_Variable)ast.decsAndStatements.get(0);
	assertEquals(dec.type.kind, KW_int);
	assertEquals(dec.name, "def");
	assertEquals(dec.e, null);
	}
	@Test
	public void testCase18() throws SyntaxException, LexicalException {
	String input = "+ !x*y";
	show(input);
	Scanner scanner = new Scanner(input).scan();
	        Parser parser = new Parser(scanner);  
	Expression exp = parser.expression();  //Parse the program 
	show(exp);
	assertEquals(exp.getClass(), Expression_Binary.class);
	Expression_Binary expbin = (Expression_Binary)exp;
	assertEquals(expbin.e0.getClass(), Expression_Unary.class);
	assertEquals(((Expression_Unary)expbin.e0).op, OP_PLUS);
	assertEquals(((Expression_Unary)expbin.e0).e.getClass(), Expression_Unary.class);
	assertEquals(((Expression_Unary)((Expression_Unary)expbin.e0).e).op, OP_EXCL);
	assertEquals(expbin.op, OP_TIMES);
	assertEquals(expbin.e1.getClass(), Expression_PredefinedName.class);
	assertEquals(((Expression_PredefinedName)expbin.e1).kind, KW_y);
	}
	
	@Test

	public void test1() throws LexicalException, SyntaxException{

		String input = "u m2[[r,A]] = m1;";

		//show(input);

		Scanner scanner = new Scanner(input).scan();

		//show(scanner);   

		Parser parser = new Parser(scanner);

		Program ast = parser.parse();

		show(ast);

		assertEquals(ast.name, "u");

		Statement_Assign s = (Statement_Assign) ast.decsAndStatements.get(0);

		LHS l = s.lhs;

		assertEquals("m2",l.name);

		Index i = l.index;

		assertEquals(KW_r,((Expression_PredefinedName)(i.e0)).kind);

		assertEquals(KW_A,((Expression_PredefinedName)(i.e1)).kind);

		assertEquals(Expression_Ident.class, s.e.getClass());

		assertEquals("m1", ((Expression_Ident)(s.e)).name);

	}

	


	

	@Test

	public void test2() throws SyntaxException, LexicalException {

		

		String input = "hey man <- \"test\";";//Legal program with only a name

		show(input);//display input

		Scanner scanner = new Scanner(input).scan();//Create scanner and create token list

		show(scanner); //display the tokens

		Parser parser = new Parser(scanner);//create parser

		Program ast = parser.parse(); //parse program and get AST

		show(ast);//Display the AST

		assertEquals(ast.name, "hey"); //Check the name field in the Program object

		Statement_In dec = (Statement_In) ast.decsAndStatements.get(0);

		assertEquals(Source_StringLiteral.class,dec.source.getClass());

		assertEquals("man",dec.name);

		assertEquals("test", ((Source_StringLiteral)dec.source).fileOrUrl);	

	}


	@Test

	public void test3() throws SyntaxException, LexicalException {

		String input = "R-hey";

		Expression e = (new Parser(new Scanner(input).scan())).expression();

		show(e);

		assertEquals(Expression_Binary.class, e.getClass());

		Expression_Binary ebin = (Expression_Binary)e;

		assertEquals(Expression_PredefinedName.class, ebin.e0.getClass());

		assertEquals(KW_R, ((Expression_PredefinedName)(ebin.e0)).kind);

		assertEquals(Expression_Ident.class, ebin.e1.getClass());

		assertEquals("hey", ((Expression_Ident)(ebin.e1)).name);

		assertEquals(OP_MINUS, ebin.op);

	}


@Test

	public void test4() throws SyntaxException, LexicalException {

		String input = " a+b == true ? c : e ";

		Expression e = (new Parser(new Scanner(input).scan())).expression();

		show(e);

		Expression_Conditional econd = (Expression_Conditional)e;

		Expression_Binary ebin = (Expression_Binary)econd.condition;

		assertEquals(Expression_Conditional.class, e.getClass());

		assertEquals(Expression_Ident.class, econd.trueExpression.getClass());

		assertEquals(Expression_Ident.class, econd.falseExpression.getClass());

		assertEquals(Expression_Binary.class, ebin.e0.getClass());

		assertEquals(Expression_BooleanLit.class, ebin.e1.getClass());

		assertEquals(OP_EQ, ebin.op);

		assertEquals(Expression_Binary.class, ebin.e0.getClass());

		Expression_Binary ebin2 = (Expression_Binary)ebin.e0;

		assertEquals(Expression_PredefinedName.class, ebin2.e0.getClass());

		assertEquals(Expression_Ident.class, ebin2.e1.getClass());

		assertEquals(OP_PLUS, ebin2.op);	

		assertEquals(KW_a, ((Expression_PredefinedName)(ebin2.e0)).kind);

		assertEquals("b", ((Expression_Ident)(ebin2.e1)).name);

	}
}
