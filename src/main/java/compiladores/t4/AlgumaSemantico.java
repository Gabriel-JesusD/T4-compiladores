package compiladores.t4;

import java.util.ArrayList;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.ibm.icu.impl.Pair;

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
import compiladores.t4.AlgumaParser.RegistroContext;
import compiladores.t4.AlgumaParser.Tipo_basico_identContext;
import compiladores.t4.AlgumaParser.VariavelContext;

public class AlgumaSemantico extends AlgumaBaseVisitor {
    
    Escopo escopos = new Escopo();
    RegTable nomeReg = new RegTable();
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
            Table.Tipos aux = SemanticoUtils.getTipo(ctx.tipo_basico().getText()) ;
            if(aux != null)
                tipo = aux;
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
            Table.Tipos tipo = SemanticoUtils.getTipo(ctx.tipo().getText());
            if(tipo != null)
                escopoAtual.insert(ctx.IDENT().getText(), tipo);
            else if(ctx.tipo().registro() != null){
                ArrayList<Table.InSymbol> varReg = new ArrayList<>();
                        for(VariavelContext va : ctx.tipo().registro().variavel()){
                            Table.Tipos tipoReg =  SemanticoUtils.getTipo(va.tipo().getText());
                            for(IdentificadorContext id2 : va.identificador()){
                                varReg.add(escopoAtual.new InSymbol(id2.getText(), tipoReg));
                            }

                        }

                        if (escopoAtual.exists(ctx.IDENT().getText())) {
                            SemanticoUtils.adicionarErroSemantico(ctx.start, "identificador " + ctx.IDENT().getText()
                                    + " ja declarado anteriormente");
                        }
                        else{
                            escopoAtual.insert(ctx.IDENT().getText(), Table.Tipos.REG);
                        }

                        for(Table.InSymbol re : varReg){
                            String nameVar = ctx.IDENT().getText() + '.' + re.name;
                            if (escopoAtual.exists(nameVar)) {
                                SemanticoUtils.adicionarErroSemantico(ctx.start, "identificador " + nameVar
                                        + " ja declarado anteriormente");
                            }
                            else if(escopoAtual.exists(re.name)) {
                                SemanticoUtils.adicionarErroSemantico(ctx.start, "identificador " + re.name
                                        + " ja declarado anteriormente");
                            }
                            else{
                                // SemanticoUtils.adicionarErroSemantico(id.start, "oi rs tamo adicionando " + re.name );
                                escopoAtual.insert(re);
                                escopoAtual.insert(nameVar, re.tipo);
                            }
                        }
            }
            escopoAtual.insert(ctx.IDENT().getText(), Table.Tipos.TIPO);
        }
        return super.visitDeclaracao_tipo(ctx);
    }

    @Override
    public Object visitRegistro(RegistroContext ctx) {
        // TODO Auto-generated method stub
        // String p = ctx.parent.getText();
        // p = ctx.parent.toStringTree();
        // SemanticoUtils.adicionarErroSemantico(ctx.start, "tamanho dessa desgrama " + ctx.variavel().size() + " filho da " + p);
        return super.visitRegistro(ctx);
    }

    @Override
    public Object visitDeclaracao_variavel(Declaracao_variavelContext ctx) {
        Table escopoAtual = escopos.getEscopo();
        for (IdentificadorContext id : ctx.variavel().identificador()) {
            // SemanticoUtils.adicionarErroSemantico(id.start, "yeah " + id.getText());
            if (escopoAtual.exists(id.getText())) {
                SemanticoUtils.adicionarErroSemantico(id.start, "identificador " + id.getText()
                        + " ja declarado anteriormente");
            } else {
                Table.Tipos tipo = SemanticoUtils.getTipo(ctx.variavel().tipo().getText());
                if(tipo != null)
                    escopoAtual.insert(id.getText(), tipo);
                else{
                    //recursao para o insert
                    
                    if(ctx.variavel().tipo().registro() != null){
                        ArrayList<Table.InSymbol> varReg = new ArrayList<>();
                        for(VariavelContext va : ctx.variavel().tipo().registro().variavel()){
                            Table.Tipos tipoReg =  SemanticoUtils.getTipo(va.tipo().getText());
                            for(IdentificadorContext id2 : va.identificador()){
                                varReg.add(escopoAtual.new InSymbol(id2.getText(), tipoReg));
                            }

                        }
                        escopoAtual.insert(id.getText(), Table.Tipos.REG);

                        for(Table.InSymbol re : varReg){
                            String nameVar = id.getText() + '.' + re.name;
                            if (escopoAtual.exists(nameVar)) {
                                SemanticoUtils.adicionarErroSemantico(id.start, "identificador " + nameVar
                                        + " ja declarado anteriormente");
                            }
                            else if(escopoAtual.exists(re.name)) {
                                SemanticoUtils.adicionarErroSemantico(id.start, "identificador " + re.name
                                        + " ja declarado anteriormente");
                            }
                            else{
                                // SemanticoUtils.adicionarErroSemantico(id.start, "oi rs tamo adicionando " + re.name );
                                escopoAtual.insert(re);
                                escopoAtual.insert(nameVar, re.tipo);
                            }
                        }

                    }
                    else{//tipo registro estendido
                        escopoAtual.insert(id.getText(), Table.Tipos.INT);
                    }
                }
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
        String nomeVar = "";
        int i = 0;
        for(TerminalNode id : ctx.IDENT()){
            if(i++ > 0)
                nomeVar += ".";
            nomeVar += id.getText();
        }
        for(Table escopo : escopos.getPilha()) {
            
            // if(!escopo.exists(ctx.IDENT(0).getText())) {
            if(!escopo.exists(nomeVar)) {
                // SemanticoUtils.adicionarErroSemantico(ctx.start, "identificador " + ctx.IDENT(0).getText()
                        // + " nao declarado");
                SemanticoUtils.adicionarErroSemantico(ctx.start, "identificador " + nomeVar + " nao declarado");
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
