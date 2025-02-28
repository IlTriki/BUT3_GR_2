package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
import com.iut.banque.modele.Banque;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;

public class TestsBanqueMockito {

    private Banque banque;
    
    @Mock
    private Client clientMock;
    
    @Mock
    private CompteSansDecouvert compteSansDecouvertMock;
    
    @Mock
    private CompteAvecDecouvert compteAvecDecouvertMock;

    @Before
    public void setUp() throws IllegalFormatException, IllegalOperationException {
        MockitoAnnotations.initMocks(this);
        
        banque = new Banque();
        
        // Setup mocks
        when(clientMock.getUserId()).thenReturn("j.doe1");
        when(compteSansDecouvertMock.getNumeroCompte()).thenReturn("FR1234567890");
        when(compteSansDecouvertMock.getOwner()).thenReturn(clientMock);
        when(compteAvecDecouvertMock.getNumeroCompte()).thenReturn("FR1234567891");
        when(compteAvecDecouvertMock.getOwner()).thenReturn(clientMock);
        
        // Setup client map
        Map<String, Client> clients = new HashMap<>();
        clients.put(clientMock.getUserId(), clientMock);
        banque.setClients(clients);

        // Setup account map
        Map<String, Compte> accounts = new HashMap<>();
        accounts.put(compteSansDecouvertMock.getNumeroCompte(), compteSansDecouvertMock);
        accounts.put(compteAvecDecouvertMock.getNumeroCompte(), compteAvecDecouvertMock);
        banque.setAccounts(accounts);
    }

    @Test
    public void testDebiter() throws IllegalFormatException, InsufficientFundsException {
        banque.debiter(compteSansDecouvertMock, 50);
        verify(compteSansDecouvertMock, times(1)).debiter(50);
    }

    @Test
    public void testCrediter() throws IllegalFormatException {
        banque.crediter(compteSansDecouvertMock, 50);
        verify(compteSansDecouvertMock, times(1)).crediter(50);
    }

    @Test
    public void testDeleteUser() throws IllegalOperationException {
        banque.deleteUser(clientMock.getUserId());
        assertNull(banque.getClients().get(clientMock.getUserId()));
    }

    @Test
    public void testChangeDecouvert() throws IllegalFormatException, IllegalOperationException {
        banque.changeDecouvert(compteAvecDecouvertMock, 100);
        verify(compteAvecDecouvertMock, times(1)).setDecouverAutorise(100);
    }
} 