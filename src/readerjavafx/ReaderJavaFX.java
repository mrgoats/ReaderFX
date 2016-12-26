package readerjavafx;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ReaderJavaFX extends Application {
    
    private static Stage primaryStage; // **Declare static Stage**
    final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

    private void setPrimaryStage(Stage stage) {
        ReaderJavaFX.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return ReaderJavaFX.primaryStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        setPrimaryStage(stage);
        stage.getIcons().add(new Image("readerjavafx/icon.png"));
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("basic.fxml"));
        Parent root = (Parent)loader.load();
        FXMLDocumentController controller = (FXMLDocumentController)loader.getController();
        controller.hello();

        Scene scene = new Scene(root, 800, 600);
        
        
        controller.setDragEvents(scene);

        stage.setTitle("Reader");     
        stage.setScene(scene);
        stage.sizeToScene(); 
        stage.show();  
    }

    public static void main(String[] args) {   
        launch(args);       
    }
                
}

