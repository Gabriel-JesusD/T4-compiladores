package compiladores.t4;

import compiladores.t4.AlgumaBaseVisitor;
import compiladores.t4.AlgumaParser.CmdAtribuicaoContext;
import compiladores.t4.AlgumaParser.CmdLeiaContext;
import compiladores.t4.AlgumaParser.Declaracao_constanteContext;
import compiladores.t4.AlgumaParser.Declaracao_globalContext;
import compiladores.t4.AlgumaParser.Declaracao_localContext;
import compiladores.t4.AlgumaParser.Declaracao_tipoContext;
import compiladores.t4.AlgumaParser.Declaracao_variavelContext;
import compiladores.t4.AlgumaParser.Exp_aritmeticaContext;
import compiladores.t4.AlgumaParser.IdentificadorContext;
import compiladores.t4.AlgumaParser.ProgramaContext;
import compiladores.t4.AlgumaParser.Tipo_basico_identContext;

public class AlgumaSemantico extends AlgumaBaseVisitor {
    
    Escopo escopos = new Escopo();

    @Override
    public Object visitPrograma(ProgramaContext ctx) {
        return super.visitPrograma(ctx);
    }

    @Override
    public Object visitDeclaracao_constante(Declaracao_constanteContext ctx) {
        Table escopoAtual = escopos.getEscopo();
        if (escopoAtual.exists(ctx.IDENT().getText())) {
            SemanticoUtils.adicionarErroSemantico(ctx.start, "constante" + ctx.IDENT().getText()
                    + " ja declarado anteriormente");
        } else {
            Table.Tipos tipo = Table.Tipos.INT;
            switch(ctx.tipo_basico().getText()) {
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
            }
            escopoAtual.insert(ctx.IDENT().getText(), tipo);
        }

        return super.visitDeclaracao_constante(ctx);
    }

    @Override
    public Object visitDeclaracao_tipo(Declaracao_tipoContext ctx) {
        Table escopoAtual = escopos.getEscopo();
        if (escopoAtual.exists(ctx.IDENT().getText())) {
             SemanticoUtils.adicionarErroSemantico(ctx.start, "tipo " + ctx.IDENT().getText()
                    + " declarado duas vezes num mesmo escopo");
        } else {
            escopoAtual.insert(ctx.IDENT().getText(), Table.Tipos.TIPO);
        }
        return super.visitDeclaracao_tipo(ctx);
    }

    @Override
    public Object visitDeclaracao_variavel(Declaracao_variavelContext ctx) {
        Table escopoAtual = escopos.getEscopo();
        for (IdentificadorContext id : ctx.variavel().identificador()) {
            if (escopoAtual.exists(id.getText())) {
                SemanticoUtils.adicionarErroSemantico(id.start, "identificador " + id.getText()
                        + " ja declarado anteriormente");
            } else {
                Table.Tipos tipo = Table.Tipos.INT;
                switch(ctx.variavel().tipo().getText()) {
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
                }
                escopoAtual.insert(id.getText(), tipo);
            }
        }
        return super.visitDeclaracao_variavel(ctx);
    }

    @Override
    public Object visitDeclaracao_global(Declaracao_globalContext ctx) {
         Table escopoAtual = escopos.getEscopo();
        if (escopoAtual.exists(ctx.IDENT().getText())) {
            SemanticoUtils.adicionarErroSemantico(ctx.start, ctx.IDENT().getText()
                    + " ja declarado anteriormente");
        } else {
            escopoAtual.insert(ctx.IDENT().getText(), Table.Tipos.TIPO);
        }
        return super.visitDeclaracao_global(ctx);
    }


    @Override
    public Object visitTipo_basico_ident(Tipo_basico_identContext ctx) {
        if(ctx.IDENT() != null){
            for(Table escopo : escopos.getPilha()) {
                if(!escopo.exists(ctx.IDENT().getText())) {
                    SemanticoUtils.adicionarErroSemantico(ctx.start, "tipo " + ctx.IDENT().getText()
                            + " nao declarado");
                }
            }
        }
        return super.visitTipo_basico_ident(ctx);
    }

    @Override
    public Object visitIdentificador(IdentificadorContext ctx) {
        for(Table escopo : escopos.getPilha()) {
            if(!escopo.exists(ctx.IDENT(0).getText())) {
                SemanticoUtils.adicionarErroSemantico(ctx.start, "identificador " + ctx.IDENT(0).getText()
                        + " nao declarado");
            }
        }
        return super.visitIdentificador(ctx);
    }

    @Override
    public Object visitCmdAtribuicao(CmdAtribuicaoContext ctx) {
        Table.Tipos tipoExpressao = SemanticoUtils.verificarTipo(escopos, ctx.expressao());
        boolean error = false;
        String nomeVar = ctx.identificador().getText();
        if (tipoExpressao != Table.Tipos.INVALIDO) {
            for(Table escopo : escopos.getPilha()){
                if (escopo.exists(nomeVar))  {
                    Table.Tipos tipoVariavel = SemanticoUtils.verificarTipo(escopos, nomeVar);
                    Boolean varNumeric = tipoVariavel == Table.Tipos.REAL || tipoVariavel == Table.Tipos.INT;
                    Boolean expNumeric = tipoExpressao == Table.Tipos.REAL || tipoExpressao == Table.Tipos.INT;
                    if  (!(varNumeric && expNumeric) && tipoVariavel != tipoExpressao && tipoExpressao != Table.Tipos.INVALIDO) {
                        error = true;
                    }
                } 
            }
        } else{
            error = true;
        }

        if(error)
            SemanticoUtils.adicionarErroSemantico(ctx.identificador().start, "atribuicao nao compativel para " + nomeVar );

        return super.visitCmdAtribuicao(ctx);
    }

}
