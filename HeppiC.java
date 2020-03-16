package heppic;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView; 
import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.control.Button; 
import javafx.scene.control.Label; 
import javafx.scene.control.MenuBar; 
import javafx.scene.control.Menu; 
import javafx.scene.control.MenuItem; 
import javafx.scene.control.TextField; 
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Popup;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * @author Evaldas Karpys
 * JavaFx UI build, mostly contains the UI elements, and little program logic (mostly for the top menu)
 */
public class HeppiC extends Application {
    
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        
        //************** LAYOUT ***********************
        //use VBox for items with no actions (labels, images etc)
        VBox box = new VBox();
        //use stack pane so elements can be stacked on top of each other
        StackPane root = new StackPane(box);
        //st the sene with stackpane layout
        Scene scene = new Scene(root, 590, 330);
        
         //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TOP MENU %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
         //top menu User_Guide popup
        Popup user_guide = new Popup();
        TextFlow flow_user_guide = new TextFlow(); //User_Guide textflow
        Text user_guide_text = new Text("Once program is up and running, please locate a text field \narea near the bottom of the program. Here you can input up to 3 words. \nHappiCard will use these words to generate the greeting. \n"
                + "\nHowever, there are few rules regarding your input: \n1. You must specify one of the possible occasions (Birthday, Easter, Christmas)."
                + "\n2. Make sure to start names with a capital letter. (e.g. John)."
                + "\n3. Make sure to seperate each individual word with a comma (,)."
                + "\nAND THAT'S ALL. WHEN YOU'RE READY CLICK GENERATE BUTTON AND ENJOY <3");
        //add user guide text to the flow
        flow_user_guide.getChildren().addAll(user_guide_text);
        flow_user_guide.setStyle(" -fx-background-color: white"); //text flow User_Guide style
        user_guide.getContent().addAll(flow_user_guide); //add User_Guide textflow to the authors info pop-up
        user_guide.setAutoHide(true); //autohide, hides popup after clicking anywhere outside the pop-up
        
        
         //top menu Authors_Info popup
        Popup authors_info = new Popup();
        TextFlow flow_authorInfo = new TextFlow(); //author info textflow
        Text author_text = new Text("Program Author: Evaldas Karpys \nFeel free to use and modify the program under GNU license. \nShare it as you wish, but please credit me. Thanks.");
        //add author text to the author flow
        flow_authorInfo.getChildren().addAll(author_text);
        flow_authorInfo.setStyle(" -fx-background-color: white"); //text flow author info style
        authors_info.getContent().addAll(flow_authorInfo); //add authors info textflow to the authors info pop-up
        authors_info.setAutoHide(true); //autohide, hides popup after clicking anywhere outside the pop-up
        
        //top menu build
        MenuBar top_menu = new MenuBar();
        Menu menu2 = new Menu("User Guide");
        Menu menu3 = new Menu("Authors Info");
        MenuItem userGuide = new MenuItem("Learn me some HappiCard");
        MenuItem authorsInfo = new MenuItem("Display Authors Information");
        //add menus to the main menu
        top_menu.getMenus().add(menu2);
        top_menu.getMenus().add(menu3);
        //add menu items to relevant menus
        menu2.getItems().add(userGuide);
        menu3.getItems().add(authorsInfo);
        //on menu item click, show the relevant popup
        userGuide.setOnAction(e->{user_guide.show(primaryStage);});
        authorsInfo.setOnAction(e->{authors_info.show(primaryStage);});
        
         //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% TOP MENU END %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        //use VBox2 to hold all the elements requiring some onAction (menu, button etc)
        VBox box2 = new VBox(top_menu);
        
        //stage/window setup
        primaryStage.setTitle("HeppiCard - Let us generate your next greeting card!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); //non resizable window
        primaryStage.show();
        
        //text field for user input declaration
        TextField enter_words = new TextField();
       
        //*************** GREETING BG IMAGE ********************************
        //Image expects URL param, thus we need FileInputStream declaration with the img
        final FileInputStream inputstream_line = new FileInputStream("src\\resources\\images\\bg_line.png"); 
        //us FileInput stream to load image into bg_line
        final Image bg_line = new Image(inputstream_line); 
        //declare imgView with bg_line
        ImageView imageView2 = new ImageView(bg_line);
        imageView2.setStyle("-fx-opacity:0.5;"); //I use black block as bg_line,0.5 opacity makes it soft grey
        //image size
        imageView2.setFitHeight(130); 
        imageView2.setFitWidth(600); 
        
        //greeting label, font 20, color white
        Label greeting = new Label("Let's create a greeting?");
        greeting.setStyle("-fx-font-size: 20;");
        greeting.setTextFill(Color.WHITE);
        
        //Instruction labels, below the image and greeting
        Label word_box_txt = new Label("Input a set of words to be used to generate your card.");
        Label word_box_txt2 = new Label("Separate each word by a comma (,) - min 1 / max 3 words.");
        //set padding so when we move them around it keeps consistent gaps (just easier when coding, has no effect to UX)
        word_box_txt.setPadding(new Insets(5,5,5,0));
        word_box_txt2.setPadding(new Insets(5,5,5,0));
      

        //Generate Button
        Button btn_generate = new Button();
        btn_generate.setText("Generate A Card");
        //********* GREETING GENERATION IS TRIGGERED HERE onButtonClick ************
        btn_generate.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                //string from the textField
                String entered_words_original = enter_words.getText();
                //split entered_words string by comma and one or more whitespaces before/after comma
                //populate a list with the words split from the entered_words string
                List<String> entered_words = Arrays.asList(entered_words_original.toLowerCase().split("\\s*,\\s*"));
                //main controls point object, contains most of the functional logic methods
                ControlPoint panel = new ControlPoint();
                //VALIDATE INPUT - if not valid display information message
                //see ControlPoint class validateField method to learn how validation works
                if(panel.validateField(entered_words) == false){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Ups Something Went Wrong");
                alert.setHeaderText("Wrong Input");
                alert.setContentText("Please follow the rules specified for the input words. Minimum 1 or maximum 3 words. Note: Spaces are not words, check if you have any spaces seperated by commas.");
                alert.showAndWait();}
                //ELSE IF INPUT VALID
                else{
                    
                    String entered_words_tagged = null;
                    
                    try {
                        //tag input words with POS
                        entered_words_tagged = panel.tagWords(entered_words);
                    } catch (IOException ex) {
                        Logger.getLogger(HeppiC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                    //set new greeting text
                    greeting.setText(panel.generateGreeting(entered_words, entered_words_tagged));
                    
                    //change guide labels and button after generate click to allow user to leave feedback rating
                    word_box_txt.setText("Please rate currently generated greeting, in the text area below. (Help me improve)");
                    word_box_txt2.setText("Leave a numerical rating from 1 (being the worst) to 5 (being the best).");
                    btn_generate.setText("Leave a rating!");
                    //clear textfield
                    enter_words.setText("");
                    
                    //********************** RATING BUTTON ON ACTION ***************************//
                     btn_generate.setOnAction(new EventHandler<ActionEvent>() {
                         
                         int rating = 0; //init rating
            
                     @Override
                    public void handle(ActionEvent event) {
                        
                        try{ //try to parse rating as an integer, if fails then not an integer - display alert
                                    rating = Integer.parseInt(enter_words.getText());
                                    }catch (NumberFormatException ex) {
                                    Alert alert1 = new Alert(AlertType.INFORMATION);
                                    alert1.setTitle("Ups Something Went Wrong");
                                    alert1.setHeaderText("Wrong Input");
                                    alert1.setContentText("Your entered rating is not a number, an I only understand numbers (sadface)");
                                    alert1.showAndWait();
                                    return;
                                    }
                        //validate that rating is in range from 1-5, else display alert
                        if (rating < 1 || rating > 5){
                                    Alert alert3 = new Alert(AlertType.INFORMATION);
                                    alert3.setTitle("Ups Something Went Wrong");
                                    alert3.setHeaderText("Wrong Input");
                                    alert3.setContentText("Your entered rating is not a number, an I only understand numbers (sadface)");
                                    alert3.showAndWait();
                                    return;
                        }
                        
                        //if rating is valid then execute the following
                         try {
                             panel.adjustWeights(rating, entered_words); //adjust greeting core words weights - main adjust method is in Dictionary class
                             //after adjusting rating set up the scene for a new greeting
                             greeting.setText("Let's create a greeting?"); 
                             start(primaryStage);
                         } catch (FileNotFoundException ex) {
                             Logger.getLogger(HeppiC.class.getName()).log(Level.SEVERE, null, ex);
                         }
                    }
                     }); //************************* RATING BUTTON END HERE ******************//
                    }
                }
                
                
            
        }); //btn_generate end here
        
        //add all elements to the layout
        //st margins and do positioning here
        //*word_box_txt has paddings added, found above where theyre declared
        box.getChildren().add(imageView2);
        box.getChildren().add(greeting);
        box.getChildren().add(word_box_txt);
        box.getChildren().add(word_box_txt2);
        box2.getChildren().add(enter_words);
        box2.getChildren().add(btn_generate);
        root.getChildren().add(box2);
        //align all elements in box to center
        //important, because makes the greeting always appear in the center of bg_line
        box.setAlignment(Pos.BASELINE_CENTER);
        VBox.setMargin(greeting, new Insets(-80,0,0,0));
        VBox.setMargin(imageView2, new Insets(15,0,0,0));
        VBox.setMargin(word_box_txt, new Insets(50,0,0,0));
        VBox.setMargin(btn_generate, new Insets(10,0,0,245));
        VBox.setMargin(enter_words, new Insets(200,0,0,0));
       
    }

    /***************************************************************************
     * ************************ MAIN - LAUNCH *******************************************
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        launch(args);
       
        
    }
    
}