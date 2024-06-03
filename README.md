## Wędrownik
#### Aplikacja typu lista-szczegóły z animacją i elementami biblioteki-wsparcia

|                |                |                |
| -------------- | -------------- | -------------- |
| ![alt text](image.png) | ![alt text](image-1.png) | ![alt text](image-2.png) |

 ![alt text](image-3.png)  ![alt text](image-4.png)  ![alt text](image-5.png) 


### Streszczenie
Wędrownik jest prostą aplikacją do przeglądania szlaków w formacie gpx oraz xml mającą być wsparciem dla użytkownika w jego hobbistycznych podróżach. 

Aplikacja pozwala na:
* przeglądanie szlaków z widokiem na punkty
* odznaczanie odwiedzonych punktów
* katoryzowanie szlaków według własnych potrzeb,
* wgląd szlaku na interaktywnej mapie
* uruchomienia czasomierza do mierzenia czasu przejścia przez szlak (dla wielu szlaków jednocześnie, przy wyłączonej aplikacji)
* podgląd przewyższenia trasy na animowanym wykresie
* możliwość obliczenia przybliżonego czasu przybycia do następnego punktu
* przeglądanie widoku historii przebytych szlaków wraz ze stanem ich przejścia
* pobieranie lokalizacji geograficznej do pokazania aktualnej pozycji na mapie (z nie wiadomych mi przyczyn, nawet przy wyłączonej aplikacji)
* możliwość zatwierdzenia punktu jako odwiedzony po podejściu do niego wystarczająco blisko*

### Spis treści
1. Specyfikacje aplikacji i wymagania dotyczące uruchomienia
2. Zależności zewnętrzne i użyte biblioteki


### 1. Specyfikacje aplikacji i wymagania dotyczące uruchomienia
Do poprawnego uruchomienia aplikacji na urządzeniu mobilnym wymagana jest co najmniej wersja api 30 (android 11), wcześniejsze wersje nie są wspierane. Do kompilacji programu wystarczy najnowsza wersja kotlina, java 17 i agp - który sam pobierze pozostałe pakiety i zbuduje cały projekt, albo działające ide takie jak android studio.

### 2. Zależności zewnętrzne i użyte biblioteki
* Aplikacja napisana w kotline 
* Używa jetpack compose jako frameworku UI

* Hilt + Dagger - umożliwia di(dependency injection) i wprowadza wsparcie dla architektury MVVM
* Room - zarządza bazą danych w orm
* Voyager - lepsze zarządzanie nawigacji i przechowywaniem stanu aplikacji
* OsmDroid - interaktywne mapy (w javie, bez wsparcia i po wstecznej kompatybilności)
* Vico - przedstawienie interaktywnych wykresów
