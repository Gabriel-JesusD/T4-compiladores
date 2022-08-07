package compiladores.t4;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

import compiladores.t4.AlgumaParser.Exp_aritmeticaContext;
import compiladores.t4.AlgumaParser.ExpressaoContext;
import compiladores.t4.AlgumaParser.FatorContext;
import compiladores.t4.AlgumaParser.Fator_logicoContext;
import compiladores.t4.AlgumaParser.ParcelaContext;
import compiladores.t4.AlgumaParser.TermoContext;
import compiladores.t4.AlgumaParser.Termo_logicoContext;

public class SemanticoUtils {
    public static List<String> errosSemanticos = new ArrayList<>();
    
    public static void adicionarErroSemantico(Token t, String mensagem) {
        int linha = t.getLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }
    
    public static Table.Tipos verificarTipo(Escopo escopos, AlgumaParser.ExpressaoContext ctx) {
        Table.Tipos ret = null;
        for (Termo_logicoContext ta : ctx.termo_logico()) {
            Table.Tipos aux = verificarTipo(escopos, ta);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != Table.Tipos.INVALIDO) {
                ret = Table.Tipos.INVALIDO;
            }
        }
        //SemanticoUtils.adicionarErroSemantico(ctx.start, "9" +ctx.getText() + ret);
        return ret;
    }

    public static Table.Tipos verificarTipo(Escopo escopos, AlgumaParser.Termo_logicoContext ctx) {
        Table.Tipos ret = null;
        for (Fator_logicoContext ta : ctx.fator_logico()) {
            Table.Tipos aux = verificarTipo(escopos, ta);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != Table.Tipos.INVALIDO) {
                ret = Table.Tipos.INVALIDO;
            }
        }

        //SemanticoUtils.adicionarErroSemantico(ctx.start, "8" +ctx.getText() + ret);
        return ret;
    }

    public static Table.Tipos verificarTipo(Escopo escopos, AlgumaParser.Fator_logicoContext ctx) {
        //SemanticoUtils.adicionarErroSemantico(ctx.start, ctx.getText() + verificarTipo(escopos, ctx.parcela_logica()));
        return verificarTipo(escopos, ctx.parcela_logica());
    }

    public static Table.Tipos verificarTipo(Escopo escopos, AlgumaParser.Parcela_logicaContext ctx) {
        Table.Tipos ret = null;
        if(ctx.exp_relacional() != null){
            ret = verificarTipo(escopos, ctx.exp_relacional());
        } else{
            ret = Table.Tipos.LOGICO;
        }

        //SemanticoUtils.adicionarErroSemantico(ctx.start, "7" +ctx.getText() + ret);
        return ret;
    }

    public static Table.Tipos verificarTipo(Escopo escopos, AlgumaParser.Exp_relacionalContext ctx) {
        Table.Tipos ret = null;
        if(ctx.op_relacional() != null){
            for (Exp_aritmeticaContext ta : ctx.exp_aritmetica()) {
                Table.Tipos aux = verificarTipo(escopos, ta);
                Boolean auxNumeric = aux == Table.Tipos.REAL || aux == Table.Tipos.INT;
                Boolean retNumeric = ret == Table.Tipos.REAL || ret == Table.Tipos.INT;
                if (ret == null) {
                    ret = aux;
                } else if (!(auxNumeric && retNumeric) && aux != ret) {
                    ret = Table.Tipos.INVALIDO;
                }
            }
            if(ret != Table.Tipos.INVALIDO){
                ret = Table.Tipos.LOGICO;
            }
        } else {
            ret = verificarTipo(escopos, ctx.exp_aritmetica(0));
        }

        //SemanticoUtils.adicionarErroSemantico(ctx.start, "6" +ctx.getText() + ret);
        return ret;
    }

    public static Table.Tipos verificarTipo(Escopo escopos, AlgumaParser.Exp_aritmeticaContext ctx) {
        Table.Tipos ret = null;
        for (TermoContext ta : ctx.termo()) {
            Table.Tipos aux = verificarTipo(escopos, ta);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != Table.Tipos.INVALIDO) {
                ret = Table.Tipos.INVALIDO;
            }
        }

        //SemanticoUtils.adicionarErroSemantico(ctx.start, "5" +ctx.getText() + ret);
        return ret;
    }

    public static Table.Tipos verificarTipo(Escopo escopos, AlgumaParser.TermoContext ctx) {
        Table.Tipos ret = null;

        for (FatorContext fa : ctx.fator()) {
            Table.Tipos aux = verificarTipo(escopos, fa);
            Boolean auxNumeric = aux == Table.Tipos.REAL || aux == Table.Tipos.INT;
            Boolean retNumeric = ret == Table.Tipos.REAL || ret == Table.Tipos.INT;
            if (ret == null) {
                ret = aux;
            } else if (!(auxNumeric && retNumeric) && aux != ret) {
                ret = Table.Tipos.INVALIDO;
            }
        }
        //SemanticoUtils.adicionarErroSemantico(ctx.start, "4" +ctx.getText() + ret);
        return ret;
    }
    public static Table.Tipos verificarTipo(Escopo escopos, AlgumaParser.FatorContext ctx) {
        Table.Tipos ret = null;

        for (ParcelaContext fa : ctx.parcela()) {
            Table.Tipos aux = verificarTipo(escopos, fa);
            if (ret == null) {
                ret = aux;
            } else if (ret != aux && aux != Table.Tipos.INVALIDO) {
                ret = Table.Tipos.INVALIDO;
            }
        }
        //SemanticoUtils.adicionarErroSemantico(ctx.start, "3" +ctx.getText() + ret);
        return ret;
    }
    public static Table.Tipos verificarTipo(Escopo escopos, AlgumaParser.ParcelaContext ctx) {
        Table.Tipos ret = Table.Tipos.INVALIDO;

        if(ctx.parcela_nao_unario() != null){
            ret = verificarTipo(escopos, ctx.parcela_nao_unario());
        }
        else {
            //SemanticoUtils.adicionarErroSemantico(ctx.start, "ta aq: " + ctx.getText() + verificarTipo(escopos, ctx.parcela_unario()));
            ret = verificarTipo(escopos, ctx.parcela_unario());
        }
        //SemanticoUtils.adicionarErroSemantico(ctx.start, "2" + ctx.getText() + ret);
        return ret;
    }

    public static Table.Tipos verificarTipo(Escopo escopos, AlgumaParser.Parcela_nao_unarioContext ctx) {
        if (ctx.identificador() != null) {
            return verificarTipo(escopos, ctx.identificador());
        }
        return Table.Tipos.CADEIA;
    }

    public static Table.Tipos verificarTipo(Escopo escopos, AlgumaParser.IdentificadorContext ctx) {//kk suspeitos
        String nomeVar = "";
        Table.Tipos ret = Table.Tipos.INVALIDO;
        for(int i = 0; i < ctx.IDENT().size(); i++){
            nomeVar += ctx.IDENT(i).getText();
            if(i != ctx.IDENT().size() - 1){
                nomeVar += ".";
            }
        }
        for(Table tabela : escopos.getPilha()){
            if (tabela.exists(nomeVar)) {
                ret = verificarTipo(escopos, nomeVar);
            }
        }
        System.out.println(nomeVar);
        return ret;
    }
    
    public static Table.Tipos verificarTipo(Escopo escopos, AlgumaParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null) {
            return Table.Tipos.INT;
        }
        if (ctx.NUM_REAL() != null) {
            return Table.Tipos.REAL;
        }
        if(ctx.identificador() != null){
            return verificarTipo(escopos, ctx.identificador());
        }
        if (ctx.IDENT() != null) {
            return verificarTipo(escopos, ctx.IDENT().getText());
        } else {
            Table.Tipos ret = null;
            for (ExpressaoContext fa : ctx.expressao()) {
                Table.Tipos aux = verificarTipo(escopos, fa);
                if (ret == null) {
                    ret = aux;
                } else if (ret != aux && aux != Table.Tipos.INVALIDO) {
                    ret = Table.Tipos.INVALIDO;
                }
            }
            return ret;
        }
    }
    
    public static Table.Tipos verificarTipo(Escopo escopos, String nomeVar) {
        Table.Tipos type = Table.Tipos.INVALIDO;
        for(Table tabela : escopos.getPilha()){
            if(tabela.exists(nomeVar)){
                return tabela.verify(nomeVar);
            }
        }

        return type;
    }

    public static Table.Tipos getTipo(String val){
        Table.Tipos tipo = null;
                switch(val) {
                    case "literal": 
                        tipo = Table.Tipos.CADEIA;
                        break;
                    case "inteiro": 
                        tipo = Table.Tipos.INT;
                        break;
                    case "real": 
                        tipo = Table.Tipos.REAL;
                        break;
                    case "logico": 
                        tipo = Table.Tipos.LOGICO;
                        break;
                    default:
                        break;
                }
        return tipo;
    }

}
