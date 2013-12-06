package com.tree.combatcalculator;

/**
 * @(#)AtkVarUser.java
 *
 *
 *a parent class for all the classes that use variable lists
 *
 * @author
 * @version 1.00 2012/9/13
 */
import java.util.ArrayList;
import java.util.List;


public class AtkVarUser {

	protected List<AtkVar> variables = new ArrayList<AtkVar>();
	//default name
	protected String name = "default";

    public AtkVarUser() {
    }


	//variable stuff

	//add a variable to the user
    public void addVariables(List<AtkVar> newVariables){
    	variables.addAll(newVariables);
    }

	//remove a variable by using it's name
    public void removeVariable(String variableName){
    	AtkVar.removeVariable(variables, variableName);
    }

	//get the variables
    public List<AtkVar> getVariables(){
    	return variables;
    }

	//set the variables from an existing list
    public void setVariables(ArrayList<AtkVar> newVariables){
    	variables = newVariables;
    }


    //end variable stuff

    public String getName(){
    	return name;
    }

    public void setName(String newName){
    	name = newName;
    }


}