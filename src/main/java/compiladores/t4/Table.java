package compiladores.t4;

import java.util.HashMap;

public class Table {

    public enum Tipos{
        INT, REAL, CADEIA, LOGICO, INVALIDO, TIPO, IDENT
    }

    class InSymbol{
        String name;
        Tipos tipo;

        private InSymbol(String name, Tipos tipo){
            this.name = name;
            this.tipo = tipo;
        }

    }
    private HashMap<String, InSymbol> myTable;

    public Table(){
        myTable = new HashMap<>();
    }

    public void insert(String name, Tipos tipo){
        InSymbol input = new InSymbol(name, tipo);
        myTable.put(name, input);
    }

    public Tipos verify(String name){
        return myTable.get(name).tipo;
    }

    public boolean exists(String name){
        return myTable.containsKey(name);
    }
}
