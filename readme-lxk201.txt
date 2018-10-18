2/23/2018: upload code related to registration webpage and data access
3/2/2018: uploaded UpdateInfo.java and SecurityTools.java. Allowed modification of user information, and implemented RSA for message privacy.

3/7/2018: implemented DSA.
3/8/2018: tested and debugged UpdateInfo.java and optimized return statement to make it more informative regarding an update's failure or success.

3/23/2018: allowed server to access database to verify login, record win/loss, check whether a card is upgraded. Artistic update on cards.

3/30/2018: debugged DSA signature system. Implemented entropy into RSA to make RSA a non-deterministic cryptosystem. For that, ElGamal cryptosystem is implemented to introduce entropy.

4/6/2018: improved entropy system by selecting random position to insert noise. 
	Utilized concept from block cipher to hide delimiter in cyphertext.
	Also introduced entropy into block cipher to attempt to prevent statistical cryptanalysis that might reveal encrypted delimiter.

4/13/2018: implemented the level system. Allowed player to unlock new cards if a card reached certain level and the player has enough currency

4/20/2018: changed server from UDP to TCP. Encryption encountered unexpected issue. Will attempt to resolve over the weekend.
	Character loss between sockets occurred where the character is recognizeable locally on Windows and Linux. 
	Possible solution 1 is to map 2 digits into 3 digits(because of the need to hide comma, 2 digits to 2 digits will not have enough space)
	However, this solution will increase workload.
	For possible solution 2: will attempt to find character that will not be lost during transition between sockets.
	lf that failed, will fall back to the inferior solution (solution 1).

4/23/2018: resolve character loss problem. The socket will now transmit character in UTF-8 encoding.
	Also solved a mysterious problem by calling a built-in java function.
	Input stream will sometimes fail and the reason behind the failure was unidentifiable.