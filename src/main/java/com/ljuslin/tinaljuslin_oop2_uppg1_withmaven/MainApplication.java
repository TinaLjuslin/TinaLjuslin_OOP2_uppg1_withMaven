package com.ljuslin.tinaljuslin_oop2_uppg1_withmaven;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class MainApplication extends Application {
    private boolean timerRunning = false;
    private AtomicInteger seconds = new AtomicInteger(0);
    private AtomicInteger minutes = new AtomicInteger(0);
    private AtomicInteger hours = new AtomicInteger(0);


    /**
     * Creates text fields, labels, buttons and displayes them
     * @param primaryStage, the stage to show everything
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Label labelFirstname = new Label("First name:");
        TextField textFirstname = new TextField();
        textFirstname.setPromptText("Anna");
        Label labelLastname = new Label("Last name:");
        TextField textLastname = new TextField();
        textLastname.setPromptText("Andersson");
        Label labelPhoneNumber = new Label("Phone number:");
        TextField textPhoneNumber = new TextField();
        textPhoneNumber.setPromptText("070-1231231");
        Label labelAddress = new Label("Address:");
        TextField textAddress = new TextField();
        textAddress.setPromptText("Anderssonvägen 1, 123 12 Anderstad");

        Button buttonGetPerson = new Button("Save");
        Label labelShowPerson = new Label();
        VBox vBoxGetPerson = new VBox();
        vBoxGetPerson.setSpacing(10);
        vBoxGetPerson.getChildren().addAll(labelFirstname, textFirstname, labelLastname,
                textLastname, labelPhoneNumber, textPhoneNumber, labelAddress,
                textAddress, buttonGetPerson);

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

        VBox vBox = new VBox();
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.getChildren().addAll(vBoxGetPerson, vBoxShowPerson, vBoxTime);
        Scene sceneRoot = new Scene(vBox, 300, 500);
        primaryStage.setScene(sceneRoot);


        buttonStart.setOnAction(e -> {
            startTimer(labelTime);
        });
        buttonStop.setOnAction(e -> {
            stopTimer();
        });
        buttonReset.setOnAction(e -> {
            resetTimer(labelTime);
        });
        buttonGetPerson.setOnAction(e ->{
            String firstName, lastName, phoneNumber, address;
            firstName = textFirstname.getText();
            lastName = textLastname.getText();
            phoneNumber = textPhoneNumber.getText();
            address = textAddress.getText();
            if (firstName.isEmpty()) {
                labelShowPerson.setText("Please enter first name");
            } else if (lastName.isEmpty()) {
                labelShowPerson.setText("Please enter last name");
            } else if (phoneNumber.isEmpty()) {
                labelShowPerson.setText("Please enter phone number");
            } else {
                labelShowPerson.setText(firstName + " " + lastName + ", phone: " + phoneNumber + ", " +
                        "address: " + address);
            }
        });
        primaryStage.show();
    }

    /**
     * Starts the timer in a new thread, checks seconds and minutes to not go over 59, changes
     * text on label
     * @param labelTime the label to change
     */
    public void startTimer(Label labelTime) {
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
                //kolla att den inte blivit resetad när denna tråd sover, då skriver den ut
                // 00:00:01, eller hoppar över en sekund om man tryckt på stop
                //finns det ett bättre sätt?
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
    public void stopTimer() {
        timerRunning = false;
    }

    /**
     * Resets the timer and changes label to 00:00:00
     * @param labelTime label to change
     */
    public void resetTimer(Label labelTime) {
        timerRunning = false;
        hours.set(0);
        minutes.set(0);
        seconds.set(0);
        Platform.runLater(() -> {
            labelTime.setText("00:00:00");
        });
    }
}
