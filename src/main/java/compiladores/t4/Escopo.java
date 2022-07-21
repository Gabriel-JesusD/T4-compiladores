package compiladores.t4;

import java.util.LinkedList;
import java.util.List;

public class Escopo {
    private LinkedList<Table> pilha; //empilhando tabelas

    public Escopo(){
        pilha = new LinkedList<>();
        create();
    }

    public void create(){
        pilha.push(new Table());
    }

    public Table getEscopo(){
        return pilha.peek();
    }

    public List<Table> getPilha(){
        return pilha;
    }

    public void dropEscopo(){
        pilha.pop();
    }

}
