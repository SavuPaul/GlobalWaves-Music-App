# <span style="color: cyan;">README GlobalWaves - Etapa 3</span>

### <span style="color: purple;">Implementare:</span>

#### <span style="color:white;">Design Pattern:</span>
* Am ales sa implementez Singleton pentru clasa Admin deoarece clasa admin poate fi asociata cu o baza de date,
adminul fiind unic. <br>


* Am implementat design pattern-ul Factory pentru User, in momentul crearii acestuia. Fiind de 3 tipuri, "user",
"artist" sau "host", clasa UserFactory contine o functie ce returneaza un obiect nou de tipurile respective in
functie de parametrul primit (type). Apelul functiei se poate regasi in clasa User, functia createUser.


* Am implementat design pattern-ul Builder pentru WrappedEntity pentru crearea obiectelor in functie de campurile
necesare (spre exemplu un podcast nu va avea genre/arist/album).


* Am implementat design pattern-ul Visitor pentru printarea paginii unui artist/host deoarece fiecare entitate (Album,
Event, Merch, Podcast, Announcement) va avea un format propriu asociat, diferit de celelalte entitati. Astfel, cu
ajutorul unui visitor, fiecare obiect va apela metoda proprie pentru afisare.

#### <span style="color:white;">Flow-ul programului:</span>

* Pentru afisarea statisticilor din comanda wrapped, am creat clasa WrappedEntity.
  WrappedEntity este o clasa ce ajuta la numararea listen-urilor pentru fiecare instanta in parte (album, cantec, 
  podcast). De exemplu, fiecare user are un array list de tip SongHelper, unde se adauga cantece noi (abia ascultate), 
  sau se incrementeaza numarul de listen-uri pentru  un cantec deja ascultat.
* Listen-urile entitatilor sunt updatate: in momentul unui load (pentru cantec), cand
  player-ul unui user se intrerupe in momentul cand acesta executa comanda search sau cand trebuie executata comanda wrapped.
  Acest lucru se face prin retinerea
  timestamp-ului comenzii load si calculand diferenta intre timestamp-ul search/wrapped si timestamp-ul load. Diferenta este
  intervalul de timp trecut dintre cele 2 comenzi, moment in care se parcurge entitatea din player (de exemplu un album)
  si se numara cate cantece au fost ascultate in acel interval de timp, adaugandu-le in campul listenedEntities din
  clasa user.
* Pentru generarea statisticilor unui user de pe platforma, se parcurge lista listenedEntities a user-ului si
  se "triaza" entitatile in functie de tipul lor, creand obiecte de tip SongHelper/AlbumHelper/GenreHelper care sunt
  adaugate in liste ce urmeaza a fi sortate si trunchiate la dimensiunea 5 (daca au dimensiunea mai mare de 5).
* Pentru generarea statisticilor unui artist de pe platforma, se parcurg listenedEntities ale tuturor user-ilor,
  si se adauga noii listeneri / noile cantece ascultate ale artistului care apeleaza comanda wrapped. Modul de lucru
  este similar cu wrapped-ul user-ului normal, insa in cazul artistului trebuie parcurse toate listele user-ilor.
* De asemenea, in momentul in care un artist apeleaza comanda wrapped, trebuie sa se updateze wrapped-urile tuturor
  userilor pentru a nu rata cantece care au fost ascultate intre timp de catre utilizatori.
* Programul se termina mereu cu comanda endProgram, care este initializata cu valori default si apelata in functia main.