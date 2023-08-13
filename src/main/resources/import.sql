INSERT INTO usuarios (username, password, enabled, nombre, apellido, email) VALUES ('jose','$2a$10$NdXEfVP4FolkIY20GBFpyu/kBa6yex5NEAMglW6t2TSRC7AqMcTN6',1,'Jose','Plasencia','plasen@gmail.com');
INSERT INTO usuarios (username, password, enabled, nombre, apellido, email) VALUES ('maria','$2a$10$sR1qF9H2la8F/KyNr8WPte093kix9PoPQEHUueKkdK/hPa4Xf7gWq',1,'Maria','Dolores','maria@gmail.com');

INSERT INTO roles (nombre) VALUES ('ROLE_USER');
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN');

INSERT INTO usuarios_in_role (usuario_id, role_id) VALUES (1,1);
INSERT INTO usuarios_in_role (usuario_id, role_id) VALUES (2,2);
INSERT INTO usuarios_in_role (usuario_id, role_id) VALUES (2,1);