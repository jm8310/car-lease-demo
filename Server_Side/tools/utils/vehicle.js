'use strict';

const Util = require('./util.js');
const hfc = require('hfc');

class Vehicle {

    constructor(usersToSecurityContext) {
        this.usersToSecurityContext = usersToSecurityContext;
        this.chain = hfc.getChain('myChain'); //TODO: Make this a config param?
    }

    create(userId) {
        let securityContext = this.usersToSecurityContext[userId];
        let barcode = Vehicle.newV5cID();

        return this.doesV5cIDExist(userId, barcode)
        .then(function() {
            return Util.invokeChaincode(securityContext, 'create_vehicle', [ barcode ])
            .then(function() {
                return barcode;
            });
        });
    }

    transfer(userId, buyer, functionName, barcode) {
        return this.updateAttribute(userId, functionName , buyer, barcode);
    }

    updateAttribute(userId, functionName, value, barcode) {
        let securityContext = this.usersToSecurityContext[userId];
        return Util.invokeChaincode(securityContext, functionName, [ value, barcode ]);
    }

    doesV5cIDExist(userId, barcode) {
        let securityContext = this.usersToSecurityContext[userId];
        return Util.queryChaincode(securityContext, 'check_unique_v5c', [ barcode ]);
    }

    static newV5cID() {
        let numbers = '1234567890';
        let characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        let barcode = '';
        for(let i = 0; i < 7; i++)
            {
            barcode += numbers.charAt(Math.floor(Math.random() * numbers.length));
        }
        barcode = characters.charAt(Math.floor(Math.random() * characters.length)) + barcode;
        barcode = characters.charAt(Math.floor(Math.random() * characters.length)) + barcode;
        return barcode;
    }
}

module.exports = Vehicle;
