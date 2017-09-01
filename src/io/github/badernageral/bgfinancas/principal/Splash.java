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
package io.github.badernageral.bgfinancas.principal;

import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Splash extends Preloader {

    private Stage palco;

    @Override
    public void start(Stage stage) throws Exception {
        palco = stage;
        palco.initStyle(StageStyle.UNDECORATED);
        ImageView splash = new ImageView(new Image(Kernel.RAIZ+"/recursos/imagem/layout/splash.gif"));
        VBox layout = new VBox(20);
        layout.getChildren().add(splash);
        Scene scene = new Scene(layout);
        palco.setWidth(300);
        palco.setHeight(220);
        palco.setScene(scene);
        palco.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == Type.BEFORE_START) {
            palco.hide();
        }
    }
}
