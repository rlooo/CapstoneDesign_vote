package main

import (
	"encoding/json"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type SimpleChaincode struct {
}

type personalInfo struct {
	UserID			string	`json:"userID"`
	AptID			string	`json: "aptID"`
	PositionID		string	`json: "positionID"`
	Name			string	`json: "name"`
	PhoneNum		string	`json: "phoneNum"`
	EmailAddr		string	`json: "emailAddr"`
	DongNum			string	`json: "dongNum"`
	HouseHoldNum	string	`json: "houseHoldNum"`
	TransferDate 	string	`json: "transferDate"`
	State			string	`json: "state"`
	Password		string	`json: "password"`
}

func main(){
	err :=shim.Start(new(SimpleChaincode))
	if err != nil{
		fmt.Printf("Error starting Simple chaincode %s", err)
	}
}

func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("invoke is running " + function)

	// Handle different functions
	if function == "savePersonalInfo" { //create a new marble
		return t.savePersonalInfo(stub, args)
	} else if function == "getPersonalInfo" { //change owner of specific marble
		return t.getPersonalInfo(stub, args)
	}
	fmt.Println("invoke did not fine func: " + function) //error
	return shim.Error("Received unknown function invocation")
}

func (t *SimpleChaincode) savePersonalInfo(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var err error
	transMap, err := stub.GetTransient()
	if err != nil {
		return shim.Error(error.Error())
	}

	var pi personalInfo
	err = json.Unmarshal(transMap["personalInfo"], &pi)
	if err != nil{
		return shim.Error("Failed to decode JSON of: " + string(transMap["personalInfo"]))
	}

	fmt.Println("Start picc: savePersonalInfo func")

	if len(pi.UserID)<=0{
		return shim.Error("1st argument must be a non-empty string")
	}
	if len(pi.AptID)<=0{
		return shim.Error("2st argument must be a non-empty string")
	}
	if len(pi.PositionID)<=0{
		return shim.Error("3st argument must be a non-empty string")
	}
	if len(pi.Name)<=0{
		return shim.Error("4st argument must be a non-empty string")
	}
	if len(pi.PhoneNum)<=0{
		return shim.Error("5st argument must be a non-empty string")
	}
	if len(pi.EmailAddr)<=0{
		return shim.Error("6st argument must be a non-empty string")
	}
	if len(pi.DongNum)<=0{
		return shim.Error("7st argument must be a non-empty string")
	}
	if len(pi.HouseHoldNum)<=0{
		return shim.Error("8st argument must be a non-empty string")
	}
	if len(pi.TransferDate)<=0{
		return shim.Error("9st argument must be a non-empty string")
	}
	if len(pi.State)<=0{
		return shim.Error("10st argument must be a non-empty string")
	}
	if len(pi.Password)<=0{
		return shim.Error("11st argument must be a non-empty string")
	}

	infoBytes, err := json.Marshal(pi)
	if err != nil {
		return shim.Error(err.Error())
	}

	err = stub.PutPrivateData("personalInfo",pi.UserID, infoBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	fmt.Printf("KEY: %s \n", pi.UserID)
	bytes, err := json.Marshal(pi.Id)
	if err = nil {
		return shim.Error(err.Error())
	}
	return shim.Success(bytes)
}
func (t *SimpleChaincode) getPersonalInfo(stub shim.ChaincodeStubINterface, args []string) pb.Response {
	var err error
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	if len(args[0])<=0 {
		return shim.Error("1st argument must be a non-empty string")
	}

	id := args[0]
	infoBytes, err := stub.GetPrivateData("persinalInfo", id)

	if err != nil{
		return shim.Error(err.Error())
	} else if infoBytes == nil{
			jsonResp := "{\"ERROR\":\"personal information does not exist\"}"
			return shim.Error(jsonResp)
	}
	return shim.Success(infoBytes)
}