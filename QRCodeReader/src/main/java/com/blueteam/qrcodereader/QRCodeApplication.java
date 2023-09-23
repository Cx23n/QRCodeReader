package com.blueteam.qrcodereader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class QRCodeApplication extends Application {
    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        StackPane stackPane = new StackPane();
        Region dropRegion = new Region();
        dropRegion.setStyle("-fx-background-color: lightgray;");
        dropRegion.prefWidthProperty().bind(stackPane.widthProperty());
        dropRegion.prefHeightProperty().bind(stackPane.heightProperty());

        Text message = new Text("Drag and drop an image file here");
        message.setWrappingWidth(280); // Set maximum width
        message.setTextAlignment(TextAlignment.CENTER);

        dropRegion.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        dropRegion.setOnDragDropped(event -> {
            File file = event.getDragboard().getFiles().get(0);
            try {
                String qrCodeData = readQRCode(file);
                message.setText(qrCodeData);
            } catch (IOException | NotFoundException e) {
                message.setText("Error reading QR code");
            }
            event.setDropCompleted(true);
            event.consume();
        });

        // Add context menu for copying
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(message.getText());
            clipboard.setContent(content);
        });
        contextMenu.getItems().add(copyItem);

        message.setOnContextMenuRequested(event -> contextMenu.show(message, event.getScreenX(), event.getScreenY()));

        root.setCenter(stackPane);
        stackPane.getChildren().addAll(dropRegion, message);

        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("QR Code Reader");
        stage.setScene(scene);
        stage.show();
    }

    private String readQRCode(File file) throws IOException, NotFoundException {
        BufferedImage image = ImageIO.read(file);
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(binaryBitmap);
        return result.getText();
    }

    public static void main(String[] args) {
        launch();
    }
}
