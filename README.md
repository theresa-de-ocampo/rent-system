# Andrion Food Services' Renting System
## Overview
Andrion Food Services is a food catering that expanded to become a rental business as well. The items that could be rented ranges from the trivial drinking glasses, to chafers, and up to the hefty food transportation devices. Hence, this subsystem was developed to support the new business process.

## Main Asset
The entire system did not use any GUI builder, utilizing Object Oriented Programming (OOP) at its finest.

# Features
1. Login & Account Management
	1. Password update.
	2. Password recovery through Gmail.
2. Home *(Rental)*
	1. Customer Details
	2. Rent Details
		1. Prevents a customer from renting an item if it is not available during the period that it will be rented *(similar to reservation)*.
3. Inventory
	1. Add new equipment per category *(Buffet Line, Dining Furniture, Flatware, etc)*.
	2. Increase an existing items' quantity.
	3. Decrease an existing items' quantity.
		1. Prompts for an **Equipment Incident Report** that is automatically mailed to the owner.
4. Pick-Up Dates
	1. Prompts for an **Equipment Return Form**.
	2. Prompts for an **Equipment Incident Report** if the customer was unable to return every rented item.

## Requirements
1. Microsoft Access 2016 *(or higher)*.
2. Java Development Kit (JDK) 13 *(or higher)*.

## Installation
1. Clone the repository.
	```
		git clone https://github.com/theresa-de-ocampo/rent-system.git
	```
2. Add the jar files in ```External Files/Jar Files/```.
3. Run the package's main class: ```src/RentSystem/Login.java```.
4. Provide the following access details.
	```
		Username: AndrionServices
		Password: DemoPassword
	```