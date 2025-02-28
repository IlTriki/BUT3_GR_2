package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.CompteAvecDecouvert;

public class TestsCompteAvecDecouvertMockito {

    private CompteAvecDecouvert compte;
    
    @Mock
    private Client clientMock;

    @Before
    public void setUp() throws IllegalFormatException, IllegalOperationException {
        MockitoAnnotations.initMocks(this);
        compte = new CompteAvecDecouvert("FR0123456789", 100, 100, clientMock);
    }

    @Test
    public void testSetDecouvertAutoriseValeurPositive() throws IllegalFormatException, IllegalOperationException {
        double nouveauDecouvert = 200.0;
        compte.setDecouverAutorise(nouveauDecouvert);
        assertEquals(nouveauDecouvert, compte.getDecouvertAutorise(), 0.001);
    }

    @Test(expected = IllegalFormatException.class)
    public void testSetDecouvertAutoriseValeurNegative() throws IllegalFormatException, IllegalOperationException {
        compte.setDecouverAutorise(-100.0);
    }

    @Test(expected = IllegalOperationException.class)
    public void testSetDecouvertAutoriseMontantInferieurAuSoldeNegatif() throws IllegalFormatException, IllegalOperationException, InsufficientFundsException {
        compte.debiter(150); // Solde devient -50
        compte.setDecouverAutorise(40); // Devrait échouer car le solde est à -50
    }

    @Test
    public void testDebiterCompteAvecDecouvertValeurPossible() throws IllegalFormatException, InsufficientFundsException {
        compte.debiter(150);
        assertEquals(-50.0, compte.getSolde(), 0.0001);
    }

    @Test
    public void testDebiterCompteAvecDecouvertValeurImpossible() throws IllegalFormatException {
        try {
            compte.debiter(250);
            fail("Il devrait avoir une InsufficientFundsException ici.");
        } catch (InsufficientFundsException e) {
            // Expected exception
        }
    }
    
    @Test
    public void testDebiterAvecFondsInssufisants() {
        try {
            compte.debiter(201); // Dépasse le découvert autorisé
            fail("Devrait lever une InsufficientFundsException");
        } catch (InsufficientFundsException e) {
            // Expected
        } catch (IllegalFormatException e) {
            fail("Exception inattendue: " + e.getMessage());
        }
    }
} 