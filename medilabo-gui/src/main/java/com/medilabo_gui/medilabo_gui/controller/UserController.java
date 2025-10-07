//package com.medilabo_gui.medilabo_gui.controller;

// import com.medilabo_gui.medilabo_gui.model.User;
// import com.medilabo_gui.medilabo_gui.services.IUserService;
// import com.medilabo_gui.medilabo_gui.services.JwtTokenService;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;


// @Controller
// public class UserController {

//     private final IUserService userService;
//     private final JwtTokenService jwtTokenService;

    
//     public UserController(IUserService userService, JwtTokenService jwtTokenService) {
//         this.userService = userService;
//         this.jwtTokenService = jwtTokenService;
//     }

//     @GetMapping("/home")
//     public String home(){
//         return "home";
//     }

//     @GetMapping("/login")
//     public String showLoginForm(Model model){
//         model.addAttribute("user", new User());
//         return "login";
//     }

//     @PostMapping("/custom-login")
//     public String login(@ModelAttribute("user") User user, BindingResult result, Model model) {
//             if (userService.authenticate(user.getUsername(), user.getPassword())) {
//                 System.out.println("le token sauvegarde est " +jwtTokenService.getJwtToken());
//                 return "/list";
//             } else {
//                 model.addAttribute("error", "Invalid username or password");
//                 model.addAttribute("user", user);
//                 return "login";
//             }
//     }
// }
