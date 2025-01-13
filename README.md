# Service Oriented Architectures Project
Subject @ PUT, Winter Semester 2025.
Subject taught by Juliusz Jezierski PhD

Coded by Karol Wesołowski BSE, PUT
Thanks for help with SOAP goes to @Pojemnik

*Is it free? It is. Is it good? It is free.*

## To start the app:
1. Run build.sh from soap-server directory. Make sure, that you stop docker container, that may start right after.
2. Run build.sh from main directory. It will start all 7 of the images, creating full docker container (zookeeper + kafka, soap and 4 microservices)
3. Enjoy or cry

Gateway serves as front end server as well.

## Logic
There is none.
Only thing that is checked is eType with three options:
- Harness will produce an error
- Tent will return 100
- Default will return 50

Don't expect anything else. My eyes hurts just from looking at this. Do better, start eariler, make good use of chat.
5.0 ggwp
