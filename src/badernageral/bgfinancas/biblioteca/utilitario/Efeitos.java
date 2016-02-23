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

package badernageral.bgfinancas.biblioteca.utilitario;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;

public final class Efeitos {
    
    private Efeitos(){ }
    
    public static void innerShadow(Node node) {
        InnerShadow is = new InnerShadow();
        is.setOffsetX(2.0f);
        is.setOffsetY(2.0f);
        node.setEffect(is);
    }
    
    public static void dropShadow(Node node){
        DropShadow ds = new DropShadow();
        ds.setOffsetX(2.0f);
        ds.setOffsetY(2.0f);
        node.setEffect(ds);
    }
    
    public static void remover(Node node){
        node.setEffect(null);
    }
    
}
