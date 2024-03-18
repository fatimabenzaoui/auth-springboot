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
  - envoyer un email contenant la clé d'activation du compte<br>
   NB : Pour vérifier la réception de l'email avec la clé d'activation<br>
   Se rendre sur http://localhost:9081/ (usage d'un serveur mail local : smtp4dev)
