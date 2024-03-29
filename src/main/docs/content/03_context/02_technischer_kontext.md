+++
title = "Technischer Kontext"
menuTitle = "3.2 Technischer Kontext"
weight = 2
+++

## 3.2 Technischer- oder Verteilungskontext

![Technische Interaktion zwischen kp-status und den Beteiligten](/images/Abb09_05_TechnischerKontext.png "Technische Interaktion zwischen kp-status und den Beteiligten")
##### Bild: Technische Interaktion zwischen kp-status und den Beteiligten

-----

#### XBoard Client (Fremdsystem)
Die "Anbindung" menschlicher Spieler erfolgt über ein grafisches Frontend, dessen Entwicklung nicht Teil von kp-status ist. Stattdessen kann jedes grafische Frontend verwendet werden, welches das sogenannte XBoard-Protokoll unterstützt. Hierzu zählen Xboard (bzw. Winboard unter Windows), Arena und Aquarium.

#### Polyglot Opening Book (Fremdsystem)
Polyglot Opening Book ist ein binäres Dateiformat für Eröffnungsbibliotheken. kp-status erlaubt die optionale Anbindung solcher Bücher. Der Zugriff erfolgt ausschließlich lesend.

-----

#### Zu Endspielen
Von der Implementierung einer Anbindung von Endspieldatenbanken (z.B. Nalimov Endgame Tablebases) wurde aus Aufwandsgründen Abstand genommen, siehe Risiken in Abschnitt 11. Der Entwurf ist aber offen für entsprechende Erweiterungen.
