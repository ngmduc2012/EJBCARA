package cmc.vn.ejbca.RA.functions;

import cmc.vn.ejbca.RA.api.DeleteUser;
import cmc.vn.ejbca.RA.api.FindUsers;
import cmc.vn.ejbca.RA.api.UserAPI;
import org.ejbca.core.protocol.ws.client.gen.EjbcaWS;
import org.ejbca.core.protocol.ws.client.gen.UserDataVOWS;
import org.ejbca.core.protocol.ws.client.gen.UserMatch;

import java.util.List;

public class User {

    /**
     * Add or Edit User
     **/
    void editUser(EjbcaWS ejbcaraws, UserDataVOWS userdata) throws Exception {
        try {
            ejbcaraws.editUser(userdata);
        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }
    public boolean addOrEditUser(UserDataVOWS userDataVOWS,
                        EjbcaWS ejbcaraws,
                              UserAPI userAPI
    ) throws Exception {
        //Check Empty values
        if (userAPI.getUserName().isEmpty()
                || userAPI.getSubjectDN().isEmpty()
                || userAPI.getCaName().isEmpty()
                || userAPI.getTokenType().isEmpty()
                || userAPI.getEndEntityProfileName().isEmpty()
                || userAPI.getCertificateProfileName().isEmpty()
        ) {
            System.out.println("\n\n");
            System.out.println("missing one of them");
            return false;
        } else {
            //Set Value for userDataVOWS
            userDataVOWS.setUsername(userAPI.getUserName());
            userDataVOWS.setPassword(userAPI.getPassword());
            userDataVOWS.setClearPwd(userAPI.isClearPwd());
            userDataVOWS.setSubjectDN(userAPI.getSubjectDN());
            userDataVOWS.setCaName(userAPI.getCaName());
            userDataVOWS.setTokenType(userAPI.getTokenType());
            userDataVOWS.setStatus(userAPI.getStatus());
            userDataVOWS.setEmail(userAPI.getEmail());
            userDataVOWS.setSubjectAltName(userAPI.getSubjectAltName());
            userDataVOWS.setEndEntityProfileName(userAPI.getEndEntityProfileName());
            userDataVOWS.setCertificateProfileName(userAPI.getCertificateProfileName());
            userDataVOWS.setStartTime(userAPI.getStartTime());
            //Add User
            editUser(ejbcaraws, userDataVOWS);
            return true;
        }
    }

    public UserDataVOWS setUser(UserDataVOWS userDataVOWS,
                        EjbcaWS ejbcaraws,
                        String userName,
                        String password,
                        boolean clearPwd,
                        String subjectDN,
                        String CaName,
                        String tokenType,
                        int status,
                        String email,
                        String subjectAltName,
                        String endEntityProfileName,
                        String certificateProfileName,
                        String startTime
    ) throws Exception {
        //Check Empty value
        if (userName.isEmpty()
                || subjectDN.isEmpty()
                || CaName.isEmpty()
                || tokenType.isEmpty()
                || endEntityProfileName.isEmpty()
                || certificateProfileName.isEmpty()
        ) {
            System.out.println("\n\n");
            System.out.println("missing one of them");
            return null;
        } else {
            //Set Value for userDataVOWS
            userDataVOWS.setUsername(userName);
            userDataVOWS.setPassword(password);
            userDataVOWS.setClearPwd(clearPwd);
            userDataVOWS.setSubjectDN(subjectDN);
            userDataVOWS.setCaName(CaName);
            userDataVOWS.setTokenType(tokenType);
            userDataVOWS.setStatus(status);
            userDataVOWS.setEmail(email);
            userDataVOWS.setSubjectAltName(subjectAltName);
            userDataVOWS.setEndEntityProfileName(endEntityProfileName);
            userDataVOWS.setCertificateProfileName(certificateProfileName);
            userDataVOWS.setStartTime(startTime);
        }
        return userDataVOWS;
    }

    /**
     * Find User
     **/
    UserMatch userMatch = new UserMatch();
    List<UserDataVOWS> findUser(EjbcaWS ejbcaraws) throws Exception {
        try {
            return ejbcaraws.findUser(userMatch);
        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }
    public List<UserDataVOWS> findUsers( EjbcaWS ejbcaraws, FindUsers findUsers) throws Exception {
        //Set User Match
        for ( int i: findUsers.getUsermatch()
             ) {
            userMatch.setMatchwith(i);
        }
        //Set Value search
        userMatch.setMatchvalue(findUsers.getSearch());
        return findUser(ejbcaraws);
    }
    public UserDataVOWS findUserByUserName( EjbcaWS ejbcaraws, String userName) throws Exception {
        //Set user match to find by username
        userMatch.setMatchwith(UserMatch.MATCH_WITH_USERNAME);
        userMatch.setMatchvalue(userName);
        //Find user
        List<UserDataVOWS> listUser = findUser(ejbcaraws);
        //if list user has one item, return user
        if (listUser.size() == 1) {
            for (UserDataVOWS i :
                    listUser) {
                return i;
            }
        //if list has no or more than one, return null
        } else if (listUser.size() == 0) {
            System.out.println("No User for search!");
        } else {
            System.out.println("There are more one user using this userName");
        }
        return null;
    }

    /**
     * Revoke User
     **/
    Boolean revokeUser(EjbcaWS ejbcaraws, String username, int reason, boolean deleteUser) {
        try {
            ejbcaraws.revokeUser(username, reason, deleteUser);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Boolean deleteUser(EjbcaWS ejbcaraws, DeleteUser deleteUser) {
        return revokeUser(ejbcaraws, deleteUser.getUserName(), deleteUser.getReason(), deleteUser.isDecision());
    }

    /**
     * Show User
     **/
    public void showUser(List<UserDataVOWS> result) {
        System.out.println("\n\n");
        if (result.size() != 0) {
            for (UserDataVOWS i :
                    result) {
                System.out.println("User name               : " + i.getUsername());
                System.out.println("CA name                 : " + i.getCaName());
                System.out.println("email                   : " + i.getEmail());
                System.out.println("Certificate Profile Name: " + i.getCertificateProfileName());
                System.out.println("Status                  : " + i.getStatus());
                System.out.println("=========================================");
            }
        } else {
            System.out.println("No User for search!");
        }
    }
}
