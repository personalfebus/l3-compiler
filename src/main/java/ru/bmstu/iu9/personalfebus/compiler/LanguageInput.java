package ru.bmstu.iu9.personalfebus.compiler;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstProgram;
import ru.bmstu.iu9.personalfebus.compiler.generator.LabelGenerationHelper;
import ru.bmstu.iu9.personalfebus.compiler.generator.VariableNameTranslator;
import ru.bmstu.iu9.personalfebus.compiler.lexer.Lexer;
import ru.bmstu.iu9.personalfebus.compiler.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class LanguageInput {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line  = in.readLine();

        StringBuilder input = new StringBuilder();
        while (line != null) {
            input.append(line);
            input.append('\n');

            // next iteration
            line = in.readLine();
        }

        Lexer lexer = new Lexer(input.toString());
        Parser parser = new Parser(lexer);

        try {
            AstProgram program = parser.parse();
            //code gen and semantics
            System.out.println(program.generateIL(new HashSet<>(), new VariableNameTranslator(), new VariableNameTranslator(), new LabelGenerationHelper(), null));
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

//        while (lexer.hasTokens()) {
//            Token token = lexer.nextToken();
//            System.out.println("------");
//            System.out.println(token.getType());
//            System.out.println(token.getBody());
//            //System.out.println("------");
//        }
    }
}
