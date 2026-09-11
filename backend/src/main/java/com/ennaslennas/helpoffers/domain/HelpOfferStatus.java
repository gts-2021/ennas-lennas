package com.ennaslennas.helpoffers.domain;

public enum HelpOfferStatus {
    NEW,        // Nouvelle proposition reçue
    CONTACTED,  // L'équipe a contacté l'aidant
    ACCEPTED,   // Proposition validée et convenue
    COMPLETED,  // Aide effectivement fournie
    CANCELLED   // Annulée / désistement
}
