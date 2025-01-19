package com.iut.banque.test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.iut.banque.controller.ListeCompteManager;
import com.iut.banque.modele.Client;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test/resources/TestsListeCompteManager-context.xml")
@Transactional("transactionManager")
public class TestsListeCompteManager {

    @Autowired
    private ListeCompteManager listeCompteManager;

    @Test
    public void testSetAndGetClient() {
        Client client = new Client("Test", "User", "Address", true, "t.user1", "password", "1234567890");
        listeCompteManager.setClient(client);
        assertNotNull(listeCompteManager.getClient());
        assertEquals(client, listeCompteManager.getClient());
    }
} 