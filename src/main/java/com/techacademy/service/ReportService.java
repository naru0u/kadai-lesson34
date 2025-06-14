package com.techacademy.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {
	
	private final ReportRepository reportRepository;

	public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
	}
	
	
	
	// 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }
    
    //一般従業員一覧表示
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }
    
    // 日報登録
    @Transactional
    public ErrorKinds save(Report report) {
    	
    	List<Report> existReports = reportRepository.findByEmployeeAndReportDate(
                report.getEmployee(), report.getReportDate());

            if (!existReports.isEmpty()) {
            	return ErrorKinds.DATECHECK_ERROR;
            }
 
    	report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        
    	reportRepository.save(report);
    	return ErrorKinds.SUCCESS;
    	
    }
    
    //　日報更新
    @Transactional
    public ErrorKinds update(Report report) {
    	Report reports = findById(report.getId());
    	
    	List<Report> exist = reportRepository.findByEmployeeAndReportDate(
                report.getEmployee(), report.getReportDate());

            if (!exist.isEmpty()) {
            	return ErrorKinds.DATECHECK_ERROR;
            }
            

        reports.setTitle(report.getTitle());
        reports.setContent(report.getContent());
        reports.setReportDate(report.getReportDate());
        
        LocalDateTime now = LocalDateTime.now();
        reports.setUpdatedAt(now);
    	reportRepository.save(reports);
    	return ErrorKinds.SUCCESS;
    }
    
    //　日報削除
    @Transactional
    public void delete(Integer id) {
    	Report report = findById(id);
    	
    	report.setDeleteFlg(true); 
        reportRepository.save(report);
    }
    
    
    
        
}
