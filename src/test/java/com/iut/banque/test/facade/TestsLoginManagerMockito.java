package com.iut.banque.test.facade;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.facade.LoginManager;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;

public class TestsLoginManagerMockito {
    
    private LoginManager loginManager;
    
    @Mock
    private IDao daoMock;
    
    @Mock
    private Client clientMock;
    
    @Mock
    private Gestionnaire gestionnaireMock;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        loginManager = new LoginManager();
        loginManager.setDao(daoMock);
        
        // Setup client mock
        when(clientMock.getUserId()).thenReturn("j.doe1");
        
        // Setup gestionnaire mock
        when(gestionnaireMock.getUserId()).thenReturn("admin");
        
        // Setup DAO mock
        when(daoMock.getUserById("j.doe1")).thenReturn(clientMock);
        when(daoMock.getUserById("admin")).thenReturn(gestionnaireMock);
        when(daoMock.isUserAllowed("j.doe1", "toto")).thenReturn(true);
        when(daoMock.isUserAllowed("admin", "adminpass")).thenReturn(true);
        when(daoMock.isUserAllowed(anyString(), eq("invalidPassword"))).thenReturn(false);
    }
    
    @Test
    public void testTryLogin() throws IllegalFormatException {
        int result = loginManager.tryLogin("j.doe1", "toto");
        assertEquals(LoginConstants.USER_IS_CONNECTED, result);
        verify(daoMock).isUserAllowed("j.doe1", "toto");
        verify(daoMock).getUserById("j.doe1");
        assertEquals(clientMock, loginManager.getConnectedUser());
    }
    
    @Test
    public void testTryLoginWithInvalidCredentials() throws IllegalFormatException {
        int result = loginManager.tryLogin("invalidUser", "invalidPassword");
        assertEquals(LoginConstants.LOGIN_FAILED, result);
        verify(daoMock).isUserAllowed("invalidUser", "invalidPassword");
        assertNull(loginManager.getConnectedUser());
    }
    
    @Test
    public void testTryLoginAsManager() throws IllegalFormatException {
        int result = loginManager.tryLogin("admin", "adminpass");
        assertEquals(LoginConstants.MANAGER_IS_CONNECTED, result);
        verify(daoMock).isUserAllowed("admin", "adminpass");
        verify(daoMock).getUserById("admin");
        assertEquals(gestionnaireMock, loginManager.getConnectedUser());
    }
    
    @Test
    public void testGetConnectedUser() throws IllegalFormatException {
        loginManager.tryLogin("j.doe1", "toto");
        assertEquals("j.doe1", loginManager.getConnectedUser().getUserId());
    }
    
    @Test
    public void testSetCurrentUser() {
        loginManager.setCurrentUser(clientMock);
        assertEquals("j.doe1", loginManager.getConnectedUser().getUserId());
    }
    
    @Test
    public void testLogout() throws IllegalFormatException {
        loginManager.tryLogin("j.doe1", "toto");
        loginManager.logout();
        assertNull(loginManager.getConnectedUser());
    }
    
    @Test
    public void testAdminIsAlwaysConnected() {
        when(daoMock.isUserAllowed(anyString(), anyString())).thenReturn(true);
        when(daoMock.getUserById(anyString())).thenReturn(gestionnaireMock);
        
        try {
            // Try with any credentials
            int result = loginManager.tryLogin("anything", "anything");
            assertEquals(LoginConstants.MANAGER_IS_CONNECTED, result);
            assertEquals("admin", loginManager.getConnectedUser().getUserId());
            
            // Try again with different credentials
            result = loginManager.tryLogin("different", "credentials");
            assertEquals(LoginConstants.MANAGER_IS_CONNECTED, result);
            assertEquals("admin", loginManager.getConnectedUser().getUserId());
        } catch (IllegalFormatException e) {
            fail("Exception shouldn't be thrown: " + e.getMessage());
        }
    }
} 