package com.smalser.pdat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
}
