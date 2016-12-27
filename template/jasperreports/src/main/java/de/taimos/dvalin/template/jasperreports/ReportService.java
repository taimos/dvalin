/*
 * Copyright (c) 2016. Taimos GmbH
 *
 */

package de.taimos.dvalin.template.jasperreports;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class ReportService {
    
    public JasperPrint createReport(final String fileName, final Map<String, Object> parameters, Collection data) {
        try {
            parameters.put("datetime", DateTime.now().toString("dd.MM.yyyy HH:mm"));
            JasperReport jr = JasperCompileManager.compileReport(this.getClass().getResourceAsStream("/reports/" + fileName + ".jrxml"));
            return JasperFillManager.fillReport(jr, parameters, new JRBeanCollectionDataSource(data));
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void concatReport(final JasperPrint master, final JasperPrint extraReport) {
        extraReport.getPages().forEach(master::addPage);
    }
    
    public void writeReport(JasperPrint print, OutputStream stream) {
        try {
            JasperExportManager.exportReportToPdfStream(print, stream);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }
    
}
