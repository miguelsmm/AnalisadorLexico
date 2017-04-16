import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Lexema {

	private BufferedReader reader; // Reader
	private char current; // Char atual sendo scaneado

	private static final char EOF = (char) (-1);

	// End of file character

	public Lexema(String file) {
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Lê o primeiro caractere
		current = read();
	}

	private char read() {
		try {
			return (char) (reader.read());
		} catch (IOException e) {
			e.printStackTrace();
			return EOF;
		}
	}

	// Verifica se o caractere é um número
	private boolean isNumeric(char c) {
		if (c >= '0' && c <= '9')
			return true;

		return false;
	}

	// verifica se o caractere é uma letra
	public boolean isALetter(char c) {
		if (c >= 'a' && c <= 'z')
			return true;
		if (c >= 'A' && c <= 'Z')
			return true;

		return false;

	}

	public Token nextToken() {

		int state = 1; // Estado inicial
		int numBuffer = 0; // Buffer para numeros literais
		String letterBuffer = ""; // Buffer para letras
		int decBuffer = 0; // buffer para numeros decimais
		boolean skipped = false;
		while (true) {
			if (current == EOF && !skipped) {
				skipped = true;

			} else if (skipped) {

				try {

					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}

			switch (state) {
			// Classificação em seus respectivos tokens
			case 1:
				switch (current) {
				case ' ': // Tira os espaços em branco e formatações
				case '\n':
				case '\b':
				case '\f':
				case '\r':
				case '\t':
					current = read();
					continue;

				case ';':
					current = read();
					return new Token("semi_colon", ";");

				case '+':
					current = read();
					return new Token("Arith_Op", "+");

				case '-':
					current = read();
					return new Token("Arith_Op", "-");

				case '*':
					current = read();
					return new Token("Arith_Op", "*");

				case '/':
					current = read();
					state = 14;
					continue;

				case ',':
					current = read();
					return new Token("comma", ",");
				case '(':
					current = read();
					return new Token("left_parenthesis", "(");
				case ')':
					current = read();
					return new Token("right_parenthesis", ")");
				case '{':
					current = read();
					return new Token("left_bracket", "{");
				case '}':
					current = read();
					return new Token("right_bracket", "}");
				case '%':
					current = read();
					return new Token("Arith_Op", "%");
				case '=':
					current = read();
					state = 8;
					continue;

				case '<':
					current = read();
					state = 18;
					continue;

				case '>':
					current = read();
					state = 19;
					continue;

				case '!':
					current = read();
					state = 9;
					continue;
				case '&':
					current = read();
					state = 10;
					continue;
				case '|':
					current = read();
					state = 11;
					continue;
				case '"':
					current = read();
					state = 13;
					letterBuffer = "";
					continue;

				default:
					state = 2; // Verifica a próxima possibilidade
					continue;
				}

				// Numeros
			case 2:
				if (isNumeric(current)) {
					numBuffer = 0; // Reseta o Buffer;
					numBuffer += (current - '0');

					state = 3;

					current = read();

				} else {
					state = 5; // se não começa com número ou símbolo, pula para
								// o case 5;
				}
				continue;

			// Verifica se é um número inteiro
			case 3:
				if (isNumeric(current)) {
					numBuffer *= 10;
					numBuffer += (current - '0');

					current = read();

				} else if (current == '.') {

					current = read();

					state = 4; // Se é número decimal, é tratado no case 4;

				} else {
					return new Token("num", "" + numBuffer);
				}

				continue;

			// decimal
			case 4:
				if (isNumeric(current)) {
					decBuffer = 0;
					decBuffer += (current - '0');
					state = 7;
					current = read();

				} else {
					return new Token("ERROR", "Valor inválido: " + numBuffer + ".");
				}
				continue;
			// Verifica se é numero decimal
			case 7:
				if (isNumeric(current)) {
					decBuffer *= 10;
					decBuffer += (current - '0');

					current = read();
				} else {
					return new Token("num", "" + numBuffer + "." + decBuffer);
				}
				continue;

			// Verifica o buffer de letras, verificando se é valido
			case 5:
				if (isALetter(current) || current == '_') {
					letterBuffer = "";
					letterBuffer += current;
					state = 6;
					current = read();
				} else {
					letterBuffer = "";
					letterBuffer += current;
					current = read();
					return new Token("ERROR", "Valor inválido: " + letterBuffer);
				}
				continue;

			// Identifica se é uma palavra reservada ou id;
			case 6:
				if ((isALetter(current) || isNumeric(current) || current == '_')) {

					letterBuffer += current;
					current = read();

				} else {

					if (letterBuffer.equals("class") || letterBuffer.equals("static") || letterBuffer.equals("else")
							|| letterBuffer.equals("if") || letterBuffer.equals("int") || letterBuffer.equals("float")
							|| letterBuffer.equals("boolean") || letterBuffer.equals("String")
							|| letterBuffer.equals("public") || letterBuffer.equals("return")
							|| letterBuffer.equals("while") || letterBuffer.equals("for") || letterBuffer.equals("do")
							|| letterBuffer.equals("printf") || letterBuffer.equals("null")
							|| letterBuffer.equals("double") || letterBuffer.equals("private")) {
						return new Token("reserved_word", "" + letterBuffer);

					} else if (letterBuffer.equals("true") || letterBuffer.equals("false")) {
						return new Token("boolean", "" + letterBuffer);
					}

					return new Token("id", "" + letterBuffer);
				}
				continue;

			// if ==
			case 8:
				if (current == '=') {
					current = read();
					return new Token("Equal_Op", "==");
				} else {

					return new Token("Atrib_Op", "=");
				}
				// if !=
			case 9:
				if (current == '=') {
					current = read();
					return new Token("Equal_OP", "!=");
				} else {
					return new Token("ERROR", "Invalid input: !");
				}

				// if &&
			case 10:
				if (current == '&') {
					current = read();
					return new Token("Equal_OP", "&&");
				} else {
					return new Token("ERROR", "Valor inválido: &");
				}
				// if ||
			case 11:
				if (current == '|') {
					current = read();
					return new Token("Equal_OP", "||");
				} else {
					return new Token("ERROR", "Valor inválido: |");
				}

			case 13:
				if (current == '"') {
					current = read();
					return new Token("string_literal", "\"" + letterBuffer + "\"");
				} else if (current == '\n' || current == EOF) {
					current = read();
					return new Token("ERROR", "Valor de String Inválido");
				} else {
					letterBuffer += current;
					current = read();
				}
				continue;

			case 14:
				if (current == '/') {
					state = 15;
					current = read();
				} else if (current == '*') {
					state = 16;
					current = read();
				} else {
					return new Token("Arith_Op", "/");
				}
				continue;
			case 15:
				if (current == '\n') {

					state = 1;
				}
				current = read();
				continue;
			case 16:
				if (current == '*') {
					state = 17;

				}
				current = read();
				continue;
			case 17:
				if (current == '/') {
					current = read();
					state = 1;
				} else {
					current = read();
					state = 16;
				}
				continue;
			// if < || <=
			case 18:
				if (current == '=') {
					current = read();
					return new Token("Relational_Op", "<=");
				} else {
					return new Token("Relational_Op", "<");
				}
				// if > || >=
			case 19:
				if (current == '=') {
					current = read();
					return new Token("Relational_Op", ">=");
				} else {
					return new Token("Relational_Op", ">");
				}
			}
		}
	}
}
