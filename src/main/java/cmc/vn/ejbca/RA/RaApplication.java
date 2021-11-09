package cmc.vn.ejbca.RA;

import cmc.vn.ejbca.RA.responds.*;
import cmc.vn.ejbca.RA.responds.FindCerts;
import cmc.vn.ejbca.RA.responds.SoftTokenRequest;
import cmc.vn.ejbca.RA.controllers.User;
import cmc.vn.ejbca.RA.controllers.WebClient;
import cmc.vn.ejbca.RA.controllers.WebServiceConnection;
import cmc.vn.ejbca.RA.response.ResponseObject;
import cmc.vn.ejbca.RA.response.ResponseStatus;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.ejbca.core.protocol.ws.client.gen.CertificateResponse;
import org.ejbca.core.protocol.ws.client.gen.EjbcaWS;
import org.ejbca.core.protocol.ws.client.gen.UserDataVOWS;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ejbca.core.protocol.ws.client.gen.*;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Base64;
import java.util.List;

@SpringBootApplication
@RestController
public class RaApplication {

    /**
     * Connect to functions: WebServiceConnection, WebClient, User
     **/
    WebServiceConnection connection = new WebServiceConnection();
    WebClient client = new WebClient();
    User user = new User();

    // Declare UserDataVOWS of EJBCA
    UserDataVOWS userDataVOWS = new UserDataVOWS();

    //Server connect
    private static final String ipAddress = "http://localhost:4200/";

    EjbcaWS ejbcaWS = null;

    public static void main(String[] args) {
        SpringApplication.run(RaApplication.class, args);
    }

    /**
     * Connect EJBCA RA
     * <p>
     * Connect to server virtual machine with URL (Change in host file)
     * Select trustsstore.jks & superadmin.p12
     * Follow: https://download.primekey.com/docs/EJBCA-Enterprise/6_15_2/Web_Service_Interface.html
     **/
    public EjbcaWS connectEJBCA(EjbcaWS ejbcaWS) {
        this.ejbcaWS = ejbcaWS;
        return this.ejbcaWS;
    }
    public EjbcaWS ejbcaraws() throws Exception {
        String urlstr = "https://caadmin.cmc.vn:8443/ejbca/ejbcaws/ejbcaws?wsdl";
        return connection.connectService(
                urlstr,
                connection.pathFileTrustStore,
                connection.passwordTrustStore,
                connection.pathFileP12,
                connection.passwordP12);
    }

    /**
     * API for RA server connect to CA server
     * <p>
     * Test Postman POST body form-data
     * fileP12             : file : superadmin.p12
     * passwordP12         : text : ******
     * fileTrustStore      : file : truststore.jks
     * passwordTrustStore  : text : ******
     **/
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping(value = "/connect",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> connect(
            @RequestBody MultipartFile fileP12, //Transmission file p12: superadmin.p12
            String passwordP12,//Transmission password for file superadmin.p12
            @RequestBody MultipartFile fileTrustStore,//Transmission file trustStore: truststore.jks
            String passwordTrustStore//Transmission password for file truststore.jks
    ) throws Exception {
        ResponseObject<?> res;
        ResponseObject<?> saveFile = connection.connectRAtoCAServer(
                fileP12,
                passwordP12,
                fileTrustStore,
                passwordTrustStore);
        if (saveFile.getResult()) {
            try {
                connectEJBCA(ejbcaraws());
                res = connection.getAvailableCA(ejbcaWS);
            } catch (Exception e) {
                res = new ResponseObject<>(false, ResponseStatus.UNHANDLED_ERROR, e.getMessage());
            }

        } else {
            res = saveFile;
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * API for RA server connect to CA server
     * <p>
     * Test Postman POST body form-data
     * fileP12             : file : superadmin.p12
     * passwordP12         : text : ******
     * fileTrustStore      : file : truststore.jks
     * passwordTrustStore  : text : ******
     **/
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @GetMapping(value = "/disconnect")
    public ResponseEntity<?> disconnect() throws Exception {
        ResponseObject<?> res = connection.disConnectRAtoCAServer();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * Get version
     **/
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @GetMapping("/version")
    public ResponseEntity<?> version() throws Exception {
        ResponseObject<Version> res = new ResponseObject<>(true, ResponseStatus.DO_SERVICE_SUCCESSFUL);
        try {
            res.setData(new Version(ejbcaWS.getEjbcaVersion()));
        } catch (Exception e) {
            res = new ResponseObject<>(false, ResponseStatus.UNHANDLED_ERROR, e.getMessage());
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * Get end entity
     **/
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @GetMapping("/endentity")
    public ResponseEntity<?> endentity(
    ) throws Exception {
        ResponseObject<?> res =  connection.getEndEntity(ejbcaWS);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * Get profile By Id
     **/
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @GetMapping("/profile")
    public ResponseEntity<?> profileById(
            @RequestParam String id,
            @RequestParam(defaultValue = "cp") String type
    ) throws Exception {
        ResponseObject<?> res =  connection.getProfileById(ejbcaWS, id, type);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * Get available CA
     **/
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @GetMapping("/availableCA")
    public ResponseEntity<?> availableCA(
    ) throws Exception {
        ResponseObject<?> res = connection.getAvailableCA(ejbcaWS);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    /**
     * Add User
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "userName": "ngmduc4",
          "password": "1",
          "clearPwd": false,
          "subjectDN": "CN=ngmduc4, OU=CMC, O=CMC company, L=ha noi, ST=cau giay, C=VN",
          "CaName": "ServerCA",
          "tokenType": "USERGENERATED",
          "status": 40,
          "email": null,
          "subjectAltName": null,
          "endEntityProfileName": "EndEntityProfile",
          "certificateProfileName": "EndEntityCertificateProfile",
          "startTime": null
      }
     */
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping("/addOrEditUser")
    public ResponseEntity<?> addOrEditUser(@RequestBody UserAPI newUserAPI) throws Exception {
        ResponseObject<?> res = user.addOrEditUser(userDataVOWS, ejbcaWS, newUserAPI);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * Find users
     * <p>
     * Test Posman POST body raw JSON
     *
     * MATCH_WITH_USERNAME = 0;
     * MATCH_WITH_EMAIL = 1;
     * MATCH_WITH_STATUS = 2;
     * MATCH_WITH_ENDENTITYPROFILE = 3;
     * MATCH_WITH_CERTIFICATEPROFILE = 4;
     * MATCH_WITH_CA = 5;
     * MATCH_WITH_TOKEN = 6;
     * MATCH_WITH_DN = 7;
     * MATCH_WITH_UID = 100;
     * MATCH_WITH_COMMONNAME = 101;
     * MATCH_WITH_DNSERIALNUMBER = 102;
     * MATCH_WITH_GIVENNAME = 103;
     * MATCH_WITH_INITIALS = 104;
     * MATCH_WITH_SURNAME = 105;
     * MATCH_WITH_TITLE = 106;
     * MATCH_WITH_ORGANIZATIONALUNIT = 107;
     * MATCH_WITH_ORGANIZATION = 108;
     * MATCH_WITH_LOCALITY = 109;
     * MATCH_WITH_STATEORPROVINCE = 110;
     * MATCH_WITH_DOMAINCOMPONENT = 111;
     * MATCH_WITH_COUNTRY = 112;
     * MATCH_TYPE_EQUALS = 0;
     * MATCH_TYPE_BEGINSWITH = 1;
     * MATCH_TYPE_CONTAINS = 2;
     *
     *
     * Get status of User after get data
     * (Example: EndEntityConstants.STATUS_NEW)
     * STATUS_NEW = 10;            New user
     * STATUS_FAILED = 11;         Generation of user certificate failed
     * STATUS_INITIALIZED = 20;    User has been initialized
     * STATUS_INPROCESS = 30;      Generation of user certificate in process
     * STATUS_GENERATED = 40;      A certificate has been generated for the user
     * STATUS_REVOKED = 50;        The user has been revoked and should not have any more certificates issued
     * STATUS_HISTORICAL = 60;     The user is old and archived
     * STATUS_KEYRECOVERY  = 70;   The user is should use key recovery functions in next certificate generation.
     *
     *
     * TOKEN_TYPE_USERGENERATED = "USERGENERATED";
     * TOKEN_TYPE_JKS = "JKS";
     * TOKEN_TYPE_PEM = "PEM";
     * TOKEN_TYPE_P12 = "P12";
     */
    /*
      {
          "search" : "ServerCA",
          "usermatch" : [5]
      }
     */
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping("/findUsers")
    public ResponseEntity<?> findUsers(@RequestBody FindUsers findUsers) throws Exception {
        ResponseObject<?> res = user.findUsers(ejbcaWS, findUsers);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * Delete user
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "userName" : "nmduc16",
          "reason" : 6,
          "decision" : true
      }
     */
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping("/revokeUser")
    public ResponseEntity<?> revokeUserService(@RequestBody DeleteUser deleteUser) throws Exception {
        ResponseObject<?> res = user.revokeUserService(ejbcaWS, deleteUser);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    /**
     * Create Certificate Respond from File
     * <p>
     * Test Postman POST body form-data
     * fileRequest  : file : selectedFile.csr (ngmduc4.csr)
     * userName     : text : ngmduc4
     * requestType  : text : 0
     * hardTokenSN  : text :
     * responseType : text : CERTIFICATE
     **/
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping(value = "/respondCertificate",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public TextRespondHttpClient respondCertificate(
            @RequestBody MultipartFile fileRequest,
            String userName,
            String requestType,
            String hardTokenSN,
            String responseType
    ) throws Exception {
        CertificateResponse certificateResponse = connection.certificateRequestFromFile(
                ejbcaWS,
                fileRequest,
                user.findUserByUserName(ejbcaWS, userName), //Find the user
                Integer.parseInt(requestType),
                hardTokenSN,
                responseType);

        //change certificate Respone Data to Base64 string
        return new TextRespondHttpClient(Base64.getEncoder().encodeToString(certificateResponse.getData()));
    }


    /**
     * Find certificate
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "userName" : "ngmduc4",
          "onlyValid" : false
      }
     */
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping("/findCerts")
    public List<Certificate> listCerts(@RequestBody FindCerts findCerts) throws Exception {
        return connection.findCerts(ejbcaWS, findCerts.getUserName(), findCerts.isOnlyValid());
    }


    /**
     * Revoke Certificate
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "userName" : "ngmduc4",
          "onlyValid" : false,
          "idCert" : 40,
          "reason" : 0
      }
     */
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping("/revokeCertificate")
    public Boolean revokeCertificate(@RequestBody RevokeCertificate revoke) throws Exception {
        return connection.revokeCertificate(
                ejbcaWS,
                // Find the Certificate that want to revoke
                connection.findCerts(ejbcaWS, revoke.getUserName(), revoke.isOnlyValid()).get(revoke.getIdCert()),
                revoke.getReason());
    }


    /**
     * Check Revokation
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "userName" : "ngmduc4",
          "onlyValid" : false,
          "idCert" : 41
      }
     */
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping("/checkRevokation")
    public RevokeStatus checkRevokation(@RequestBody CheckRevokation check) throws Exception {
        return connection.checkRevokation(
                ejbcaWS,
                connection.findCerts(ejbcaWS, check.getUserName(), check.isOnlyValid()).get(check.getIdCert()));
    }

    /**
     * Add User
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "userName": "client2",
          "password": "1",
          "clearPwd": false,
          "subjectDN": "CN=client2, OU=CMC, O=CMC company, L=ha noi, ST=cau giay, C=VN",
          "CaName": "ServerCA",
          "tokenType": "P12",
          "status": 10,
          "email": null,
          "subjectAltName": null,
          "endEntityProfileName": "EndEntityProfile",
          "certificateProfileName": "EndEntityCertificateProfile",
          "startTime": null
      }
     */

    /**
     * Generate P12 KeyStore Request
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "userName": "client2",
          "password": "1",
          "hardTokenSN": null,
          "keyspec": "2048",
          "keyalg": "RSA"
      }
     **/
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping("/p12Req")
    public KeyStore p12Req(@RequestBody PKCS12ReqAPI pkcs12) throws Exception {
        return connection.pkcs12Req(
                ejbcaWS,
                pkcs12.getUsername(),
                pkcs12.getPassword(),
                pkcs12.getHardTokenSN(),
                pkcs12.getKeyspec(),
                pkcs12.getKeyalg());
    }

    /**
     * Add User
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "userName": "client2",
          "password": "1",
          "clearPwd": false,
          "subjectDN": "CN=client2, OU=CMC, O=CMC company, L=ha noi, ST=cau giay, C=VN",
          "CaName": "ServerCA",
          "tokenType": "P12",
          "status": 10,
          "email": null,
          "subjectAltName": null,
          "endEntityProfileName": "EndEntityProfile",
          "certificateProfileName": "EndEntityCertificateProfile",
          "startTime": null
      }
     */

    /**
     * Generate Certificate from P12
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "userName": "client2",
          "password": "1",
          "hardTokenSN": null,
          "keyspec": "2048",
          "keyalg": "RSA"
      }
     */
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping("/certificateFromP12")
    public TextRespondHttpClient certificateFromP12(@RequestBody PKCS12ReqAPI pkcs12) throws Exception {
        java.security.cert.Certificate certificate = connection.certificateFromP12(
                // Below is Generation P12 KeyStore Request
                connection.pkcs12Req(ejbcaWS, pkcs12.getUsername(), pkcs12.getPassword(), pkcs12.getHardTokenSN(), pkcs12.getKeyspec(), pkcs12.getKeyalg()),
                "PKCS12",
                pkcs12.getPassword());
        //change certificate Data to Base64 string
        return new TextRespondHttpClient(Base64.getEncoder().encodeToString(certificate.getEncoded()));
    }


    /**
     * Soft Token Request
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "userName": "client5",
          "password": "1",
          "clearPwd": true , //have to setup default password
          "subjectDN": "CN=client5, OU=CMC, O=CMC company, L=ha noi, ST=cau giay, C=VN",
          "CaName": "ServerCA",
          "tokenType": "P12", //have to setup P12
          "status": 10, //have to setup NEW
          "email": null,
          "subjectAltName": null,
          "endEntityProfileName": "EndEntityProfile",
          "certificateProfileName": "EndEntityCertificateProfile",
          "startTime": null,
          "hardTokenS" : null,
          "keyspec" : "2048",
          "keyalg" : "RSA"
      }
     */
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping("/softTokenRequest")
    public KeyStore softTokenRequest(@RequestBody SoftTokenRequest softTokenRequest) throws Exception {
        return connection.softTokenRequest(ejbcaWS, user.setUser(
                userDataVOWS,
                ejbcaWS,
                softTokenRequest.getUserName(),
                softTokenRequest.getPassword(),
                softTokenRequest.isClearPwd(),
                softTokenRequest.getSubjectDN(),
                softTokenRequest.getCaName(),
                softTokenRequest.getTokenType(),
                softTokenRequest.getStatus(),
                softTokenRequest.getEmail(),
                softTokenRequest.getSubjectAltName(),
                softTokenRequest.getEndEntityProfileName(),
                softTokenRequest.getCertificateProfileName(),
                softTokenRequest.getStartTime()
        ), softTokenRequest.getHardTokenS(), softTokenRequest.getKeyspec(), softTokenRequest.getKeyalg());
    }


    /**
     * Generate Keys
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "keySpec" : "2048",
          "keyalgorithmRsa" : "RSA"
      }
     */
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping("/generateKeys")
    public Keys generateKeys(@RequestBody GenerateKeys keys) throws Exception {
        KeyPair keyPair = connection.generateKeys(keys.getKeySpec(), keys.getKeyalgorithmRsa());
        return new Keys(
                Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()),  //change Public key Data to Base64 string
                Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));//change Public key Data to Base64 string
    }

    /**
     * Add User
     *
     * Test Posman POST body raw JSON
     */
    /*
      {
          "userName": "client6",
          "password": "1",
          "clearPwd": false,
          "subjectDN": "CN=client6, OU=CMC, O=CMC company, L=ha noi, ST=cau giay, C=VN",
          "CaName": "ServerCA",
          "tokenType": "USERGENERATED",
          "status": 10,
          "email": null,
          "subjectAltName": null,
          "endEntityProfileName": "EndEntityProfile",
          "certificateProfileName": "EndEntityCertificateProfile",
          "startTime": null
      }
      */

    /**
     * Generate Request PKCS10
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "keySpec": "2048",
          "keyalgorithmRsa": "RSA",
          "signatureAlgorithm": "SHA1WithRSA",
          "dn": "CN=client6, OU=CMC, O=CMC company, L=ha noi, ST=cau giay, C=VN"
      }
     */
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping("/pkcs10CertificationRequest")
    public TextRespondHttpClient pkcs10CertificationRequest(@RequestBody PKCS10Certification pkcs10) throws Exception {
        //Generate Keys
        KeyPair keys = connection.generateKeys(pkcs10.getKeySpec(), pkcs10.getKeyalgorithmRsa());

        //change pkcs10 Certification Request Data to Base64 string
        return new TextRespondHttpClient(Base64.getEncoder().encodeToString(client.pkcs10CertificationRequest(pkcs10.getSignatureAlgorithm(), pkcs10.getDn(), keys).getEncoded()));
    }

    /**
     * Add User
     *
     * Test Posman POST body raw JSON **/
    /*
    {
         "userName": "client6",
         "password": "1",
         "clearPwd": false,
         "subjectDN": "CN=client6, OU=CMC, O=CMC company, L=ha noi, ST=cau giay, C=VN",
         "CaName": "ServerCA",
         "tokenType": "USERGENERATED",
         "status": 10,
         "email": null,
         "subjectAltName": null,
         "endEntityProfileName": "EndEntityProfile",
         "certificateProfileName": "EndEntityCertificateProfile",
         "startTime": null
     }
     */

    /**
     * Get certificate respond from pkcs 10 request
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
     {
         "keySpec": "2048",
         "keyalgorithmRsa": "RSA",
         "signatureAlgorithm": "SHA1WithRSA",
         "dn": "CN=client6, OU=CMC, O=CMC company, L=ha noi, ST=cau giay, C=VN",
         "userName": "client6",
         "password": "1",
         "hardTokenSN": null,
         "responseType": "CERTIFICATE"
     }
     */
    @CrossOrigin(origins = ipAddress) //For accept to connect to this url
    @PostMapping("/certificateRequestFromP10")
    public TextRespondHttpClient certificateRequestFromP10(@RequestBody CertificateRequestFromP10 cert) throws Exception {
        //Generate Keys
        KeyPair keys = connection.generateKeys(cert.getKeySpec(), cert.getKeyalgorithmRsa());

        //Generate pkcs10 Certification Request
        PKCS10CertificationRequest pkcs10Cert = client.pkcs10CertificationRequest(cert.getSignatureAlgorithm(), cert.getDn(), keys);

        //Generate Certificate Response
        CertificateResponse certenv = connection.certificateRequestFromP10(
                ejbcaWS,
                pkcs10Cert,
                cert.getUserName(),
                cert.getPassword(),
                cert.getHardTokenSN(),
                cert.getResponseType());

        //Change Certificate Response data to Base64 sring
        return new TextRespondHttpClient(Base64.getEncoder().encodeToString(certenv.getData()));
    }

}
