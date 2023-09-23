module com.busymonkeys.qrcodereader {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.zxing;
    requires com.google.zxing.javase;


    opens com.blueteam.qrcodereader to javafx.fxml;
    exports com.blueteam.qrcodereader;
}