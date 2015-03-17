/*
 * kawi
 *
 * Copyright (C) 2010
 * Ing. Felix D. Lopez M. - flex.developments en gmail
 * 
 * Desarrollo apoyado por la Superintendencia de Servicios de Certificación 
 * Electrónica (SUSCERTE) durante 2010-2014 por:
 * Ing. Felix D. Lopez M. - flex.developments en gmail | flopez en suscerte gob ve
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

package flex.kawi.components.queue;

import flex.eSign.operators.signers.EncodedSignOperator;
import flex.eSign.operators.signers.PDFOperator;
import flex.eSign.operators.exceptions.EncodedSignOperatorException;
import flex.eSign.operators.exceptions.PDFOperadorException;
import flex.helpers.FileHelper;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * KawiQueueOperator
 *
 * @author Ing. Felix D. Lopez M. - flex.developments en gmail
 * @version 1.0
 */
public class KawiQueueOperator {
    
    public static String processDataQueueNodeSign(
        KawiDataQueueNode node,
        String signStandard,
        PrivateKey privateKey,
        X509Certificate certificate,
        String signAlg,
        Date date,
        Provider cryptographyProvider
    ) throws EncodedSignOperatorException {
        /*
         * Se debe discriminar el tipo de tratamiento que se le dará a la
         * data, los tratamientos disponibles se encuentran en 
         * flex.kawi.componente.queue.KawiDataQueue.KAWI_TREATMENT_?
         * (0- String, 1- Local File, 2- Remote File, 3- Remote PDF File)
         */
        String encodedSign = null;
        switch (node.getTreatment()) {
            
            //Tratamiento para string de data
            case KawiDataQueueNode.KAWI_QUEUE_DATA_NODE_TREATMENT_STRING: {
                encodedSign = 
                    EncodedSignOperator.generateSMimeEncodedSignOfString(
                        signStandard, 
                        node.getData().getBytes(), 
                        date,
                        privateKey, 
                        certificate, 
                        signAlg,
                        cryptographyProvider
                );
                break;
            }

            //Tratamiento para ruta de un archivo local
            case KawiDataQueueNode.KAWI_QUEUE_DATA_NODE_TREATMENT_LOCAL_FILE: {
                byte[] bytesFile;
                try {
                    bytesFile = FileHelper.getBytes(node.getData());

                } catch (IOException ex) {
                    throw new EncodedSignOperatorException(ex.getLocalizedMessage());
                }

                encodedSign = 
                    EncodedSignOperator.generateSMimeEncodedSignOfString(
                        signStandard, 
                        bytesFile, 
                        date,
                        privateKey, 
                        certificate, 
                        signAlg,
                        cryptographyProvider
                );
                break;
            }
            
            //Tratamiento para string de un archivo remoto
            case KawiDataQueueNode.KAWI_QUEUE_DATA_NODE_TREATMENT_REMOTE_FILE: {
                encodedSign = 
                    EncodedSignOperator.endSMimeSignFromHexHash(
                        signStandard, 
                        node.getData(), 
                        privateKey, 
                        signAlg
                );
                break;
            }
            
            //Tratamiento para string de un PDF remoto
            case KawiDataQueueNode.KAWI_QUEUE_DATA_NODE_TREATMENT_REMOTE_PDF: {
                //OJO... Falta implementación que se encuentra en desarrollo
                break;
            }
        }
        
        return encodedSign;
    }
    
    public static void processPDFQueueNodeSign(
        KawiPDFQueueNode node,
        String signStandard,
        PrivateKey privateKey,
        X509Certificate certificate,
        String signAlg,
        Date date,
        Provider cryptographyProvider
    ) throws PDFOperadorException, IOException {
        
        byte[] image = FileHelper.getBytes(node.getImgPath());
        File pdfIn = new File(node.getPdfInPath());

        byte[] pdfFirmado = 
            PDFOperator.signLocalPDF(
                PDFOperator.getPdfReader(pdfIn, node.getReadPass()), 
                privateKey, 
                certificate, 
                node.getReadPass(), 
                node.getWritePass(), 
                node.getReason(), 
                node.getLocation(), 
                node.getContact(), 
                date, 
                signAlg,
                node.isNoModify(), 
                node.isVisible(), 
                node.getPage(),
                image,
                node.getImgP1X(),
                node.getImgP1Y(),
                node.getImgP2X(),
                node.getImgP2Y(),
                node.getImgRotation(),
                cryptographyProvider
        );

        FileHelper.write(node.getPdfOutPath(), pdfFirmado);
    }
}
