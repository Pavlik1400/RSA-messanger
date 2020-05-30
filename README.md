# RSA-messanger (RSAssanger)
This is our small projects - android messanger that encrypts messagse with rsa. Authors - Pavlo Hilei, Yevhen Pankevych

## Download app
You can download app from [here](https://github.com/Pavlik1400/RSA-messanger/tree/master/apk_files)

## How to use an app
- Before messaging you have to go to settings and set constants for ciphering. p and q should be prime numbers, p*q > 255255 and e should have no common deviders with (p-1)*(q-1). It is good idea to use p and q such that their lenngth > 200, than it will be impossible to crack message
- Write your name amd name of room, where you and your friend want to chat
- Save changes. Now go to "go to messages", and you will can chat there
- Before sending you should press "encode". Server keeps only last 20 messages
- You can clear all messages from room locally, by pressing "Clear room from data base", or clear messages from all rooms by pressing "Clear whole data base"
- You can delete all messages from on the server by pressing "Delete all messages from server"

Remember, You and only You own your data
