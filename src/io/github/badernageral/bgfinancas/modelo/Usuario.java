/*
Copyright 2012-2017 Jose Robson Mariano Alves

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
import io.github.badernageral.bgfinancas.biblioteca.banco.Coluna;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Secure;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Usuario extends Banco<Usuario> implements Modelo {
    
    private static final String MODULO = RAIZ+"/modulo/usuario";
    
    public static final String FXML = MODULO+"/Usuario.fxml";
    public static final String FXML_FORMULARIO = MODULO+"/UsuarioFormulario.fxml";

    public static final String TABELA = "usuarios";
    
    private final Coluna idUsuario = new Coluna(TABELA, "id_usuarios");
    private final Coluna nome = new Coluna(TABELA, "nome");
    private final Coluna usuario = new Coluna(TABELA, "usuario");
    private final Coluna senha = new Coluna(TABELA, "senha");
    
    public Usuario(){ this("", "", "", ""); }
    
    public Usuario(String idUsuario, String nome, String usuario, String senha){
        this.idUsuario.setValor(idUsuario);
        this.nome.setValor(nome);
        this.usuario.setValor(usuario);
        this.senha.setValor(Secure.md5(senha));
    }
    
    @Override
    protected Usuario instanciar(ResultSet rs) throws SQLException{
        return new Usuario(
            rs.getString(idUsuario.getColuna()),
            rs.getString(nome.getColuna()),
            rs.getString(usuario.getColuna()),
            rs.getString(senha.getColuna())
        );
    }
    
    @Override
    public boolean cadastrar(){
        return this.insert(nome, usuario, senha).commit();
    }
    
    @Override
    public boolean alterar(){
        return this.update(nome, usuario).where(idUsuario, "=").commit();
    }
    
    @Override
    public boolean excluir(){
        return this.delete(idUsuario, "=").commit();
    }
    
    @Override
    public Usuario consultar() {
        try{
            this.select(idUsuario, nome, usuario, senha);
            this.where(usuario, "=");
            this.and(senha, "=");
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
    public ObservableList<Usuario> listar(){
        try{
            this.select(idUsuario, nome, usuario, senha);
            if(!nome.getValor().equals("") && !usuario.getValor().equals("")){
                this.where(nome, "LIKE");
                this.or(usuario, "LIKE");
            }
            this.orderByAsc(nome);
            ResultSet rs = this.query();
            if(rs != null){
                List<Usuario> Linhas = new ArrayList<>();
                while(rs.next()){
                    Linhas.add(instanciar(rs));
                }
                ObservableList<Usuario> Resultado = FXCollections.observableList(Linhas);
                return Resultado;
            }else{
                return null;
            }
        }catch(SQLException ex){
            Janela.showException(ex);
            return null;
        }
    }
    
    public String getIdUsuario() {
        return idUsuario.getValor();
    }
    
    public String getNome() {
        return nome.getValor();
    }
    
    public String getUsuario() {
        return usuario.getValor();
    }
    
    public String getSenha() {
        return senha.getValor();
    }
    
    public Usuario setUsuario(String usuario) {
        this.usuario.setValor(usuario);
        return this;
    }
    
    public Usuario setSenha(String senha) {
        this.senha.setValor(Secure.md5(senha));
        return this;
    }
    
    public Usuario setFiltro(String filtro){
        nome.setValor(filtro);
        usuario.setValor(filtro);
        return this;
    }

    @Override
    protected Usuario getThis() {
        return this;
    }

}
