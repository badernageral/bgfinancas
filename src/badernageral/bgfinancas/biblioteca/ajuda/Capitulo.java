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

package badernageral.bgfinancas.biblioteca.ajuda;

import badernageral.bgfinancas.biblioteca.tipo.Posicao;
import java.util.List;
import javafx.scene.Node;

public final class Capitulo {
    
    private Node node;
    private List<Node> nodes;
    private final String mensagem;
    private final Posicao posicao;
    
    public Capitulo(Posicao posicao, String mensagem){
        this.node = null;
        this.nodes = null;
        this.posicao = posicao;
        this.mensagem = mensagem;
    }
    
    public Capitulo(Node node, Posicao posicao, String mensagem){
        this(posicao,mensagem);
        this.node = node;
    }
    
    public Capitulo(List<Node> nodes, Posicao posicao, String mensagem){
        this(posicao,mensagem);
        this.nodes = nodes;
    }
    
    public Node getNode(){
        return node;
    }
    
    public List<Node> getNodes(){
        return nodes;
    }
    
    public Posicao getPosicao(){
        return posicao;
    }
    
    public String getMensagem(){
        return mensagem;
    }
    
}