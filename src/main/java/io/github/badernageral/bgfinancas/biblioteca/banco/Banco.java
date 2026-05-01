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

package io.github.badernageral.bgfinancas.biblioteca.banco;

import io.github.badernageral.bgfinancas.idioma.Linguagem;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Banco<T extends Banco<T>> {
    
    protected final Linguagem idioma = Linguagem.getInstance();
    
    private final Conexao banco = Conexao.getInstance();
    private final List<String> parametros = new ArrayList<>();
    private StringBuilder sql = new StringBuilder();
    private boolean orderBy = false;
    
    protected abstract T getThis();
    
    protected abstract T instanciar(ResultSet rs) throws SQLException;
    
    private String getParametros(int q){
        String param = "";
        for(int i=0;i<q; i++){
            param = param.concat("?, ");
        }
        param = param.substring(0, param.length()-2);
        return param;
    }
    
    protected T insert(Coluna... colunas){
        sql = new StringBuilder();
        sql.append("INSERT INTO ").append(colunas[0].getTabela()).append(" (");
        for(Coluna coluna : colunas) {
            sql.append(coluna.getColuna()).append(", ");
            parametros.add(coluna.getValor());
        }
        sql.setLength(sql.length() - 2);
        sql.append(") VALUES (").append(getParametros(colunas.length)).append(")");
        return getThis();
    }
    
    protected T update(Coluna... colunas){
        sql = new StringBuilder();
        sql.append("UPDATE ").append(colunas[0].getTabela()).append(" SET ");
        for(Coluna coluna : colunas) {
            sql.append(coluna.getColuna()).append(" = ?, ");
            parametros.add(coluna.getValor());
        }
        sql.setLength(sql.length() - 2);
        return getThis();
    }
    
    protected T delete(Coluna coluna, String operador){
        sql = new StringBuilder();
        sql.append("DELETE FROM ").append(coluna.getTabela()).append(" WHERE ");
        sql.append(coluna.getColuna()).append(" ").append(operador).append(" ? ");
        parametros.add(coluna.getValor());
        return getThis();
    }
    
    protected T select(Coluna... colunas){
        orderBy = false;
        sql = new StringBuilder();
        sql.append("SELECT ");
        for(Coluna coluna : colunas) {
            sql.append(coluna.getTabelaColuna(true)).append(", ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(" FROM ").append(colunas[0].getTabela()).append(" ");
        return getThis();
    }
    
    protected T inner(Coluna coluna, Coluna colunaInner){
        sql.append(" INNER JOIN ").append(colunaInner.getTabela()).append(" ");
        sql.append("ON ").append(coluna.getTabelaColuna(false)).append(" = ").append(colunaInner.getTabelaColuna(false));
        return getThis();
    }
    
    protected T left(Coluna coluna, Coluna colunaInner){
        sql.append(" LEFT JOIN ").append(colunaInner.getTabela()).append(" ");
        sql.append("ON ").append(coluna.getTabelaColuna(false)).append(" = ").append(colunaInner.getTabelaColuna(false));
        return getThis();
    }
    
    protected T where(Coluna coluna, String operador){
        this.where(coluna, operador, null);
        return getThis();
    }
    
    protected T where(Coluna coluna, String operador, String grupo){
        sql.append(" WHERE ");
        this.abreGrupo(grupo);
        sql.append(coluna.getTabelaColuna(false)).append(" ").append(operador).append(" ? ");
        if(operador.equals("LIKE") || operador.equals("like")){
            parametros.add("%"+coluna.getValor()+"%");
        }else{
            parametros.add(coluna.getValor());
        }
        this.fechaGrupo(grupo);
        return getThis();
    }
    
    protected T and(Coluna coluna, String operador){
        this.and(coluna, operador, null);
        return getThis();
    }
    
    protected T and(Coluna coluna, String operador, String grupo){
        sql.append(" AND ");
        this.abreGrupo(grupo);
        sql.append(coluna.getTabelaColuna(false)).append(" ").append(operador).append(" ? ");
        if(operador.equals("LIKE") || operador.equals("like")){
            parametros.add("%"+coluna.getValor()+"%");
        }else{
            parametros.add(coluna.getValor());
        }
        this.fechaGrupo(grupo);
        return getThis();
    }
    
    public T andIN(Coluna coluna, List<String> valores){
        sql.append(" AND ");
        sql.append(coluna.getTabelaColuna(false)).append(" IN ");
        sql.append("(");
        for(String valor : valores){
            sql.append("?,");
            parametros.add(valor);
        }
        sql.setLength(sql.length() - 1);
        sql.append(")");
        return getThis();
    }
    
    public T andIsNull(Coluna coluna){
        sql.append(" AND ");
        sql.append(coluna.getTabelaColuna(false));
        sql.append(" IS NULL ");
        return getThis();
    }
    
    public T andIsNotNull(Coluna coluna){
        sql.append(" AND ");
        sql.append(coluna.getTabelaColuna(false));
        sql.append(" IS NOT NULL ");
        return getThis();
    }
    
    protected T or(Coluna coluna, String operador){
        this.or(coluna, operador, null);
        return getThis();
    }
    
    protected T or(Coluna coluna, String operador, String grupo){
        sql.append(" OR ");
        this.abreGrupo(grupo);
        sql.append(coluna.getTabelaColuna(false)).append(" ").append(operador).append(" ? ");
        if(operador.toUpperCase().equals("LIKE")){
            parametros.add("%"+coluna.getValor()+"%");
        }else{
            parametros.add(coluna.getValor());
        }   
        this.fechaGrupo(grupo);
        return getThis();
    }
    
    protected T groupBy(Coluna... colunas){
        sql.append(" GROUP BY ");
        for(Coluna coluna : colunas) {
            sql.append(coluna.getTabelaColuna(false)).append(", ");
        }
        sql.setLength(sql.length() - 2);
        return getThis();
    }
    
    protected T orderByAsc(Coluna... colunas){
        return orderBy("ASC", colunas);
    }
    
    protected T orderByDesc(Coluna... colunas){
        return orderBy("DESC", colunas);
    }
   
    private T orderBy(String ordem, Coluna... colunas){
        if(!orderBy){
            sql.append(" ORDER BY ");
            orderBy = true;
        }else{
            sql.append(", ");
        }
        for(Coluna coluna : colunas) {
            sql.append(coluna.getTabelaColuna(false)).append(" ").append(ordem).append(", ");
        }
        sql.setLength(sql.length() - 2);
        return getThis();
    }
    
    protected boolean commit(){
        banco.prepararSQL(sql.toString());
        for(int i=0; i<parametros.size(); i++){
            banco.setParametro(i+1, parametros.get(i));
        }
        parametros.clear();
        return banco.finalizarUpdate()>0;
    }
    
    protected ResultSet query(){
        banco.prepararSQL(sql.toString());
        for(int i=0; i<parametros.size(); i++){
            banco.setParametro(i+1, parametros.get(i));
        }
        parametros.clear();
        if(banco.finalizarQuery()){
            return banco.getResultSet();
        }else{
            return null;
        }
    }
    
    private void abreGrupo(String grupo){
        if(grupo != null && grupo.equals("(")){
            sql.append(" ( ");
        }
    }
    
    private void fechaGrupo(String grupo){
        if(grupo != null && grupo.equals(")")){
            sql.append(" ) ");
        }
    }
    
}