package ru.bmstu.iu9.personalfebus.compiler.lexer.token;

public class StringToken implements Token {
    private final int line;
    private final int position;
    private final String body;

    private static final String TYPE = "STRING";

    public StringToken(int line, int position, String body) {
        this.line = line;
        this.position = position;
        this.body = interpretBody(body);
    }

    private String interpretBody(String str) {
        while (needInterpretation(str)) {
            //todo $x16$
            if (str.contains("$@$")) {
                String lit = "";
                lit += (char)7;
                str = str.replace("$@$", lit);
            } else if (str.contains("$<$")) {
                String lit = "";
                lit += (char)8;
                str = str.replace("$<$", lit);
            } else if (str.contains("$>$")) {
                String lit = "";
                lit += (char)9;
                str = str.replace("$>$", lit);
            } else if (str.contains("$^$")) {
                String lit = "";
                lit += (char)10;
                str = str.replace("$^$", lit);
            } else if (str.contains("$<<$")) {
                String lit = "";
                lit += (char)13;
                str = str.replace("$<<$", lit);
            } else if (str.contains("$$")) {
                String lit = "";
                lit += (char)36;
                str = str.replace("$$", lit);
            } else if (str.contains("$\"$")) {
                String lit = "";
                lit += (char)34;
                str = str.replace("$\"$", lit);
            } else if (str.contains("$'$")) {
                String lit = "";
                lit += (char)39;
                str = str.replace("$'$", lit);
            }
        }
        return str;
    }

    private boolean needInterpretation(String str) {
        return str.contains("$@$") || str.contains("$<$") || str.contains("$>$") || str.contains("$^$") ||
                str.contains("$<<$") || str.contains("$$") || str.contains("$\"$") || str.contains("$'$");
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getBody() {
        return body;
    }
}
