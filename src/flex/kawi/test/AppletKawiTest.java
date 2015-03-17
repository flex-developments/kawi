/*
 * kawi
 *
 * Copyright (C) 2010
 * Ing. Felix D. Lopez M. - flex.developments en gmail
 * 
 * Desarrollo apoyado por la Superintendencia de Servicios de Certificación 
 * Electrónica (SUSCERTE) durante 2010-2014 por:
 * Ing. Felix D. Lopez M. - flex.developments en gmail | flopez en suscerte gob ve
 * Ing. Yessica De Ascencao - yessicadeascencao en gmail | ydeascencao en suscerte gob ve
 *
 * Este programa es software libre; Usted puede usarlo bajo los terminos de la
 * licencia de software GPL version 2.0 de la Free Software Foundation.
 *
 * Este programa se distribuye con la esperanza de que sea util, pero SIN
 * NINGUNA GARANTIA; tampoco las implicitas garantias de MERCANTILIDAD o
 * ADECUACION A UN PROPOSITO PARTICULAR.
 * Consulte la licencia GPL para mas detalles. Usted debe recibir una copia
 * de la GPL junto con este programa; si no, escriba a la Free Software
 * Foundation Inc. 51 Franklin Street,5 Piso, Boston, MA 02110-1301, USA.
 */

package flex.kawi.test;

import flex.eSign.helpers.AlgorithmsHelper;
import flex.helpers.VirtualClockHelper;
import flex.helpers.exceptions.SystemHelperException;
import flex.helpers.exceptions.VirtualClockHelperException;
import flex.kawi.applet.AppletKawi;
import flex.kawi.components.queue.KawiDataQueueNode;
import flex.kawi.exception.KawiException;
import flex.eSign.test.TestsResources;
import java.util.ArrayList;
import java.util.List;

/**
 * AppletKawiTest
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @author Ing. Yessica De Ascencao - yessicadeascencao en gmail
 * @version 1.0
 */
public class AppletKawiTest {
    
    public static void main(String[] args) throws Exception {
//        virtualClockTest();
        probarKawiGenerarPaqueteKawi();
//        KawiSignLocalPDFDeprecatedTest();
//        KawiSignLocalPDFTest();
        
        System.out.println("End!");
        System.exit(0);
    }
    
    private static void virtualClockTest() throws VirtualClockHelperException, InterruptedException {
        List<String> ntps = new ArrayList<>();
        ntps.add("test");
        ntps.add("0.pool.ntp.org");
        ntps.add("tecnica.lab");
        ntps.add("servicios.lab");
        
        //Local
        //VirtualClockHelper vc = new VirtualClockHelper();
        //NTP
        VirtualClockHelper vc = new VirtualClockHelper(ntps, 6000);
        while(true) {
            System.out.println(vc.getDateSource() + " -- " + vc.getDate());
            Thread.sleep(1000);
        }
    }
    
    private static void probarKawiGenerarPaqueteKawi() throws KawiException, SystemHelperException {
        //Levantar el applet y dejo la configuración por defecto
        AppletKawi applet = new AppletKawi();
        applet.setConfiguration(TestsResources.getEncodedRepositoryConfiguration());
        
        //Cargar la data a firmar
        addData(applet, "test1", "test1", 0);
        addData(applet, "test2", "test2", 0);
        addData(applet, "file1", TestsResources.resourcesPath + "prueba.pdf", KawiDataQueueNode.KAWI_QUEUE_DATA_NODE_TREATMENT_LOCAL_FILE);
        
        //Generar paquete kawi cifrado
        String result = applet.generateKawiPack();
        
        if (!result.startsWith(KawiException.ERROR_GENERAL_PREFIX))
            System.out.println(result);
    }
    
    private static void addData(AppletKawi applet, String id, String data, int treatment) throws KawiException {
        String result = applet.addData(id, data, treatment);
        if(result.compareTo("TRUE") != 0)
            throw new KawiException(result);
    }
    
    @Deprecated
    private static void KawiSignLocalPDFDeprecatedTest() throws Exception {
        //Levantar el applet y dejo la configuración por defecto
        AppletKawi applet = new AppletKawi();
        
//        applet.setConfiguration(TestsResources.getEncodedRepositoryConfiguration());
//        applet.setConfiguration("");
        
        String result = applet.signLocalPDF(TestsResources.resourcesPath + "prueba.pdf", 
            TestsResources.resourcesPath + "prueba-Firmado.pdf", 
            null, 
            null, 
            "razon", 
            "location", 
            "contact",
            AlgorithmsHelper.SIGN_ALGORITHM_SHA1_RSA, 
            true, 
            true, 
            1, 
            TestsResources.resourcesPath + "fondo_firma.png", 
            1, 
            1, 
            200, 
            200, 
            0
        );
        
        System.out.println(result);
    }
    
    private static void KawiSignLocalPDFTest() throws Exception{
        //Levantar el applet y dejo la configuración por defecto
        AppletKawi applet = new AppletKawi();
        
        applet.setConfiguration(TestsResources.getEncodedRepositoryConfiguration());
//        applet.setConfiguration("");
        
        applet.addPDF(
            "idPDF1",
            TestsResources.resourcesPath + "prueba1.pdf", 
            TestsResources.resourcesPath + "prueba-Firmado.pdf", 
            null, 
            null, 
            "razon", 
            "location", 
            "contact",
            false, 
            true, 
            1, 
            TestsResources.resourcesPath + "fondo_firma.png", 
            1, 
            1, 
            100, 
            100, 
            0
        );
        
        applet.addPDF(
            "idPDF2",
            TestsResources.resourcesPath + "modificado.pdf", 
            TestsResources.resourcesPath + "modificado-Firmado.pdf", 
            null, 
            null, 
            "razon", 
            "location", 
            "contact",
            false, 
            true, 
            1, 
            TestsResources.resourcesPath + "fondo_firma.png", 
            1, 
            1, 
            100, 
            100, 
            0
        );
        
        String result = applet.generateSignedPDFFiles();
        
        System.out.println(result);
    }
}
