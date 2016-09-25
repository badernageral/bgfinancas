/*
Copyright 2012-2015 Jose Robson Mariano Alves

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
import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.Grafico;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.tipo.Funcao;
import badernageral.bgfinancas.biblioteca.utilitario.Datas;
import badernageral.bgfinancas.biblioteca.utilitario.Erro;
import badernageral.bgfinancas.biblioteca.utilitario.Validar;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public final class Receita extends Banco<Receita> implements Modelo, Grafico {
    
    private static final String MODULO = RAIZ+"/modulo/receita";
    
    public static final String FXML = MODULO+"/Receita.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/ReceitaFormulario.fxml";

    public static final String TABELA = "receitas";

    private final Coluna idReceita = new Coluna(TABELA, "id_receita");
    private final Coluna idConta = new Coluna(TABELA, "id_conta");
    private final Coluna idItem = new Coluna(TABELA, "id_item");
    private final Coluna descricao = new Coluna(TABELA, "descricao", "");
    private final Coluna valor = new Coluna(TABELA, "valor");
    private final Coluna data = new Coluna(TABELA, "data");
    private final Coluna hora = new Coluna(TABELA, "hora");
    
    private final Coluna idContaInner = new Coluna(Conta.TABELA, "id_conta");
    private final Coluna nomeConta = new Coluna(Conta.TABELA, "nome", "", "nome_conta");

    private final Coluna idItemInner = new Coluna(ReceitaItem.TABELA, "id_item");
    private final Coluna idCategoria = new Coluna(ReceitaItem.TABELA, "id_categoria");
    private final Coluna nomeItem = new Coluna(ReceitaItem.TABELA, "nome", "", "nome_item");
    
    private final Coluna idCategoriaInner = new Coluna(ReceitaCategoria.TABELA, "id_categoria");
    private final Coluna nomeCategoria = new Coluna(ReceitaCategoria.TABELA, "nome", "", "nome_categoria");
    
    private final Coluna sumValor = new Coluna(TABELA, "valor", "sum_valor", Funcao.SOMAR);
    
    public Receita(){ }
    
    public Receita(String idItem, String nomeItem, String nomeCategoria, String descricao, String valor){
        this.idItem.setValor(idItem);
        this.nomeItem.setValor(nomeItem);
        this.nomeCategoria.setValor(nomeCategoria);
        this.descricao.setValor(descricao);
        this.valor.setValor(valor);
    }   

    public Receita(String idReceita, String idConta, String idItem, String descricao, String valor, LocalDate data, String hora){
        this.idReceita.setValor(idReceita);
        this.idConta.setValor(idConta);
        this.idItem.setValor(idItem);
        this.descricao.setValor(descricao);
        this.valor.setValor(valor);
        this.data.setValor(Datas.toSqlData(data));
        this.hora.setValor(hora);
    }
    
    @Override
    protected Receita instanciar(ResultSet rs) throws SQLException{
        Receita d = new Receita(
            rs.getString(idReceita.getColuna()),
            rs.getString(idConta.getColuna()),
            rs.getString(idItem.getColuna()),
            rs.getString(descricao.getColuna()),
            rs.getString(valor.getColuna()),
            rs.getDate(data.getColuna()).toLocalDate(),
            rs.getString(hora.getColuna())
        );
        d.setNomeConta(rs.getString(nomeConta.getAliasColuna()));
        d.setNomeItem(rs.getString(nomeItem.getAliasColuna()));
        d.setNomeCategoria(rs.getString(nomeCategoria.getAliasColuna()));
        return d;
    }
      
    @Override
    public boolean cadastrar(){
        return this.insert(idConta, idItem, descricao, valor, data, hora).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(idConta, descricao, valor, data).where(idReceita, "=").commit();
    }
    
    @Override
    public boolean excluir(){
        return this.delete(idReceita, "=").commit();
    }
    
    @Override
    public Usuario consultar() {
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    @Override
    public ObservableList<Receita> listar(){
        try{
            this.select(idReceita, idConta, idItem, descricao, valor, data, hora, nomeConta, nomeItem, nomeCategoria);
            this.inner(idConta, idContaInner);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            this.where(descricao, "LIKE", "(");
            this.or(nomeConta, "LIKE");
            this.or(nomeItem, "LIKE");
            this.or(data, "=");
            this.or(valor, "=");
            this.or(nomeCategoria, "LIKE", ")");
            if(idItem.getValor() != null){
                this.and(idItem, "=");
            }
            if(idCategoria.getValor() != null){
                this.and(idCategoria, "=");
            }
            if(idConta.getValor() != null){
                this.and(idConta, "=");
            }
            this.orderby("DESC", data, hora);
            ResultSet rs = this.query();
            if(rs != null){
                List<Receita> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<Receita> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public String getIdReceita() {
        return idReceita.getValor();
    }
    
    public String getIdConta() {
        return idConta.getValor();
    }
    
    public String getIdItem() {
        return idItem.getValor();
    }
    
    public String getDescricao() {
        return descricao.getValor();
    }
    
    public String getValor() {
        return valor.getValor();
    }
    
    public String getData() {
        return badernageral.bgfinancas.biblioteca.utilitario.Datas.getDataExibicao(data.getValor());
    }
    
    public LocalDate getDataLocal() {
        return badernageral.bgfinancas.biblioteca.utilitario.Datas.getLocalDate(data.getValor());
    }
    
    public String getHora() {
        return hora.getValor();
    }
    
    public String getDataHora(){
        return getData()+" "+getHora();
    }
    
    public String getNomeConta() {
        return nomeConta.getValor();
    }
    
    public String getNomeItem() {
        return nomeItem.getValor();
    }
    
    public String getNomeCategoria() {
        return nomeCategoria.getValor();
    }
    
    public Receita setIdItem(String id_item) {
        this.idItem.setValor(id_item);
        return getThis();
    }
    
    public void setValor(String valor) {
        this.valor.setValor(valor);
    }
    
    public void setDescricao(String descricao) {
        this.descricao.setValor(descricao);
    }
    
    public Receita setIdConta(Categoria conta) {
        this.idConta.setValor(conta.getIdCategoria());
        return getThis();
    }
        
    public void setData(LocalDate data) {
        this.data.setValor(Datas.toSqlData(data));
    }
    
    public void setHora(String hora) {
        this.hora.setValor(hora);
    }
    
    public void setNomeConta(String conta) {
        this.nomeConta.setValor(conta);
    }
    
    public void setNomeItem(String item) {
        this.nomeItem.setValor(item);
    }
    
    public void setNomeCategoria(String categoria) {
        this.nomeCategoria.setValor(categoria);
    }
    
    public Receita setIdCategoria(String idCategoria) {
        this.idCategoria.setValor(idCategoria);
        return this;
    }
    
    public Receita setFiltro(String filtro){
        descricao.setValor(filtro);
        nomeConta.setValor(filtro);
        nomeItem.setValor(filtro);
        nomeCategoria.setValor(filtro);
        try{
            LocalDate filtroData = LocalDate.parse(filtro, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
            data.setValor(Datas.toSqlData(filtroData));
        }catch(DateTimeParseException e){}
        try{
            Validar.decimal(filtro, null);
            valor.setValor(filtro);
        }catch(Erro e){}
        return this;
    }
    
    public ObservableList<Receita> getRelatorioMensal(LocalDate hoje){
        try{
            LocalDate inicio = hoje.withDayOfMonth(1);
            LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
            this.select(sumValor, nomeCategoria);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            data.setValor(Datas.toSqlData(inicio));
            this.and(data, ">=");
            data.setValor(Datas.toSqlData(fim));
            this.and(data, "<=");
            this.groupBy(nomeCategoria);
            this.orderby(nomeCategoria);
            ResultSet rs = this.query();
            if(rs != null){
                List<Receita> linhas = new ArrayList<>();
                while(rs.next()){
                    Receita r = new Receita();
                    r.setValor(rs.getString(sumValor.getAliasColuna()));
                    r.setNomeCategoria(rs.getString(nomeCategoria.getAliasColuna()));
                    linhas.add(r);
                }
                ObservableList<Receita> resultado = FXCollections.observableList(linhas);
                return resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    @Override
    public List<XYChart.Series<String,Number>> getRelatorioMensalBarras(LocalDate inicio, LocalDate fim, String nome_categoria, String id_categoria, Integer tipo_categoria){
        try{
            Coluna coluna = nomeCategoria;
            if(nome_categoria != null){
                coluna = nomeItem;
                nomeCategoria.setValor(nome_categoria);
            }
            if(id_categoria != null){
                idConta.setValor(id_categoria);
            }
            this.select(sumValor, coluna);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            data.setValor(Datas.toSqlData(inicio));
            this.and(data, ">=");
            data.setValor(Datas.toSqlData(fim));
            this.and(data, "<=");
            if(nome_categoria != null){
                this.and(nomeCategoria, "=");
            }
            if(id_categoria != null){
                this.and(idConta, "=");
            }
            this.groupBy(coluna);
            this.orderby(coluna);
            ResultSet rs = this.query();
            if(rs != null){
                List<XYChart.Series<String,Number>> categorias = new ArrayList<>();
                while(rs.next()){
                    XYChart.Series<String,Number> categoria = new XYChart.Series<>();
                    categoria.setName(rs.getString(coluna.getAliasColuna()));
                    categoria.getData().add(new XYChart.Data<>("",rs.getDouble(sumValor.getAliasColuna())));
                    categorias.add(categoria);
                }
                return categorias;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }

    @Override
    protected Receita getThis() {
        return this;
    }

}
