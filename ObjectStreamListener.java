/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychat;

/**
 *
 * @author miyagi
 */
interface ObjectStreamListener {

    public void objectReceived(int number, Object object, Exception exception);
    
}
