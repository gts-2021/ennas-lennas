# الناس للناس — Ennas Lennas (MVP)

> **Plateforme solidaire de mise en relation directe entre personnes dans le besoin et bienfaiteurs en Algérie.**

---

## 1. Principes & Vision Produit

- **Non-intermédiation financière** : La plateforme ne gère aucun flux d'argent, aucune cagnotte et aucun portefeuille électronique. Les aides sont apportées directement par les bienfaiteurs (achat direct en pharmacie, fourniture d'équipement, don en nature, soutien logistique).
- **Privacy By Design absolue** :
  - **Univers Privé** : Coordonnées du demandeur (nom, prénom, téléphone, email, ordonnances, justificatifs médicaux, notes internes) accessibles uniquement aux administrateurs autorisés dans le back-office.
  - **Univers Public** : Seules les données validées et anonymisées par l'équipe (référence, titre soigné, description anonyme, wilaya, catégorie, urgence, estimation) sont visibles par les visiteurs.

---

## 2. Stack Technique

- **Backend** : Java 21, Spring Boot 3.4.2, Spring Security 6 (JWT + BCrypt), Spring Data JPA, Hibernate, Bean Validation, Flyway Migrations, MinIO SDK.
- **Frontend** : Vue 3 (Composition API), Vite, Pinia, Vue Router 4, Lucide Icons (`@lucide/vue`), Axios, CSS Vanilla épuré & Design System Mobile-First (Bilingue FR/AR avec support RTL/LTR).
- **Base de données** : PostgreSQL 17 (Migrations versionnées Flyway avec 58 Wilayas algériennes, communes et catégories initiales).
- **Stockage de fichiers** : MinIO (S3-compatible) avec clés générées côté serveur et accès sécurisé par URLs éphémères signées.
- **Conteneurisation** : Docker, Multi-stage builds & Docker Compose.

---

## 3. Identifiants par Défaut (Environnement de Développement)

| Rôle | Email / Utilisateur | Mot de passe |
| :--- | :--- | :--- |
| **Administrateur Principal** | `admin@ennaslennas.org` | `Admin123!` |
| **PostgreSQL (Local)** | `postgres` (Port 5432) | `root` |
| **MinIO Console** | `http://localhost:9001` (Port API: 9008) | User: `n62JqnPRXJRV2JAHDCg8` / Pass: `XgySJpBcz2wU6NSlF3cvpXK87CUDFj4Q6ZOcrysy` |

---

## 4. Démarrage Rapide

### Option A : En Local (Développement)

#### 1. Backend (Spring Boot)
```bash
cd backend
mvn clean spring-boot:run
```
Le backend démarre sur `http://localhost:8080`.

#### 2. Frontend (Vue 3 / Vite)
```bash
cd frontend
npm run dev
```
Le frontend démarre sur `http://localhost:5173` (avec proxy automatique des requêtes `/api` vers `8080`).

---

### Option B : Avec Docker Compose (Production)

```bash
docker-compose up -d --build
```
- Frontend Web : `http://localhost` (Port 80)
- Backend API : `http://localhost:8080`
- MinIO Console : `http://localhost:9001`

---

## 5. Machine d'États du Workflow (Enforced Backend)

```
SUBMITTED
   │
   ▼
UNDER_REVIEW ◄───┐
   │             │
   ├─────────────┴── NEED_MORE_INFO
   ├───────────────► REJECTED / CANCELLED
   ▼
APPROVED
   │
   ▼
PUBLISHED ◄────────┐ (si désistement)
   │               │
   ▼               │
IN_PROGRESS ───────┘
   │
   ▼
COMPLETED
   │
   ▼
 CLOSED
```

---

## 6. Endpoints REST (`/api/v1`)

### Publics
- `GET /api/v1/categories` : Liste des catégories de besoin actives.
- `GET /api/v1/locations/wilayas` : Liste des 58 wilayas algériennes.
- `GET /api/v1/locations/wilayas/{id}/communes` : Communes d'une wilaya.
- `GET /api/v1/cases` : Catalogue paginé des cas publiés avec filtres (`categoryId`, `wilayaId`, `urgency`).
- `GET /api/v1/cases/{reference}` : Fiche publique anonymisée d'un cas.
- `GET /api/v1/cases/urgent` : Cas prioritaires pour la page d'accueil.
- `GET /api/v1/cases/latest` : Derniers cas publiés.
- `POST /api/v1/help-requests` : Soumission multipart d'une demande avec pièces jointes.
- `POST /api/v1/cases/{reference}/help-offers` : Soumission d'une proposition citoyenne (« Je peux aider »).
- `POST /api/v1/auth/login` : Authentification modérateur / administrateur (JWT).

### Administration (Protégés `ROLE_ADMIN` / `ROLE_MODERATOR`)
- `GET /api/v1/admin/dashboard/stats` : Indicateurs KPI du tableau de bord.
- `GET /api/v1/admin/cases` : Liste filtrable des dossiers.
- `GET /api/v1/admin/cases/{id}` : Dossier complet avec pièces privées et URLs éphémères de téléchargement.
- `PATCH /api/v1/admin/cases/{id}` : Édition des données publiques et notes internes.
- `POST /api/v1/admin/cases/{id}/review` : Prise en charge (`UNDER_REVIEW`).
- `POST /api/v1/admin/cases/{id}/request-info` : Demande de compléments (`NEED_MORE_INFO`).
- `POST /api/v1/admin/cases/{id}/approve` : Validation (`APPROVED`).
- `POST /api/v1/admin/cases/{id}/publish` : Publication publique (`PUBLISHED`).
- `POST /api/v1/admin/cases/{id}/in-progress` : Mise en relation (`IN_PROGRESS`).
- `POST /api/v1/admin/cases/{id}/completed` : Résolution satisfaite (`COMPLETED`).
- `POST /api/v1/admin/cases/{id}/close` : Clôture définitive (`CLOSED`).
- `POST /api/v1/admin/cases/{id}/reject` : Rejet motivé (`REJECTED`).
- `GET /api/v1/admin/help-offers` : Suivi des offres d'aide reçues.
- `PATCH /api/v1/admin/help-offers/{id}/status` : Traitement d'une offre d'aide.

---

## 7. Exécution des Tests Automatisés

```bash
cd backend
mvn test
```
La suite de tests vérifie :
- Les référentiels géographiques et catégories.
- La soumission multipart et l'enregistrement MinIO / PostgreSQL.
- La génération atomique de référence (`ENL-YYYY-NNNNNN`).
- La validation stricte des formats de téléphone algériens.
- L'étanchéité absolue des DTOs publics (vérification par réflexion unitaire qu'aucune donnée privée n'est exposée).
- L'authentification JWT et l'interdiction d'accès sans privilège (403).
- L'ensemble du cycle de vie du workflow et le rejet des transitions illégales.
