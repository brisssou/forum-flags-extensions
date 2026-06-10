# Forum Flags

Plus besoin de F5 vos sujets drapalisés sur HFR (et les forums cousins) toutes
les cinq minutes : l'extension surveille pour vous les sujets suivis, compte
vos MP non lus, et vous le signale directement sur l'icône — sans
jamais rafraîchir la page à la main.

Manifest V3. Kotlin/JS, multi-module, Compose HTML pour les interfaces.
Fonctionne sur les navigateurs Chromium (Chrome, Vivaldi, …) et Firefox.

## Ce que ça fait

- Un **badge** sur l'icône vous dit combien de sujets ont bougé (+ vos nouveaux
  MP) : bleu quand vous avez des MP, rouge sinon, et un `x` quand vous n'êtes
  pas/plus connecté.
- La **popup** déroule les sujets qui ont du nouveau, groupés par catégorie si
  vous voulez, avec un bouton « tout ouvrir » et de quoi mettre un sujet en
  sourdine d'un clic.
- Pas envie d'ouvrir la popup ? **Survolez l'icône**, la liste s'affiche en
  infobulle.
- Une **page d'options** pour régler le reste : fréquence, ce qu'on surveille,
  comportement des clics, couleur du badge, et les sujets masqués.

## Modules

- `js-lib` — externals typés de l'API d'extension `browser.*`.
- `common` — logique métier (sites, analyse HTML, préférences, snapshot) +
  tests unitaires.
- `worker` — service worker en arrière-plan (Chrome) / page d'événements
  (Firefox) : interrogation, badge, messagerie.
- `popup`, `prefs` — les interfaces Compose HTML.
- `package` — assemble l'extension chargeable et l'archive zip distribuable.

## Compilation

```
./gradlew jsTest assemble
```

Produit, par navigateur (manifeste adapté à chacun) :

- `package/build/dist/chrome/` et `package/build/dist/firefox/` — l'extension
  décompressée.
- `package/build/distributions/package-chrome-<version>.zip` et
  `package-firefox-<version>.zip` — les archives zip.

## Installation

**Chrome / Vivaldi**

1. Ouvrez `chrome://extensions` et activez le *mode développeur*.
2. *Charger l'extension non empaquetée* → sélectionnez
   `package/build/dist/chrome`.

**Firefox**

1. Ouvrez `about:debugging#/runtime/this-firefox`.
2. *Charger un module complémentaire temporaire* → sélectionnez
   `package/build/dist/firefox/manifest.json`.

## Tests

```
./gradlew jsTest
```

La logique métier est couverte par des suites `kotlin.test` qui s'exécutent sur
Node ; les interfaces, les externals et la glu du worker sont vérifiés en
chargeant le build dans Chrome et Firefox.

## Licence

Distribué sous la WTFPL (Do What The Fuck You Want To Public License),
version 2.

```
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
                    Version 2, December 2004

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>

 Everyone is permitted to copy and distribute verbatim or modified
 copies of this license document, and changing it is allowed as long
 as the name is changed.

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

  0. You just DO WHAT THE FUCK YOU WANT TO.
```
