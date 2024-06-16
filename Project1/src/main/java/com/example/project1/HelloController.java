package com.example.project1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    private TableView<StudentGrade> tblview;

    @FXML
    private TableColumn<StudentGrade, String> idColumn;

    @FXML
    private TableColumn<StudentGrade, String> nameColumn;

    @FXML
    private TableColumn<StudentGrade, String> subjectColumn;

    @FXML
    private TableColumn<StudentGrade, String> gradeColumn;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Button toggleButton;

    @FXML
    private HBox radioButtonHBox;

    private ObservableList<StudentGrade> studentData = FXCollections.observableArrayList();
    private int clickCount = 0; // Counter for button clicks
    private static final int MAX_CLICKS = 5; // Maximum allowed clicks

    @FXML
    private void initialize() {
        // Set up the columns in the table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // Load data from CSV file
        loadCSVData("src/main/java/StudentGrades.csv");

        // Add data to the table
        tblview.setItems(studentData);

        // Initially hide the TableView
        tblview.setVisible(false);
        tblview.setManaged(false);

        // Initially show the BarChart
        barChart.setVisible(true);
        barChart.setManaged(true);

        // Populate the BarChart with data
        populateBarChart();
    }

    private void loadCSVData(String csvFile) {
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Skip the header line
            br.readLine();

            while ((line = br.readLine()) != null) {
                // Split the line using the delimiter
                String[] data = line.split(cvsSplitBy);

                // Ensure there are exactly 4 elements in the data array
                if (data.length == 4) {
                    // Remove quotes from the grade value if present
                    String grade = data[3].replace("\"", "").trim();
                    // Create a StudentGrade object and add to the list
                    StudentGrade studentGrade = new StudentGrade(data[0], data[1], data[2], grade);
                    studentData.add(studentGrade);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Student Grades");

        for (StudentGrade studentGrade : studentData) {
            try {
                String gradeStr = studentGrade.getGrade().trim();
                // Parse the cleaned grade value
                double grade = Double.parseDouble(gradeStr);

                // Check if grade is within a valid range (e.g., 0 to 100, or any other valid range)
                if (grade >= 0 && grade <= 100) {
                    series.getData().add(new XYChart.Data<>(studentGrade.getSubject(), grade));
                } else {
                    System.err.println("Invalid grade value (out of range): " + gradeStr + ". Skipping.");
                }
            } catch (NumberFormatException e) {
                // Handle the case where the grade is not a valid number
                System.err.println("Invalid grade value (parse error): " + studentGrade.getGrade() + ". Error: " + e.getMessage());
            }
        }

        barChart.getData().add(series);
    }

    @FXML
    protected void onHelloButtonClick() {
        boolean isTableViewVisible = tblview.isVisible();

        // Toggle visibility of TableView and BarChart
        tblview.setVisible(!isTableViewVisible);
        tblview.setManaged(!isTableViewVisible);
        barChart.setVisible(isTableViewVisible);
        barChart.setManaged(isTableViewVisible);

        // Toggle visibility of the HBox containing radio buttons
        radioButtonHBox.setVisible(!isTableViewVisible);
        radioButtonHBox.setManaged(!isTableViewVisible);

        // Change the button text based on the visibility
        if (tblview.isVisible()) {
            toggleButton.setText("View Chart");
        } else {
            toggleButton.setText("View Table");
        }

        // Increment the click counter
        clickCount++;
        // Disable the button if the click limit is reached
        if (clickCount >= MAX_CLICKS) {
            toggleButton.setDisable(true);
        }
    }
}
