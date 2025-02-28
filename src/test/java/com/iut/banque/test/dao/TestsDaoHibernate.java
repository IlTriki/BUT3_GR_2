package com.iut.banque.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

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
//import org.junit.Ignore;
//
//@Ignore

/**
 * Class de test pour la DAO.
 * 
 * L'annotation @Rollback n'est pas nécéssaire partout car par défaut elle est
 * true pour les méthodes de test.
 */
// @RunWith indique à JUnit de prendre le class runner de Spirng
@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration permet de charger le context utilisé pendant les tests.
// Par défault (si aucun argument n'est précisé), cherche le fichier
// TestsDaoHibernate-context.xml dans le même dosssier que la classe
@ContextConfiguration("/test/resources/TestsDaoHibernate-context.xml")
@Transactional("transactionManager")
public class TestsDaoHibernate {

	// Indique que c'est un champ à injecter automatiquement. Le bean est choisi
	// en fonction du type.
	@Autowired
	private DaoHibernate daoHibernate;

	@Test
	public void testGetAccountByIdExist() {
		Compte account = daoHibernate.getAccountById("IO1010010001");
		if (account == null) {
			fail("Le compte ne doit pas être null.");
		} else if (!"IO1010010001".equals(account.getNumeroCompte()) && !"j.doe2".equals(account.getOwner().getUserId())
				&& !"IO1010010001".equals(account.getNumeroCompte())) {
			fail("Les informations du compte ne correspondent pas à celles de la BDD.");
		}
	}

	@Test
	public void testGetAccountByIdDoesntExist() {
		Compte account = daoHibernate.getAccountById("IO1111111111");
		if (account != null) {
			fail("Le compte n'aurait pas du être renvoyé.");
		}
	}

	@Test
	public void testGetCompteSansDecouvert() {
		assertTrue(daoHibernate.getAccountById("SA1011011011") instanceof CompteSansDecouvert);
	}

	@Test
	public void testGetCompteAvecDecouvert() {
		assertTrue(daoHibernate.getAccountById("AV1011011011") instanceof CompteAvecDecouvert);
	}

	@Test
	public void testCreateCompteAvecDecouvert() {
		Client client = (Client) daoHibernate.getUserById("c.exist");
		String id = "NW1010010001";
		try {
			Compte compte = daoHibernate.createCompteAvecDecouvert(0, id, 100, client);
			assertEquals(0, compte.getSolde(), 0.001);
			assertEquals(id, compte.getNumeroCompte());
			assertEquals("c.exist", compte.getOwner().getUserId());
			assertEquals(100, ((CompteAvecDecouvert) compte).getDecouvertAutorise(), 0.001);
			assertTrue(compte instanceof CompteAvecDecouvert);
		} catch (TechnicalException | IllegalFormatException | IllegalOperationException e) {
			e.printStackTrace();
			fail("Le compte aurait du être créé.");
		}
	}

	@Test
	public void testCreateCompteAvecDecouvertExistingId() {
		Client client = (Client) daoHibernate.getUserById("c.exist");
		String id = "AV1011011011";
		try {
			Compte compte = daoHibernate.createCompteAvecDecouvert(0, id, 100, client);
			if (compte != null) {
				System.out.println(compte);
				fail("Le compte n'aurait pas du être créé.");
			}
		} catch (TechnicalException | IllegalFormatException | IllegalOperationException e) {
			assertTrue(e instanceof TechnicalException);
		}
	}

	@Test
	public void testCreateCompteSansDecouvert() {
		Client client = (Client) daoHibernate.getUserById("c.exist");
		String id = "NW1010010001";
		try {
			Compte compte = daoHibernate.createCompteSansDecouvert(0, id, client);
			assertEquals(0, compte.getSolde(), 0.001);
			assertEquals(id, compte.getNumeroCompte());
			assertEquals("c.exist", compte.getOwner().getUserId());
			assertTrue(compte instanceof CompteSansDecouvert);
		} catch (TechnicalException | IllegalFormatException e) {
			e.printStackTrace();
			fail("Le compte aurait du être crée.");
		}
	}

	@Test
	public void testCreateCompteSansDecouvertExistingId() {
		Client client = (Client) daoHibernate.getUserById("c.exist");
		String id = "SA1011011011";
		try {
			daoHibernate.createCompteSansDecouvert(0, id, client);
			fail("Le compte n'aurait pas du être créé.");
		} catch (TechnicalException | IllegalFormatException e) {
			assertTrue(e instanceof TechnicalException);
		}
	}

	@Test
	public void testDeleteAccountExist() {
		Compte account = daoHibernate.getAccountById("SA1011011011");
		if (account == null) {
			fail("Problème de récupération du compte.");
		}
		try {
			daoHibernate.deleteAccount(account);
		} catch (TechnicalException e) {
			fail("Le compte aurait du être supprimé.");
		}
		account = daoHibernate.getAccountById("SA1011011011");
		if (account != null) {
			fail("Le compte n'a pas été supprimé.");
		}
	}

	@Test
	public void testGetUserByIdExist() {
		Utilisateur user = daoHibernate.getUserById("c.exist");
		if (user == null) {
			fail("Le compte n'aurait pas du être null.");
		} else if (!"TEST NOM".equals(user.getNom()) && !"TEST PRENOM".equals(user.getPrenom())
				&& !"TEST ADRESSE".equals(user.getAdresse()) && !"TEST PASS".equals(user.getUserPwd())
				&& !"c.exist".equals(user.getUserId())) {
			fail("Les informations de l'utilisateur ne correspondent pas à celle de la BDD.");
		}
	}

	@Test
	public void testGetUserByIdDoesntExist() {
		Utilisateur user = daoHibernate.getUserById("c.doesntexist");
		if (user != null) {
			fail("L'utilisateur n'aurait pas du être renvoyé.");
		}
	}

	@Test
	public void testGetAccountsByUserIdExist() {
		Map<String, Compte> accounts = daoHibernate.getAccountsByClientId("g.descomptes");
		if (accounts == null) {
			fail("Ce client devrait avoir des comptes.");
		} else if (!daoHibernate.getAccountById("SA1011011011").equals(accounts.get("SA1011011011"))
				&& !daoHibernate.getAccountById("AV1011011011").equals(accounts.get("AV1011011011"))) {
			fail("Les mauvais comptes ont été chargés.");
		}
	}

	@Test
	public void testGetClientById() {
		assertTrue(daoHibernate.getUserById("c.exist") instanceof Client);
	}

	@Test
	public void testGetgestionnaireById() {
		assertTrue(daoHibernate.getUserById("admin") instanceof Gestionnaire);
	}

	@Test
	public void testGetAccountsByUserIdDoesntExist() {
		Map<String, Compte> accounts = daoHibernate.getAccountsByClientId("c.doesntexit");
		if (accounts != null) {
			fail("Les comptes de cette utilisateur inexistant n'aurait pas du être renvoyés.");
		}
	}

	@Test
	public void testGetAccountsByUserIdNoAccount() {
		Map<String, Compte> accounts = daoHibernate.getAccountsByClientId("c.exist");
		if (accounts.size() != 0) {
			fail("Ce client ne devrait pas avoir de compte.");
		}
	}

	@Test
	public void testCreateUser() {
		try {
			try {
				daoHibernate.createUser("NOM", "PRENOM", "ADRESSE", true, "c.new1", "PASS", false, "5544554455");
			} catch (IllegalArgumentException e) {
				fail("Il ne devrait pas y avoir d'exception ici");
			} catch (IllegalFormatException e) {
				fail("Il ne devrait pas y avoir d'exception ici");
			}
			Utilisateur user = daoHibernate.getUserById("c.new1");
			assertEquals("NOM", user.getNom());
			assertEquals("PRENOM", user.getPrenom());
			assertEquals("ADRESSE", user.getAdresse());
			assertEquals("c.new1", user.getUserId());
			assertTrue(BCrypt.checkpw("PASS", user.getUserPwd()));
			assertTrue(user.isMale());
		} catch (TechnicalException he) {
			fail("L'utilisateur aurait du être créé.");
		}
	}

	@Test
	public void testCreateUserExistingId() {
		try {
			try {
				daoHibernate.createUser("NOM", "PRENOM", "ADRESSE", true, "c.exist", "PASS", false, "9898989898");
			} catch (IllegalArgumentException e) {
				fail("Il ne devrait pas y avoir d'exception ici");
				e.printStackTrace();
			} catch (IllegalFormatException e) {
				fail("Il ne devrait pas y avoir d'exception ici");

			}
			fail("Une TechnicalException aurait d'être lançée ici.");
		} catch (TechnicalException e) {
			assertTrue(e instanceof TechnicalException);
		}
	}

	@Test
	public void testCreateGestionnaire() {
		try {
			try {
				daoHibernate.createUser("NOM", "PRENOM", "ADRESSE", true, "g.new", "PASS", true, "9898989898");
			} catch (IllegalArgumentException | IllegalFormatException e) {
				fail("Il ne devrait pas y avoir d'exception ici");
				e.printStackTrace();
			}
			Utilisateur gestionnaire = daoHibernate.getUserById("g.new");
			if (!(gestionnaire instanceof Gestionnaire)) {
				fail("Cet utilisateur devrait être un gestionnaire.");
			}
		} catch (TechnicalException e) {
			fail("Il ne devrait pas y avoir d'exception ici.");
		}
	}

	@Test
	public void testCreateClient() {
		try {
			try {
				daoHibernate.createUser("NOM", "PRENOM", "ADRESSE", true, "c.new1", "PASS", false, "9898989898");
			} catch (IllegalArgumentException | IllegalFormatException e) {
				fail("Il ne devrait pas y avoir d'exception ici");
				e.printStackTrace();
			}
			Utilisateur client = daoHibernate.getUserById("c.new1");
			if (!(client instanceof Client)) {
				fail("Cet utilisateur devrait être un client.");
			}
			assertEquals("9898989898", ((Client) client).getNumeroClient());
		} catch (TechnicalException e) {
			fail("Il ne devrait pas y avoir d'exception ici.");
		}
	}

	@Test
	public void testDeleteUser() {
		Utilisateur user = daoHibernate.getUserById("c.exist");
		if (user == null) {
			fail("Problème de récupération de ;'utilisateur.");
		}
		try {
			daoHibernate.deleteUser(user);
		} catch (TechnicalException e) {
			fail("L'utilisateur aurait du être supprimé.");
		}
		user = daoHibernate.getUserById("c.exist");
		if (user != null) {
			fail("L'utilisateur n'a pas été supprimé.");
		}
	}

	@Test
	public void testIsUserAllowedUser() {
		assertTrue(daoHibernate.isUserAllowed("c.exist", "TEST PASS"));
	}

	@Test
	public void testIsUserAllowedWrongPassword() {
		assertEquals(false, daoHibernate.isUserAllowed("c.exist", "WRONG PASS"));
	}

	@Test
	public void testIsUserAllowedUserDoesntExist() {
		assertEquals(false, daoHibernate.isUserAllowed("c.doesntexist", "TEST PASS"));
	}

	@Test
	public void testIsNullPassword() {
		assertEquals(false, daoHibernate.isUserAllowed("c.exist", null));
	}

	@Test
	public void testIsUserAllowedNullLogin() {
		assertEquals(false, daoHibernate.isUserAllowed(null, "TEST PASS"));
	}

	@Test
	public void testIsUserAllowedEmptyPassword() {
		assertEquals(false, daoHibernate.isUserAllowed("c.exist", ""));
	}

	@Test
	public void testIsUserAllowedEmptyTrimmedLogin() {
		assertEquals(false, daoHibernate.isUserAllowed("", "TEST PASS"));
		assertEquals(false, daoHibernate.isUserAllowed(" ", "TEST PASS"));
		assertEquals(false, daoHibernate.isUserAllowed("  ", "TEST PASS"));
		assertEquals(false, daoHibernate.isUserAllowed("   ", "TEST PASS"));
		assertEquals(false, daoHibernate.isUserAllowed("    ", "TEST PASS"));
	}

	@Test
	public void testUpdateAccountSuccess() {
		try {
			Compte compte = daoHibernate.getAccountById("IO1010010001");
			double ancienSolde = compte.getSolde();
			double montant = 1000.0;
			
			compte.crediter(montant);
			daoHibernate.updateAccount(compte);
			
			Compte compteModifie = daoHibernate.getAccountById("IO1010010001");
			assertEquals(ancienSolde + montant, compteModifie.getSolde(), 0.001);
			
			compte.debiter(montant);
			daoHibernate.updateAccount(compte);
		} catch (Exception e) {
			fail("L'update du compte aurait dû réussir : " + e.getMessage());
		}
	}

	@Test
	public void testUpdateAccountWithNullAccount() {
		try {
			daoHibernate.updateAccount(null);
			fail("Une exception aurait dû être levée avec un compte null");
		} catch (IllegalArgumentException e) {
			// Test réussi
		} catch (Exception e) {
			fail("Mauvais type d'exception : attendu IllegalArgumentException, reçu " + e.getClass().getSimpleName());
		}
	}

	@Test
	public void testUpdateUserSuccess() {
		try {
			Utilisateur user = daoHibernate.getUserById("c.exist");
			String oldAddress = user.getAdresse();
			user.setAdresse("Nouvelle adresse");
			
			daoHibernate.updateUser(user);
			
			Utilisateur userModifie = daoHibernate.getUserById("c.exist");
			assertEquals("Nouvelle adresse", userModifie.getAdresse());
			
			// Remettre l'ancienne adresse
			user.setAdresse(oldAddress);
			daoHibernate.updateUser(user);
		} catch (Exception e) {
			fail("L'update de l'utilisateur aurait dû réussir : " + e.getMessage());
		}
	}

	@Test
	public void testUpdateUserWithNullUser() {
		try {
			daoHibernate.updateUser(null);
			fail("Une exception aurait dû être levée avec un utilisateur null");
		} catch (IllegalArgumentException e) {
			// Test réussi
		} catch (Exception e) {
			fail("Mauvais type d'exception : attendu IllegalArgumentException, reçu " + e.getClass().getSimpleName());
		}
	}

	@Test
	public void testIsUserAllowedWithException() {
		try {
			// Forcer une exception en passant un ID invalide
			boolean result = daoHibernate.isUserAllowed("' OR '1'='1", "password");
			assertFalse(result);
		} catch (Exception e) {
			fail("L'exception aurait dû être gérée en interne");
		}
	}

	@Test
	public void testDisconnect() {
		try {
			daoHibernate.disconnect();
			// Vérifier que la déconnexion ne lève pas d'exception
		} catch (Exception e) {
			fail("La déconnexion ne devrait pas lever d'exception");
		}
	}

	@Test
	public void testIsUserAllowedWithNullSession() {
		try {
			// Forcer une exception de session en utilisant une requête malformée
			boolean result = daoHibernate.isUserAllowed("'; DROP TABLE users; --", "password");
			assertFalse(result);
			
			// Vérifier que la session est bien fermée même en cas d'erreur
			result = daoHibernate.isUserAllowed("c.exist", "TEST PASS");
			assertTrue(result);
		} catch (Exception e) {
			fail("L'exception aurait dû être gérée en interne");
		}
	}

	@Test
	public void testUpdateAccountWithNullAccountTechnical() {
		try {
			Compte compte = new CompteSansDecouvert("TEST123", 0, null);
			daoHibernate.updateAccount(compte);
			fail("Une TechnicalException aurait dû être levée");
		} catch (IllegalFormatException e) {
			// Test réussi
		} catch (Exception e) {
			fail("Mauvais type d'exception : attendu TechnicalException, reçu " + e.getClass().getSimpleName());
		}
	}

	@Test
	public void testUpdateUserWithNonExistentUser() {
		try {
			Client client = new Client("TEST", "TEST", "TEST", true, "nonexistent", "pass", "12345");
			daoHibernate.updateUser(client);
			fail("Une TechnicalException aurait dû être levée");
		} catch (IllegalFormatException e) {
			// Test réussi
		} catch (Exception e) {
			fail("Mauvais type d'exception : attendu TechnicalException, reçu " + e.getClass().getSimpleName());
		}
	}

	@Test
	public void testUpdateAccountWithInexistentAccount() {
		try {
			Compte compte = new CompteSansDecouvert("INEXISTANT", 0, null);
			daoHibernate.updateAccount(compte);
			fail("Une exception aurait dû être levée");
		} catch (IllegalFormatException e) {
			// Test réussi
		} catch (Exception e) {
			fail("Mauvais type d'exception : attendu IllegalArgumentException, reçu " + e.getClass().getSimpleName());
		}
	}

	@Test
	public void testUpdateUserWithInexistentUser() {
		try {
			Client client = new Client("TEST", "TEST", "TEST", true, "nonexistent", "pass", "12345");
			daoHibernate.updateUser(client);
			fail("Une exception aurait dû être levée");
		} catch (IllegalFormatException e) {
			// Test réussi
		} catch (Exception e) {
			fail("Mauvais type d'exception : attendu IllegalArgumentException, reçu " + e.getClass().getSimpleName());
		}
	}

	@Test
	public void testSetDecouvertAutoriseValeurZero() throws IllegalFormatException, IllegalOperationException {
		CompteAvecDecouvert compte = new CompteAvecDecouvert("FR0123456789", 100, 100, new Client());
		compte.setDecouverAutorise(0);
		assertEquals(0, compte.getDecouvertAutorise(), 0.001);
	}

	@Test
	public void testGetComptesAvecSoldeNonNulAvecPlusieursComptes() throws IllegalFormatException, IllegalOperationException {
		Client client = new Client("Test", "User", "Address", true, "t.user1", "password", "1234567890");
		client.addAccount(new CompteSansDecouvert("FR1234567890", 100, client));
		client.addAccount(new CompteSansDecouvert("FR1234567891", 0, client));
		client.addAccount(new CompteAvecDecouvert("FR1234567892", 200, 100, client));
		
		Map<String, Compte> comptesNonNuls = client.getComptesAvecSoldeNonNul();
		assertEquals(2, comptesNonNuls.size());
		assertTrue(comptesNonNuls.containsKey("FR1234567890"));
		assertTrue(comptesNonNuls.containsKey("FR1234567892"));
	}

	@Test
	public void testCrediterMontantZero() throws IllegalFormatException {
		CompteSansDecouvert compte = new CompteSansDecouvert("FR0123456789", 100, new Client());
		compte.crediter(0);
		assertEquals(100, compte.getSolde(), 0.001);
	}

	@Test
	public void testCrediterGrandMontant() throws IllegalFormatException {
		CompteSansDecouvert compte = new CompteSansDecouvert("FR0123456789", 100, new Client());
		compte.crediter(1000000);
		assertEquals(1000100, compte.getSolde(), 0.001);
	}
}
