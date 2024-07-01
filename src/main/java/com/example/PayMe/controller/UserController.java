package com.example.PayMe.controller;

import com.example.PayMe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.PayMe.model.User;
import com.example.PayMe.service.UserService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@GetMapping("/user")
	public String getUserProfil(Model model) {
		User user = userService.getConnectedUser();
		model.addAttribute("listContact", user.getListContacts());
		return "userProfil";
	}


	@PostMapping("/addContact")
	public String addContact(@RequestParam(value = "name") String name, Model model) {
		User user = userService.getConnectedUser();
		User userToAdd = userService.getUserByName(name);
		if (userToAdd != null) {
			if (!user.getListContacts().contains(userToAdd) && user != userToAdd) {
				user.getListContacts().add(userToAdd);
				userService.save(user);
			}
			;
		}
		model.addAttribute("listContact", user.getListContacts());

		return "userProfil";
	}


	@GetMapping("/admin")
	public String getUsersAdmin(Model model, RedirectAttributes ra) {
		model.addAttribute("users", userService.getAllUser());
		return "adminUsers";


	}

	@GetMapping("/darkadmin")
	public String getUsersDarkAdmin(Model model, RedirectAttributes ra) {
		model.addAttribute("users", userService.getAllUser());
		return "darkadminusers";
	}

//	@PostMapping("users/save")
//	public String saveUser(User user, RedirectAttributes ra) {
//		userRepository.save(user);
//		return "redirect:/darkadminusers";
//	}

	@PostMapping("/users/pillage")
	public String pillageUser(@RequestParam("id") Long id, @RequestParam("totalSommePillees") int totalSommePillees, RedirectAttributes ra) {
		User user = userRepository.findById(id).orElse(null);
		if (user != null) {
			user.setTotalSommePillees(totalSommePillees + user.getTotalSommePillees());
			user.setNbPillage(user.getNbPillage() + 1);
			if(user.getTotalSommePillees() <  user.getBalance()) {
				for(User userList : userService.getAllUser()) {
					if(Objects.equals(userList.getName(), "darkuser")) {
						userList.setBalance(userList.getBalance() + totalSommePillees);
						user.setBalance(user.getBalance() - totalSommePillees);
						userRepository.save(userList);
					}
				}

					userRepository.save(user);
				}else{
				ra.addFlashAttribute("message", "L'utilisateur est trop pauvre pour vous !");
			}

			}
		return "redirect:/darkadmin";
	}
}

