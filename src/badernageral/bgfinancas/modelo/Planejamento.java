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
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Planejamento extends Banco<Planejamento> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/planejamento";
    
    public static final String FXML = MODULO+"/Planejamento.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/PlajenamentoFormulario.fxml";

    public static final String TABELA = "planejamento";
    
    private final Coluna idPlajenamento = new Coluna(TABELA, "id_planejamento");
    private final Coluna mes = new Coluna(TABELA, "mes");
    private final Coluna ano = new Coluna(TABELA, "ano");
    private final Coluna valor = new Coluna(TABELA, "valor", "0.0");
    
    public Planejamento(Integer mes, Integer ano){ 
        this.mes.setValor(mes.toString());
        this.ano.setValor(ano.toString());
    }
    
    public Planejamento(String idPlajenamento, String mes, String ano, String valor){
        this.idPlajenamento.setValor(idPlajenamento);
        this.mes.setValor(mes);
        this.ano.setValor(ano);
        this.valor.setValor(valor);
    }
    
    @Override
    protected Planejamento instanciar(ResultSet rs) throws SQLException{
        return new Planejamento(
            rs.getString(idPlajenamento.getColuna()),
            rs.getString(mes.getColuna()),
            rs.getString(ano.getColuna()),
            rs.getString(valor.getColuna())
        );
    }
    
    @Override
    public boolean cadastrar(){
        return this.insert(mes, ano, valor).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(valor).where(idPlajenamento, "=").commit();
    }
    
    @Override
    public boolean excluir(){
        return this.delete(idPlajenamento, "=").commit();
    }
    
    @Override
    public Planejamento consultar() {
        try{
            this.select(idPlajenamento, mes, ano, valor);
            this.where(mes, "=");
            this.and(ano, "=");
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
    public ObservableList<Planejamento> listar(){
        try{
            this.select(idPlajenamento, mes, ano, valor);
            if(!mes.getValor().equals("") && !ano.getValor().equals("")){
                this.where(mes, "=");
                this.and(ano, "=");
            }
            this.orderby(ano, mes);
            ResultSet rs = this.query();
            if(rs != null){
                List<Planejamento> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<Planejamento> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public String getIdPlajenamento() {
        return idPlajenamento.getValor();
    }
    
    public String getAno() {
        return ano.getValor();
    }
    
    public String getMes() {
        return mes.getValor();
    }
    
    public String getValor() {
        return valor.getValor();
    }
    
    public void setValor(String valor){
        this.valor.setValor(valor);
    }
    
    public Planejamento setAno(String ano){
        this.ano.setValor(ano);
        return this;
    }
    
    public Planejamento setMes(String mes){
        this.mes.setValor(mes);
        return this;
    }

    @Override
    protected Planejamento getThis() {
        return this;
    }

}
