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

package badernageral.bgfinancas.biblioteca.sistema;

import badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import badernageral.bgfinancas.biblioteca.tipo.Duracao;
import badernageral.bgfinancas.biblioteca.tipo.Status;
import badernageral.bgfinancas.idioma.Linguagem;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public final class Janela {
    
    private Janela(){ }
    
    private static final String PREFIXO_TOOLTIP = "Tooltip";
    private static final Linguagem idioma = Linguagem.getInstance();
    
    public static Scene criarCenaModal(Parent raiz){
        Scene cena = new Scene(raiz);
        cena.getStylesheets().add(Janela.class.getResource(Kernel.CSS_PRINCIPAL).toExternalForm());
        cena.getStylesheets().add(Janela.class.getResource(Kernel.CSS_JANELA).toExternalForm());
        cena.getStylesheets().add(Janela.class.getResource(Kernel.CSS_TOOLTIP).toExternalForm());
        cena.setFill(null);
        return cena;
    }
    
    public static Stage criarPalcoModal(Scene cena, String titulo){
        Stage palco = new Stage();
        palco.setTitle(titulo);
        palco.setScene(cena);
        palco.initModality(Modality.APPLICATION_MODAL);
        palco.initStyle(StageStyle.TRANSPARENT);
        palco.setResizable(false);
        return palco;
    }
    
    public static <T> T abrir(String FXMLDocumento, String titulo){
        return Janela.abrir(FXMLDocumento, titulo, false);
    }
    
    public static <T> T abrir(String FXMLDocumento, String titulo, Boolean esperar){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Janela.class.getResource(FXMLDocumento));
            Parent raiz = loader.load(Janela.class.getResourceAsStream(FXMLDocumento));
            Scene cena = criarCenaModal(raiz);
            Atalho.setAtalhosModal(loader.getController(), cena);
            Stage palco = criarPalcoModal(cena, titulo);
            Animacao.fadeOutIn(Kernel.layoutGeral, raiz);
            if(!esperar){
                palco.show();
            }
            Kernel.palcoModal = palco;
            return loader.getController();
        } catch (IOException ex) {
            Janela.showException(ex);
            return null;
        }
    }
    
    public static boolean showPergunta(String mensagem){
        Alert pergunta = new Alert(AlertType.CONFIRMATION);
        pergunta.setTitle(idioma.getMensagem("pergunta"));
        pergunta.setHeaderText(null);
        pergunta.setContentText(mensagem);
        ButtonType sim = new ButtonType(idioma.getMensagem("sim"));
        ButtonType nao = new ButtonType(idioma.getMensagem("nao"));
        pergunta.getButtonTypes().setAll(sim,nao);
        Optional<ButtonType> resposta = pergunta.showAndWait();
        return resposta.get() == sim;
    }
    
    public static String showEntrada(String mensagem, String valorPadrao){
        TextInputDialog entrada = new TextInputDialog(valorPadrao);
        entrada.setTitle(idioma.getMensagem("informe_dados"));
        entrada.setHeaderText(null);
        entrada.setContentText(mensagem);
        ButtonType ok = new ButtonType(idioma.getMensagem("ok"), ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType(idioma.getMensagem("cancelar"), ButtonData.CANCEL_CLOSE);
        entrada.getDialogPane().getButtonTypes().setAll(ok, cancelar);
        Optional<String> result = entrada.showAndWait();
        if(result.isPresent()){
            return result.get();
        }else{
            return null;
        }
    }
    
    public static void showMensagem(Status tipo, String mensagem){
        Alert alerta = new Alert(AlertType.NONE);
        switch(tipo){
            case ERRO:
                alerta.setAlertType(AlertType.ERROR);
                alerta.setTitle(idioma.getMensagem("erro"));
                break;
            case SUCESSO:
                alerta.setAlertType(AlertType.INFORMATION);
                alerta.setTitle(idioma.getMensagem("sucesso"));
                break;
            case ADVERTENCIA:
                alerta.setAlertType(AlertType.WARNING);
                alerta.setTitle(idioma.getMensagem("atencao"));
                break;
        }
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
    
    public static void showException(Exception ex){
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle(idioma.getMensagem("erro"));
        alerta.setHeaderText(null);
        alerta.setContentText(ex.getMessage());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();
        TextArea textArea = new TextArea(exceptionText);
        textArea.setBackground(Background.EMPTY);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        alerta.getDialogPane().setExpandableContent(textArea);
        alerta.showAndWait();
    }
    
    public static void showTooltip(Status tipo, String mensagem, Control control, Duracao tempo){
        try{
            Tooltip tooltip = getTooltip(tipo, mensagem);
            Scene cena = control.getScene();
            Window janela = control.getScene().getWindow();
            Tooltip oldtooltip = control.getTooltip();
            control.setTooltip(tooltip);
            Point2D coordenada = control.localToScene(0.0, 0.0);
            Double x = coordenada.getX();
            Double y = coordenada.getY();
            x += cena.getX() + janela.getX();
            y += cena.getY() + janela.getY() + 28;
            tooltip.show(janela, x, y);
            control.setTooltip(oldtooltip);
            if(tempo.getValor()>0){
                setTimeTooltip(tooltip, tempo);
            }
        }catch(Exception e){
            Janela.showMensagem(Status.ERRO, e.getMessage());
        }
    }
    
    public static void showTooltip(Status tipo, String mensagem, Duracao tempo){
        try{
            Tooltip tooltip = getTooltip(tipo, mensagem);
            Window janela = Kernel.cena.getWindow();
            tooltip.show(janela);
            if(tempo.getValor()>0){
                setTimeTooltip(tooltip, tempo);
            } 
        }catch(Exception e){
            Janela.showMensagem(Status.ERRO, e.getMessage());
        }
    }
    
    private static Tooltip getTooltip(Status tipo, String mensagem){
        Tooltip tooltip = new Tooltip();
        tooltip.setText(mensagem);
        tooltip.getStyleClass().add(PREFIXO_TOOLTIP);
        tooltip.getStyleClass().add(PREFIXO_TOOLTIP+tipo.getValor());
        return tooltip;
    }
    
    public static void setTimeTooltip(Tooltip tooltip, Duracao tempo){
        Task<Void> task = new Task<Void>() {
            @Override protected Void call() throws Exception {
                Thread.sleep(tempo.getValor());
                return null;
            }
        };
        task.setOnSucceeded((WorkerStateEvent event) -> {
            tooltip.hide();
        });
        Thread th = new Thread(task);
        th.start();
    }
    
}
