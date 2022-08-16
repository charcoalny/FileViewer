package com.demo.fileviewer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class MainController {


    @FXML public TreeView<String> treeView;
    @FXML public MenuButton menu;
    @FXML public MenuItem promptDirectory;
    @FXML public MenuItem selectDirectory;
    @FXML public TextArea textPanel;
    @FXML public ToolBar save;

    private TreeItem<String> currentFile;
    private ChangeListener listener;
    private boolean edited;


    public void initialize() {
        File root = new File(GetDocumentsPath());
        treeView.setRoot(getTreeItem(root));

        listener = new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                indicateEditedFile();
            }
        };
    }

    public void changePath(String arg) {
        File root = new File(arg);
        treeView.setRoot(getTreeItem(root));
    }

    public String GetDocumentsPath() {
            JFileChooser fr = new JFileChooser();
            FileSystemView fw = fr.getFileSystemView();
            return fw.getDefaultDirectory().getAbsolutePath();
    }

    public List<String> listFilesUsingJavaIO(File file) {
        return Stream.of(file.listFiles())
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
    }

    public TreeItem<String> getTreeItem(File file) {
        TreeItem<String> rootItem = new TreeItem<>(file.getAbsolutePath());
        rootItem.setExpanded(true);
        List<String> list = listFilesUsingJavaIO(file);
        for (String fileName : list) {
            File subDir = new File(fileName);
            if (subDir.isDirectory()) {
                rootItem.getChildren().add(getTreeItem(subDir));
            } else {
                TreeItem<String> item = new TreeItem<>(subDir.getAbsolutePath());
                item.setExpanded(false);
                rootItem.getChildren().add(item);
            }
        }
        return rootItem;
    }

    public void btnEventHandler(ActionEvent event) throws Exception {
        FXMLLoader loader;
        if(event.getSource() == selectDirectory) {
            loader = new FXMLLoader(getClass().getResource("FileChooser.fxml"));
        } else if (event.getSource() == promptDirectory){
            loader = new FXMLLoader(getClass().getResource("DirectoryPrompt.fxml"));
        } else {
            return;
        }

        Stage popupStage = new Stage();
        Scene popupScene = new Scene(loader.load());
        popupStage.setScene(popupScene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(menu.getScene().getWindow());
        popupStage.setTitle("Enter directory");
        popupStage.showAndWait();

        DirectoryPromptController controller = loader.getController();
        String dir = controller.getDirectory();
        if(dir != null) {
            changePath(dir);
        }
    }

    public void showContent(MouseEvent mouseEvent) throws Exception {
        if(mouseEvent.getClickCount() == 2)
        {
            TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
            currentFile = item;
            String path = item.getValue();
            if("txt".equals(getExtension(path))) {
                edited = false;
                save.setDisable(true);
                textPanel.textProperty().removeListener(listener);
                String data = new String(Files.readAllBytes(Paths.get(path)));
                textPanel.setText(data);
                textPanel.textProperty().addListener(listener);
            }
        }
    }

    public void saveContent() throws IOException {
        int pathLength = currentFile.getValue().length();
        if(currentFile.getValue().lastIndexOf('*') == pathLength - 1){
            String content = textPanel.getText();
            String path = currentFile.getValue().substring(0, pathLength - 1);
            Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8));
            currentFile.setValue(path);
            save.setDisable(true);
            edited = false;
        }
    }

    public void indicateEditedFile(){
        if(currentFile.getValue().lastIndexOf('*') != currentFile.getValue().length() - 1){
            currentFile.setValue(currentFile.getValue() + "*");
            save.setDisable(false);
            edited = true;
        }
    }

    public boolean saveAlert(){
        if(!edited) {
            return false;
        }

        Stage primaryStage = (Stage)save.getScene().getWindow();
        Stage popupStage = new Stage();
        Label label = new Label("File is not saved. Are you sure you want to leave?");
        Button btn1 = new Button("No");
        btn1.setLayoutX(20);
        btn1.setLayoutY(40);
        btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                popupStage.close();
            }
        });
        Button btn2 = new Button("Leave");
        btn2.setLayoutX(60);
        btn2.setLayoutY(40);
        btn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                popupStage.close();
                primaryStage.close();
            }
        });

        AnchorPane pane = new AnchorPane(label,btn1, btn2);
        Scene scene = new Scene(pane);
        popupStage.setScene(scene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
        popupStage.showAndWait();
        return true;
    }

    private String getExtension(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i+1);
        }
        return extension;
    }
}
