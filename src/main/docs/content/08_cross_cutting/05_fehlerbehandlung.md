+++
title = "Fehlerbehandlung"
menuTitle = "8.5 Fehlerbehandlung"
weight = 5
+++

## 8.5 Ausnahme- und Fehlerbehandlung

kp-status verfügt über keine eigene Oberfläche. Es muss Probleme daher nach außen signalisieren.

Die Methoden der kp-status-Subsysteme werfen dazu Runtime Exceptions, im Falle des Engine-Subsystems bei asynchroner Zugermittlung zusätzlich Fehlernachrichten (_onError_). Eigene Erweiterungen (beispielsweise eine eigene Zugauswahl) müssen entsprechend implementiert sein, Checked Exceptions (zum Beispiel _java.io.IOException_) etwa sind geeignet zu verpacken.

Die wenigen erwarteten Exceptions bei kp-status zeigt das javadoc der entsprechenden Methoden und Konstruktoren an. Probleme beim Einlesen einer Eröffnungsbibliothek etwa, oder beim Versuch der Zugermittlung innerhalb der Engine bei ungültiger Stellung (falls erkannt). Alle übrigen Exceptions wären Programmierfehler (bitte melden Sie solche Fälle unter https://github.com/kp-status/).

Das XBoard-Subsystem fängt sämtliche Exceptions und kommuniziert sie über das XBoard-Protokoll nach außen (Kommando "tellusererror"). Ein grafisches Frontend visualisiert sie in der Regel in einem Fehler-Dialog oder einer Alert-Box, das folgende Bild zeigt das für das Schachfrontend Arena.


![kp-status-Fehlermeldung visualisiert durch Arena](/images/Abb09_22_FehlermeldungArena.png "kp-status-Fehlermeldung visualisiert durch Arena")

*Bild: kp-status-Fehlermeldung visualisiert durch Arena: Datei nicht gefunden*

kp-status arbeitet dann "normal" weiter, wobei der Anwender selbst entscheidet, ob ein Fortfahren in der konkreten Situation sinnvoll ist. Beispielsweise könnte er ohne Eröffnungsbibliothek weiterspielen.
