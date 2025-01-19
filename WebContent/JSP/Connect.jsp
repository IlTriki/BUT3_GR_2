<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html lang="fr">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Tableau de bord</title>
<link rel="stylesheet" href="/_00_ASBank2023/style/style.css" />
</head>
<body>
	<div class="btnLogout">
		<s:form name="myForm" action="logout" method="POST">
			<s:submit name="Retour" value="Logout" />
		</s:form>
	</div>
	<h1>Tableau de bord</h1>
	<p>
		Bienvenue <b><s:property value="connectedUser.prenom" /> <s:property
				value="connectedUser.nom" /></b> !
	</p>
	<p>Voici l'état de vos comptes :</p>
	<table>
		<thead>
			<tr>
				<th id="numCompte"><b>Numéro de compte</b></th>
				<th id="typeCompte"><b>Type de compte</b></th>
				<th id="soldeActuel"><b>Solde actuel</b></th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="accounts">
				<tr>
					<td>
						<s:url action="urlDetail" var="urlDetail">
							<s:param name="compte"><s:property value="key" /></s:param>
						</s:url>
						<s:a href="%{urlDetail}">
							<s:property value="key" />
						</s:a>
					</td>
					<td>
						<s:if test="%{value.className == \"CompteAvecDecouvert\"}">
							Découvert possible
						</s:if>
						<s:else>
							Simple
						</s:else>
					</td>
					<td class="<s:if test='%{value.solde < 0}'>soldeNegatif</s:if>">
						<s:property value="value.solde" />
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>