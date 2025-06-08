package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Employee.Role;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {
	
    private final ReportService reportService;
    private final EmployeeService employeeService;
    
    @Autowired
    public ReportController(ReportService reportService,EmployeeService employeeService) {
        this.reportService = reportService;
        this.employeeService = employeeService;
        
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        
        String username = userDetail.getUsername();
        Employee employee = employeeService.findByCode(username);

        List<Report> reports;

        if (employee.getRole() == Role.GENERAL) {
            reports = reportService.findByEmployee(employee);
        } else {
            reports = reportService.findAll();
        }

        model.addAttribute("listSize", reports.size());
        model.addAttribute("reportList", reports);

        return "reports/list";
    }


    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable("id") Integer id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }
    
    //*日報更新画面
    @GetMapping(value = "/{id}/update")
     public String update(@PathVariable("id") Integer id, Model model) {
    	
    	if(id != null) {
    		model.addAttribute("report",reportService.findById(id));
    		}
    	
    	return "reports/update";
    }

    //*日報更新処理
    @PostMapping(value = "/{id}/update")
    public String postUpdate(@PathVariable("id") Integer id, @ModelAttribute @Validated Report report ,BindingResult res,@AuthenticationPrincipal UserDetail userDetail,Model model) {
    	
    	String name = userDetail.getUsername();
        Employee employee = employeeService.findByCode(name);
        report.setEmployee(employee);
    	
        if (res.hasErrors()) {
            return update(null,model);
        }
        
      //重複チェック
        ErrorKinds result = reportService.update(report);

        if (ErrorMessage.contains(result)) {
        	model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return update(null,model);
        }
        
    	return "redirect:/reports";

    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report,@AuthenticationPrincipal UserDetail userDetail) {
    	
    	if (userDetail != null) {
            String name = userDetail.getUsername();
            Employee employee = employeeService.findByCode(name);
            report.setEmployee(employee);
        }
    	
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res,@AuthenticationPrincipal UserDetail userDetail,Model model) {

    	String name = userDetail.getUsername();
        Employee employee = employeeService.findByCode(name);
        report.setEmployee(employee);
        
        // 入力チェック
        if (res.hasErrors()) {
            return create(report,null);
        }
        
        //重複チェック
        ErrorKinds result = reportService.save(report);

        if (result == ErrorKinds.DATECHECK_ERROR) {
        	model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report, null);
        }
        
        return "redirect:/reports";
        
    }

    // 従業員削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

    	reportService.delete(id);
    	
        return "redirect:/reports";
    }
    
}
