package com.smalser.pdat;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SimpleController
{
    @RequestMapping(value = {"/", "/index**"}, method = RequestMethod.GET)
    public ModelAndView welcomePage()
    {
        ModelAndView model = new ModelAndView();
        model.addObject("title", "Spring Security title");
        model.addObject("body", "Hello, Spring!");
        model.addObject("author", "smal");
        model.setViewName("index");
        return model;
    }

    @RequestMapping(value = "/admin**", method = RequestMethod.GET)
    public ModelAndView admin()
    {
        ModelAndView model = new ModelAndView();
        model.addObject("title", "Spring Security title");
        model.addObject("body", "This is protected page!");
        model.setViewName("admin");
        return model;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout)
    {
        ModelAndView model = new ModelAndView();
        if (error != null)
        {
            model.addObject("error", "Wrong username or password");
        }

        if (logout != null)
        {
            model.addObject("msg", "You have been logged out successfully!");
        }

        model.setViewName("login");
        return model;
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public ModelAndView accessDenied()
    {
        ModelAndView model = new ModelAndView();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null)
        {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            model.addObject("username", userDetail.getUsername());
        }

        model.setViewName("403");
        return model;
    }
}
