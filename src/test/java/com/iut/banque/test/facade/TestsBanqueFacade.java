package com.iut.banque.test.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Client;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.TechnicalException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test/resources/TestsBanqueFacade-context.xml")
@Transactional("transactionManager")
public class TestsBanqueFacade {

    @Autowired
    private BanqueFacade banqueFacade;

    @Test
    public void testCrediter() throws IllegalFormatException {
        CompteSansDecouvert compte = new CompteSansDecouvert("FR1234567890", 0, new Client());
        try {
            banqueFacade.crediter(compte, 100);
            assertEquals(100, compte.getSolde(), 0.001);
        } catch (IllegalFormatException e) {
            fail("Exception ne devrait pas être levée");
        }
    }

    @Test
    public void testDebiter() throws IllegalFormatException, IllegalOperationException {
        try {
            CompteAvecDecouvert compte = new CompteAvecDecouvert("FR1234567890", 100, 50, new Client());
            banqueFacade.debiter(compte, 50);
            assertEquals(50, compte.getSolde(), 0.001);
        } catch (InsufficientFundsException | IllegalFormatException e) {
            fail("Exception ne devrait pas être levée");
        }
    }

    @Test
    public void testCreateAccount() throws IllegalFormatException {
        Client client = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
        try {
            banqueFacade.createAccount("FR1234567890", client);
            // Vérifiez que le compte a été créé correctement
        } catch (TechnicalException | IllegalFormatException e) {
            fail("Exception ne devrait pas être levée");
        }
    }

    @Test
    public void testCreateAccountWithDecouvert() throws IllegalFormatException, IllegalOperationException {
        Client client = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
        try {
            banqueFacade.createAccount("FR1234567891", client, 100);
            // Vérifiez que le compte a été créé correctement
        } catch (TechnicalException | IllegalFormatException | IllegalOperationException e) {
            fail("Exception ne devrait pas être levée");
        }
    }

    @Test
    public void testDeleteAccount() throws IllegalFormatException {
        CompteSansDecouvert compte = new CompteSansDecouvert("FR1234567890", 0, new Client());
        try {
            banqueFacade.deleteAccount(compte);
            // Vérifiez que le compte a été supprimé correctement
        } catch (IllegalOperationException | TechnicalException e) {
            fail("Exception ne devrait pas être levée");
        }
    }
}