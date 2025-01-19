<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>




<html lang="fr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Liste des comptes de la banque</title>
<link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
</head>
<body>
	<div class="btnLogout">
		<s:form name="myForm" action="logout" method="POST">
			<s:submit name="Retour" value="Logout" />
		</s:form>
	</div>
	<s:if test="aDecouvert">
		<h1>Liste des comptes à découvert de la banque</h1>
	</s:if>
	<s:else>
		<h1>Liste des comptes de la banque</h1>
	</s:else>
	<s:form name="myForm" action="retourTableauDeBordManager" method="POST">
		<s:submit name="Retour" value="Retour" />
	</s:form>

	<s:if test="aDecouvert">
		<p>Voici les comptes a découvert de la banque :</p>
	</s:if>
	<s:else>
		<p>Voici l'état des comptes de la banque :</p>
	</s:else>
	<table>
		<thead>
			<tr>
				<th>Client</th>
				<th>Numéro de compte</th>
				<th>Type de compte</th>
				<th>Solde</th>
				<th>Actions sur le compte</th>
				<th>Actions sur le client</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="allClients">
				<s:if test="(value.possedeComptesADecouvert() || !aDecouvert)">
					<!-- Ligne client -->
					<tr>
						<td colspan="4"><b>Client :</b> <s:property value="value.prenom" /> 
							<s:property value="value.nom" /> (n°<s:property value="value.numeroClient" />)
						</td>
						<s:if test="(!aDecouvert)">
							<td colspan="2">
								<!-- Bouton pour ajouter un compte -->
								<s:url action="urlAddAccount" var="addAccount">
									<s:param name="client">
										<s:property value="value.userId" />
									</s:param>
								</s:url>
								<s:a href="%{addAccount}">
									<img src="https://cdn4.iconfinder.com/data/icons/e-commerce-icon-set/48/More-128.png" 
										 style="width: 20px; height: 20px" 
										 alt="Créer un compte" 
										 title="Créer un compte pour ce client" />
								</s:a>
	
								<!-- Bouton pour supprimer un client -->
								<s:url action="deleteUser" var="deleteUser">
									<s:param name="client">
										<s:property value="value.userId" />
									</s:param>
								</s:url>
								<s:a href="%{deleteUser}" onclick="return confirm('Voulez-vous vraiment supprimer cet utilisateur ?')">
									<img src="https://cdn2.iconfinder.com/data/icons/windows-8-metro-style/512/trash-.png" 
										 style="width: 20px; height: 20px" 
										 alt="Supprimer ce client" 
										 title="Supprimer ce client et tous ses comptes associés" />
								</s:a>
							</td>
						</s:if>
					</tr>
					<!-- Lignes des comptes -->
					<s:iterator value="value.accounts">
						<s:if test="(value.solde < 0 || !aDecouvert)">
							<tr>
								<td></td> <!-- Colonne vide pour aligner avec "Client" -->
								<td><s:property value="key" /></td>
								<td>
									<s:if test="%{value.className == 'CompteAvecDecouvert'}">
										Découvert possible
									</s:if>
									<s:else>
										Simple
									</s:else>
								</td>
								<td class="<s:if test='%{value.solde < 0}'>soldeNegatif</s:if>">
									<s:property value="value.solde" />
								</td>
								<td>
									<!-- Bouton pour éditer un compte -->
									<s:url action="editAccount" var="editAccount">
										<s:param name="compte">
											<s:property value="value.numeroCompte" />
										</s:param>
									</s:url>
									<s:a href="%{editAccount}">
										<img src="http://freeflaticons.com/wp-content/uploads/2014/10/write-copy-14138051958gn4k.png" 
											 style="width: 20px; height: 20px" 
											 alt="Editer ce compte" 
											 title="Editer ce compte" />
									</s:a>
	
									<!-- Bouton pour supprimer un compte -->
									<s:url action="deleteAccount" var="deleteAccount">
										<s:param name="compte">
											<s:property value="value.numeroCompte" />
										</s:param>
										<s:param name="client">
											<s:property value="value.owner.userId" />
										</s:param>
									</s:url>
									<s:a href="%{deleteAccount}" onclick="return confirm('Voulez-vous vraiment supprimer ce compte ?')">
										<img src="https://cdn2.iconfinder.com/data/icons/windows-8-metro-style/512/trash-.png" 
											 style="width: 20px; height: 20px" 
											 alt="Supprimer ce compte" 
											 title="Supprimer ce compte" />
									</s:a>
								</td>
								<td></td> <!-- Colonne vide pour aligner avec les actions clients -->
							</tr>
						</s:if>
					</s:iterator>
				</s:if>
			</s:iterator>
		</tbody>
	</table>	
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>