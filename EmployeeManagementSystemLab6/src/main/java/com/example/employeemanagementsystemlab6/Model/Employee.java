package com.example.employeemanagementsystemlab6.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Employee {
    @NotEmpty(message = "the id can't be empty")
    @Size(min = 3,message = "the id should be more than 2")
    private String id;

    @NotEmpty(message = "the name can't be empty")
    @Size(min = 5,message = "the name should be more than 4")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "name should contain only characters")
    private String name;

    @Email(message = "please enter valid email")
    private String email;


    @Size(min = 10,max = 10,message = "the phone number should be exactly 10 digits")
    @Pattern(regexp = "^05\\d{8}$", message = "phone number should start with 05 and be exactly 10 digits")
    private String phoneNumber;

    @NotNull(message = "the age can't be empty")
    @Min(value = 26,message = "the age should be more than 25")
    private int age;

    @NotEmpty(message = "the position can't be empty")
    @Pattern(regexp = "supervisor|coordinator",message = "the position should be either supervisor or coordinator")
    private String position;

    @AssertFalse
    private boolean onLeave;

    @NotNull(message = "the hire date can't be empty")
    @JsonFormat(pattern="yyyy-MM-dd")
    @PastOrPresent(message = "hire date should be date in the past or present")
    private LocalDate hireDate;

    @NotNull(message = "annualLeave can't be empty")
    @Positive(message = "annual leave should be positive number")
    private int annualLeave;
}
