package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.CompteSansDecouvert;

public class TestsCompteSansDecouvertMockito {

    private CompteSansDecouvert compte;
    
    @Mock
    private Client clientMock;

    @Before
    public void setUp() throws IllegalFormatException {
        MockitoAnnotations.initMocks(this);
        compte = new CompteSansDecouvert("FR0123456789", 100, clientMock);
    }

    @Test
    public void testGetClassName() {
        assertEquals("CompteSansDecouvert", compte.getClassName());
    }

    @Test
    public void testCrediterCompteMontantNegatif() {
        try {
            compte.debiter(-100);
            fail("La méthode n'a pas renvoyé d'exception!");
        } catch (IllegalFormatException ife) {
            // Expected exception
        } catch (Exception e) {
            fail("Exception de type " + e.getClass().getSimpleName() 
                + " récupérée alors qu'un IllegalFormatException était attendu");
        }
    }

    @Test
    public void testDebiterCompteSansDecouvertValeurPossible() throws IllegalFormatException, InsufficientFundsException {
        compte.debiter(50);
        assertEquals(50.0, compte.getSolde(), 0.001);
    }

    @Test
    public void testDebiterCompteSansDecouvertValeurImpossible() throws IllegalFormatException {
        try {
            compte.debiter(200);
            fail("Il devrait avoir une InsufficientFundsException ici.");
        } catch (InsufficientFundsException e) {
            // Expected exception
        }
    }
    
    @Test
    public void testCrediterMontantZero() throws IllegalFormatException {
        double soldeInitial = compte.getSolde();
        compte.crediter(0);
        assertEquals(soldeInitial, compte.getSolde(), 0.001);
    }
    
    @Test
    public void testCrediterGrandMontant() throws IllegalFormatException {
        compte.crediter(1000000);
        assertEquals(1000100, compte.getSolde(), 0.001);
    }
} 