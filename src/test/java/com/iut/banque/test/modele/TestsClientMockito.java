package com.iut.banque.test.modele;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.modele.Client;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;

import java.util.HashMap;
import java.util.Map;

public class TestsClientMockito {

    @Mock
    private CompteSansDecouvert compteSansDecouvertMock;
    
    @Mock
    private CompteAvecDecouvert compteAvecDecouvertPositifMock;
    
    @Mock
    private CompteAvecDecouvert compteAvecDecouvertNegatifMock;
    
    private Client client;

    @Before
    public void setUp() throws IllegalFormatException {
        MockitoAnnotations.initMocks(this);
        client = new Client("Doe", "John", "123 Main St", true, "j.doe1", "password", "1234567890");
        
        // Setup mocks
        when(compteSansDecouvertMock.getNumeroCompte()).thenReturn("FR1234567890");
        when(compteSansDecouvertMock.getSolde()).thenReturn(100.0);
        
        when(compteAvecDecouvertPositifMock.getNumeroCompte()).thenReturn("FR1234567891");
        when(compteAvecDecouvertPositifMock.getSolde()).thenReturn(100.0);
        
        when(compteAvecDecouvertNegatifMock.getNumeroCompte()).thenReturn("FR1234567892");
        when(compteAvecDecouvertNegatifMock.getSolde()).thenReturn(-50.0);
    }

    @Test
    public void testMethodeCheckFormatUserIdClientCorrect() {
        String strClient = "a.utilisateur928";
        assertTrue(Client.checkFormatUserIdClient(strClient));
    }

    @Test
    public void testMethodeCheckFormatUserIdClientCommencantParUnChiffre() {
        String strClient = "32a.abc1";
        assertFalse(Client.checkFormatUserIdClient(strClient));
    }

    @Test
    public void testMethodePossedeComptesADecouvertPourClientAvecCompteADecouvert() throws IllegalFormatException, IllegalOperationException {
        // Add mocked accounts
        client.addAccount(compteSansDecouvertMock);
        client.addAccount(compteAvecDecouvertNegatifMock);
        
        assertTrue(client.possedeComptesADecouvert());
    }
    
    @Test
    public void testMethodePossedeComptesADecouvertPourClientSansCompteADecouvert() throws IllegalFormatException, IllegalOperationException {
        // Add mocked account
        client.addAccount(compteSansDecouvertMock);
        
        // Using stubbed positive value for compte avec decouvert
        client.addAccount(compteAvecDecouvertPositifMock);
        
        assertFalse(client.possedeComptesADecouvert());
    }
    
    @Test
    public void testMethodeGetCompteAvecSoldeNonNulAvecCompteNonNul() throws IllegalFormatException, IllegalOperationException {
        client.addAccount(compteSansDecouvertMock);
        
        Map<String, Compte> comptesNonNuls = client.getComptesAvecSoldeNonNul();
        assertEquals(1, comptesNonNuls.size());
        assertTrue(comptesNonNuls.containsKey("FR1234567890"));
    }
    
    @Test
    public void testMethodeGetCompteAvecSoldeNonNulAvecCompteSoldeZero() throws IllegalFormatException, IllegalOperationException {
        // Create a new mock with zero balance
        CompteSansDecouvert compteZeroMock = mock(CompteSansDecouvert.class);
        when(compteZeroMock.getNumeroCompte()).thenReturn("FR0000000000");
        when(compteZeroMock.getSolde()).thenReturn(0.0);
        
        client.addAccount(compteZeroMock);
        
        Map<String, Compte> comptesNonNuls = client.getComptesAvecSoldeNonNul();
        assertEquals(0, comptesNonNuls.size());
    }
} 