package chess.start;

import chess.controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;
import chess.model.Model;
import chess.view.Window;

import java.io.IOException;

public class Start extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Controller ctrl = new Controller();
        Window view = new Window(ctrl);
        Model mod = new Model(view);
        ctrl.setLinks(view, mod);

        view.initWindow();
    }

    public static void main(String[] args) {
        launch();
    }
}