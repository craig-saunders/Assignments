/**
 * 
 */
package com.ss.craig.week.two.weekend.assignment.controllers;

import java.util.Arrays;
import java.util.List;

import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ss.craig.week.two.weekend.assignment.jpaentities.User;
import com.ss.craig.week.two.weekend.assignment.jpaentities.UserRole;
import com.ss.craig.week.two.weekend.assignment.repositories.UserRepository;

/**
 * @author Craig Saunders
 *
 */
@Controller
@RequestMapping(path = "/admin/", produces = MediaType.TEXT_HTML_VALUE)
public class AdminUserController {
    private final String VIEW_EDIT_STR = "What would you like to view/edit?";
    private final String CREATE_YOUR_STR = "Create your user:";
    private final String CHOOSE_OBJ_STR = "Choose the user to ";
    private final String FAILED_STR = " failed: not a valid ";
    private final String OBJECT_STR = "User";
    private final String TEMPLATE_STR = "admin_users";
    private final String MAPPING_STR = "/users";
    
    @Autowired
    private UserRepository object_repo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping(value = MAPPING_STR)
    public String userSubmit(@ModelAttribute User form_result, 
            @RequestParam(name = "form_action", defaultValue = "update")String form_action, 
            @RequestParam(name = "object_id", defaultValue = "")String object_id,
            Model model) throws PropertyValueException
    {
        if (form_action.equals("read"))
        {
            addPostAttributes(model, form_result, object_id, "Read");
        }
        else if (form_action.equals("update"))
        {
            if (!form_result.getPassword().equals("") && !passwordEncoder.matches(form_result.getPassword(), object_repo.findByUsername(form_result.getUsername()).getPassword()))
            {
                form_result.setPassword(passwordEncoder.encode(form_result.getPassword()));
            }
            else
            {
                form_result.setPassword(object_repo.findByUsername(form_result.getUsername()).getPassword());
            }
            addPostAttributes(model, form_result, object_id, "Updated");
        }
        else if (form_action.equals("add"))
        {
            form_result.setPassword(passwordEncoder.encode(form_result.getPassword()));
            addPostAttributes(model, form_result, object_id, "Created");
        }
        else if (form_action.equals("delete"))
        {
            try
            {
                object_repo.delete(object_repo.findById(parseIntSafe(object_id)));
            }
            catch (Exception e)
            {
            }
            model = addPostAttributes(model, form_result, object_id, "Deleted");
        }
        return TEMPLATE_STR;
    }
    
    @GetMapping(value = MAPPING_STR)
    public String adminUsers(
            @RequestParam(name = "action", defaultValue = "choose") String action,
            @RequestParam(name = "object_id", defaultValue = "") String object_id,
            Model model)
    {
        User empty_object = new User();       
        if (action.equals("choose"))
        { 
            model = addGetAttributes(model, Arrays.asList("choices_display"), VIEW_EDIT_STR, empty_object, "", "");
        }
        else if (action.equals("list"))
        {
            model = addListAttributes(model, VIEW_EDIT_STR, (List<User>) object_repo.findAll(), empty_object);
        }
        else if (action.equals("add"))
        { 
            model = addGetAttributes(model, Arrays.asList("form_display"), CREATE_YOUR_STR, empty_object, "add", "");
        }
        else if (action.equals("delete_id"))
        {
            if (object_repo.existsByUsername(object_id))
            {
                model = addGetAttributes(model, Arrays.asList("form_display"), "Delete your "+OBJECT_STR.toLowerCase()+":", object_repo.findByUsername(object_id), "delete", Integer.toString(object_repo.findByUsername(object_id).getId()));
            }
            else
            {
                model = addGetAttributes(model, Arrays.asList("choices_display"), "Delete "+FAILED_STR+" id", empty_object, "choose", "");
            }
        }
        else if (action.equals("delete"))
        {
            model = addGetAttributes(model, Arrays.asList("id_form_display"), CHOOSE_OBJ_STR+"delete:", empty_object, "delete_id", "");
        }
        else if (action.equals("update_id"))
        {
            if (object_repo.existsByUsername(object_id))
            {
                model = addGetAttributes(model, Arrays.asList("form_display"), "Update your "+OBJECT_STR.toLowerCase()+":", object_repo.findByUsername(object_id), "update", Integer.toString(object_repo.findByUsername(object_id).getId()));
            }
            else
            {
                model = addGetAttributes(model, Arrays.asList("choices_display"), "Update "+FAILED_STR+" id", empty_object, "choose", "");
            }
        }
        else if (action.equals("update"))
        {
            model = addGetAttributes(model, Arrays.asList("id_form_display"), CHOOSE_OBJ_STR+"update:", empty_object, "update_id", "");
        }
        else if (action.equals("read_id"))
        {
            if (object_repo.existsByUsername(object_id))
            {
                model = addGetAttributes(model, Arrays.asList("form_display"), "Read your "+OBJECT_STR.toLowerCase()+":", object_repo.findByUsername(object_id), "read", Integer.toString(object_repo.findByUsername(object_id).getId()));
            }
            else
            {
                model = addGetAttributes(model, Arrays.asList("choices_display"), "Read "+FAILED_STR+" id", empty_object, "choose", "");
            }
        }
        else if (action.equals("read"))
        {
            model = addGetAttributes(model, Arrays.asList("id_form_display"),
                    CHOOSE_OBJ_STR+"read:", empty_object, "read_id", "");
        }
        return TEMPLATE_STR;
    }
    
    private int parseIntSafe(String num)
    {
        int id = 0;
        try
        {
            id = Integer.parseInt(num);
        }
        catch(Exception e)
        {   
            id = -1;
        } 
        return id;
    }

    private Model addListAttributes(Model model, String header, List<User> all, User obj)
    {        
        model.addAttribute("choices_display", "display");
        model.addAttribute("header_text", header);
        model.addAttribute("form_result", obj);
        model.addAttribute("form_action", "list_all");
        model.addAttribute("obj_list", all);
        model.addAttribute("object_id", "");
        return model;
    }
    
    private Model addGetAttributes(Model model, List<String> displays,
            String header, User form_result, String form_action, String object_id)
    {
        if(form_result.getUserRole() == null)
        {
            form_result.setUserRole(new UserRole());
        }
        
        displays.stream().forEach(s -> model.addAttribute(s,"display"));
        model.addAttribute("header_text", header);
        model.addAttribute("form_result", form_result);
        model.addAttribute("form_action", form_action);
        model.addAttribute("object_id", object_id);
        return model;        
    }
    
    private Model addPostAttributes(Model model, User form_result, String object_id, String verb)
    {
        if (form_result != null && form_result.getUserRole() != null)
        {
            User result = form_result;
            if (verb.equals("Read") || verb.equals("Updated") || verb.equals("Deleted"))
            {
                try
                {
                    result.setId(parseIntSafe(object_id));
                    if (verb.equals("Updated"))
                    {
                        result = object_repo.save(form_result);
                    }
                }
                catch (Exception e)
                {
                    object_id = Integer.toString(form_result.getId());
                }
            }
            else if (verb.equals("Created"))
            {
                result = object_repo.save(form_result);
            }
            model = addGetAttributes(model, Arrays.asList("result_display","choices_display"), 
                    VIEW_EDIT_STR, result, verb, Integer.toString(result.getId()));
        }
        else
        {                
            model = addGetAttributes(model, Arrays.asList("result_display","choices_display"), 
                    VIEW_EDIT_STR, new User(), "Not "+verb, object_id);
        }
        return model;
    }
}
