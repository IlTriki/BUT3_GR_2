package com.iut.banque.test.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import com.iut.banque.facade.LoginManager;
import com.iut.banque.modele.Utilisateur;
import com.iut.banque.modele.Client;
import com.iut.banque.constants.LoginConstants;
import com.iut.banque.exceptions.IllegalFormatException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test/resources/TestsLoginManager-context.xml")
@Transactional("transactionManager")
public class TestsLoginManager {
    
    @Autowired
    private LoginManager loginManager;

    @Test
    public void testTryLogin() throws IllegalFormatException {
        int result = loginManager.tryLogin("j.doe1", "toto");
        assertEquals(LoginConstants.USER_IS_CONNECTED, result);
    }

    @Test
    public void testTryLoginWithInvalidCredentials() throws IllegalFormatException {
        int result = loginManager.tryLogin("invalidUser", "invalidPassword");
        assertEquals(LoginConstants.LOGIN_FAILED, result);
    }

    @Test
    public void testTryLoginAsManager() throws IllegalFormatException {
        int result = loginManager.tryLogin("admin", "adminpass");
        assertEquals(LoginConstants.MANAGER_IS_CONNECTED, result);
    }

    @Test
    public void testGetConnectedUser() throws IllegalFormatException {
        loginManager.tryLogin("j.doe1", "toto");
        assertEquals("j.doe1", loginManager.getConnectedUser().getUserId());
    }

    @Test
    public void testSetCurrentUser() throws IllegalFormatException {
        Utilisateur user = new Client();
        user.setUserId("j.doe1"); 
        loginManager.setCurrentUser(user);
        assertEquals("j.doe1", loginManager.getConnectedUser().getUserId());
    }

    @Test
    public void testLogout() throws IllegalFormatException {
        loginManager.tryLogin("j.doe1", "toto");
        loginManager.logout();
        assertNull(loginManager.getConnectedUser());
    }
}