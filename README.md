# Java-Microservices-con-Spring-Boot-Spring-Cloud-e-AWS


Creazione di un container contenente Mongo utilizzato per salvare gli utenti in fase di registrazione


	1) Configurare il docker-compose e il DockerFile

	2) Accedere al container appena creato con il comando: docker exec -ti <CONTAINER ID> bash

	3) Digitare il comando "mongo"

	4) Recarsi all'interno del container nel path cd /etc e modificare il file mongodb.conf

	5) moficare  nel file mongodb.conf:

		- la linea "bind_ip" in bind_ip = 0.0.0.0
		- la linea auth = true
		
	6) Riavviare il container con il comando: 

		- docker stop <CONTAINER ID>
		- docker start <CONTAINER ID>

	7) Digitare il comando "mongo"

	
	8) digitare il comando use admin

	9) Creazione admin con il seguente script

		db.createUser({

			user:"admin",
			pwd:"password",
			roles:[
				{role: "userAdminAnyDatabase" , db: "admin"}
			]

		})

	10) Eseguire lo script copiando e incollando il comando nel container

	11) accedere al db mongo con il comando:

		- mongo -u admin --authenticationDatabase admin -p

	12) Creazione utente utilizzato dalla web api 

		- use admin

		Eseguire lo script:

			db.createUser({

			user:"user",
			pwd:"password",
			roles:[
				{role: "readWrite" , db: "ms-users"}
			]

		})

	13) Eseguire lo script sul container mongo per abilitare l'user

	14) Accedere come utente con il seguente comando:

		- mongo -u user --authenticationDatabase admin -p

	15) Utilizzare il database con il comando

		- use ms-users