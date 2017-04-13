public class Token {
	private String token; 
	private String lexeme; 

	public Token(String token, String lexeme) {
		this.token = token;
		this.lexeme = lexeme;
	}

	// Retorna o tipo do Token
	public String getTokenType() {
		return token;
	}

	// Retorna o Lexema do Token
	public String getLexeme() {
		return lexeme;
	}

	// Retorna o token em String
	public String toString() {
		return token + "\t" + lexeme;
	}
}