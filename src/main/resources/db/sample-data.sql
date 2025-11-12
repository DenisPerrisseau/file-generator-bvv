-- Scripts SQL pour insérer des données de test
-- À exécuter manuellement après le démarrage de l'application

-- Connexion à la base de données
-- psql -U postgres -d filegeneration_db_dev

-- ============================================
-- INSERT : Template CLIENTS
-- ============================================

INSERT INTO templates (name, prefix, type, version, nomenclature, file_path, created_at, updated_at)
VALUES (
  'CLIENTS_TEMPLATE',
  'CLIENTS',
  'JSON',
  1,
  'CLIENTS_JSON_20251112_v1.json',
  'files/templates/CLIENTS_JSON_20251112_v1.json',
  NOW(),
  NOW()
);

-- Récupérer l'ID du template créé
SELECT SETVAL('templates_id_seq', (SELECT MAX(id) FROM templates));

-- ============================================
-- INSERT : Champs du template CLIENTS
-- ============================================

INSERT INTO template_fields (template_id, field_name, field_type, required, min_length, max_length, min_value, max_value, date_format, field_position, created_at)
VALUES
  (1, 'clientId', 'INTEGER', true, NULL, NULL, 1, 999999, NULL, 1, NOW()),
  (1, 'firstName', 'STRING', true, 1, 100, NULL, NULL, NULL, 2, NOW()),
  (1, 'lastName', 'STRING', true, 1, 100, NULL, NULL, NULL, 3, NOW()),
  (1, 'email', 'STRING', true, 5, 255, NULL, NULL, NULL, 4, NOW()),
  (1, 'phone', 'STRING', false, 10, 20, NULL, NULL, NULL, 5, NOW()),
  (1, 'createdDate', 'DATE', true, NULL, NULL, NULL, NULL, 'yyyy-MM-dd', 6, NOW()),
  (1, 'isActive', 'BOOLEAN', true, NULL, NULL, NULL, NULL, NULL, 7, NOW()),
  (1, 'accountBalance', 'DECIMAL', false, NULL, NULL, 0, 999999.99, NULL, 8, NOW());

-- ============================================
-- INSERT : Template INVOICES
-- ============================================

INSERT INTO templates (name, prefix, type, version, nomenclature, file_path, created_at, updated_at)
VALUES (
  'INVOICES_TEMPLATE',
  'INVOICES',
  'XML',
  1,
  'INVOICES_XML_20251112_v1.json',
  'files/templates/INVOICES_XML_20251112_v1.json',
  NOW(),
  NOW()
);

-- ============================================
-- INSERT : Champs du template INVOICES
-- ============================================

INSERT INTO template_fields (template_id, field_name, field_type, required, min_length, max_length, min_value, max_value, date_format, field_position)
VALUES
  (2, 'invoiceNumber', 'STRING', true, 5, 20, NULL, NULL, NULL, 1),
  (2, 'clientId', 'INTEGER', true, NULL, NULL, 1, 999999, NULL, 2),
  (2, 'amount', 'DECIMAL', true, NULL, NULL, 0.01, 999999.99, NULL, 3),
  (2, 'issueDate', 'DATE', true, NULL, NULL, NULL, NULL, 'yyyy-MM-dd', 4),
  (2, 'dueDate', 'DATE', true, NULL, NULL, NULL, NULL, 'yyyy-MM-dd', 5),
  (2, 'status', 'STRING', true, 1, 20, NULL, NULL, NULL, 6),
  (2, 'description', 'STRING', false, 0, 1000, NULL, NULL, NULL, 7);

-- ============================================
-- INSERT : Tâches de génération de test
-- ============================================

INSERT INTO generation_jobs (template_id, total_lines, error_lines, output_format, status, file_path, created_at, completed_at)
VALUES
  (1, 100, 5, 'JSON', 'SUCCESS', 'files/generated/CLIENTS_JSON_20251112_v1_data_1.json', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '1 hour 55 minutes'),
  (1, 500, 25, 'JSON', 'SUCCESS', 'files/generated/CLIENTS_JSON_20251112_v1_data_2.json', NOW() - INTERVAL '1 hour', NOW() - INTERVAL '50 minutes'),
  (2, 200, 10, 'XML', 'PENDING', NULL, NOW(), NULL);

-- ============================================
-- INSERT : Logs de génération
-- ============================================

INSERT INTO generation_logs (job_id, action, http_status, response_message, sent_at, retry_count, created_at)
VALUES
  (1, 'CREATED', NULL, NULL, NULL, 0, NOW() - INTERVAL '2 hours'),
  (1, 'SENT', 200, 'OK', NOW() - INTERVAL '1 hour 55 minutes', 0, NOW() - INTERVAL '1 hour 55 minutes'),
  (2, 'CREATED', NULL, NULL, NULL, 0, NOW() - INTERVAL '1 hour'),
  (2, 'SENT', 200, 'OK', NOW() - INTERVAL '50 minutes', 0, NOW() - INTERVAL '50 minutes'),
  (3, 'CREATED', NULL, NULL, NULL, 0, NOW());

-- ============================================
-- VÉRIFICATIONS
-- ============================================

-- Vérifier les templates
SELECT * FROM templates;

-- Vérifier les champs
SELECT * FROM template_fields;

-- Vérifier les jobs
SELECT * FROM generation_jobs;

-- Vérifier les logs
SELECT * FROM generation_logs;

-- Compter les enregistrements
SELECT COUNT(*) as template_count FROM templates;
SELECT COUNT(*) as field_count FROM template_fields;
SELECT COUNT(*) as job_count FROM generation_jobs;
SELECT COUNT(*) as log_count FROM generation_logs;

-- ============================================
-- SUPPRESSION (si besoin de réinitialiser)
-- ============================================

-- ATTENTION : L'ordre est important (contraintes FK)

-- DELETE FROM generation_logs;
-- DELETE FROM generation_jobs;
-- DELETE FROM template_fields;
-- DELETE FROM templates;

-- Réinitialiser les séquences
-- ALTER SEQUENCE templates_id_seq RESTART WITH 1;
-- ALTER SEQUENCE template_fields_id_seq RESTART WITH 1;
-- ALTER SEQUENCE generation_jobs_id_seq RESTART WITH 1;
-- ALTER SEQUENCE generation_logs_id_seq RESTART WITH 1;

