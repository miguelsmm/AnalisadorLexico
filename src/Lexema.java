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
	public boolean isAlpha(char c) {
		if (c >= 'a' && c <= 'z')
			return true;
		if (c >= 'A' && c <= 'Z')
			return true;

		return false;

	}

	public Token nextToken() {

		int state = 1; // Initial state
		int numBuffer = 0; // A buffer for number literals
		String alphaBuffer = "";
		int decBuffer = 0;
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
			// Controle do código fonte
			case 1:
				switch (current) {
				case ' ': // Tira os espaços em branco e comentários
				case '\n':
				case '\b':
				case '\f':
				case '\r':
				case '\t':
					current = read();
					continue;

				case ';':
					current = read();
					return new Token("SM", ";");

				case '+':
					current = read();
					return new Token("PO", "+");

				case '-':
					current = read();
					return new Token("MO", "-");

				case '*':
					current = read();
					return new Token("TO", "*");

				case '/':
					current = read();
					state = 14;
					continue;
				// return new Token("DO", "/");
				case ',':
					current = read();
					return new Token("FA", ",");
				case '(':
					current = read();
					return new Token("LP", "(");
				case ')':
					current = read();
					return new Token("RP", ")");
				case '{':
					current = read();
					return new Token("LB", "{");
				case '}':
					current = read();
					return new Token("RB", "}");
				case '%':
					current = read();
					return new Token("MD", "%");
				case '=':
					current = read();
					state = 8;
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
					alphaBuffer = "";
					continue;

				default:
					state = 2; // Check the next possibility
					continue;
				}

				// Integer - Start
			case 2:
				if (isNumeric(current)) {
					numBuffer = 0; // Reset the buffer.
					numBuffer += (current - '0');

					state = 3;

					current = read();

				} else {
					state = 5; // does not start with number or symbol go to
								// case 5
				}
				continue;

			// Integer - Body
			case 3:
				if (isNumeric(current)) {
					numBuffer *= 10;
					numBuffer += (current - '0');

					current = read();

				} else if (current == '.') {

					current = read();

					state = 4; // has decimal point go to case 4

				} else {
					return new Token("NUM", "" + numBuffer);
				}

				continue;

			// decimal-start
			case 4:
				if (isNumeric(current)) {
					decBuffer = 0;
					decBuffer += (current - '0');
					state = 7;
					current = read();

				} else {
					return new Token("ERROR", "Invalid input: " + numBuffer + ".");
				}
				continue;
			// decimal body
			case 7:
				if (isNumeric(current)) {
					decBuffer *= 10;
					decBuffer += (current - '0');

					current = read();
				} else {
					return new Token("NM", "" + numBuffer + "." + decBuffer);
				}
				continue;

			// identifier -start
			case 5:
				if (isAlpha(current) || current == '_') {
					alphaBuffer = "";
					alphaBuffer += current;
					state = 6;
					current = read();
				} else {
					alphaBuffer = "";
					alphaBuffer += current;
					current = read();
					return new Token("ERROR", "Invalid input:" + alphaBuffer);
				}
				continue;

			// identifier - Body
			case 6:
				if ((isAlpha(current) || isNumeric(current) || current == '_')) {

					alphaBuffer += current;
					current = read();

				} else {

					if (alphaBuffer.equals("class") || alphaBuffer.equals("static") || alphaBuffer.equals("else")
							|| alphaBuffer.equals("if") || alphaBuffer.equals("int") || alphaBuffer.equals("float")
							|| alphaBuffer.equals("boolean") || alphaBuffer.equals("String")
							|| alphaBuffer.equals("return") || alphaBuffer.equals("while")) {
						return new Token("[reserved_word]", "" + alphaBuffer);

					} else if (alphaBuffer.equals("true") || alphaBuffer.equals("false")) {
						return new Token("[boolean]", "" + alphaBuffer);
					}

					return new Token("[id]", "" + alphaBuffer);
				}
				continue;

			// if ==
			case 8:
				if (current == '=') {
					current = read();
					return new Token("EQ", "==");
				} else {

					return new Token("AO", "=");
				}
				// if !=
			case 9:
				if (current == '=') {
					current = read();
					return new Token("NE", "!=");
				} else {
					return new Token("ERROR", "Invalid input: !");
				}

				// if &&
			case 10:
				if (current == '&') {
					current = read();
					return new Token("LA", "&&");
				} else {
					return new Token("ERROR", "Invalid input: &");
				}
				// if ||
			case 11:
				if (current == '|') {
					current = read();
					return new Token("LO", "||");
				} else {
					return new Token("ERROR", "Invalid input: |");
				}

			case 13:
				if (current == '"') {
					current = read();
					return new Token("ST", "\"" + alphaBuffer + "\"");
				} else if (current == '\n' || current == EOF) {
					current = read();
					return new Token("ERROR", "Invalid string literal");
				} else {
					alphaBuffer += current;
					current = read();
				}
				continue;
			// alphaBuffer += curr;
			// curr = read();

			case 14:
				if (current == '/') {
					state = 15;
					current = read();
				} else if (current == '*') {
					state = 16;
					current = read();
				} else {
					return new Token("DO", "/");
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

			}
		}
	}
}
