# checkers-server

Serwer do gry w warcaby obsługujący wiele gier równocześnie. Korzysta z frameworka Spring (Dependency Injection). Klienci obsługiwani są na oddzielnych wątkach, wymiana wiadomości w formacie json. Logika gry i ruchy graczy sprawdzane również po stronie serwera by wykluczyć możliwość oszukiwania przez któregoś z graczy.
