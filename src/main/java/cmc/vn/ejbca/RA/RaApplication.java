package cmc.vn.ejbca.RA;

import cmc.vn.ejbca.RA.api.*;
import cmc.vn.ejbca.RA.api.FindCerts;
import cmc.vn.ejbca.RA.api.SoftTokenRequest;
import cmc.vn.ejbca.RA.functions.User;
import cmc.vn.ejbca.RA.functions.WebClient;
import cmc.vn.ejbca.RA.functions.WebServiceConnection;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.cesecore.certificates.endentity.EndEntityConstants;
import org.cesecore.certificates.util.AlgorithmConstants;
import org.ejbca.core.protocol.ws.client.gen.CertificateResponse;
import org.ejbca.core.protocol.ws.client.gen.EjbcaWS;
import org.ejbca.core.protocol.ws.client.gen.UserDataVOWS;
import org.ejbca.core.protocol.ws.common.CertificateHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ejbca.core.protocol.ws.client.gen.*;

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
    public EjbcaWS ejbcaraws() throws Exception {
        String urlstr = "https://caadmin.cmc.vn:8443/ejbca/ejbcaws/ejbcaws?wsdl";
        String truststore = "C:\\Users\\ngmdu\\Desktop\\p12\\truststore.jks";
        String passTruststore = "123456";
        String superadmin = "C:\\Users\\ngmdu\\Desktop\\p12\\superadmin.p12";
        String passSuperadmin = "123456";
        EjbcaWS ejbcaraws = connection.connectService(urlstr, truststore, passTruststore, superadmin, passSuperadmin);
        return ejbcaraws;
    }


    /**
     * Get version
     **/
    @GetMapping("/version")
    public Version version() throws Exception {
        return new Version(ejbcaraws().getEjbcaVersion());
    }

    /**
     * Get end entity
     **/
    @GetMapping("/endentity")
    public List<EndEntityList> endentity(
    ) throws Exception {
        return connection.getEndEntity(ejbcaraws());
    }

    /**
     * Get available CA
     **/
    @GetMapping("/availableCA")
    public List<AvailableCA> availableCA(
    ) throws Exception {
        return connection.getAvailableCA(ejbcaraws());
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
    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody UserAPI newUserAPI) throws Exception {
        if (user.addOrEditUser(userDataVOWS, ejbcaraws(), newUserAPI)) {
            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.ok("Failure");
    }

    /**
     * Find users
     * <p>
     * Test Posman POST body raw JSON
     */
    /*
      {
          "search" : "ServerCA",
          "usermatch" : [5]
      }
     */
    @PostMapping("/findUsers")
    public List<UserDataVOWS> findUsers(@RequestBody FindUsers findUsers) throws Exception {
        return user.findUsers(ejbcaraws(), findUsers);
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
    @PostMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestBody DeleteUser deleteUser) throws Exception {
        if (user.deleteUser(ejbcaraws(), deleteUser)) {
            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.ok("Failure");
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
    @PostMapping(value = "/respondCertificate",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> respondCertificate(
            @RequestBody MultipartFile fileRequest,
            String userName,
            String requestType,
            String hardTokenSN,
            String responseType
    ) throws Exception {
        CertificateResponse certificateResponse = connection.certificateRequestFromFile(
                ejbcaraws(),
                fileRequest,
                user.findUserByUserName(ejbcaraws(), userName), //Find the user
                Integer.parseInt(requestType),
                hardTokenSN,
                responseType);

        //change certificate Respone Data to Base64 string
        String encoded = Base64.getEncoder().encodeToString(certificateResponse.getData());
        return ResponseEntity.ok(encoded);
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
    @PostMapping("/findCerts")
    public List<Certificate> listCerts(@RequestBody FindCerts findCerts) throws Exception {
        return connection.findCerts(ejbcaraws(), findCerts.getUserName(), findCerts.isOnlyValid());
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
    @PostMapping("/revokeCertificate")
    public ResponseEntity<String> revokeCertificate(@RequestBody RevokeCertificate revoke) throws Exception {
        if (connection.revokeCertificate(
                ejbcaraws(),
                // Find the Certificate that want to revoke
                connection.findCerts(ejbcaraws(), revoke.getUserName(), revoke.isOnlyValid()).get(revoke.getIdCert()),
                revoke.getReason())) {
            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.ok("Failure");
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
    @PostMapping("/checkRevokation")
    public RevokeStatus checkRevokation(@RequestBody CheckRevokation check) throws Exception {
        return connection.checkRevokation(
                ejbcaraws(),
                connection.findCerts(ejbcaraws(), check.getUserName(), check.isOnlyValid()).get(check.getIdCert()));
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
    @PostMapping("/p12Req")
    public KeyStore p12Req(@RequestBody PKCS12ReqAPI pkcs12) throws Exception {
        return connection.pkcs12Req(
                ejbcaraws(),
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
    @PostMapping("/certificateFromP12")
    public ResponseEntity<String> certificateFromP12(@RequestBody PKCS12ReqAPI pkcs12) throws Exception {
        java.security.cert.Certificate certificate = connection.certificateFromP12(
                // Below is Generation P12 KeyStore Request
                connection.pkcs12Req(ejbcaraws(), pkcs12.getUsername(), pkcs12.getPassword(), pkcs12.getHardTokenSN(), pkcs12.getKeyspec(), pkcs12.getKeyalg()),
                "PKCS12",
                pkcs12.getPassword());

        //change certificate Data to Base64 string
        String encoded = Base64.getEncoder().encodeToString(certificate.getEncoded());
        return ResponseEntity.ok(encoded);
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
    @PostMapping("/softTokenRequest")
    public KeyStore softTokenRequest(@RequestBody SoftTokenRequest softTokenRequest) throws Exception {
        return connection.softTokenRequest(ejbcaraws(), user.setUser(
                userDataVOWS,
                ejbcaraws(),
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
    @PostMapping("/pkcs10CertificationRequest")
    public ResponseEntity<String> pkcs10CertificationRequest(@RequestBody PKCS10Certification pkcs10) throws Exception {
        //Generate Keys
        KeyPair keys = connection.generateKeys(pkcs10.getKeySpec(), pkcs10.getKeyalgorithmRsa());

        //change pkcs10 Certification Request Data to Base64 string
        String encoded = Base64.getEncoder().encodeToString(client.pkcs10CertificationRequest(pkcs10.getSignatureAlgorithm(), pkcs10.getDn(), keys).getEncoded());
        return ResponseEntity.ok(encoded);
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
    @PostMapping("/certificateRequestFromP10")
    public ResponseEntity<String> certificateRequestFromP10(@RequestBody CertificateRequestFromP10 cert) throws Exception {
        //Generate Keys
        KeyPair keys = connection.generateKeys(cert.getKeySpec(), cert.getKeyalgorithmRsa());

        //Generate pkcs10 Certification Request
        PKCS10CertificationRequest pkcs10Cert = client.pkcs10CertificationRequest(cert.getSignatureAlgorithm(), cert.getDn(), keys);

        //Generate Certificate Response
        CertificateResponse certenv = connection.certificateRequestFromP10(
                ejbcaraws(),
                pkcs10Cert,
                cert.getUserName(),
                cert.getPassword(),
                cert.getHardTokenSN(),
                cert.getResponseType());

        //Change Certificate Response data to Base64 sring
        String encoded = Base64.getEncoder().encodeToString(certenv.getData());
        return ResponseEntity.ok(encoded);
    }

}
