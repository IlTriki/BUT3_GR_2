package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteSansDecouvert;

public class TestsCompteMockito {

    private Compte compte;
    
    @Mock
    private Client clientMock;

    @Before
    public void setUp() throws IllegalFormatException {
        MockitoAnnotations.initMocks(this);
        compte = new CompteSansDecouvert("WU1234567890", 0, clientMock);
    }

    @Test
    public void testCrediterCompte() throws IllegalFormatException {
        compte.crediter(100);
        assertEquals(100.0, compte.getSolde(), 0.001);
    }

    @Test
    public void testCrediterCompteMontantNegatif() {
        try {
            compte.crediter(-100);
            fail("La méthode n'a pas renvoyé d'exception!");
        } catch (IllegalFormatException ife) {
            // Expected exception
        }
    }

    @Test
    public void testMethodeCheckFormatNumeroCompteCorrect() {
        String strNumCompte = "FR0123456789";
        assertTrue(Compte.checkFormatNumeroCompte(strNumCompte));
    }

    @Test
    public void testMethodeCheckFormatNumeroCompteAvecUneSeuleLettreAuDebut() {
        String strNumCompte = "F0123456789";
        assertFalse(Compte.checkFormatNumeroCompte(strNumCompte));
    }

    @Test
    public void testGetOwner() {
        assertEquals(clientMock, compte.getOwner());
    }

    @Test
    public void testSetOwner() throws IllegalFormatException {
        Client newClientMock = mock(Client.class);
        compte.setOwner(newClientMock);
        assertEquals(newClientMock, compte.getOwner());
    }
} 