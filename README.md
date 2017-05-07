Mobicheck Demo
=======

##Deploying the demo##
To deploy to Bluemix simply use the button below then follow the instructions. This will generate the NodeJS server and the Blockchain service for you.

[![Deploy to Bluemix](https://bluemix.net/deploy/button.png)](https://bluemix.net/deploy?repository=https://github.com/jm8310/mobicheck-demo.git)

To deploy the demo locally follow the instructions [here](Documentation/Installation%20Guide.md#deploying-locally)

##Application overview##
This application is designed to demonstrate how assets can be modeled on the Blockchain using a car leasing scenario. In the scenario vehicles are modeled using Blockchain technology with the following attributes:

| Attribute       | Type                                                                                                  |
| --------------- | ----------------------------------------------------------------------------------------------------- |
| VIN             | 15 digit int                                                                                         |
| Make            | String                                                                                                |
| Model           | String                                                                                                |
| Colour          | String                                                                                                |
| Reg             | String                                                                                                |
| Owner           | Identity of participant                                                                               |
| Scrapped        | Boolean                                                                                               |
| Status          | Int between 0 and 4                                                                                   |
| Barcode	  | Unique string

The application is designed to allow participants to interact with the vehicle assets creating, updating and transferring them as their permissions allow. The participants included in the application are as follows:

| Participant    | Permissions                                                          |
| -------------- | ---------------------------------------------------------------------|
| Regulator      | Create, Read (All Products), Transfer                                |
| Manufacturer   | Read (Own products), Update (VIN, Make, Model, Colour, Reg), Transfer|
| Dealership     | Read (Own products), Update (Colour, Reg), Transfer                  |
| Lease Company  | Read (Own products), Update (Colour, Reg), Transfer                  |
| Leasee         | Read (Own products), Update (Colour, Reg), Transfer                  |
| Scrap Merchant | Read (Own products), Scrap                                           |
| Customer	 | Read (All products)

The demonstration allows a view of the ledger that stores all the interactions that the above participants have has with their assets. The ledger view shows the regulator every transaction that has occurred showing who tried to to what at what time and to which vehicle. The ledger view also allows the user to see transactions that they were involved with as well as showing the interactions with the assets they own before they owned them e.g. they can see when it was created.


##Application scenario##
The scenario goes through the lifecycle of a mobile which has the following stages:

![Application scenario overview](/Images/Scenario_Overview.png)

####Stages:####

 1. Product  is created as a template by the regulator.
 2. Product template is transferred to the manufacturer.
 3. Manufacturer updates the Product template to define it as a Product giving it a make, model, ESN, Barcode etc.
 4. Manufacturer transfers the Product  to dealership to be sold.
 5. Dealership transfers the Product to a Customer.
 6. Manufacturer transfers the Product to Customer.
 7. Customer returns the Product in case of Buyback/Return/Exchange.
 8. Scrap merchant scraps the Product.

##Component model##
The demo is built using a 3 tier architecture forked from exising car lease demo developed by IBM blockchain. The user interacts with the demo using a [web front end](Documentation/Client%20Side.md) that is provided by the NodeJS server in the middle tier. This web front end uses JavaScript to make HTTP requests to the NodeJS server which has an API ([defined here](Documentation/API Methods.md)) which in turn makes calls via HTTP to the HyperLedger fabric to get details about the blockchain and also interact with the [chaincode](Chaincode/src/vehicle_code/vehicles.go). Information on the chaincode interface can be found [here](Documentation/Chaincode Interface.md). All the file names are retained as per car lease demo and the code within the file is modified to Mobicheck needs.

![Technical Component Model](/Images/Technical_Component_Model.png)
