/*
Copyright 2012-2018 Jose Robson Mariano Alves

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
package io.github.badernageral.bgfinancas.modelo;

import io.github.badernageral.bgfinancas.biblioteca.banco.Banco;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Colavel;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Modelo;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public final class Planejamento extends Banco<Planejamento> implements Modelo, Colavel {
    
    private static final String MODULO = RAIZ+"/modulo/planejamento";
    
    public static final String FXML = MODULO+"/Planejamento.fxml";
    public static final String FXML_MODAL_PAGAR = MODULO + "/ModalPagar.fxml";
    
    private Despesa despesa = null;
    private Receita receita = null;
    
    public Planejamento(){ }
    
    public Planejamento(Despesa d){
        this.despesa = d;
    }
    
    public Planejamento(Receita r){
        this.receita = r;
    }
    
    public Planejamento(Despesa d, Receita r){
        this.despesa = d;
        this.receita = r;
    }
    
    public ObservableList<Planejamento> listar(int mes, int ano, Categoria cartao){
        ObservableList<Planejamento> itens = FXCollections.observableList(new ArrayList<>());
        new Despesa().setSomenteAgendamento().setMesAno(mes, ano).setIdCartaoCredito(cartao).listar().forEach(d -> itens.add(new Planejamento(d)));
        new Receita().setSomenteAgendamento().setMesAno(mes, ano).listar().forEach(r -> itens.add(new Planejamento(r)));
        itens.sort((Planejamento p1, Planejamento p2) -> p1.getData().compareTo(p2.getData()));
        return itens;
    }
    
    @Override
    public Planejamento getClone() {
        return new Planejamento(despesa, receita);
    }
    
    @Override
    protected Planejamento instanciar(ResultSet rs) throws SQLException{
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    @Override
    public boolean cadastrar(){
        return (isDespesa()) ? despesa.cadastrar(): receita.cadastrar();
    }
    
    @Override
    public boolean alterar(){
        return (isDespesa()) ? despesa.alterar(): receita.alterar();
    }
    
    @Override
    public boolean excluir(){
        return (isDespesa()) ? despesa.excluir(): receita.excluir();
    }
    
    @Override
    public Planejamento consultar() {
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    @Override
    public ObservableList<Planejamento> listar(){
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    @Override
    protected Planejamento getThis() {
        return this;
    }
    
    public boolean isDespesa(){
        return despesa!=null;
    }
    
    public String getIsDespesa(){
        return (despesa!=null) ? "1" : "0";
    }
    
    public Despesa getDespesa(){
        return despesa;
    }
    
    public Receita getReceita(){
        return receita;
    }
    
    public String getId() {
        return (isDespesa()) ? despesa.getIdDespesa() : receita.getIdReceita();
    }

    public String getIdConta() {
        return (isDespesa()) ? despesa.getIdConta(): receita.getIdConta();
    }

    public String getIdItem() {
        return (isDespesa()) ? despesa.getIdItem(): receita.getIdItem();
    }

    public BigDecimal getQuantidade() {
        return (isDespesa()) ? despesa.getQuantidade(): null;
    }
    
    public String getDescricao() {
        return (isDespesa()) ? null: receita.getDescricao();
    }

    public BigDecimal getValor() {
        return (isDespesa()) ? despesa.getValor(): receita.getValor();
    }

    @Override
    public LocalDate getData() {
        return (isDespesa()) ? despesa.getData(): receita.getData();
    }

    public LocalTime getHora() {
        return (isDespesa()) ? despesa.getHora(): receita.getHora();
    }

    public LocalDateTime getDataHora() {
        return (isDespesa()) ? despesa.getDataHora(): receita.getDataHora();
    }
    
    public String getAgendada() {
        return (isDespesa()) ? despesa.getAgendada(): receita.getAgendada();
    }

    public String getNomeConta() {
        return (isDespesa()) ? despesa.getNomeConta(): receita.getNomeConta();
    }

    public String getIdCartaoCredito() {
        return (isDespesa()) ? despesa.getIdCartaoCredito(): null;
    }

    public String getNomeCartaoCredito() {
        return (isDespesa()) ? despesa.getNomeCartaoCredito(): null;
    }

    public String getNomeItem() {
        return (isDespesa()) ? despesa.getNomeItem(): receita.getNomeItem();
    }

    public String getNomeCategoria() {
        return (isDespesa()) ? despesa.getNomeCategoria(): receita.getNomeCategoria();
    }

    @Override
    public void setData(String data) {
       if(isDespesa()){
           despesa.setData(data);
       }else{
           receita.setData(data);
       }
    }
    
    public void setIdConta(String id_conta) {
       if(isDespesa()){
           despesa.setIdConta(id_conta);
       }else{
           receita.setIdConta(id_conta);
       }
    }
    
    public void setAgendada(String agendada) {
       if(isDespesa()){
           despesa.setAgendada(agendada);
       }else{
           receita.setAgendada(agendada);
       }
    }
    
    public static void prepararColunaTipo(TableColumn colunaTipo){
        colunaTipo.setMinWidth(35);
        colunaTipo.setMaxWidth(35);
        colunaTipo.setCellFactory(coluna -> {
            return new TableCell<Planejamento, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        if(item.equals("1")){
                            setStyle("-fx-background-image: url(\"io/github/badernageral/bgfinancas/recursos/imagem/outros/gastos.png\");-fx-background-repeat: no-repeat;-fx-background-position: center center");
                        }else{
                            setStyle("-fx-background-image: url(\"io/github/badernageral/bgfinancas/recursos/imagem/outros/credito.png\");-fx-background-repeat: no-repeat;-fx-background-position: center center");
                        }
                    }else{
                        setStyle("-fx-background-image: none;");
                    }
                }
            };
        });
    }

}
