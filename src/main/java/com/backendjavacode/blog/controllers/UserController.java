package com.backendjavacode.blog.controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.backendjavacode.blog.payloads.ApiResponse;
import com.backendjavacode.blog.payloads.UserDto;
import com.backendjavacode.blog.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private JavaMailSender javaMailSender;

	// POST-create user
	@PostMapping("/User")
	public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
		List<String> allEmails = userService.getAllEmails();
		if (allEmails.stream().anyMatch(email -> email.equals(userDto.getEmail()))) {
			System.out.println("Email Already Exists !!");
			return new ResponseEntity<>(userDto,HttpStatus.BAD_REQUEST);
		}
			Random random = new Random();
			// Create a StringBuilder to store the OTP
			StringBuilder otp = new StringBuilder();
			for (int i = 0; i < 5; i++) {
				otp.append(random.nextInt(10));
			}
			String to = userDto.getEmail();
			String subject = "API Development";
			String message = "Congratulations !! You have Successfully Created your Account ..  This is your Required OTP ++ " + otp;
			userService.sendSimpleEmail(to, subject, message);
			UserDto createUserDto = this.userService.createUser(userDto);

			return new ResponseEntity<>(createUserDto, HttpStatus.CREATED);
	}

	// Email Scheduler for send the email on particular time
	@PostMapping("/sendScheduledMail")
	public ResponseEntity<UserDto> sendScheduledMail(@Valid @RequestBody UserDto userDto){
		System.out.println("This is email   "+ userDto.getEmail());
		 this.userService.sendScheduledEmail();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// PUT- update user
	@PutMapping("/{userId}")
	public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable("userId") Integer uId) {
		UserDto updatedUser = this.userService.updateUser(userDto, uId);
		return ResponseEntity.ok(updatedUser);
	}

	@PatchMapping("/updateUsingPatch/{userId}")
	public ResponseEntity<UserDto> updateUserByPatchMapping(@Valid @PathVariable("userId") Integer id,
															@RequestBody Map<String, Object> userDto){
		return ResponseEntity.ok(this.userService.updateUserUsingPatch(id,userDto));
	}

	//ADMIN
	// DELETE -delete user
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Integer uId) {
		this.userService.deleteUser(uId);
		return new ResponseEntity<ApiResponse>(new ApiResponse("User deleted Successfully", true), HttpStatus.OK);
	}

	// GET - user get
	@GetMapping("/")
	public ResponseEntity<List<UserDto>> getAllUsers()
	{
		return ResponseEntity.ok(this.userService.getAllUsers());
	}

	// GET - user get
	@GetMapping("/{userId}")
	public ResponseEntity<UserDto> getSingleUser(@PathVariable Integer userId) {
		return ResponseEntity.ok(this.userService.getUserById(userId));
	}

	@PostMapping("/create-text-file")
	public ResponseEntity<?> createTextFile(@RequestBody UserDto userData) throws IOException {
		Path fileBytes = userService.createTextFile(userData);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document.txt");
		headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
		ResponseEntity<?> tResponseEntity = new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
		return tResponseEntity;
	}

	@GetMapping("/getAllEmails")
	public ResponseEntity<List<String>> getAllEmails(){

		return ResponseEntity.ok(this.userService.getAllEmails());
	}

	@GetMapping("/send-email-to-all")
	public ResponseEntity<?> sendEmailToAllUsers() {
		List<String> emailAddresses = userService.getAllEmails();
		String[] emailArray = emailAddresses.toArray(new String[0]);
		if (emailArray != null){
		userService.sendMailToALl(emailArray, "Welcome EMAIL !", "Congratulations !! " +
				"You have successfully created your Account." +
				"Kindly Visit to Database to check !! ");
		}
		return ResponseEntity.ok("Email Sent Successfully !!");
	}

//	@PostMapping("/uploadSingleFiles")
//	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
//		try {
//			userService.saveSingleFile(file);
//			return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully: " + file.getOriginalFilename());
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
//		}
//	}
//	implementing the search api by the user name.
	@GetMapping("/search/{keyword}")
	public ResponseEntity<List<UserDto>> searchUserByEmail(@PathVariable("keyword") String keyword) {
		List<UserDto> searchResult = this.userService.searchUsers(keyword);
		return new ResponseEntity<List<UserDto>>(searchResult, HttpStatus.OK);
	}
}
