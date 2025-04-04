module com.dinidu.lk.pmt {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.mail;
    requires java.sql;
    requires jbcrypt;
    requires org.apache.commons.csv;
    requires org.apache.poi.ooxml;
    requires net.sf.jasperreports.core;
    requires javafx.media;
    requires activation;
    requires org.apache.commons.lang3;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires jakarta.persistence;
    requires java.management;


    opens com.dinidu.lk.pmt.controller to javafx.fxml;
    exports com.dinidu.lk.pmt;
    opens com.dinidu.lk.pmt.entity to org.hibernate.orm.core;
    opens com.dinidu.lk.pmt.controller.dashboard to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.dashboard.project to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.forgetpassword to javafx.fxml;
    opens com.dinidu.lk.pmt.utils to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.dashboard.task to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.dashboard.report to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.dashboard.issue to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.dashboard.task.checklist to javafx.fxml;
    opens com.dinidu.lk.pmt.utils.listeners to javafx.fxml;
    opens com.dinidu.lk.pmt.utils.customAlerts to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.dashboard.timesheet to javafx.fxml;
    opens com.dinidu.lk.pmt.bo.custom.Impl to javafx.fxml;
}