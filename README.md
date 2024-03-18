Création d'un compte et activation avec une clé

// Lancer les containers mariaDB, adminer et smtp4dev<br>
cd src/main/resources<br>
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

- Activer un compte utilisateur :
  - vérifier si aucun utilisateur n'est trouvé avec la clé d'activation
  - vérifier si la clé d'activation n'a pas expiré

- Demander une nouvelle clé d'activation :
  - vérifier si le surnom existe dans la base de données
  - vérifier si le compte de l'utilisateur est déjà activé
  - vérifier si la clé d'activation précédente est toujours valide et n'a pas expiré

