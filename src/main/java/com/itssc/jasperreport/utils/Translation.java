package com.itssc.jasperreport.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum Translation {
    HELLO("Hello", "Bonjour"),
    CUSTOMERNAME("Customer Name", "Nom du client"),
    CUSTOMERID("Customer ID", "ID du client"),
    DATEOFBIRTH("Date of Birth", "Date de naissance"),
    MODIFIEDBY("Modified By", "Modifié par"),
    DATETIME("Date & Time", "Date et heure"),
    DATE("Date", "Date"),
    CONTRACTNAME("Contract Name", "Nom du contrat"),
    CONTRACTID("Contract ID", "ID du contrat"),
    REASON("Reason", "Raison"),
    CUSTOMERTYPE("Customer Type", "Type de client"),
    SMSTYPE("SMS Type", "Type de SMS"),
    ACCOUNTNUMBER("Account Number", "Numéro de compte"),
    ORDERDATE("Order Date", "Date de commande"),
    ORDERSTATUS("Order Status", "Statut de la commande"),
    FIRSTCHEQUEBOOKNUMBER("1st Cheque Book Number", "Numéro du 1er chéquier"),
    DATESTOPPED("Date Stopped", "Date d'arrêt"),
    INITIATORACCOUNTNUMBER("Initiator Account Number", "Numéro de compte de l'initiateur"),
    RECEIVERNAME("Receiver Name", "Nom du destinataire"),
    RECEIVERACCOUNTNUMBER("Receiver Account Number", "Numéro de compte du destinataire"),
    AMOUNT("Amount", "Montant"),
    TRANSFERTYPE("Transfer Type", "Type de transfert"),
    STATUS("Status", "Statut"),
    CARDNUMBER("Card Number", "Numéro de carte"),
    ACTION("Action", "Action"),
    PLATFORM("Platform", "Plateforme"),
    MODIFICATION("Modification", "Modification"),
    CONTRACTMODIFICATION("Contract Modification", "Modification de Contrat"),
    SUSPENDEDCONTRACT("Suspended Contract", "Contrat Suspendu"),
    SUSPENDEDUSER("Suspended User", "Utilisateur Suspendu"),
    SMS("SMS", "SMS"),
    CHEQUES("Cheques", "Chèques"),
    BLOCKEDCHEQUE("Blocked Cheque", "Chèque bloqué"),
    TRANSFER("Transfer", "Transfert"),
    REALTIMECONNECTION("Real Time Connection", "Connexion en Temps Réel"),
    CARDACTIONS("Card Actions", "Actions de Carte"),
    USERNAME("USER NAME", "Nom d'utilisateur"),
    TRANSACTIONDATE("Transaction Date","Date de Transaction"),
    SENDER("Sender","Expéditeur"),
    BANK("Bank","Banque"),
    BENEFICIARY("Beneficiary","Bénéficiaire"),
    DESCRIPTION("Description","Description"),
    ACCOUNT("Account","Compte"),
    TRANSACTIONREFERENCE("Transaction Reference","Référence de Transaction"),
    TRANSACTIONAMOUNT("Transaction Amount","Montant de la Transaction"),
    CARDNAME("CARD NAME", "Nom de la carte"),
    TRANSACTIONRECEIPTTITLE("Transaction Receipt","Rede Transaction"),
    PILGRIMNAME("Pilgrim's Name","Nom du pèlerin"),
    HAJJRECEIPTTITLE("Hajj Transaction Receipt","Hadj Rede Transaction"),
    OUSTANDINGBALANCE("Outstanding Balance","Solde Impayé"),
    FROMDATE("fromdate","date de début"),
    GENERATEDBY("generatedby","généré par"),
    GENERATEDON("generatedon","date de génération"),
    BANKNAME("bankname","nom de la banque"),
    TODATE("todate","date d'arrivée"),
    USERID("userid","identifiant utilisateur"),
    ADDRESS("address","adresse"),
    TELEPHONE("telephone","téléphone"),
    EMAIL("email","email"),
    TITLE("title","titre"),
    BRANCH("branch","branche"),
    CONTRACTREPORTS("CONTRACT REPORTS","RAPPORTS DE CONTRAT"),
    BRANCH("branch","branche"),
    CLOSINGBALANCE("Closing Balance", "Solde de clôture"),
    STARTDATE("Start Date", "Date de début"),
    ENDDATE("End Date", "Date de fin"),
    FILEFORMAT("File Format", "Format de fichier"),
    ACCOUNTNAME("Account Name:","Nom du compte:"),
    SUMDEBIT("Sum of Debits:","Somme des débits:"),
    SUMCREDIT("Sum of Credits:","Somme des crédit:"),
    OPENINGBALANCE("Opening Balance:","Solde initial:");




    private final Map<Locale, String> translations;

    Translation(String... translations) {
        this.translations = new HashMap<>();
        this.translations.put(Locale.ENGLISH, translations[0]);
        this.translations.put(Locale.FRENCH, translations[1]);
    }

    public String getTranslation(Locale locale) {
        return translations.getOrDefault(locale, translations.get(Locale.ENGLISH));
    }
}
