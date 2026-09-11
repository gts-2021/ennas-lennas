-- ============================================================
-- ENNAS LENNAS (الناس للناس) - V1 SCHEMA INITIALIZATION
-- ============================================================

-- 1. Référentiels géographiques
CREATE TABLE wilayas (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name_fr VARCHAR(100) NOT NULL,
    name_ar VARCHAR(100) NOT NULL
);

CREATE TABLE communes (
    id BIGSERIAL PRIMARY KEY,
    wilaya_id BIGINT NOT NULL REFERENCES wilayas(id) ON DELETE RESTRICT,
    name_fr VARCHAR(100) NOT NULL,
    name_ar VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10)
);

CREATE INDEX idx_communes_wilaya_id ON communes(wilaya_id);

-- 2. Catégories de besoins
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name_fr VARCHAR(100) NOT NULL,
    name_ar VARCHAR(100) NOT NULL,
    icon VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0
);

-- 3. Demandeurs (Données privées confidentielles)
CREATE TABLE requesters (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(150),
    wilaya_id BIGINT NOT NULL REFERENCES wilayas(id),
    commune_id BIGINT NOT NULL REFERENCES communes(id),
    internal_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_requesters_phone ON requesters(phone);

-- 4. Séquence de référence publique (ENL-YYYY-000001)
CREATE SEQUENCE case_reference_seq START WITH 1 INCREMENT BY 1;

-- 5. Dossiers / Cas
CREATE TABLE cases (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(30) NOT NULL UNIQUE,
    requester_id BIGINT NOT NULL REFERENCES requesters(id) ON DELETE RESTRICT,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    wilaya_id BIGINT NOT NULL REFERENCES wilayas(id),
    commune_id BIGINT NOT NULL REFERENCES communes(id),
    
    -- Données brutes (demandeur)
    raw_title VARCHAR(255) NOT NULL,
    raw_description TEXT NOT NULL,
    
    -- Données publiques (modérées & validées)
    public_title VARCHAR(255),
    public_description TEXT,
    public_image_url VARCHAR(500),
    
    urgency VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    amount_needed NUMERIC(12, 2),
    amount_collected NUMERIC(12, 2) DEFAULT 0,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_cases_reference ON cases(reference);
CREATE INDEX idx_cases_status ON cases(status);
CREATE INDEX idx_cases_public_filter ON cases(category_id, wilaya_id, urgency, status);
CREATE INDEX idx_cases_published_date ON cases(published_at DESC) WHERE status = 'PUBLISHED';

-- 6. Documents justificatifs confidentiels
CREATE TABLE case_documents (
    id BIGSERIAL PRIMARY KEY,
    case_id BIGINT NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    original_filename VARCHAR(255) NOT NULL,
    storage_key VARCHAR(255) NOT NULL UNIQUE,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    document_type VARCHAR(50),
    is_verified BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_case_documents_case_id ON case_documents(case_id);

-- 7. Propositions d'aide
CREATE TABLE help_offers (
    id BIGSERIAL PRIMARY KEY,
    case_id BIGINT NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(150),
    offer_type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',
    admin_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_help_offers_case_id ON help_offers(case_id);
CREATE INDEX idx_help_offers_status ON help_offers(status);

-- 8. Utilisateurs administrateurs
CREATE TABLE admin_users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'ROLE_ADMIN',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_login_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_admin_users_email ON admin_users(email);

-- 9. Journal d'audit
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    admin_user_id BIGINT REFERENCES admin_users(id),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
