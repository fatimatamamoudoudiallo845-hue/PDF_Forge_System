package com.example.gateway;

import org.omg.CORBA.ORB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Configuration
public class CorbaConfig {

    @Value("${corba.host:localhost}")
    private String corbaHost;

    @Value("${corba.port:1050}")
    private String corbaPort;

    @Value("${corba.ior.path:/root/PDF_Forge_System/PDFProcessor.ior}")
    private String iorPath;

    @PostConstruct
    public void init() {
        System.out.println("=== Initialisation CORBA Gateway ===");
        System.out.println("IOR path : " + iorPath);
    }

    @Bean
    public ORB orb() {
        System.setProperty("org.glassfish.gmbal.NO_MONITORING", "true");
        Properties props = new Properties();
        props.put("org.omg.CORBA.ORBClass", "com.sun.corba.ee.impl.orb.ORBImpl");
        props.put("org.omg.CORBA.ORBSingletonClass", "com.sun.corba.ee.impl.orb.ORBSingleton");
        props.put("org.glassfish.gmbal.NO_MONITORING", "true");
        String[] orbArgs = {
            "-ORBInitialHost", corbaHost,
            "-ORBInitialPort", corbaPort
        };
        ORB orb = ORB.init(orbArgs, props);
        System.out.println("[Gateway] ORB CORBA initialise");
        return orb;
    }

    @Bean
    public PDFModule.PDFProcessor pdfProcessor(ORB orb) {
        try {
            String ior = new String(Files.readAllBytes(Paths.get(iorPath))).trim();
            org.omg.CORBA.Object obj = orb.string_to_object(ior);
            PDFModule.PDFProcessor processor = PDFModule.PDFProcessorHelper.narrow(obj);
            System.out.println("[Gateway] PDFProcessor CORBA connecte via IOR");
            return processor;
        } catch (Exception e) {
            System.err.println("[Gateway] CORBA non disponible — mode simulation: " + e.getMessage());
            return null;
        }
    }
}
