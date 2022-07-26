package compiladores.t4;

import java.util.HashMap;
import java.util.Vector;

public class Table {

    public enum Tipos{
        INT, REAL, CADEIA, LOGICO, INVALIDO, TIPO, IDENT, REG,
        REGINT, REGREAL, REGCADEIA, REGLOGICO,
    }

    class InSymbol{
        String name;
        Tipos tipo;

        public InSymbol(String name, Tipos tipo){
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

    public void insert(InSymbol input){
        myTable.put(input.name, input);
    }

    public Tipos verify(String name){
        return myTable.get(name).tipo;
    }

    public boolean exists(String name){
        return myTable.containsKey(name);
    }
}
