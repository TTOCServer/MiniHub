# MiniHub - Plugin Velocity

> **Déclaration de génération IA** : Ce projet est généré avec l'assistance de l'IA, le code est destiné à un usage normal, à des fins de référence et d'apprentissage uniquement, veuillez ne pas l'utiliser à nouveau pour l'entraînement de l'IA !

Un plugin simple de serveur proxy Velocity qui permet aux joueurs de se téléporter vers des serveurs spécifiés via des commandes configurées.

## Fonctionnalités

- 🚀 Téléportation vers des serveurs spécifiés via des commandes simples. Prend en charge les commandes françaises !

## Installation

1. Téléchargez le dernier fichier jar de MiniHub
2. Placez le fichier jar dans le répertoire `plugins` de Velocity
3. Redémarrez le serveur Velocity
4. Le plugin générera automatiquement les fichiers de configuration et les fichiers de langue

## Configuration

Fichier de configuration situé dans `plugins/MiniHub/config.yml` :

```yaml
# Fichier de configuration du plugin MiniHub

# Langue à utiliser
language: "fr_fr"

# Configuration des commandes de serveur
# Format : nom du serveur -> liste de commandes
servers:
  lobby:
    commands: ["hub", "lobby"]
    # Utilisez /hub ou /lobby pour vous téléporter vers le serveur lobby
  survival:
    commands: ["survival", "srv"]
  creative:
    commands: ["creative", "crea"]
```

## Utilisation des commandes

Selon les paramètres de configuration, les joueurs peuvent utiliser les commandes correspondantes pour se téléporter vers les serveurs cibles :

- `/hub` ou `/lobby` - Téléportation vers le serveur lobby
- `/survival` ou `/srv` - Téléportation vers le serveur survie  
- `/creative` ou `/crea` - Téléportation vers le serveur créatif

## Informations de développement

- **Langage de développement** : Java 17
- **Outil de build** : Maven
- **Gestion des dépendances** : Maven
- **Plateforme supportée** : Velocity Proxy

## Journal des modifications

### v1.0-SNAPSHOT
- Version initiale publiée
- Fonctionnalité de téléportation par commande de base
- Support multilingue
- Système de fichiers de configuration