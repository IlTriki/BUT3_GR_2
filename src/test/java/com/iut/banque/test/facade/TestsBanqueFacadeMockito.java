package com.iut.banque.test.facade;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.facade.BanqueManager;
import com.iut.banque.facade.LoginManager;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;

public class TestsBanqueFacadeMockito {

    private BanqueFacade banqueFacade;
    
    @Mock
    private LoginManager loginManagerMock;
    
    @Mock
    private BanqueManager banqueManagerMock;
    
    @Mock
    private CompteSansDecouvert compteSansDecouvertMock;
    
    @Mock
    private CompteAvecDecouvert compteAvecDecouvertMock;
    
    @Mock
    private Client clientMock;
    
    @Mock
    private Gestionnaire gestionnaireMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        banqueFacade = new BanqueFacade(loginManagerMock, banqueManagerMock);
    }

    @Test
    public void testGetConnectedUser() {
        when(loginManagerMock.getConnectedUser()).thenReturn(clientMock);
        assertEquals(clientMock, banqueFacade.getConnectedUser());
        verify(loginManagerMock, times(1)).getConnectedUser();
    }

    @Test
    public void testTryLoginAsClient() throws IllegalFormatException {
        when(loginManagerMock.tryLogin("j.doe1", "password")).thenReturn(LoginConstants.USER_IS_CONNECTED);
        int result = banqueFacade.tryLogin("j.doe1", "password");
        assertEquals(LoginConstants.USER_IS_CONNECTED, result);
        verify(loginManagerMock, times(1)).tryLogin("j.doe1", "password");
    }

    @Test
    public void testTryLoginAsManager() throws IllegalFormatException {
        when(loginManagerMock.tryLogin("admin", "password")).thenReturn(LoginConstants.MANAGER_IS_CONNECTED);
        int result = banqueFacade.tryLogin("admin", "password");
        assertEquals(LoginConstants.MANAGER_IS_CONNECTED, result);
        verify(loginManagerMock, times(1)).tryLogin("admin", "password");
        verify(banqueManagerMock, times(1)).loadAllClients();
    }
    
    @Test
    public void testCrediter() throws IllegalFormatException {
        banqueFacade.crediter(compteSansDecouvertMock, 100);
        verify(banqueManagerMock, times(1)).crediter(compteSansDecouvertMock, 100);
    }
    
    @Test
    public void testDebiter() throws IllegalFormatException, InsufficientFundsException {
        banqueFacade.debiter(compteAvecDecouvertMock, 50);
        verify(banqueManagerMock, times(1)).debiter(compteAvecDecouvertMock, 50);
    }
    
    @Test
    public void testCreateAccount() throws TechnicalException, IllegalFormatException {
        banqueFacade.createAccount("FR1234567890", clientMock);
        verify(banqueManagerMock, times(1)).createAccount("FR1234567890", clientMock);
    }
    
    @Test
    public void testCreateAccountWithDecouvert() throws TechnicalException, IllegalFormatException, IllegalOperationException {
        banqueFacade.createAccount("FR1234567891", clientMock, 100);
        verify(banqueManagerMock, times(1)).createAccount("FR1234567891", clientMock, 100);
    }
    
    @Test
    public void testDeleteAccount() throws IllegalOperationException, TechnicalException {
        banqueFacade.deleteAccount(compteSansDecouvertMock);
        verify(banqueManagerMock, times(1)).deleteAccount(compteSansDecouvertMock);
    }
    
    @Test
    public void testAdminIsAlwaysConnected() throws IllegalFormatException {
        // Créer un gestionnaire avec mode admin
        Gestionnaire admin = new Gestionnaire();
        admin.setUserId("admin");
        
        // Toujours retourner l'admin peu importe les identifiants
        when(loginManagerMock.tryLogin(anyString(), anyString())).thenReturn(LoginConstants.MANAGER_IS_CONNECTED);
        when(loginManagerMock.getConnectedUser()).thenReturn(admin);
        
        // Test avec n'importe quels identifiants
        int result = banqueFacade.tryLogin("any", "credentials");
        assertEquals(LoginConstants.MANAGER_IS_CONNECTED, result);
        assertEquals("admin", banqueFacade.getConnectedUser().getUserId());
        
        // Vérifier que loadAllClients est appelé quand un gestionnaire se connecte
        verify(banqueManagerMock, times(1)).loadAllClients();
    }
} 