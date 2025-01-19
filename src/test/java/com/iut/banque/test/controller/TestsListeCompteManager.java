package com.iut.banque.test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.iut.banque.controller.ListeCompteManager;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.modele.Client;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test/resources/TestsListeCompteManager-context.xml")
@Transactional("transactionManager")
public class TestsListeCompteManager {

    @Autowired
    private ListeCompteManager listeCompteManager;

    @Test
    public void testSetAndGetClient() {
        try {
            Client client = new Client("Test", "User", "Address", true, "t.user1", "password", "1234567890");
            listeCompteManager.setClient(client);
            assertNotNull(listeCompteManager.getClient());
            assertEquals(client, listeCompteManager.getClient());
        } catch (IllegalFormatException e) {
            fail("IllegalFormatException récupérée : " + e.getMessage());
        } catch (Exception e) {
            fail("Exception inattendue récupérée : " + e.getMessage());
        }
    }
} 