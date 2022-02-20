package ru.bmstu.iu9.personalfebus.compiler.ast.value;

import java.util.ArrayList;

public class AstIdentExpr implements RValue {
    private String name;
    private ArrayList<Integer> tail;

    private static String TYPE = "IDENTIFIER";

    public AstIdentExpr(String name) {
        this.name = name;
        this.tail = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Integer> getTail() {
        return tail;
    }

    public void addTail(int a) {
        tail.add(a);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
