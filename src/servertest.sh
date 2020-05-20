#!/usr/bin/bash
#Author: Chinagorom Mbaraonye
javac tiktak/*/*.java tiktak/app/*/*.java
jar cfe Client.jar tiktak.app.server.Server tiktak/*/*.class tiktak/app/*/*.class
java -cp Client.jar tiktak.app.server.Server 1257 5 "C:\Users\Chinagorom Mbaraonye\IdeaProjects\Data Communications\src\password.txt"
sleep 500
java -cp Client.jar tiktak.app.server.Server 1257 -1 "C:\Users\Chinagorom Mbaraonye\IdeaProjects\Data Communications\src\password.txt"
sleep 500
java -cp Client.jar tiktak.app.server.Server 125 5 "C:\Users\Chinagorom Mbaraonye\IdeaProjects\Data Communications\src\password.txt"
sleep 500