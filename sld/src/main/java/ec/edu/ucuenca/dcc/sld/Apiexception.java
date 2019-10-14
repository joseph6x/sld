/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

/**
 *
 * @author joe
 */
public class Apiexception extends Exception {

  public Apiexception(String this_is_My_error_Message) {
  }

  public String toString(){ 
	return ("Invalid Api key") ;
   }
}