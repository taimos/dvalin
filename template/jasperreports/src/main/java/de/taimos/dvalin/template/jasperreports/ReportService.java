/*
 * Copyright (c) 2016. Taimos GmbH
 *
 */

package de.taimos.dvalin.template.jasperreports;

/*-
 * #%L
 * Dvalin JasperReports support
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
