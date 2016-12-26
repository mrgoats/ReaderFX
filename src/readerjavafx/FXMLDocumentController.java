package readerjavafx;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

public class FXMLDocumentController implements Initializable {
    List<ImageView> images = new ArrayList<>();
    List<File> recentFiles = new ArrayList<>();
    ImageView iv;
    ScrollPane sp;
    
    @FXML
    private AnchorPane content;
    private MenuItem quitMenu;
    private MenuItem closeMenu;
    private MenuItem openMenu;
    private MenuItem reverseMenu;
    private MenuItem mostRecentlyOpened;
    private Menu openRecentMenu;
    private MenuItem teste;
    
    @FXML
    private void handleRecentFiles(ActionEvent event){
        
    }
    
    @FXML
    private void handleReverseReadingAction(ActionEvent event){
//        File f = recentFiles.get(recentFiles.lastIndexOf(this));
//        zipStuff(f);
    }
    
    @FXML
    private void handleOpenAction(ActionEvent event){
        
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ZIP files (*.zip)", "*.zip");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(ReaderJavaFX.getPrimaryStage());
        System.out.println(file);
        
        if (file != null) {
            zipStuff(file);
        }
    }
    
    @FXML
    private void handleQuitAction(ActionEvent event) {
        System.exit(0);
    }
    @FXML
    private void handleCloseAction(ActionEvent event){
       close();
        
    }
    
    private void close(){
        content.getChildren().remove(1);
        
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {   

        content.setStyle("-fx-background-color: black;");

    }  
    
    public void hello(){
        System.out.println("Hello.");
    }
    
    public void setDragEvents(Scene scene){
        setOnDragOverEvent(scene);
        setOnDragDroppedEvent(scene);   
    }
    
    public void setOnDragOverEvent(Scene scene){

        scene.setOnDragOver((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });  
    }
    
    public void setOnDragDroppedEvent(Scene scene){
        // Dropping over surface
        scene.setOnDragDropped((DragEvent event) -> {
            
            Dragboard db = event.getDragboard();

            boolean success = false;
            
            if (db.hasFiles()) {
                success = true;
                String filePath = null;
                for (File file:db.getFiles()) {               
                    
                    filePath = file.getAbsolutePath();
                    System.out.println(filePath);
                    recentFiles.add(file);
                    zipStuff(file);
                    
                }
            }
            event.setDropCompleted(success);
            //System.out.println(recentFiles.toString());
            event.consume();
        });        
    }
    
    public void zipStuff(File file){
        HBox box = new HBox();
        //box.setAlignment(Pos.CENTER);

        try {
            ZipFile zipFile = new ZipFile(file);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while(entries.hasMoreElements()){

                ZipEntry entry = entries.nextElement();
                System.out.println(entry);
                InputStream stream = zipFile.getInputStream(entry);
                
                
                //BufferedImage bufferedImage = ImageIO.read(stream);
                
                BufferedImage img = toCompatibleImage(ImageIO.read(stream));
                
                Image image = SwingFXUtils.toFXImage(img, null);
               
                iv = new ImageView();
                iv.setImage(image);                
                images.add(iv);
                //System.out.println(iv);
                iv.setId(entry.getName());
                
        }
            
        zipFile.close();
        arrangeImages(images);
        
            
        int i = 0;
        while (i < images.size()) {
                
                ImageView iv = images.get(i);
                iv.setFitHeight(665);

                //iv.setFitWidth(480);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                iv.setCache(true);           
                
                box.getChildren().add(iv);
                  
            i++;
        }
        
            images.clear();
            
            sp = new ScrollPane();
            sp.setPrefViewportHeight(665);
            sp.setPrefViewportWidth(1350);

            sp.setContent(box);
            
            content.getChildren().add(sp);



        } catch (IOException ex) {
            Logger.getLogger(ReaderJavaFX.class.getName()).log(Level.SEVERE, null, ex);   
        }
    }

    private void arrangeImages(List<ImageView> images) {
        // Sorting
        Collections.sort(images, new Comparator<ImageView>() {
            @Override
            public int compare(ImageView iv2, ImageView iv1)
            {
                return  iv1.getId().compareTo(iv2.getId());
            }
        });    
    }
    
    private BufferedImage toCompatibleImage(BufferedImage image){
        // obtain the current system graphical settings
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
            getLocalGraphicsEnvironment().getDefaultScreenDevice().
            getDefaultConfiguration();

        /*
         * if image is already compatible and optimized for current system 
         * settings, simply return it
         */
        if (image.getColorModel().equals(gfx_config.getColorModel()))
            return image;

        // image is not optimized, so create a new image that is
        BufferedImage new_image = gfx_config.createCompatibleImage(
                image.getWidth(), image.getHeight(), image.getTransparency());

        // get the graphics context of the new image to draw the old image on
        Graphics2D g2d = (Graphics2D) new_image.getGraphics();

        // actually draw the image and dispose of context no longer needed
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // return the new optimized image
        return new_image;    
    }
}
