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

package io.github.badernageral.bgfinancas.modulo.despesa;

import io.github.badernageral.bgfinancas.biblioteca.contrato.Categoria;
import io.github.badernageral.bgfinancas.biblioteca.contrato.ControladorFormulario;
import io.github.badernageral.bgfinancas.biblioteca.contrato.Item;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Janela;
import io.github.badernageral.bgfinancas.biblioteca.sistema.Kernel;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Animacao;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Erro;
import io.github.badernageral.bgfinancas.biblioteca.utilitario.Validar;
import io.github.badernageral.bgfinancas.idioma.Linguagem;
import io.github.badernageral.bgfinancas.modelo.Despesa;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
    
public final class ModalCupomFiscalControlador implements Initializable, ControladorFormulario {
    
    @FXML private TitledPane formulario;
    @FXML private Text mensagem_1;
    @FXML private Text mensagem_2;
    @FXML private Button pegar_itens;
    @FXML private Label valor_total;
    @FXML private TableView<Despesa> lista_itens;
    @FXML private TableColumn<String, Despesa> coluna_1;
    @FXML private TableColumn<ComboBox<Item>, Despesa> coluna_2;
    @FXML private TableColumn<BigDecimal, Despesa> coluna_3;
    @FXML private TableColumn<BigDecimal, Despesa> coluna_4;
    @FXML private Button ok;
    @FXML private Button cancelar;
    
    private List<Despesa> resultado = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.setText(idioma.getMensagem("cupom_fiscal"));
        mensagem_1.setText(idioma.getMensagem("arquivo_xml")+":");
        mensagem_2.setText(idioma.getMensagem("itens_cupom"));
        coluna_1.setText(idioma.getMensagem("item_cupom"));
        coluna_2.setText(idioma.getMensagem("item"));
        coluna_3.setText(idioma.getMensagem("quantidade"));
        coluna_4.setText(idioma.getMensagem("valor_total"));
        valor_total.setText(idioma.getMensagem("valor_total")+": "+idioma.getMensagem("moeda")+" 0.0");
        ok.setText(Linguagem.getInstance().getMensagem("ok"));
        cancelar.setText(Linguagem.getInstance().getMensagem("cancelar"));
        ok.setTooltip(new Tooltip(Linguagem.getInstance().getMensagem("ok")+" (ALT+ENTER)"));
        cancelar.setTooltip(new Tooltip(Linguagem.getInstance().getMensagem("cancelar")+" (ESCAPE)"));
        pegar_itens.setText(idioma.getMensagem("selecionar_arquivo"));
        pegar_itens.setTooltip(new Tooltip(idioma.getMensagem("selecionar_arquivo")));
        pegar_itens.setOnAction((ActionEvent e) -> {
                acaoCadastrar(1);
        });
        coluna_1.setCellValueFactory(new PropertyValueFactory<>("nomeItem"));
        coluna_2.setCellValueFactory(new PropertyValueFactory<>("comboItem"));
        coluna_3.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        coluna_4.setCellValueFactory(new PropertyValueFactory<>("valor"));
    }
    
    @FXML
    @Override
    public void acaoFinalizar() {
        try {
            ObservableList<Despesa> itens = lista_itens.getItems();
            for(Despesa d : itens){
                Validar.autoFiltro(d.getItemAutoFiltro(), d.getComboAutoFiltro());
                Despesa df = new Despesa(d.getItemAutoFiltro().getItem().getIdItem(), d.getItemAutoFiltro().getItem().getNome(), d.getItemAutoFiltro().getItem().getNomeCategoria(), d.getQuantidade().toString(), d.getValor().toString());
                resultado.add(df);
            }
            Animacao.fadeInOutClose(formulario);
        } catch (Erro ex) {
            resultado.clear();
        }
    }
    
    public List<Despesa> getResultado(){
        return resultado;
    }
    
    @Override
    public void acaoCadastrar(int botao) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(idioma.getMensagem("arquivo_xml"));
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("XML", "*.xml"),
            new FileChooser.ExtensionFilter(idioma.getMensagem("todos_arquivos"), "*.*")
        );
        File arquivoSelecionado = fileChooser.showOpenDialog(Kernel.palco);
        if (arquivoSelecionado != null) {
            try {
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document document = docBuilder.parse(arquivoSelecionado);
                NodeList produtos = document.getElementsByTagName("prod");
                BigDecimal valorTotal = new BigDecimal("0.0");
                for (int i=0; i < produtos.getLength(); i++) {
                    Node produto = produtos.item(i);
                    if (produto.getNodeType() == Node.ELEMENT_NODE) {
                        NodeList atributos = produto.getChildNodes();
                        Despesa d = new Despesa();
                        for(int j=0;j<atributos.getLength();j++){
                            if(atributos.item(j).getNodeName().equals("xProd")){
                                d.setNomeItem(atributos.item(j).getFirstChild().getNodeValue());
                            }
                            if(atributos.item(j).getNodeName().equals("qCom")){
                                d.setQuantidade(atributos.item(j).getFirstChild().getNodeValue());
                            }
                            if(atributos.item(j).getNodeName().equals("vProd")){
                                d.setValor(atributos.item(j).getFirstChild().getNodeValue());
                                valorTotal = valorTotal.add(new BigDecimal(atributos.item(j).getFirstChild().getNodeValue()));
                            }
                        }
                        lista_itens.getItems().add(d);
                    }
                }
                valor_total.setText(idioma.getMensagem("valor_total")+": "+idioma.getMensagem("moeda")+" "+valorTotal.toString());
            } catch (Exception ex) {
                Janela.showException(ex);
            }
        }
//        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
//        URL site = new URL(cupom_url.getText());
//        BufferedReader re = new BufferedReader(new InputStreamReader(site.openStream()));
//        String inputLine;
//        Matcher m;
//        Pattern r = Pattern.compile("fade\">([a-zA-Z0-9_ .]+)<\\/a>.+?([0-9.]+)<\\/td>.+?R\\$.([0-9,]+)<\\/td>.+?R\\$.([0-9,]+)<\\/td>");
//        while ((inputLine = re.readLine()) != null) {
//            m = r.matcher(inputLine);
//            while (m.find()) {
//                adicionar na tabela
//            }
//        }
//        re.close();
    }
    
    @FXML
    @Override
    public void acaoCancelar() {
        Stage palco = (Stage) formulario.getScene().getWindow();
        palco.setTitle("");
        Animacao.fadeInOutClose(formulario);
    }

    @Override
    public void selecionarComboCategoria(int combo, Categoria categoria) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

    @Override
    public void selecionarComboItem(int combo, Item item) {
        System.out.println(idioma.getMensagem("nao_implementado"));
    }

}
