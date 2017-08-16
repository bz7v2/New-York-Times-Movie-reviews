/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bz7v2moviereviews;

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 *
 * @author Bo 
 */
public class MovieReviewsController implements Initializable {

    private Stage stage;
    @FXML
    private TextField searchTextField;
    @FXML
    private ListView movieListView;
    @FXML
    private FlowPane movieFlowPane;
    @FXML
    private TextArea movieTextArea;
    @FXML
    private ImageView movieImg;
    @FXML
    private TextFlow movieInfo;
    private String searchString = "Star Wars";
    private NYTMoviesManager moviesManager;
    ObservableList<String> movieListItems;
    private ArrayList<NYTMovieReview> movieReviews;
    private NYTMovieReview selectedMovieReview;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private Label label;

    public void ready(Stage stage) {

        this.stage = stage;
        moviesManager = new NYTMoviesManager();
        movieTextArea.setWrapText(true);

        movieListItems = FXCollections.observableArrayList();
        movieListView.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            if ((int) new_val < 0 || (int) new_val > (movieReviews.size() - 1)) {
                return;
            }
            selectedMovieReview = movieReviews.get((int) new_val);
            movieTextArea.setText(selectedMovieReview.summaryShort);
            // show a image, or show no image in local folder
            if (selectedMovieReview.mediaSrc == null) {
                movieImg.setImage(new Image("noImage.jpg"));
            } else {
                movieImg.setImage(new Image(selectedMovieReview.mediaSrc));
            }
            // add information to the text flow
            movieInfo.getChildren().clear();
            Text text1 = new Text("Title: " + selectedMovieReview.displayTitle + "\n\n"
                    + "Headline: " + selectedMovieReview.headline + "\n\n"
                    + "Publication Date: " + selectedMovieReview.publicationDate + "\n\n"
                    + "MPAA Rate: " + selectedMovieReview.mpaaRating + "\n\n"
                    + "Byline: " + selectedMovieReview.byline + "\n\n"
            );

            text1.setFont(new Font("Verdana", 18));
            movieInfo.getChildren().add(text1);
        });

        // put initial search string in searchTextField and load news based
        // on that search
        searchTextField.setText(searchString);
        loadReviews();
    }

    private void loadReviews() {
        try {
            moviesManager.load(searchString);
        } catch (Exception ex) {
            displayExceptionAlert(ex);
            return;
        }

        movieReviews = moviesManager.getMovieReviews();
        movieListItems.clear();

        for (NYTMovieReview movieReview : movieReviews) {
            movieListItems.add(movieReview.displayTitle);
        }
        //newsListView.getItems().clear();
        movieListView.setItems(movieListItems);
        if (movieReviews.size() > 0) {
            movieListView.getSelectionModel().select(0);
            movieListView.getFocusModel().focus(0);
            movieListView.scrollTo(0);
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        if (searchTextField.getText().equals("")) {
            displayErrorAlert("The search field cannot be blank. Enter one or more search words.");
            return;
        }
        searchString = searchTextField.getText();
        System.out.println(searchString);
        loadReviews();
    }

    @FXML
    private void handleAbout(ActionEvent event) {
        displayAboutAlert();
    }

    @FXML
    private void handleWeb(ActionEvent event) {
        try {
            try {
                Desktop.getDesktop().browse(new URI(selectedMovieReview.linkUrl));
            } catch (IOException ex) {
                Logger.getLogger(MovieReviewsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(MovieReviewsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void displayErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void displayExceptionAlert(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText("An Exception Occurred!");
        alert.setContentText("An exception occurred.  View the exception information below by clicking Show Details.");

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    private void displayAboutAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("New York Times Moive Reviews Viewer");
        alert.setContentText("This application was developed by for CS4330 hw at the University of Missouri. Nothing elase to talk about");

        TextArea textArea = new TextArea("The New York Times API is used to obtain a news feed.  Developer information is available at http://developer.nytimes.com. ");
        textArea.appendText("Dale's api-key is used in this application.  If you develop your own applicatyion get your own api-key from the New York Times.");
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

}
