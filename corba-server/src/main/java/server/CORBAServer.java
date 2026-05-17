package server;
import PDFModule.PDFProcessorHelper;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import java.io.FileWriter;
import java.util.Properties;

public class CORBAServer {
    public static void main(String[] args) {
        try {
            System.out.println("PDF_Forge - Serveur CORBA");
            System.setProperty("org.glassfish.gmbal.NO_MONITORING", "true");

            Properties props = new Properties();
            props.put("org.omg.CORBA.ORBClass", "com.sun.corba.ee.impl.orb.ORBImpl");
            props.put("org.omg.CORBA.ORBSingletonClass", "com.sun.corba.ee.impl.orb.ORBSingleton");
            props.put("org.glassfish.gmbal.NO_MONITORING", "true");

            // Sans orbd
            String[] orbArgs = {};
            ORB orb = ORB.init(orbArgs, props);
            System.out.println("[CORBA] ORB initialise");

            POA rootPOA = POAHelper.narrow(
                orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();
            System.out.println("[CORBA] POA active");

            PDFProcessorImpl impl = new PDFProcessorImpl();
            byte[] id = rootPOA.activate_object(impl);
            org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);

            // Ecrire l'IOR dans /app/
            String ior = orb.object_to_string(ref);
            String iorPath = "/app/PDFProcessor.ior";

            try (FileWriter fw = new FileWriter(iorPath)) {
                fw.write(ior);
            }

            System.out.println("[CORBA] IOR ecrit dans : " + iorPath);
            System.out.println("[CORBA] Service pret - En attente de requetes...");
            orb.run();

        } catch (Exception e) {
            System.err.println("[CORBA] Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
