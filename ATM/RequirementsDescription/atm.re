As a customer, I want to withdraw cash
{
Basic Flow {
	(User) 1. the customer shall put card into ATM.
	(System) 2. When a card is put in, the ATM shall indentify whether it belongs to this bank and show customer to enter password.
	(User) 3. the customer shall enter password.
	(System) 4. When password is entered, the ATM shall indentify whether it is right.
	(System) 5. If password entered is not valid more than three times in a day, the ATM shall return error information and refuse service.
	(System) 6. When password is valid, the ATM shall show amount of money left in card.
	(User) 7. the user shall choose to withdraw money and enter amount of money to withdraw.
	(System) 8. When amount of money to withdraw is entered, the ATM shall see whether there is enough money in card.
	(System) 9. If number entered is more than money in card, the ATM shall return error information.
	(System) 10. When money in card is enough, the ATM shall show get money out.
	(User) 11. the customer shall get money and push get card button.
	(System) 12. When get card button is pushed, the ATM shall show get card out.
	(User) 13. the customer shall get card.
	(System) 14. When end of deal, the ATM shall show log all behaviors and upload to cloud.
	}
	
Alternative Flow {
	A. At any time, ATM should log last user behavior and upload to remote severs :
		1. after system successfully response to user request atm should upload to remote sever.
	B. At any time, ATM fails :
		1. Remote severs should try to restart ATM automatically and set to prior state.
		2. System reconstructs prior state.
	}
}

As a customer, I want to deposit funds
{
Basic Flow {
	(User) 1. the customer shall put card into ATM.
	(System) 2. When a card is put in, the ATM shall indentify whether it belongs to this bank and show customer to enter password.
	(User) 3. the customer shall enter password.
	(System) 4. When password is entered, the ATM shall indentify whether it is right.
	(System) 5. If password is not valid, the ATM shall return error information.
	(System) 6. When password is valid, the ATM shall show amount of money left in card.
	(User) 7. the user shall choose to deposit funds and put cash in.
	(System) 8. When money is put in, the ATM shall count amount and add number to bank card.
	(User) 9. the customer shall push get card button.
	(System) 10. When get card button is pushed, the ATM shall show get card out.
	(User) 11. the customer shall get card.
	(System) 12. When end of deal, the ATM shall show log all behaviors and load to cloud.
	}
}

As a customer, I want to check balance
{
Basic Flow {
	(User) 1. the customer shall put card into ATM.
	(System) 2. When a card is put in, the ATM shall indentify whether it belongs to this bank and show customer to enter password.
	(User) 3. the customer shall enter password.
	(System) 4. When password is entered, the ATM shall indentify whether it is right.
	(System) 5. If password entered is not valid more than three times in a day, the ATM shall return error information and refuse service.
	(System) 6. When password is valid, the ATM shall show amount of money left in card.
	(User) 7. the user shall choose to withdraw moner and enter amount of money to withdraw.
	(User) 8. the customer shall gpush get card button.
	(System) 9. When get card button is pushed, the ATM shall show get card out.
	(User) 10. the customer shall get card.
	(System) 11. When end of deal, the ATM shall show log all behaviors and upload to cloud.
	}
}

As a customer, I want to transfer money
{
Basic Flow {
	(User) 1. the customer shall put card into ATM.
	(System) 2. When a card is put in, the ATM shall indentify whether it belongs to this bank and show customer to enter password.
	(User) 3. the customer shall enter password.
	(System) 4. When password is entered, the ATM shall indentify whether it is right.
	(System) 5. If password entered is not valid more than three times in a day, the ATM shall return error information and refuse service.
	(System) 6. When password is valid, the ATM shall show amount of money left in card.
	(User) 7. the user shall choose to transfer money and enter amount of money as well as another card number.
	(System) 8. When amount of money as well as another card number are entered, the ATM shall see whether there is enough money in card and confirm.
	(System) 9. If number entered is more than money in card, the ATM shall return error information.
	(System) 10. When money in card is enough and get user confirmed, the ATM shall transfer and show it is done.
	(User) 11. the customer shall push get card button.
	(System) 12. When get card button is pushed, the ATM shall show get card out.
	(User) 13. the customer shall get card.
	(System) 14. When end of deal, the ATM shall show log all behaviors and upload to cloud.
	}
}

As a customer, I want to check bank account statement
{
Basic Flow {
	(User) 1. the customer shall put card into ATM.
	(System) 2. When a card is put in, the ATM shall indentify whether it belongs to this bank and show customer to enter password.
	(User) 3. the customer shall enter password.
	(System) 4. When password is entered, the ATM shall indentify whether it is right.
	(System) 5. If password entered is not valid more than three times in a day, the ATM shall return error information and refuse service.
	(System) 6. When password is valid, the ATM shall show amount of money left in card.
	(User) 7. the user shall choose to check bank account statement.
	(System) 8. the ATM shall show bank account statement in a month.
	(User) 9. the customer shall get money and push get card button.
	(System) 10. When get card button is pushed, the ATM shall show get card out.
	(User) 11. the customer shall get card.
	(System) 12. When end of deal, the ATM shall show log all behaviors and upload to cloud.
	}
}

As a bank clerk, I want to manage user
{
Basic Flow {
	(User) 1. the bank clerk shall enter authorization code.
	(System) 2. When a right authorization code is entered, the ATM shall change to admin mode.
	(User) 3. the bank clerk shall choose to manage user and check information.
	(User) 4. the bank clerk shall choose to exit.
	(System) 5. When getting exit code, the ATM shall change to common user mode.
	(System) 5. When end of use, the ATM shall log all behaviors and upload to cloud.
	}
}

As a bank clerk, I want to manage bank card
{
Basic Flow {
	(User) 1. the bank clerk shall enter authorization code.
	(System) 2. When a right authorization code is entered, the ATM shall change to admin mode.
	(User) 3. the bank clerk shall choose to manage bank card and check information.
	(User) 4. the bank clerk shall choose to exit.
	(System) 5. When getting exit code, the ATM shall change to common user mode.
	(System) 5. When end of use, the ATM shall log all behaviors and upload to cloud.
	}
}
