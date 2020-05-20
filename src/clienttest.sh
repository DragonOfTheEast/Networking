#!/usr/bin/bash
#Author: Chinagorom Mbaraonye
javac tiktak/*/*.java tiktak/app/*/*.java
jar cfe Client.jar tiktak.app.client.Client tiktak/*/*.class tiktak/app/*/*.class
java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 12345 mbaraonye gwyko by TOSI
sleep 500
java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 12345 mbaraonye gwyko TOST
sleep 500
java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 1345 mbaraonye gwyko TOST
sleep 500
java -cp Client.jar tiktak.app.client.Client wind.ecs.baylor.edu 12345 mbaraonye gwyko LTSR
