module com.mycompany.sistemagestiontutorias {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.sistemagestiontutorias to javafx.fxml;
    exports com.mycompany.sistemagestiontutorias;
}
