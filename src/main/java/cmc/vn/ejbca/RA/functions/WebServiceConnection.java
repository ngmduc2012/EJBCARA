package cmc.vn.ejbca.RA.functions;

import cmc.vn.ejbca.RA.RaApplication;
import cmc.vn.ejbca.RA.api.AvailableCA;
import cmc.vn.ejbca.RA.api.CAs;
import cmc.vn.ejbca.RA.api.CPs;
import cmc.vn.ejbca.RA.api.EndEntityList;
import cmc.vn.ejbca.RA.response.ResponseObject;
import cmc.vn.ejbca.RA.response.ResponseStatus;
import org.bouncycastle.util.encoders.Base64;
import org.cesecore.keys.util.KeyTools;
import org.cesecore.util.CertTools;
import org.cesecore.util.CryptoProviderTools;
import org.ejbca.core.protocol.ws.client.gen.*;
import org.ejbca.core.protocol.ws.common.KeyStoreHelper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;


import javax.xml.namespace.QName;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class WebServiceConnection {
    public String pathFileP12 = "src\\p12\\superadmin.p12";
    public String pathFileTrustStore = "src\\p12\\truststore.jks";
    public String passwordP12 = ""; //if upload a wrong P12 password at the first time, we cannot connect with another visit, we have to restart server
    public String passwordTrustStore = "";


    /**
     * Connect to Web Server
     * Follow: https://download.primekey.com/docs/EJBCA-Enterprise/6_15_2/Web_Service_Interface.html
     **/
    public EjbcaWS connectService(String urlstr, String truststore, String passTruststore, String superadmin, String passSuperadmin) throws Exception {
        try {
//            System.out.println("truststore: "+ truststore);
//            System.out.println("passTruststore: " + passTruststore);
//            System.out.println("superadmin: " + superadmin);
//            System.out.println("passSuperadmin: " + passSuperadmin);

            CryptoProviderTools.installBCProvider();
            System.setProperty("javax.net.ssl.trustStore", truststore);
            System.setProperty("javax.net.ssl.trustStorePassword", passTruststore);

            System.setProperty("javax.net.ssl.keyStore", superadmin);
            System.setProperty("javax.net.ssl.keyStorePassword", passSuperadmin);

            QName qname = new QName("http://ws.protocol.core.ejbca.org/", "EjbcaWSService");
            EjbcaWSService service = new EjbcaWSService(new URL(urlstr), qname);
            return service.getEjbcaWSPort();
        } catch (Exception exc) {
            System.err
                    .println("*** Could not connect to non-authenticated web service");
            System.out.println(exc);

            return null;
        }
    }

    //write file to path file (address file)
    private static void writeToFile(byte[] data, String file) throws FileNotFoundException, IOException {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(data);
        }
    }

    /**
     * Create connect RA Server to CA Server
     **/
    public ResponseObject<?> connectRAtoCAServer(MultipartFile fileP12,
                                                 String passwordP12,
                                                 MultipartFile fileTrustStore,
                                                 String passwordTrustStore) {
        ResponseObject<?> res = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);

        try{
            //save files and passwords to CA server
            writeToFile(fileTrustStore.getBytes(), pathFileTrustStore);
            writeToFile(fileP12.getBytes(), pathFileP12);
            this.passwordP12 = passwordP12;
            this.passwordTrustStore = passwordTrustStore;
            res.setData(null);
        } catch (Exception exception){
            System.out.println("Cannot save file and password");
            return new ResponseObject<>(false, ResponseStatus.UNHANDLED_ERROR, exception.getMessage());
        }
        return res;
    }


    /**
     * Get Available CAs
     **/
    public List<AvailableCA> getAvailableCA(EjbcaWS ejbcaraws) throws Exception {
        List<AvailableCA> availableCAList = new ArrayList<AvailableCA>();
        System.out.println("\n\n");
        // if no AvailableCA be getted
        if (ejbcaraws.getAvailableCAs().isEmpty()) {
            System.out.println("No Available CAs");
        } else {
            for (NameAndId i : ejbcaraws.getAvailableCAs()
            ) {
                // add AvailableCA to list
                availableCAList.add(new AvailableCA(i.getName(), i.getId()));
            }
        }
        // return list AvailableCA (even null)
        return availableCAList;
    }

    /**
     * Get End Entity Profile
     **/
    public List<EndEntityList> getEndEntity(EjbcaWS ejbcaraws) throws Exception {
        System.out.println("\n\n");
        List<EndEntityList> endEntityList = new ArrayList<EndEntityList>();
        // if get no Authorized End Entity Profiles
        if (ejbcaraws.getAuthorizedEndEntityProfiles().isEmpty()) {
            System.out.println("No End Entity Profile");
        } else {
            for (NameAndId i : ejbcaraws.getAuthorizedEndEntityProfiles()
            ) {
                // add list
                endEntityList.add(new EndEntityList(
                        i.getName(), //name
                        i.getId(),   //id
                        availableCA(ejbcaraws.getAvailableCertificateProfiles(i.getId())), //list Certificate Profiles
                        availableCP(ejbcaraws.getAvailableCertificateProfiles(i.getId()))  //list CAs Profiles
                ));

            }

        }
        //return list End Entity (even null)
        return endEntityList;
    }

    public List<CPs> availableCP(List<NameAndId> available) {
        List<CPs> cPsList = new ArrayList<CPs>();
        if (available.isEmpty()) {
            System.out.println("No Available Certificate Profiles");
        } else {
            for (NameAndId i : available
            ) {
                //Add list
                cPsList.add(new CPs(i.getName(), i.getId()));
            }
        }
        //return list Certificate Profiles (even null)
        return cPsList;
    }

    public List<CAs> availableCA(List<NameAndId> available) {
        List<CAs> cAsList = new ArrayList<CAs>();
        if (available.isEmpty()) {
            System.out.println("No Available CAs Profiles");
        } else {
            for (NameAndId i : available
            ) {
                //Add list
                cAsList.add(new CAs(i.getName(), i.getId()));
            }
        }
        //return list CAs (even null)
        return cAsList;
    }

    /**
     * Soft token request
     **/
    public org.ejbca.core.protocol.ws.client.gen.KeyStore softTokenRequest(EjbcaWS ejbcaraws, UserDataVOWS userData, String hardTokenSN,
                                                                           String keyspec, String keyalg) throws Exception {
        try {
            KeyStore keyStore = ejbcaraws.softTokenRequest(userData, hardTokenSN, keyspec,
                    keyalg);
//            System.out.println("\n\n");
//            System.out.println("Soft Token Request: \n" + new String(keyStore.getKeystoreData(), StandardCharsets.UTF_8));
            return keyStore;
        } catch (EjbcaException_Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Certificate Response
     **/
    CertificateResponse certificateRequest(EjbcaWS ejbcaraws, org.bouncycastle.pkcs.PKCS10CertificationRequest requestData, UserDataVOWS userData,
                                           int requestType,
                                           String hardTokenSN, String responseType)
            throws Exception {
        try {
            CertificateResponse certenv = ejbcaraws.certificateRequest(userData, new String(Base64.encode(requestData.getEncoded())),
                    requestType, hardTokenSN, responseType);
            return certenv;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public CertificateResponse certificateRequestFromP10(EjbcaWS ejbcaraws, org.bouncycastle.jce.PKCS10CertificationRequest requestData, String userName, String password,
                                                         String hardTokenSN, String responseType)
            throws Exception {
        try {
            CertificateResponse certenv = ejbcaraws.pkcs10Request(userName, password, new String(Base64.encode(requestData.getEncoded())), hardTokenSN,
                    responseType);
            return certenv;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public CertificateResponse certificateRequestFromFile(EjbcaWS ejbcaraws, MultipartFile fileRequest, UserDataVOWS userData,
                                                          int requestType,
                                                          String hardTokenSN, String responseType)
            throws Exception {
        //Declare Function Units
        Units units = new Units();

        //Read file request
        byte[] request = fileRequest.getBytes();
        //Convest file to String
        String requestText = new String(request, StandardCharsets.UTF_8);
        //Convest to PKCS10 Certification Request
        org.bouncycastle.pkcs.PKCS10CertificationRequest requestData = units.convertPemToPKCS10CertificationRequest(requestText);
        try {
            CertificateResponse certenv = certificateRequest(ejbcaraws, requestData, userData, requestType, hardTokenSN, responseType);
            return certenv;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

//    void showCertificateRespond(CertificateResponse certificateResponse) throws Exception {
//        String caString = new String(certificateResponse.getData(), StandardCharsets.UTF_8);
//        System.out.println("\n\n");
//        System.out.println("Certificate response: \n" + caString);
//        System.out.println(certificateResponse.getCertificate().getIssuerX500Principal().getName());
//        System.out.println(certificateResponse.getCertificate().getSubjectX500Principal().getName());
//    }


    /**
     * pkcs12Req
     **/
    public org.ejbca.core.protocol.ws.client.gen.KeyStore pkcs12Req(EjbcaWS ejbcaraws,
                                                                    String username, String password,
                                                                    String hardTokenSN, String keyspec,
                                                                    String keyalg) throws Exception {
        try {
            KeyStore keyStore = ejbcaraws.pkcs12Req(username, password, hardTokenSN,
                    keyspec, keyalg);
//            System.out.println("\n\n");
//            System.out.println("keyStore Data (P12): \n" + new String(keyStore.getKeystoreData(), StandardCharsets.UTF_8));
            return keyStore;
        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    /**
     * Generate Server Certificate from P12
     **/
    public java.security.cert.Certificate certificateFromP12(KeyStore p12Req, String type, String password) throws Exception {
        try {
            java.security.KeyStore ks = KeyStoreHelper.getKeyStore(p12Req.getKeystoreData(), type, password);
            Enumeration<String> en = ks.aliases();
            String alias = en.nextElement();
            java.security.cert.Certificate certificateP12 = (java.security.cert.Certificate) ks.getCertificate(alias);
//            System.out.println("\n\n");
//            System.out.println("Server Certificate from P12:");
//            System.out.println("Encoded   : " + String.format("%8s", Integer.toBinaryString(ByteBuffer.wrap(certificateP12.getEncoded()).getInt())));
//            System.out.println("Type      : " + certificateP12.getType());
//            System.out.println("Public Key: " + certificateP12.getPublicKey());
            return certificateP12;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Find Certificate
     **/
    public List<Certificate> findCerts(EjbcaWS ejbcaraws, String username,
                                       boolean onlyValid) throws Exception {
        try {
            return ejbcaraws.findCerts(username, onlyValid);
        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    public void showCertificate(List<Certificate> result) {
        System.out.println("\n\n");
        if (result.size() != 0) {
            for (Certificate i :
                    result) {
                System.out.println("Certificate        : " + i.getCertificate());
                System.out.println("CertificateData    : \n" + new String(i.getCertificateData(), StandardCharsets.UTF_8));
                System.out.println("RawCertificateData : " + String.format("%8s", Integer.toBinaryString(ByteBuffer.wrap(i.getRawCertificateData()).getInt()))
                        .replaceAll(" ", "0"));
                System.out.println("KeyStore           : " + i.getKeyStore());
                System.out.println("Type               : " + i.getType());
                System.out.println("=========================================");
            }
        } else {
            System.out.println("No Certificate for search!");
        }
    }

    /**
     * Check Revokation Status
     **/
    RevokeStatus checkRevokationStatus(EjbcaWS ejbcaraws, String issuerDN,
                                       String certificateSN) throws Exception {
        try {
            return ejbcaraws.checkRevokationStatus(issuerDN, certificateSN);
        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    public RevokeStatus checkRevokation(EjbcaWS ejbcaraws, Certificate cert) {
        try {
            //Generate x509 Certificate
            X509Certificate x509Cert = (X509Certificate) CertTools
                    .getCertfromByteArray(cert.getRawCertificateData());
            RevokeStatus check = checkRevokationStatus(ejbcaraws, x509Cert.getIssuerDN().toString(), CertTools
                    .getSerialNumberAsString(x509Cert));
            return check;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    String checkReason(int i) {
        if (i == RevokeStatus.NOT_REVOKED) {
            return "NOT_REVOKED";
        } else if (i == RevokeStatus.REVOKATION_REASON_UNSPECIFIED) {
            return "REVOKATION_REASON_UNSPECIFIED";
        } else if (i == RevokeStatus.REVOKATION_REASON_KEYCOMPROMISE) {
            return "REVOKATION_REASON_KEYCOMPROMISE";
        } else if (i == RevokeStatus.REVOKATION_REASON_CACOMPROMISE) {
            return "REVOKATION_REASON_CACOMPROMISE";
        } else if (i == RevokeStatus.REVOKATION_REASON_AFFILIATIONCHANGED) {
            return "REVOKATION_REASON_AFFILIATIONCHANGED";
        } else if (i == RevokeStatus.REVOKATION_REASON_SUPERSEDED) {
            return "REVOKATION_REASON_SUPERSEDED";
        } else if (i == RevokeStatus.REVOKATION_REASON_CESSATIONOFOPERATION) {
            return "REVOKATION_REASON_CESSATIONOFOPERATION";
        } else if (i == RevokeStatus.REVOKATION_REASON_CERTIFICATEHOLD) {
            return "REVOKATION_REASON_CERTIFICATEHOLD";
        } else if (i == RevokeStatus.REVOKATION_REASON_REMOVEFROMCRL) {
            return "REVOKATION_REASON_REMOVEFROMCRL";
        } else if (i == RevokeStatus.REVOKATION_REASON_PRIVILEGESWITHDRAWN) {
            return "REVOKATION_REASON_PRIVILEGESWITHDRAWN";
        } else {
            return "REVOKATION_REASON_AACOMPROMISE";
        }

    }

    /**
     * Revoke Certificate
     **/
    boolean revokeCert(EjbcaWS ejbcaraws, String issuerDN, String certificateSN,
                       int reason) {
        try {
            ejbcaraws.revokeCert(issuerDN, certificateSN, reason);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean revokeCertificate(EjbcaWS ejbcaraws, Certificate cert, int reason) {
        try {
            //Generate x509 Certificate
            X509Certificate x509Cert = (X509Certificate) CertTools
                    .getCertfromByteArray(cert.getRawCertificateData());

            return revokeCert(ejbcaraws, x509Cert.getIssuerDN().toString(), CertTools
                    .getSerialNumberAsString(x509Cert), reason);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generate Keys
     **/
    public KeyPair generateKeys(String keySpec, String keyalgorithmRsa) throws Exception {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            KeyPair keys = KeyTools.genKeys(keySpec, keyalgorithmRsa);
//            System.out.println("\n\n");
//            System.out.println("Private Key: " + keys.getPrivate());
//            System.out.println("Public key : " + keys.getPublic());
            return keys;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
