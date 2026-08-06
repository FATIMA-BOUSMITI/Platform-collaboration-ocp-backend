package com.ocp.organisation_service.controllers;


import com.ocp.organisation_service.dto.request.CreateUserRequest;
import com.ocp.organisation_service.dto.request.UpdateUserRequest;
import com.ocp.organisation_service.dto.response.UserResponse;

import com.ocp.organisation_service.services.UserService;


import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {



	private final UserService userService;



	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse create(
		@Valid @RequestBody CreateUserRequest request
	){

		return userService.createUser(request);

	}



	@GetMapping
	public List<UserResponse> getAll(){

		return userService.getAll();

	}



	@GetMapping("/{id}")
	public UserResponse getById(
		@PathVariable UUID id
	){

		return userService.getUser(id);

	}



	@PutMapping("/{id}")
	public UserResponse update(
		@PathVariable UUID id,
		@Valid @RequestBody UpdateUserRequest request
	){

		return userService.updateUser(
			id,
			request
		);

	}



	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
		@PathVariable UUID id
	){

		userService.deleteUser(id);

	}

}
