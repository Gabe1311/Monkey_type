package task3.task3;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import task3.task3.Load.Dictionary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private static int time =15;
    private static File selectedFile;
    private static ArrayList<Label> letterLabels;
    private static VBox vBox;
    private static ArrayList<HBox> hBoxes;
    private static Stage window;
    private static Dictionary dictionary;
    private static MonkeyType monkeyType;

    public static void main(String[] args) {
        dictionary = new Dictionary();
        monkeyType = new MonkeyType(dictionary);
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;
        stage.setTitle("Monkey Type");
        languageSelectionScene();
        stage.show();
    }
    //Scenes
    public static void languageSelectionScene(){
        try {
            VBox vBox = new VBox();
            HBox hBox1 = new HBox();
            HBox hBox2 = new HBox();
            BorderPane borderPane = new BorderPane();
            Label label = new Label("Select language: ");
            label.setTextFill(Color.AQUAMARINE);
            label.setAlignment(Pos.TOP_CENTER);

            borderPane.setStyle("-fx-background-color: #383838;");

            MenuButton menuButton = selectionMenu();
            MenuButton timeButton = setTimeButton();

            Button startButton = new Button("Start");
            startButton.setOnAction(e -> {
                dictionary.setWords(selectedFile);
                monkeyType.select30RandomWords();
                monkeyType.setTimerLimit(time);
                try {
                    gameScene();
                } catch (Exception exception) {
                    exception.getSuppressed();
                }
            });
            hBox1.getChildren().addAll(label, menuButton, startButton);
            hBox1.setSpacing(10);
            Label label1 = new Label("Select Time: ");
            label1.setTextFill(Color.AQUAMARINE);
            label1.setAlignment(Pos.TOP_CENTER);

            hBox2.getChildren().addAll(label1, timeButton);
            hBox2.setSpacing(10);
            vBox.getChildren().addAll(hBox1, hBox2);
            vBox.setAlignment(Pos.CENTER);
            vBox.setMaxWidth(250);

            borderPane.setCenter(vBox);

            Scene scene = new Scene(borderPane, 500, 420);
            window.setScene(scene);
        }catch (Exception e){
            e.getSuppressed();
        }
    }
    static void gameScene(){
        letterLabels = new ArrayList<>();
        char[] letters = monkeyType.selectedWords.toCharArray();
        for(char a : letters){
            String current = "" + a;
            letterLabels.add(new Label(current));
        }
        BorderPane bp = new BorderPane();
        vBox = new VBox();
        hBoxes = new ArrayList<>();
        setBoxesLayout();
        bp.setCenter(vBox);
        vBox.setAlignment(Pos.CENTER);
        Label commandsLabel = new Label("tab + enter - restart test         ctrl + shift + p - pause         esc - end test" );
        commandsLabel.setPrefSize(500,50);
        commandsLabel.setAlignment(Pos.CENTER);
        bp.setBottom(commandsLabel);

        hBoxes.get(monkeyType.y).getChildren().get(monkeyType.x).setStyle("-fx-background-color: rgba(5,150,133,0.9)");
        Scene scene = new Scene(bp,500,420);
        monkeyType.sethBoxesAndVbox(hBoxes,vBox);
        scene.setOnKeyPressed(MonkeyType :: HandleKeyPresses);
        window.setScene(scene);
    }
    public static void changeToGraphScene(Boolean[] typedLetters, int[] lettersPerSecond, int timer){
        LineChart<Number, Number> lineChart = getLettersPerSecond(lettersPerSecond, timer);
        PieChart pieChart = createPieChart(typedLetters);
        Label averageWpm = new Label("\n \n \n \n  Average WPM: "+ getAverageWPM(typedLetters,timer));
        averageWpm.setAlignment(Pos.CENTER_RIGHT);
        averageWpm.setPrefSize(250,100);
        Button restart = new Button("Restart");
        restart.setOnAction(e->{
            languageSelectionScene();
            MonkeyType.index = 0;
            MonkeyType.x =0;
            MonkeyType.y=0;
            MonkeyType.TimerHandler.timer = 0;
            MonkeyType.selectedWords = "";
                });
        restart.setPrefSize(500,20);
        HBox chartHBox = new HBox(averageWpm,pieChart);
        VBox chartVBox = new VBox(chartHBox,restart,lineChart);
        BorderPane bp = new BorderPane(chartVBox);
        bp.setStyle("-fx-background-color: #02b0a5;");
        Scene scene = new Scene(bp, 500, 420);

        window.setScene(scene);
    }

    //methods to make the components of the various scenes
    private static MenuButton setTimeButton() {
        ArrayList<MenuItem> timeItems = new ArrayList<>();
        String name = " seconds";
        int[] times = {15, 20, 45, 60, 90, 120, 300};
        MenuButton menuButton = new MenuButton("15 seconds",null);
        for (int i = 0; i<times.length; i++){
            timeItems.add(new MenuItem(times[i]+name));
            int finalI = i;
            timeItems.get(i).setOnAction(e->{
                time = times[finalI];
                menuButton.setText(times[finalI]+name);
            });
        }

        menuButton.getItems().addAll(timeItems);
        return menuButton;
    }
    private static void setBoxesLayout() {
        int x = 0, y = 0;
        hBoxes.add(new HBox());
        hBoxes.get(0).setMaxWidth(420);
        for (Label letter:letterLabels){
            if (x >= 40 &&letter.getText().equals(" ")) {
                hBoxes.get(y).getChildren().add(letter);
                vBox.getChildren().add(hBoxes.get(y));
                x = 0;
                y++;
                hBoxes.add(new HBox());
                hBoxes.get(y).setMaxWidth(420);
                letter.setStyle("-fx-text-fill:grey;");
            } else {
                hBoxes.get(y).getChildren().add(letter);
                x++;
                letter.setStyle("-fx-text-fill:grey;");
            }
        }

        vBox.getChildren().add(hBoxes.get(hBoxes.size()-1));
        vBox.setMaxWidth(350);
        vBox.setAlignment(Pos.CENTER);
    }
    private static MenuButton selectionMenu() {
        MenuButton menuButton = new MenuButton("english.txt");
        List<MenuItem> buttons = new ArrayList<>();
        selectedFile = dictionary.languageFiles[5];

        for (File file : dictionary.languageFiles) {
            MenuItem menuItem = new MenuItem(file.getName());
            menuItem.setOnAction(e ->{
                selectedFile = file;
                menuButton.setText(file.getName());
            });

            buttons.add(menuItem);
        }

        menuButton.getItems().addAll(buttons);
        return menuButton;
    }
    private static int getAverageWPM(Boolean[] typedLetters,int timer) {
        int correctLetters = 0;
        int wrongLetters = 0;
        for(int i = 0; i< typedLetters.length;i++){
            if(typedLetters[i]==null)
                break;
            if(typedLetters[i])
                correctLetters++;
            if(!typedLetters[i])
                wrongLetters++;
        }
        if(timer!=0)
            return correctLetters/timer*60/5;
        else
            return 0;
    }
    private static LineChart<Number,Number> getLettersPerSecond(int[] lettersPerSecond, int timer){
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Current WPM");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for(int i = 0; i < timer; i++) {
            series.getData().add(new XYChart.Data<>(i, (lettersPerSecond[i])*60/5));
        }
        lineChart.getData().add(series);
        lineChart.setScaleX(0.8);
        lineChart.setScaleY(0.8);
        lineChart.setStyle("-fx-background-color: #969696");

        return lineChart;
    }
    private static PieChart createPieChart(Boolean[] typedLetters) {
        int correctLetters = 0;
        int wrongLetters = 0;
        for(int i = 0; i< typedLetters.length;i++){
            if(typedLetters[i]==null)
                break;
            if(typedLetters[i])
                correctLetters++;
            if(!typedLetters[i])
                wrongLetters++;
        }
        double correctLettersPercentage =((double) correctLetters /(correctLetters+wrongLetters)*100);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Correct letters " + (int)correctLettersPercentage + "%", correctLetters),
                new PieChart.Data("Wrong Letters "+ (100 - (int)correctLettersPercentage) +"%", wrongLetters)
        );

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Letters chart");
        pieChart.setScaleX(0.8);
        pieChart.setScaleY(0.8);
        return pieChart;
    }
}
