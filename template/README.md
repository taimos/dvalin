## templating

This component provides a templating engine based on Velocity and PDF generation based on XDocReport or JasperReports.

### Template engine

For templating the Velocity template engine is used. Inject the `ITemplateResolver` in your bean to process 
templates. You can provide a location relative to the folder `/velocity` in your classpath or you provide 
the template as String.

### PDF generation (XDocReport)

For PDF generation XDocReport can be used. Inject the `ReportService` to create PDF files. You provide a location 
relative to the folder `/xdocreport` in your classpath targeting a docx file. This file is then merged 
with the given context and a PDF is created. 

### PDF generation (JasperReports)

For PDF generation JasperReports can be used. Inject the `ReportService` to create PDF files. You provide a location 
relative to the folder `/reports` in your classpath targeting a jrxml file. This file is then merged 
with the given context and a PDF is created. 
