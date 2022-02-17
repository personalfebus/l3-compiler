package ru.bmstu.iu9.personalfebus.compiler.parser;

import ru.bmstu.iu9.personalfebus.compiler.ast.AstProgram;

public interface IParser {
    //return ast tree here
    AstProgram parse();
}
