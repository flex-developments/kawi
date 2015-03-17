/*
 * kawi
 *
 * Copyright (C) 2010
 * Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * 
 * Desarrollo apoyado por la Superintendencia de Servicios de Certificación 
 * Electrónica (SUSCERTE) durante 2010-2014 por:
 * Ing. Felix D. Lopez M. - flex.developments@gmail.com | flopez@suscerte.gob.ve
 * Ing. Yessica De Ascencao - yessicadeascencao@gmail.com | ydeascencao@suscerte.gob.ve
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

package flex.kawi;

import flex.eSign.helpers.ProviderHelper;
import flex.eSign.operators.SMimeCypherOperator;
import flex.eSign.operators.exceptions.SMimeCypherOperatorException;
import flex.kawi.i18n.I18n;
import flex.eSign.operators.signers.PDFOperator;
import flex.eSign.operators.PeriodicCertificateVerifierOperator;
import flex.eSign.operators.components.CertificateVerifierOperatorResults;
import flex.eSign.operators.components.CertificateVerifierConfig;
import flex.eSign.operators.exceptions.EncodedSignOperatorException;
import flex.eSign.operators.exceptions.PDFOperadorException;
import flex.kawi.components.queue.KawiDataQueue;
import flex.kawi.components.pack.KawiPackSignatures;
import flex.kawi.components.pack.KawiPack;
import flex.kawi.components.pack.exceptions.KawiPackException;
import flex.kawi.exception.KawiException;
import flex.helpers.FileHelper;
import flex.helpers.DateHelper;
import flex.helpers.SMimeCoderHelper;
import flex.helpers.VirtualClockHelper;
import flex.helpers.exceptions.DateHelperException;
import flex.helpers.exceptions.SMimeCoderHelperException;
import flex.helpers.exceptions.VirtualClockHelperException;
import flex.kawi.components.queue.KawiPDFQueue;
import flex.kawi.components.queue.KawiQueueOperator;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Kawi
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public class Kawi {
    private static VirtualClockHelper kawiClock = new VirtualClockHelper();
    
    //////////////////////////////// General ///////////////////////////////////
    public static void setKawiClock(VirtualClockHelper clock) {
        kawiClock = clock;
    }
    
    public static VirtualClockHelper getKawiclock() {
        return kawiClock;
    }
    
    private static void validateSignerCertificate(
        CertificateVerifierConfig vConfig, 
        X509Certificate certificate,
        int certificateIntervalVerification
    ) throws KawiException {
        vConfig.setCertificate(certificate);
        CertificateVerifierOperatorResults verifierResults = 
                PeriodicCertificateVerifierOperator.runPeriodicVerificationToNewSignature(
                    vConfig, 
                    certificateIntervalVerification
        );
        
        if(!verifierResults.isAutorized())
            throw new KawiException(verifierResults.getDetails(), verifierResults.getCause());
    }
    ////////////////////////////////////////////////////////////////////////////
    
    //////////////////////////////// KawiPDF ///////////////////////////////////
    @Deprecated
    public static void signLocalPDF(
        String pdfInPath, 
        String pdfOutPath, 
        PrivateKey clientPrivateKey, 
        X509Certificate clientCertificate, 
        String readPass, 
        String writePass, 
        String reason, 
        String location, 
        String contact, 
        String signAlg, 
        boolean noModify, 
        boolean visible, 
        int page,
        String imgPath, 
        float imgP1X, 
        float imgP1Y, 
        float imgP2X, 
        float imgP2Y, 
        int imgRotation,
        CertificateVerifierConfig vcConfig,
        int certificateIntervalVerification,
        Provider cryptographyProvider
    ) throws KawiException {
        //Verificar el certificado antes de firmar
        validateSignerCertificate(
            vcConfig, 
            clientCertificate, 
            certificateIntervalVerification
        );
        
        try {
            byte[] image = FileHelper.getBytes(imgPath);
            File pdfIn = new File(pdfInPath);
            
            byte[] pdfFirmado = 
                PDFOperator.signLocalPDF(
                    PDFOperator.getPdfReader(pdfIn, readPass), 
                    clientPrivateKey, 
                    clientCertificate, 
                    readPass, 
                    writePass, 
                    reason, 
                    location, 
                    contact, 
                    kawiClock.getDate(), 
                    signAlg,
                    noModify, 
                    visible, 
                    page,
                    image,
                    imgP1X,
                    imgP1Y,
                    imgP2X,
                    imgP2Y,
                    imgRotation,
                    cryptographyProvider
            );
            
            FileHelper.write(pdfOutPath, pdfFirmado);
            
        } catch (VirtualClockHelperException | IOException | PDFOperadorException ex) {
            throw new KawiException(ex);
        }
    }
    
    public static HashMap<String, Object> generateSignedPDFFiles(
        String signStandard, 
        KawiPDFQueue queue, 
        PrivateKey clientPrivateKey, 
        X509Certificate clientCertificate, 
        String signAlg,
        CertificateVerifierConfig vcConfig,
        int certificateIntervalVerification,
        Provider cryptographyProvider
    ) throws KawiException {
        HashMap<String, Object> result = new HashMap<>();
        List<String> ids = queue.getIds();
        for(String id : ids) {
            try {   
                //Verificar el certificado antes de firmar
                validateSignerCertificate(
                    vcConfig, 
                    clientCertificate, 
                    certificateIntervalVerification
                );
        
                KawiQueueOperator.processPDFQueueNodeSign(
                    queue.getNode(id), 
                    signStandard, 
                    clientPrivateKey, 
                    clientCertificate, 
                    signAlg, 
                    kawiClock.getDate(),
                    cryptographyProvider
                );
                
                result.put(id, Boolean.TRUE);
                
            } catch (VirtualClockHelperException | IOException | PDFOperadorException ex) {
                result.put(id, new KawiException(ex));
            }
        }
        
        return result;
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ///////////////////////////// KawiPackage //////////////////////////////////
    public static KawiPackSignatures generateKawiPackSignatures(
        String signStandard, 
        KawiDataQueue queue,
        PrivateKey clientPrivateKey, 
        X509Certificate clientCertificate, 
        String signAlg,
        CertificateVerifierConfig vcConfig,
        int certificateIntervalVerification,
        Provider cryptographyProvider
    ) throws KawiException, 
             NoSuchAlgorithmException, 
             DateHelperException, 
             KawiPackException 
    {
        
        KawiPackSignatures signatures = new KawiPackSignatures();
        List<String> ids = queue.getIds();
        for(String id : ids) {
            //Verificar el certificado antes de firmar
                validateSignerCertificate(
                    vcConfig, 
                    clientCertificate, 
                    certificateIntervalVerification
                );
                
            /*
             * Se debe discriminar el tipo de tratamiento que se le dará a la
             * data, los tratamientos disponibles se encuentran en 
             * flex.kawi.componente.queue.KawiDataQueue.KAWI_TREATMENT_?
             * (0- String, 1- Local File, 2- Remote File, 3- Remote PDF File)
             */
            Date date = null;
            String encodedSign = null;
            try {
                date = Kawi.getKawiclock().getDate();
                cryptographyProvider = ProviderHelper.getRegCryptographyProviderOrDefault(cryptographyProvider);
                
                encodedSign = KawiQueueOperator.processDataQueueNodeSign(
                                    queue.getNode(id), 
                                    signStandard, 
                                    clientPrivateKey, 
                                    clientCertificate, 
                                    signAlg,
                                    date,
                                    cryptographyProvider
                );
                
            } catch (EncodedSignOperatorException | VirtualClockHelperException ex) {
                encodedSign = KawiException.ERROR_GENERAL_PREFIX + ex.getMessage();
            }
            
            signatures.add(
                id, 
                encodedSign, 
                DateHelper.dateToString(date)
            );
        }

        return signatures;
    }
    
    public static String encryptSMimeKawiPack(
        String stringPack, 
        X509Certificate ucaimaCertificate,
        CertificateVerifierConfig vcConfig,
        int certificateIntervalVerification
    ) throws KawiException {
        try {
            //Verificar el certificado antes de firmar
                try {
                    validateSignerCertificate(
                        vcConfig, 
                        ucaimaCertificate, 
                        certificateIntervalVerification
                    );

                } catch (KawiException ex) {
                    throw new KawiException(
                            I18n.get(
                                I18n.M_ERROR_SERVER_CERTIFICATE_VERIFICATION, 
                                ex.getLocalizedMessage()
                            )
                    );
                }
                
            //Cifrar paquete kawi
            byte[] encryptedPack = SMimeCypherOperator.encryptData(
                    stringPack.getBytes(),
                    ucaimaCertificate
            );
            
            return SMimeCoderHelper.getSMimeEncoded(encryptedPack);
            
        } catch (IOException | SMimeCypherOperatorException | SMimeCoderHelperException ex) {
            throw new KawiException(ex);
        }
    }
    
    /**
     * 
     * @param encryptedPack
     * @param serverPrivateKey
     * @return Contenido del paquete Kawi en formato XML.
     * @throws KawiException 
     */
    public static String decryptSMimeKawiPack(
        String encryptedPack, 
        PrivateKey serverPrivateKey
    ) throws KawiException {
        try {
            if(encryptedPack == null) encryptedPack = "";
            
            if(encryptedPack.isEmpty())
                throw new KawiException(I18n.get(I18n.M_ERROR_EMPTY_PACK));
            
            //Decodificar los datos recibidos
            byte[] dataCCaux = SMimeCoderHelper.getSMimeDecoded(encryptedPack);
            //Descifrar la data decodificada
            byte[] pack = SMimeCypherOperator.decryptData(serverPrivateKey, dataCCaux);
            
            return new KawiPack(pack).toString();
            
        } catch (SMimeCoderHelperException | SMimeCypherOperatorException | KawiPackException ex) {
            throw new KawiException(ex);
        }
    }
    ////////////////////////////////////////////////////////////////////////////
}
