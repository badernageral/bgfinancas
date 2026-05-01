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

import io.github.badernageral.bgfinancas.biblioteca.tipo.Funcao;

public class Coluna {
    
    protected final String tabela;
    protected final String coluna;
    protected String valor;
    protected String alias_tabela;
    protected String alias_coluna;
    protected Funcao funcao;
    
    public Coluna(String tabela, String coluna){
        this.tabela = tabela;
        this.coluna = coluna;
        this.valor = null;
        this.alias_tabela = null;
        this.alias_coluna = null;
        this.funcao = null;
    }
    
    public Coluna(String tabela, String coluna, String valor){
        this(tabela, coluna);
        this.valor = valor;
    }
    
    public Coluna(String tabela, String coluna, String alias_coluna, Funcao funcao){
        this(tabela, coluna);
        this.alias_coluna = alias_coluna;
        this.funcao = funcao;
    }
    
    public Coluna(String tabela, String coluna, String valor, String alias_coluna){
        this(tabela, coluna, valor);
        this.alias_coluna = alias_coluna;
    }
    
    public Coluna(String tabela, String coluna, String valor, String alias_coluna, String alias_tabela){
        this(tabela, coluna, valor);
        this.alias_coluna = alias_coluna;
        this.alias_tabela = alias_tabela;
    }
    
    public String getTabelaColuna(Boolean retornar){
        String str;
        if(tabela==null){
            str = coluna;
        }else{
            if(alias_tabela==null){
                str = tabela+"."+coluna;
            }else{
                str = alias_tabela+"."+coluna;
            }
        }
        if(funcao!=null){
            str = funcao.getValor()+"("+str+")";
        }
        if(alias_coluna!=null && retornar){
            str = str.concat(" AS "+alias_coluna);
        }
        return str;
    }
    
    public String getTabela(){
        if(alias_tabela==null){
            return tabela;
        }else{
            return tabela+" "+alias_tabela;
        }
    }
    
    public String getColuna(){
        return coluna;
    }
    
    public String getValor(){
        return valor;
    }
    
    public String getAliasColuna(){
        return alias_coluna;
    }
    
    public void setValor(String valor){
        this.valor = valor;
    }
    
    public void setRetorno(String retorno){
        this.alias_coluna = retorno;
    }
    
}