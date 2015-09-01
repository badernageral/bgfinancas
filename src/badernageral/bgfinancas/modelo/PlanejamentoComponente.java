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
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class PlanejamentoComponente extends Banco<PlanejamentoComponente> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/planejamento";
    
    public static final String FXML = MODULO+"/PlanejamentoComponente.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/PlajenamentoComponenteFormulario.fxml";
    public static final String FXML_MODAL_ITEM = MODULO+"/ModalPlanejamentoItem.fxml";

    public static final String TABELA = "planejamento_componentes";
    
    private final Coluna idPlajenamento = new Coluna(TABELA, "id_planejamento", "");
    private final Coluna idItem = new Coluna(TABELA, "id_item");
    private final Coluna valor = new Coluna(TABELA, "valor");
    
    private final Coluna idPlanejamentoInner = new Coluna(Planejamento.TABELA, "id_planejamento");
    private final Coluna mes = new Coluna(Planejamento.TABELA, "mes", "");
    private final Coluna ano = new Coluna(Planejamento.TABELA, "ano", "");
    
    private final Coluna idItemInner = new Coluna(PlanejamentoItem.TABELA, "id_item");
    private final Coluna nomeItem = new Coluna(PlanejamentoItem.TABELA, "nome", "", "nome_item");
    
    public PlanejamentoComponente(){ this("","",""); }
    
    public PlanejamentoComponente(String idPlajenamento, String idItem, String valor){
        this.idPlajenamento.setValor(idPlajenamento);
        this.idItem.setValor(idItem);
        this.valor.setValor(valor);
    }
    
    public PlanejamentoComponente(String idPlajenamento, String idItem, String valor, String mes, String ano, String nomeItem){
        this.idPlajenamento.setValor(idPlajenamento);
        this.idItem.setValor(idItem);
        this.valor.setValor(valor);
        this.mes.setValor(mes);
        this.ano.setValor(ano);
        this.nomeItem.setValor(nomeItem);
    }
    
    @Override
    protected PlanejamentoComponente instanciar(ResultSet rs) throws SQLException{
        return new PlanejamentoComponente(
            rs.getString(idPlajenamento.getColuna()),
            rs.getString(idItem.getColuna()),
            rs.getString(valor.getColuna()),
            rs.getString(mes.getColuna()),
            rs.getString(ano.getColuna()),
            rs.getString(nomeItem.getAliasColuna())
        );
    }
    
    @Override
    public boolean cadastrar(){
        return this.insert(idPlajenamento, idItem, valor).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(valor).where(idPlajenamento, "=").and(idItem, "=").commit();
    }
    
    @Override
    public boolean excluir(){
        return this.delete(idPlajenamento, "=").and(idItem, "=").commit();
    }
    
    @Override
    public PlanejamentoComponente consultar() {
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    @Override
    public ObservableList<PlanejamentoComponente> listar(){
        try{
            this.select(idPlajenamento, idItem, valor, mes, ano, nomeItem);
            this.inner(idPlajenamento, idPlanejamentoInner);
            this.inner(idItem, idItemInner);
            if(!idPlajenamento.getValor().equals("")){
                this.where(idPlajenamento, "=");
            }else if(!mes.getValor().equals("") && !ano.getValor().equals("")){
                this.where(mes, "=").and(ano, "=");
            }else if(!idItem.getValor().equals("")){
                this.where(idItem, "=");
            }
            this.orderby(nomeItem);
            ResultSet rs = this.query();
            if(rs != null){
                List<PlanejamentoComponente> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<PlanejamentoComponente> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(Exception ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public String getIdPlajenamento() {
        return idPlajenamento.getValor();
    }
    
    public String getIdItem() {
        return idItem.getValor();
    }
    
    public String getNomeItem() {
        return nomeItem.getValor();
    }
    
    public String getValor() {
        return valor.getValor();
    }
    
    public void setValor(String valor){
        this.valor.setValor(valor);
    }
    
    public PlanejamentoComponente setIdItem(String idItem){
        this.idItem.setValor(idItem);
        return getThis();
    }
    
    public PlanejamentoComponente setIdPlanejamento(String idPlanejamento){
        this.idPlajenamento.setValor(idPlanejamento);
        return this;
    }
    
    public PlanejamentoComponente setMes(Integer mes){
        this.mes.setValor(mes.toString());
        return this;
    }
    
    public PlanejamentoComponente setAno(Integer ano){
        this.ano.setValor(ano.toString());
        return this;
    }
    
    public List<String> calcularSaldo(ObservableList<PlanejamentoComponente> itens, String salario){
        BigDecimal saldoReceita = new BigDecimal(salario);
        BigDecimal saldoGastos = new BigDecimal("0.0");
        for(PlanejamentoComponente p : itens){
            saldoGastos = saldoGastos.add(new BigDecimal(p.getValor()));
        }
        BigDecimal saldoFinal = saldoReceita.subtract(saldoGastos);
        return Arrays.asList(saldoGastos.toString(), saldoFinal.toString());
    }

    @Override
    protected PlanejamentoComponente getThis() {
        return this;
    }

}
