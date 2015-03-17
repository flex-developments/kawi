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

package flex.kawi.applet;

import flex.eSign.helpers.CertificateHelper;
import flex.eSign.helpers.exceptions.CertificateHelperException;
import flex.eSign.operators.components.CertificateVerifierConfig;
import flex.helpers.DateHelper;
import flex.helpers.StringHelper;
import flex.helpers.SystemHelper;
import flex.helpers.VirtualClockHelper;
import flex.helpers.exceptions.DateHelperException;
import flex.helpers.exceptions.VirtualClockHelperException;
import flex.kawi.Kawi;
import flex.kawi.KawiLogger;
import flex.kawi.components.pack.KawiPack;
import flex.kawi.components.pack.KawiPackHeaders;
import flex.kawi.components.pack.exceptions.KawiPackException;
import flex.kawi.components.queue.KawiDataQueue;
import flex.kawi.components.queue.KawiPDFQueue;
import flex.kawi.exception.KawiException;
import flex.pkikeys.PKIKeys;
import flex.pkikeys.Repositories.AbstractRepositoryConfiguration;
import flex.pkikeys.Repositories.RepositoryConfigurationFactory;
import flex.pkikeys.Repositories.RepositoriesWhiteList;
import flex.pkikeys.exceptions.PKIKeysException;
import flex.pkikeys.exceptions.PKIKeysQuitWinException;
import flex.kawi.i18n.I18n;
import flex.kawi.resources.KawiPropertiesBundle;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 * AppletKawi
 * Applet que recibe los parámetros del consumidor y ejecuta las actividades solicitadas.
 * 
 * @author Ing. Felix D. Lopez M. - flex.developments@gmail.com
 * @author Ing. Yessica De Ascencao - yessicadeascencao@gmail.com
 * @version 1.0
 */
public class AppletKawi extends Applet {
    //Atributos estaticos
    final private static Color KAWI_APPLET_BACKGROUND_COLOR = Color.WHITE;
    final private static String KAWI_CERTIFICATE_PATH_LOCATION = "resources/Cadena.pem";
    final private static String KAWI_REPOSITORIES_WHITE_LIST_LOCATION = "resources/KawiRepositoryWhiteList";
    final private static String UCAIMA_CERTIFICATE_LOCATION = "resources/Ucaima.pem";
    final private static String KAWI_POSSITIVE_ANSWER = "TRUE";
    
    //Atributos que se cargan desde el properties
    private static String APPLET_TITLE = null;
    private static int APPLET_TITLE_SIZE = 10;
    private static String KAWI_SIGN_STANDARD = null;
    private static String KAWI_SIGN_ALG = null;
    private static boolean KAWI_DRIVER_VERIFY = false;
    private static int KAWI_CERTIFICATE_INTERVAL_VERIFICATION = 600000;
    private static int KAWI_NTP_INTERVAL_SYNC = 600000;
    
    //Atributos que se inicializan durante la ejecución del applet
    private static final LinkedHashMap<String, Boolean> KAWI_PACK_HEADERS = new LinkedHashMap<>();
    private static final CertificateVerifierConfig vcConfig = new CertificateVerifierConfig();
    private static RepositoriesWhiteList repositoriesWhiteList = null;
    private static X509Certificate ucaimaCertificate = null;
    
    private KawiDataQueue dataQueue = new KawiDataQueue();
    private KawiPDFQueue pdfQueue = new KawiPDFQueue();
    private List<String> ntpServers = new ArrayList<>();
    private AbstractRepositoryConfiguration configuration = null;
    
    //////////////////////////////// General ///////////////////////////////////
    /**
     * Constructora de la clase que carga las configuraciones necesarias para el
     * funcionamiento del applet.
     */
    public AppletKawi() {
        try {
            //Cargar titulo
                APPLET_TITLE = KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_TITLE);
            //Cargar tamaño de letra del titulo
                APPLET_TITLE_SIZE = Integer.parseInt(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_TITLE_SIZE));
            //Cargar las cabeceras habilitadas para ser incluidas en el paqueteKawi
                StringTokenizer cabeceras = new StringTokenizer(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_PACKAGE_HEADERS), ";");
                while(cabeceras.hasMoreElements()) {
                    KAWI_PACK_HEADERS.put(cabeceras.nextToken(), true);
                }
            //Cargar lista de servidores NTPs
                String[] ntps = KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_NTP_SERVERS).trim().split(",");
                ntpServers.addAll(Arrays.asList(ntps));
            //Cargar intervalo de sincronizacion con servidor NTP
                KAWI_NTP_INTERVAL_SYNC = Integer.parseInt(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_NTP_SYNC_INTERVAL).trim());
            //Cargar el estandard que se utilizara para generar las firmas electronicas
                KAWI_SIGN_STANDARD = KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_SIGN_STANDARD).trim();
            //Cargar el algoritmo para la generacion de las firmas electronicas
                KAWI_SIGN_ALG = KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_SIGN_ALG).trim();
            //Cargar la fuente de hora que se utilizara para la generacion de las firmas electronicas y configurar reloj virtual
                String fuenteHora = KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_DATE_SOURCE).trim();
                switch (fuenteHora) {
                    case VirtualClockHelper.DATE_SOURCE_LOCAL: {
                        Kawi.setKawiClock(new VirtualClockHelper());
                        break;
                    }
                    
                    case VirtualClockHelper.DATE_SOURCE_NTP: {
                        Kawi.setKawiClock(new VirtualClockHelper(ntpServers, KAWI_NTP_INTERVAL_SYNC));
                        break;
                    }
                    
                    default: {
                        throw new VirtualClockHelperException(VirtualClockHelperException.ERROR_DATE_SOURCE_UNKNOWN + "[" + fuenteHora + "]");
                    }
                }
                
            //Cargar parametros para verificacion de certificado
                vcConfig.setVerifyDateWithHost(Boolean.parseBoolean(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_VERIFY_CERTIFICATE_WITH_HOST).trim()));
                vcConfig.setNtpServers(ntpServers);
                vcConfig.setVerifyWithOCSP(Boolean.parseBoolean(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_VERIFY_CERTIFICATE_WITH_OCSP).trim()));
                vcConfig.setInvalidOnNTPFail(Boolean.parseBoolean(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_CERTIFICATE_INVALID_ON_NTP_FAIL).trim()));
                vcConfig.setInvalidOnOCSPFail(Boolean.parseBoolean(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_CERTIFICATE_INVALID_ON_OCSP_FAIL).trim()));
                vcConfig.setInvalidOnCRLFail(Boolean.parseBoolean(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_CERTIFICATE_INVALID_ON_LCR_FAIL).trim()));
                vcConfig.setInvalidOnCRLandOCSPFail(Boolean.parseBoolean(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_CERTIFICATE_INVALID_ON_BOTH_FAIL).trim()));
                vcConfig.setTryDownloadCRL(Boolean.parseBoolean(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_VERIFY_CERTIFICATE_TRY_DOWNLOAD_LCR).trim()));
                vcConfig.setVerbose(Boolean.parseBoolean(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_CERTIFICATE_VERIFY_VERBOSE).trim()));
            
            //Carga intervalo de verificación de certificado
                KAWI_CERTIFICATE_INTERVAL_VERIFICATION = Integer.parseInt(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_VERIFY_CERTIFICATE_INTERVAL).trim());
            //Habilita/Deshabilita la verificacion del repositorio de llaves
                KAWI_DRIVER_VERIFY = Boolean.parseBoolean(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_DRIVER_VERIFY_INTEGRITY).trim());
            //Cargar lista blanca de repositorios
                if (KAWI_DRIVER_VERIFY) {
                    try (InputStream whiteListReader = Kawi.class.getResourceAsStream(KAWI_REPOSITORIES_WHITE_LIST_LOCATION)) {
                        repositoriesWhiteList = new RepositoriesWhiteList(whiteListReader);
                    }
                }
                
            try ( //Cargar cadena de confianza
                InputStream reader = Kawi.class.getResourceAsStream(KAWI_CERTIFICATE_PATH_LOCATION)) {
                vcConfig.setAuthorities(CertificateHelper.loadPEMCertificate(reader));
            }
            
            //Cargar certificado de Ucaima en caso de que se encuentre activa la
            //la opcion para cifrado del paquete kawi
            if(Boolean.parseBoolean(KawiPropertiesBundle.get(KawiPropertiesBundle.KAWI_PACKAGE_ENCRYPT).trim())) {
                InputStream inStream;
                inStream = Kawi.class.getResourceAsStream(UCAIMA_CERTIFICATE_LOCATION);
                ucaimaCertificate = CertificateHelper.loadPEMCertificate(inStream).get(0);
                inStream.close();
            }
                
            //OJO... Falta evaluar valores cargados para evitar NULLs asignando valores por defecto
            
            KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_INITIALIZED));
            
        } catch (IOException | 
                 NumberFormatException | 
                 VirtualClockHelperException | 
                 CertificateHelperException | 
                 PKIKeysException ex) {
            
            KawiLogger.writeErrorLog(ex);
        }
    }
    
    @Override
    /**
     * Inicializar ejecucion del applet.
     */
    public void init() {
        setBackground(KAWI_APPLET_BACKGROUND_COLOR);
    }

    @Override
    /**
     * Visualizar applet.
     */
    public void paint(Graphics g) {
        g.drawString(APPLET_TITLE, 0, APPLET_TITLE_SIZE);
    }
    
    /**
     * Mostrar un cuadro de dialogo de error con el mensaje de una excepcion.
     * 
     * @param ex Excepcion que contiene el mensaje de error.
     * @return String con el mensaje de error de la excepcion.
     */
    @SuppressWarnings("empty-statement")
    private String errorMessage(Throwable ex, boolean showMessage) {
        //Excepciones que se ignoran
        if(ex.getCause() instanceof PKIKeysQuitWinException)
            return PKIKeysQuitWinException.CANCEL_KEY_ACCESS;
        
        String msj = KawiException.ERROR_GENERAL_PREFIX + ex.getMessage();
        if(showMessage) {
            //Emitir mensaje de error
            JOptionPane.showMessageDialog(
                null, 
                msj, 
                APPLET_TITLE + I18n.get(I18n.I_PKCS12_TITLE_SUFIX_ERROR), 
                JOptionPane.ERROR_MESSAGE
            );
            
//            //OJO... Eliminar
//            JOptionPane.showOptionDialog(
//                null,
//                msj,
//                APPLET_TITLE + I18n.get(I18n.I_PKCS12_TITLE_SUFIX_ERROR),
//                JOptionPane.DEFAULT_OPTION,
//                JOptionPane.ERROR_MESSAGE,
//                null,
//                null,
//                null
//            );   
        }
        
        KawiLogger.writeErrorLog(ex);
        return msj;
    }
    
    /**
     * Acceder al repositorio de las llaves que se utilizaran para generar 
     * firmas electrónicas.
     * 
     * @return objeto flex.pkikeys.PKIKeys con las llaves cargadas
     * 
     * @throws PKIKeysException 
     */
    private PKIKeys loadKeys() throws PKIKeysException {
        PKIKeys clientKeys;
        if (configuration == null) clientKeys = new PKIKeys();
        else clientKeys = new PKIKeys(configuration);
        clientKeys.loadFromGUI();
        clientKeys.setWitheListRepositories(repositoriesWhiteList);
        clientKeys.setVerifyRepositoryIntegrity(KAWI_DRIVER_VERIFY);
        
        clientKeys.loadKeys(null);
        return clientKeys;
    }
    
    /**
     * Setear configuracion preexistente de acceso al repositorio de las llaves 
     * que se utilizaran para generar las firmas electronicas.
     * 
     * @param encodedConf Configuracion preexistente codificada.
     * 
     * @return String con mensaje de configuracion exitosa o mensaje de error.
     */
    public String setConfiguration (final String encodedConf) {
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                try {
                    KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_INIT_CONFIGURATION));
                    configuration = RepositoryConfigurationFactory.getInstanceFromEncodedConf(encodedConf);
                    
                    KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_METHOD_EXECUTED));
                    return KAWI_POSSITIVE_ANSWER;
                    
                } catch (PKIKeysException ex) {
                    return errorMessage(ex, false);
                }
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////// PDF /////////////////////////////////////
    /**
     * Agregar pdf a la cola del applet para que se le genere una firma electronica.
     * 
     * @param id
     * @param pdfInPath
     * @param pdfOutPath
     * @param readPass
     * @param writePass
     * @param reason
     * @param location
     * @param contact
     * @param noModify
     * @param visible
     * @param page
     * @param imgPath
     * @param imgP1X
     * @param imgP1Y
     * @param imgP2X
     * @param imgP2Y
     * @param imgRotation
     * @return 
     */
    public String addPDF(
        final String id, 
        final String pdfInPath, 
        final String pdfOutPath, 
        final String readPass, 
        final String writePass, 
        final String reason, 
        final String location, 
        final String contact, 
        final boolean noModify, 
        final boolean visible, 
        final int page,
        final String imgPath, 
        final float imgP1X, 
        final float imgP1Y, 
        final float imgP2X, 
        final float imgP2Y, 
        final int imgRotation
    ) {
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                try {
                    KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_PDF_ADD_TO_QUEUE));
                    pdfQueue.addPDF(
                        id,
                        pdfInPath, 
                        pdfOutPath, 
                        readPass, 
                        writePass, 
                        reason, 
                        location, 
                        contact,  
                        noModify, 
                        visible, 
                        page,
                        imgPath, 
                        imgP1X, 
                        imgP1Y, 
                        imgP2X, 
                        imgP2Y, 
                        imgRotation
                    );
                    
                    KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_METHOD_EXECUTED));
                    return KAWI_POSSITIVE_ANSWER;
                    
                } catch (KawiException ex) {
                    return errorMessage(ex, false);
                }
            }
        });
    }
    
    /**
     * Remover PDF de la cola del applet.
     * 
     * @param id Entero que indica la posicion de la cola que se desea remover.
     * @return 
     */
    public String removePDF(final String id) {
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_PDF_REMOVE_FROM_QUEUE));
                pdfQueue.removePDF(id);
                
                KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_METHOD_EXECUTED));
                return KAWI_POSSITIVE_ANSWER;
            }
        });
    }
    
    /**
     * Remover todos los PDFs de la cola del applet.
     * @return 
     */
    public String clearPDF() {
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_PDF_REMOVE_ALL_FROM_QUEUE));
                pdfQueue.clearPDF();
                
                KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_METHOD_EXECUTED));
                return KAWI_POSSITIVE_ANSWER;
            }
        });
    }
    
    /**
     * Iniciar generacion de PDFs firmados electronicamente.
     * @return 
     */
    public String generateSignedPDFFiles() {
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                PKIKeys clientKeys = null;
                try {
                    KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_PDF_INIT_GEN_SIGNED_LOCAL_PDF));
                    //Acceder a las claves para firmar electrónicamente
                        clientKeys = loadKeys();
                    //Actualizar configuracion del cliente
                        configuration = clientKeys.getRepositoryConfiguration();
                    
                    //Generar PDFs firmados
                    HashMap<String, Object> signResults = 
                        Kawi.generateSignedPDFFiles(
                            KAWI_SIGN_STANDARD, 
                            pdfQueue,
                            clientKeys.getPrivateKey(), 
                            clientKeys.getSignCertificate(), 
                            KAWI_SIGN_ALG,
                            vcConfig,
                            KAWI_CERTIFICATE_INTERVAL_VERIFICATION,
                            clientKeys.getRepositoryCryptographyProvider()
                    );
                    //Finalizar
                    clientKeys.close();
                    
                    String result = "";
                    for (Map.Entry e : signResults.entrySet()) {
                        if(e.getValue() instanceof KawiException) {
                            KawiException ex = (KawiException) e.getValue();
                            String msj = ex.getMessage();
                            result = result + e.getKey() + "=" + msj.replaceAll("\n", "  ") + "\n";
                        } else {
                            result = result + e.getKey() + "=" + KAWI_POSSITIVE_ANSWER + "\n";
                        }
                    }
                    
                    result = StringHelper.removeLastCarrierReturn(result);
                    
                    KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_METHOD_EXECUTED));
                    return result;

                } catch (PKIKeysException | KawiException ex) {
                    if (clientKeys != null) clientKeys.close();
                    return errorMessage(ex, true);
                }
            }
         });
    }
    
    /**
     * Tomar un archivo PDF existente en la rutaOrigen y generar en la
     * rutaDestino el archivo PDF firmado electronicamente.
     * 
     * @param pdfInPath String con la ruta del PDF original.
     * @param pdfOutPath String con la ruta del PDF que se generara firmado electronicamente.
     * @param readPass String con el password de lectura del archivo PDF.
     * @param writePass String con el password de Escritura del archivo PDF.
     * @param reason String con el valor del campo "razon" que aparece en las propiedades de la firma del archivo PDF.
     * @param location String con el valor del campo "locacion" que aparece en las propiedades de la firma del archivo PDF.
     * @param contact String con el valor del campo "contacto" que aparece en las propiedades de la firma del archivo PDF.
     * @param signAlg String con el algoritmo de hash que se utilizará para generar la firma electronica del archivo PDF.
     * @param noModify
     * @param visible Boolean que indica si se agregará o no, un cuadro de firma visible al archivo PDF.
     * @param page Entero que indica el numero de pagina donde aparecera el cuadro de firma visible. El valor 0 indica todas las paginas.
     * @param imgPath String con la ruta de la imagen que aparecerá de fondo en el cuadro de firma visible.
     * @param imgP1X Entero que indica la coordenada P1X del cuadro de firma visible.
     * @param imgP1Y Entero que indica la coordenada P1Y del cuadro de firma visible.
     * @param imgP2X Entero que indica la coordenada P2X del cuadro de firma visible.
     * @param imgP2Y Entero que indica la coordenada P2Y del cuadro de firma visible.
     * @param imgRotation Entero que indica los grados de rotacion del cuadro de firma visible.
     * 
     * @return String con mensaje de firma exitosa o mensaje de error.
     * @deprecated 
     */
    public String signLocalPDF(
        final String pdfInPath, 
        final String pdfOutPath, 
        final String readPass, 
        final String writePass, 
        final String reason, 
        final String location, 
        final String contact, 
        final String signAlg, 
        final boolean noModify, 
        final boolean visible, 
        final int page,
        final String imgPath, 
        final float imgP1X, 
        final float imgP1Y, 
        final float imgP2X, 
        final float imgP2Y, 
        final int imgRotation
    ) {
        
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                PKIKeys clientKeys = null;
                try {
                    KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_PDF_INIT_SIGN_LOCAL_PDF));
                    //Acceder a las claves para firmar electrónicamente
                        clientKeys = loadKeys();
                    //Actualizar configuracion del cliente
                        configuration = clientKeys.getRepositoryConfiguration();
                    //Firmar PDF del lado del cliente
                        Kawi.signLocalPDF(
                            pdfInPath,
                            pdfOutPath,
                            clientKeys.getPrivateKey(),
                            clientKeys.getSignCertificate(), 
                            readPass,
                            writePass,
                            reason,
                            location,
                            contact,
                            signAlg, 
                            noModify, 
                            visible,
                            page,
                            imgPath,
                            imgP1X,
                            imgP1Y,
                            imgP2X,
                            imgP2Y,
                            imgRotation,
                            vcConfig,
                            KAWI_CERTIFICATE_INTERVAL_VERIFICATION,
                            clientKeys.getRepositoryCryptographyProvider()
                        );
                    //Finalizar
                    clientKeys.close();
                    
                    KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_METHOD_EXECUTED));
                    return KAWI_POSSITIVE_ANSWER;

                } catch (PKIKeysException | KawiException ex) {
                    if (clientKeys != null) clientKeys.close();
                    return errorMessage(ex, true);
                }
            }
         });
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////// DATA ////////////////////////////////////
    /**
     * Agregar data a la cola del applet para que se le genere una firma electronica.
     * 
     * @param id
     * @param data String de datos que se procesaran para la generacion de la firma electronica.
     * @param treatment Entero correspondiente al tipo de tratamiento que se le
     * dara a la data (0- String, 1- Local File, 2- Remote File, 3- Remote PDF File)
     * 
     * @return 
     */
    public String addData(final String id, final String data, final int treatment) {
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                try {
                    KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_DATA_ADD_TO_QUEUE));
                    dataQueue.addData(id, data, treatment);
                    
                    KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_METHOD_EXECUTED));
                    return KAWI_POSSITIVE_ANSWER;
                    
                } catch (KawiException ex) {
                    return errorMessage(ex, false);
                }
            }
        });
    }
    
    /**
     * Remover data precisa de la cola del applet.
     * 
     * @param id Entero que indica la posicion de la cola que se desea remover.
     * @return 
     */
    public String removeData(final String id) {
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_DATA_REMOVE_FROM_QUEUE));
                dataQueue.removeData(id);
                
                KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_METHOD_EXECUTED));
                return KAWI_POSSITIVE_ANSWER;
            }
        });
    }
    
    /**
     * Remover toda la data de la cola del applet.
     * @return 
     */
    public String clearData() {
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_DATA_REMOVE_ALL_FROM_QUEUE));
                dataQueue.clearData();
                
                KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_METHOD_EXECUTED));
                return KAWI_POSSITIVE_ANSWER;
            }
        });
    }
    
    /**
     * Generar paqueteKawi con las cabeceras y firmas electronicas.
     * 
     * @return String con un paqueteKawi cifrado y codificado SMime.
     */
    public String generateKawiPack() {
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                PKIKeys clientKeys = null;
                try {
                    KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_DATA_INIT_GEN_KAWI_PACK));
                    //Acceder a las llaves para firmar electrónicamente
                        clientKeys = loadKeys();
                    
                    //Actualizar configuracion del cliente para acceso a llaves
                        configuration = clientKeys.getRepositoryConfiguration();
                        
                    //Iniciarlizar paquete kawi
                    KawiPack pack = new KawiPack();
                    
                    //Setear cabeceras del paquete
                        //Siempre se agrega cabecera algoritmoFirma
                            pack.getHeaders().add(KawiPackHeaders.KAWI_PACK_HEADER_SIGN_ALG, KAWI_SIGN_ALG);
                        //Agregar cabecera fecha y hora al paqueteKawi
                            if(KAWI_PACK_HEADERS.get(KawiPackHeaders.KAWI_PACK_HEADER_DATE) != null)
                                if(KAWI_PACK_HEADERS.get(KawiPackHeaders.KAWI_PACK_HEADER_DATE))
                                    pack.getHeaders().add(KawiPackHeaders.KAWI_PACK_HEADER_DATE, DateHelper.dateToString(Kawi.getKawiclock().getDate()));
                        //Agregar cabecera certificado usado para generar las firmas
                            if(KAWI_PACK_HEADERS.get(KawiPackHeaders.KAWI_PACK_HEADER_DATE) != null)
                                if(KAWI_PACK_HEADERS.get(KawiPackHeaders.KAWI_PACK_HEADER_DATE))
                                    pack.getHeaders().add(KawiPackHeaders.KAWI_PACK_HEADER_CERTIFICATE, CertificateHelper.encode(clientKeys.getSignCertificate()));
                        //Agregar cabecera configuración
                            if(KAWI_PACK_HEADERS.get(KawiPackHeaders.KAWI_PACK_HEADER_DATE) != null)
                                if(KAWI_PACK_HEADERS.get(KawiPackHeaders.KAWI_PACK_HEADER_DATE))
                                    pack.getHeaders().add(KawiPackHeaders.KAWI_PACK_HEADER_CONFIGURATION, configuration.getConfigurationEncode());
                    
                    //Generar y setear firmas del paquete
                        pack.setSignatures(
                            Kawi.generateKawiPackSignatures(
                                KAWI_SIGN_STANDARD, 
                                dataQueue,
                                clientKeys.getPrivateKey(), 
                                clientKeys.getSignCertificate(), 
                                KAWI_SIGN_ALG,
                                vcConfig,
                                KAWI_CERTIFICATE_INTERVAL_VERIFICATION,
                                clientKeys.getRepositoryCryptographyProvider()
                            )
                        );
                    
                    //Cerrar acceso a llaves
                        clientKeys.close();
                        
                    //Preparar resultado
                        String result = pack.toString();
                        if (ucaimaCertificate != null) {
                            result = Kawi.encryptSMimeKawiPack(
                                result, 
                                ucaimaCertificate,
                                vcConfig,
                                KAWI_CERTIFICATE_INTERVAL_VERIFICATION
                            );
                        }
                        
                    KawiLogger.writeInfoLog(I18n.get(I18n.L_KAWI_METHOD_EXECUTED));
                    return result;

                } catch (VirtualClockHelperException | 
                         PKIKeysException | 
                         CertificateHelperException | 
                         KawiException | 
                         DateHelperException | 
                         NoSuchAlgorithmException | 
                         KawiPackException ex)
                {
                    if (clientKeys != null) clientKeys.close();
                    return errorMessage(ex, true);
                }
            }
         });
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////// Adicionales /////////////////////////////////
    public String getUserHomeDirectory() {
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                return SystemHelper.getUserHomeDirectory();
            }
        });
    }
    
    public String getTempDirectory() {
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                return SystemHelper.getTempDirectory();
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////
}
