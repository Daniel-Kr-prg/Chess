module chess.chess {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires junit;


    opens chess.start to javafx.fxml;
    exports chess.start;
}