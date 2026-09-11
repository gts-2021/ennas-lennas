package com.ennaslennas.cases.domain;

public enum CaseStatus {
    SUBMITTED,      // Demande reçue
    UNDER_REVIEW,   // En cours d'instruction par l'équipe
    NEED_MORE_INFO, // Complément demandé au demandeur
    APPROVED,       // Demande validée par l'équipe
    PUBLISHED,      // Visible publiquement
    IN_PROGRESS,    // Prise en charge / aide en cours
    COMPLETED,      // Besoin comblé avec succès
    CLOSED,         // Dossier archivé et clos
    REJECTED,       // Demande rejetée
    CANCELLED       // Demande annulée
}
