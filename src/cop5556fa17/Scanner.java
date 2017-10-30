/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
 */

package cop5556fa17;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Scanner {

	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {

		int pos;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}

		public int getPos() { return pos; }

	}

	public static enum Kind {
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, 
		KW_x/* x */, KW_X/* X */, KW_y/* y */, KW_Y/* Y */, KW_r/* r */, KW_R/* R */, KW_a/* a */, 
		KW_A/* A */, KW_Z/* Z */, KW_DEF_X/* DEF_X */, KW_DEF_Y/* DEF_Y */, KW_SCREEN/* SCREEN */, 
		KW_cart_x/* cart_x */, KW_cart_y/* cart_y */, KW_polar_a/* polar_a */, KW_polar_r/* polar_r */, 
		KW_abs/* abs */, KW_sin/* sin */, KW_cos/* cos */, KW_atan/* atan */, KW_log/* log */, 
		KW_image/* image */,  KW_int/* int */, 
		KW_boolean/* boolean */, KW_url/* url */, KW_file/* file */, OP_ASSIGN/* = */, OP_GT/* > */, OP_LT/* < */, 
		OP_EXCL/* ! */, OP_Q/* ? */, OP_COLON/* : */, OP_EQ/* == */, OP_NEQ/* != */, OP_GE/* >= */, OP_LE/* <= */, 
		OP_AND/* & */, OP_OR/* | */, OP_PLUS/* + */, OP_MINUS/* - */, OP_TIMES/* * */, OP_DIV/* / */, OP_MOD/* % */, 
		OP_POWER/* ** */, OP_AT/* @ */, OP_RARROW/* -> */, OP_LARROW/* <- */, LPAREN/* ( */, RPAREN/* ) */, 
		LSQUARE/* [ */, RSQUARE/* ] */, SEMI/* ; */, COMMA/* , */, EOF;
	}

	public static enum State{
		START, IN_DIGIT, IN_IDENT, AFTER_DIV, COMMENT,
		 STRING_LIT;
	}

	public static final HashMap<String, Kind> kindMap = new HashMap<>();
	public static final HashSet<Character> identStartSet;

	static{
		kindMap.put("x", Kind.KW_x); kindMap.put("X", Kind.KW_X); kindMap.put("y", Kind.KW_y); kindMap.put("Y", Kind.KW_Y); kindMap.put("r", Kind.KW_r); kindMap.put("R", Kind.KW_R); 
		kindMap.put("a", Kind.KW_a); kindMap.put("A", Kind.KW_A); kindMap.put("Z", Kind.KW_Z); kindMap.put("DEF_X", Kind.KW_DEF_X); kindMap.put("DEF_Y", Kind.KW_DEF_Y);
		kindMap.put("SCREEN", Kind.KW_SCREEN); kindMap.put("cart_x", Kind.KW_cart_x); kindMap.put("cart_y", Kind.KW_cart_y); kindMap.put("polar_a", Kind.KW_polar_a); 
		kindMap.put("polar_r", Kind.KW_polar_r); kindMap.put("abs", Kind.KW_abs); kindMap.put("sin", Kind.KW_sin); kindMap.put("cos", Kind.KW_cos); kindMap.put("atan", Kind.KW_atan);
		kindMap.put("log", Kind.KW_log); kindMap.put("image", Kind.KW_image); kindMap.put("int", Kind.KW_int); kindMap.put("boolean", Kind.KW_boolean); kindMap.put("url", Kind.KW_url);
		kindMap.put("file", Kind.KW_file); kindMap.put("=", Kind.OP_ASSIGN); kindMap.put("=", Kind.OP_ASSIGN); kindMap.put(">", Kind.OP_GT); kindMap.put("<", Kind.OP_LT); kindMap.put("?", Kind.OP_Q);
		kindMap.put(":", Kind.OP_COLON); kindMap.put("==", Kind.OP_EQ); kindMap.put("!=", Kind.OP_NEQ); kindMap.put(">=", Kind.OP_GE); kindMap.put("<=", Kind.OP_LE); kindMap.put("&", Kind.OP_AND);
		kindMap.put("|", Kind.OP_OR); kindMap.put("+", Kind.OP_PLUS); kindMap.put("-", Kind.OP_MINUS); kindMap.put("*", Kind.OP_TIMES); kindMap.put("/", Kind.OP_DIV); kindMap.put("%", Kind.OP_MOD);
		kindMap.put("**", Kind.OP_POWER); kindMap.put("->", Kind.OP_RARROW); kindMap.put("<-", Kind.OP_LARROW); kindMap.put("@", Kind.OP_AT); kindMap.put("(", Kind.LPAREN); kindMap.put(")", Kind.RPAREN);
		kindMap.put("[", Kind.LSQUARE); kindMap.put("]", Kind.RSQUARE); kindMap.put(";", Kind.SEMI); kindMap.put(",", Kind.COMMA); 
	}
	static{
		identStartSet = new HashSet<Character>(Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
				'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
				'_','$','0','1','2','3','4','5','6','7','8','9'));
	}
	/** Class to represent Tokens. 
	 * 
	 * This is defined as a (non-static) inner class
	 * which means that each Token instance is associated with a specific 
	 * Scanner instance.  We use this when some token methods access the
	 * chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
	 */
	public class Token {
		public final Kind kind;
		public final int pos;
		public final int length;
		public final int line;
		public final int pos_in_line;

		public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.line = line;
			this.pos_in_line = pos_in_line;
		}

		public String getText() {
			if (kind == Kind.STRING_LITERAL) {
				return chars2String(chars, pos, length);
			}
			else return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the
		 * enclosing " characters and convert escaped characters to
		 * the represented character.  For example the two characters \ t
		 * in the char array should be converted to a single tab character in
		 * the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial and final "
				char ch = chars[i];
				if (ch == '\\') { // handle escape
					i++;
					ch = chars[i];
					switch (ch) {
					case 'b':
						sb.append('\b');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'r':
						sb.append('\r'); //for completeness, line termination chars not allowed in String literals
						break;
					case 'n':
						sb.append('\n'); //for completeness, line termination chars not allowed in String literals
						break;
					case '\"':
						sb.append('\"');
						break;
					case '\'':
						sb.append('\'');
						break;
					case '\\':
						sb.append('\\');
						break;
					default:
						assert false;
						break;
					}
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}

		/**
		 * precondition:  This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length)  + "," + pos + "," + length + "," + line + ","
					+ pos_in_line + "]";
		}

		/** 
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + line;
			result = prime * result + pos;
			result = prime * result + pos_in_line;
			return result;
		}

		/**
		 * Override equals method to return true if other object
		 * is the same class and all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (line != other.line)
				return false;
			if (pos != other.pos)
				return false;
			if (pos_in_line != other.pos_in_line)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is 
		 * associated with.
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	/** 
	 * Extra character added to the end of the input characters to simplify the
	 * Scanner.  
	 */
	static final char EOFchar = 0;

	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;

	/**
	 * An array of characters representing the input.  These are the characters
	 * from the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;  




	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFchar;
		tokens = new ArrayList<Token>();
	}


	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	public Scanner scan() throws LexicalException {
		/* TODO  Replace this with a correct and complete implementation!!! */
		int pos = 0;
		int line = 1;
		int posInLine = 1;
		int startPos=0;
		State state = State.START;

		while(pos < chars.length){
			switch(state){

			case START: {
				startPos = pos;
				char ch = chars[startPos];

				if(ch =='0')
				{
					tokens.add(new Token(Kind.INTEGER_LITERAL,startPos,1,line,posInLine));
					pos++;
					posInLine++;
				}
				else if(ch == '"'){
					state = State.STRING_LIT;
					pos++; 
				}
				else if(ch ==' ' || ch == '\t' || ch == '\f')
				{
					pos++;
					posInLine++;
					state = State.START;
				}
				else if(ch == '/')
				{
					pos++;
					state = State.AFTER_DIV;
//					
//					if(chars[pos+1] == '/')
//					{
//						state = State.COMMENT;
//						pos++;
//						posInLine++;
//					}
//					else{
//						tokens.add(new Token(Kind.OP_DIV, startPos, 1,line, posInLine));
//						pos++;
//						posInLine++;
//						state = State.START;
//					}
				}
				else if(ch == '\n')
				{	pos++;
				line++;
				posInLine=1;
				}
				else if(ch== '\r')
				{
					pos++;
					line++;
					posInLine = 1;
					if(chars[pos] == '\n')
					{  
						pos++;
						state = State.START;
					}
				}
				else if(ch == '=')
				{
					if(chars[pos+1] == '=')
					{
						tokens.add(new Token(Kind.OP_EQ, startPos, 2,line,posInLine));
						pos+=2;
						posInLine+=2;
					}
					else
					{
						tokens.add(new Token(Kind.OP_ASSIGN, startPos, 1,line,posInLine));
						pos++;
						posInLine++;
						
					}
				}
				else if(ch == '-')
				{
					if(chars[pos+1] == '>')
					{
						tokens.add(new Token(Kind.OP_RARROW, startPos, 2,line,posInLine));
						pos+=2;
						posInLine+=2;
					}
					else
					{
						tokens.add(new Token(Kind.OP_MINUS, startPos, 1,line,posInLine));
						pos++;
						posInLine++;	
					}
				}
				else if(ch == '!')
				{
					if(chars[pos+1] == '=')
					{
						tokens.add(new Token(Kind.OP_NEQ, startPos, 2,line,posInLine));
						pos+=2;
						posInLine+=2;
					}
					else
					{
						tokens.add(new Token(Kind.OP_EXCL, startPos, 1,line,posInLine));
						pos++;
						posInLine++;	
					}
				}
				else if(ch == '*')
				{
					if(chars[pos+1] == '*')
					{
						tokens.add(new Token(Kind.OP_POWER, startPos, 2,line,posInLine));
						pos+=2;
						posInLine+=2;
					}
					else
					{
						tokens.add(new Token(Kind.OP_TIMES, startPos, 1,line,posInLine));
						pos++;
						posInLine++;	
					}
				}
				else if(ch == '>')
				{
					if(chars[pos+1] == '=')
					{
						tokens.add(new Token(Kind.OP_GE, startPos, 2,line,posInLine));
						pos+=2;
						posInLine+=2;
					}
					else
					{
						tokens.add(new Token(Kind.OP_GT, startPos, 1,line,posInLine));
						pos++;
						posInLine++;	
					}
				}
				else if(ch == '<')
				{
					if(chars[pos+1] == '=')
					{
						tokens.add(new Token(Kind.OP_LE, startPos, 2,line,posInLine));
						pos+=2;
						posInLine+=2;
					}
					else if(chars[pos+1] == '-')
					{
						tokens.add(new Token(Kind.OP_LARROW, startPos, 2,line,posInLine));
						pos+=2;
						posInLine+=2;
					}
					else
					{
						tokens.add(new Token(Kind.OP_LT, startPos, 1,line,posInLine));
						pos++;
						posInLine++;	
					}
				}
				else if(ch == EOFchar){
					if(pos!=chars.length-1) throw new LexicalException("EOF found before termination",pos);
					tokens.add(new Token(Kind.EOF, startPos, 0,line,posInLine));
					pos++;
					posInLine++;
				}
				else if(Character.isJavaIdentifierStart(ch))
				{
					state = State.IN_IDENT;			
				}
				else if(kindMap.containsKey(Character.toString(ch)))
				{
					tokens.add(new Token(kindMap.get(Character.toString(ch)),startPos,1,line,posInLine));
					pos++;
					posInLine++;
					state= State.START;
				}
				else if (Character.isDigit(ch)) {
					state =State.IN_DIGIT;
				}  
				else {
					throw new LexicalException("Invalid token",pos);
				}
			}break;
			
			case STRING_LIT:{
				char ch = chars[pos];
				if(ch == '"')
				{
					tokens.add(new Token(Kind.STRING_LITERAL,startPos,pos-startPos+1,line,posInLine));
					pos++;
					posInLine+=pos - startPos; 
					state = State.START;
				}
				else if(ch == '\n' || ch =='\r')
				{
					throw new LexicalException("line seperator occured before line termination",pos);
				}
				else if(ch == EOFchar)
				{
					throw new LexicalException("EOF occured before string literal termination",pos);
				}
				else if( ch == '\\')
					{
						if(chars[pos+1] == 'b' || chars[pos+1] == 't' || chars[pos+1] == 'f' || 
						 chars[pos+1] == '\"'|| chars[pos+1] == '\'' || chars[pos+1] == 'n' || chars[pos+1] == '\\' || chars[pos+1] == 'r' )
						{
							pos+=2;
						}
						else
						{
							throw new LexicalException("Invalid escape sequence",pos+1);
						}
					}
				else{
					pos++;
				}
			}break;
			
			case IN_DIGIT:{
				char ch = chars[pos];
				if (Character.isDigit(ch)) {
					pos++;
				}
				else {
					String num = new String(chars,startPos,pos-startPos);
				try{
					Integer.parseInt(num);
				}
				catch(NumberFormatException e)
				{throw new LexicalException("Number:"+num+" too large", pos);}
				tokens.add(new Token(Kind.INTEGER_LITERAL, startPos, pos - startPos,line,posInLine));
				
				posInLine+=pos-startPos;
				state = State.START;
				}
			}break;

			case IN_IDENT: {
				char ch = chars[pos];
				if(identStartSet.contains(ch))
				{
					state = State.IN_IDENT;
					pos++;
				}
				else 
				{	String token = new String(chars,startPos, pos-startPos);
				if(kindMap.containsKey(token))
				{tokens.add(new Token(kindMap.get(token),startPos,pos-startPos,line,posInLine));
				state = State.START;
				posInLine+= pos-startPos;}
				else if(token.equals("true") || token.equals("false"))
				{tokens.add(new Token(Kind.BOOLEAN_LITERAL,startPos,pos-startPos,line,posInLine));
				state = State.START;
				posInLine+= pos-startPos;
				}
				else
				{   tokens.add(new Token(Kind.IDENTIFIER, startPos, pos - startPos,line, posInLine));
				posInLine+= pos- startPos; 
				state = State.START;
				/*if (ch == '\n') {
					line++;
					posInLine = 1;
					}*/}

				}
			}break;

			case AFTER_DIV : {  
				char ch = chars[pos];
				if(ch == '/')
				{
					state = State.COMMENT;
					posInLine+=pos-startPos;
				}
				else{
					tokens.add(new Token(Kind.OP_DIV, startPos, 1,line, posInLine));
					state = State.START;
				}
			} break;
				
			case COMMENT:{
				char ch = chars[pos];
				if(ch == '\n' || ch == '\r' )
				{   state = State.START;
				}
				else if(ch==EOFchar)
				{
					state = State.START;
				}
				else
				{
					state= State.COMMENT;
					pos++;
					posInLine++;			
				}
			}break;
			}

		}
		return this;
	}


	/**
	 * Returns true if the internal iterator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that
	 * the next call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}

	/**
	 * Returns the next Token, but does not update the internal iterator.
	 * This means that the next call to nextToken or peek will return the
	 * same Token as returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition:  hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}


	/**
	 * Resets the internal iterator so that the next call to peek or nextToken
	 * will return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens 
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i)).append('\n');
		}
		return sb.toString();
	}

}
