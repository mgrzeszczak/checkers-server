# checkers-server

Checkers game server which allows for multiple games to be played simultaneously. It uses Spring framework for Dependency Injection. Clients are handled on separate threads, the messages are exchanged over sockets in json format. Game logic and data received from clients is verified on the server's side in order to prevent cheating.

Serwer do gry w warcaby obsługujący wiele gier równocześnie. Korzysta z frameworka Spring (Dependency Injection). Klienci obsługiwani są na oddzielnych wątkach, wymiana wiadomości w formacie json. Logika gry i ruchy graczy sprawdzane również po stronie serwera by wykluczyć możliwość oszukiwania przez któregoś z graczy.
