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
import badernageral.bgfinancas.biblioteca.contrato.Categoria;
import badernageral.bgfinancas.biblioteca.contrato.Grafico;
import badernageral.bgfinancas.biblioteca.sistema.Janela;
import badernageral.bgfinancas.biblioteca.tipo.Funcao;
import badernageral.bgfinancas.biblioteca.utilitario.Datas;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public final class Despesa extends Banco<Despesa> implements Modelo, Grafico {
    
    private static final String MODULO = RAIZ+"/modulo/despesa";
    
    public static final String FXML = MODULO+"/Despesa.fxml";
    public static final String FXML_CADASTRO_MULTIPLO = MODULO+"/DespesaCadastroMultiplo.fxml";
    public static final String FXML_DESPESAS_AGENDADAS = MODULO+"/DespesasAgendadas.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/DespesaFormulario.fxml";
    public static final String FXML_MODAL_DESPESA = MODULO+"/ModalDespesa.fxml";

    public static final String TABELA = "despesas";

    private final Coluna idDespesa = new Coluna(TABELA, "id_despesa");
    private final Coluna idConta = new Coluna(TABELA, "id_conta");
    private final Coluna idItem = new Coluna(TABELA, "id_item");
    private final Coluna quantidade = new Coluna(TABELA, "quantidade");
    private final Coluna valor = new Coluna(TABELA, "valor");
    private final Coluna data = new Coluna(TABELA, "data");
    private final Coluna hora = new Coluna(TABELA, "hora");
    private final Coluna agendada = new Coluna(TABELA, "agendada");
    private final Coluna parcela = new Coluna(TABELA, "parcela");
    
    private final Coluna idContaInner = new Coluna(Conta.TABELA, "id_conta");
    private final Coluna nomeConta = new Coluna(Conta.TABELA, "nome", "", "nome_conta");

    private final Coluna idItemInner = new Coluna(DespesaItem.TABELA, "id_item");
    private final Coluna idCategoria = new Coluna(DespesaItem.TABELA, "id_categoria");
    private final Coluna nomeItem = new Coluna(DespesaItem.TABELA, "nome", "", "nome_item");
    
    private final Coluna idCategoriaInner = new Coluna(DespesaCategoria.TABELA, "id_categoria");
    private final Coluna nomeCategoria = new Coluna(DespesaCategoria.TABELA, "nome", "", "nome_categoria");
    
    private final Coluna sumValor = new Coluna(TABELA, "valor", "sum_valor", Funcao.SOMAR);
    private final Coluna sumQuantidade = new Coluna(TABELA, "quantidade", "sum_quantidade", Funcao.SOMAR);
    private final Coluna mes = new Coluna(TABELA, "data", "mes", Funcao.MES);
    private final Coluna ano = new Coluna(TABELA, "data", "ano", Funcao.ANO);
    
    private Boolean somenteAgendamento = false;
    private Boolean especificarMesAno = false;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    
    public Despesa(){ }
      
    public Despesa(String idItem, String nomeItem, String nomeCategoria, String quantidade, String valor){
        this.idItem.setValor(idItem);
        this.nomeItem.setValor(nomeItem);
        this.nomeCategoria.setValor(nomeCategoria);
        this.quantidade.setValor(quantidade);
        this.valor.setValor(valor);
    }   

    public Despesa(String idDespesa, String idConta, String idItem, String quantidade, String valor, LocalDate data, String hora, String agendada){
        this.idDespesa.setValor(idDespesa);
        this.idConta.setValor(idConta);
        this.idItem.setValor(idItem);
        this.quantidade.setValor(quantidade);
        this.valor.setValor(valor);
        this.data.setValor(Datas.toSqlData(data));
        this.hora.setValor(hora);
        this.agendada.setValor(agendada);
    }
    
    @Override
    protected Despesa instanciar(ResultSet rs) throws SQLException{
        Despesa d = new Despesa(
            rs.getString(idDespesa.getColuna()),
            rs.getString(idConta.getColuna()),
            rs.getString(idItem.getColuna()),
            rs.getString(quantidade.getColuna()),
            rs.getString(valor.getColuna()),
            rs.getDate(data.getColuna()).toLocalDate(),
            rs.getString(hora.getColuna()),
            rs.getString(agendada.getColuna())
        );
        d.setParcela(rs.getString(parcela.getColuna()));
        d.setNomeConta(rs.getString(nomeConta.getAliasColuna()));
        d.setNomeItem(rs.getString(nomeItem.getAliasColuna()));
        d.setNomeCategoria(rs.getString(nomeCategoria.getAliasColuna()));
        return d;
    }
       
    @Override
    public boolean cadastrar(){
        return this.insert(idConta, idItem, quantidade, valor, data, hora, agendada, parcela).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(idConta, quantidade, valor, data, agendada).where(idDespesa, "=").commit();
    }
    
    @Override
    public boolean excluir(){
        return this.delete(idDespesa, "=").commit();
    }
    
    @Override
    public Usuario consultar() {
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    @Override
    public ObservableList<Despesa> listar(){
        try{
            this.select(idDespesa, idConta, idItem, quantidade, valor, data, hora, agendada, parcela, nomeConta, nomeItem, nomeCategoria);
            this.inner(idConta, idContaInner);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            this.where(nomeConta, "LIKE", "(");
            this.or(nomeItem, "LIKE");
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
            agendada.setValor("1");
            if(somenteAgendamento){
                this.and(agendada, "=");
            }else{
                this.and(agendada, "<>");
            }
            if(especificarMesAno){
                data.setValor(Datas.toSqlData(dataInicio));
                this.and(data, ">=");
                data.setValor(Datas.toSqlData(dataFim));
                this.and(data, "<=");
            }
            if(somenteAgendamento){
                this.orderby(data, nomeItem);
            }else{
                this.orderby("DESC", data, hora);
            }
            ResultSet rs = this.query();
            if(rs != null){
                List<Despesa> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<Despesa> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
     public boolean isDespesasAtrasadas(){
        try{
            this.select(idDespesa);
            agendada.setValor("1");
            this.where(agendada, "=");
            data.setValor(Datas.toSqlData(LocalDate.now()));
            this.and(data, "<=");
            data.setValor(Configuracao.getPropriedade("data_notificacao"));
            this.and(data, ">");
            this.orderby("DESC", data, hora);
            ResultSet rs = this.query();
            if(rs != null){
                return rs.next();
            }else{
                return false;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return false;
        }
    }
    
    public String getIdDespesa() {
        return idDespesa.getValor();
    }
    
    public String getIdConta() {
        return idConta.getValor();
    }
    
    public String getIdItem() {
        return idItem.getValor();
    }
    
    public String getQuantidade() {
        return quantidade.getValor();
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
    
    public String getAgendada() {
        return agendada.getValor();
    }
    
    public String getDataHora(){
        return getData()+" "+getHora();
    }
    
    public String getNomeConta() {
        return nomeConta.getValor();
    }
    
    public String getNomeItem() {
        if(agendada.getValor().equals("1") && parcela.getValor()!=null){
            return nomeItem.getValor()+" - "+parcela.getValor();
        }else{
            return nomeItem.getValor();
        }
    }
    
    public String getNomeCategoria() {
        return nomeCategoria.getValor();
    }
    
    public void setValor(String valor) {
        this.valor.setValor(valor);
    }
    
    public Despesa setIdItem(String id_item) {
        this.idItem.setValor(id_item);
        return getThis();
    }
    
    public void setQuantidade(String quantidade) {
        this.quantidade.setValor(quantidade);
    }
    
    public Despesa setIdConta(Categoria conta) {
        this.idConta.setValor(conta.getIdCategoria());
        return getThis();
    }
    
    public void setData(LocalDate data) {
        this.data.setValor(Datas.toSqlData(data));
    }
    
    public void setHora(String hora) {
        this.hora.setValor(hora);
    }
    
    public void setAgendada(String agendada) {
        this.agendada.setValor(agendada);
    }
    
    public void setParcela(String parcela) {
        this.parcela.setValor(parcela);
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
    
    public Despesa setIdCategoria(String idCategoria) {
        this.idCategoria.setValor(idCategoria);
        return this;
    }
    
    public Despesa setSomenteAgendamento() {
        this.somenteAgendamento = true;
        return getThis();
    }
    
    public Despesa setMesAno(int mes, int ano) {
        this.especificarMesAno = true;
        this.dataInicio = LocalDate.of(ano, mes, 1);
        this.dataFim = dataInicio.withDayOfMonth(dataInicio.lengthOfMonth());
        return getThis();
    }
    public Despesa setFiltro(String filtro){
        nomeConta.setValor(filtro);
        nomeItem.setValor(filtro);
        nomeCategoria.setValor(filtro);
        return this;
    }
    
    public String getSumValor(){
        try{
            if(idCategoria.getValor() != null){
                LocalDate hoje = LocalDate.now();
                LocalDate inicio = hoje.withDayOfMonth(1);
                LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
                this.select(sumValor);
                this.inner(idItem, idItemInner);
                this.inner(idCategoria, idCategoriaInner);
                this.where(idCategoria, "=");
                data.setValor(Datas.toSqlData(inicio));
                this.and(data, ">=");
                data.setValor(Datas.toSqlData(fim));
                this.and(data, "<=");
                agendada.setValor("1");
                this.and(agendada, "<>");
                ResultSet rs = this.query();
                if(rs.next()){
                    String resultado = rs.getString(sumValor.getAliasColuna());
                    if(resultado!=null){
                        return resultado;
                    }
                }
            }
            return "0.0";
        }catch(SQLException ex){
            Janela.showException(ex);
            return "0.0";
        }
    }
    
    public ObservableList<Despesa> getRelatorioMensal(){
        try{
            LocalDate hoje = LocalDate.now();
            LocalDate inicio = hoje.withDayOfMonth(1);
            LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
            this.select(sumValor, nomeCategoria);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            data.setValor(Datas.toSqlData(inicio));
            this.and(data, ">=");
            data.setValor(Datas.toSqlData(fim));
            this.and(data, "<=");
            agendada.setValor("1");
            this.and(agendada, "<>");
            this.groupBy(nomeCategoria);
            this.orderby(nomeCategoria);
            ResultSet rs = this.query();
            if(rs != null){
                List<Despesa> linhas = new ArrayList<>();
                while(rs.next()){
                    Despesa d = new Despesa();
                    d.setValor(rs.getString(sumValor.getAliasColuna()));
                    d.setNomeCategoria(rs.getString(nomeCategoria.getAliasColuna()));
                    linhas.add(d);
                }
                ObservableList<Despesa> resultado = FXCollections.observableList(linhas);
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
    public List<XYChart.Series<String,Number>> getRelatorioMensalBarras(LocalDate inicio, LocalDate fim, String nome_categoria){
        try{
            Coluna coluna = nomeCategoria;
            if(nome_categoria != null){
                coluna = nomeItem;
                nomeCategoria.setValor(nome_categoria);
            }
            this.select(sumValor, sumQuantidade, coluna);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            data.setValor(Datas.toSqlData(inicio));
            this.and(data, ">=");
            data.setValor(Datas.toSqlData(fim));
            this.and(data, "<=");
            agendada.setValor("1");
            this.and(agendada, "<>");
            if(nome_categoria != null){
                this.and(nomeCategoria, "=");
            }
            this.groupBy(coluna);
            this.orderby(coluna);
            ResultSet rs = this.query();
            if(rs != null){
                List<XYChart.Series<String,Number>> categorias = new ArrayList<>();
                while(rs.next()){
                    XYChart.Series<String,Number> categoria = new XYChart.Series<>();
                    StringBuilder descricao = new StringBuilder(rs.getString(coluna.getAliasColuna()));
                    if(nome_categoria != null){
                        descricao.insert(0, rs.getString(sumQuantidade.getAliasColuna())+" ");
                    }
                    categoria.setName(descricao.toString());
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
    
    public List<XYChart.Series<String,Number>> getRelatorioMensalLinhas(LocalDate inicio, LocalDate fim, List<String> categoriasSelecionadas){
        try{
            this.select(sumValor, mes, ano, nomeCategoria);
            this.inner(idItem, idItemInner);
            this.inner(idCategoria, idCategoriaInner);
            data.setValor(Datas.toSqlData(inicio));
            this.and(data, ">=");
            data.setValor(Datas.toSqlData(fim));
            this.and(data, "<=");
            agendada.setValor("1");
            this.and(agendada, "<>");
            if (categoriasSelecionadas.size() > 0) {
                this.andIN(idCategoria, categoriasSelecionadas);
            }
            this.groupBy(nomeCategoria, mes, ano);
            this.orderby(nomeCategoria, mes, ano);
            ResultSet rs = this.query();
            if(rs != null){
                List<XYChart.Series<String,Number>> categorias = new ArrayList<>();
                String ultimaCategoria = "";
                XYChart.Series<String,Number> categoria = new XYChart.Series<>();
                while(rs.next()){
                    String novaCategoria = rs.getString(nomeCategoria.getAliasColuna());
                    if(!ultimaCategoria.equals(novaCategoria)){
                        if(!ultimaCategoria.equals("")){
                            categorias.add(categoria);
                        }
                        ultimaCategoria = novaCategoria;
                        categoria = new XYChart.Series<>();
                        categoria.setName(novaCategoria);
                    }
                    String descricao = idioma.getNomeMes(rs.getInt(mes.getAliasColuna())).substring(0,3)+"/"+rs.getString(ano.getAliasColuna());
                    categoria.getData().add(new XYChart.Data<>(descricao,rs.getDouble(sumValor.getAliasColuna())));
                }
                categorias.add(categoria);
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
    protected Despesa getThis() {
        return this;
    }

}
