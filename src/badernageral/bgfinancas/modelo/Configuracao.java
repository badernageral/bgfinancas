/*
Copyright 2015 Jose Robson Mariano Alves

This file is part of bgfinancas.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.

*/

package badernageral.bgfinancas.modelo;

import badernageral.bgfinancas.biblioteca.banco.Banco;
import badernageral.bgfinancas.biblioteca.contrato.Modelo;
import badernageral.bgfinancas.biblioteca.banco.Coluna;
import badernageral.bgfinancas.biblioteca.banco.Conexao;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.ObservableList;

public final class Configuracao extends Banco<Configuracao> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/utilitario";
    
    public static final String FXML_FORMULARIO = MODULO+"/ConfiguracaoFormulario.fxml";

    public static final String TABELA = "configuracoes";
    
    private final Coluna nome = new Coluna(TABELA, "nome");
    private final Coluna valor = new Coluna(TABELA, "valor");
    
    public Configuracao(){ }
    
    public Configuracao(String nome, String valor){
        this.nome.setValor(nome);
        this.valor.setValor(valor);
    }
    
    @Override
    protected Configuracao instanciar(ResultSet rs) throws SQLException{
        return new Configuracao(
            rs.getString(nome.getColuna()),
            rs.getString(valor.getColuna())
        );
    }
    
    @Override
    public boolean cadastrar(){
        return this.insert(nome, valor).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(valor).where(nome, "=").commit();
    }
    
    @Override
    public boolean excluir(){
        System.out.println(idioma.getMensagem("nao_implementado"));
        return false;
    }
    
    @Override
    public Configuracao consultar() {
        try{
            this.select(nome, valor);
            this.where(nome, "=");
            ResultSet rs = this.query();
            if(rs != null && rs.next()){
                return instanciar(rs);
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    @Override
    public ObservableList<Configuracao> listar(){
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    public String getNome() {
        return nome.getValor();
    }
    
    public String getValor() {
        return valor.getValor();
    }
    
    public Configuracao setNome(String nome){
        this.nome.setValor(nome);
        return getThis();
    }
    
    public void setValor(String valor){
        this.valor.setValor(valor);
    }
    
    @Override
    protected Configuracao getThis() {
        return this;
    }
    
    public static void verificar(){
        try {
            Conexao banco = Conexao.getInstance();
            // Tabela de configuracao            
            banco.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_TYPE='TABLE' AND (TABLE_NAME='configuracoes' OR TABLE_NAME='CONFIGURACOES')");
            if(!banco.getResultSet().next()){
                banco.executeUpdate("CREATE TABLE configuracoes(nome VARCHAR(20) PRIMARY KEY, valor VARCHAR(50))");
            }
            // Configuracao: idioma
            if(Configuracao.getPropriedade("idioma") == null){
                new Configuracao("idioma", "pt_BR").cadastrar();
            }
            // Configuracao: moeda
            if(Configuracao.getPropriedade("moeda") == null){
                new Configuracao("moeda", "R$").cadastrar();
            }
        } catch (SQLException ex) {
            Janela.showException(ex);
        }
    }
    
    public static String getPropriedade(String propriedade){
        Configuracao configuracao = new Configuracao().setNome(propriedade).consultar();
        if(configuracao != null){
            return configuracao.getValor();
        }else{
            return null;
        }
    }

}
