package task3.task3;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import task3.task3.Load.Dictionary;

import java.util.ArrayList;

public class MonkeyType {
    private Dictionary dictionary;
    public static String selectedWords;
    private static Boolean[] typedLetters;
    private static int[] lettersPerSecond;
    private static int correctTypedLetters = 0;
    private static Timeline timeline;
    private static char[] wordsInChar;
    public static int x = 0;
    public static int y = 0;
    static int timerLimit = 15;
    static int index = 0;
    private static VBox vBox;
    private static ArrayList<HBox> hBoxes;
    public MonkeyType(Dictionary dictionary){
        this.dictionary = dictionary;
    }

    private static boolean tabPressed = false;

    //collects key presses and checks if input is correct or not, also calls animation methods
    public static void HandleKeyPresses(KeyEvent keyEvent){
        if (index == 0)
            startTimer();
        else
        if (keyEvent.isShortcutDown() && keyEvent.isShiftDown() && keyEvent.getCode() == KeyCode.P) {
            System.out.println("Pause");
            timeline.pause();
            return;
        }else if(timeline.getStatus() == Timeline.Status.PAUSED){
                timeline.play();
        }

        if (keyEvent.getCode() == KeyCode.TAB) {
            tabPressed = true;
            keyEvent.consume();
        } else if (tabPressed && keyEvent.getCode() == KeyCode.ENTER) {
            index = 0;
            x =0;
            y=0;
            TimerHandler.timer = 0;
            timeline.stop();
            System.out.println("Tab + Enter pressed!");
            keyEvent.consume();
            tabPressed = false;
            selectedWords = "";
            Main.languageSelectionScene();
            return;
        } else {
            tabPressed = false;
        }
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            index = 0;
            x =0;
            y=0;
            selectedWords = "";
            Main.changeToGraphScene(typedLetters,lettersPerSecond,TimerHandler.timer);
            TimerHandler.timer = 0;
            timeline.stop();
            System.out.println("End Test");
            keyEvent.consume();

            return;
        }

        try {
            String pressedKey = keyEvent.getText();
            if (index >= wordsInChar.length) {
                Main.changeToGraphScene(typedLetters,lettersPerSecond,TimerHandler.timer);
                return;
            }
            if (pressedKey.equals(String.valueOf(wordsInChar[index]))) {
                typedLetters[index] = true;
                correctTypedLetters++;
                index++;

                hBoxes.get(y).getChildren().get(x).setStyle("-fx-text-fill: green;");
                correctLettersAnimation((Label) hBoxes.get(y).getChildren().get(x));
                x++;

                if (x >= hBoxes.get(y).getChildren().size()) {
                    y++;
                    x = 0;
                }

                hBoxes.get(y).getChildren().get(x).setStyle("-fx-background-color: rgba(5,150,133,0.9)");
                currentLettersAnimation((Label) hBoxes.get(y).getChildren().get(x));

            }
            else if(index != 0 && pressedKey.equals(String.valueOf(wordsInChar[index-1])) ){
                if(pressedKey.equals(" ")){
                    hBoxes.get(y).getChildren().add(x,new Label(pressedKey));
                    hBoxes.get(y).getChildren().get(x).setStyle("-fx-background-color: rgba(192,192,0,0.73)");
                    x++;
                }else {
                    hBoxes.get(y).getChildren().add(x, new Label(pressedKey));
                    hBoxes.get(y).getChildren().get(x).setStyle("-fx-text-fill: #c0c000");
                    x++;
                }
            }
            else {
                typedLetters[index] = false;
                index++;
                hBoxes.get(y).getChildren().get(x).setStyle("-fx-text-fill:red;");
                wrongLettersAnimation((Label) hBoxes.get(y).getChildren().get(x));
                x++;
                if (x >= hBoxes.get(y).getChildren().size()) {
                    y++;
                    x = 0;
                }
                hBoxes.get(y).getChildren().get(x).setStyle("-fx-background-color: rgba(5,150,133,0.9)");
                currentLettersAnimation((Label) hBoxes.get(y).getChildren().get(x));
            }
        }catch (Exception e){
            e.getSuppressed();
        }

    }

    //various animation methods
    private static void correctLettersAnimation(Label label){

            TranslateTransition jumpTransition = new TranslateTransition(Duration.seconds(1), label);
            jumpTransition.setByY(-50);
            jumpTransition.setByX(-25);
            jumpTransition.setCycleCount(1);

            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), label);
            rotateTransition.setByAngle(75);
            rotateTransition.setCycleCount(1);

            TranslateTransition fallAnimation = new TranslateTransition(Duration.seconds(1.5), label);
            fallAnimation.setByY(250);
            fallAnimation.setByX(-25);
            fallAnimation.setCycleCount(1);

            RotateTransition rotateTransition2 = new RotateTransition(Duration.seconds(1.5), label);
            rotateTransition2.setByAngle(120);
            rotateTransition2.setCycleCount(1);

            ParallelTransition parallelTransition1 = new ParallelTransition(jumpTransition, rotateTransition);
            ParallelTransition parallelTransition2 = new ParallelTransition(fallAnimation, rotateTransition2);
            SequentialTransition sequentialTransition = new SequentialTransition(parallelTransition1, parallelTransition2);

            sequentialTransition.play();
    }
    private static void wrongLettersAnimation(Label label){
        try {
            TranslateTransition leftTransition = new TranslateTransition(Duration.seconds(0.25), label);
            leftTransition.setByX(-5);
            leftTransition.setCycleCount(1);
            leftTransition.setAutoReverse(true);


            RotateTransition leftRotate = new RotateTransition(Duration.seconds(0.25), label);
            leftRotate.setByAngle(-45);
            leftRotate.setCycleCount(1);
            leftRotate.setAutoReverse(true);


            ParallelTransition p1 = new ParallelTransition(leftTransition, leftRotate);

            p1.play();
        }catch (Exception e){
        e.getSuppressed();
    }
    }
    private static void currentLettersAnimation(Label label) {
        TranslateTransition upTransition = new TranslateTransition(Duration.seconds(0.1),label);
        upTransition.setByY(-3);
        upTransition.setCycleCount(2);
        upTransition.setAutoReverse(true);

        upTransition.play();
    }

    //time related stuff
    private static void startTimer() {
        Duration duration = Duration.seconds(1);
        KeyFrame keyFrame = new KeyFrame(duration, new TimerHandler());
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(timerLimit);
        lettersPerSecond = new int[timerLimit];

        timeline.play();
    }
    static class TimerHandler implements EventHandler<ActionEvent> {
        static int timer = 0;

        @Override
        public void handle(ActionEvent event) {
            System.out.println(timer);
            try {
                lettersPerSecond[timer] = correctTypedLetters;
            }catch (Exception e){
                e.getSuppressed();
            }
            correctTypedLetters = 0;

            timer++;

            if(index>= wordsInChar.length){
                timeline.stop();
                x=0;
                y =0;
                selectedWords = "";
                Main.changeToGraphScene(typedLetters,lettersPerSecond,timer);
            }
            if(timer >=timerLimit){
                timeline.stop();
                x=0;
                y =0;
                selectedWords = "";
                Main.changeToGraphScene(typedLetters,lettersPerSecond,timer);
            }
        }
    }

    //setters
    public void sethBoxesAndVbox(ArrayList<HBox> hBoxes, VBox vBox){
        MonkeyType.hBoxes = hBoxes;
        MonkeyType.vBox = vBox;
    }
    public void select30RandomWords(){
        selectedWords = dictionary.words.get((int) (Math.random()*dictionary.words.size()));
        for(int i =0; i < 20; i++){
            selectedWords += (" " + dictionary.words.get((int) (Math.random()*dictionary.words.size())));
        }
        wordsInChar = selectedWords.toCharArray();
        typedLetters = new Boolean[wordsInChar.length];
    }
    public void setTimerLimit(int i){
        timerLimit = i;
    }
}
