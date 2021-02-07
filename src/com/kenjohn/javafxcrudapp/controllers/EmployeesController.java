package com.kenjohn.javafxcrudapp.controllers;

import com.kenjohn.javafxcrudapp.models.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class EmployeesController implements Initializable {

    @FXML
    TableView<Employee> tvEmployee;
    @FXML
    TableColumn<Employee, Integer> colEmployeeId;
    @FXML
    TableColumn<Employee, String> colFirstName;
    @FXML
    TableColumn<Employee, String> colLastName;
    @FXML
    TableColumn<Employee, String> colPosition;
    @FXML
    TextField txtFirstname;
    @FXML
    TextField txtLastname;
    @FXML
    TextField txtPosition;
    @FXML
    TextField txtSearch;
    private int selectedId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showEmployees();
    }

    public void buttonAddClicked() {
        insertEmployee();
    }

    public void buttonUpdateClicked(){
        updateEmployee();
    }

    public void buttonDeleteClicked() {
        deleteEmployee();
    }

    public void tableViewClicked() {
        Employee employee = tvEmployee.getSelectionModel().getSelectedItem();

        if(employee == null) return;

        selectedId = employee.getId();
        txtFirstname.setText(employee.getFirstName());
        txtLastname.setText(employee.getLastName());
        txtPosition.setText(employee.getPosition());
    }

    public void txtSearchChanged() {
        searchEmployee();
    }

    private Connection getConnection(){
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javafxCrud", "root", "1234");
            return conn;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    private ObservableList<Employee> getEmployeesList(String query) {
        ObservableList<Employee> employeesList = FXCollections.observableArrayList();
        Connection conn = getConnection();

        Statement st;
        ResultSet rs;

        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Employee employee;

            while(rs.next()){
                employee = new Employee(rs.getInt("employee_id"),
                                        rs.getString("firstname"),
                                        rs.getString("lastname"),
                                        rs.getString("position"));
                employeesList.add(employee);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return employeesList;
    }

    private void showEmployees() {
        var employeesList = getEmployeesList("SELECT * FROM employees");

        colEmployeeId.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<Employee, String>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<Employee, String>("lastName"));
        colPosition.setCellValueFactory(new PropertyValueFactory<Employee, String>("position"));

        tvEmployee.setItems(employeesList);
    }

    private void insertEmployee() {
        executeQuery("INSERT INTO employees (firstname, lastname, position) VALUES" +
                " ('" + txtFirstname.getText() + "', '" + txtLastname.getText() + "'," +
                "'" + txtPosition.getText() + "')");
        clearText();
        showEmployees();
    }

    private void updateEmployee() {
        executeQuery("UPDATE employees SET firstname = '" + txtFirstname.getText() + "', lastname = '" + txtLastname.getText() + "', " +
                "position = '" + txtPosition.getText() + "' WHERE employee_id = " + selectedId);
        clearText();
        showEmployees();
    }

    private void deleteEmployee() {
        executeQuery("DELETE FROM employees WHERE employee_id = " + selectedId);
        clearText();
        showEmployees();
    }

    private void searchEmployee() {
        var employeesList = getEmployeesList("SELECT * FROM employees WHERE firstname LIKE '%" + txtSearch.getText() + "%'");
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("id"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<Employee, String>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<Employee, String>("lastName"));
        colPosition.setCellValueFactory(new PropertyValueFactory<Employee, String>("position"));

        tvEmployee.setItems(employeesList);
    }

    private void clearText() {
        txtLastname.clear();
        txtFirstname.clear();
        txtPosition.clear();
    }

    private void executeQuery(String query) {
        Connection conn = getConnection();
        Statement st;

        try {
            st = conn.createStatement();
            st.executeUpdate(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

}
