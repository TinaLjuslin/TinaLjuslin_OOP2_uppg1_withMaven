package com.ljuslin.tinaljuslin_oop2_uppg1_withmaven;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainApplication extends Application {
    private boolean timerRunning = false;
    private AtomicInteger seconds = new AtomicInteger(0);
    private AtomicInteger minutes = new AtomicInteger(0);
    private AtomicInteger hours = new AtomicInteger(0);
    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
    private final String FILENAME = "Persons.json";
    private List<Person> persons = new ArrayList<>();
    public MainApplication() {
        try {
            writer.writeValue(new File(FILENAME), persons);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(0);
        }

    }
    /**
     * Creates text fields, labels, buttons and displayes them
     * @param primaryStage, the stage to show everything
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        TabPane tabPane = new TabPane();
        Tab tabPersons = new Tab("Persons");
        tabPersons.setClosable(false);
        Tab tabTimer = new Tab("Timer");
        tabTimer.setClosable(false);
        tabPane.getTabs().addAll(tabPersons, tabTimer);

        Label labelFirstname = new Label("First name:");
        TextField textFirstName = new TextField();
        textFirstName.setPromptText("Anna");
        Label labelLastname = new Label("Last name:");
        TextField textLastName = new TextField();
        textLastName.setPromptText("Andersson");
        Label labelPhoneNumber = new Label("Phone number:");
        TextField textPhoneNumber = new TextField();
        textPhoneNumber.setPromptText("070-1231231");
        Label labelAddress = new Label("Address:");
        TextField textAddress = new TextField();
        textAddress.setPromptText("Anderssonvägen 1, 123 12 Anderstad");

        Button buttonSavePerson = new Button("Save");
        Button buttonGetAllPersons = new Button("Get All Persons");
        Label labelShowPerson = new Label();
        HBox hBoxButtonsPersons = new HBox();
        hBoxButtonsPersons.setSpacing(10);
        hBoxButtonsPersons.getChildren().addAll(buttonSavePerson, buttonGetAllPersons);
        VBox vBoxGetPerson = new VBox();
        vBoxGetPerson.setSpacing(10);
        vBoxGetPerson.setPadding(new Insets(10));
        vBoxGetPerson.getChildren().addAll(labelFirstname, textFirstName, labelLastname,
                textLastName, labelPhoneNumber, textPhoneNumber, labelAddress,
                textAddress, hBoxButtonsPersons);

        VBox vBoxShowPerson = new VBox();
        vBoxShowPerson.setSpacing(20);
        vBoxShowPerson.getChildren().addAll(labelShowPerson);


        Label labelTime= new Label("00:00:00");
        labelTime.setAlignment(Pos.CENTER);
        labelTime.setStyle("-fx-text-fill: purple;" + "-fx-font-size: 20;"
                + "-fx-font-weight: bold;");
        Button buttonStart = new Button("Start");
        Button buttonStop = new Button("Stop");
        Button buttonReset = new Button("Reset");
        HBox hBoxTimerButtons = new HBox();
        hBoxTimerButtons.setSpacing(20);
        hBoxTimerButtons.setAlignment(Pos.CENTER);
        hBoxTimerButtons.getChildren().addAll(buttonStart, buttonStop, buttonReset);
        VBox vBoxTime = new VBox();
        vBoxTime.setSpacing(20);
        vBoxTime.setStyle("-fx-border-color: red;" + "-fx-border-width: 4;"
                + "-fx-border-style: solid;" + "-fx-border-radius: 15;"
                + "-fx-background-radius: 15;" + "-fx-padding: 10;");
        vBoxTime.setAlignment(Pos.CENTER);

        vBoxTime.getChildren().addAll(labelTime, hBoxTimerButtons);
        tabTimer.setContent(vBoxTime);
        VBox vBoxPersons = new VBox();
        vBoxPersons.setSpacing(20);
        vBoxPersons.setAlignment(Pos.CENTER);
        vBoxPersons.getChildren().addAll(vBoxGetPerson, vBoxShowPerson);
        vBoxPersons.setPadding(new Insets(10));
        tabPersons.setContent(vBoxPersons);
        Scene sceneRoot = new Scene(tabPane, 500, 500);
        primaryStage.setScene(sceneRoot);

        buttonGetAllPersons.setOnAction( ae -> {
            showAllPersons(vBoxShowPerson);
            textFirstName.requestFocus();
        });
        buttonStart.setOnAction(ae -> {
            startTimer(labelTime);

        });
        buttonStop.setOnAction(ae -> {
            stopTimer();
        });
        buttonReset.setOnAction(ae -> {
            resetTimer(labelTime);
        });
        buttonSavePerson.setOnAction(ae ->{
            getPerson(textFirstName, textLastName, textPhoneNumber, textAddress, labelShowPerson,
                    vBoxShowPerson);
            textFirstName.requestFocus();
        });
        primaryStage.show();
    }

    /**
     * Starts the timer in a new thread, checks seconds and minutes to not go over 59, changes
     * text on label
     * @param labelTime the label to change
     */
    private void startTimer(Label labelTime) {
        if (timerRunning) {
            return;
        }
        timerRunning = true;
        Thread thread = new Thread(() -> {
            while(timerRunning) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                int h, min, sec;
                if (timerRunning) {
                    if (seconds.get() == 59) {
                        seconds.set(0);
                        sec = seconds.get();
                        if (minutes.get() == 59) {
                            minutes.set(0);
                            min = minutes.get();
                            h = hours.incrementAndGet();
                        } else {
                            h = hours.get();
                            min = minutes.incrementAndGet();
                        }
                    } else {
                        h = hours.get();
                        min = minutes.get();
                        sec = seconds.incrementAndGet();
                    }
                    Platform.runLater(() -> {
                        String s = String.format("%02d:%02d:%02d", h, min, sec);
                        labelTime.setText(s);
                    });
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Stops the timer
     */
    private void stopTimer() {
        timerRunning = false;
    }

    /**
     * Resets the timer and changes label to 00:00:00
     * @param labelTime label to change
     */
    private void resetTimer(Label labelTime) {
        timerRunning = false;
        hours.set(0);
        minutes.set(0);
        seconds.set(0);
        Platform.runLater(() -> {
            labelTime.setText("00:00:00");
        });
    }
    private void showAllPersons(VBox vBoxShowPerson) {

        TableView<Person> tablePersons = new TableView<>();
        TableColumn<Person, String> columnFirstName = new TableColumn<>("First name");
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<Person, String> columnLastName = new TableColumn<>("Last name");
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        TableColumn<Person, String> columnPhoneNumber = new TableColumn<>("Phone number");
        columnPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        TableColumn<Person, String> columnAddress = new TableColumn<>("Address");
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        tablePersons.getColumns().addAll(columnFirstName, columnLastName, columnPhoneNumber,
                columnAddress);
        try {
            persons = Arrays.asList(mapper.readValue(new File(FILENAME), Person[].class));
            ObservableList<Person> printList = FXCollections.observableArrayList(persons);
            tablePersons.setItems(printList);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        vBoxShowPerson.getChildren().clear();
        vBoxShowPerson.getChildren().add(tablePersons);
    }

    private void getPerson(TextField textFirstName, TextField textLastName,
                           TextField textPhoneNumber, TextField textAddress,
                           Label labelShowPerson, VBox vBoxShowPerson) {
        String firstName, lastName, phoneNumber, address;
        firstName = textFirstName.getText();
        lastName = textLastName.getText();
        phoneNumber = textPhoneNumber.getText();
        address = textAddress.getText();
        if (firstName.isEmpty()) {
            labelShowPerson.setText("Please enter first name");
        } else if (lastName.isEmpty()) {
            labelShowPerson.setText("Please enter last name");
        } else if (phoneNumber.isEmpty()) {
            labelShowPerson.setText("Please enter phone number");
        } else {
            Person person = new Person(firstName, lastName, phoneNumber, address);
            try {
                persons = new ArrayList<>(Arrays.asList(mapper.readValue(new File(FILENAME),
                        Person[].class)));
                persons.add(person);
                writer.writeValue(new File(FILENAME), persons);
                labelShowPerson.setText("Person saved: " + person.toString());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            textFirstName.clear();
            textLastName.clear();
            textPhoneNumber.clear();
            textAddress.clear();

        }
        vBoxShowPerson.getChildren().clear();
        vBoxShowPerson.getChildren().add(labelShowPerson);


    }
}
