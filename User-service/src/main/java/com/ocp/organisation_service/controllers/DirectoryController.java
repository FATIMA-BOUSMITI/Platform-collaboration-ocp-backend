package com.ocp.organisation_service.controllers;
import com.ocp.organisation_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/users/directory") @RequiredArgsConstructor
public class DirectoryController {
 private final UserProfileRepository users;
 private final com.ocp.organisation_service.repository.DepartementRepository departments;
 public record Department(UUID id,String name){}
 @GetMapping("/departments") public List<Department> departments(){return departments.findAll().stream().map(d->new Department(d.getId(),d.getName())).toList();}
 public record Person(UUID id,String firstName,String lastName,String email){}
 @GetMapping public List<Person> all(){return users.findAll().stream().map(u->new Person(u.getId(),u.getFirstName(),u.getLastName(),u.getEmail())).toList();}
}
