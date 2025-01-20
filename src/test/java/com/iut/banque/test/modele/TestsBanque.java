package com.iut.banque.test.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.modele.Banque;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;

public class TestsBanque {

    private Banque banque;
    private Client client;
    private CompteSansDecouvert compteSansDecouvert;
    private CompteAvecDecouvert compteAvecDecouvert;

    @Before
    public void setUp() throws IllegalFormatException, IllegalOperationException {
        banque = new Banque();
        client = new Client("Doe", "John", "123 Main St", true, "j.doe1", "password", "1234567890");
        compteSansDecouvert = new CompteSansDecouvert("FR1234567890", 100, client);
        compteAvecDecouvert = new CompteAvecDecouvert("FR1234567891", 100, 50, client);

        Map<String, Client> clients = new HashMap<>();
        clients.put(client.getUserId(), client);
        banque.setClients(clients);

        Map<String, Compte> accounts = new HashMap<>();
        accounts.put(compteSansDecouvert.getNumeroCompte(), compteSansDecouvert);
        accounts.put(compteAvecDecouvert.getNumeroCompte(), compteAvecDecouvert);
        banque.setAccounts(accounts);
    }

    @Test
    public void testDebiter() throws IllegalFormatException {
        try {
            banque.debiter(compteSansDecouvert, 50);
            assertEquals(50, compteSansDecouvert.getSolde(), 0.001);
        } catch (InsufficientFundsException | IllegalFormatException e) {
            fail("Exception ne devrait pas être levée");
        }
    }

    @Test
    public void testCrediter() throws IllegalFormatException {
        try {
            banque.crediter(compteSansDecouvert, 50);
            assertEquals(150, compteSansDecouvert.getSolde(), 0.001);
        } catch (IllegalFormatException e) {
            fail("Exception ne devrait pas être levée");
        }
    }

    @Test
    public void testDeleteUser() throws IllegalOperationException {
        banque.deleteUser(client.getUserId());
        assertNull(banque.getClients().get(client.getUserId()));
    }

    @Test
    public void testChangeDecouvert() throws IllegalFormatException {
        try {
            banque.changeDecouvert(compteAvecDecouvert, 100);
            assertEquals(100, compteAvecDecouvert.getDecouvertAutorise(), 0.001);
        } catch (IllegalFormatException | IllegalOperationException e) {
            fail("Exception ne devrait pas être levée");
        }
    }
}