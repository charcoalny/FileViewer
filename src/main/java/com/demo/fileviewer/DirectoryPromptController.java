package com.demo.fileviewer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class DirectoryPromptController {
    private String dir;
    @FXML private TextField directory;

    public String getDirectory() {
        return dir;
    }

    public void showFileChooser(ActionEvent event) throws Exception  {
        DirectoryChooser dirChooser = new DirectoryChooser();
        Stage Stage = (Stage) directory.getScene().getWindow();
        File selectedFile = dirChooser.showDialog(Stage);
        if(selectedFile != null) {
            directory.appendText(selectedFile.getAbsolutePath());
        }
    }

    public void submitDirectory(ActionEvent event) throws Exception {
        dir = directory.getText();
        Stage Stage = (Stage) directory.getScene().getWindow();
        Stage.close();
    }

    public void cancel(ActionEvent event) throws Exception {
        Stage Stage = (Stage) directory.getScene().getWindow();
        Stage.close();
    }

}
