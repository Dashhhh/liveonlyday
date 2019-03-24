package com.ejdash.esbn.ERC20EthereumToken;

public class Config {

    public static String addresSetHNode() {
//        String ethnode = "http://IP_ADDRESS"; // ethereum network
        String ethnode = "https://ropsten.infura.io/v3/a463246e20a046f4acd21ecb9ef7283b"; // ethereum network
        return ethnode;
    }

    public static String addressSmartContract() {
//        return "0x9a95627f2ddb8a3dd61dd108310852133c722187"; // Contract Address
//        return "0xBa78B7246fb259A322674B437e6dad88f2f8C748"; // ejToken Contract Address
//        return "0x3c5a823bb1b411a718cf931e84be67bda8d932c4"; // EJD Contract Address
        return "0xd21de7f695d31436a72bb9faa5db6a48405ef1bb"; // EJD2 Contract Address
    }

    public static String passwordWallet() {
        return "";
    }
}
