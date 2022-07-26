package compiladores.t4;

import java.util.HashMap;
import java.util.Vector;

public class RegTable {

    class InSymbol{
        String name;
        String tipo;

        public InSymbol(String name, String tipo){
            this.name = name;
            this.tipo = tipo;
        }

    }
    private HashMap<String, InSymbol> myTable;

    public RegTable(){
        myTable = new HashMap<>();
    }

    public void insert(String name, String tipo){
        InSymbol input = new InSymbol(name, tipo);
        myTable.put(name, input);
    }

    public void insert(InSymbol input){
        myTable.put(input.name, input);
    }

    public String verify(String name){
        return myTable.get(name).tipo;
    }

    public boolean exists(String name){
        return myTable.containsKey(name);
    }
}
