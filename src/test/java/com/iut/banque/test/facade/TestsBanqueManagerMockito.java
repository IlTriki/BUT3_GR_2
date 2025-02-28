package com.iut.banque.test.facade;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.facade.BanqueManager;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Banque;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;

public class TestsBanqueManagerMockito {

    private BanqueManager banqueManager;
    
    @Mock
    private IDao daoMock;
    
    @Mock
    private Banque banqueMock;
    
    @Mock
    private Client clientMock;
    
    @Mock
    private Gestionnaire gestionnaireMock;
    
    @Mock
    private CompteAvecDecouvert compteAvecDecouvertMock;
    
    @Mock
    private CompteSansDecouvert compteSansDecouvertMock;
    
    @Before
    public void setUp() throws IllegalFormatException, IllegalOperationException {
        MockitoAnnotations.initMocks(this);
        
        banqueManager = new BanqueManager();
        banqueManager.setDao(daoMock);
        
        // Setup client mock
        when(clientMock.getUserId()).thenReturn("j.doe1");
        
        // Setup compte mocks
        when(compteAvecDecouvertMock.getNumeroCompte()).thenReturn("CADV000000");
        when(compteAvecDecouvertMock.getSolde()).thenReturn(0.0);
        when(compteAvecDecouvertMock.getDecouvertAutorise()).thenReturn(100.0);
        
        when(compteSansDecouvertMock.getNumeroCompte()).thenReturn("CSDV000000");
        when(compteSansDecouvertMock.getSolde()).thenReturn(0.0);
        
        // Setup DAO mock
        when(daoMock.getUserById("j.doe1")).thenReturn(clientMock);
        when(daoMock.getUserById("admin")).thenReturn(gestionnaireMock);
        when(daoMock.getAccountById("CADV000000")).thenReturn(compteAvecDecouvertMock);
        when(daoMock.getAccountById("CSDV000000")).thenReturn(compteSansDecouvertMock);
    }

    @Test
    public void testGetAccountById() {
        Compte account = banqueManager.getAccountById("CADV000000");
        assertEquals(compteAvecDecouvertMock, account);
        verify(daoMock, times(1)).getAccountById("CADV000000");
    }

    @Test
    public void testGetUserById() {
        Utilisateur user = banqueManager.getUserById("j.doe1");
        assertEquals(clientMock, user);
        verify(daoMock, times(1)).getUserById("j.doe1");
    }
    
    @Test
    public void testCreationDunClient() throws IllegalOperationException {
        // Setup pour éviter doublons de numéro de téléphone
        when(daoMock.getAllClients()).thenReturn(new HashMap<>());
        
        banqueManager.createClient("t.test1", "password", "test1nom", "test1prenom", "test town", true, "4242424242");
        
        verify(daoMock, times(1)).createClient(
            eq("t.test1"), eq("password"), eq("test1nom"), eq("test1prenom"), 
            eq("test town"), eq(true), eq("4242424242")
        );
    }
    
    @Test(expected = IllegalOperationException.class)
    public void testCreationDunClientAvecDeuxNumerosDeCompteIdentiques() throws IllegalOperationException {
        // Setup pour simuler un doublon de numéro de téléphone
        Map<String, Client> clients = new HashMap<>();
        Client existingClient = new Client();
        existingClient.setUserId("existingClient");
        existingClient.setNumeroTelephone("0101010101");
        clients.put(existingClient.getUserId(), existingClient);
        
        when(daoMock.getAllClients()).thenReturn(clients);
        
        banqueManager.createClient("t.test1", "password", "test1nom", "test1prenom", "test town", true, "0101010101");
    }

    @Test
    public void testSuppressionDunCompteAvecDecouvertAvecSoldeZero() throws IllegalOperationException {
        banqueManager.deleteAccount(compteAvecDecouvertMock);
        verify(daoMock, times(1)).deleteAccount(compteAvecDecouvertMock);
    }
    
    @Test(expected = IllegalOperationException.class)
    public void testSuppressionDunCompteAvecDecouvertAvecSoldeDifferentDeZero() throws IllegalOperationException {
        // Override la valeur retournée pour le solde
        when(compteAvecDecouvertMock.getSolde()).thenReturn(50.0);
        
        banqueManager.deleteAccount(compteAvecDecouvertMock);
    }
    
    @Test
    public void testChangeDecouvertSuccess() throws IllegalFormatException, IllegalOperationException {
        double nouveauDecouvert = 200.0;
        
        banqueManager.changeDecouvert(compteAvecDecouvertMock, nouveauDecouvert);
        
        verify(compteAvecDecouvertMock, times(1)).setDecouverAutorise(nouveauDecouvert);
        verify(daoMock, times(1)).updateAccount(compteAvecDecouvertMock);
    }
    
    @Test(expected = IllegalFormatException.class)
    public void testChangeDecouvertNegative() throws IllegalFormatException, IllegalOperationException {
        banqueManager.changeDecouvert(compteAvecDecouvertMock, -100.0);
    }
} 