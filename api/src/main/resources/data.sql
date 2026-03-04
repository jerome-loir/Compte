DROP TABLE IF EXISTS COMPTE;

CREATE TABLE COMPTE (
  numero_compte BIGINT AUTO_INCREMENT  PRIMARY KEY,
  nom VARCHAR(250) NOT NULL,
  prenom VARCHAR(250) NOT NULL,
  mail VARCHAR(250) NOT NULL
);

INSERT INTO COMPTE (nom, prenom, mail) VALUES
  ('DURAND', 'Jean-Pierre', 'jean-pierre.durand@gmail.com');
  
DROP TABLE IF EXISTS LIGNE;
 
CREATE TABLE LIGNE (
  id BIGINT AUTO_INCREMENT  PRIMARY KEY,
  numero_compte INT,
  description VARCHAR(250) NOT NULL,
  categorie VARCHAR(250) NOT NULL,
  montant FLOAT(24) NOT NULL,
  date_operation DATE NOT NULL,
  FOREIGN KEY (numero_compte) REFERENCES COMPTE(numero_compte)
);
 
INSERT INTO LIGNE (description, numero_compte, categorie, montant, date_operation) VALUES
  ('Crédit coopératif', '1', 'dépot', '3000.00', '2025-09-15'),
  ('Moi', '1', 'retrait', '-150.00', '2025-09-17'),
  ('EDF', '1', 'facture', '-51.48', '2025-09-19');
