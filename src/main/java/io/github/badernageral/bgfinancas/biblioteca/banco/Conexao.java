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

import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.tipo.Status;
import java.io.File;
import java.sql.*;

public final class Conexao {
    
    private Conexao(){ }
    
    private static final Boolean DEBUG_SQL = false;
    private static final Boolean DEBUG_PARAMETRO = false;
    private static final String DRIVER = "org.hsqldb.jdbcDriver";
    private static final Conexao instance = new Conexao();
    private static String url;
    private static Connection conexao;
    private static Statement statement;
    private static PreparedStatement pstatement;
    private static ResultSet resultset;
    
    public boolean conectar(){
        try{
            url = getUrl(false);
            Class.forName(DRIVER);
            conexao = DriverManager.getConnection("jdbc:hsqldb:file:"+url, "sa", "");
            return true;
        }catch(ClassNotFoundException driver){
            Janela.showMensagem(Status.ERRO, "Driver do banco não encontrado.");
            return false;
        }catch(SQLException fonte){
            Janela.showMensagem(Status.ERRO, "Erro na conexão com o banco de dados.");
            return false;
        }
    }
    
    public String getUrl(boolean diretorio){
        String nomeBanco = "banco";
        String sistema_operacional = System.getProperty("os.name").toLowerCase();
        File diretorioBanco;
        if(sistema_operacional.contains("linux")){
            diretorioBanco = new File(System.getProperty("user.home"), ".bgfinancas");
        }else if(sistema_operacional.contains("windows")){
            String localAppData = System.getenv("LOCALAPPDATA");
            String appData = System.getenv("APPDATA");
            if(localAppData != null && !localAppData.isBlank()){
                diretorioBanco = new File(localAppData, "BGFinancas");
            }else if(appData != null && !appData.isBlank()){
                diretorioBanco = new File(appData, "BGFinancas");
            }else{
                diretorioBanco = new File(System.getProperty("user.home"), "BGFinancas");
            }
        }else if(sistema_operacional.contains("mac")){
            diretorioBanco = new File(System.getProperty("user.home")+"/Library/Application Support", "BGFinancas");
        }else{
            return (diretorio) ? "." : nomeBanco;
        }
        diretorioBanco.mkdirs();
        return (diretorio) ? diretorioBanco.getAbsolutePath()+File.separator : new File(diretorioBanco, nomeBanco).getAbsolutePath();
    }
    
    private boolean desconectar(){
        try{
            conexao.close();
            conexao = null;
            return true;
        }catch(SQLException sqlex) {
            Janela.showMensagem(Status.ERRO, sqlex.getMessage());
            return false;
        }
    }
    
    private void isConectado(){
        if(conexao == null){
            conectar();
        }
    } 
    
    public ResultSet getResultSet(){
        return resultset;
    }
    
    public boolean executeQuery(String sql){
        try{
            isConectado();
            statement = conexao.createStatement();
            resultset = statement.executeQuery(sql);
            return true;
        }catch(SQLException sqlex){
            Janela.showMensagem(Status.ERRO,sqlex.getMessage());
            return false;
        }
    }
    
    public int executeUpdate(String sql){
        try{
            isConectado();
            statement = conexao.createStatement();
            return statement.executeUpdate(sql);
        }catch(SQLException sqlex){
            Janela.showMensagem(Status.ERRO,sqlex.getMessage());
            return 0;
        }
    }
    
    public void prepararSQL(String sql){
        try{
            isConectado();
            if(DEBUG_SQL){
                System.out.println(sql);
            }
            pstatement = conexao.prepareStatement(sql);
        }catch(SQLException sqlex){
            Janela.showMensagem(Status.ERRO, sqlex.getMessage());
        }
    }
    
    public void setParametro(int posicao, String parametro){
        try{
            if(DEBUG_PARAMETRO){
                System.out.println(posicao+": "+parametro);
            }
            pstatement.setString(posicao, parametro);
        }catch(SQLException sqlex){
            Janela.showMensagem(Status.ERRO, sqlex.getMessage());
        }
    }
    
    public int finalizarUpdate(){
        try{
            isConectado();
            return pstatement.executeUpdate();
        }catch(SQLException sqlex){
            Janela.showMensagem(Status.ERRO, sqlex.getMessage());
            return 0;
        }
    }
    
    public boolean finalizarQuery(){
        try{
            isConectado();
            resultset = pstatement.executeQuery();
            return true;
        }catch(SQLException sqlex){
            Janela.showMensagem(Status.ERRO,sqlex.getMessage());
            return false;
        }
    }
    
    public static Conexao getInstance(){
        return instance;  
    }
    
}
