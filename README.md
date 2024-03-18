Création d'un compte et activation avec une clé

// Lancer mariaDB, adminer et smt4dev<br>
docker-compose.yml up -d

// Vérifier que les serveurs sont bien lancés<br>
docker-compose ls

// Spécifications fonctionnelles<br>
- Créer un compte utilisateur :
  - crypter le mot de passe
  - ajouter un rôle par défaut (rôle "CUSTOMER")
