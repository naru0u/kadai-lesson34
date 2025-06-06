package com.techacademy.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 1件を検索
    public Report findById(String id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }
    
    // 日報登録
    @Transactional
    public Report save(Report report) {
 
    	report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        
    	return reportRepository.save(report);
    	
    }
    
    //　日報更新
    @Transactional
    public Report update(Report report) {
    	
        report.setTitle(report.getTitle());
        report.setContent(report.getContent());
        report.setReportDate(report.getReportDate());
        
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
    	return reportRepository.save(report);
    }
    
    //　日報削除
    @Transactional
    public void delete(String id) {
    	reportRepository.deleteById(id);
    }
    
    
    
        
}
