import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Teste {
	public static void main(String[] args) {
		String inFile = "src/CodigoFonte.txt";
		String outFile = "src/TabelaGerada.txt";

		if (args.length > 1) {
			inFile = args[0];
			outFile = args[1];
		}

		Lexema lexer = new Lexema(inFile);

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

			Token t;

			while ((t = lexer.nextToken()) != null) {
				writer.write(t.toString());
				writer.newLine();
			}

			writer.close(); 
			
			System.out.println("Analisador Léxico Feito");
			System.out.println("File: " + outFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
