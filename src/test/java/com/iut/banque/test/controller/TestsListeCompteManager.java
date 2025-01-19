package com.iut.banque.test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iut.banque.controller.ListeCompteManager;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test/resources/TestsListeCompteManager-context.xml")
public class TestsListeCompteManager {

    @Autowired
    private ListeCompteManager listeCompteManager;
    
    private Client client;

    @Before
    public void setUp() throws IllegalFormatException {
        client = new Client("Test", "User", "Address", true, "t.user1", "password", "1234567890");
    }

    @Test
    public void testGetComptesClient() throws IllegalFormatException, IllegalOperationException {
        // Ajouter des comptes au client
        client.addAccount(new CompteSansDecouvert("FR1234567890", 100, client));
        client.addAccount(new CompteAvecDecouvert("FR1234567891", 200, 100, client));
        
        listeCompteManager.setClient(client);
        Map<String, Compte> comptes = client.getComptes();
        
        assertNotNull(comptes);
        assertEquals(2, comptes.size());
        assertTrue(comptes.containsKey("FR1234567890"));
        assertTrue(comptes.containsKey("FR1234567891"));
    }

    @Test
    public void testSetAndGetClient() {
        listeCompteManager.setClient(client);
        assertEquals(client, listeCompteManager.getClient());
    }
} 