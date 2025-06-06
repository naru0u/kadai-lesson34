package com.techacademy.controller;

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

import com.techacademy.entity.Employee;
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
    public String list(Model model) {

        model.addAttribute("listSize", reportService.findAll().size());
        model.addAttribute("reportList", reportService.findAll());

        return "reports/list";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable("id") String id, Model model) {

        model.addAttribute("report", reportService.findById(id));
        return "reports/detail";
    }
    
    //*従業員更新画面
    @GetMapping(value = "/{id}/update")
     public String update(@PathVariable("id") String id, Model model,@ModelAttribute Report report) {
    	
    	if (id != null) {
    		report = reportService.findById(id);
    		}
    		model.addAttribute("id", report.getId());
    		report.setTitle(report.getTitle());
    		report.setContent(report.getContent());
    		model.addAttribute("report", report);
    		
    	
    	return "reports/update";
    }

    //*従業員更新処理
    @PostMapping(value = "/{id}/update")
    public String postUpdate(@PathVariable("id") String id, @ModelAttribute @Validated Report report ,BindingResult res,Model model) {
    	
        if (res.hasErrors()) {
            return update(null,model,report);
        }
        
        reportService.update(report);
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
    public String add(@Validated Report report, BindingResult res,@AuthenticationPrincipal UserDetail userDetail) {

    	String name = userDetail.getUsername();
        Employee employee = employeeService.findByCode(name);
        report.setEmployee(employee);
        
        // 入力チェック
        if (res.hasErrors()) {
            return create(report,null);
        }
        
        reportService.save(report);
        return "redirect:/reports";
    }

    // 従業員削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") String id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

    	reportService.delete(id);
    	
        return "redirect:/reports";
    }
    
}
