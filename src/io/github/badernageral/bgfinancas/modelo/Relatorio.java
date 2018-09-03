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
import io.github.badernageral.bgfinancas.biblioteca.contrato.Modelo;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.ObservableList;

public final class Relatorio extends Banco<Relatorio> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/relatorio";
    
    public static final String FXML = MODULO+"/Relatorios.fxml";

    public Relatorio(){ }
    
    @Override
    protected Relatorio instanciar(ResultSet rs) throws SQLException{
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    @Override
    public boolean cadastrar(){
        System.out.println(idioma.getMensagem("nao_implementado"));
        return false;
    }
    
    @Override
    public boolean alterar(){
        System.out.println(idioma.getMensagem("nao_implementado"));
        return false;
    }
    
    @Override
    public boolean excluir(){
        System.out.println(idioma.getMensagem("nao_implementado"));
        return false;
    }
    
    @Override
    public Relatorio consultar() {
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    @Override
    public ObservableList<Relatorio> listar(){
        System.out.println(idioma.getMensagem("nao_implementado"));
        return null;
    }
    
    @Override
    protected Relatorio getThis() {
        return this;
    }

}
