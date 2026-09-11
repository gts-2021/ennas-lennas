-- ============================================================
-- ENNAS LENNAS (الناس للناس) - V2 SEED REFERENCE DATA
-- ============================================================

-- 1. Les 58 Wilayas d'Algérie
INSERT INTO wilayas (id, code, name_fr, name_ar) VALUES
(1, '01', 'Adrar', 'أدرار'),
(2, '02', 'Chlef', 'الشلف'),
(3, '03', 'Laghouat', 'الأغواط'),
(4, '04', 'Oum El Bouaghi', 'أم البواقي'),
(5, '05', 'Batna', 'باتنة'),
(6, '06', 'Béjaïa', 'بجاية'),
(7, '07', 'Biskra', 'بسكرة'),
(8, '08', 'Béchar', 'بشار'),
(9, '09', 'Blida', 'البليدة'),
(10, '10', 'Bouira', 'البويرة'),
(11, '11', 'Tamanrasset', 'تمنراست'),
(12, '12', 'Tébessa', 'تبسة'),
(13, '13', 'Tlemcen', 'تلمسان'),
(14, '14', 'Tiaret', 'تيارت'),
(15, '15', 'Tizi Ouzou', 'تيزي وزو'),
(16, '16', 'Alger', 'الجزائر'),
(17, '17', 'Djelfa', 'الجلفة'),
(18, '18', 'Jijel', 'جيجل'),
(19, '19', 'Sétif', 'سطيف'),
(20, '20', 'Saïda', 'سعيدة'),
(21, '21', 'Skikda', 'سكيكدة'),
(22, '22', 'Sidi Bel Abbès', 'سيدي بلعباس'),
(23, '23', 'Annaba', 'عنابة'),
(24, '24', 'Guelma', 'قالمة'),
(25, '25', 'Constantine', 'قسنطينة'),
(26, '26', 'Médéa', 'المدية'),
(27, '27', 'Mostaganem', 'مستغانم'),
(28, '28', 'M''Sila', 'المسيلة'),
(29, '29', 'Mascara', 'معسكر'),
(30, '30', 'Ouargla', 'ورقلة'),
(31, '31', 'Oran', 'وهران'),
(32, '32', 'El Bayadh', 'البيض'),
(33, '33', 'Illizi', 'إليزي'),
(34, '34', 'Bordj Bou Arreridj', 'برج بوعريريج'),
(35, '35', 'Boumerdès', 'بومرداس'),
(36, '36', 'El Tarf', 'الطارف'),
(37, '37', 'Tindouf', 'تندوف'),
(38, '38', 'Tissemsilt', 'تيسمسيلت'),
(39, '39', 'El Oued', 'الوادي'),
(40, '40', 'Khenchela', 'خنشلة'),
(41, '41', 'Souk Ahras', 'سوق أهراس'),
(42, '42', 'Tipaza', 'تيبازة'),
(43, '43', 'Mila', 'ميلة'),
(44, '44', 'Aïn Defla', 'عين الدفلى'),
(45, '45', 'Naâma', 'النعامة'),
(46, '46', 'Aïn Témouchent', 'عين تموشنت'),
(47, '47', 'Ghardaïa', 'غرداية'),
(48, '48', 'Relizane', 'غليزان'),
(49, '49', 'El M''Ghair', 'المغير'),
(50, '50', 'El Meniaa', 'المنيعة'),
(51, '51', 'Ouled Djellal', 'أولاد جلال'),
(52, '52', 'Bordj Baji Mokhtar', 'برج باجي مختار'),
(53, '53', 'Béni Abbès', 'بني عباس'),
(54, '54', 'Timimoun', 'تيميمون'),
(55, '55', 'Touggourt', 'تقرت'),
(56, '56', 'Djanet', 'جانت'),
(57, '57', 'In Salah', 'عين صالح'),
(58, '58', 'In Guezzam', 'عين قزام');

SELECT setval('wilayas_id_seq', (SELECT MAX(id) FROM wilayas));

-- 2. Communes clés (représentatives pour le MVP)
-- Alger (16)
INSERT INTO communes (wilaya_id, name_fr, name_ar, postal_code) VALUES
(16, 'Alger Centre', 'الجزائر الوسطى', '16000'),
(16, 'Sidi M''Hamed', 'سيدي امحمد', '16001'),
(16, 'Bab El Oued', 'باب الواد', '16008'),
(16, 'Hussein Dey', 'حسين داي', '16005'),
(16, 'El Harrach', 'الحراش', '16200'),
(16, 'Kouba', 'القبة', '16050'),
(16, 'Bir Mourad Raïs', 'بئر مراد رايس', '16015'),
(16, 'Hydra', 'حيدرة', '16035'),
(16, 'Chéraga', 'الشراقة', '16014'),
(16, 'Zéralda', 'زرالدة', '16063'),
(16, 'Rouïba', 'الرويبة', '16017'),
(16, 'Dar El Beïda', 'الدار البيضاء', '16033');

-- Oran (31)
INSERT INTO communes (wilaya_id, name_fr, name_ar, postal_code) VALUES
(31, 'Oran', 'وهران', '31000'),
(31, 'Bir El Djir', 'بئر الجير', '31011'),
(31, 'Es Senia', 'السانية', '31008'),
(31, 'Arzew', 'أرزيو', '31200');

-- Constantine (25)
INSERT INTO communes (wilaya_id, name_fr, name_ar, postal_code) VALUES
(25, 'Constantine', 'قسنطينة', '25000'),
(25, 'El Khroub', 'الخروب', '25100'),
(25, 'Aïn Smara', 'عين سمارة', '25140');

-- Blida (09)
INSERT INTO communes (wilaya_id, name_fr, name_ar, postal_code) VALUES
(9, 'Blida', 'البليدة', '09000'),
(9, 'Boufarik', 'بوفاريك', '09400'),
(9, 'Ouled Yaïch', 'أولاد يعيش', '09014');

-- Sétif (19)
INSERT INTO communes (wilaya_id, name_fr, name_ar, postal_code) VALUES
(19, 'Sétif', 'سطيف', '19000'),
(19, 'El Eulma', 'العلمة', '19600');

-- Annaba (23)
INSERT INTO communes (wilaya_id, name_fr, name_ar, postal_code) VALUES
(23, 'Annaba', 'عنابة', '23000'),
(23, 'El Bouni', 'البوني', '23005');

-- Communes chefs-lieux pour les autres wilayas
INSERT INTO communes (wilaya_id, name_fr, name_ar, postal_code) VALUES
(1, 'Adrar', 'أدرار', '01000'),
(2, 'Chlef', 'الشلف', '02000'),
(3, 'Laghouat', 'الأغواط', '03000'),
(4, 'Oum El Bouaghi', 'أم البواقي', '04000'),
(5, 'Batna', 'باتنة', '05000'),
(6, 'Béjaïa', 'بجاية', '06000'),
(7, 'Biskra', 'بسكرة', '07000'),
(8, 'Béchar', 'بشار', '08000'),
(10, 'Bouira', 'البويرة', '10000'),
(11, 'Tamanrasset', 'تمنراست', '11000'),
(12, 'Tébessa', 'تبسة', '12000'),
(13, 'Tlemcen', 'تلمسان', '13000'),
(14, 'Tiaret', 'تيارت', '14000'),
(15, 'Tizi Ouzou', 'تيزي وزو', '15000'),
(17, 'Djelfa', 'الجلفة', '17000'),
(18, 'Jijel', 'جيجل', '18000'),
(20, 'Saïda', 'سعيدة', '20000'),
(21, 'Skikda', 'سكيكدة', '21000'),
(22, 'Sidi Bel Abbès', 'سيدي بلعباس', '22000'),
(24, 'Guelma', 'قالمة', '24000'),
(26, 'Médéa', 'المدية', '26000'),
(27, 'Mostaganem', 'مستغانم', '27000'),
(28, 'M''Sila', 'المسيلة', '28000'),
(29, 'Mascara', 'معسكر', '29000'),
(30, 'Ouargla', 'ورقلة', '30000'),
(32, 'El Bayadh', 'البيض', '32000'),
(33, 'Illizi', 'إليزي', '33000'),
(34, 'Bordj Bou Arreridj', 'برج بوعريريج', '34000'),
(35, 'Boumerdès', 'بومرداس', '35000'),
(36, 'El Tarf', 'الطارف', '36000'),
(37, 'Tindouf', 'تندوف', '37000'),
(38, 'Tissemsilt', 'تيسمسيلت', '38000'),
(39, 'El Oued', 'الوادي', '39000'),
(40, 'Khenchela', 'خنشلة', '40000'),
(41, 'Souk Ahras', 'سوق أهراس', '41000'),
(42, 'Tipaza', 'تيبازة', '42000'),
(43, 'Mila', 'ميلة', '43000'),
(44, 'Aïn Defla', 'عين الدفلى', '44000'),
(45, 'Naâma', 'النعامة', '45000'),
(46, 'Aïn Témouchent', 'عين تموشنت', '46000'),
(47, 'Ghardaïa', 'غرداية', '47000'),
(48, 'Relizane', 'غليزان', '48000'),
(49, 'El M''Ghair', 'المغير', '49000'),
(50, 'El Meniaa', 'المنيعة', '50000'),
(51, 'Ouled Djellal', 'أولاد جلال', '51000'),
(52, 'Bordj Baji Mokhtar', 'برج باجي مختار', '52000'),
(53, 'Béni Abbès', 'بني عباس', '53000'),
(54, 'Timimoun', 'تيميمون', '54000'),
(55, 'Touggourt', 'تقرت', '55000'),
(56, 'Djanet', 'جانت', '56000'),
(57, 'In Salah', 'عين صالح', '57000'),
(58, 'In Guezzam', 'عين قزام', '58000');

-- 3. Catégories recommandées par le cahier des charges
INSERT INTO categories (id, code, name_fr, name_ar, icon, active, display_order) VALUES
(1, 'HEALTH', 'Médicaments & Santé', 'أدوية وصحة', 'HeartPulse', TRUE, 1),
(2, 'FOOD', 'Alimentation & Vivres', 'إطعام ومواد غذائية', 'Utensils', TRUE, 2),
(3, 'EDUCATION', 'Scolarité & Fournitures', 'تعليم ومستلزمات دراسية', 'GraduationCap', TRUE, 3),
(4, 'CLOTHING', 'Vêtements & Linge', 'ملابس وكسوة', 'Shirt', TRUE, 4),
(5, 'HOUSING', 'Logement & Travaux', 'سكن وترميم', 'Home', TRUE, 5),
(6, 'EQUIPMENT', 'Équipement & Électroménager', 'أجهزة وتجهيزات', 'Refrigerator', TRUE, 6),
(7, 'TRANSPORT', 'Transport & Déplacement', 'نقل ومواصلات', 'Car', TRUE, 7),
(8, 'OTHER', 'Autre besoin social', 'احتياج اجتماعي آخر', 'HelpCircle', TRUE, 8);

SELECT setval('categories_id_seq', (SELECT MAX(id) FROM categories));

-- 4. Compte Administrateur Initial
-- Email: admin@ennaslennas.org
-- Mot de passe: Admin123!
INSERT INTO admin_users (email, password_hash, full_name, role, active) VALUES
('admin@ennaslennas.org', '$2a$10$G0uKjYpsd1AGMwHZo9NRCutKqjH6W.M4D20BobE4.B3W3qmnk6KJC', 'Administrateur Principal', 'ROLE_ADMIN', TRUE);
