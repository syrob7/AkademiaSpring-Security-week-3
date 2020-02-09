package pl.akademiaspring.week3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pl.akademiaspring.week3.entity.AppUser;
import pl.akademiaspring.week3.service.UserService;


import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {

    private UserService userService;

    public MainController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @RequestMapping("/singup")
    public ModelAndView singup() {
        return new ModelAndView("registration", "user", new AppUser());
    }

    @RequestMapping("/register")
    public ModelAndView register(AppUser user, HttpServletRequest request) {
        if (userService.addNewUser(user, request)) {
            return new ModelAndView("redirect:/login");
        } else {
            ModelAndView modelAndView = new ModelAndView("registration", "user", user);
            modelAndView.addObject("registerError", true);

            return modelAndView;
        }
    }

    @RequestMapping("/verify-token")
    public ModelAndView register(@RequestParam String role, @RequestParam String token) {
        userService.verifyToken(role, token);
        return new ModelAndView("redirect:/login");
    }
}
