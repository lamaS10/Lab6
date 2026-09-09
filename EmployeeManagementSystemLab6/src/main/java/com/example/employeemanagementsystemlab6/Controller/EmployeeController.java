package com.example.employeemanagementsystemlab6.Controller;


import com.example.employeemanagementsystemlab6.ApiResponse.ApiResponse;
import com.example.employeemanagementsystemlab6.Model.Employee;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {
    ArrayList<Employee> employees=new ArrayList<>();


    //CRUD METHODS: Read
    @GetMapping("/get-employees")
    public ResponseEntity<?>getEmployees(){
        return ResponseEntity.status(200).body(employees);
    }


    //Create
    @PostMapping("/add-employee")
    public ResponseEntity<?>addEmployee(@RequestBody @Valid Employee employee, Errors errors){
        //handle error
        if(errors.hasErrors()){
            String message=errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        //add employee
        employees.add(employee);
        return ResponseEntity.status(200).body(new ApiResponse("Added successfully"));
    }

    //Update
    @PutMapping("/update-employee/{index}")
    public ResponseEntity<?> updateEmployee(@PathVariable int index,@RequestBody @Valid Employee employee,Errors errors){
        //handle error
        if(errors.hasErrors()){
            String message=errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }
        if (index>=employees.size() ||index<0){
            return ResponseEntity.status(400).body(new ApiResponse("the index is out of bound, should be index in range"));
        }

        //update
        employees.set(index,employee);
        return ResponseEntity.status(200).body(new ApiResponse("updated successfully"));
    }

    //Delete
    @DeleteMapping("/delete-employee/{index}")
    public ResponseEntity<?> deleteEmployee(@PathVariable int index){
        //handle error
        if (index>=employees.size() ||index<0){
            return ResponseEntity.status(400).body(new ApiResponse("the index is out of bound, should be index in range"));
        }

        //delete
        employees.remove(index);
        return ResponseEntity.status(200).body(new ApiResponse("Deleted successfully"));
    }




    //search employees by position
    @GetMapping("/search-by-position/{position}")
    public ResponseEntity<?>searchByPosition(@PathVariable String position){
        //check if position is supervisor or coordinator
        if(position.equals("supervisor")||position.equals("coordinator")){
            ArrayList<Employee>employees1=new ArrayList<>();

            for (Employee employee:employees){
                if(employee.getPosition().equals(position)){
                    employees1.add(employee);
                }
            }

            if(employees1.isEmpty()){
                return ResponseEntity.status(404).body(new ApiResponse("sorry there is no employee for requested position: "+position));
            }

            return ResponseEntity.status(200).body(employees1);
        }

        return ResponseEntity.status(400).body(new ApiResponse("should be either supervisor or coordinator"));
    }


    //search by age
    @GetMapping("/search-by-age/{minAge}/{maxAge}")
    public ResponseEntity<?>searchByAge(@PathVariable int minAge,@PathVariable int maxAge){
        //handle error
        if (minAge <= 25 || maxAge <= 25 || minAge > maxAge) {
            return ResponseEntity.status(400).body(new ApiResponse("please enter valid age range"));
        }

        ArrayList<Employee>employees1=new ArrayList<>();

        //search employee match the age range
        for (Employee employee:employees){
            if(employee.getAge() >= minAge && employee.getAge() <= maxAge){
                employees1.add(employee);
            }
        }

        //if didn't find matched age employees
        if(employees1.isEmpty()){
            return ResponseEntity.status(404).body(new ApiResponse("sorry there is no employee in requested age range"));
        }

        return ResponseEntity.status(200).body(employees1);
    }


    //apply for annual leave
    @PutMapping("/apply-annual-leave/{id}")
    public ResponseEntity<?>applyAnnualLeave(@PathVariable String id){
        //handle error
        if(id.length()<3){
            return ResponseEntity.status(400).body(new ApiResponse("the id should be more than 2"));
        }

        //check if employee exist
        for (Employee employee:employees){
            if (employee.getId().equalsIgnoreCase(id)){

                //check if employee is on leave
                if(employee.isOnLeave()){
                    return ResponseEntity.status(400).body(new ApiResponse("You are already on leave!"));
                }else {
                    //check if employee have annal leave
                    if (employee.getAnnualLeave()>0){
                        employee.setOnLeave(true);
                        employee.setAnnualLeave(employee.getAnnualLeave()-1);
                        return ResponseEntity.status(200).body(new ApiResponse("your request is accepted"));
                    }else {
                        return ResponseEntity.status(400).body(new ApiResponse("you don't have enough annual leave"));
                    }
                }
            }
        }


        return ResponseEntity.status(400).body(new ApiResponse("the id dose not exist"));

    }


    //get employees with no annual leave
    @GetMapping("/get-emp-no-annualLeave")
    public ResponseEntity<?>getEmployeesNoAnnualLeave(){
        //check if there is employee or not
        if(employees.isEmpty()){
            return ResponseEntity.status(404).body(new ApiResponse("There is no employee in the system"));
        }

        ArrayList<Employee>employees1=new ArrayList<>();
        //check and add the employee with no annual leave
        for (Employee employee:employees){
            if (employee.getAnnualLeave()==0){
                employees1.add(employee);
            }
        }

        if(employees1.isEmpty()){
            return ResponseEntity.status(404).body(new ApiResponse("All employee have annual leave"));
        }

        return ResponseEntity.status(200).body(employees1);
    }



    //promote employee
    @PutMapping("/promote-employee/{index}/{id}")
    public ResponseEntity<?>promoteEmployee(@PathVariable int index,@PathVariable String id){
        //handle error
        if (index>=employees.size() ||index<0){
            return ResponseEntity.status(400).body(new ApiResponse("the index is out of bound, should be index in range"));
        }
        if(id.length()<3){
            return ResponseEntity.status(400).body(new ApiResponse("the id should be more than 2"));
        }


        //check if requester is supervisor
        if (employees.get(index).getPosition().equals("supervisor")){

            for (Employee employee:employees){

                //check if id is existed
                if (employee.getId().equalsIgnoreCase(id)){

                    if(employee.getPosition().equals("supervisor")){
                        return ResponseEntity.status(400).body(new ApiResponse("the employee is already promoted"));
                    }
                    //check if age at least 30
                    if (employee.getAge()>=30){

                        //check if is not on leave
                        if (!employee.isOnLeave()){
                            employee.setPosition("supervisor");
                            return ResponseEntity.status(200).body(new ApiResponse("The employee with id: "+employee.getId()+" is promoted to supervisor"));
                        }else {
                            return ResponseEntity.status(400).body(new ApiResponse("the employee now on leave"));
                        }

                    }else {
                        return ResponseEntity.status(400).body(new ApiResponse("The employee age is less than 30"));
                    }

                }
            }

            //if loop end and didn't find the id of employee
            return ResponseEntity.status(404).body(new ApiResponse("the id of employee dose not exist"));

        }

        //if requester is not supervisor so he is not allowed to promote
        return ResponseEntity.status(400).body(new ApiResponse("the requester is not supervisor, so is not allowed to promote"));
    }






    //extra method
    //apply for multiple annual leave days
    @PutMapping("/apply-annual-leave-days/{id}/{days}")
    public ResponseEntity<?> applyAnnualLeaveDays(@PathVariable String id, @PathVariable int days) {
        //handle error
        if(id.length()<3){
            return ResponseEntity.status(400).body(new ApiResponse("the id should be more than 2"));
        }
        if (days <= 0) {
            return ResponseEntity.status(400).body(new ApiResponse("days should be more than 0"));
        }

        for (Employee employee : employees) {

            if (employee.getId().equalsIgnoreCase(id)) {

                if (employee.isOnLeave()) {
                    return ResponseEntity.status(400).body(new ApiResponse("You are already on leave!"));
                }

                //check if employee have enough balance of leave
                if (employee.getAnnualLeave() >= days) {
                    employee.setOnLeave(true);
                    employee.setAnnualLeave(employee.getAnnualLeave() - days);

                    return ResponseEntity.status(200).body(new ApiResponse("Your " + days + " days leave request is accepted"));
                }

                return ResponseEntity.status(400).body(new ApiResponse("You don't have enough annual leave"));
            }
        }

        return ResponseEntity.status(404).body(new ApiResponse("the id does not exist"));
    }









}
