package com.backendjavacode.blog.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.backendjavacode.blog.entities.Role;
import com.backendjavacode.blog.entities.User;
import com.backendjavacode.blog.exceptions.ResourceNotFoundException;
import com.backendjavacode.blog.repositories.RoleRepo;
import com.backendjavacode.blog.repositories.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.backendjavacode.blog.payloads.UserDto;
import com.backendjavacode.blog.services.UserService;
import com.backendjavacode.blog.config.AppConstants;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.Email;

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
	public void sendSimpleEmail(String toAddress, String emailSubject, String emailBook) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		try {
			helper.setTo(toAddress);
			helper.setSubject(emailSubject);
			helper.setText(emailBook, false); // Set to false if you don't want HTML content
		} catch (Exception e) {
			e.printStackTrace();
		}
		javaMailSender.send(mimeMessage);
	}
	@Scheduled(fixedRate = 60000) // 60000 milliseconds = 1 minute
	public void sendScheduledEmail() {
		String to= "vikash@singsys.com";
		String subject="Push Notifications";
		String message="This is Scheduled Message !! ";
		sendSimpleEmail(to,subject,message);
	}

	@Override
	public Path createTextFile(UserDto userData) throws IOException {
		List<String> lines = new ArrayList<>();
		Path filePath = Paths.get("C:\\Users\\hp\\Downloads\\Documents.txt"); // Adjust the path as needed
		// Example of adding multiple strings to the list
		lines.add("Name: " + "\n" + userData.getName()+" ");
		lines.add("Email: "+ "\n" + userData.getEmail()+ " " );
		lines.add("about: "+ "\n"+ userData.getAbout()+ " ");
		// Convert List<String> to a single string with new line separators
		String content = String.join(System.lineSeparator(), lines);
		Path write = Files.write(filePath, content.getBytes());

		return write;
	}

	@Override
	public UserDto updateUserUsingPatch(Integer id, Map<String, Object> userDto) {

		User user = this.userRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User", " Id ", id));

		user.setName(userDto.get("name").toString());
		user.setEmail(userDto.get("email").toString());
		user.setAbout(userDto.get("about").toString());
		User updatedUser = this.userRepo.save(user);
		UserDto userDto1 = this.userToDto(updatedUser);
		return userDto1;
	}

		public List<String> getAllEmails() {
			List<String> allEmails = userRepo.findAll().stream()
					.map(User::getEmail)
					.collect(Collectors.toList());
			return allEmails;
		}

	public void sendEmailsToUsers(List<String> emailAddresses, String emailSubject, String emailBody) {
		for (String email : emailAddresses) {
			sendSimpleEmail(email, emailSubject, emailBody);
		}
	}
	@Override
	public void sendMailToALl(String[] toAddress, String emailSubject, String emailBook) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		try {
			helper.setTo(toAddress);
			helper.setSubject(emailSubject);
			helper.setText(emailBook, false); // Set to false if you don't want HTML content
		} catch (Exception e) {
			e.printStackTrace();
		}
		javaMailSender.send(mimeMessage);
	}

	@Override
	public List<UserDto> searchUsers(String keyword) {
		List<User> users = this.userRepo.findByNameContaining(keyword);
		List<UserDto> userDtos = users.stream().map((User) -> this.modelMapper.map(User, UserDto.class)).collect(Collectors.toList());
		return userDtos;
	}

	//	private final String uploadDir = "C:\\Users\\hp\\Downloads";
//	@Override
//	public void saveSingleFile(MultipartFile file) {
//			try {
//				// Create the directory if it doesn't exist
//				Path directoryPath = Paths.get(uploadDir);
//				if (!Files.exists(directoryPath)) {
//					Files.createDirectories(directoryPath);
//				}
//				// Get the file's original name and construct the full path
//				String originalFileName = file.getOriginalFilename();
//				Path filePath = directoryPath.resolve(originalFileName);
//
//				// Save the file to the specified path
//				Files.write(filePath, file.getBytes());
//
//			} catch (IOException e) {
//				// Handle the exception according to your needs
//				throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
//			}
//		}
}




