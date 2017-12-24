package spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;

/**
 * describe :
 * Created by jiadu on 2017/10/22 0022.
 */
@Controller
@RequestMapping("/home")
public class HomeContorller {

    @RequestMapping(value = "/go",method = RequestMethod.GET)
    public String go(){
        return "home";
    }

    @RequestMapping(value = "/path_{id}",method = RequestMethod.GET)
    public String path(@PathVariable int id,Model model){
        model.addAttribute("resultID",id);
        return "path";
    }
}
