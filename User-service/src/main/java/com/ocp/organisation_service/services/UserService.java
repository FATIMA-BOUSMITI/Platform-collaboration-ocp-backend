package com.ocp.organisation_service.services;


import com.ocp.organisation_service.dto.request.CreateUserRequest;
import com.ocp.organisation_service.dto.request.UpdateUserRequest;
import com.ocp.organisation_service.dto.response.UserResponse;

import com.ocp.organisation_service.entity.Department;
import com.ocp.organisation_service.entity.User;

import com.ocp.organisation_service.enums.UserStatus;

import com.ocp.organisation_service.mappers.UserMapper;

import com.ocp.organisation_service.repository.DepartmentRepository;
import com.ocp.organisation_service.repository.UserRepository;




import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {


	private final UserRepository userRepository;

	private final DepartmentRepository departmentRepository;

	private final UserMapper userMapper;




	public UserResponse createUser(
		CreateUserRequest request
	){

		if(userRepository.existsByEmail(request.getEmail())){

			throw new RuntimeException(
				"Email already exists"
			);
		}


		User user =
			userMapper.toEntity(request);



		Department department =
			departmentRepository.findById(
					request.getDepartmentId()
				)
				.orElseThrow(
					() -> new RuntimeException(
						"Department not found"
					)
				);


		user.setDepartment(department);


		user.setStatus(
			UserStatus.PENDING
		);


		User saved =
			userRepository.save(user);


		return userMapper.toResponse(saved);

	}




	@Transactional(readOnly = true)
	public UserResponse getUser(UUID id){


		User user =
			userRepository.findById(id)
				.orElseThrow(
					() -> new RuntimeException(
						"User not found"
					)
				);


		return userMapper.toResponse(user);

	}




	@Transactional(readOnly = true)
	public List<UserResponse> getAll(){

		return userRepository.findAll()
			.stream()
			.map(userMapper::toResponse)
			.toList();

	}




	public UserResponse updateUser(
		UUID id,
		UpdateUserRequest request
	){

		User user =
			userRepository.findById(id)
				.orElseThrow(
					() -> new RuntimeException(
						"User not found"
					)
				);
		AuthUserResponse authUser =
			authClient.getUser(request.getAuthUserId());

		if (authUser == null) {
			throw new RuntimeException("User not found in Auth Service");
		}


		userMapper.updateEntity(
			request,
			user
		);


		return userMapper.toResponse(
			userRepository.save(user)
		);

	}




	public void deleteUser(UUID id){

		userRepository.deleteById(id);

	}

}
