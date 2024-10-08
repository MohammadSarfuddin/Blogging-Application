package com.backendjavacode.blog.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.backendjavacode.blog.entities.Role;
import com.backendjavacode.blog.entities.User;
import com.backendjavacode.blog.exceptions.ResourceNotFoundException;
import com.backendjavacode.blog.repositories.RoleRepo;
import com.backendjavacode.blog.repositories.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.backendjavacode.blog.payloads.UserDto;
import com.backendjavacode.blog.services.UserService;
import com.backendjavacode.blog.config.AppConstants;

import javax.mail.internet.MimeMessage;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public UserDto createUser(UserDto userDto) {
		User user = this.dtoToUser(userDto);
		User savedUser = this.userRepo.save(user);
		return this.userToDto(savedUser);
	}

	@Override
	public UserDto updateUser(UserDto userDto, Integer userId) {

		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", " Id ", userId));

		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		user.setAbout(userDto.getAbout());

		User updatedUser = this.userRepo.save(user);
		UserDto userDto1 = this.userToDto(updatedUser);
		return userDto1;
	}

	@Override
	public UserDto getUserById(Integer userId) {

		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", " Id ", userId));

		return this.userToDto(user);
	}

	@Override
	public List<UserDto> getAllUsers() {

		List<User> users = this.userRepo.findAll();
		List<UserDto> userDtos = users.stream().map(user -> this.userToDto(user)).collect(Collectors.toList());

		return userDtos;
	}

	@Override
	public void deleteUser(Integer userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
		this.userRepo.delete(user);

	}

	public User dtoToUser(UserDto userDto) {
		User user = this.modelMapper.map(userDto, User.class);

//		 user.setId(userDto.getId());
//		 user.setName(userDto.getName());
//		 user.setEmail(userDto.getEmail());
//		 user.setAbout(userDto.getAbout());
//		 user.setPassword(userDto.getPassword());
		return user;
	}

	public UserDto userToDto(User user) {
		UserDto userDto = this.modelMapper.map(user, UserDto.class);
		return userDto;
	}

	@Override
	public UserDto registerNewUser(UserDto userDto) {

		User user = this.modelMapper.map(userDto, User.class);

		// encoded the password
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));

		// roles
		Role role = this.roleRepo.findById(AppConstants.NORMAL_USER).get();

//		user.getRoles().add(role);
//		User newUser = this.userRepo.save(user);
//		return this.modelMapper.map(newUser, UserDto.class);

		user.getRoles().add(role);
		User newUser = this.userRepo.save(user);
		return this.modelMapper.map(newUser, UserDto.class);
	}
	@Override
	public void sendSimpleEmail(String to, String subject, String text) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

		try {
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text, false); // Set to false if you don't want HTML content
		} catch (Exception e) {
			e.printStackTrace();
		}
		javaMailSender.send(mimeMessage);
	}
}


