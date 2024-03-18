Création d'un compte et activation avec une clé

// Lancer mariaDB, adminer et smt4dev<br>
docker-compose.yml up -d

// Vérifier que les serveurs sont bien lancés<br>
docker-compose ls

// Spécifications fonctionnelles<br>
- Créer un compte utilisateur :
  - crypter le mot de passe
  - ajouter un rôle par défaut (rôle "CUSTOMER")
  - vérifier si la longueur du password est incorrecte
  - vérifier si le username existe déjà en bdd
  - vérifier si l'email existe déjà en bdd
  - vérifier si l'email est valide
