# P9-MediLaboSolutions
# Recommandations de Green Code pour le projet

## Pour les images
- **Format WebP ou AVIF** pour les images principales (compression efficace). Mis en place dans le projet actuel
- **Utilisation de SVG** pour les icônes, afin de réduire la taille des fichiers et préserver la qualité.
- **Lazy-loading** pour les images actives afin de ne charger que celles visibles à l'utilisateur.

## Pour le HTML / CSS
- **HTML propre** avec une structure sémantique et accessible.
- **CSS minifié** et allégé (utilisation de [PurgeCSS](https://purgecss.com/) pour éliminer le CSS inutilisé).
- **Polices web optimisées** (n'utiliser que les caractères nécessaires et les formats les plus légers comme WOFF2).

## Pour les API et réseaux
- **JSON allégé** : Réduire la taille des objets JSON envoyés pour minimiser les échanges de données.
- **Éviter les appels API inutiles** : Ne pas multiplier les requêtes inutiles, surtout lors du chargement initial.
- **Appels API groupés** lorsque cela est possible, pour réduire le nombre de requêtes réseau.

## Pour le Backend
- **Serveurs écologiques** : Héberger les services sur des serveurs utilisant des énergies renouvelables.
- **Optimisation des processus en arrière-plan** avec des solutions de mise en file d'attente.
- **Base de données optimisée** : Minimiser les requêtes lourdes et utiliser des indices appropriés.

## Pour les performances
- **Compression des ressources** côté serveur (GZIP, Brotli).
- **Lazy loading** pour les ressources non essentielles, y compris les scripts JavaScript si existant.
- **Eviter les reflows/repaints inutiles** en optimisant les interactions avec le DOM.

## Conclusion
En appliquant ces recommandations, nous contribuons non seulement à améliorer les performances du projet, mais aussi à réduire notre impact écologique en optimisant les ressources utilisées par le code et les infrastructures.
