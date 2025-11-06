-- data.sql generated for testing
-- Order: insert parent entities first to satisfy FK constraints (Users -> Meters -> Readings/Consumptions -> Alerts/Recommendations)
-- Passwords are stored as bcrypt hashes. Plain passwords are commented beside each INSERT for testing.

-- === Users ===
-- plain password for carlos.perez@example.com: Password123!
INSERT INTO users (id, full_name, email, citizen_id, service_number, phone, role, status, created_at, updated_at)
VALUES    ('11111111-1111-1111-1111-111111111111', 'Carlos Perez', 'carlos.perez@example.com', '1234567890', 'SV-0001', '+571300000001', 'CLIENT', 'ACTIVE', '2025-10-01T08:00:00+00:00', '2025-10-01T08:00:00+00:00');
-- plain password for admin@example.com: AdminPass!2025
INSERT INTO users (id, full_name, email, citizen_id, service_number, phone, role, status, created_at, updated_at)
VALUES ('22222222-2222-2222-2222-222222222222', 'Admin Root', 'admin@example.com', '0987654321', 'SV-0002', '+571300000002', 'ADMIN', 'ACTIVE', '2025-10-01T08:00:00+00:00', '2025-10-01T08:00:00+00:00');
    -- plain password: TestPass!2025  laura.gomez@example.com
INSERT INTO users (id, full_name, email, citizen_id, service_number, phone, role, status, created_at, updated_at)
VALUES ('33333333-3333-3333-3333-333333333333', 'Laura Gomez', 'laura.gomez@example.com', '1357913579', 'SV-0003', '+571300000003', 'CLIENT', 'ACTIVE', '2025-10-15T08:00:00Z', '2025-10-15T08:00:00Z');


-- === Password credentials (bcrypt hashes) ===
-- user carlos.perez@example.com: plain='Password123!'
INSERT INTO password_credentials (user_id, password_hash, created_at, updated_at) VALUES ( '11111111-1111-1111-1111-111111111111', '$2b$10$LMgCd4nJ4g4V5Nuf59o7Cu.sah2332YmtCIJsN4xOJBqwkoCQPQ6W', '2025-10-01T08:00:00+00:00', '2025-10-01T08:00:00');
-- user admin@example.com: plain='AdminPass!2025'
INSERT INTO password_credentials (user_id, password_hash, created_at, updated_at) VALUES ( '22222222-2222-2222-2222-222222222222', '$2b$10$3NroGfWj/frefYH0sU9sIO6cRGnVErzbBxuVndnmoisOvHSBSG5fa', '2025-10-01T08:00:00+00:00', '2025-10-01T08:00:00');
-- bcrypt hash de 'TestPass!2025' (ejemplo)
INSERT INTO password_credentials (user_id, password_hash, created_at, updated_at) VALUES ('33333333-3333-3333-3333-333333333333', '$2a$12$lVu0uZaddGHF7ujGB8bVSuL7cwmcGEB6y0oyPsbztjhhe8nhajDai', '2025-10-15T08:00:00Z', '2025-10-15T08:00:00Z');

-- === Refresh sessions ===
INSERT INTO refresh_sessions (id, user_id, refresh_token, expires_at, created_at)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'rftoken-user1-2025', '2026-10-01T08:00:00Z', '2025-10-01T08:00:00Z'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'rftoken-admin-2025', '2026-10-01T08:00:00Z', '2025-10-01T08:00:00Z');

-- === Meters ===
INSERT INTO meters (id, user_id, serial_number, meter_type, status,  provider, installation_address,service_number,alias, installed_at)
VALUES
    ('a01b2edc-51e9-45d2-8b34-f126c36e9c8d', '11111111-1111-1111-1111-111111111111', 'MTR-0001', 'SMART', 'ACTIVE', 'ENEL', 'Calle 123', '123', 'Apto','2025-01-01T00:00:00Z'),
    ('aa28230d-b957-4894-85fe-6e65f928b797', '22222222-2222-2222-2222-222222222222', 'MTR-0002', 'SMART', 'ACTIVE','ENEL', 'Transversal 69', '456', 'Casa Campo','2025-01-01T00:00:00Z'),
    ('b01f2edc-aaaa-4b4c-9f11-111111111111', '33333333-3333-3333-3333-333333333333', 'MTR-0003', 'SMART', 'ACTIVE', 'ENEL', 'Cra 45 #10-20', 'SV-0003', 'Depto Laura', '2025-10-20T00:00:00Z');
-- === Meter readings ===
INSERT INTO meter_readings (id, meter_id, ts, kwh_accum)
VALUES
    ('0b3a6a67-0d08-4e9e-9b9d-6b3a2db2b001', 'a01b2edc-51e9-45d2-8b34-f126c36e9c8d', '2025-10-16T08:00:00Z', 10234.567),
    ('0b3a6a67-0d08-4e9e-9b9d-6b3a2db2b002', 'a01b2edc-51e9-45d2-8b34-f126c36e9c8d', '2025-10-16T08:01:00Z', 10234.687),
    ('0b3a6a67-0d08-4e9e-9b9d-6b3a2db2b003', 'aa28230d-b957-4894-85fe-6e65f928b797', '2025-10-01T00:00:00Z', 5000.000);

-- === Meter Quota ===
-- Cuota mensual de 250 kWh vigente desde el 1-nov-2025
INSERT INTO meter_quotas (id, meter_id, periodicity, kwh_limit, valid_from, valid_to)
VALUES ('44444444-4444-4444-4444-444444444444', 'b01f2edc-aaaa-4b4c-9f11-111111111111', 'MONTHLY', 250.00, '2025-11-01T00:00:00Z', NULL);

-- === Consumptions ===
-- === Consumptions (MONTHLY) — METER2 2024-2025 ===
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('c8d71886-a3f5-46e9-8ac5-993a4a0b8688','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2024-01-01T00:00:00Z',350.5);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('ee29b858-8f1d-4575-a78b-7751daa5837c','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2024-02-01T00:00:00Z',218.3);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('ba5b6de3-3ee4-4b98-a732-797c0860046f','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2024-03-01T00:00:00Z',273.9);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('9010c018-4ae9-4cda-8acf-eb6399ee7436','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2024-04-01T00:00:00Z',262.3);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('49e1a031-f7dd-4448-91c6-3c8aa4e30316','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2024-05-01T00:00:00Z',310.4);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('32a6960d-48ec-4675-978f-8fc7e59c624a','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2024-06-01T00:00:00Z',160.8);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('617f8479-03ff-415c-8c78-4628f6368c1d','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2024-07-01T00:00:00Z',334.8);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('20e79612-d201-4b34-b57b-82c45d843e8c','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2024-08-01T00:00:00Z',119.2);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('b8a3c03b-d481-451e-bbcf-e6cb35d4d9ca','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2024-09-01T00:00:00Z',60.0);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('36425666-a54e-49b4-8a27-1133792bae53','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2024-10-01T00:00:00Z',185.4);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('a3bb2cea-360b-45b7-b1fb-069c88e89d2c','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2024-11-01T00:00:00Z',157.6);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('eedade06-89dc-46d1-83f1-9b7283d5c379','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2024-12-01T00:00:00Z',350.7);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('7ac2af4b-6754-40c0-b23f-cc2fa95ec857','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2025-01-01T00:00:00Z',329.8);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('02dc8012-9e1b-4520-b40a-009486f273f9','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2025-02-01T00:00:00Z',130.3);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('117a665e-423e-4266-a458-0154a8f674c6','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2025-03-01T00:00:00Z',316.3);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('e7b707e5-1179-4893-a3e4-de6031a54bd6','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2025-04-01T00:00:00Z',149.8);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('873c4f53-9cce-4a14-af6f-4bed7d52142d','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2025-05-01T00:00:00Z',232.2);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('a1ba5cee-783b-4e74-ac7e-d7c61ffe24ce','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2025-06-01T00:00:00Z',245.6);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('699fa279-b0c0-4a3d-a134-88ddd6ad42c5','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2025-07-01T00:00:00Z',287.8);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('debd7a24-1072-49ba-8f71-c026f2625a70','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2025-08-01T00:00:00Z',360.7);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('d37c4a4b-84c6-4df4-913f-89d1a733d424','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2025-09-01T00:00:00Z',297.5);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('cc08c36d-fa55-4428-9a86-320cd4662ec9','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2025-10-01T00:00:00Z',271.5);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('4cb884f0-6651-42b7-8ca3-e2277530b56a','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2025-11-01T00:00:00Z',298.5);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('71b57fc8-4505-43c2-ad03-34c3aae723d1','aa28230d-b957-4894-85fe-6e65f928b797','MONTHLY','2025-12-01T00:00:00Z',311.4);

-- === Consumptions (DAILY) — METER1 Oct-2025 ===
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('e766ff8f-0fd2-45b4-9bc3-af9e827f40b7','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-01T00:00:00Z',9.12);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('f34beaf7-8e2b-447f-99e4-925c1170f291','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-02T00:00:00Z',8.85);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('8dd8ae31-9b9f-4c60-a8a7-fe191e95f0f5','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-03T00:00:00Z',9.59);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('143a541a-3ccc-48ac-a705-d9580d7fb11a','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-04T00:00:00Z',13.75);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('176f0a33-38eb-4872-97b4-a2b99db836c0','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-05T00:00:00Z',11.27);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('73489f0b-1b25-4d1b-a129-9af910ee7d89','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-06T00:00:00Z',18.01);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('c3363ff4-31e9-40aa-8b84-b2485bb474d7','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-07T00:00:00Z',8.54);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('2f129795-4334-47af-a80c-bd061a6a9d03','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-08T00:00:00Z',7.7);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('084e4f0d-39b9-4308-b6cc-77898ec73675','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-09T00:00:00Z',13.31);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('bad2db98-8686-494e-b86e-c93a59927674','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-10T00:00:00Z',15.27);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('de8b0a1c-f4fb-4e81-8b99-f50f55a2648e','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-11T00:00:00Z',13.77);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('6620df6b-1898-4e29-980e-7245f4e8c572','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-12T00:00:00Z',14.86);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('576ea628-3dd5-48dd-96da-d77adb95ef1a','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-13T00:00:00Z',15.28);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('b5b3fc96-2aa9-4b0a-8c6d-e270e4402d73','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-14T00:00:00Z',10.72);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('031127ac-a9a1-4a11-8a30-0319178ab144','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-15T00:00:00Z',6.73);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('c8c66137-c6fa-483f-8372-2435ac4166be','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-16T00:00:00Z',9.4);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('e3060a2e-1dcf-4bc8-be61-5d6db5e61d29','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-17T00:00:00Z',13.86);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('eca1249e-b606-4a5c-8a2c-b6a79dae72f1','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-18T00:00:00Z',7.34);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('a1479bc2-5750-4cf5-af49-be5f8db91185','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-19T00:00:00Z',12.21);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('a28813bc-6b48-426e-9758-32d8a62a8fe9','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-20T00:00:00Z',11.76);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('ef007b1f-b562-4100-9362-f8716e43be25','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-21T00:00:00Z',10.05);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('b0bff86b-624f-45ef-86c8-0ea02f76fc69','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-22T00:00:00Z',13.17);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('c411cf90-0624-46b3-8a49-ed85087734f8','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-23T00:00:00Z',12.74);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('7616e8ff-4ee8-4df1-b769-68ee74bbc20a','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-24T00:00:00Z',17.96);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('48ab7523-d038-48a3-9005-80d55fd15ffa','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-25T00:00:00Z',14.15);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('b8922f4c-af0b-43d0-a1f8-4e665cfae17f','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-26T00:00:00Z',10.09);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('fe5aff4f-8aa1-4e5b-980a-c7dc961c3980','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-27T00:00:00Z',9.31);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('cacb3d9d-3952-4917-b6dd-bd0bdc3c5889','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-28T00:00:00Z',8.51);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('374c88a2-aad8-4102-b965-686a8118ea01','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-29T00:00:00Z',13.86);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('721f2e84-76c9-4ed7-bdf6-a2c2817673e8','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-30T00:00:00Z',9.3);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('ccfef427-7fa0-46d2-afaa-9e0e087fb5aa','a01b2edc-51e9-45d2-8b34-f126c36e9c8d','DAILY','2025-10-31T00:00:00Z',10.79);

-- === Consumptions (DAILY) — METER2 Oct-2025 ===
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('cc362448-e2fd-4d79-a947-8b648a24038f','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-01T00:00:00Z',10.37);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('a6328ac6-913a-43a4-8e36-286c3c361be1','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-02T00:00:00Z',6.69);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('c07c02d3-bb40-4b37-8e2b-dc3dd9f628a4','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-03T00:00:00Z',7.77);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('bd2740a7-d942-475f-b989-3c4b8342fdd6','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-04T00:00:00Z',4.29);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('f6daf304-875e-410c-8a32-ce5a9a0868b6','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-05T00:00:00Z',6.37);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('325707de-1680-487c-a2aa-1157ee3e35b0','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-06T00:00:00Z',7.08);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('016c0430-1450-485f-b34b-c8d88fb99da9','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-07T00:00:00Z',9.54);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('561b001a-b2fe-4a49-8f77-309b0466fc39','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-08T00:00:00Z',11.48);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('84e31667-5a9d-4f44-b93e-0c2226e68b53','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-09T00:00:00Z',8.45);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('1e18b38a-a79a-4ce8-a9f4-f012fbeef9b4','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-10T00:00:00Z',9.15);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('b825dd4d-0120-4b02-b881-d4fa0e7a27e8','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-11T00:00:00Z',9.81);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('afbde672-92d5-42fa-b402-a9e7bbcbb8a2','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-12T00:00:00Z',12.33);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('a382e850-a327-4300-9cd3-e6c5ffe75c44','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-13T00:00:00Z',10.73);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('d42d4037-765b-4f9c-a0ee-a133a29d34bc','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-14T00:00:00Z',9.18);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('a37f6cd5-7388-4522-b2b6-04ccb1076469','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-15T00:00:00Z',5.97);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('a38dd982-b2dd-4c3f-b1a4-fec8c3596996','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-16T00:00:00Z',10.76);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('49845983-bfff-4256-9d35-f61101d4abb5','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-17T00:00:00Z',9.45);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('33e30812-71aa-414a-8802-4f17381e2548','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-18T00:00:00Z',12.72);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('bd059910-6820-45dd-860d-82eea9442df9','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-19T00:00:00Z',9.27);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('d631c70b-2400-40c2-9e22-2b575ea62033','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-20T00:00:00Z',13.38);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('aeb83e6f-5db9-40e7-af83-0559dffc5595','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-21T00:00:00Z',7.6);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('950a87dc-1f9e-43af-9b74-2550a9fd8aed','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-22T00:00:00Z',12.48);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('2a9c0a76-65fa-428d-9972-07a0a38ce4fe','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-23T00:00:00Z',8.79);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('84aeb4e7-d958-4efe-95b2-986f63c7f87a','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-24T00:00:00Z',7.21);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('a3e46f1e-51eb-4fad-badc-0f36ca78be15','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-25T00:00:00Z',6.25);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('bab2d600-c074-47e2-bd6a-c4df5f2cef0a','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-26T00:00:00Z',8.93);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('10d33512-c74d-40b5-b662-0e0de00ccedc','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-27T00:00:00Z',12.06);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('c55f10a1-ee69-45d5-a061-a9b91dab5c2c','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-28T00:00:00Z',10.54);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('b9b695e5-3748-4e1e-90cb-b370706e20b3','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-29T00:00:00Z',10.22);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('bd5fdb9f-838c-4678-b5ab-4f0ac901800d','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-30T00:00:00Z',2.56);
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh) VALUES ('135033eb-a0d6-425c-b525-8bec00c0d9b4','aa28230d-b957-4894-85fe-6e65f928b797','DAILY','2025-10-31T00:00:00Z',10.28);

-- === DAILY Octubre 2025 (suma ~200 kWh) ===
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh)
VALUES
    ('d0010001-0000-0000-0000-000000000001','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-01T00:00:00Z',6.5),
    ('d0010001-0000-0000-0000-000000000002','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-02T00:00:00Z',6.7),
    ('d0010001-0000-0000-0000-000000000003','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-03T00:00:00Z',6.2),
    ('d0010001-0000-0000-0000-000000000004','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-04T00:00:00Z',5.9),
    ('d0010001-0000-0000-0000-000000000005','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-05T00:00:00Z',6.8),
    ('d0010001-0000-0000-0000-000000000006','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-06T00:00:00Z',7.0),
    ('d0010001-0000-0000-0000-000000000007','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-07T00:00:00Z',6.6),
    ('d0010001-0000-0000-0000-000000000008','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-08T00:00:00Z',6.3),
    ('d0010001-0000-0000-0000-000000000009','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-09T00:00:00Z',6.1),
    ('d0010001-0000-0000-0000-000000000010','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-10T00:00:00Z',6.4),
    ('d0010001-0000-0000-0000-000000000011','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-11T00:00:00Z',6.5),
    ('d0010001-0000-0000-0000-000000000012','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-12T00:00:00Z',6.7),
    ('d0010001-0000-0000-0000-000000000013','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-13T00:00:00Z',6.2),
    ('d0010001-0000-0000-0000-000000000014','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-14T00:00:00Z',5.9),
    ('d0010001-0000-0000-0000-000000000015','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-15T00:00:00Z',6.8),
    ('d0010001-0000-0000-0000-000000000016','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-16T00:00:00Z',7.0),
    ('d0010001-0000-0000-0000-000000000017','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-17T00:00:00Z',6.6),
    ('d0010001-0000-0000-0000-000000000018','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-18T00:00:00Z',6.3),
    ('d0010001-0000-0000-0000-000000000019','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-19T00:00:00Z',6.1),
    ('d0010001-0000-0000-0000-000000000020','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-20T00:00:00Z',6.4),
    ('d0010001-0000-0000-0000-000000000021','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-21T00:00:00Z',6.5),
    ('d0010001-0000-0000-0000-000000000022','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-22T00:00:00Z',6.7),
    ('d0010001-0000-0000-0000-000000000023','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-23T00:00:00Z',6.2),
    ('d0010001-0000-0000-0000-000000000024','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-24T00:00:00Z',5.9),
    ('d0010001-0000-0000-0000-000000000025','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-25T00:00:00Z',6.8),
    ('d0010001-0000-0000-0000-000000000026','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-26T00:00:00Z',7.0),
    ('d0010001-0000-0000-0000-000000000027','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-27T00:00:00Z',6.6),
    ('d0010001-0000-0000-0000-000000000028','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-28T00:00:00Z',6.3),
    ('d0010001-0000-0000-0000-000000000029','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-29T00:00:00Z',6.1),
    ('d0010001-0000-0000-0000-000000000030','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-30T00:00:00Z',6.4),
    ('d0010001-0000-0000-0000-000000000031','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-10-31T00:00:00Z',6.4);

-- …(agrega más filas similares hasta cubrir 31 días; total objetivo ≈ 200 kWh)

-- === DAILY Noviembre 2025 (suma ~280 kWh) ===
INSERT INTO consumptions (id, meter_id, consumption_type, period_start, kwh)
VALUES
    ('d0020001-0000-0000-0000-000000000001','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-01T00:00:00Z',9.5),
    ('d0020001-0000-0000-0000-000000000002','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-02T00:00:00Z',9.1),
    ('d0020001-0000-0000-0000-000000000003','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-03T00:00:00Z',9.3),
    ('d0020001-0000-0000-0000-000000000004','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-04T00:00:00Z',9.2),
    ('d0020001-0000-0000-0000-000000000005','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-05T00:00:00Z',9.8),
    ('d0020001-0000-0000-0000-000000000006','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-06T00:00:00Z',9.6),
    ('d0020001-0000-0000-0000-000000000007','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-07T00:00:00Z',9.4),
    ('d0020001-0000-0000-0000-000000000008','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-08T00:00:00Z',9.7),
    ('d0020001-0000-0000-0000-000000000009','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-09T00:00:00Z',9.0),
    ('d0020001-0000-0000-0000-000000000010','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-10T00:00:00Z',9.9),
    ('d0020001-0000-0000-0000-000000000011','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-11T00:00:00Z',9.5),
    ('d0020001-0000-0000-0000-000000000012','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-12T00:00:00Z',9.1),
    ('d0020001-0000-0000-0000-000000000013','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-13T00:00:00Z',9.3),
    ('d0020001-0000-0000-0000-000000000014','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-14T00:00:00Z',9.2),
    ('d0020001-0000-0000-0000-000000000015','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-15T00:00:00Z',9.8),
    ('d0020001-0000-0000-0000-000000000016','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-16T00:00:00Z',9.6),
    ('d0020001-0000-0000-0000-000000000017','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-17T00:00:00Z',9.4),
    ('d0020001-0000-0000-0000-000000000018','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-18T00:00:00Z',9.7),
    ('d0020001-0000-0000-0000-000000000019','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-19T00:00:00Z',9.0),
    ('d0020001-0000-0000-0000-000000000020','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-20T00:00:00Z',9.9),
    ('d0020001-0000-0000-0000-000000000021','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-21T00:00:00Z',9.5),
    ('d0020001-0000-0000-0000-000000000022','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-22T00:00:00Z',9.1),
    ('d0020001-0000-0000-0000-000000000023','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-23T00:00:00Z',9.3),
    ('d0020001-0000-0000-0000-000000000024','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-24T00:00:00Z',9.2),
    ('d0020001-0000-0000-0000-000000000025','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-25T00:00:00Z',9.8),
    ('d0020001-0000-0000-0000-000000000026','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-26T00:00:00Z',9.6),
    ('d0020001-0000-0000-0000-000000000027','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-27T00:00:00Z',9.4),
    ('d0020001-0000-0000-0000-000000000028','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-28T00:00:00Z',9.7),
    ('d0020001-0000-0000-0000-000000000029','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-29T00:00:00Z',9.0),
    ('d0020001-0000-0000-0000-000000000030','b01f2edc-aaaa-4b4c-9f11-111111111111','DAILY','2025-11-30T00:00:00Z',9.9);

-- …(agrega filas hasta cubrir noviembre; total objetivo ≈ 280 kWh)


-- === Alerts ===
-- INSERT INTO alerts (id, meter_id, status, threshold_kwh, current_kwh, created_at, updated_at)
-- VALUES
--    ('31d4e0b1-91f2-4fcd-97d1-ecdf6c45a1e1', 'aa28230d-b957-4894-85fe-6e65f928b797', 'ACTIVE', 350.00, 325.00, '2025-10-18T08:00:00Z', '2025-10-18T08:00:00Z'),
--    ('f61b8a24-1d0c-46f3-9217-f7629e2397b5', 'a01b2edc-51e9-45d2-8b34-f126c36e9c8d', 'RESOLVED', 400.00, 390.00, '2025-09-01T08:00:00Z', '2025-09-02T08:00:00Z');

-- === Recommendations ===
INSERT INTO recommendations (id, user_id, message, status, created_at, updated_at)
VALUES
    ('294ec4d9-6653-4ac0-a3bf-cc12903740ea', '11111111-1111-1111-1111-111111111111', 'Reduce A/C usage between 6pm-10pm to save energy', 'ACTIVE', '2025-10-10T10:00:00Z', '2025-10-10T10:00:00Z'),
    ('cac38e37-9e8c-42d5-b824-5cbae7505322', '22222222-2222-2222-2222-222222222222', 'Schedule monthly audit of meter wiring', 'ARCHIVED', '2025-09-05T08:00:00Z', '2025-09-06T09:00:00Z');
