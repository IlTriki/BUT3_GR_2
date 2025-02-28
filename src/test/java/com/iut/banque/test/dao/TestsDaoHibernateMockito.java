package com.iut.banque.test.dao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.iut.banque.dao.DaoHibernate;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;

public class TestsDaoHibernateMockito {

    private DaoHibernate daoHibernate;
    
    @Mock
    private SessionFactory sessionFactoryMock;
    
    @Mock
    private Session sessionMock;
    
    @Mock
    private Transaction transactionMock;
    
    @Mock
    private Query queryMock;
    
    @Mock
    private Client clientMock;
    
    @Mock
    private Compte compteMock;
    
    @Mock
    private CompteAvecDecouvert compteAvecDecouvertMock;
    
    @Mock
    private CompteSansDecouvert compteSansDecouvertMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        daoHibernate = new DaoHibernate();
        daoHibernate.setSessionFactory(sessionFactoryMock);
        
        // Setup session mock
        when(sessionFactoryMock.openSession()).thenReturn(sessionMock);
        when(sessionMock.beginTransaction()).thenReturn(transactionMock);
        when(sessionMock.createQuery(anyString())).thenReturn(queryMock);
        
        // Setup client mock
        when(clientMock.getUserId()).thenReturn("j.doe1");
        
        // Setup compte mock
        when(compteMock.getNumeroCompte()).thenReturn("IO1010010001");
        when(compteMock.getOwner()).thenReturn(clientMock);
    }
    
    @Test
    public void testGetAccountById() {
        when(sessionMock.get(Compte.class, "IO1010010001")).thenReturn(compteMock);
        
        Compte account = daoHibernate.getAccountById("IO1010010001");
        
        assertEquals(compteMock, account);
        verify(sessionMock, times(1)).get(Compte.class, "IO1010010001");
        verify(sessionMock, times(1)).close();
    }
    
    @Test
    public void testGetAccountByIdDoesntExist() {
        when(sessionMock.get(Compte.class, "IO1111111111")).thenReturn(null);
        
        Compte account = daoHibernate.getAccountById("IO1111111111");
        
        assertNull(account);
        verify(sessionMock, times(1)).get(Compte.class, "IO1111111111");
        verify(sessionMock, times(1)).close();
    }
    
    @Test
    public void testCreateCompteAvecDecouvert() throws IllegalFormatException, IllegalOperationException, TechnicalException {
        String id = "NW1010010001";
        when(sessionMock.save(any(CompteAvecDecouvert.class))).thenReturn(id);
        
        Compte compte = daoHibernate.createCompteAvecDecouvert(0, id, 100, clientMock);
        
        assertNotNull(compte);
        assertEquals(id, compte.getNumeroCompte());
        assertEquals(clientMock, compte.getOwner());
        assertTrue(compte instanceof CompteAvecDecouvert);
        assertEquals(100, ((CompteAvecDecouvert) compte).getDecouvertAutorise(), 0.001);
        
        verify(sessionMock, times(1)).save(any(CompteAvecDecouvert.class));
        verify(transactionMock, times(1)).commit();
        verify(sessionMock, times(1)).close();
    }
    
    @Test
    public void testCreateCompteSansDecouvert() throws IllegalFormatException, TechnicalException {
        String id = "NW1010010002";
        when(sessionMock.save(any(CompteSansDecouvert.class))).thenReturn(id);
        
        Compte compte = daoHibernate.createCompteSansDecouvert(0, id, clientMock);
        
        assertNotNull(compte);
        assertEquals(id, compte.getNumeroCompte());
        assertEquals(clientMock, compte.getOwner());
        assertTrue(compte instanceof CompteSansDecouvert);
        
        verify(sessionMock, times(1)).save(any(CompteSansDecouvert.class));
        verify(transactionMock, times(1)).commit();
        verify(sessionMock, times(1)).close();
    }
    
    @Test
    public void testGetUserById() {
        when(sessionMock.get(Utilisateur.class, "j.doe1")).thenReturn(clientMock);
        
        Utilisateur user = daoHibernate.getUserById("j.doe1");
        
        assertEquals(clientMock, user);
        verify(sessionMock, times(1)).get(Utilisateur.class, "j.doe1");
        verify(sessionMock, times(1)).close();
    }
} 