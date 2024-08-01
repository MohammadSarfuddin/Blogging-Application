package com.backendjavacode.blog.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.backendjavacode.blog.entities.User;
import com.backendjavacode.blog.payloads.UserDto;

import javax.validation.constraints.Email;

public interface UserService {

	UserDto registerNewUser(UserDto user);

	UserDto createUser(UserDto user);

	UserDto updateUser(UserDto user, Integer userId);

	UserDto getUserById(Integer userId);

	List<UserDto> getAllUsers();

	void deleteUser(Integer userId);

	void sendSimpleEmail(String to, String subject, String text);

	public Path createTextFile(UserDto userData) throws IOException;

	UserDto updateUserUsingPatch(Integer id, Map<String,Object> userDto);

	void sendScheduledEmail();

	List<String> getAllEmails();

	public void sendEmailsToUsers(List<String> emailAddresses, String emailSubject, String emailBody);

	public void sendMailToALl(String[] toAddress, String emailSubject, String emailBook);

	List<UserDto> searchUsers(String keyword);

	//void saveSingleFile(MultipartFile file);
}
