Alessio Mugnaini 65674

Note:
Al momento non sono presenti bug noti.

Per il design dell'interfaccia mi sono basato sui concetti del Material Design, usando 
widget e stili derivanti da questo. 

L'interfaccia è dotata di due temi, uno chiaro e uno scuro, che variano in base al tema
del dispositivo. L'attivazione della dark mode (o modalità notte) è stata aggiunta a 
partire da Android 10 (Q) e il tema di questa app si adatta in base all'attivazione
di questa modalità o meno. Per dispositivi con versione di android inferiore alla 10, ho
impostato il tema scuro di default, in quanto ho riscontrato maggior apprezzamento da parte
degli utenti a cui l'ho fatta testare. Entrambi i temi sono caratterizzati da colori 
semplici con contrasti eleganti e non fastidiosi alla vista. 

L'orientamento dell'applicazione è bloccato su portrait, inoltre ho sviluppato i layouts
adatti solo per smartphones e non gli ho testati su schermi piu grandi, come i tablets, o
piu piccoli, come gli smartwatch. I layout sono comunque adattivi, ma potrebbero non essere 
ideali per schermi con dimensione diversa rispetto agli smartphones.

Ho lasciato la possibilità agli utenti admin di poter autoeliminarsi o rimuovere i permessi
di admin al proprio account. Il sistema noterà questi cambiamenti in tempo reale agendo di
conseguenza. L'utente speciale (admin - admin) non compare nella lista degli utenti 
registrati in quanto non lo considero reale, ma solo un account di gestione del sistema.

Le TextViews che contengono i dati di un utente sono provviste di scrolling laterale per 
la visualizzazione di eventuali testi lunghi.

Il salvataggio dei dati avviene in memoria locale con l'uso di file di Shared Preferences. 
La scrittura e lettura da questi file avviene con l'ausilio delle funzioni JSON di Google.
