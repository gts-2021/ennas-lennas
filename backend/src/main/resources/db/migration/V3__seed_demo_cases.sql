-- ============================================================
-- ENNAS LENNAS (الناس للناس) - V3 SEED DEMO REALISTIC CASES
-- ============================================================

-- 1. Demandeur(s) réalistes à travers l'Algérie
INSERT INTO requesters (id, first_name, last_name, phone, email, wilaya_id, commune_id, internal_notes, created_at) VALUES
(101, 'Fatima', 'Mansouri', '0551234567', 'f.mansouri@gmail.com', 16, 3, 'Situation confirmée après visite de quartier. Famille honorable et digne.', NOW() - INTERVAL '10 days'),
(102, 'Karim', 'Belkacem', '0662345678', 'k.belkacem@gmail.com', 31, 15, 'Veuve avec 4 enfants à charge sans pension de réversion.', NOW() - INTERVAL '8 days'),
(103, 'Rachid', 'Haddad', '0773456789', 'r.haddad@hotmail.com', 25, 18, 'Certificat médical et dossier MDPH/Handicap vérifiés.', NOW() - INTERVAL '5 days'),
(104, 'Samira', 'Touati', '0554567890', 's.touati@yahoo.fr', 9, 21, 'Père de famille temporairement sans emploi, 3 enfants scolarisés.', NOW() - INTERVAL '7 days'),
(105, 'Mourad', 'Benali', '0665678901', 'm.benali@gmail.com', 19, 24, 'Logement rural précaire nécessitant étanchéité avant hiver.', NOW() - INTERVAL '12 days'),
(106, 'Yamina', 'Khelil', '0776789012', 'y.khelil@gmail.com', 23, 25, 'Ordonnance ophtalmologique certifiée par le CHU d''Annaba.', NOW() - INTERVAL '18 days'),
(107, 'Mustapha', 'Brahimi', '0557890123', 'm.brahimi@yahoo.com', 47, 67, 'Mère au foyer experte en couture, projet d''autonomie.', NOW() - INTERVAL '25 days'),
(108, 'Nassima', 'Ziani', '0668901234', 'n.ziani@gmail.com', 16, 6, 'Prescription pneumologie pour apnée du sommeil sévère.', NOW() - INTERVAL '1 day'),
(109, 'Amar', 'Bouzid', '0779012345', 'a.bouzid@gmail.com', 15, 40, 'Couple de personnes âgées isolées dans le village.', NOW() - INTERVAL '1 day'),
(110, 'Salima', 'Cherif', '0550112233', 's.cherif@gmail.com', 1, 27, 'Enfant nécessitant déplacement médical spécialisé vers Alger.', NOW() - INTERVAL '2 days'),
(111, 'Lyes', 'Meziani', '0661223344', 'l.meziani@yahoo.fr', 16, 1, 'Fratrie de 3 enfants orphelins de père.', NOW() - INTERVAL '3 days'),
(112, 'Houda', 'Saadi', '0559887766', 'h.saadi@gmail.com', 31, 13, 'Diabétique type 1 sans couverture sociale.', NOW() - INTERVAL '4 hours'),
(113, 'Abdelkader', 'Larbi', '0775544332', 'a.larbi@gmail.com', 25, 17, 'Personne âgée grabataire suite à un AVC.', NOW() - INTERVAL '1 hour')
ON CONFLICT (id) DO NOTHING;

-- Mettre à jour la séquence requesters
SELECT setval('requesters_id_seq', (SELECT GREATEST(MAX(id), 150) FROM requesters));

-- 2. Dossiers (Cases) avec statuts diversifiés et contenus rédigés
INSERT INTO cases (
    id, reference, requester_id, category_id, wilaya_id, commune_id,
    raw_title, raw_description, public_title, public_description,
    urgency, status, amount_needed, amount_collected, public_image_url,
    created_at, updated_at, published_at, closed_at
) VALUES
-- Cas 1: PUBLISHED (Santé - Chimiothérapie)
(101, 'ENL-2026-000002', 101, 1, 16, 3,
 'Besoin urgent de 3 boîtes d''Erlotinib 150mg pour chimiothérapie ciblée',
 'Mon mari est suivi au CPMC pour un cancer du poumon. Le médecin prescrit Erlotinib 150mg en urgence, médicament en rupture dans les pharmacies hospitalières.',
 'Traitement oncologique ciblé pour un père de famille',
 'Nous recherchons en urgence 3 boîtes de comprimés d''Erlotinib 150mg (ou équivalent certifié) pour un patient sous protocole de chimiothérapie à l''hôpital de Bab El Oued. Ordonnance vérifiée et authentifiée par l''équipe médicale bénévole.',
 'CRITICAL', 'PUBLISHED', 75000.00, 25000.00,
 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=800&auto=format&fit=crop&q=60',
 NOW() - INTERVAL '10 days', NOW() - INTERVAL '1 day', NOW() - INTERVAL '3 days', NULL),

-- Cas 2: PUBLISHED (Alimentation - Famille orphelins)
(102, 'ENL-2026-000003', 102, 2, 31, 15,
 'Colis alimentaire mensuel pour veuve sans revenus avec 4 enfants à Es Senia',
 'Je suis mère de 4 enfants scolarisés, mon mari est décédé il y a 6 mois. Nous n''avons aucune rentrée d''argent en attendant le dossier de pension.',
 'Soutien alimentaire mensuel pour une famille de 4 orphelins',
 'Prise en charge d''un panier complet de denrées de première nécessité (semoule, huile, lait infantile, légumineuses) pour une maman seule avec 4 enfants en bas âge à Oran.',
 'HIGH', 'PUBLISHED', 20000.00, 15000.00,
 'https://images.unsplash.com/photo-1593113598332-cd288d649433?w=800&auto=format&fit=crop&q=60',
 NOW() - INTERVAL '8 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '5 days', NULL),

-- Cas 3: PUBLISHED (Équipement - Fauteuil roulant)
(103, 'ENL-2026-000004', 103, 6, 25, 18,
 'Fauteuil roulant manuel pliable pour adolescent handicapé moteur',
 'Mon fils de 16 ans a besoin d''un fauteuil pliable pour aller au lycée d''El Khroub. Notre ancien fauteuil est cassé et irréparable.',
 'Fauteuil roulant pour un jeune lycéen à mobilité réduite',
 'Recherche d''un fauteuil roulant pliable en bon état pour permettre à un élève sérieux de 16 ans de poursuivre ses cours sans interruption.',
 'MEDIUM', 'PUBLISHED', 35000.00, 0.00,
 'https://images.unsplash.com/photo-1576765608535-5f04d1e3f289?w=800&auto=format&fit=crop&q=60',
 NOW() - INTERVAL '5 days', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', NULL),

-- Cas 4: IN_PROGRESS (Éducation - Fratrie)
(104, 'ENL-2026-000006', 104, 3, 9, 21,
 'Fournitures et manuels scolaires pour 3 enfants scolarisés',
 'Trois enfants au primaire et collège. Difficultés à réunir les manuels et trousseaux complets.',
 'Trousseau et livres scolaires pour une fratrie de 3 enfants',
 'Accompagnement de trois écoliers pour leur rentrée scolaire (sacs à dos, tabliers, cahiers et livres officiels de collège).',
 'HIGH', 'IN_PROGRESS', 18000.00, 18000.00,
 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=800&auto=format&fit=crop&q=60',
 NOW() - INTERVAL '7 days', NOW() - INTERVAL '1 day', NOW() - INTERVAL '6 days', NULL),

-- Cas 5: IN_PROGRESS (Logement - Toiture)
(105, 'ENL-2026-000007', 105, 5, 19, 24,
 'Réparation d''urgence d''une toiture en tôle endommagée avant l''hiver',
 'Fuites d''eau importantes dans les deux pièces de l''habitation. Risque d''inondation pour la famille.',
 'Isolation et réfection de toiture pour une famille rurale',
 'Aide matérielle (tôles ondulées et isolation) pour sécuriser le toit d''une habitation précaire avant les pluies.',
 'HIGH', 'IN_PROGRESS', 45000.00, 30000.00,
 'https://images.unsplash.com/photo-1513694203232-719a280e022f?w=800&auto=format&fit=crop&q=60',
 NOW() - INTERVAL '12 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '10 days', NULL),

-- Cas 6: COMPLETED (Santé - Lunettes médicales)
(106, 'ENL-2026-000008', 106, 1, 23, 25,
 'Lunettes de vue à forte correction pour fillette malvoyante',
 'Ordonnance pour verres spéciaux avec fort astigmatisme pour ma fille de 7 ans.',
 'Paire de lunettes médicales pour une petite fille',
 'Financement et commande d''une monture adaptée avec verres spécifiques pour une enfant scolarisée.',
 'HIGH', 'COMPLETED', 16000.00, 16000.00,
 'https://images.unsplash.com/photo-1574258495973-f010dfbb5371?w=800&auto=format&fit=crop&q=60',
 NOW() - INTERVAL '18 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '15 days', NOW() - INTERVAL '2 days'),

-- Cas 7: COMPLETED (Équipement - Machine à coudre)
(107, 'ENL-2026-000009', 107, 6, 47, 67,
 'Machine à coudre d''occasion pour autonomisation d''une mère artisane',
 'Besoin d''une machine à coudre pour confectionner des vêtements et nourrir ma famille.',
 'Machine à coudre mécanique pour subsistance familiale',
 'Mise à disposition d''une machine à coudre professionnelle d''occasion pour permettre à une couturière de lancer son activité.',
 'MEDIUM', 'COMPLETED', 25000.00, 25000.00,
 'https://images.unsplash.com/photo-1544816155-12df9643f363?w=800&auto=format&fit=crop&q=60',
 NOW() - INTERVAL '25 days', NOW() - INTERVAL '5 days', NOW() - INTERVAL '20 days', NOW() - INTERVAL '5 days'),

-- Cas 8: UNDER_REVIEW (Santé - Machine PPC)
(108, 'ENL-2026-000010', 108, 1, 16, 6,
 'Appareil respiratoire PPC (Pression Positive Continue) pour apnée sévère',
 'Le médecin spécialiste demande l''installation d''une machine PPC suite à des désaturations nocturnes dangereuses.',
 NULL, NULL,
 'CRITICAL', 'UNDER_REVIEW', 85000.00, 0.00, NULL,
 NOW() - INTERVAL '6 hours', NOW() - INTERVAL '1 hour', NULL, NULL),

-- Cas 9: UNDER_REVIEW (Alimentation - Personnes âgées)
(109, 'ENL-2026-000011', 109, 2, 15, 40,
 'Aide alimentaire pour personnes âgées isolées en zone de montagne',
 'Couple de retraités sans pension suffisante vivant dans un village reculé.',
 NULL, NULL,
 'MEDIUM', 'UNDER_REVIEW', 15000.00, 0.00, NULL,
 NOW() - INTERVAL '12 hours', NOW() - INTERVAL '3 hours', NULL, NULL),

-- Cas 10: NEED_MORE_INFO (Transport - Transfert médical)
(110, 'ENL-2026-000012', 110, 7, 1, 27,
 'Prise en charge trajet médical vers hôpital d''Alger pour enfant dialysé',
 'Déplacement urgent nécessaire pour consultation pédiatrique spécialisée.',
 NULL, NULL,
 'HIGH', 'NEED_MORE_INFO', 30000.00, 0.00, NULL,
 NOW() - INTERVAL '1 day', NOW() - INTERVAL '4 hours', NULL, NULL),

-- Cas 11: APPROVED (Vêtements - Fratrie)
(111, 'ENL-2026-000013', 111, 4, 16, 1,
 'Vêtements d''hiver chauds et manteaux pour fratrie',
 'Recherche de manteaux et chaussures fermées pour 3 enfants.',
 'Manteaux et tenues d''hiver pour 3 enfants',
 'Collecte de vêtements chauds (tailles 6, 8 et 12 ans) en excellent état pour la saison hivernale.',
 'LOW', 'APPROVED', 12000.00, 0.00, NULL,
 NOW() - INTERVAL '2 days', NOW() - INTERVAL '6 hours', NULL, NULL),

-- Cas 12: SUBMITTED (Santé - Diabète)
(112, 'ENL-2026-000014', 112, 1, 31, 13,
 'Recherche bandelettes et lecteur de glycémie pour diabétique de type 1',
 'Besoin mensuel de 2 boîtes de bandelettes réactives et lancettes.',
 NULL, NULL,
 'MEDIUM', 'SUBMITTED', 8000.00, 0.00, NULL,
 NOW() - INTERVAL '4 hours', NOW() - INTERVAL '4 hours', NULL, NULL),

-- Cas 13: SUBMITTED (Autre - Matelas médical)
(113, 'ENL-2026-000015', 113, 8, 25, 17,
 'Matelas orthopédique anti-escarres pour personne alitée',
 'Patient âgé hémiplégique ayant développé des escarres douloureuses.',
 NULL, NULL,
 'LOW', 'SUBMITTED', 22000.00, 0.00, NULL,
 NOW() - INTERVAL '1 hour', NOW() - INTERVAL '1 hour', NULL, NULL)
ON CONFLICT (id) DO NOTHING;

-- Mettre à jour la séquence cases
SELECT setval('cases_id_seq', (SELECT GREATEST(MAX(id), 150) FROM cases));

-- 3. Propositions d'aide citoyennes (Help Offers)
INSERT INTO help_offers (
    id, case_id, first_name, last_name, phone, email,
    offer_type, message, status, admin_notes, created_at, updated_at
) VALUES
(101, 101, 'Mehdi', 'Zerrouki', '0555112233', 'mehdi.zerrouki@gmail.com',
 'Don en nature',
 'J''ai réussi à obtenir 2 boîtes d''Erlotinib 150mg non entamées auprès d''une pharmacie partenaire à Alger. Je peux les remettre directement à votre équipe de bénévoles.',
 'NEW', NULL,
 NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),

(102, 101, 'Yacine', 'Bencherif', '0661998877', 'y.bencherif@hotmail.com',
 'Participation financière',
 'Je souhaite prendre en charge le coût de la 3ème boîte (25 000 DZD). Contactez-moi pour finaliser le paiement directement auprès de l''officine.',
 'CONTACTED', 'Contacté par l''admin le 04/09. Accord pour paiement direct de la boîte.',
 NOW() - INTERVAL '1 day', NOW() - INTERVAL '12 hours'),

(103, 102, 'Khadidja', 'Mebarki', '0770334455', 'khadidja.m@yahoo.fr',
 'Don en nature',
 'Nous avons préparé un pack complet de vivres (semoule 25kg, huile 5L, sucre, conserves, couches). Nous sommes basés à Oran (Akid Lotfi) et pouvons livrer sur place.',
 'ACCEPTED', 'Colis validé avec la famille. Livraison planifiée pour ce samedi.',
 NOW() - INTERVAL '3 days', NOW() - INTERVAL '1 day'),

(104, 104, 'Karim', 'Dahmani', '0550887766', 'kdahmani@gmail.com',
 'Achat direct',
 'Je suis libraire à Blida, je peux offrir l''intégralité des manuels scolaires et 3 cartables équipés.',
 'COMPLETED', 'Fournitures remises en main propre le 03/09. Famille très reconnaissante.',
 NOW() - INTERVAL '5 days', NOW() - INTERVAL '1 day'),

(105, 105, 'Sofiane', 'Belhadj', '0663445566', 's.belhadj@gmail.com',
 'Matériaux & Main d''œuvre',
 'Artisan couvreur à El Eulma, je fournis 10 tôles et assure bénévolement la pose sur un week-end.',
 'ACCEPTED', 'Travaux planifiés dès amélioration des conditions météo.',
 NOW() - INTERVAL '8 days', NOW() - INTERVAL '2 days')
ON CONFLICT (id) DO NOTHING;

-- Mettre à jour la séquence help_offers
SELECT setval('help_offers_id_seq', (SELECT GREATEST(MAX(id), 150) FROM help_offers));
